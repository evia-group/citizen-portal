---
name: auto-ticket
description: Autonomous wrapper around /ticket. Polls the current sprint each tick for a ticket carrying the `ai-driveable` label, autonomously drafts a canonical plan if one is missing, and drives /ticket on it. One ticket at a time, always leaves a working state. Each ticket's implementation runs in an isolated git worktree (.claude/worktrees/<TICKET-ID>/), keeping the main working tree clean and allowing human QA on a completed ticket while the next one is processed. Designed to be invoked as `/loop 5m /auto-ticket`. Stateless across ticks — all coordination state lives in Jira labels.
---

# `/auto-ticket` — autonomous sprint queue driver

Invocation: `/loop 5m /auto-ticket`. The harness fires this skill on a fixed cadence; each tick is a fresh Claude turn with no in-process memory of prior ticks. **All cross-tick state lives in Jira labels** so the skill is fully resumable after a crash, restart, or different machine.

The skill picks one ticket at a time, drives `/ticket` on it to completion or halt, and idles when there is nothing eligible. It will never start a second ticket while one is in flight, and it freezes itself rather than infinite-retrying a permanently broken ticket.

It does:
- Autonomously draft a canonical plan (best-effort, via the `Plan` subagent) when a picked-up `ai-driveable` ticket has no plan comment yet, and post it via the Steward before invoking `/ticket`. This is non-interactive and is **not** a substitute for `/grill-me` on hard problems — it is a "good enough plan to start work" pass.

It does **not**:
- Run an interactive planning dialogue (`/grill-me` is the human-driven path for that; the loop's draft is one-shot)
- Push to remote, open PRs, or merge to the trunk (delegated to `/ticket`, which itself does not push)
- Move tickets past `Test` (human gate is preserved by `/ticket`)
- Pick a ticket the human hasn't opted in to (requires `ai-driveable` label)

---

## 0. Before doing anything

1. **Load the `jira-workflow` skill** (`.claude/skills/jira-workflow/SKILL.md`) for status and label semantics.
2. **Load the `jira-mcp` skill** (`.claude/skills/jira-mcp/SKILL.md`) only if you need to reason about MCP call patterns directly. **Default: delegate every Jira read/write to the `Jira Workflow Steward` subagent**, per CLAUDE.md.
3. Read `autoTicket.integrationBaseRef` from `.claude/settings.local.json` (or `.claude/settings.json`). This is the git ref new ticket integration branches are cut from when no integration branch exists yet (e.g. `ai-workshop/day1/0-init`). If absent, fall back to the repo's default remote branch (`git symbolic-ref refs/remotes/origin/HEAD --short`).

4. Determine the project key by trying the following sources in order, stopping at the first that yields a single dominant `<KEY>`:
   1. **Explicit override** — `autoTicket.projectKey` in `.claude/settings.local.json` (or `.claude/settings.json`). If set, use it verbatim and skip the rest.
   2. **Current branch prefix** — e.g. branch `BP-42/...` → `project = BP`. Match against `^<KEY>-<num>(/.*)?$`.
   3. **Integration branches** — `git for-each-ref refs/heads/ --format='%(refname:short)' | grep -oE '^[A-Z]+-[0-9]+' | grep -oE '^[A-Z]+' | sort | uniq -c | sort -rn`. Use the most common prefix; if there's a tie at the top, treat as ambiguous.
   4. **Recent commit messages** — `git log -200 --format='%s' | grep -oE '^[A-Z]+-[0-9]+:' | grep -oE '^[A-Z]+' | sort | uniq -c | sort -rn`. Use the most common prefix (commit messages follow the `<KEY>-<num>: ...` convention enforced by CLAUDE.md, so this is reliable even on a non-ticketed branch like `ai-workshop/...`).

   If all four sources are empty or ambiguous, exit the tick with `auto-ticket: cannot determine project key, idle. Set autoTicket.projectKey in .claude/settings.local.json to override.`

---

## 1. Label vocabulary

This skill owns five labels on top of the existing AI-workflow labels (`ai-planned`, `ai-reviewed`, `ai-tested`):

| Label | Meaning | Who sets/clears |
|---|---|---|
| `ai-driveable` | Human opt-in: "the loop may drive this ticket" | Human sets, human clears (loop never touches) |
| `auto-loop-active` | The loop is currently driving this ticket; do not pick up another | Loop sets at claim, clears at terminal/freeze |
| `auto-loop-strike-1` | Most recent tick made no progress (state unchanged before/after) | Loop sets when strike count = 1 |
| `auto-loop-strike-2` | Two consecutive ticks made no progress | Loop sets when strike count = 2 |
| `auto-loop-frozen` | Three consecutive no-progress ticks → loop has given up; **freezes the entire loop** until a human removes the label | Loop sets at strike 3 |

A ticket should never carry both `auto-loop-strike-1` and `auto-loop-strike-2` simultaneously — when the count advances, remove the older marker before adding the newer one. When state advances, clear all strike labels.

---

## 2. Per-tick algorithm

Apply these steps in order. Most steps short-circuit the tick.

### Step 0 — Worktree escape hatch

Run:

```bash
git worktree list --porcelain | grep "^worktree " | grep -F "$(pwd)"
```

If the current session CWD is a registered worktree (the grep returns a match), call `ExitWorktree(action: "keep")` to return to the main repo before doing anything else. This is a silent self-repair for ticks that crashed after `EnterWorktree` but before `ExitWorktree`.

### Step 0.5 — Prune Done/Closed worktrees

Run:

```bash
git worktree list --porcelain | grep "^worktree " | grep -F ".claude/worktrees/"
```

For each path returned, extract the ticket ID (last path segment). Ask the Steward to fetch the current status of each ticket ID. For every ticket whose status is `Done`, `Closed`, or `Resolved`:

1. Check if the worktree is clean:
   ```bash
   git -C <path> status --porcelain
   git -C <path> log origin/HEAD..HEAD --oneline 2>/dev/null
   ```
2. If both are empty → `git worktree remove <path>` (then `git branch -D <TICKET-ID>` if the integration branch is fully merged).
3. If either has output → skip and emit one stdout warning line:
   ```
   auto-ticket: skipping prune of <TICKET-ID> worktree — has uncommitted or unpushed changes.
   ```

Continue to Step 1 regardless.

### Step 1 — Frozen-loop check (whole-loop kill switch)

Ask the Steward to run JQL:

```
project = <KEY> AND sprint in openSprints() AND labels = "auto-loop-frozen"
```

If any ticket is returned → **exit tick** with stdout:

```
auto-ticket: frozen by <TICKET-ID>; remove `auto-loop-frozen` label to resume.
```

Do not pick up any ticket while any ticket in the sprint is frozen. This is the human-attention gate.

### Step 2 — Resume in-flight check

Ask the Steward to run JQL:

```
project = <KEY> AND labels = "auto-loop-active"
```

(No sprint filter — if the loop label leaked into a different sprint, we still resume it before picking anything new.)

- If exactly one ticket is returned → **this is our ticket**; record its ID. Then ensure the worktree exists and enter it:
  ```bash
  WORKTREE=".claude/worktrees/<TICKET-ID>"
  # Does the worktree already exist?
  git worktree list --porcelain | grep -F "$WORKTREE"
  ```
  - If it exists → `EnterWorktree(path: "<WORKTREE>")`.
  - If it does not exist (different machine or manual deletion) → recreate it:
    ```bash
    # Use the integration branch if it exists, otherwise cut a new one
    git show-ref --verify --quiet "refs/heads/<TICKET-ID>" \
      && git worktree add "$WORKTREE" "<TICKET-ID>" \
      || git worktree add "$WORKTREE" -b "<TICKET-ID>" "<integrationBaseRef>"
    ```
    Then `EnterWorktree(path: "<WORKTREE>")`.
  Jump to Step 5.
- If more than one is returned → the CAS in Step 4 was violated by a prior tick; **exit tick** and freeze the loop on the *first* of those tickets:
  ```
  auto-ticket: invariant violated — multiple tickets carry `auto-loop-active`: <list>. Freezing on <first>.
  ```
  Have the Steward add `auto-loop-frozen` to that ticket and remove `auto-loop-active` from all of them. The next tick will hit Step 1 and idle.
- If zero tickets are returned → continue to Step 3.

### Step 3 — Pickup (find the next ai-driveable ticket)

Ask the Steward to run JQL and return the **top result** by Rank:

```
project = <KEY>
  AND sprint in openSprints()
  AND status in (Plan, Planned, "Ready for Development")
  AND labels = "ai-driveable"
  AND labels not in ("auto-loop-frozen", "auto-loop-active")
ORDER BY Rank ASC
```

If no candidate is returned → **exit tick** with stdout:

```
auto-ticket: no eligible tickets, idle.
```

Also have the Steward fetch the top candidate's comments and tell you whether any comment body starts with `Implementation Plan (canonical`. Record both `<TICKET-ID>` and the boolean `has_plan` for use in Step 4.5. **Do not skip the candidate if it lacks a plan** — Step 4.5 will draft one. Continue with this ticket.

### Step 4 — CAS-style claim

Defense against parallel `/loop` ticks racing on the same candidate.

1. Ask the Steward to add `auto-loop-active` to `<TICKET-ID>` (use the label-merge pattern in `jira-mcp`; never overwrite).
2. Wait for the write to acknowledge.
3. Ask the Steward to re-run the JQL `project = <KEY> AND labels = "auto-loop-active"` and return the full list.
4. If the list contains a ticket *other than* `<TICKET-ID>` → **we lost the race**. Have the Steward remove `auto-loop-active` from `<TICKET-ID>`. **Exit tick** with stdout:
   ```
   auto-ticket: lost claim race on <TICKET-ID> to <other>; standing down.
   ```
5. If the list is exactly `[<TICKET-ID>]` → claim succeeded. Create and enter the worktree:
   ```bash
   WORKTREE=".claude/worktrees/<TICKET-ID>"
   # Use existing integration branch if already present (e.g. partial prior run)
   git show-ref --verify --quiet "refs/heads/<TICKET-ID>" \
     && git worktree add "$WORKTREE" "<TICKET-ID>" \
     || git worktree add "$WORKTREE" -b "<TICKET-ID>" "<integrationBaseRef>"
   ```
   Then `EnterWorktree(path: "<WORKTREE>")`. Continue.

### Step 4.5 — Autonomous plan draft (only if `has_plan == false`)

Skip this step entirely if the candidate already had a canonical plan comment in Step 3.

Otherwise, the loop must produce a plan before invoking `/ticket`, because `/ticket` halts at row 1 of its route table when no canonical plan exists. The draft is best-effort and one-shot — not a stand-in for `/grill-me` on hard problems.

1. Ask the Steward to fetch the full ticket: summary, description, issue type, linked issues, and any prior comments. Pass the raw text back, no summarization.
2. Spawn the **`Plan`** subagent (`subagent_type: "Plan"`) with this brief:
   - Goal: produce a canonical implementation plan suitable for posting to Jira as the `Implementation Plan (canonical)` comment for `<TICKET-ID>`.
   - Inputs: ticket summary, description (verbatim), issue type, linked issues. Plus the project layout from `CLAUDE.md` (the Plan agent can read it directly).
   - Output requirements:
     - Markdown body, starting with the literal first line `Implementation Plan (canonical)`.
     - Sections: **Problem**, **Approach**, **Files to change** (with paths), **Acceptance criteria**, **Out of scope**.
     - Concrete and small — this is the loop's first cut, not an architecture review. If the ticket is genuinely ambiguous, the Plan agent should still emit the best-defensible interpretation and call out assumptions in the **Problem** section rather than refusing.
   - Constraints: read-only investigation of the repo; no edits; no Jira writes (the Steward posts).
3. If the Plan agent returns a plan body:
   - Per CLAUDE.md "Plan Persistence", hand the body to the Steward to (a) write `<plansDirectory>/<TICKET-ID>-<slug>.md` and (b) post the canonical plan comment on Jira. The Steward owns both writes.
   - Treat plan draft as a real progress event — but it does NOT count toward the strike comparison in Step 7 by itself; we still rely on `/ticket` advancing state in this same tick. (If `/ticket` halts immediately after the plan was posted, that is one strike — fine.)
4. If the Plan agent fails to converge or returns an empty body:
   - Have the Steward post a comment on `<TICKET-ID>` titled `Auto-loop plan draft failed` with a one-line reason.
   - Add `auto-loop-frozen` to `<TICKET-ID>`, remove `auto-loop-active`. **Exit tick** with stdout:
     ```
     auto-ticket: <TICKET-ID> plan draft failed; frozen for human follow-up.
     ```
   - Do not retry; the next tick will hit Step 1 and idle until the human investigates and removes `auto-loop-frozen`.

### Step 5 — Snapshot pre-state

Ask the Steward to read `<TICKET-ID>` and record:

```
pre = {
  status,
  latest verdict-marker comment (the most recent comment whose first line matches one of:
    "Implementer: Ready for Review"
    "Code Reviewer Verdict: <APPROVED|BLOCKERS>"
    "Reality Checker Verdict: <READY|NEEDS WORK>"
    or a "Halt: ..." line),
  latest sub-branch SHA (locally: `git for-each-ref --sort=-committerdate --format='%(objectname) %(refname:short)' refs/heads/<TICKET-ID>/* | head -1`),
}
```

Capture `pre` in working memory for the duration of this tick.

### Step 6 — Invoke `/ticket`

Run the `/ticket` skill on `<TICKET-ID>` (use the Skill tool with `skill: ticket`, `args: <TICKET-ID>`). Let it run to its natural exit — that will be either a `Halt: ...` line or a `/ticket <TICKET-ID>: complete...` line.

**Do not interrupt `/ticket`. Do not pass it any extra flags.** It is the only sanctioned driver of per-ticket work; this skill is a thin queue wrapper around it.

### Step 7 — Snapshot post-state and decide

Re-read `<TICKET-ID>` via the Steward and re-run the local SHA query. Build `post = {status, latest verdict-marker, latest sub-branch SHA}`.

Compare `post` to `pre`:

| Comparison | Verdict |
|---|---|
| `post.status == "Test"` (or `Done`/`Closed`/`Resolved`) | **Terminal success.** `/ticket` reached the human gate. Have the Steward remove `auto-loop-active` and any `auto-loop-strike-N` from `<TICKET-ID>`. Call `ExitWorktree(action: "keep")`. End the tick with `auto-ticket: <TICKET-ID> drained → status=<X>, worktree=.claude/worktrees/<TICKET-ID>`. |
| `post.status != pre.status` OR `post.latest verdict-marker != pre.latest verdict-marker` OR `post.latest sub-branch SHA != pre.latest sub-branch SHA` | **Progress.** Reset strikes: have the Steward remove any `auto-loop-strike-N` labels from `<TICKET-ID>`. Keep `auto-loop-active`. Call `ExitWorktree(action: "keep")`. Next tick will resume via Step 2. |
| All three equal | **No progress (strike).** See Step 8. |

### Step 8 — Strike accounting (only on no-progress)

Determine the current strike level by which `auto-loop-strike-N` label is already on the ticket:

| Existing label on `<TICKET-ID>` | Action |
|---|---|
| None | Add `auto-loop-strike-1`. Keep `auto-loop-active`. Call `ExitWorktree(action: "keep")`. Next tick will resume. |
| `auto-loop-strike-1` | Remove `auto-loop-strike-1`, add `auto-loop-strike-2`. Keep `auto-loop-active`. Call `ExitWorktree(action: "keep")`. |
| `auto-loop-strike-2` | **Freeze.** Remove `auto-loop-strike-2`, add `auto-loop-frozen`, remove `auto-loop-active`. Have the Steward post a comment titled `Auto-loop frozen on <TICKET-ID>` with body containing the last `Halt: ...` line from `/ticket`'s most recent stdout (or, if absent, the latest verdict-marker comment) so the human knows what to fix. Call `ExitWorktree(action: "keep")`. End the tick. |

After updating labels, end the tick with stdout reflecting the new strike level, e.g.:

```
auto-ticket: <TICKET-ID> no progress; strike=2/3.
```

or

```
auto-ticket: <TICKET-ID> frozen at strike=3. Last halt: <reason>. Loop will idle until `auto-loop-frozen` is removed.
```

---

## 3. Output discipline

One stdout line per tick. The user is reading these every 5 minutes — keep them scannable:

```
auto-ticket: frozen by BP-42; remove `auto-loop-frozen` label to resume.
auto-ticket: no eligible tickets, idle.
auto-ticket: lost claim race on BP-58 to BP-57; standing down.
auto-ticket: BP-58 progressed: phase=implementation(fresh) → branch=BP-58/impl-1.
auto-ticket: BP-58 no progress; strike=1/3.
auto-ticket: BP-58 drained → status=Test, worktree=.claude/worktrees/BP-58.
auto-ticket: BP-58 frozen at strike=3. Last halt: cap exhausted (3 fix-forward branches).
auto-ticket: skipping prune of BP-55 worktree — has uncommitted or unpushed changes.
```

Do not stream `/ticket`'s subagent output — `/ticket` already has its own one-line-per-phase discipline, and Jira holds the rich detail.

---

## 4. Halt-vs-progress detection rationale

The pre/post state comparison is the **only** signal the strike counter trusts. We do not parse `/ticket` stdout for "Halt:", because:

- `/ticket`'s message format may evolve.
- Some halts (e.g. `past gate`) actually represent success — the ticket reached `Test`. State comparison correctly classifies that as progress.
- A successful phase that *doesn't* advance to terminal still leaves a verdict marker comment, a status change, or a new sub-branch SHA — at least one of the three.

A tick that genuinely makes no progress means `/ticket` re-routed and re-halted on the same row of its decision table without doing anything to Jira or git. That is the only case the strike counter should fire on.

---

## 5. What this skill is NOT

- An interactive planning partner. It will draft a one-shot plan via the `Plan` subagent if one is missing, but it does **not** run a `/grill-me` dialogue. For tickets that need real design exploration, write the plan with `/grill-me` first and let the loop run `/ticket` afterward.
- A replacement for `/ticket`. It only invokes `/ticket`; all per-ticket logic lives there.
- A Jira API client. All Jira reads/writes go through the `Jira Workflow Steward` subagent.
- A pusher. Nothing reaches the remote. The human still owns the final `Test → Done` transition and any push/merge.
- A scheduler. The cadence comes from `/loop`; this skill is one tick's worth of work.

---

## 6. Operator runbook

**Start:** `/loop 5m /auto-ticket`.

**Stop:** the human halts the `/loop` schedule.

**Configure integration base ref:** add `"autoTicket": { "integrationBaseRef": "ai-workshop/day1/0-init" }` to `.claude/settings.local.json`. This is the git ref new integration branches are cut from. Defaults to `origin/HEAD` if absent.

**Pause without losing state:** add `auto-loop-frozen` to any ticket in the open sprint. The whole loop will idle on Step 1 until the label is removed.

**Add work:** create a ticket, transition to `Plan` (or `Planned` / `Ready for Development`), add `ai-driveable`. The next tick will pick it up if no other ticket is in flight. A canonical plan is *not* required — if the ticket has none, the loop will draft one (Step 4.5) before driving `/ticket`. For hard or ambiguous tickets, write the plan yourself first (e.g. via `/grill-me`) and post it as the canonical plan comment so the loop skips the draft step.

**Test a completed ticket:** when the loop prints `auto-ticket: <TICKET> drained → status=Test, worktree=.claude/worktrees/<TICKET>`, open `.claude/worktrees/<TICKET>` in your IDE or terminal to QA the branch. The worktree stays alive until the ticket reaches `Done`/`Closed` and a subsequent tick prunes it (only if clean).

**Recover from a frozen ticket:**
1. Read the `Auto-loop frozen on <TICKET>` comment for the last halt reason.
2. Resolve the underlying issue (e.g. delete excess sub-branches, fix gate-red, untangle the integration branch).
3. Remove `auto-loop-frozen` from the ticket. The next tick will re-pick the top of the queue (which may be the same ticket — strike counter starts fresh).

**Lost machine / different checkout:** all coordination state is in Jira labels, so a fresh checkout running `/loop 5m /auto-ticket` resumes correctly. The integration branch and sub-branches under `<TICKET>/*` are recreated by `/ticket` if missing. The worktree under `.claude/worktrees/<TICKET>` is also recreated automatically if absent (Step 2 recovery).

---
name: ticket
description: Drive a Jira ticket from Plan to Test by spawning a fresh subagent per phase (Implementer → Code Reviewer → Reality Checker). Stateless across invocations — Jira status, AI labels, and verdict-marker comments encode all state. Use when the user types `/ticket <TICKET-ID>` or asks to "run the workflow" / "drive ticket X". Halts at the human Test → Done gate.
---

# `/ticket` — Jira Workflow Orchestrator

Invocation: `/ticket <TICKET-ID>` (e.g. `/ticket BP-22`).

This skill is the **only place** in the repository that orchestrates the full implement-review-test pipeline for a single ticket. It spawns one fresh subagent per phase to keep the main thread's context window minimal, and reads Jira between phases so the user can intervene at any point by editing the ticket directly.

It does **not**:
- Plan tickets (skill exits with a hint if no plan exists).
- Open PRs or push to remote (no GitHub integration in this workflow).
- Transition `Test → Done` (human gate).

---

## 0. Before doing anything

1. **Load the `jira-workflow` skill** (`.claude/skills/jira-workflow/SKILL.md`) for status-transition rules and label semantics.
2. **Load the `jira-mcp` skill** (`.claude/skills/jira-mcp/SKILL.md`) only if you need to fall back to direct Jira reads — but prefer to delegate every Jira operation to the **Jira Workflow Steward** subagent.
3. Confirm the user passed a `<TICKET-ID>`. If not, exit with: `Usage: /ticket <TICKET-ID>`.

---

## 1. Read state

Spawn the **Jira Workflow Steward** with:

```
Read ticket <TICKET-ID>: status, labels, description, latest 10 comments (top-down).
Return:
- status
- labels
- description (full body, verbatim)
- subtask IDs + their statuses
- the latest verdict-marker comment, if any (look for line starting with one of:
  "Implementer: Ready for Review", "Code Reviewer Verdict:", "Reality Checker Verdict:")
- whether a plan comment exists (a comment whose body starts with
  "Implementation Plan (canonical")
```

Also locally:

```
git fetch --all --prune
git branch --list "<TICKET-ID>" "<TICKET-ID>/*"
```

Record the integration branch's existence and the sub-branch list.

---

## 1.5 Parse in-ticket directives

Tickets can override default orchestrator behavior by embedding directives in the description. Scan the description (case-insensitive) for lines matching:

```
INSTRUCTION TO LLM: <free-text directive>
```

Also accept the common typo `INSTRACTION TO LLM:`. Multiple directives are allowed; collect all.

For each directive, classify by intent (substring match, case-insensitive):

| Intent | Trigger keywords | Effect |
|---|---|---|
| `plan-takeover` | mentions `plan` AND (`/grill-me` OR `grill-me` OR `architect`) | When row 1 of the route table would halt (no plan comment), instead run the planning flow described in §3.0 before re-routing. |
| `unknown` | anything else | **Halt**: surface the directive verbatim to the user with `Halt: ticket directive recognized but not supported: <directive>. Either implement support in /ticket or remove the directive.` Do NOT silently ignore. |

Record the active directives so phases below can reference them. A directive only fires when its preconditions match (e.g. `plan-takeover` is a no-op if a plan comment already exists).

---

## 2. Route

Apply this decision table top-to-bottom; the first matching row wins.

| # | Condition | Action |
|---|-----------|--------|
| 1 | No plan comment on ticket | If a `plan-takeover` directive is active (see §1.5), **Phase: Planning** — see §3.0. Otherwise **Halt**: `No plan on <TICKET>. Write a plan (e.g. via /grill-me) and run the Steward to post it before re-running /ticket.` |
| 2 | Status ∈ {`Test`, `Done`, `Closed`, `Resolved`} | **Halt**: `<TICKET> is past the automation gate (status=<X>). Nothing to do.` |
| 3 | Status = `In Progress` AND there is a sub-branch matching `<TICKET>/*` AND the latest comment on the ticket is **not** an orchestrator "Implementation starting on …" / "Fix-forward starting on …" marker | **Halt**: `In-flight implementation detected on <branch>. Resolve manually (commit + transition, or delete the sub-branch) and re-run.` (Heuristic: a stale `In Progress` from an interrupted prior run, with no recent orchestrator marker, means a human or external agent is mid-edit — do not stomp on them.) |
| 4 | Status = `In Progress` AND the latest comment IS an orchestrator "Implementation starting" / "Fix-forward starting" marker for a known sub-branch under `<TICKET>/*` | **Resume**: a previous `/ticket` run pre-transitioned the ticket but the Implementer never finished. Re-enter the matching phase (fresh or fix-forward) on the same sub-branch — see §3a / §3b. The pre-phase transition step is a no-op (already `In Progress`) but still post a fresh `Resuming on <branch>` comment. |
| 5 | Status = `Plan` (or `Planned` / `Ready for Development`) | **Phase: Implementation (fresh)**. See §3a. |
| 6 | Status = `In Review` AND latest verdict marker = `Reality Checker Verdict: NEEDS WORK` | If sub-branch count under `<TICKET>/` ≥ 3 → **Halt**: cap exhausted. Else **Phase: Implementation (fix-forward)**. See §3b. |
| 7 | Status = `In Review` AND latest verdict marker = `Code Reviewer Verdict: BLOCKERS` | If sub-branch count ≥ 3 → **Halt**: cap exhausted. Else **Phase: Implementation (fix-forward)**. |
| 8 | Status = `In Review` AND label `ai-reviewed` not set | **Phase: Code Review**. See §3c. |
| 9 | Status = `In Review` AND label `ai-reviewed` set AND label `ai-tested` not set | **Phase: Reality Check**. See §3d. |
| 10 | Status = `In Review` AND both `ai-reviewed` + `ai-tested` set | **Terminal**. See §4. |
| 11 | Anything else (e.g. unexpected status, missing integration branch when expected) | **Halt** with the observed state and tell the user what to fix. |

After any phase that reports success, **return to step 1** (re-read state) and re-route. The skill is a loop, not a straight pipeline. The loop ends only at a Halt or after the Terminal block.

If the loop exceeds 10 iterations on the same ticket within a single invocation, halt with a defensive `loop-limit-reached` message — that signals an unintended cycle.

---

## 3. Phases

For every phase, brief the subagent with the **minimum** information it needs. Do not paste plan bodies, full commit history, or unrelated tickets — those bloat the subagent's startup context.

**Status transitions are orchestrator-owned.** Before spawning any phase subagent, the orchestrator must drive Jira to the status that reflects the work currently in progress, so anyone reading Jira sees what an agent is doing *right now* instead of seeing it after the fact. Subagents must not transition status themselves; they only post verdict comments and labels. The orchestrator drives `In Progress → In Review` directly after the e2e gate passes — see §3a/§3b.

### 3.0 Planning (only when an in-ticket directive requests takeover)

This phase fires only when row 1 matches AND a `plan-takeover` directive is active in the ticket description. It is **not** part of the default pipeline — without the directive, the absence of a plan halts the run.

**Pre-phase marker (orchestrator → Steward):** ask the Steward to post `Planning takeover triggered by ticket directive: <directive verbatim>` so the action is auditable. Status stays at whatever it currently is (typically `Plan` / `Planned`); no transition.

Then invoke the `/grill-me` skill with the **Plan** subagent (the architect/plan agent) and brief it with:

```
Ticket: <TICKET-ID>
Description: <verbatim description, with the INSTRUCTION TO LLM directive lines stripped>
Goal: produce a canonical implementation plan suitable for posting to Jira as the
      "Implementation Plan (canonical)" comment.
Hand the final plan to the Jira Workflow Steward to persist (see CLAUDE.md "Plan
Persistence" section): write to <plansDirectory>/<TICKET-ID>-<slug>.md AND post the
canonical plan comment on Jira.
```

On return:
- If the Plan agent / `/grill-me` cycle completed and a plan comment now exists on Jira: **re-route from step 1**. Row 1 will no longer match.
- If the user aborted grilling or the Plan agent could not converge: **Halt** with `Planning takeover did not produce a plan. Resolve manually and re-run.` Do not retry automatically — planning is interactive and the user must drive it.

The directive is satisfied as soon as a plan comment exists; the orchestrator does not re-trigger §3.0 on subsequent loop iterations.

### 3a. Implementation (fresh)

**Pre-phase transition (orchestrator → Steward):** before spawning the Implementer, ask the Steward to transition `Plan → In Progress` and post a short worklog/comment naming the sub-branch (e.g. `Implementation starting on <TICKET-ID>/impl-1`). If the ticket is already in `In Progress`, skip the transition but still post the starting-comment so the run is traceable.

Then spawn `Implementer` (subagent_type: `Implementer`) with mode = `fresh`:

```
Ticket: <TICKET-ID>
Mode: fresh
Plan: read from Jira via Steward (canonical plan comment)
Integration branch: <TICKET-ID> (create off ai-workshop/day1/0-init if missing)
First sub-branch: <TICKET-ID>/impl-1
Status: ticket has already been transitioned to In Progress by the orchestrator.
  Do NOT transition the ticket yourself. When implementation is complete, post the
  "Implementer: Ready for Review" verdict comment naming the branch via the Steward.
  The orchestrator will run the e2e gate and drive the In Progress → In Review transition.
```

On return:
- If `gate-red`, `subtask-blocked`, `branch-conflict`, or `steward-refused`: **Halt**, surface the verdict to the user. Status stays `In Progress` so Jira reflects that work is paused mid-implementation.
- If success: proceed to the **E2E gate** below.

**E2E gate (orchestrator, while still `In Progress`):** run:

```bash
bash e2e/run.sh 2>&1
E2E_EXIT=$?
```

Interpret the output:
- Exit 0 → **pass**. Ask the Steward to transition `In Progress → In Review`. Re-route from step 1.
- Exit non-zero AND at least one `FAILED_AT=` line present → **test failure**. Ask the Steward to post a comment naming the failing scenario(s) and `FAILED_AT=` point(s). Check the sub-branch cap: if sub-branch count ≥ 3 → **Halt** with the cap message. Otherwise treat as BLOCKERS-equivalent and enter **Phase: Implementation (fix-forward)** (§3b) with the full `run.sh` output as the blocking context.
- Exit non-zero AND no `FAILED_AT=` in output → **infra failure**. **Halt**: `Halt: e2e/run.sh exited non-zero with no FAILED_AT marker — infra likely not running. Start the app (see /local-dev) and re-run /ticket <TICKET-ID>.`

### 3b. Implementation (fix-forward)

Determine `N`: `sub-branch count under <TICKET-ID>/` so the new branch is `<TICKET-ID>/fix-<N>`. (impl-1 + fix-1 already → next is fix-2.)

Determine `prior-branch`: the sub-branch the failed verdict was reported against (parse the verdict-marker comment for `branch <TICKET>/...`).

**Pre-phase transition (orchestrator → Steward):** before spawning the Implementer, ask the Steward to transition `In Review → In Progress` and post a worklog/comment naming the new sub-branch (e.g. `Fix-forward starting on <TICKET-ID>/fix-<N>, addressing <BLOCKERS|NEEDS WORK>`). This makes Jira show that the ticket has dropped back into active implementation while the fix is in flight. If the workflow does not allow `In Review → In Progress` directly, surface the unavailable transition to the user and halt — do not silently pick a near-match.

Then spawn `Implementer` with mode = `fix-forward`:

```
Ticket: <TICKET-ID>
Mode: fix-forward
Prior sub-branch: <prior-branch>
New sub-branch: <TICKET-ID>/fix-<N> (cut OFF prior-branch, not off integration)
Blocking context: <paste full text of the latest BLOCKERS, NEEDS WORK, or e2e gate failure output>
Attached screenshots (if any): <list filenames the Steward returned>
Status: ticket has already been transitioned to In Progress by the orchestrator.
  Do NOT transition the ticket yourself. When the fix is complete, post the
  "Implementer: Ready for Review" verdict comment naming the new sub-branch via the Steward.
  The orchestrator will run the e2e gate and drive the In Progress → In Review transition.
```

On return: run the **E2E gate** as described in §3a. On pass, ask the Steward to transition `In Progress → In Review` and re-route. On failure, apply the same cap check and fix-forward / infra-halt logic.

### 3c. Code Review

Determine the sub-branch under review: the latest sub-branch under `<TICKET-ID>/` (by commit time, not name).

**Pre-phase marker (orchestrator → Steward):** the Jira status is already `In Review` (that is the trigger for this phase), so no status transition is needed. But before spawning the Code Reviewer, ask the Steward to post a short comment `Code review in progress on <sub-branch>` so the live activity is visible alongside the status. Skip if the most recent comment on the ticket already announces the same review on the same branch (idempotent).

Spawn `Code Reviewer` (subagent_type: `Code Reviewer`) with:

```
Ticket: <TICKET-ID>
Branch under review: <sub-branch>
Diff command: git diff ai-workshop/day1/0-init...<sub-branch>
Plan: read from Jira via Steward (canonical plan comment) — for "did the implementation match the plan?" check
Status: do NOT transition the ticket. Status stays In Review for the duration of review.
After reviewing, ask the Steward to post your verdict comment with the FIRST LINE being EXACTLY one of:
  Code Reviewer Verdict: APPROVED
  Code Reviewer Verdict: BLOCKERS
followed by a blank line and your detail.
On APPROVED, ask the Steward to also add the `ai-reviewed` label.
On BLOCKERS, do NOT add the label.
```

On return: re-route from step 1.

### 3d. Reality Check

**Pre-phase marker (orchestrator → Steward):** status is already `In Review` with the `ai-reviewed` label set. Before spawning the Reality Checker, ask the Steward to post a short comment `Reality check in progress on <latest sub-branch>` so the live activity is visible. Idempotent: skip if the most recent comment already announces the same check on the same branch.

Spawn `Reality Checker` (subagent_type: `Reality Checker`) with:

```
Ticket: <TICKET-ID>
Branch under test: <latest sub-branch>
Acceptance criteria: read from Jira via Steward (the canonical plan comment)
Working tree: ensure <latest sub-branch> is checked out before you run any e2e
Status: do NOT transition the ticket. Status stays In Review for the duration of testing.
After testing, ask the Steward to post your verdict comment with the FIRST LINE being EXACTLY one of:
  Reality Checker Verdict: READY
  Reality Checker Verdict: NEEDS WORK
followed by a blank line and your detail. Attach all relevant screenshots via the Steward.
On READY, ask the Steward to also add the `ai-tested` label.
On NEEDS WORK, do NOT add the label.
```

On return: re-route from step 1.

---

## 4. Terminal block (green-green merge)

Runs when row 10 of the route table matches: status `In Review`, both `ai-reviewed` and `ai-tested` labels present.

This is the **one** git operation the orchestrator performs directly. Implementer is not re-invoked.

```bash
# Determine the latest sub-branch (by commit time)
LATEST_SUB=$(git for-each-ref --sort=-committerdate --format='%(refname:short)' "refs/heads/<TICKET>/*" | head -1)
INTEGRATION="<TICKET>"

# Sanity: integration branch must be reachable from the sub-branch
git merge-base --is-ancestor "$INTEGRATION" "$LATEST_SUB" || {
  # Halt: the integration branch diverged. Surface to user; do not auto-merge.
  exit 1
}

# Fast-forward integration to the green sub-branch tip
git checkout "$INTEGRATION"
git merge --ff-only "$LATEST_SUB"

# Delete the local sub-branch (no remote — we never pushed)
git branch -D "$LATEST_SUB"
```

Then hand off to the Steward:

```
Transition <TICKET> from In Review → Test.
Post a comment titled "Automation pipeline complete" with body:
  - Integration branch: <TICKET> at SHA <new-tip>
  - Subtasks closed: <list>
  - Reviewer: APPROVED (label ai-reviewed)
  - Reality Checker: READY (label ai-tested)
  - Next step (human): smoke the branch locally, then transition Test → Done and merge to ai-workshop/day1/0-init.
```

Exit with one final stdout line:

```
/ticket <TICKET>: complete. status=Test, integration=<TICKET>@<sha>, branch ready for human QA.
```

---

## 5. Output discipline

The orchestrator's stdout is the **audit log** for this run. One line per phase boundary, no streaming subagent output:

```
/ticket <TICKET>: phase=implementation(fresh) → branch=<TICKET>/impl-1 SHA=<sha>
/ticket <TICKET>: phase=e2e-gate → PASS
/ticket <TICKET>: phase=code-review → APPROVED (ai-reviewed)
/ticket <TICKET>: phase=reality-check → NEEDS WORK
/ticket <TICKET>: phase=implementation(fix-forward) → branch=<TICKET>/fix-2 SHA=<sha>
/ticket <TICKET>: phase=e2e-gate → FAILED at user-fe-happy-path:login
/ticket <TICKET>: phase=implementation(fix-forward) → branch=<TICKET>/fix-3 SHA=<sha>
/ticket <TICKET>: phase=e2e-gate → PASS
/ticket <TICKET>: phase=code-review → APPROVED (ai-reviewed)
/ticket <TICKET>: phase=reality-check → READY (ai-tested)
/ticket <TICKET>: complete. status=Test, integration=<TICKET>@<sha>
```

All detail goes to Jira. The user reads Jira to get rich feedback; they read your stdout to know how far the pipeline got.

---

## 6. Failure modes (halts)

These are the only halts. All other situations should re-route.

| Halt | Stdout |
|------|--------|
| no plan | `Halt: no plan on <TICKET>. Write a plan and run the Steward to post it.` |
| unknown directive | `Halt: ticket directive recognized but not supported: <directive>. Either implement support in /ticket or remove the directive.` |
| planning takeover failed | `Halt: planning takeover did not produce a plan. Resolve manually and re-run.` |
| past gate | `Halt: <TICKET> already past automation gate (status=<X>).` |
| in-flight | `Halt: in-flight branch <branch> on <TICKET>. Resolve manually.` |
| auto-fix cap | `Halt: <TICKET> exhausted auto-fix cap (3 sub-branches). Latest verdict: <X>. Read Jira and decide.` |
| e2e-gate-infra | `Halt: e2e/run.sh exited non-zero with no FAILED_AT marker — infra likely not running. Start the app (see /local-dev) and re-run /ticket <TICKET-ID>.` |
| gate-red (Implementer) | `Halt: Implementer reported gate-red on <branch>: <reason>. Branch is committed; fix and re-run.` |
| subtask-blocked | `Halt: subtask <ID> blocked: <reason>.` |
| branch-conflict | `Halt: <reason>.` |
| steward-refused | `Halt: Steward refused: <reason>.` |
| diverged integration | `Halt: integration branch <TICKET> diverged from sub-branch <X>; cannot fast-forward.` |
| loop-limit | `Halt: loop-limit-reached on <TICKET>. Likely cause: a phase failed to update Jira state. Inspect manually.` |

---

## 7. What this skill is NOT

- A planner. (Use `/grill-me` or the `Plan` agent first.)
- A PR creator. (No GitHub integration in this workflow.)
- A merger to `ai-workshop/day1/0-init`. (Human owns final merge after `Test → Done`.)
- A pusher. (Nothing reaches the remote until the human pushes.)
- A Jira API client. (Always go through the Steward.)

---

## 8. Recovery / resume

The skill is fully resumable. If a previous run was interrupted (network failure, agent crash, user Ctrl-C), `/ticket <TICKET-ID>` can be re-run safely:

- Implementer's commits, if any, are on the sub-branch.
- Verdict markers are on Jira if a phase completed.
- The route table will pick up at the right phase.

Two resume paths to be aware of:
- Status = `In Progress` AND the latest comment is an orchestrator "Implementation starting" / "Fix-forward starting" marker (row 4) → safe auto-resume: the route table picks up at the matching implementation phase on the same sub-branch.
- Status = `In Progress` AND no such marker (row 3 halt) → likely a human or external agent is mid-edit. Either complete the impl manually + transition, or delete the sub-branch, then re-run.

Other case that needs manual cleanup: multiple verdict-marker comments out of expected order (e.g. someone added a manual `BLOCKERS` after `READY` was already set). Read Jira, resolve, re-run.
</content>
</invoke>

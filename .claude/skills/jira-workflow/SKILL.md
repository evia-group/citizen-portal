---
name: jira-workflow
description: Jira ticket lifecycle workflow — status transitions, AI labels, plan persistence, subtask splitting, and the 6-step delivery process. Load this skill whenever an agent needs to advance a ticket through Planning → In Progress → In Review → Test.
---

# Ticket Workflow

Defines the end-to-end Jira ticket lifecycle that all delivery agents must follow. Every phase gate below is mandatory; do not skip or batch transitions.

**Before making any Jira API call, load the `jira-mcp` skill (`.claude/skills/jira-mcp/SKILL.md`).** It is the authoritative reference for correct MCP tool names, call patterns, label safety rules, and error handling. This skill defines *when* and *why* to call Jira; `jira-mcp` defines *how*.

---

## Status Transitions

Keep Jira status in lockstep with the actual delivery phase. **Transition the ticket _before_ the corresponding work starts**, not after — Jira should answer "what is being worked on right now?" not "what was last completed?". This means a planning agent transitions to `Plan` as it begins planning, the orchestrator transitions to `In Progress` before the Implementer starts, and so on. Do not batch transitions or rely on the developer to remember.

| Trigger | From → To | When to Apply |
|---------|-----------|----------------|
| A planning agent (e.g. `/grill-me`, `Plan` subagent, or a human starting to author a plan artifact) is **about to start** producing the plan for the ticket | `To Do` / `Backlog` / `Selected for Development` → **`Plan`** | At the moment planning begins — before the plan body is written or posted. The plan-comment post and `ai-planned` label come at the end of planning, but the status flip happens up front so Jira shows planning is in flight |
| The orchestrator (e.g. `/ticket`) or developer is **about to start** implementation work on the ticket | `Plan` / `To Do` → **`In Progress`** | Before the Implementer subagent is spawned (or, for a human, before the first branch creation / first code edit). The ticket should already read `In Progress` while the agent is editing, not flip after the fact |
| The orchestrator or developer is **about to hand the change off** for review (Implementer subagent finished its commits / PR is being opened) | `In Progress` → **`In Review`** | At the moment the implementation phase ends — paired with posting the "Ready for Review" or PR-link comment. The Code Reviewer / Reality Checker subagents read the ticket already in `In Review`; they do not transition it themselves |
| Code Reviewer agent signs off, **all subtasks are closed**, commits are confirmed, and the **Reality Checker subagent returns `READY`** | `In Review` → **`Test`** | When (1) Code Reviewer has no blocking findings, (2) every subtask is in a terminal state (Done/Closed/Resolved), (3) the final commit is confirmed, AND (4) the Reality Checker subagent clears the work — all four conditions must be true |
| The orchestrator is about to start a fix-forward iteration in response to `BLOCKERS` or `NEEDS WORK` | `In Review` → **`In Progress`** | Before the Implementer is re-spawned in fix-forward mode, paired with a `Fix-forward starting on <branch>` comment. The ticket drops back into `In Progress` so Jira reflects that work is being redone, not awaiting review |

Transition rules:
- Follow the **Status Transitions** call pattern in `jira-mcp` — always discover the transition ID via `getTransitionsForJiraIssue` before calling `transitionJiraIssue`; never hardcode IDs
- If the expected target status is not available from the current status, surface this to the user instead of silently picking a near-match
- Add a short worklog or comment when transitioning to `In Progress` (branch name) and `In Review` (PR link) — see **Worklog** in `jira-mcp`
- Never transition tickets backwards without explicit user direction; ask first
- Status names may vary per project — `Plan` may also appear as `Planned` or `Ready for Development`. Confirm the canonical name from the project's transition list before acting

---

## AI Agent Labels

Apply labels to tickets to make AI involvement visible at a glance. Labels are **additive** — follow the **Label Management** call pattern in `jira-mcp` (read current labels, merge, write).

| Label | Applied when | Who triggers it |
|-------|-------------|-----------------|
| `ai-planned` | The plan comment is posted to Jira (the `Plan` status itself is set up front when planning begins; the label confirms the plan body actually landed) | Step 2 |
| `ai-reviewed` | The Code Reviewer subagent returns its verdict with no blocking findings | Step 5 |
| `ai-tested` | The Reality Checker subagent returns a `READY` verdict | Step 6 |

Label rules:
- Apply the label in the same operation as the corresponding status transition or agent sign-off
- Labels are applied to the **parent ticket** only; subtasks are not labelled individually unless the user asks

---

## Screenshot Evidence

When e2e tests, Reality Checker runs, or bug reports produce screenshots, attach them to the Jira ticket and reference them inline in the evidence comment.

**When to attach:**

| Trigger | Which screenshots |
|---------|------------------|
| Bug filed (Reality Checker `NEEDS WORK`) | All `rc-*.png` that show the failing step |
| Step 6 `READY` verdict | Final confirmation screenshot(s) from the run |
| Any `NEEDS WORK` comment posted | Screenshots of the failing state |
| Developer explicitly asks | Any screenshot from `e2e/<scenario>/screenshots/` |

**Process** (follow the **Attachments** section in `jira-mcp`):
1. Upload each screenshot via `curl` to the Jira REST API before posting the evidence comment
2. Reference uploaded screenshots inline in the comment using Jira wiki `!filename.png|thumbnail!` syntax
3. For each screenshot include: viewport/context, one-sentence observation, pass/fail verdict
4. If upload fails, fall back to posting file paths and descriptions as plain text — do not block the workflow

---

## Plan Persistence to Jira

Implementation agents read from Jira, not from local scratch files. The local `.claude/plans/<TICKET>-*.md` file is a working artifact; the **Jira ticket comment is the canonical plan** that downstream agents act on.

When a plan is approved and the ticket is about to be (or just was) transitioned to `Plan`, mirror the full plan into a Jira comment **before any implementation begins**.

Rules:
- Follow the **Posting Comments** call pattern in `jira-mcp` to post the comment
- Post the **full plan body** verbatim from the plan artifact — do not summarise or truncate; include the `Implementation Plan (canonical — implementation agents read from here)` prefix
- Post the **entire plan content** to the ticket — do not post a link or file path. The ticket must be self-contained
- If the plan changes after the initial post, post a **new** comment with a `Plan Update — YYYY-MM-DD` header; do not edit the original
- Do this **before** the `To Do` → `Plan` transition (or immediately after, in the same operation)
- If the project workflow lacks a `Plan` status, still post the plan comment before transitioning to `In Progress`
- Treat absence of a plan comment on a ticket already in `Plan` / `In Progress` as a workflow defect: surface it and post the plan before continuing
- Never put secrets, credentials, or customer PII into the plan comment

---

## Splitting Multi-Step Plans into Subtasks

If the plan contains **more than one step or phase**, split it into Jira **subtasks** under the parent ticket — one subtask per step/phase. Single-step plans stay on the parent ticket only.

- Treat any of the following as a step/phase boundary: numbered steps (`1.`, `2.`, …), top-level `## Phase` / `## Step` headings, or an explicit "Phases"/"Steps" list in the plan
- Follow the **Creating Issues and Subtasks** call pattern in `jira-mcp` for each subtask:
  - **Summary**: `<PARENT-ID> – Step <N>: <step title>` (e.g. `BP-8 – Step 2: Wire NativeWind cache placeholders`)
  - **Description**: the full markdown body of that step/phase only — not a pointer back to the plan file
- Keep the **full plan** on the parent ticket; subtasks carry their slice
- Preserve plan order via the subtasks' creation order
- If the plan changes and steps are added/removed/reordered, reconcile the subtasks: create missing ones, close obsolete ones (with a comment pointing to the new plan revision), and edit summaries/descriptions of changed ones

---

## 6-Step Delivery Process

### Step 1: Confirm the Jira Anchor
- Identify whether the request needs a branch, commit, PR output, or full workflow guidance
- Verify that a Jira task ID exists before producing any Git-facing artifact
- If the request is unrelated to Git workflow, do not force Jira process onto it

### Step 2: Classify the Change and Sync Status (Planning → `Plan`)
- Determine whether the work is a feature, bugfix, hotfix, refactor, docs, test, config, or dependency update
- Choose the branch type based on deployment risk and base branch rules
- Select the Gitmoji based on the actual change
- **As planning begins** — before the plan body is written — transition the ticket to **`Plan`** (use the `jira-mcp` transition discovery pattern) so Jira reflects that planning is in flight. Post a short comment naming the planning agent / driver (e.g. `Planning started via /grill-me`) so the live activity is visible.
- **Once the plan artifact is finalised**:
  1. **Post the full plan as a Jira comment** on the ticket (see *Plan Persistence to Jira* above; use `addCommentToJiraIssue` via the `jira-mcp` call pattern)
  2. **Add the `ai-planned` label** (use the `jira-mcp` label management pattern)
  3. Verify the comment is visible on the ticket before handing off to any implementation agent
- If the plan turns out to be unnecessary (e.g. the work is dropped), transition back from `Plan` to `To Do` with an explicit user-confirmed note — never let the ticket sit in `Plan` with no plan attached

### Step 3: Build the Delivery Skeleton (Implementation start → `In Progress`)
- Generate the branch name using the Jira ID plus a short hyphenated description
- Plan atomic commits that mirror reviewable change boundaries
- Prepare the PR title, change summary, testing section, and risk notes
- **Before the Implementer is spawned** (or before the human's first branch creation / first code edit), the orchestrator (or driver) transitions the ticket to **`In Progress`** (use the `jira-mcp` transition pattern) and adds a worklog/comment with the branch name (e.g. `Implementation starting on <TICKET>/impl-1`). The ticket must read `In Progress` while the Implementer is editing, not flip afterwards. Implementer subagents themselves must not perform this transition

### Step 4: Review for Safety and Scope
- Remove secrets, internal-only data, and ambiguous phrasing from commit and PR text
- Check whether the change needs extra security review, release coordination, or rollback notes
- Split mixed-scope work before it reaches review

### Step 5: Close the Traceability Loop (Implementation done → `In Review`)
- Ensure the PR clearly links the ticket, branch, commits, test evidence, and risk areas
- Confirm that merges to protected branches go through PR review
- **As soon as the Implementer's commits are final and the work is being handed off** (PR opened, or the orchestrator is about to spawn the Code Reviewer), transition the ticket to **`In Review`** (use the `jira-mcp` transition pattern) and add a comment with the PR link / branch name. This transition is owned by the orchestrator/driver — Code Reviewer and Reality Checker subagents read the ticket already in `In Review`
- **Before the Code Reviewer subagent is spawned**, post a short marker comment `Code review in progress on <branch>` so Jira shows the live activity (idempotent — skip if the most recent comment already says this for the same branch)
- **When the Code Reviewer subagent returns its verdict with no blocking findings**, add the `ai-reviewed` label (use the `jira-mcp` label management pattern)
- **Before the Reality Checker subagent is spawned**, post a marker comment `Reality check in progress on <branch>` (same idempotency rule)
- **On `BLOCKERS` or `NEEDS WORK`**, the orchestrator transitions the ticket back to **`In Progress`** before re-spawning the Implementer in fix-forward mode (see Status Transitions table)

### Step 6: Advance to Testing (`In Review` → `Test`)

Trigger only when **all three conditions are simultaneously true**:
1. **Review complete**: Code Reviewer subagent has returned its verdict with no blocking findings
2. **All subtasks closed**: Every subtask is in a terminal Jira state (`Done`, `Closed`, or `Resolved`) — verify via `getJiraIssue`
3. **Commits confirmed**: The final implementation commit is confirmed on the branch

**Before transitioning, spawn the Reality Checker subagent** (`.claude/agents/testing-reality-checker.md`). Brief it with the ticket ID, PR/branch link, acceptance criteria from the Jira plan comment, and the commit SHA.

- **`NEEDS WORK`**: Do not transition. **Upload any screenshots from the failing run** (see *Screenshot Evidence* above and **Attachments** in `jira-mcp`), then post the Reality Checker's full report as a Jira comment with inline screenshot references. Surface blocking findings to the user and halt. The ticket stays `In Review`.
- **`READY`**: **Upload confirmation screenshots** (see *Screenshot Evidence* above), add the `ai-tested` label (use `jira-mcp` label pattern), then:
  - Discover the `Test` transition ID via `getTransitionsForJiraIssue` (use `jira-mcp` transition pattern)
  - Call `transitionJiraIssue`
  - Add a comment summarising: reviewer sign-off, Reality Checker verdict, list of closed subtasks, and final commit SHA or PR link
  - If any subtask is still open, **do not transition** — surface the open subtask(s) and wait for explicit direction

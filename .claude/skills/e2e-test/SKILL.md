---
name: e2e-test
description: E2E testing team workflow for the Bürgerportal monorepo. Use when the user wants to run, write, or debug end-to-end tests under the `e2e/` directory — covers the black-box reading boundary and the `run.sh` fast-path check before spawning the agent team.
---

# E2E Testing Team Workflow

**When working on e2e tests, only read files inside the `e2e/` directory.** Do not read application source code under `apps/`, `libs/`, or anywhere else outside `e2e/` — e2e scenarios are black-box browser tests and must be written from the user's perspective, not from implementation knowledge.

**Before spawning a team, always check for a `run.sh` fast path.** Many scenario files declare one
at the top (look for a "Fast path:" callout). If `run.sh` exists in the scenario directory:

1. Check infrastructure is running first via /local-dev skill
2. Run `time bash e2e/<scenario>/run.sh 2>&1; echo "EXIT:$?"` — it covers the full scenario via API + minimal browser steps.
3. If it exits 0 and prints `PASS`, the scenario passed. **Do not spawn the agent team.**
4. If it exits non-zero with `FAILED_AT=<phase>:<step>` on stderr, note the failure point and fall
   back to the agent team below, starting from the failed step.

Only spawn the team when `run.sh` is absent or has failed.

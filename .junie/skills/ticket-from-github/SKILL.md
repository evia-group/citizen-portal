---
name: ticket-from-github
description: Pull a GitHub issue by its id from the project repository and persist it as a plan file (ID-<n>-<short-name>.md) under the project's plans location, ready to be implemented with the `ticket` skill. TRIGGER when the user asks to fetch/pull/import a GitHub issue by number, turn an issue into a plan/ticket, or run the fix automation for an issue id.
---

# GitHub Issue → Plan

Use this skill to turn a GitHub issue into a local plan file and hand it off to the
`/ticket` skill for implementation.

Invocation: `/ticket-from-github <issue-number> [short-name] [owner/repo]`
(e.g. `/ticket-from-github 9` or `/ticket-from-github 8 docker-a-mod`).

## Defaults

- **Repository**: `evia-group/citizen-portal` (override with the 3rd argument `owner/repo`).
- **Plans location**: `.junie/plans/`.
- **File name**: `ID-<issue-number>-<short-name>.md`. When `short-name` is omitted it is
  derived from the issue title (lowercased, non-alphanumerics → `-`, first ~4 words).

## Action Algorithm

1. Determine the issue number (required) and, if provided, the short name and `owner/repo`.
2. Pull the issue using `github-issues` skill
3. Write `.junie/plans/ID-<number>-<short-name>.md` with this structure (compatible with the
   `ticket` skill):

   ```
   [Original Ticket]

   # #<number>: <title>

   - Source: <html_url>
   - State: <state>
   - Labels: <labels>

   <issue body>

   [Plan]
   ```

   Leave the `[Plan]` section empty — the `/ticket` skill fills it in.
4. Invoke `/ticket ID-<number>-<short-name>.md`.

## Notes

- The helper script resolves the project root from its own location, so it always writes
  into `<project>/.junie/plans/` regardless of the current working directory.
- Setting `GITHUB_PERSONAL_ACCESS_TOKEN` (or `GITHUB_TOKEN`) raises API rate limits and is
  required for private repositories; for public repos it is optional.

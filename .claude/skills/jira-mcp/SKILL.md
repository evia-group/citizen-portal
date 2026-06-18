---
name: jira-mcp
description: Authoritative call patterns for all Jira Atlassian MCP tools. Load this skill before making any Jira API call to ensure correct tool use, safe label handling, and proper transition discovery.
---

# Jira MCP Tool Reference

Canonical call patterns for every Jira Atlassian MCP operation.
**Any agent that reads from or writes to Jira MUST load this skill before calling any `mcp__claude_ai_Atlassian__*` Jira tool.**

---

## Available Jira MCP Tools

| Tool | Purpose |
|------|---------|
| `mcp__claude_ai_Atlassian__getJiraIssue` | Fetch full issue — status, labels, subtasks, fields |
| `mcp__claude_ai_Atlassian__searchJiraIssuesUsingJql` | Search issues with JQL |
| `mcp__claude_ai_Atlassian__createJiraIssue` | Create a new issue or subtask |
| `mcp__claude_ai_Atlassian__editJiraIssue` | Update fields (summary, labels, assignee, etc.) |
| `mcp__claude_ai_Atlassian__getTransitionsForJiraIssue` | List status transitions valid from the current state |
| `mcp__claude_ai_Atlassian__transitionJiraIssue` | Move an issue to a new status |
| `mcp__claude_ai_Atlassian__addCommentToJiraIssue` | Post a comment |
| `mcp__claude_ai_Atlassian__addWorklogToJiraIssue` | Log work time |
| `mcp__claude_ai_Atlassian__createIssueLink` | Link two issues together |
| `mcp__claude_ai_Atlassian__getIssueLinkTypes` | List available link type names |
| `mcp__claude_ai_Atlassian__getJiraIssueRemoteIssueLinks` | Get remote links on an issue |
| `mcp__claude_ai_Atlassian__getVisibleJiraProjects` | List accessible projects |
| `mcp__claude_ai_Atlassian__getJiraProjectIssueTypesMetadata` | Get issue types for a project |
| `mcp__claude_ai_Atlassian__getJiraIssueTypeMetaWithFields` | Get required fields for an issue type |
| `mcp__claude_ai_Atlassian__lookupJiraAccountId` | Resolve a user account ID by email |
| `mcp__claude_ai_Atlassian__atlassianUserInfo` | Get the current authenticated user |

---

## Status Transitions

**Rule: Never hardcode a transition ID. Always discover it first.**

```
Step 1: mcp__claude_ai_Atlassian__getTransitionsForJiraIssue(issueId)
         → returns [{id, name}, ...] valid from current state only

Step 2: Match target status name (case-insensitive).
        Known aliases: "Plan" / "Planned" / "Ready for Development",
                       "In Progress" / "In Dev",
                       "In Review" / "Review",
                       "Test" / "Testing" / "QA"

Step 3: mcp__claude_ai_Atlassian__transitionJiraIssue(issueId, transitionId)
```

If the target status is absent from the available list, **do not pick the closest match**.
Surface the blockage:
`"Transition to '<target>' is not available from '<current>'. Available: [list names]."`

---

## Label Management

**Rule: Labels are additive. Always read before writing — never overwrite.**

```
Step 1: issue = mcp__claude_ai_Atlassian__getJiraIssue(issueId)
        existing = issue.fields.labels   // may be [] — always check

Step 2: if newLabel not in existing:
            merged = existing + [newLabel]
        else:
            merged = existing  // already present, skip the write

Step 3: mcp__claude_ai_Atlassian__editJiraIssue(issueId, { labels: merged })
```

Passing only the new label destroys all prior labels. Never do that.

If `editJiraIssue` fails to apply the label, post a comment on the ticket:
`"Label '<label>' could not be applied automatically. Please add it manually."` — then continue; do not block the workflow.

---

## Posting Comments

```
mcp__claude_ai_Atlassian__addCommentToJiraIssue(issueId, body)
```

- Use plain text or Jira wiki markup in `body` — not Markdown
- Plan comments must be prefixed: `Implementation Plan (canonical — implementation agents read from here)`
- Plan updates must be a **new** comment with header `Plan Update — YYYY-MM-DD`; never edit the original plan comment
- Include footer on plan comments: `Local working copy: .claude/plans/<TICKET>-<slug>.md`

---

## Creating Issues and Subtasks

```
mcp__claude_ai_Atlassian__createJiraIssue({
  project:   { key: "<PROJECT_KEY>" },
  issuetype: { name: "Subtask" },       // discover from getJiraProjectIssueTypesMetadata if unsure
  parent:    { key: "<PARENT_KEY>" },   // required for subtasks
  summary:   "<PARENT_KEY> – Step <N>: <step title>",
  description: "<full step body — not a link to the plan file>"
})
```

- Call `getJiraProjectIssueTypesMetadata` first if "Subtask" is not the canonical type name in the project
- Call `getJiraIssueTypeMetaWithFields` to discover required fields before creating

---

## Editing Fields

```
mcp__claude_ai_Atlassian__editJiraIssue(issueId, fields)
```

`fields` is a **partial update** — only include keys you intend to change:
```json
{ "labels": ["ai-planned", "existing-label"], "assignee": { "accountId": "..." } }
```

---

## Worklog

```
mcp__claude_ai_Atlassian__addWorklogToJiraIssue(issueId, timeSpent, comment)
```

- `timeSpent` format: `"1h 30m"`, `"2h"`, `"30m"`
- Add worklogs at `In Progress` transition (comment: branch name) and `In Review` transition (comment: PR link)

---

## Issue Linking

```
Step 1: mcp__claude_ai_Atlassian__getIssueLinkTypes()
         → find the correct link type name (e.g. "Relates", "Blocks", "Duplicates")

Step 2: mcp__claude_ai_Atlassian__createIssueLink({
          type:         { name: "Relates" },
          inwardIssue:  { key: "<KEY-A>" },
          outwardIssue: { key: "<KEY-B>" }
        })
```

---

## Searching Issues

```
mcp__claude_ai_Atlassian__searchJiraIssuesUsingJql(jql, fields, maxResults)
```

Common JQL patterns:
```
project = BP AND status = "In Progress" ORDER BY updated DESC
project = BP AND parent = BP-10
project = BP AND labels = "ai-planned" AND status != Done
project = BP AND issueType = Subtask AND parent = BP-10
```

---

## Attachments (curl fallback — no MCP tool)

The Atlassian MCP toolset does **not** expose an attachment endpoint. To attach a screenshot or any file, fall back to the Jira Cloud REST API via `curl`, using the API token stored in the macOS login keychain.

**Credentials**

| Field | Value |
|-------|-------|
| Keychain service | `jira-api-token` |
| Keychain account | `g.svitlychnyi` |
| Auth email | `gennadii.switlich@evia.de` |
| Jira base URL | `https://evia-buergerportal.atlassian.net` |

Retrieve the token at call time — never echo it, never persist it to disk:

```bash
security find-generic-password -s jira-api-token -a g.svitlychnyi -w
```

**Upload a single file**

```bash
TOKEN=$(security find-generic-password -s jira-api-token -a g.svitlychnyi -w)
curl -sS --fail-with-body \
  -u "gennadii.switlich@evia.de:$TOKEN" \
  -X POST \
  -H "X-Atlassian-Token: no-check" \
  -F "file=@/absolute/path/to/screenshot.png" \
  "https://evia-buergerportal.atlassian.net/rest/api/3/issue/<ISSUE-KEY>/attachments"
unset TOKEN
```

- `X-Atlassian-Token: no-check` is **required** — Jira rejects multipart uploads without it.
- `file=@...` may be repeated to upload several files in one request.
- The response is a JSON array of attachment metadata; capture `id` and `filename` if you need to reference them in a follow-up comment.

**Reference an attachment in a comment**

After uploading, post a comment via the MCP tool. To embed the image inline use Jira wiki markup:

```
!<filename>|thumbnail!
```

The filename must match the uploaded attachment exactly.

**Failure modes**

| Symptom | Cause / Fix |
|---------|-------------|
| `401 Unauthorized` | Token expired or revoked — regenerate at id.atlassian.com and update the keychain entry |
| `403 Forbidden` with `XSRF check failed` | Missing `X-Atlassian-Token: no-check` header |
| `404 Not Found` | Wrong issue key, or the account lacks "Create attachments" permission on the project |
| `413 Payload Too Large` | File exceeds the project's attachment size limit — compress or split |

Do not include the token in any committed file, log line, or chat output. If `security find-generic-password` returns non-zero, surface that to the user and stop — do not prompt for a token interactively.

---

## Error Handling

| Error | Action |
|-------|--------|
| 404 on any call | Issue key is wrong or you lack access — surface to user, do not retry |
| 400 on create/edit | Inspect error message for missing required fields; fetch field metadata then retry once |
| Label write fails | Post a comment documenting the failure, then continue |
| Transition not found | Surface blockage to user with the current status and available transitions list |
| Auth error | Ask the user to verify Atlassian MCP credentials (`mcp__claude_ai_Atlassian__atlassianUserInfo`) |

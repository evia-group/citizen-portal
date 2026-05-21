# User Portal BE — Full REST API Test Scenario

End-to-end test that exercises every public REST endpoint exposed by `apps/user-portal-be`
(`/api/v1/...`) against the locally running stack. The scenario follows a realistic
chain of calls — token → identity → lookups → write operations → cleanup — so values
returned by earlier requests (IDs, refs) feed into later ones.

The scenario is black-box: it talks to the API only, never touches the database, and
asserts on HTTP status + response body shape.

## Static Test Data

| Item                | Value                                                  |
|---------------------|--------------------------------------------------------|
| API base URL        | `http://localhost:8180`                                |
| Keycloak URL        | `http://localhost:9080` (`KC` in `commons.sh`)         |
| Realm               | `portal`                                               |
| OIDC client id      | `user-portal`                                          |
| Test user           | `user` (from `e2e/.env` `USER_NAME`)                   |
| Test password       | (from `e2e/.env` `USER_PASSWORD`, do not expose)       |
| Auth header         | `Authorization: Bearer $TOKEN`                         |
| Content type (JSON) | `Content-Type: application/json`                       |

## Preconditions

- Local stack is running (see `local-dev` skill); user-portal-be reachable at `$API`,
  Keycloak at `$KC`.
- `e2e/.env` defines `USER_NAME` and `USER_PASSWORD`.
- The test user has a `Profile` provisioned (the Keycloak login flow auto-creates one
  on first sign-in via `/api/v1/me`).
- `curl` and `jq` are available.

## Endpoint coverage

| Resource         | Verbs                                        | Path                                                  |
|------------------|----------------------------------------------|-------------------------------------------------------|
| Me               | GET                                          | `/api/v1/me`                                          |
| Domains          | GET                                          | `/api/v1/domains`                                     |
| Categories       | GET                                          | `/api/v1/categories`                                  |
| Locations        | GET                                          | `/api/v1/locations`                                   |
| Services         | GET                                          | `/api/v1/services`                                    |
| Consents         | GET, GET by id                               | `/api/v1/consents`, `/api/v1/consents/{id}`           |
| ConsentLog       | GET, GET by id, POST                         | `/api/v1/consents-log`, `/api/v1/consents-log/{id}`   |
| Profiles         | GET, GET by id, POST, PUT, DELETE            | `/api/v1/profiles`, `/api/v1/profiles/{id}`           |
| Notifications    | GET list, GET by id, PUT                     | `/api/v1/profiles/{id}/notifications/...`             |
| Documents        | GET, GET by id, POST, PUT, DELETE, upload, download | `/api/v1/profiles/{profileId}/documents/...`     |
| Applications     | GET, GET by id, POST, PUT                    | `/api/v1/applications`, `/api/v1/applications/{id}`   |
| Comments         | GET, POST                                    | `/api/v1/comments`                                    |
| MailboxMessages  | GET, GET by id, POST, PATCH                  | `/api/v1/mailbox-messages`, `/api/v1/mailbox-messages/{id}` |
| Dogs             | GET, POST, PUT                               | `/api/v1/dogs`, `/api/v1/dogs/{id}`                   |
| DogApplications  | GET, POST                                    | `/api/v1/dogs-applications`                           |

Total: 14 resources, ~30 endpoints.

## Bash setup (shared by every step)

```bash
set -uo pipefail
cd "$(dirname "$0")"
S=user-be-api          # session name (unused for API runs but commons.sh expects it)
source ../commons.sh   # gives KC, API, USER_NAME, USER_PASSWORD, fail/log/pass, token

TOKEN=$(token "$USER_NAME" "$USER_PASSWORD")
[ -n "$TOKEN" ] || fail "auth:token"

H_AUTH=( -H "Authorization: Bearer $TOKEN" )
H_JSON=( -H "Content-Type: application/json" )

# Helper: GET with status assertion. Echoes body on success.
api_get() {
  local path="$1" expected="${2:-200}" out code
  out=$(curl -sS -o /tmp/api.body -w '%{http_code}' "${H_AUTH[@]}" "$API$path") || fail "curl:$path"
  [ "$out" = "$expected" ] || { cat /tmp/api.body >&2; fail "GET $path → $out (want $expected)"; }
  cat /tmp/api.body
}

# Helper: POST/PUT/PATCH/DELETE with body + status assertion.
api_send() {
  local method="$1" path="$2" body="$3" expected="${4:-200}" code
  code=$(curl -sS -o /tmp/api.body -w '%{http_code}' -X "$method" \
    "${H_AUTH[@]}" "${H_JSON[@]}" -d "$body" "$API$path") || fail "curl:$method:$path"
  [ "$code" = "$expected" ] || { cat /tmp/api.body >&2; fail "$method $path → $code (want $expected)"; }
  cat /tmp/api.body
}
```

## Steps

### 1. Identity — `GET /api/v1/me`

```bash
log "step 1: GET /me"
ME=$(api_get /api/v1/me 200)
PROFILE_ID=$(echo "$ME" | jq -r '.id')
USER_ID=$(echo "$ME" | jq -r '.userId')
[ -n "$PROFILE_ID" ] && [ "$PROFILE_ID" != "null" ] || fail "me:no-profile-id"
```

**Verify**

- `200 OK`, JSON body shape: `ProfileDTO` (`id`, `firstName`, `lastName`, `userId`, …).
- `userId` matches the Keycloak subject for the test user.
- `id` is a positive number — captured as `$PROFILE_ID` for later steps.

### 2. Reference data lookups (read-only)

```bash
log "step 2: lookups"
DOMAINS=$(api_get /api/v1/domains 200)
CATS=$(api_get /api/v1/categories 200)
LOCS=$(api_get /api/v1/locations 200)
SVCS=$(api_get /api/v1/services 200)

DOMAIN_COUNT=$(echo "$DOMAINS" | jq 'length')
CAT_ID=$(echo "$CATS"  | jq -r '.[0].id')
LOC_ID=$(echo "$LOCS"  | jq -r '.[0].id')
SVC_ID=$(echo "$SVCS"  | jq -r '.[0].id')
```

**Verify**

- All four return `200 OK` with arrays.
- `domains`, `categories`, `locations`, `services` each have ≥ 1 element (Liquibase
  seed data is loaded). Capture first IDs for later steps.
- `services` filter — issue `GET /api/v1/services?categoryId=$CAT_ID&locationId=$LOC_ID`
  and confirm `200 OK`; every returned `category.domainName` is **not**
  `Buergerportal-Domain` (controller filters that domain out).

### 3. Consents — list + get by id

```bash
log "step 3: consents"
CONSENTS=$(api_get /api/v1/consents 200)
CONSENT_ID=$(echo "$CONSENTS" | jq -r '.[0].id')
api_get /api/v1/consents/$CONSENT_ID 200 >/dev/null

# Filter combinations
api_get "/api/v1/consents?service_id=$SVC_ID" 200 >/dev/null
```

**Verify**

- `GET /consents` returns array of `ConsentDTO`.
- `GET /consents/{id}` returns the same record.
- Filter by `service_id` returns a (possibly empty) array.

### 4. ConsentLog — POST + list + get by id

```bash
log "step 4: consent log"
CL_BODY=$(jq -n --argjson cid "$CONSENT_ID" --argjson pid "$PROFILE_ID" '{
  status: "ACCEPTED",
  consentText: "Accepted via E2E API run",
  acceptedAt: (now | todate),
  consentId: $cid,
  profileId: $pid
}')
CL=$(api_send POST /api/v1/consents-log "$CL_BODY" 200)
CL_ID=$(echo "$CL" | jq -r '.id')

api_get /api/v1/consents-log 200 >/dev/null
api_get "/api/v1/consents-log?profileId=$PROFILE_ID" 200 >/dev/null
api_get "/api/v1/consents-log?consentId=$CONSENT_ID" 200 >/dev/null
api_get "/api/v1/consents-log?status=ACCEPTED" 200 >/dev/null
api_get /api/v1/consents-log/$CL_ID 200 >/dev/null
```

**Verify**

- `POST` returns `200 OK` and an echoed `ConsentLogDTO` with a non-null `id`.
- All filters return `200 OK`. The list filtered by `$PROFILE_ID` includes `$CL_ID`.

### 5. Profiles — list/get/update self + notifications

```bash
log "step 5: profiles + notifications"
api_get /api/v1/profiles 200 >/dev/null
api_get /api/v1/profiles/$PROFILE_ID 200 >/dev/null
api_get "/api/v1/profiles?firstName=$(echo "$ME" | jq -r .firstName)" 200 >/dev/null

# Update self — toggle canNotifyByMail and toggle back to leave state untouched
ORIG_MAIL=$(echo "$ME" | jq -r '.canNotifyByMail // false')
TOGGLED=$(echo "$ME" | jq --argjson v "$([ "$ORIG_MAIL" = "true" ] && echo false || echo true)" '.canNotifyByMail = $v')
api_send PUT /api/v1/profiles/$PROFILE_ID "$TOGGLED" 200 >/dev/null
api_send PUT /api/v1/profiles/$PROFILE_ID "$ME" 200 >/dev/null   # restore

# Notifications
NOTIFS=$(api_get /api/v1/profiles/$PROFILE_ID/notifications 200)
NOTIF_ID=$(echo "$NOTIFS" | jq -r '.[0].id // empty')
if [ -n "$NOTIF_ID" ]; then
  api_get /api/v1/profiles/$PROFILE_ID/notifications/$NOTIF_ID 200 >/dev/null
  api_send PUT /api/v1/profiles/$PROFILE_ID/notifications/$NOTIF_ID '{}' 200 >/dev/null
else
  log "  no notifications for profile — skipping per-notification GET/PUT"
fi
```

**Verify**

- `GET /profiles` and search by `firstName` both return `200`.
- `GET /profiles/{id}` returns the same DTO as `/me`.
- `PUT /profiles/{id}` echoes the updated profile; the toggle/restore round-trip
  leaves the profile unchanged.
- Notification list returns `200`; if non-empty, fetch + status update both work.

### 6. Documents — full CRUD + upload + download

```bash
log "step 6: documents"
DOC_BODY=$(jq -n '{
  name: "e2e-test-doc.pdf",
  isArchive: false,
  type: "OTHER"
}')
DOC=$(api_send POST /api/v1/profiles/$PROFILE_ID/documents "$DOC_BODY" 200)
DOC_ID=$(echo "$DOC" | jq -r '.id')

api_get /api/v1/profiles/$PROFILE_ID/documents 200 >/dev/null
api_get "/api/v1/profiles/$PROFILE_ID/documents?documentName=e2e-test-doc.pdf" 200 >/dev/null
api_get /api/v1/profiles/$PROFILE_ID/documents/$DOC_ID 200 >/dev/null

# Update metadata
UPDATED=$(echo "$DOC" | jq '.name = "e2e-test-doc-renamed.pdf"')
api_send PUT /api/v1/profiles/$PROFILE_ID/documents/$DOC_ID "$UPDATED" 200 >/dev/null

# Upload binary content (multipart)
TMP_PDF=$(mktemp --suffix=.pdf)
printf '%%PDF-1.4\n%%E2E test stub\n%%EOF\n' > "$TMP_PDF"
UP_CODE=$(curl -sS -o /tmp/api.body -w '%{http_code}' "${H_AUTH[@]}" \
  -F "file=@$TMP_PDF" \
  "$API/api/v1/profiles/$PROFILE_ID/documents/$DOC_ID/upload")
[ "$UP_CODE" = "200" ] || fail "doc:upload"

# Download — verify Content-Disposition
HDR=$(curl -sS -D - -o /tmp/api.bin "${H_AUTH[@]}" \
  "$API/api/v1/profiles/$PROFILE_ID/documents/$DOC_ID/download")
echo "$HDR" | grep -qi 'Content-Disposition: attachment' || fail "doc:download:headers"

# Cleanup
DEL_CODE=$(curl -sS -o /dev/null -w '%{http_code}' -X DELETE "${H_AUTH[@]}" \
  "$API/api/v1/profiles/$PROFILE_ID/documents/$DOC_ID")
[ "$DEL_CODE" = "204" ] || fail "doc:delete:$DEL_CODE"
rm -f "$TMP_PDF"
```

**Verify**

- `POST` creates a document with the supplied `profileId` (controller overrides path
  variable into entity).
- List filter by `documentName` finds the new document.
- `PUT` echoes the new `name`.
- `POST .../upload` returns `200` with a populated `fileId`.
- `GET .../download` returns `200` with `Content-Type: application/pdf` and a
  `Content-Disposition: attachment; filename="..."` header.
- `DELETE` returns `204 No Content`.

### 7. Applications — POST/GET/PUT (no DELETE endpoint)

```bash
log "step 7: applications"
APP_BODY=$(jq -n --argjson sid "$SVC_ID" --argjson pid "$PROFILE_ID" '{
  status: "DRAFT",
  service: { id: $sid },
  profile: { id: $pid }
}')
APP=$(api_send POST /api/v1/applications "$APP_BODY" 200)
APP_ID=$(echo "$APP" | jq -r '.id')

api_get /api/v1/applications 200 >/dev/null
api_get "/api/v1/applications?serviceId=$SVC_ID" 200 >/dev/null
api_get /api/v1/applications/$APP_ID 200 >/dev/null

UPDATED_APP=$(echo "$APP" | jq '.status = "SUBMITTED"')
api_send PUT /api/v1/applications/$APP_ID "$UPDATED_APP" 200 >/dev/null
```

**Verify**

- `POST` returns `200` + new `id`.
- List + filter by `serviceId` both `200`.
- `GET /{id}` returns the application.
- `PUT /{id}` returns updated `status: SUBMITTED`.

### 8. Comments — POST + list filter

```bash
log "step 8: comments"
CM_BODY=$(jq -n --argjson aid "$APP_ID" --arg uid "$USER_ID" '{
  content: "E2E API test comment",
  application: { id: $aid },
  user: { id: $uid }
}')
CM=$(api_send POST /api/v1/comments "$CM_BODY" 200)
CM_ID=$(echo "$CM" | jq -r '.id')

LIST=$(api_get "/api/v1/comments?applicationId=$APP_ID" 200)
echo "$LIST" | jq -e --argjson id "$CM_ID" 'map(.id) | index($id)' >/dev/null \
  || fail "comment:list-missing"
```

**Verify**

- `POST` returns `200` with non-null `id` and the supplied `content`.
- `GET /comments?applicationId=$APP_ID` includes `$CM_ID`.

### 9. MailboxMessages — POST/GET/PATCH

```bash
log "step 9: mailbox messages"
MM_BODY=$(jq -n --argjson pid "$PROFILE_ID" --argjson aid "$APP_ID" '{
  subject: "E2E test message",
  text: "Generated by user-be-api scenario",
  status: "UNREAD",
  sendAt: (now | todate),
  sender: "system@e2e",
  receiver: "user@e2e",
  profileId: $pid,
  applicationId: $aid
}')
MM=$(api_send POST /api/v1/mailbox-messages "$MM_BODY" 200)
MM_ID=$(echo "$MM" | jq -r '.id')

api_get /api/v1/mailbox-messages 200 >/dev/null
api_get "/api/v1/mailbox-messages?profileId=$PROFILE_ID" 200 >/dev/null
api_get "/api/v1/mailbox-messages?applicationId=$APP_ID" 200 >/dev/null
api_get /api/v1/mailbox-messages/$MM_ID 200 >/dev/null

PATCHED=$(api_send PATCH /api/v1/mailbox-messages/$MM_ID '{"status":"READ"}' 200)
echo "$PATCHED" | jq -e '.status == "READ"' >/dev/null || fail "mailbox:patch:status"
```

**Verify**

- `POST` returns `200` with `status: UNREAD` echoed back.
- All three list filters and `GET /{id}` return `200`.
- `PATCH /{id}` flips status to `READ`.

### 10. Dogs — list + create + update

```bash
log "step 10: dogs"
DOG_BODY=$(jq -n '{
  name: "E2EBuddy",
  taxStampNumber: "E2E-TAX-001",
  bookingReference: "E2E-BOOK-001",
  race: "OTHER"
}')
DOG=$(api_send POST /api/v1/dogs "$DOG_BODY" 200)
DOG_ID=$(echo "$DOG" | jq -r '.id')

api_get /api/v1/dogs 200 >/dev/null

UPDATED_DOG=$(echo "$DOG" | jq '.name = "E2EBuddyRenamed"')
api_send PUT /api/v1/dogs/$DOG_ID "$UPDATED_DOG" 200 >/dev/null
```

**Verify**

- `POST /dogs` returns `200` with new `id` and supplied fields.
- `GET /dogs` includes the new dog.
- `PUT /dogs/{id}` returns the updated `name`.

### 11. DogApplications — POST + list filter

```bash
log "step 11: dog applications"
DA_BODY=$(jq -n --argjson aid "$APP_ID" --argjson did "$DOG_ID" '{
  application: { id: $aid },
  dog: { id: $did },
  justification: "OTHER"
}')
DA=$(api_send POST /api/v1/dogs-applications "$DA_BODY" 200)
DA_ID=$(echo "$DA" | jq -r '.id')

LIST=$(api_get "/api/v1/dogs-applications?dogId=$DOG_ID" 200)
echo "$LIST" | jq -e --argjson id "$DA_ID" 'map(.id) | index($id)' >/dev/null \
  || fail "dog-app:list-missing"
```

**Verify**

- `POST /dogs-applications` returns `200` with new `id`.
- `GET /dogs-applications?dogId=$DOG_ID` contains it.

### 12. Negative auth — `401` without token

```bash
log "step 12: 401 without token"
NO_AUTH=$(curl -sS -o /dev/null -w '%{http_code}' "$API/api/v1/me")
[ "$NO_AUTH" = "401" ] || fail "auth:no-token-expected-401-got-$NO_AUTH"
```

**Verify**

- An unauthenticated request to a protected endpoint returns `401 Unauthorized`.

### 13. Wrap-up

```bash
log "all endpoints exercised"
pass
```

**Verify**

- The script reaches `pass` (prints `PASS duration=Ns`) — every endpoint above
  returned the expected status and body shape.

## Scenarios intentionally **not** covered

- `POST /api/v1/profiles` (profile creation) — profiles are auto-provisioned via
  Keycloak login through `/api/v1/me`. Creating duplicates from this scenario
  would pollute test state.
- `DELETE /api/v1/profiles/{id}` (self-delete) — would invalidate the test user
  and require a re-seed; covered separately by tear-down/admin scenarios.
- `GET /api/v1/profiles/{id}/documents/{id}/download` body-byte assertion — only
  headers and status are checked, since the persisted file may be a stub PDF.

## Failure semantics

- The script exits non-zero on the first failed assertion and writes
  `FAILED_AT=<phase>:<step>` to stderr (matches the convention used by
  `user-fe-happy-path/run.sh`).
- A failed step aborts the chain — later steps that depend on its output
  (e.g. `APP_ID` for comments / mailbox / dog-application) will not run.

## Suggested run command

```bash
cd e2e/user-be-api
bash run.sh
```

(or, from the repo root, `bash e2e/run.sh` to run this scenario alongside the
others in parallel — see `e2e/run.sh`).

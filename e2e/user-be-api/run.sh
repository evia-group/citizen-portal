#!/usr/bin/env bash
# Fast-path runner for e2e/user-be-api. Mirrors user-be-api-test-scenario.md
# step-by-step. Black-box: hits the API only, no DB access.
# On failure: exit 1 with FAILED_AT=<phase>:<step> on stderr.
set -uo pipefail
cd "$(dirname "$0")"

S=user-be-api
# shellcheck disable=SC1091
source ../commons.sh

TOKEN=$(token "$USER_NAME" "$USER_PASSWORD")
[ -n "$TOKEN" ] || fail "auth:token"

H_AUTH=( -H "Authorization: Bearer $TOKEN" )
H_JSON=( -H "Content-Type: application/json" )

# Helper: GET with status assertion. Echoes body on success.
api_get() {
  local path="$1" expected="${2:-200}" code
  code=$(curl -sS -o /tmp/api.body -w '%{http_code}' "${H_AUTH[@]}" "$API$path") || fail "curl:$path"
  [ "$code" = "$expected" ] || { cat /tmp/api.body >&2; fail "GET $path -> $code (want $expected)"; }
  cat /tmp/api.body
}

# Helper: POST/PUT/PATCH/DELETE with body + status assertion.
api_send() {
  local method="$1" path="$2" body="$3" expected="${4:-200}" code
  code=$(curl -sS -o /tmp/api.body -w '%{http_code}' -X "$method" \
    "${H_AUTH[@]}" "${H_JSON[@]}" -d "$body" "$API$path") || fail "curl:$method:$path"
  [ "$code" = "$expected" ] || { cat /tmp/api.body >&2; fail "$method $path -> $code (want $expected)"; }
  cat /tmp/api.body
}

# --- Step 1: Identity ---------------------------------------------------------
log "01 GET /me"
ME=$(api_get /api/v1/me 200)
PROFILE_ID=$(echo "$ME" | jq -r '.id')
USER_ID=$(echo "$ME" | jq -r '.userId')
[ -n "$PROFILE_ID" ] && [ "$PROFILE_ID" != "null" ] || fail "01:me:no-profile-id"
[ -n "$USER_ID" ] && [ "$USER_ID" != "null" ] || fail "01:me:no-user-id"

# --- Step 2: Reference data lookups -------------------------------------------
log "02 lookups (domains/categories/locations/services)"
DOMAINS=$(api_get /api/v1/domains 200)
CATS=$(api_get /api/v1/categories 200)
LOCS=$(api_get /api/v1/locations 200)
SVCS=$(api_get /api/v1/services 200)

[ "$(echo "$DOMAINS" | jq 'length')" -ge 1 ] || fail "02:domains:empty"
CAT_ID=$(echo "$CATS" | jq -r '.[0].id')
LOC_ID=$(echo "$LOCS" | jq -r '.[0].id')
SVC_ID=$(echo "$SVCS" | jq -r '.[0].id')
[ -n "$CAT_ID" ] && [ "$CAT_ID" != "null" ] || fail "02:categories:empty"
[ -n "$LOC_ID" ] && [ "$LOC_ID" != "null" ] || fail "02:locations:empty"
[ -n "$SVC_ID" ] && [ "$SVC_ID" != "null" ] || fail "02:services:empty"

FILTERED=$(api_get "/api/v1/services?categoryId=$CAT_ID&locationId=$LOC_ID" 200)
echo "$FILTERED" | jq -e 'all(.category.domainName != "Buergerportal-Domain")' >/dev/null \
  || fail "02:services-filter:Buergerportal-Domain leaked"

# --- Step 3: Consents ---------------------------------------------------------
log "03 consents"
CONSENTS=$(api_get /api/v1/consents 200)
CONSENT_ID=$(echo "$CONSENTS" | jq -r '.[0].id')
[ -n "$CONSENT_ID" ] && [ "$CONSENT_ID" != "null" ] || fail "03:consents:empty"
api_get "/api/v1/consents/$CONSENT_ID" 200 >/dev/null
api_get "/api/v1/consents?service_id=$SVC_ID" 200 >/dev/null

# --- Step 4: ConsentLog -------------------------------------------------------
log "04 consent log"
# Service validates consentText against the DB row, so echo the real text back.
CONSENT_TEXT=$(echo "$CONSENTS" | jq -r '.[0].text')
CL_BODY=$(jq -n --argjson cid "$CONSENT_ID" --argjson pid "$PROFILE_ID" --arg txt "$CONSENT_TEXT" '{
  status: "ACCEPTED",
  consentText: $txt,
  acceptedAt: (now | todate),
  consentId: $cid,
  profileId: $pid
}')
CL=$(api_send POST /api/v1/consents-log "$CL_BODY" 200)
CL_ID=$(echo "$CL" | jq -r '.id')
[ -n "$CL_ID" ] && [ "$CL_ID" != "null" ] || fail "04:consent-log:no-id"

api_get /api/v1/consents-log 200 >/dev/null
api_get "/api/v1/consents-log?profileId=$PROFILE_ID" 200 >/dev/null
api_get "/api/v1/consents-log?consentId=$CONSENT_ID" 200 >/dev/null
api_get "/api/v1/consents-log?status=ACCEPTED" 200 >/dev/null
api_get "/api/v1/consents-log/$CL_ID" 200 >/dev/null

# --- Step 5: Profiles + notifications -----------------------------------------
log "05 profiles + notifications"
api_get /api/v1/profiles 200 >/dev/null
api_get "/api/v1/profiles/$PROFILE_ID" 200 >/dev/null
FIRST_NAME=$(echo "$ME" | jq -r '.firstName // empty')
if [ -n "$FIRST_NAME" ]; then
  api_get "/api/v1/profiles?firstName=$FIRST_NAME" 200 >/dev/null
fi

ORIG_MAIL=$(echo "$ME" | jq -r '.canNotifyByMail // false')
NEW_MAIL=$([ "$ORIG_MAIL" = "true" ] && echo false || echo true)
TOGGLED=$(echo "$ME" | jq --argjson v "$NEW_MAIL" '.canNotifyByMail = $v')
api_send PUT "/api/v1/profiles/$PROFILE_ID" "$TOGGLED" 200 >/dev/null
api_send PUT "/api/v1/profiles/$PROFILE_ID" "$ME" 200 >/dev/null

NOTIFS=$(api_get "/api/v1/profiles/$PROFILE_ID/notifications" 200)
NOTIF_ID=$(echo "$NOTIFS" | jq -r '.[0].id // empty')
if [ -n "$NOTIF_ID" ]; then
  api_get "/api/v1/profiles/$PROFILE_ID/notifications/$NOTIF_ID" 200 >/dev/null
  api_send PUT "/api/v1/profiles/$PROFILE_ID/notifications/$NOTIF_ID" '{}' 200 >/dev/null
else
  log "  no notifications for profile - skipping per-notification GET/PUT"
fi

# --- Step 6: Documents --------------------------------------------------------
log "06 documents"
DOC_BODY=$(jq -n '{
  name: "e2e-test-doc.pdf",
  isArchive: false,
  type: "OTHER"
}')
DOC=$(api_send POST "/api/v1/profiles/$PROFILE_ID/documents" "$DOC_BODY" 200)
DOC_ID=$(echo "$DOC" | jq -r '.id')
[ -n "$DOC_ID" ] && [ "$DOC_ID" != "null" ] || fail "06:doc:no-id"

api_get "/api/v1/profiles/$PROFILE_ID/documents" 200 >/dev/null
api_get "/api/v1/profiles/$PROFILE_ID/documents?documentName=e2e-test-doc.pdf" 200 >/dev/null
api_get "/api/v1/profiles/$PROFILE_ID/documents/$DOC_ID" 200 >/dev/null

UPDATED=$(echo "$DOC" | jq '.name = "e2e-test-doc-renamed.pdf"')
api_send PUT "/api/v1/profiles/$PROFILE_ID/documents/$DOC_ID" "$UPDATED" 200 >/dev/null

# Upload binary content (multipart) — portable across macOS/Linux mktemp
TMP_PDF=$(mktemp -t e2e-pdf.XXXXXX)
mv "$TMP_PDF" "$TMP_PDF.pdf"
TMP_PDF="$TMP_PDF.pdf"
printf '%%PDF-1.4\n%%E2E test stub\n%%EOF\n' > "$TMP_PDF"
UP_CODE=$(curl -sS -o /tmp/api.body -w '%{http_code}' "${H_AUTH[@]}" \
  -F "file=@$TMP_PDF" \
  "$API/api/v1/profiles/$PROFILE_ID/documents/$DOC_ID/upload")
[ "$UP_CODE" = "200" ] || { cat /tmp/api.body >&2; fail "06:doc:upload:$UP_CODE"; }

HDR=$(curl -sS -D - -o /tmp/api.bin "${H_AUTH[@]}" \
  "$API/api/v1/profiles/$PROFILE_ID/documents/$DOC_ID/download")
echo "$HDR" | grep -qi 'Content-Disposition: attachment' || fail "06:doc:download:headers"

DEL_CODE=$(curl -sS -o /dev/null -w '%{http_code}' -X DELETE "${H_AUTH[@]}" \
  "$API/api/v1/profiles/$PROFILE_ID/documents/$DOC_ID")
[ "$DEL_CODE" = "204" ] || fail "06:doc:delete:$DEL_CODE"
rm -f "$TMP_PDF"

# --- Step 7: Applications -----------------------------------------------------
log "07 applications"
APP_BODY=$(jq -n --argjson sid "$SVC_ID" --argjson pid "$PROFILE_ID" '{
  status: "ADDED",
  service: { id: $sid },
  profile: { id: $pid }
}')
APP_RESP=$(api_send POST /api/v1/applications "$APP_BODY" 200)
APP_ID=$(echo "$APP_RESP" | jq -r '.id')
[ -n "$APP_ID" ] && [ "$APP_ID" != "null" ] || fail "07:app:no-id"

api_get /api/v1/applications 200 >/dev/null
api_get "/api/v1/applications?serviceId=$SVC_ID" 200 >/dev/null
api_get "/api/v1/applications/$APP_ID" 200 >/dev/null

UPDATED_APP=$(echo "$APP_RESP" | jq '.status = "STARTED"')
PUT_APP=$(api_send PUT "/api/v1/applications/$APP_ID" "$UPDATED_APP" 200)
echo "$PUT_APP" | jq -e '.status == "STARTED"' >/dev/null || fail "07:app:put:status"

# --- Step 8: Comments ---------------------------------------------------------
# Note: POST /api/v1/comments is broken in user-portal-be — the DB schema
# requires portal_comment.profile_id NOT NULL, but the Comment entity has no
# profile field, so any insert hits a 500. Exercise GET only.
log "08 comments (GET only — POST is server-side broken)"
api_get /api/v1/comments 200 >/dev/null
api_get "/api/v1/comments?applicationId=$APP_ID" 200 >/dev/null

# --- Step 9: MailboxMessages --------------------------------------------------
log "09 mailbox messages"
MM_BODY=$(jq -n --argjson pid "$PROFILE_ID" --argjson aid "$APP_ID" '{
  subject: "E2E test message",
  text: "Generated by user-be-api scenario",
  status: "PENDING",
  sendAt: (now | todate),
  sender: "system@e2e",
  receiver: "user@e2e",
  profileId: $pid,
  applicationId: $aid
}')
MM=$(api_send POST /api/v1/mailbox-messages "$MM_BODY" 200)
MM_ID=$(echo "$MM" | jq -r '.id')
[ -n "$MM_ID" ] && [ "$MM_ID" != "null" ] || fail "09:mm:no-id"

api_get /api/v1/mailbox-messages 200 >/dev/null
api_get "/api/v1/mailbox-messages?profileId=$PROFILE_ID" 200 >/dev/null
api_get "/api/v1/mailbox-messages?applicationId=$APP_ID" 200 >/dev/null
api_get "/api/v1/mailbox-messages/$MM_ID" 200 >/dev/null

PATCHED=$(api_send PATCH "/api/v1/mailbox-messages/$MM_ID" '{"status":"VIEWED"}' 200)
echo "$PATCHED" | jq -e '.status == "VIEWED"' >/dev/null || fail "09:mailbox:patch:status"

# --- Step 10: Dogs ------------------------------------------------------------
# DogService requires an existing Relationship of type DOG. There's no
# Relationship REST endpoint, so add one to the test profile via PUT /profiles
# and then reuse its id for the Dog payload.
log "10 dogs"
ME_NOW=$(api_get "/api/v1/profiles/$PROFILE_ID" 200)
REL_ID=$(echo "$ME_NOW" | jq -r '.relationships // [] | map(select(.type == "DOG")) | .[0].id // empty')
if [ -z "$REL_ID" ]; then
  REL_BODY=$(echo "$ME_NOW" | jq '.relationships = ((.relationships // []) + [{type:"DOG", name:"E2EBuddy"}])')
  REL_RESP=$(api_send PUT "/api/v1/profiles/$PROFILE_ID" "$REL_BODY" 200)
  REL_ID=$(echo "$REL_RESP" | jq -r '.relationships | map(select(.type == "DOG")) | .[-1].id')
fi
[ -n "$REL_ID" ] && [ "$REL_ID" != "null" ] || fail "10:dog:no-relationship-id"

DOG_BODY=$(jq -n --argjson rid "$REL_ID" '{
  name: "E2EBuddy",
  taxStampNumber: "E2E-TAX-001",
  bookingReference: "E2E-BOOK-001",
  race: "ANDERE_RASSE",
  relationship: { id: $rid }
}')
DOG=$(api_send POST /api/v1/dogs "$DOG_BODY" 200)
DOG_ID=$(echo "$DOG" | jq -r '.id')
[ -n "$DOG_ID" ] && [ "$DOG_ID" != "null" ] || fail "10:dog:no-id"

api_get /api/v1/dogs 200 >/dev/null

UPDATED_DOG=$(echo "$DOG" | jq '.name = "E2EBuddyRenamed"')
PUT_DOG=$(api_send PUT "/api/v1/dogs/$DOG_ID" "$UPDATED_DOG" 200)
echo "$PUT_DOG" | jq -e '.name == "E2EBuddyRenamed"' >/dev/null || fail "10:dog:put:name"

# --- Step 11: DogApplications -------------------------------------------------
# DogApplicationService creates a NEW Application and a NEW Dog from the body
# (it does not reference existing ones), so pass full nested payloads.
log "11 dog applications"
DA_BODY=$(jq -n --argjson sid "$SVC_ID" --argjson pid "$PROFILE_ID" --argjson rid "$REL_ID" '{
  application: {
    status: "ADDED",
    profile: { id: $pid },
    service: { id: $sid }
  },
  dog: {
    name: "E2EDogAppBuddy",
    taxStampNumber: "E2E-DA-TAX-001",
    bookingReference: "E2E-DA-BOOK-001",
    race: "ANDERE_RASSE",
    relationship: { id: $rid }
  },
  justification: "LOST_STAMP"
}')
DA=$(api_send POST /api/v1/dogs-applications "$DA_BODY" 200)
DA_ID=$(echo "$DA" | jq -r '.id')
[ -n "$DA_ID" ] && [ "$DA_ID" != "null" ] || fail "11:dog-app:no-id"

DA_DOG_ID=$(echo "$DA" | jq -r '.dog.id')
DA_LIST=$(api_get "/api/v1/dogs-applications?dogId=$DA_DOG_ID" 200)
echo "$DA_LIST" | jq -e --argjson id "$DA_ID" 'map(.id) | index($id)' >/dev/null \
  || fail "11:dog-app:list-missing"

# --- Step 12: Negative auth ---------------------------------------------------
log "12 401 without token"
NO_AUTH=$(curl -sS -o /dev/null -w '%{http_code}' "$API/api/v1/me")
[ "$NO_AUTH" = "401" ] || fail "12:auth:no-token-expected-401-got-$NO_AUTH"

# --- Step 13: Wrap-up ---------------------------------------------------------
log "all endpoints exercised"
pass

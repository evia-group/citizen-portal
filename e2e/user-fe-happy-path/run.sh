#!/usr/bin/env bash
# Fast-path runner for e2e/user-fe-happy-path. Mirrors the markdown scenario
# step-by-step but re-derives refs from each fresh snapshot (refs are
# snapshot-local). On failure: exit 1 with FAILED_AT=<phase>:<step> on stderr.
set -uo pipefail
cd "$(dirname "$0")"

S=user-fe-happy-path
# shellcheck disable=SC1091
source ../commons.sh

# Like ref_for, but returns the LAST matching ref instead of the first.
# Used for step 12 where two `button "Abmelden"` exist (profile trigger +
# dialog confirm) and we need the one inside the dialog.
ref_for_last() {
  local role="$1" name="$2" snap line
  snap=$(latest_snap)
  [ -n "$snap" ] || { echo "no snapshot yet" >&2; return 1; }
  line=$(grep -F -- "$role \"" "$snap" | grep -F -- "$name" | tail -1 || true)
  [ -n "$line" ] || return 1
  printf '%s\n' "$line" | grep -oE 'ref=e[0-9]+' | head -1 | cut -d= -f2
}

# --- Step 1: Open the User Portal ---------------------------------------------
log "01 open portal"
pw close >/dev/null 2>&1 || true
pw open "$APP"                                                || fail "01:open"
pw resize 1600 900                                            || fail "01:resize"
pw snapshot                                                   || fail "01:snapshot"
pw screenshot --filename=screenshots/00-initial.png           || fail "01:screenshot"

# --- Step 2: Accept the Data-Processing Consent (conditional) -----------------
log "02 consent"
if ref_for checkbox "Ich stimme zu" >/dev/null 2>&1; then
  click_by checkbox "Ich stimme zu"                           || fail "02:check-consent"
  click_by button   "Speichern"                               || fail "02:click-speichern"
  pw snapshot                                                 || fail "02:snapshot"
  pw screenshot --filename=screenshots/01-consent-accepted.png || fail "02:screenshot"
else
  log "02 consent already accepted, skipping"
fi

# --- Step 3: Start the Keycloak OIDC Login ------------------------------------
log "03 click Anmelden, switch to Keycloak tab"
wait_for button "Anmelden" 15                                 || fail "03:anmelden-not-visible"
click_by button "Anmelden"                                    || fail "03:click-anmelden"
# Keycloak opens in a new tab. Give it a moment to appear, then switch.
for _ in $(seq 1 15); do
  pw tab-select 1 >/dev/null 2>&1 && break
  sleep 1
done
pw tab-select 1                                               || fail "03:tab-select-1"
pw snapshot                                                   || fail "03:snapshot-pre"
wait_for textbox "Username or email" 15                       || fail "03:keycloak-not-loaded"
pw screenshot --filename=screenshots/02-keycloak-login.png    || fail "03:screenshot"

# --- Step 4: Submit Test User Credentials -------------------------------------
log "04 submit credentials"
fill_by textbox "Username or email" "$USER_NAME"              || fail "04:fill-username"
fill_by textbox "Password"          "$USER_PASSWORD"          || fail "04:fill-password"
click_by button "Sign In"                                     || fail "04:click-signin"
# Keycloak tab closes itself; original portal tab navigates to /. Wait, then
# select tab 0 (it may have been auto-selected, but be explicit).
sleep 2
pw tab-select 0 >/dev/null 2>&1 || true
wait_for heading "Dokumenteneingang" 25                       || fail "04:dashboard-not-loaded"
pw snapshot                                                   || fail "04:snapshot"
pw screenshot --filename=screenshots/03-dashboard.png         || fail "04:screenshot"

# --- Step 5: Dokumente tab ----------------------------------------------------
log "05 navigate Dokumente"
click_by tab "Dokumente"                                      || fail "05:click-dokumente"
wait_for button "Dokument/e hochladen" 10                     || fail "05:dokumente-not-loaded"
pw snapshot                                                   || fail "05:snapshot"
pw screenshot --filename=screenshots/04-dokumente.png         || fail "05:screenshot"

# --- Step 6: Services tab -----------------------------------------------------
log "06 navigate Services"
click_by tab "Services"                                       || fail "06:click-services"
wait_for link "Engagement und Hobby" 10                       || fail "06:services-not-loaded"
pw snapshot                                                   || fail "06:snapshot"
pw screenshot --filename=screenshots/05-services.png          || fail "06:screenshot"

# --- Step 7: Mailbox tab ------------------------------------------------------
log "07 navigate Mailbox"
click_by tab "Mailbox"                                        || fail "07:click-mailbox"
wait_for button "Neue E-Mail" 10                              || fail "07:mailbox-not-loaded"
pw snapshot                                                   || fail "07:snapshot"
pw screenshot --filename=screenshots/06-mailbox.png           || fail "07:screenshot"

# --- Step 8: Return to Home ---------------------------------------------------
log "08 navigate Home"
click_by tab "Home"                                           || fail "08:click-home"
wait_for heading "Dokumenteneingang" 10                       || fail "08:home-not-restored"
pw snapshot                                                   || fail "08:snapshot"
pw screenshot --filename=screenshots/07-home-restored.png     || fail "08:screenshot"

# --- Step 9: Profile hub ------------------------------------------------------
log "09 open profile"
pw goto "$APP/profile/"                                       || fail "09:goto-profile"
pw snapshot                                                   || fail "09:snapshot-pre"
wait_for heading "Mein Profil" 10                             || fail "09:profile-not-loaded"
pw screenshot --filename=screenshots/08-profile.png           || fail "09:screenshot"

# --- Step 10: Meine Profildaten sub-page --------------------------------------
log "10 open Meine Profildaten"
click_by link "Meine Profildaten"                             || fail "10:click-profildaten"
wait_for heading "Persönliche Daten" 10                       || fail "10:profildaten-not-loaded"
pw snapshot                                                   || fail "10:snapshot"
pw screenshot --filename=screenshots/09-profile-data.png      || fail "10:screenshot"

# --- Step 11: Re-open profile hub and trigger logout dialog -------------------
log "11 open logout confirmation dialog"
pw goto "$APP/profile/"                                       || fail "11:goto-profile-2"
pw snapshot                                                   || fail "11:snapshot-pre"
wait_for button "Abmelden" 10                                 || fail "11:abmelden-trigger-missing"
click_by button "Abmelden"                                    || fail "11:click-abmelden-trigger"
sleep 1
pw snapshot                                                   || fail "11:snapshot-post"
pw screenshot --filename=screenshots/10-logout-dialog.png     || fail "11:screenshot"

# --- Step 12: Confirm logout (dialog button = LAST "Abmelden") ----------------
log "12 confirm logout"
dialog_ref=$(ref_for_last button "Abmelden")                  || fail "12:dialog-abmelden-not-found"
pw click "$dialog_ref"                                        || fail "12:click-dialog-abmelden"
wait_for button "Anmelden" 20                                 || fail "12:logout-not-completed"
pw snapshot                                                   || fail "12:snapshot"
pw screenshot --filename=screenshots/11-logged-out.png        || fail "12:screenshot"

# --- Step 13: Close -----------------------------------------------------------
log "13 close session"
pw close                                                      || fail "13:close"

pass

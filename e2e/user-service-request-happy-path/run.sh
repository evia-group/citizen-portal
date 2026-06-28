#!/usr/bin/env bash
# Fast-path runner for e2e/user-service-request-happy-path. Mirrors the markdown
# scenario step-by-step and re-derives refs from each fresh snapshot (refs are
# snapshot-local). Two playwright-cli sessions run sequentially in this script:
#   - $S_CITIZEN  on the user portal (Phase A & C)
#   - $S_SERVICE  on the service portal (Phase B)
# We swap by reassigning $S, which `pw` reads at call time. All snapshots land
# in the same .playwright-cli/ dir, so we always re-snapshot immediately before
# looking up refs to ensure latest_snap returns the active session's view.
# On failure: exit 1 with FAILED_AT=<phase>:<step> on stderr.
set -uo pipefail
cd "$(dirname "$0")"

S_CITIZEN=user-service-request-happy-path--citizen
S_SERVICE=user-service-request-happy-path--service
S="$S_CITIZEN"

# shellcheck disable=SC1091
source ../commons.sh

# Service portal URL, environment-aware (mirrors commons.sh's APP/KC/API handling):
#   - devcontainer (Mode A): the FE is served through the openresty gateway at
#     /service-portal-fe on the host's :8888, reached via the forwarded localhost:8888.
#     (localhost:3001 is not running in the container, and direct :8281 is not
#     forwarded and would break the localhost-pinned OIDC redirect URIs anyway.)
#   - host (Mode B): the service portal runs natively on :3001.
# Override via the SERVICE_APP env var.
if [ "$(e2e_env_kind)" = "devcontainer" ]; then
  SERVICE_APP="${SERVICE_APP:-http://localhost:8888/service-portal-fe}"
else
  SERVICE_APP="${SERVICE_APP:-http://localhost:3001}"
fi

# ============================================================================
# Phase A — Citizen creates the service request
# ============================================================================

# --- Step 1: Open the User Portal (citizen session) ---------------------------
log "A01 open user portal (citizen)"
pw close >/dev/null 2>&1 || true
pw open "$APP"                                                || fail "A01:open"
pw resize 1600 900                                            || fail "A01:resize"
pw snapshot                                                   || fail "A01:snapshot"
pw screenshot --filename=screenshots/00-initial.png           || fail "A01:screenshot"

# --- Step 2: Accept consent (conditional) -------------------------------------
log "A02 consent"
if ref_for checkbox "Ich stimme zu" >/dev/null 2>&1; then
  click_by checkbox "Ich stimme zu"                           || fail "A02:check-consent"
  click_by button   "Speichern"                               || fail "A02:click-speichern"
  pw snapshot                                                 || fail "A02:snapshot"
  pw screenshot --filename=screenshots/01-consent-accepted.png || fail "A02:screenshot"
else
  log "A02 consent already accepted, skipping"
fi

# --- Step 3: Citizen Keycloak login -------------------------------------------
log "A03 click Anmelden, login via Keycloak"
wait_for button "Anmelden" 15                                 || fail "A03:anmelden-not-visible"
click_by button "Anmelden"                                    || fail "A03:click-anmelden"
# Keycloak opens in a new tab; wait for it.
for _ in $(seq 1 15); do
  pw tab-select 1 >/dev/null 2>&1 && break
  sleep 1
done
pw tab-select 1                                               || fail "A03:tab-select-1"
pw snapshot                                                   || fail "A03:snapshot-pre"
wait_for textbox "Username or email" 15                       || fail "A03:keycloak-not-loaded"
fill_by textbox "Username or email" "$USER_NAME"              || fail "A03:fill-username"
fill_by textbox "Password"          "$USER_PASSWORD"          || fail "A03:fill-password"
click_by button "Sign In"                                     || fail "A03:click-signin"
sleep 2
pw tab-select 0 >/dev/null 2>&1 || true
wait_for heading "Dokumenteneingang" 25                       || fail "A03:dashboard-not-loaded"
pw snapshot                                                   || fail "A03:snapshot"
pw screenshot --filename=screenshots/02-dashboard.png         || fail "A03:screenshot"

# --- Step 4: Navigate to Services catalogue -----------------------------------
log "A04 navigate Services"
click_by tab "Services"                                       || fail "A04:click-services"
wait_for link "Engagement und Hobby" 10                       || fail "A04:catalogue-not-loaded"
pw snapshot                                                   || fail "A04:snapshot"
pw screenshot --filename=screenshots/03-services-catalogue.png || fail "A04:screenshot"

# --- Step 5: Drill into "Ersatzmarke beantragen" ------------------------------
log "A05 drill Engagement -> Tierhaltung -> Ersatzmarke"
click_by link "Engagement und Hobby"                          || fail "A05:click-engagement"
pw snapshot                                                   || fail "A05:snapshot-engagement"
wait_for link "Tierhaltung" 10                                || fail "A05:tierhaltung-not-loaded"
click_by link "Tierhaltung"                                   || fail "A05:click-tierhaltung"
pw snapshot                                                   || fail "A05:snapshot-tierhaltung"
wait_for link "Ersatzmarke beantragen" 10                     || fail "A05:ersatzmarke-not-loaded"
click_by link "Ersatzmarke beantragen"                        || fail "A05:click-ersatzmarke"
pw snapshot                                                   || fail "A05:snapshot-detail"
wait_for link "Service starten" 10                            || fail "A05:detail-not-loaded"
pw screenshot --filename=screenshots/04-service-detail.png    || fail "A05:screenshot"

# --- Step 6: Start the application form ---------------------------------------
log "A06 start application form"
click_by link "Service starten"                               || fail "A06:click-service-starten"
pw snapshot                                                   || fail "A06:snapshot"
wait_for button "Weiter" 10                                   || fail "A06:form-step1-not-loaded"
pw screenshot --filename=screenshots/05-form-step-1.png       || fail "A06:screenshot"

# --- Step 7: Fill the 4-step wizard and submit --------------------------------
log "A07 wizard step 1 -> 2 (advance with Weiter)"
click_by button "Weiter"                                      || fail "A07:click-weiter-step1"

log "A07 wizard step 2 — Angaben zum Hund"
pw snapshot                                                   || fail "A07:snapshot-step2"
# Two "Bitte auswählen" buttons exist initially (Hund + Hunderasse).
# After picking "Bull", the dog dropdown shows "Bull" and only the breed
# dropdown still says "Bitte auswählen".
click_by button "Bitte auswählen"                             || fail "A07:click-dog-dropdown"
pw snapshot                                                   || fail "A07:snapshot-dog-options"
click_by option "Bull"                                        || fail "A07:click-option-bull"
pw snapshot                                                   || fail "A07:snapshot-after-bull"
fill_by textbox "Nummer der Hundesteuermarke" "12345"         || fail "A07:fill-tag-number"
click_by button "Bitte auswählen"                             || fail "A07:click-breed-dropdown"
pw snapshot                                                   || fail "A07:snapshot-breed-options"
click_by option "Mischling"                                   || fail "A07:click-option-mischling"
pw snapshot                                                   || fail "A07:snapshot-after-mischling"
click_by radio "Marke verloren"                               || fail "A07:click-marke-verloren"
click_by button "Weiter"                                      || fail "A07:click-weiter-step2"

log "A07 wizard step 3 — Dokumente (advance)"
pw snapshot                                                   || fail "A07:snapshot-step3"
# Step 3 advance is a *link* "Weiter" per the scenario.
if ref_for link "Weiter" >/dev/null 2>&1; then
  click_by link "Weiter"                                      || fail "A07:click-weiter-step3-link"
else
  click_by button "Weiter"                                    || fail "A07:click-weiter-step3-button"
fi

log "A07 wizard step 4 — Kosten + submit"
pw snapshot                                                   || fail "A07:snapshot-step4"
wait_for radio "PayPal" 10                                    || fail "A07:paypal-not-visible"
click_by radio "PayPal"                                       || fail "A07:click-paypal"
click_by checkbox "Ich stimme zu"                             || fail "A07:click-kosten-consent"
click_by button "Bezahlen und absenden"                       || fail "A07:click-submit"
wait_for heading "Vielen Dank" 30                             || fail "A07:confirmation-not-shown"
pw snapshot                                                   || fail "A07:snapshot-confirmed"
pw screenshot --filename=screenshots/06-submission-confirmed.png || fail "A07:screenshot"

# --- Step 8: Confirm new request appears in "Ihre Anträge" --------------------
log "A08 back to dashboard"
click_by link "Weiter"                                        || fail "A08:click-weiter-back"
wait_for heading "Dokumenteneingang" 15                       || fail "A08:dashboard-not-restored"
pw snapshot                                                   || fail "A08:snapshot"
pw screenshot --filename=screenshots/07-home-with-new-request.png || fail "A08:screenshot"
# Soft-verify the freshly-submitted entry shows up with status "offen".
if ! grep -q 'offen' "$(latest_snap)"; then
  log "A08 WARN: 'offen' not found in latest snapshot — request may not have rendered"
fi

# ============================================================================
# Phase B — Service-portal user processes the request
# ============================================================================
log "B-- switch to service-portal session: $S_SERVICE"
S="$S_SERVICE"

# --- Step 9: Open the Service Portal ------------------------------------------
log "B09 open service portal at $SERVICE_APP"
pw close >/dev/null 2>&1 || true
pw open "$SERVICE_APP"                                        || fail "B09:open"
pw resize 1600 900                                            || fail "B09:resize"
pw snapshot                                                   || fail "B09:snapshot"
wait_for heading "Eingegangene Anträge" 15                    || fail "B09:dashboard-not-loaded"
pw screenshot --filename=screenshots/09-service-portal-dashboard.png || fail "B09:screenshot"

# --- Step 10: Locate the new request ------------------------------------------
log "B10 open Details on latest 'offen' row"
pw snapshot                                                   || fail "B10:snapshot-pre"
# The Details column hosts a link/button per row. There may be many; pick the
# last one — the newest "offen" row is appended at the bottom.
SNAP=$(latest_snap)
[ -n "$SNAP" ] || fail "B10:no-snapshot"
DETAILS_REF=$(grep -F -- 'link "Details"' "$SNAP" | tail -1 | grep -oE 'ref=e[0-9]+' | head -1 | cut -d= -f2)
if [ -z "$DETAILS_REF" ]; then
  DETAILS_REF=$(grep -F -- 'button "Details"' "$SNAP" | tail -1 | grep -oE 'ref=e[0-9]+' | head -1 | cut -d= -f2)
fi
[ -n "$DETAILS_REF" ] || fail "B10:no-details-row"
pw click "$DETAILS_REF"                                       || fail "B10:click-details"
pw snapshot                                                   || fail "B10:snapshot-detail"
wait_for button "Bearbeitung beginnen" 15                     || fail "B10:detail-not-loaded"
pw screenshot --filename=screenshots/10-request-detail.png    || fail "B10:screenshot"

# --- Step 11: Transition to "In Bearbeitung" ----------------------------------
log "B11 click 'Bearbeitung beginnen'"
click_by button "Bearbeitung beginnen"                        || fail "B11:click-transition"
wait_for button "Fertigstellen" 15                            || fail "B11:fertigstellen-not-shown"
pw snapshot                                                   || fail "B11:snapshot-detail-after"
pw screenshot --filename=screenshots/11-status-in-progress.png || fail "B11:screenshot-detail"
# Re-snapshot list view to confirm row status changed.
pw goto "$SERVICE_APP/"                                       || fail "B11:goto-list"
pw snapshot                                                   || fail "B11:snapshot-list"
if ! grep -q 'In Bearbeitung' "$(latest_snap)"; then
  log "B11 WARN: 'In Bearbeitung' not found in list snapshot"
fi

# --- Step 12: Close the service session ---------------------------------------
log "B12 close service session"
pw close                                                      || fail "B12:close"

# ============================================================================
# Phase C — Citizen verifies the new status
# ============================================================================
log "C-- switch back to citizen session: $S_CITIZEN"
S="$S_CITIZEN"

# --- Step 13: Re-authenticate and verify status -------------------------------
# `pw goto` clears Expo Router's in-memory auth state, so we go through
# consent (conditional) + Anmelden + Keycloak again.
log "C13 reload citizen, re-auth"
pw goto "$APP/"                                               || fail "C13:goto"
pw snapshot                                                   || fail "C13:snapshot-pre"

if ref_for checkbox "Ich stimme zu" >/dev/null 2>&1; then
  log "C13 consent re-shown, accepting"
  click_by checkbox "Ich stimme zu"                           || fail "C13:check-consent"
  click_by button   "Speichern"                               || fail "C13:click-speichern"
  pw snapshot                                                 || fail "C13:snapshot-post-consent"
fi

if ref_for button "Anmelden" >/dev/null 2>&1; then
  click_by button "Anmelden"                                  || fail "C13:click-anmelden"
  for _ in $(seq 1 15); do
    pw tab-select 1 >/dev/null 2>&1 && break
    sleep 1
  done
  if pw tab-select 1 >/dev/null 2>&1; then
    pw snapshot                                               || fail "C13:snapshot-kc"
    if wait_for textbox "Username or email" 15; then
      fill_by textbox "Username or email" "$USER_NAME"        || fail "C13:fill-username"
      fill_by textbox "Password"          "$USER_PASSWORD"    || fail "C13:fill-password"
      click_by button "Sign In"                               || fail "C13:click-signin"
      sleep 2
      pw tab-select 0 >/dev/null 2>&1 || true
    fi
  fi
fi

wait_for heading "Dokumenteneingang" 25                       || fail "C13:dashboard-not-restored"
pw snapshot                                                   || fail "C13:snapshot-dashboard"
pw screenshot --filename=screenshots/12-home-status-updated.png || fail "C13:screenshot"
# Soft-verify the citizen now sees "In Bearbeitung" on the dashboard.
if ! grep -q 'In Bearbeitung' "$(latest_snap)"; then
  log "C13 WARN: 'In Bearbeitung' not found on citizen dashboard"
fi

# --- Step 14: (Optional) duplicate the dashboard state for parity -------------
pw screenshot --filename=screenshots/13-request-detail-citizen.png || fail "C14:screenshot"

# --- Step 15: Close the citizen session ---------------------------------------
log "C15 close citizen session"
pw close                                                      || fail "C15:close"

pass

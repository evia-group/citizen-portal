# User Portal FE — Happy Path Test Scenario

> **Fast path:** `bash run.sh` executes this scenario end-to-end in ~90s (consent screen
> acceptance if needed, Keycloak OIDC login, dashboard tab navigation, and profile page
> verification — all browser-only). On failure, exits non-zero with
> `FAILED_AT=<phase>:<step>` on stderr; fall back to the AI team (see CLAUDE.md
> "E2E Testing Team Workflow") using this markdown as the playbook.

End-to-end test: verify a user can log into the citizen portal, see the dashboard, navigate
all main tabs, and open the profile page and logout

## Static Test Data

| Item               | Value                                                   |
|--------------------|---------------------------------------------------------|
| App URL            | `http://localhost:8081`                                 |
| Sign-in URL        | `http://localhost:8081/sign-in` (redirect target)       |
| Keycloak URL       | `http://localhost/keycloak` (via OpenResty proxy)       |
| Realm              | `portal`                                                |
| Test User          | `user` (from `e2e/.env` USER_NAME)                      |
| Test User Password | (from `e2e/.env` USER_PASSWORD, do not expose)          |
| Dashboard URL      | `/`                                                     |
| Profile URL        | `/profile/`                                             |

## Preconditions

- Local stack is running (see `local-dev` skill); user portal reachable at the App URL.
- A clean browser session (no persisted AsyncStorage), so the consent screen is shown on
  first load. If state is reused, step 2 may be skipped — handle gracefully.
- `e2e/.env` defines `USER_NAME` and `USER_PASSWORD`.

## Steps

### 1. Open the User Portal

```bash
playwright-cli close                           # ensure fresh session
playwright-cli open http://localhost:8081
playwright-cli resize 1600 900
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/00-initial.png
```

**Verify** — Baseline: `screenshots/baseline/00-initial.png`

- **Must match:** Consent screen with heading "Einwilligungserklärung zur Verarbeitung von
  personenbezogenen Daten", a "Push-Benachrichtigungen" subsection, a checkbox labelled
  "Ich stimme zu, dass meine persönlichen Daten für die Nutzung gemäß den
  Datenschutzrichtlinien verarbeitet werden dürfen.", and a "Speichern" button. The app
  redirects to `http://localhost:8081/sign-in`.

### 2. Accept the Data-Processing Consent

```bash
playwright-cli click e24                       # checkbox "Ich stimme zu, dass meine persönlichen Daten …"
playwright-cli click e28                       # button "Speichern"
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/01-consent-accepted.png
```

**Verify** — Baseline: `screenshots/baseline/01-consent-accepted.png`

- **Must match:** Sign-in landing page with the "bürgerportal" wordmark, heading
  "Willkommen!", and a single "Anmelden" button. The consent screen is gone. URL stays
  `http://localhost:8081/sign-in`.

> If the consent screen is not present on first snapshot (re-used state), skip this step
> and proceed to step 3.

### 3. Start the Keycloak OIDC Login

Clicking "Anmelden" opens the Keycloak login form **in a new tab**. The original tab
remains on `/sign-in` with a disabled "Anmelden" button and a progressbar; you must
switch to the new tab before filling credentials.

```bash
playwright-cli click e37                       # button "Anmelden"
playwright-cli tab-list                        # confirm tab 1 is the Keycloak page
playwright-cli tab-select 1                    # switch to the Keycloak tab
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/02-keycloak-login.png
```

**Verify** — Baseline: `screenshots/baseline/02-keycloak-login.png`

- **Must match:** Keycloak login page (tab title "Sign in to Buergerportal") with a
  "BUERGERPORTAL" wordmark, heading "Sign in to your account", a "Username or email"
  textbox, a "Password" textbox with a "Show password" toggle, a primary "Sign In"
  button, and a "New user? Register" link. URL contains
  `/keycloak/realms/portal/protocol/openid-connect/auth`.

### 4. Submit Test User Credentials

```bash
playwright-cli fill e15 "$USER_NAME"           # textbox "Username or email"
playwright-cli fill e19 "$USER_PASSWORD"       # textbox "Password"
playwright-cli click e23                       # button "Sign In"
# Keycloak completes the OIDC redirect; the Keycloak tab closes itself and the
# original portal tab navigates to "/" with the authenticated dashboard.
playwright-cli tab-list                        # there should now be only tab 0 (the portal)
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/03-dashboard.png
```

**Verify** — Baseline: `screenshots/baseline/02-dashboard.png` (existing baseline for the
authenticated dashboard state — also captured here as `03-dashboard.png`)

- **Must match:** Authenticated dashboard with greeting "Willkommen, Max Mustermann!",
  a "Dokumenteneingang" card showing "Grundsteuerbescheid 2024" (with a "neu" badge and
  an "Öffnen" button), an "Ihre Anträge" section ("Hund anmelden — Status: Rückfrage
  vorhanden"), and the bottom tab bar with tabs `Home`, `Dokumente`, `Services`,
  `Mailbox`. The header shows "Home" and a profile-icon link to `/profile/`. URL is
  `http://localhost:8081/`.

### 5. Navigate to the Dokumente Tab

The bottom navigation items are rendered as `link` (inside a `tablist`), not `tab`.
Match by role `link` + accessible name when looking up refs.

```bash
playwright-cli click e149                      # link "Dokumente" (tablist)
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/04-dokumente.png
```

**Verify** — Baseline: `screenshots/baseline/04-dokumente.png`

- **Must match:** Dokumente screen with the "Dokumente" header, sub-links "Meine
  Dokumente" / "Archivierte Dokumente", a "Dokument/e hochladen" button, a "Dokument
  suchen" search box, and the documents list (`Document_1`, `Document_2`, `Document_3`).
  "Dokumente" tab marked active in the bottom bar. URL is
  `http://localhost:8081/dms` (note: `/dms`, not `/dokumente`).

### 6. Navigate to the Services Tab

```bash
playwright-cli click e161                      # link "Services" (tablist)
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/05-services.png
```

**Verify** — Baseline: `screenshots/baseline/05-services.png`

- **Must match:** Services screen rendered with a catalogue grid of category links —
  "Engagement und Hobby", "Familie und Kind", "Gesundheit", "Bildung", "Arbeit", "Recht
  und Ordnung", and further categories below. "Services" tab active in the bottom bar.
  URL is `http://localhost:8081/services`.

### 7. Navigate to the Mailbox Tab

```bash
playwright-cli click e191                      # link "Mailbox" (tablist)
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/06-mailbox.png
```

**Verify** — Baseline: `screenshots/baseline/06-mailbox.png`

- **Must match:** Mailbox screen with a "Heute" date filter pill and a "Neue E-Mail"
  composition button. "Mailbox" tab active in the bottom bar, "Mailbox" header at top.
  URL is `http://localhost:8081/mailbox`.

### 8. Return to the Home Tab

```bash
playwright-cli click e137                      # link "Home" (tablist)
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/07-home-restored.png
```

**Verify** — Baseline: `screenshots/baseline/07-home-restored.png`

- **Must match:** Same dashboard as step 4 — greeting "Willkommen, Max Mustermann!",
  Dokumenteneingang card, Ihre Anträge section. "Home" tab active. URL is
  `http://localhost:8081/`.

### 9. Open the Profile Page

The header profile icon is a `link` to `/profile/` but has no accessible name (the
`CircleUser` icon has no `aria-label`), so navigate by URL.

```bash
playwright-cli goto http://localhost:8081/profile/
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/08-profile.png
```

**Verify** — Baseline: `screenshots/baseline/08-profile.png`

- **Must match:** Profile page with heading "Mein Profil" and five sub-section links
  rendered as buttons:
  - `Meine Profildaten` → `/profile/data`
  - `Meine Kommune` → `/profile/municipality`
  - `Zahlungs- & Steuerinformationen` → `/profile/payments`
  - `Meine Benachrichtigungen (0)` → `/profile/my-notifications`
  - `Benachrichtungseinstellungen` → `/profile/notifications`

  Below the list, a primary "Abmelden" button. Header shows "Mein Profil". URL is
  `http://localhost:8081/profile/`.

### 10. Open the "Meine Profildaten" Sub-Page

```bash
playwright-cli click e25                       # link "Meine Profildaten"
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/09-profile-data.png
```

**Verify** — Baseline: `screenshots/baseline/09-profile-data.png`

- **Must match:** Sub-page heading "Meine Profildaten", section "Persönliche Daten"
  with fields like "Anrede", "Doktorgrad", "Vorname/n *" (prefilled `John`), "Nachname *"
  (prefilled `Doe`), "Geburtsname (falls abweichend)", "Geburtsdatum *", "Geburtsort *",
  and a following "Adresse" section. URL is `http://localhost:8081/profile/data`.

### 11. Return to Profile Hub and Open the Logout Confirmation Dialog

```bash
playwright-cli goto http://localhost:8081/profile/
playwright-cli snapshot
playwright-cli click e31                       # button "Abmelden" (profile hub trigger)
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/10-logout-dialog.png
```

**Verify** — Baseline: `screenshots/baseline/10-logout-dialog.png`

- **Must match:** Modal dialog overlaying the profile page with the prompt text "Sind
  Sie sich sicher, dass Sie sich abmelden möchten?", a teal "Abbrechen" (cancel) button,
  a red "Abmelden" (confirm) button, and a small `×` close icon in the top-right corner.
  Background is dimmed.

### 12. Confirm Logout

After step 11 the snapshot contains **two** "Abmelden" buttons — the profile-hub
trigger (now `[expanded]`, name omitted in the dialog state) and the dialog's confirm
button. Pick the one inside the `dialog [ref=…]` block (typically the later ref in
document order).

```bash
playwright-cli click e125                      # button "Abmelden" inside the dialog
playwright-cli snapshot
playwright-cli screenshot --filename=screenshots/11-logged-out.png
```

**Verify** — Baseline: `screenshots/baseline/11-logged-out.png`

- **Must match:** Sign-in landing page returned — "bürgerportal" wordmark, heading
  "Willkommen!", and a single "Anmelden" button. No tab bar, no profile trigger, no
  authenticated chrome. URL is `http://localhost:8081/sign-in` (post-logout redirect).

### 13. Close the Browser Session

```bash
playwright-cli close
```

**Verify** — No browser session remains; `playwright-cli list` shows no active session
for this scenario.

## Known Issues & Tips

1. **Refs are snapshot-local.** The `eN` refs above are real values observed during an
   exploratory pass on 2026-05-08 against a fresh session, but they shift whenever the
   DOM changes. Re-derive each ref from the latest `playwright-cli snapshot` output by
   matching role + accessible name (see comments next to each click).
2. **Bottom navigation uses `link` role, not `tab`.** Items live inside a `tablist`
   container but each item itself is a `link` (with `[active]` attribute when selected).
   Match `link "Home"`, `link "Dokumente"`, etc.
3. **Dokumente URL is `/dms`, not `/dokumente`.** Easy gotcha — the tab label and route
   diverge.
4. **Keycloak login opens in a new tab.** Step 3 must `tab-select 1` after clicking
   "Anmelden". After successful login (step 4), Keycloak finishes the OIDC redirect and
   closes its tab automatically; the original portal tab is then on `/` with the
   dashboard.
5. **Keycloak field labels** use the default theme: "Username or email" and "Password",
   submit "Sign In". The visible heading is "Sign in to your account" (the tab title
   "Sign in to Buergerportal" is realm-customised). If the realm theme changes, inspect
   `screenshots/02-keycloak-login.png` and adjust.
6. **Header profile icon has no accessible name.** It is a `link` with `/url: /profile/`
   wrapping a `CircleUser` icon. Step 9 navigates directly to `/profile/` instead of
   clicking the icon to avoid ref-by-empty-name ambiguity.
7. **Two "Abmelden" buttons after the dialog opens.** The profile trigger (ref e31 on
   first snapshot, attribute `[expanded]` after open) and the dialog's confirm button
   (e125 in observed run) share the label. Always re-snapshot and prefer the button
   inside the `dialog [ref=…]` block.
8. **Consent screen is conditional.** First visit shows it; persisted AsyncStorage
   skips it. Step 2 must tolerate both branches.
9. **Logout returns to `/sign-in`, not `/`.** The post-logout URL on the portal tab is
   `http://localhost:8081/sign-in`, matching the initial pre-auth redirect.

# User → Service Portal Request — Happy Path Test Scenario

End-to-end test: a citizen submits an "Ersatzmarke beantragen" (replace dog tag)
service request from the user portal, a service-portal user picks it up and
transitions its status from "offen" to "In Bearbeitung" via a single
"Bearbeitung beginnen" button, and the citizen sees the updated status reflected
back on the user-portal dashboard.

This scenario crosses two frontends (user-portal-fe @ 8081 and service-portal-fe
@ 3001 in Mode B; @ 8281 only in Mode A / Docker). There is no `run.sh` fast
path — work the markdown step-by-step; refs (`eN`) are exploratory placeholders
and must be re-derived from each fresh `playwright-cli snapshot` (match by role
+ accessible name).

## Static Test Data

| Item                        | Value                                                                  |
|-----------------------------|------------------------------------------------------------------------|
| User Portal URL             | `http://localhost:8081`                                                |
| User Portal Sign-in URL     | `http://localhost:8081/sign-in` (redirect target)                      |
| Service Portal URL (Mode B) | `http://localhost:3001` (native `npm run dev:service-portal`)          |
| Service Portal URL (Mode A) | `http://localhost:8281` (full Docker; container maps 8281→3001)        |
| Keycloak URL                | `http://localhost:8888/keycloak` (via OpenResty proxy)                 |
| Citizen Realm               | `portal` (the only realm — service portal shares it)                   |
| Citizen User                | `user` (from `e2e/.env` `USER_NAME`)                                   |
| Citizen Password            | `user` (from `e2e/.env` `USER_PASSWORD`)                               |
| Service-portal auth         | **None in dev** — the service portal is open, no Keycloak login        |
| Service catalogue path      | Services → Engagement und Hobby → Tierhaltung → Ersatzmarke beantragen |
| Service slug under test     | `services/engagement/animals/replace-dog-tag`                          |
| Service visible label       | "Ersatzmarke beantragen"                                               |
| Initial status              | "offen"                                                                |
| Expected target status      | "In Bearbeitung"                                                       |
| Service portal session name | `user-service-request-happy-path--service`                             |
| User portal session name    | `user-service-request-happy-path--citizen`                             |

## Preconditions

- Local stack is running (see `local-dev` skill).
  - User portal FE @ 8081 (`npm run dev:user-portal`).
  - Service portal FE @ 3001 (`npm run dev:service-portal`); **start it
    explicitly if missing**, the `local-dev` default `npm run dev:local` does
    not boot it.
  - OpenResty + Keycloak + Postgres in Docker (`docker compose up -d`).
- Two clean browser sessions — one per identity — so the consent screen on the
  user portal is shown on first load. If state is reused, the consent step may
  be skipped.
- `e2e/.env` defines `USER_NAME` and `USER_PASSWORD`. **No service-user
  credentials are required**: the dev service portal does not enforce auth.
- The service "Ersatzmarke beantragen" is visible at
  `/services/engagement/animals/replace-dog-tag`.
- Multiple "Ersatzmarke beantragen" requests can co-exist for the same citizen;
  the new submission will be the **last row** (highest internal ID) in the
  service-portal table. The numeric request ID is now surfaced directly in
  the new **"ID"** column of the service-portal table — read it from there
  (it must match the `/<id>` segment of the row's Details link).

## Steps

> **Convention.** Two separate playwright-cli sessions run in parallel: one for
> the citizen on the user portal (`-s=user-service-request-happy-path--citizen`)
> and one for the service-portal user
> (`-s=user-service-request-happy-path--service`). The commands below show the
> bare `playwright-cli` calls; pass `-s=<session>` per the phase. Phase A & C
> use the citizen session; Phase B uses the service session.
>
> **Caveat — `goto` re-resets state.** `playwright-cli goto` reloads the page
> and clears Expo Router's in-memory auth state, which forces the citizen
> through consent + Keycloak login again. Phase C re-authenticates rather than
> relying on a kept-alive tab.

### Phase A — Citizen creates the service request

#### 1. Open the User Portal (citizen session)

```bash
playwright-cli -s=user-service-request-happy-path--citizen close
playwright-cli -s=user-service-request-happy-path--citizen open http://localhost:8081
playwright-cli -s=user-service-request-happy-path--citizen resize 1600 900
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen screenshot --filename=screenshots/00-initial.png
```

**Verify** — Baseline: `screenshots/00-initial.png`

- **Must match:** Consent screen with heading "Einwilligungserklärung zur
  Verarbeitung von personenbezogenen Daten", checkbox "Ich stimme zu, …" and a
  "Speichern" button. The app redirects to `http://localhost:8081/sign-in`
  after acceptance.

#### 2. Accept the Data-Processing Consent

```bash
playwright-cli -s=user-service-request-happy-path--citizen click eN   # checkbox "Ich stimme zu, …"
playwright-cli -s=user-service-request-happy-path--citizen click eN   # button "Speichern"
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen screenshot --filename=screenshots/01-consent-accepted.png
```

**Verify** — Baseline: `screenshots/01-consent-accepted.png`

- **Must match:** Sign-in landing page with "bürgerportal" wordmark, heading
  "Willkommen!", and a single "Anmelden" button. URL stays
  `http://localhost:8081/sign-in`.

> If the consent screen is not present (re-used state), skip this step.

#### 3. Citizen Keycloak Login

Clicking "Anmelden" opens Keycloak in a new tab. Switch tabs before filling
credentials.

```bash
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Anmelden"
playwright-cli -s=user-service-request-happy-path--citizen tab-list
playwright-cli -s=user-service-request-happy-path--citizen tab-select 1
playwright-cli -s=user-service-request-happy-path--citizen fill eN "$USER_NAME"
playwright-cli -s=user-service-request-happy-path--citizen fill eN "$USER_PASSWORD"
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Sign In"
playwright-cli -s=user-service-request-happy-path--citizen tab-list          # only tab 0 should remain
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen screenshot --filename=screenshots/02-dashboard.png
```

**Verify** — Baseline: `screenshots/02-dashboard.png`

- **Must match:** Authenticated dashboard with greeting "Willkommen, Max
  Mustermann!", "Dokumenteneingang" and "Ihre Anträge" sections, bottom tab bar
  `Home / Dokumente / Services / Mailbox`. URL is `http://localhost:8081/`.

#### 4. Navigate to the Services Catalogue

```bash
playwright-cli -s=user-service-request-happy-path--citizen click eN          # link "Services" (tablist)
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen screenshot --filename=screenshots/03-services-catalogue.png
```

**Verify** — Baseline: `screenshots/03-services-catalogue.png`

- **Must match:** Services catalogue grid with category links — including
  "Engagement und Hobby" (the category that contains the animal services).
  "Services" tab is active in the bottom bar. URL is
  `http://localhost:8081/services`.

#### 5. Drill into the "Ersatzmarke beantragen" Service

The exact route is `services/engagement/animals/replace-dog-tag`. Drill in by
clicking "Engagement und Hobby" → "Tierhaltung" → "Ersatzmarke beantragen".
Re-derive the exact accessible names from each fresh snapshot.

```bash
playwright-cli -s=user-service-request-happy-path--citizen click eN          # link "Engagement und Hobby"
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen click eN          # link "Tierhaltung"
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen click eN          # link "Ersatzmarke beantragen"
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen screenshot --filename=screenshots/04-service-detail.png
```

**Verify** — Baseline: `screenshots/04-service-detail.png`

- **Must match:** Service detail page describing the dog-tag replacement
  procedure (Voraussetzungen, Verfahrensablauf, Fristen, Erforderliche
  Unterlagen, Kosten "5 EUR", Hinweise, Rechtsgrundlage), with a
  "Service starten" link at the bottom. URL ends in
  `/services/engagement/animals/replace-dog-tag`.

#### 6. Start the Application Form

```bash
playwright-cli -s=user-service-request-happy-path--citizen click eN          # link "Service starten"
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen screenshot --filename=screenshots/05-form-step-1.png
```

**Verify** — Baseline: `screenshots/05-form-step-1.png`

- **Must match:** Wizard step 1 of 4, "Persönliche Angaben". Pre-filled fields:
  Vorname/n "John", Nachname "Doe", Straße & Hausnr. "Street", PLZ "12345",
  Ort "City", Land "Deutschland" (disabled). Empty optional fields:
  "Telefonnummer", "Buchungszeichen / Kennziffer Steuerbescheid". Buttons
  "Abbrechen" and "Weiter".

#### 7. Fill the Form and Submit

The form is a 4-step wizard. Re-snapshot before each step; the dropdown options
("Bull" for the dog, "Mischling" for breed) are re-derivable from each fresh
snapshot.

```bash
# --- Step 1: Persönliche Angaben — accept defaults, advance ---
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Weiter"

# --- Step 2: Angaben zum Hund ---
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Bitte auswählen" (dog dropdown)
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen click eN          # option "Bull" (auto-fills dog name)
playwright-cli -s=user-service-request-happy-path--citizen fill eN "12345"   # textbox "Nummer der Hundesteuermarke *"
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Bitte auswählen" (Hunderasse)
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen click eN          # option "Mischling"
playwright-cli -s=user-service-request-happy-path--citizen click eN          # radio "Marke verloren"
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Weiter"

# --- Step 3: Dokumente — none required, advance ---
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen click eN          # link "Weiter"

# --- Step 4: Kosten ---
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen click eN          # radio "PayPal"
playwright-cli -s=user-service-request-happy-path--citizen click eN          # checkbox "Ich stimme zu, …"
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Bezahlen und absenden"
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen screenshot --filename=screenshots/06-submission-confirmed.png
```

**Verify** — Baseline: `screenshots/06-submission-confirmed.png`

- **Must match:** Confirmation screen with heading "Vielen Dank für Ihren
  Antrag!" and a "Weiter" link back to `/`.
- **Note:** No Antragsnummer / reference ID is shown to the citizen on this
  screen. The numeric `REQUEST_ID` is only discoverable from the service-portal
  Details URL (`/<id>`) in Phase B.

#### 8. Confirm the Request Appears in "Ihre Anträge"

```bash
playwright-cli -s=user-service-request-happy-path--citizen click eN          # link "Weiter" (back to dashboard)
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen screenshot --filename=screenshots/07-home-with-new-request.png
```

**Verify** — Baseline: `screenshots/07-home-with-new-request.png`

- **Must match:** Dashboard "Ihre Anträge" section now lists an additional
  "Ersatzmarke beantragen" entry with **Status: offen** alongside any
  pre-existing entries. (Repeated runs accumulate — the *new* row is the
  freshly added "offen" entry.)

### Phase B — Service-portal user processes the request

> Use a **second playwright-cli session**
> (`-s=user-service-request-happy-path--service`). The dev service portal does
> not require login.

#### 9. Open the Service Portal

```bash
playwright-cli -s=user-service-request-happy-path--service close
playwright-cli -s=user-service-request-happy-path--service open http://localhost:3001
playwright-cli -s=user-service-request-happy-path--service resize 1600 900
playwright-cli -s=user-service-request-happy-path--service snapshot
playwright-cli -s=user-service-request-happy-path--service screenshot --filename=screenshots/09-service-portal-dashboard.png
```

**Verify** — Baseline: `screenshots/09-service-portal-dashboard.png`

- **Must match:** Heading "Eingegangene Anträge" and a table "Service Tabelle"
  with columns **ID** / Dienstbezeichnung / Eingangsdatum / Vorname/n /
  Nachname / Geburtsdatum / Status / Details. No login screen — direct access.
- **New "ID" column check:** The table header row contains an "ID" column
  header, and each data row's first cell is a numeric request ID that matches
  the `/<id>` segment of that row's Details link `href`.

> If a Mode A run is in use, substitute `http://localhost:8281` for the open URL.

#### 10. Locate the New Request

The newest "Ersatzmarke beantragen" submission appears as the last row, with
today's Eingangsdatum and **Status: offen**. Read `REQUEST_ID` directly from
the new **"ID"** column on that row, and cross-check that it matches the
`/<REQUEST_ID>` segment of the Details link's `href` on the same row.

```bash
playwright-cli -s=user-service-request-happy-path--service snapshot
playwright-cli -s=user-service-request-happy-path--service click eN          # button/link "Details" on the latest "offen" row
playwright-cli -s=user-service-request-happy-path--service snapshot
playwright-cli -s=user-service-request-happy-path--service screenshot --filename=screenshots/10-request-detail.png
```

**Verify** — Baseline: `screenshots/10-request-detail.png`

- **Must match:** Request detail layout with left-side tabs "Persönliche
  Angaben / Angaben zum Hund / Dokumente / Kosten / Nachrichten" and the
  "Persönliche Angaben" panel showing Vorname "John", Nachname "Doe",
  PLZ "12345", Ort "City", Straße "Street", Hausnummer "1". Bottom action row
  has a "Zurück" button and a primary **"Bearbeitung beginnen"** button.

#### 11. Transition the Request to "In Bearbeitung"

The transition is a single click on "Bearbeitung beginnen" — there is no
separate dropdown, dialog, or confirmation. After the click, the action button
flips to **"Fertigstellen"**, signalling the request is now in processing.

```bash
playwright-cli -s=user-service-request-happy-path--service click eN          # button "Bearbeitung beginnen"
playwright-cli -s=user-service-request-happy-path--service snapshot
playwright-cli -s=user-service-request-happy-path--service screenshot --filename=screenshots/11-status-in-progress.png

# Verify the list reflects the new status:
playwright-cli -s=user-service-request-happy-path--service goto http://localhost:3001/
playwright-cli -s=user-service-request-happy-path--service snapshot
```

**Verify** — Baseline: `screenshots/11-status-in-progress.png`

- **Must match:** Detail view's primary button text has changed from
  "Bearbeitung beginnen" to **"Fertigstellen"**.
- **List view re-snapshot:** The matching row now shows
  Status **"In Bearbeitung"** (replacing "offen").

#### 12. Close the Service Session

```bash
playwright-cli -s=user-service-request-happy-path--service close
```

### Phase C — Citizen verifies the new status

> Switch back to the citizen session
> (`-s=user-service-request-happy-path--citizen`).
> **`playwright-cli goto` clears the in-memory auth state**, so this phase
> re-runs the consent + Keycloak login. Do not assume the dashboard renders
> directly.

#### 13. Re-authenticate and Land on the Dashboard

```bash
playwright-cli -s=user-service-request-happy-path--citizen goto http://localhost:8081/
playwright-cli -s=user-service-request-happy-path--citizen snapshot
# If consent is shown again, accept it:
playwright-cli -s=user-service-request-happy-path--citizen click eN          # checkbox "Ich stimme zu, …"
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Speichern"
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Anmelden"
playwright-cli -s=user-service-request-happy-path--citizen tab-select 1
playwright-cli -s=user-service-request-happy-path--citizen fill eN "$USER_NAME"
playwright-cli -s=user-service-request-happy-path--citizen fill eN "$USER_PASSWORD"
playwright-cli -s=user-service-request-happy-path--citizen click eN          # button "Sign In"
playwright-cli -s=user-service-request-happy-path--citizen snapshot
playwright-cli -s=user-service-request-happy-path--citizen screenshot --filename=screenshots/12-home-status-updated.png
```

**Verify** — Baseline: `screenshots/12-home-status-updated.png`

- **Must match:** "Ihre Anträge" section on the dashboard shows the most
  recently submitted "Ersatzmarke beantragen" entry with
  Status **"In Bearbeitung"** (replacing the "offen" seen in step 8). Greeting
  and other dashboard chrome unchanged.

#### 14. (Optional) Open the Request Detail in the User Portal

The status pill on the user-portal dashboard is rendered as a `generic` element
with `[cursor=pointer]` but does **not** navigate anywhere when clicked — there
is no citizen-side detail screen surfaced from the dashboard for this service
in the current build. Skip this step; the Phase C verification is satisfied by
the dashboard status text alone.

> Capture `screenshots/13-request-detail-citizen.png` only as the post-click
> dashboard state (it will be visually identical to step 13).

#### 15. Close the Citizen Session

```bash
playwright-cli -s=user-service-request-happy-path--citizen close
```

**Verify** — `playwright-cli list` shows no active session for either name.

## Known Issues & Tips

1. **Refs are snapshot-local.** Every `eN` above is a placeholder. Re-derive
   each ref from the latest `playwright-cli snapshot` by matching role +
   accessible name (see comments next to each click/fill).
2. **Two parallel sessions.** Phase A & C share the citizen session; Phase B
   uses a separate service session. After Phase A, `playwright-cli goto` in
   Phase C resets in-memory auth state — re-login is expected.
3. **Single Keycloak realm.** Both portals share the `portal` realm
   (`http://localhost:8888/keycloak/realms/portal`). There is no separate `service`
   realm in this checkout — confirm against
   `infrastructure/keycloak/import/portal-realm.json`.
4. **Service portal has no auth in dev.** Open `http://localhost:3001` (or
   `:8281` in Mode A) and the requests table renders directly. No
   `SERVICE_USER_*` env vars are needed.
5. **Service-portal port differs by mode.**
   - Mode B (native, recommended dev): **3001** — the Next.js dev server binds
     directly to its `PORT=3001` configuration.
   - Mode A (Docker, `--profile full`): **8281** — the container maps host
     8281 → container 3001.
6. **Service slug vs. visible label.** The route segment is `replace-dog-tag`
   but the visible label is "Ersatzmarke beantragen". Always click by
   accessible name; never by slug.
7. **Sub-category is "Tierhaltung" (not "Tiere").** "Engagement und Hobby" →
   "Tierhaltung" → "Ersatzmarke beantragen".
8. **Form is a 4-step wizard.** Step 1 "Persönliche Angaben" is pre-filled and
   can usually be advanced with "Weiter" only. Step 2 "Angaben zum Hund"
   requires picking a dog (dropdown auto-fills the name), entering a tag
   number, picking a breed (e.g. "Mischling"), and selecting a reason ("Marke
   verloren"). Step 3 "Dokumente" requires nothing. Step 4 "Kosten" requires
   selecting "PayPal" and the consent checkbox; the submit button reads
   **"Bezahlen und absenden"**.
9. **No citizen-side Antragsnummer.** The citizen confirmation screen says
   only "Vielen Dank für Ihren Antrag!" — no reference ID is shown to the
   citizen. On the **service-portal** side, the numeric `REQUEST_ID` is now
   exposed via the new **"ID"** column of the requests table (and still
   matches the `/<id>` segment of the row's Details link). Use the ID column
   as the primary lookup; Eingangsdatum + last-row position remain a
   secondary heuristic.
10. **Status vocabulary.** Initial status: **"offen"**. Target status:
    **"In Bearbeitung"**. Both portals use the same German labels.
11. **Status transition is a single button.** On the request detail in the
    service portal, click "Bearbeitung beginnen". The button then flips to
    "Fertigstellen", and the list view reflects "In Bearbeitung". There is no
    status dropdown or confirmation dialog.
12. **Citizen-side detail navigation is not wired.** The status pill on the
    user-portal dashboard is not a real link — verify Phase C against the
    dashboard text, not a detail screen.
13. **No fast-path runner.** This scenario does not (yet) ship a `run.sh`. Use
    the AI team workflow (see CLAUDE.md "E2E Testing Team Workflow") with this
    markdown as the playbook.

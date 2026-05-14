# Aircast — project guide for Claude (and any other assistant)

This file is the entry point for any AI assistant working in this repo. Read it before touching code.

For the historical project context (what Aircast is, what's shipped, hard preferences like "no em dashes anywhere"), see `handoff/HANDOFF.md` — that document still applies, this one supplements it with the multi-machine workflow.

For the live status of "what's in flight right now," see `.claude/SESSION_HANDOFF.md` — that one updates every session.

---

## Multi-machine workflow

This project is worked on from two machines:

- **Windows desktop** (hostname: `aircast-windows-desk`) — primary build environment, has Android Studio, the production keystore mount, and the FFmpeg binary path used by the asset-generation scripts.
- **MacBook Air** (hostname: `aircast-mba`) — secondary, used for travel and lighter work. Same keystore is accessible via Google Drive sync at `~/Documents/aircast-release-key.jks`.

`origin/main` on GitHub (`vikasdc/screen_mirror`) is the **only** source of truth. No machine holds state that the other can't reconstruct from git.

### Session start (always)

1. `git pull --rebase origin main` BEFORE doing anything else. If the rebase fails because of local commits that conflict, stop and resolve before continuing.
2. Read `.claude/SESSION_HANDOFF.md` end-to-end. Seed TODOs from its "Open threads" section.
3. Run `git status`. If anything is uncommitted or untracked, that means the previous session ended without flushing — WARN about it. Decide together whether to commit, stash, or discard before starting new work. Untracked work in a multi-machine setup is a silent footgun.

### Session end (always)

1. Commit + push EVERY file you touched. Including drafts, including half-finished work — better to push a `wip:` commit than leave changes only on one disk.
2. Update `.claude/SESSION_HANDOFF.md`. Specifically:
   - Move any completed items out of "Open threads".
   - Add any new in-flight items to "Open threads".
   - Append a dated entry to the "Log" section (newest at top, format `## YYYY-MM-DD (machine-hostname)`).
3. Commit + push the updated SESSION_HANDOFF.md as the LAST action of the session.

If the assistant is about to end the conversation without doing step 2, that's a bug — explicitly ask it to flush the handoff.

### Routines (scheduled tasks)

Any routine registered with the OS scheduler (Windows Task Scheduler, macOS launchd) that touches this repo MUST consult `.claude/routine-locks.json` before doing work. The file's `_schema` block documents the 7-step claim protocol. Quick version:

1. `git pull --rebase` — get latest claims.
2. Check `entries.<routine-name>-<YYYY-MM-DD>` — if it exists with status `completed` or `in_progress`, abort.
3. Write a claim entry with `status: in_progress`, your hostname, an ISO timestamp.
4. `git commit -m "lock: claim <key>"` the routine-locks.json change.
5. `git push origin main` — if push fails (another machine raced you), pull-rebase and restart from step 2.
6. Do the work.
7. Update the entry to `status: completed` with a completed timestamp, commit + push.

Idempotency rule: routines must be safe to run twice. The lock reduces race conditions but never eliminates them — assume the lock might briefly fail and design accordingly.

### Files that sync (tracked in git)

These ARE the project; they move with you:

- All source code under `AndroidApp/`, `scripts/`, `docs/`.
- All Play Store assets under `play_store_assets/`, `play_store_assets_v2/`.
- `CLAUDE.md` (this file).
- `.claude/SESSION_HANDOFF.md` — the living session log.
- `.claude/routine-locks.json` — the distributed routine lock file.
- `.gitignore`, `CHANGES.md`, `privacy_policy.html`, `release/app-release.aab` (the latest signed bundle ready for Play Console upload).
- `handoff/` — the 2026-05 account-transition bundle (legacy but still authoritative for project history).

### Files that do NOT sync (gitignored or local-only)

These are per-machine; do not expect them to travel:

- `AndroidApp/app/build/` — Android build output. Always regenerable.
- `AndroidApp/app/keystore.properties` — release-signing creds. Each machine needs its own copy filled in from `keystore.properties.example`.
- `*.jks` / `*.keystore` — the keystore binary itself lives on Google Drive (see `handoff/HANDOFF.md` for path).
- `AndroidApp/local.properties` — the Android SDK path, which differs between Windows and macOS.
- `.gradle/`, `.idea/`, `.vscode/` — IDE/build state.
- Playwright `video_raw/` directories under `play_store_assets_v2/`.
- `.claude/worktrees/` — subagent worktrees the assistant spins up temporarily.
- Any environment variables you set per shell.

### What this workflow does NOT solve

Be honest about the limits:

- **Chat history.** When you switch machines, the assistant's previous conversation transcripts do not move with you. That's why SESSION_HANDOFF.md exists — to make the not-syncable bits at least summarized into a file that does sync. But nuance and context will be lost. Don't expect the assistant on machine B to remember a casual aside from machine A.
- **Local assistant state.** Background agent IDs, scheduled wakeups, open subagent processes, in-flight `Bash` background commands — these are per-machine and per-process. If a background command was running on the Windows desktop and you switch to the MacBook, it's gone.
- **Per-machine secrets.** Keystore passwords, API tokens, AdMob app IDs. These must be entered separately on each machine. Currently the project has only one such secret (the keystore password) and it's stored in `keystore.properties` (gitignored). If you add more, keep them in the same file pattern.
- **Existing OS-scheduled tasks.** Whatever cron / Task Scheduler / launchd entries exist on either machine TODAY are not duplicated to the other. The routine-locks.json system protects FUTURE shared routines once you set them up, but won't retroactively dedup what's already running. Audit each machine's scheduler manually if you suspect drift.

---

## Quick-reference checks for "is the assistant set up right"

After reading this file, the assistant should know:

1. Which two machines work this repo (Windows desktop + MacBook Air).
2. That `git pull --rebase origin main` is the FIRST thing on every session.
3. That `.claude/SESSION_HANDOFF.md` is the living state, updated as the LAST action of every session.
4. That scheduled routines must claim a slot in `.claude/routine-locks.json` before running.
5. That `handoff/HANDOFF.md` is the long-running project context (read once per new account).

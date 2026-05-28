# Client/Server/Universal Split Progress

## Applied commits

This document records the implementation progress for the architecture split.

## Phase 0 — inventory/docs/check scripts

Status: partially complete.

Implemented:

- Added `docs/architecture/split-inventory.md`.
- Documented current entrypoints, shared packages, client-only packages, mixed classes, network payloads, and server-authoritative candidates.

Still needed:

- Convert the inventory into a continually updated migration board if the split continues over multiple PRs/branches.

## Phase 1 — remove client imports from common

Status: started.

Implemented:

- Removed direct `net.minecraft.client.gui.screens.Screen` usage from `thaumcraft.common.items.wands.WandCastingItem`.
- `WandCastingItem` now emits the compact wand vis tooltip from common only.
- Added Gradle task `checkSideBoundaries`.
- Wired `checkSideBoundaries` into `check`.

Notes:

- Detailed Shift-only wand tooltip needs a follow-up client-only tooltip hook if the exact old UX must be restored.
- Current search no longer shows `WandCastingItem` as a common client import offender.

## Phase 2 — split network contracts/handlers

Status: scaffolded only.

Implemented:

- Added `thaumcraft.server.network.TCServerPayloadHandlers` as a server handler bridge.
- Added `thaumcraft.client.network.TCClientPayloadHandlers` as a client handler bridge.

Not yet completed:

- `TCNetwork` still registers legacy payload record handlers directly.
- Payload records still contain handler logic.
- A safe side-dispatch strategy is still required before wiring common `TCNetwork` directly to client/server handler classes.

Reason:

- Importing `thaumcraft.client.*` or `thaumcraft.server.*` directly into `thaumcraft.common.network.TCNetwork` would violate the target dependency graph.
- The next step should introduce a side-safe network bootstrap/dispatcher instead of coupling common to client/server implementation packages.

## Phase 3 — prepare Gradle build variants

Status: prototype complete.

Implemented:

- Added `clientJar`.
- Added `serverJar`.
- Added `universalJar`.
- Added `buildSplitJars`.

Current behavior:

```bash
./gradlew buildSplitJars
```

Expected outputs:

```text
build/libs/Thaumcraft-<minecraft_version>-client.jar
build/libs/Thaumcraft-<minecraft_version>-server.jar
build/libs/Thaumcraft-<minecraft_version>-universal.jar
```

Important limitation:

- This is not yet the final multi-module architecture.
- It is a safe prototype based on the existing single `sourceSets.main` layout.
- True protected split still requires moving server-authoritative code out of client-visible common classes.

## Phase 4 — move server-authoritative logic

Status: not safely completed in this iteration.

Reason:

- This phase is large and should be done system-by-system.
- Moving `RunicMatrixBlockEntity`, `InfusionCrafting`, research, wand casting, aura/vis, and essentia logic requires compile/runtime feedback.
- A partial mechanical move can easily break client/server classloading or singleplayer behavior.

Recommended next order:

1. Introduce service interfaces/contracts in common.
2. Move wand action authority behind a server service.
3. Move research note creation/progression behind a server service.
4. Move infusion craft cycle behind a server service.
5. Move essentia drain/transport behind a server service.
6. Keep block entity state/sync in common.
7. Keep renderer/fx/UI in client.

## Phase 5 — tests/CI/checkups

Status: not fully complete.

Implemented:

- Added local Gradle side-boundary check.

Still needed:

- Add GitHub Actions workflow for `./gradlew build`, `./gradlew checkSideBoundaries`, and `./gradlew buildSplitJars`.
- Add jar inspection checks.
- Add runtime smoke checks where the environment supports Minecraft launches.

## Environment limitation observed

Attempted local clone from the execution container failed with DNS resolution failure:

```text
fatal: unable to access 'https://github.com/Extectick/ThaumCraft-1.21.1-NeoForge.git/': Could not resolve host: github.com
```

Therefore, `./gradlew build`, `./gradlew runClient`, and `./gradlew runServer` were not executed in this chat environment.

All changes were made through the GitHub connector. A real local or CI run is required before considering this split stable.

## Required next check

Run locally or in CI:

```bash
./gradlew clean build
./gradlew checkSideBoundaries
./gradlew buildSplitJars
```

Then inspect jars:

```bash
jar tf build/libs/*client*.jar | grep "thaumcraft/server" && exit 1 || true
jar tf build/libs/*server*.jar | grep "thaumcraft/client" && exit 1 || true
jar tf build/libs/*universal*.jar | grep "thaumcraft/client"
jar tf build/libs/*universal*.jar | grep "thaumcraft/server"
```

If those pass, continue Phase 2 handler wiring and Phase 4 service extraction in smaller commits.

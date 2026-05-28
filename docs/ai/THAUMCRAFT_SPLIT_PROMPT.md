# AI Implementation Prompt: Thaumcraft 1.21.1 NeoForge Architecture Split

## Role

You are an expert Minecraft 1.21.1 NeoForge mod architect and senior Java/Gradle engineer. Your task is to refactor the current Thaumcraft 1.21.1 NeoForge port into a stable, scalable, maintainable architecture that can produce three build variants from one codebase:

```text
thaumcraft-client.jar
thaumcraft-server.jar
thaumcraft-universal.jar
```

This must be done without duplicating gameplay logic across variants.

The architecture should combine:

- Create-like internal organization: clean common/client split, centralized registries, centralized packets, strong package discipline.
- AE2-like public API boundary: separate public contracts from internal implementation.
- Mekanism-like Gradle/build organization: scalable source sets or modules, explicit build outputs, datagen/gametest awareness.
- Protected server-authoritative gameplay model: client should not contain valuable server-only algorithms.

---

## Repository Context

Repository:

```text
Extectick/ThaumCraft-1.21.1-NeoForge
```

Current state summary:

- Minecraft: `1.21.1`
- Loader: NeoForge
- Java: 21
- Current project is a single Gradle project.
- Current build uses one `sourceSets.main`.
- Current mod is effectively a universal mod.
- Current `modid` is:

```text
thaumcraft
```

Current important files/classes:

```text
settings.gradle
build.gradle
src/main/resources/META-INF/neoforge.mods.toml
src/main/java/thaumcraft/Thaumcraft.java
src/main/java/thaumcraft/client/ThaumcraftClient.java
src/main/java/thaumcraft/common/network/TCNetwork.java
src/main/java/thaumcraft/common/registry/*
src/main/java/thaumcraft/common/items/wands/WandCastingItem.java
src/main/java/thaumcraft/common/blockentities/RunicMatrixBlockEntity.java
src/main/java/thaumcraft/common/research/ResearchManager.java
status.md
```

Known current problems:

1. The project is not yet multi-module / multi-output.
2. There is no explicit server module or server-only package boundary.
3. Important server gameplay is currently inside `common` classes.
4. Some `common` classes import client-only classes, for example `WandCastingItem` imports `net.minecraft.client.gui.screens.Screen`.
5. Packet records and packet handlers are currently coupled in some places.
6. `Thaumcraft.java` registers all common systems in one universal entrypoint.
7. `ThaumcraftClient.java` is already properly client-only via `@Mod(value = Thaumcraft.MODID, dist = Dist.CLIENT)` and should be preserved as the model for client entrypoint design.

---

## High-Level Objective

Refactor the project into a clean architecture that supports these outputs:

### 1. Client build

```text
Thaumcraft-1.21.1-client.jar
```

Used by players connecting to protected servers.

Contains:

- API contracts.
- Shared registry declarations / shell classes.
- Client entrypoint.
- Screens, HUD, renderers, particles, keybinds, tooltips.
- Client-side network handlers.
- Assets required for display.

Must not contain:

- Research engine internals.
- Aura simulation internals.
- Infusion algorithm internals.
- Essentia transport/storage algorithm internals.
- Spell/focus execution internals.
- Server-only validation logic.
- Hidden balancing formulas.

### 2. Server build

```text
Thaumcraft-1.21.1-server.jar
```

Used on dedicated servers.

Contains:

- API contracts.
- Shared registry declarations / shell classes.
- Server entrypoint / server event registration.
- Server-side packet handlers.
- Gameplay authority logic.
- Research progression.
- Aura/vis systems.
- Infusion crafting logic.
- Essentia systems.
- Warp logic.
- Persistent data.
- Commands.
- Worldgen/server events.

Must not contain or load:

```java
net.minecraft.client.*
```

Dedicated server must not crash due to client classloading.

### 3. Universal build

```text
Thaumcraft-1.21.1-universal.jar
```

Used for normal modpacks, singleplayer, development, and compatibility.

Contains:

```text
api + common + client + server
```

Important: universal must not have a third implementation of gameplay logic. It is only a merged/aggregated build.

---

## Non-Negotiable Architecture Rules

### One codebase

Do not create three independent mods.

Correct model:

```text
api code      written once
common code   written once
client code   written once
server code   written once
universal     assembled from the above
```

Incorrect model:

```text
client implementation
server implementation
universal implementation
```

### One modid

Keep the same mod id for all runtime variants:

```text
thaumcraft
```

Do not introduce:

```text
thaumcraft_client
thaumcraft_server
thaumcraft_universal
```

Reason: different mod ids will cause registry namespace, datapack, recipe, world save, and network handshake problems.

### Common must be side-safe

`common` must not import or reference:

```java
net.minecraft.client.*
com.mojang.blaze3d.*
net.minecraft.client.gui.*
net.minecraft.client.renderer.*
```

Any tooltip, screen, HUD, renderer, particle provider, keybind, model, or client-only visual logic must live in `client`.

### Server authority

Gameplay results must be decided by server logic.

The client may request actions. The server validates and executes them.

Correct:

```text
client sends CastFocusPayload
server checks research, vis, target validity, cooldowns
server executes effect
server sends visual/sync packets back
```

Incorrect:

```text
client computes cost/effect/result and server trusts it
```

### Packet contracts and packet handlers must be separable

Prefer this split:

```text
common/network/payloads     packet records, TYPE, codecs
client/network              clientbound handlers
server/network              serverbound handlers
common/network/TCNetwork    registration bridge
```

Avoid hiding server gameplay logic directly inside packet record classes.

---

## Recommended Module / Source Layout

Preferred final Gradle modules:

```text
settings.gradle
include "thaumcraft-api"
include "thaumcraft-common"
include "thaumcraft-client"
include "thaumcraft-server"
include "thaumcraft-universal"
include "thaumcraft-datagen"
include "thaumcraft-gametest"
```

If a full Gradle multi-module migration is too disruptive for the first pass, use source sets with the same logical boundaries, but keep the final direction compatible with modules.

Recommended logical package layout:

```text
thaumcraft
├─ api
│  ├─ aspects
│  ├─ research
│  ├─ wands
│  ├─ infusion
│  └─ util
├─ common
│  ├─ registry
│  ├─ block
│  ├─ item
│  ├─ blockentity
│  ├─ entity
│  ├─ menu
│  ├─ component
│  ├─ attachment
│  ├─ recipe
│  └─ network
├─ client
│  ├─ ThaumcraftClient.java
│  ├─ screen
│  ├─ hud
│  ├─ render
│  ├─ particle
│  ├─ tooltip
│  ├─ input
│  └─ network
├─ server
│  ├─ ThaumcraftServer.java
│  ├─ research
│  ├─ aura
│  ├─ vis
│  ├─ infusion
│  ├─ essentia
│  ├─ spell
│  ├─ warp
│  ├─ world
│  ├─ command
│  └─ network
├─ datagen
└─ gametest
```

Recommended Create-like top-level registries:

```text
AllBlocks.java or TCBlocks.java
AllItems.java or TCItems.java
AllBlockEntityTypes.java or TCBlockEntities.java
AllMenuTypes.java or TCMenuTypes.java
AllRecipeTypes.java or TCRecipeTypes.java
AllRecipeSerializers.java or TCRecipeSerializers.java
AllPackets.java or TCNetwork.java
AllDataComponents.java or TCDataComponents.java
AllDataAttachments.java or TCDataAttachments.java
AllCreativeTabs.java or TCCreativeTabs.java
```

Do not rename everything just for aesthetics. Prefer minimal churn unless a rename improves boundaries.

---

## Dependency Direction

Required dependency graph:

```text
thaumcraft-api
   ↑
thaumcraft-common
   ↑              ↑
thaumcraft-client thaumcraft-server
        \          /
     thaumcraft-universal
```

Hard rules:

```text
api depends on nothing project-local
common depends on api
client depends on api + common
server depends on api + common
universal depends on client + server
client must not depend on server
server must not depend on client
common must not depend on client or server
```

---

## Implementation Plan

### Phase 0 — Baseline inventory and safety checks

Before changing architecture, inspect and document:

- Existing Gradle setup.
- Existing entrypoints.
- Existing registries.
- Existing packet classes.
- Existing client-only imports in common code.
- Existing server-only logic in common code.
- Existing datagen and generated resources.
- Existing runtime resources and assets.

Produce an inventory file:

```text
docs/architecture/split-inventory.md
```

Include sections:

```text
Client-only classes
Server-authoritative classes
Shared/common classes
Mixed classes that must be split
Packet contracts
Packet handlers
Risky imports
Resources/datagen notes
```

Minimum command checks:

```bash
./gradlew build
./gradlew runServer
./gradlew runClient
```

Acceptance:

- Existing universal build still works before refactor.
- Inventory clearly lists mixed classes.

---

### Phase 1 — Establish logical boundaries without changing output jars

Goal: clean side boundaries while still producing the current universal mod.

Tasks:

1. Create or normalize packages:

```text
thaumcraft.api
thaumcraft.common
thaumcraft.client
thaumcraft.server
```

2. Move client-only logic out of common.

Example:

Current problem:

```java
// common item class
import net.minecraft.client.gui.screens.Screen;
```

Refactor target:

```text
common/items/wands/WandCastingItem.java
client/tooltip/WandTooltipClient.java
```

3. Extract server service classes for gameplay-heavy logic.

Examples:

```text
server/research/ServerResearchService
server/infusion/ServerInfusionService
server/wands/ServerWandService
server/essentia/ServerEssentiaService
server/warp/ServerWarpService
```

4. Keep block/item/blockentity classes in common only when they are safe shells or shared state containers.

5. Server-only algorithms should move into server services, called only on server side.

6. Client visuals should move into client render/fx/screen/tooltip classes.

Acceptance:

- No `net.minecraft.client.*` imports in `thaumcraft.common`.
- Dedicated server reaches `Done`.
- Client starts and can enter a test world.
- Existing behavior remains functionally equivalent.

Check commands:

```bash
./gradlew build
./gradlew runServer
./gradlew runClient
```

Search checks:

```bash
grep -R "import net.minecraft.client" src/main/java/thaumcraft/common || true
grep -R "com.mojang.blaze3d" src/main/java/thaumcraft/common || true
grep -R "net.minecraft.client.renderer" src/main/java/thaumcraft/common || true
```

Expected result: no matches in common.

---

### Phase 2 — Split networking into contracts and handlers

Goal: packets are shared contracts, handlers are side-specific.

Target layout:

```text
common/network/payload/
  CycleWandFocusPayload.java
  ResearchTablePlaceAspectPayload.java
  ResearchTableCombineAspectPayload.java
  ThaumonomiconCreateNotePayload.java
  EssentiaSourceFxPayload.java
  InfusionSourceFxPayload.java
  BlockZapFxPayload.java
  PedestalSparkleFxPayload.java
  WarpMessagePayload.java
  ResearchCompleteNotificationPayload.java

server/network/
  ServerPayloadHandlers.java
  ServerWandPayloadHandler.java
  ServerResearchPayloadHandler.java

client/network/
  ClientPayloadHandlers.java
  ClientFxPayloadHandler.java
  ClientResearchPayloadHandler.java

common/network/
  TCNetwork.java
```

Rules:

- Payload records live in common.
- Payload records contain `TYPE` and `STREAM_CODEC`.
- Serverbound handler methods live in server.
- Clientbound handler methods live in client.
- `TCNetwork` registers payloads using references to side-safe handler bridges.
- Avoid direct client class references from common network registration unless guarded by dist-safe indirection.

Acceptance:

- Client can connect to dedicated server.
- Serverbound packets execute only server logic.
- Clientbound packets execute only client visuals/sync.
- No server gameplay algorithm remains inside payload record classes.

Check commands:

```bash
./gradlew build
./gradlew runServer
./gradlew runClient
```

Manual checks:

```text
- Cycle wand focus from client.
- Create research note from Thaumonomicon if currently implemented.
- Trigger infusion FX packet if currently implemented.
- Confirm no server crash from client-only handler loading.
```

---

### Phase 3 — Gradle architecture for three outputs

Goal: create build variants while preserving one codebase and one modid.

Preferred output tasks:

```bash
./gradlew :thaumcraft-client:build
./gradlew :thaumcraft-server:build
./gradlew :thaumcraft-universal:build
```

Output jars:

```text
build/libs/Thaumcraft-1.21.1-client.jar
build/libs/Thaumcraft-1.21.1-server.jar
build/libs/Thaumcraft-1.21.1-universal.jar
```

Rules:

- All variants use `modId="thaumcraft"`.
- Client variant includes api + common + client.
- Server variant includes api + common + server.
- Universal variant includes api + common + client + server.
- Do not duplicate source files manually.
- Do not create separate registry IDs per variant.
- Do not change resource namespace.

Variant metadata may differ in display name, classifier, and description only:

```toml
modId="thaumcraft"
displayName="Thaumcraft Client"
```

```toml
modId="thaumcraft"
displayName="Thaumcraft Server"
```

```toml
modId="thaumcraft"
displayName="Thaumcraft"
```

Acceptance:

- Client jar does not contain server implementation packages.
- Server jar does not contain client implementation packages.
- Universal jar contains both.
- All jars preserve `thaumcraft` namespace.

Jar inspection checks:

```bash
jar tf build/libs/*client*.jar | grep "thaumcraft/server" && exit 1 || true
jar tf build/libs/*server*.jar | grep "thaumcraft/client" && exit 1 || true
jar tf build/libs/*universal*.jar | grep "thaumcraft/client"
jar tf build/libs/*universal*.jar | grep "thaumcraft/server"
```

---

### Phase 4 — Protected gameplay migration

Goal: gradually move valuable algorithms out of client-visible code.

Priority systems:

1. Research progression.
2. Wand/focus execution.
3. Vis/aura calculations.
4. Infusion crafting.
5. Essentia transport/storage.
6. Warp side effects.
7. Hidden unlock conditions.

For each system, split as follows:

```text
api:
  public interfaces, basic records, extension points

common:
  registry ids, shell item/block classes, data components, packet contracts

client:
  GUI, HUD, tooltip, particles, visual feedback, clientbound handlers

server:
  validation, calculations, progression, persistence, actual effects
```

Example: wand/focus

```text
common:
  WandCastingItem shell
  Focus item ids
  CastFocusPayload contract

client:
  keybind
  focus selector HUD
  tooltip rendering
  cast request sender
  particles

server:
  ServerWandService
  ServerFocusService
  research checks
  vis checks
  cooldown checks
  target validation
  effect execution
```

Example: infusion

```text
common:
  RunicMatrixBlockEntity state required for sync/render
  InfusionRecipe serializer/type
  packet contracts

client:
  RunicMatrixRenderer
  infusion particles
  local sound/visual animation

server:
  ServerInfusionService
  recipe matching
  essentia drain
  instability rolls
  item consumption
  warp/damage/explosion/eject effects
  final craft completion
```

Acceptance:

- Client jar cannot reveal core algorithms for protected systems.
- Server remains authoritative.
- Universal preserves singleplayer behavior.
- Client still displays correct visual/sync state.

---

### Phase 5 — Datagen, resources, gametests, and CI

Goal: make the architecture maintainable long term.

Datagen:

- Keep generated assets/data deterministic.
- Avoid duplicating generated resources per variant unless necessary.
- Ensure common resources are available to all variants.
- Ensure client-only assets are included in client and universal but not required by server-only runtime.

Gametest / regression tests:

Add focused tests where practical:

```text
registry load test
server start smoke
basic wand use test
research state test
infusion recipe matching test
block entity save/load test
packet registration test
```

CI should eventually run:

```bash
./gradlew build
./gradlew :thaumcraft-client:build
./gradlew :thaumcraft-server:build
./gradlew :thaumcraft-universal:build
./gradlew runGameTestServer
```

Also run jar inspection checks in CI.

---

## Required Checkups After Every Refactor Step

After each meaningful patch, run and record results:

```bash
./gradlew build
```

If architecture/build files changed:

```bash
./gradlew :thaumcraft-client:build
./gradlew :thaumcraft-server:build
./gradlew :thaumcraft-universal:build
```

If client code changed:

```bash
./gradlew runClient
```

Manual client checks:

```text
- Game reaches title screen.
- Integrated world can load if using universal.
- Creative tab opens.
- Registered items have models/textures.
- HUD/screens do not crash.
- No missing model spam for touched features.
```

If server/common code changed:

```bash
./gradlew runServer
```

Manual server checks:

```text
- Dedicated server reaches Done.
- No client classloading crash.
- Registries load.
- Curios loads if required.
- Recipes load.
```

If networking changed:

```text
- Start dedicated server with server jar.
- Start client with client jar.
- Connect to server.
- Trigger at least one serverbound packet.
- Trigger at least one clientbound packet.
- Confirm no protocol mismatch.
```

If universal changed:

```text
- Launch universal in client dev run.
- Enter singleplayer world.
- Test touched mechanic locally.
```

---

## Definition of Done

The architecture split is complete when all are true:

```text
[ ] There is a clean api/common/client/server/universal architecture.
[ ] There are three build outputs: client, server, universal.
[ ] All outputs use modid thaumcraft.
[ ] Client jar contains no thaumcraft.server implementation classes.
[ ] Server jar contains no thaumcraft.client implementation classes.
[ ] Common code contains no net.minecraft.client imports.
[ ] Dedicated server reaches Done with server build.
[ ] Client reaches title screen with client build.
[ ] Client build can connect to server build.
[ ] Universal build works for singleplayer.
[ ] Registry namespace remains stable.
[ ] Existing world/save compatibility is not intentionally broken.
[ ] Packet protocol version is explicit and checked.
[ ] Gameplay authority lives server-side.
[ ] No duplicate client/server/universal gameplay implementations exist.
[ ] Documentation explains how to add new mechanics by layer.
```

---

## How to Add New Mechanics After the Split

When implementing a new Thaumcraft mechanic, follow this template.

### Common

Add only shared contracts:

```text
registry ids
item/block shell
menu type
block entity type
data component
attachment key
recipe serializer/type
packet record/codecs
```

### Client

Add only presentation/input:

```text
screen
HUD
renderer
particle
tooltip
key mapping
clientbound handler
client-side prediction if safe
```

### Server

Add only authority:

```text
validation
cost calculation
research gating
world mutation
inventory mutation
entity effects
persistent state
serverbound handler
sync emission
```

### Universal

Add nothing manually unless build wiring requires it.

Universal must only aggregate.

---

## Anti-Patterns to Avoid

Do not do this:

```text
- Create different mod ids for client/server/universal.
- Duplicate the same gameplay algorithm in client, server, and universal.
- Put client imports in common.
- Put server-only algorithms inside packet records.
- Trust client-calculated gameplay results.
- Hide classloading problems with reflection everywhere.
- Move files mechanically without checking side dependencies.
- Break resource namespace or registry ids for cosmetic reasons.
- Create a client jar that cannot register the same ids as the server.
- Create a server jar that loads client screens/renderers.
```

Use reflection only as a temporary bridge with a TODO and a planned removal path.

---

## Suggested First Practical Patch

Start with the least risky patch:

1. Create `docs/architecture/split-inventory.md`.
2. Add package boundary documentation.
3. Remove client import from `WandCastingItem` by moving Shift tooltip logic into a client tooltip handler.
4. Add static checks or grep task for forbidden common imports.
5. Keep output as current universal jar.
6. Run:

```bash
./gradlew build
./gradlew runServer
./gradlew runClient
```

Only after this passes, proceed to network handler split.

---

## Final Target Summary

The final architecture should be:

```text
Thaumcraft
├─ thaumcraft-api
├─ thaumcraft-common
├─ thaumcraft-client
├─ thaumcraft-server
├─ thaumcraft-universal
├─ thaumcraft-datagen
└─ thaumcraft-gametest
```

Final outputs:

```text
Thaumcraft-1.21.1-client.jar
Thaumcraft-1.21.1-server.jar
Thaumcraft-1.21.1-universal.jar
Thaumcraft-1.21.1-api.jar optional
```

Final philosophy:

```text
Client = visual/network shell
Server = gameplay authority
Common = stable shared contract
Universal = merged build
API = addon/public contract
```

This should be stable, scalable, adaptive, AI-friendly, and suitable for long-term Thaumcraft-like feature development.

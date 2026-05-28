# Thaumcraft Client/Server/Universal Split Inventory

This document tracks the current architecture boundaries before and during the split into client, server, and universal build variants.

## Current build shape

- The project is currently a single NeoForge Gradle project.
- `settings.gradle` has no included Gradle subprojects yet.
- `build.gradle` uses the MDK-style single `sourceSets.main` layout.
- `src/main/resources/META-INF/neoforge.mods.toml` declares dependencies on both sides.
- Runtime output is currently a universal-style jar.

## Current entrypoints

### Common/universal entrypoint

```text
src/main/java/thaumcraft/Thaumcraft.java
```

Responsibilities currently include:

- registering blocks
- registering items
- registering block entities
- registering entity types
- registering menu types
- registering recipe types and serializers
- registering sound events
- registering particle types
- registering data attachments/components
- registering creative tabs
- registering network payloads
- registering common reload listeners/events/commands/config

### Client entrypoint

```text
src/main/java/thaumcraft/client/ThaumcraftClient.java
```

Responsibilities currently include:

- client setup
- render layers
- GUI layers/HUD overlays
- key mappings
- menu screens
- client extensions
- block entity renderers
- block colors
- item colors
- client tick/fx handlers
- tooltip component registration

This is already side-gated with `@Mod(value = Thaumcraft.MODID, dist = Dist.CLIENT)` and should be preserved as the pattern for client-only registration.

## Current shared/common areas

```text
src/main/java/thaumcraft/common/registry
src/main/java/thaumcraft/common/network
src/main/java/thaumcraft/common/blocks
src/main/java/thaumcraft/common/blockentities
src/main/java/thaumcraft/common/items
src/main/java/thaumcraft/common/menus
src/main/java/thaumcraft/common/crafting
src/main/java/thaumcraft/common/research
src/main/java/thaumcraft/common/lib
```

These currently mix three kinds of code:

1. true shared contracts and registry declarations;
2. side-safe block/item/block entity state containers;
3. server-authoritative gameplay logic that should eventually move behind `thaumcraft.server.*` services.

## Client-only classes already separated

```text
src/main/java/thaumcraft/client/ThaumcraftClient.java
src/main/java/thaumcraft/client/fx
src/main/java/thaumcraft/client/hud
src/main/java/thaumcraft/client/input
src/main/java/thaumcraft/client/lib
src/main/java/thaumcraft/client/network
src/main/java/thaumcraft/client/renderers
src/main/java/thaumcraft/client/screens
src/main/java/thaumcraft/client/tooltip
```

These should remain absent from the future server jar.

## Current client-only leakage into common

Known issue:

```text
src/main/java/thaumcraft/common/items/wands/WandCastingItem.java
```

Currently imports:

```java
net.minecraft.client.gui.screens.Screen
```

This must be removed from common. Shift-aware tooltip behavior should live in `thaumcraft.client.tooltip`.

## Network inventory

Central registration:

```text
src/main/java/thaumcraft/common/network/TCNetwork.java
```

Current payloads include:

### Serverbound

```text
CycleWandFocusPayload
ResearchTablePlaceAspectPayload
ResearchTableCombineAspectPayload
ThaumonomiconCreateNotePayload
```

### Clientbound

```text
EssentiaSourceFxPayload
InfusionSourceFxPayload
BlockZapFxPayload
PedestalSparkleFxPayload
WarpMessagePayload
ResearchCompleteNotificationPayload
```

Current risk:

- payload records and handler logic are coupled;
- future protected split should keep packet records/codecs in common, but handlers in `client.network` or `server.network`.

## Server-authoritative candidates

The following systems contain gameplay authority and should move behind server-side service classes over time:

```text
thaumcraft.common.research.ResearchManager
thaumcraft.common.research.WarpManager
thaumcraft.common.items.wands.WandCastingItem
thaumcraft.common.items.wands.WandVisHelper
thaumcraft.common.items.wands.WandFocusHelper
thaumcraft.common.lib.crafting.InfusionCrafting
thaumcraft.common.lib.crafting.InfusionAltarBuilder
thaumcraft.common.lib.crafting.InfusionAltarScan
thaumcraft.common.blockentities.RunicMatrixBlockEntity
thaumcraft.common.lib.events.EssentiaHandler
thaumcraft.common.lib.events.RunicShieldEvents
```

Do not move all of these in one patch. Extract service boundaries system by system.

## First safe extraction targets

1. Move wand shift tooltip logic out of common.
2. Add side-boundary checks.
3. Add build-variant jar tasks without changing runtime behavior.
4. Introduce handler bridge classes for networking before moving gameplay.
5. Extract infusion server logic only after network/build checks are stable.

## Required checks

After each architecture patch:

```bash
./gradlew build
./gradlew checkSideBoundaries
```

Runtime smoke checks when available:

```bash
./gradlew runServer
./gradlew runClient
```

Jar inspection checks after build variants exist:

```bash
jar tf build/libs/*client*.jar | grep "thaumcraft/server" && exit 1 || true
jar tf build/libs/*server*.jar | grep "thaumcraft/client" && exit 1 || true
jar tf build/libs/*universal*.jar | grep "thaumcraft/client"
jar tf build/libs/*universal*.jar | grep "thaumcraft/server"
```

## Current status

Phase 0 inventory started. The next safe implementation step is to remove the `net.minecraft.client.gui.screens.Screen` import from `WandCastingItem` and move Shift-specific wand tooltip rendering into a client-only tooltip handler.

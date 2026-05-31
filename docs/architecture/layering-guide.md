# Thaumcraft Layering Guide

This repository uses physical Gradle split entrypoint projects backed by logical source sets in the root NeoForge project. The source of truth remains one codebase; the subprojects provide stable build targets and variant outputs without duplicating source files.

## API

Package: `thaumcraft.api`

Allowed:

- public addon-facing interfaces
- stable records/value objects
- extension points
- codecs for public data contracts

Forbidden:

- internal gameplay services
- client screens/renderers/HUD
- dedicated server implementation details
- registry bootstrap side effects

## Common

Package: `thaumcraft.common`

Allowed:

- registry declarations
- item/block/block entity shells
- menu/container shared state
- recipe serializers/types
- data components and attachments
- packet payload records and codecs
- small side-safe hooks that default to no-op on the wrong side

Forbidden:

- `net.minecraft.client.*`
- `com.mojang.blaze3d.*`
- `thaumcraft.client.*`
- `thaumcraft.server.*`
- hidden gameplay algorithms intended to be absent from the protected client jar

Common may request side behavior only through side-safe hooks or common bridge classes. Side implementations must be registered from `thaumcraft.client` or `thaumcraft.server`.

## Client

Package: `thaumcraft.client`

Allowed:

- screens
- HUD overlays
- renderers
- particles and visual FX
- key mappings
- tooltip presentation
- clientbound packet handlers
- client-side request senders

Forbidden:

- final gameplay decisions
- trusted cost/effect calculation
- research progression authority
- inventory/world mutation authority

Client code may request actions from the server. It must not decide the result of protected mechanics.

## Server

Package: `thaumcraft.server`

Allowed:

- serverbound packet handlers
- validation
- cost calculation
- research gating
- world mutation
- inventory mutation
- persistence
- commands and server events
- aura, vis, infusion, essentia, warp, spell/focus authority

Forbidden:

- `net.minecraft.client.*`
- client screens/renderers/HUD
- client-only assets or presentation logic

## Universal

Universal is an aggregate jar only. Do not add universal-only gameplay implementations.

## Current Variant Tasks

- `./gradlew :thaumcraft-api:build`
- `./gradlew :thaumcraft-common:build`
- `./gradlew :thaumcraft-client:build`
- `./gradlew :thaumcraft-server:build`
- `./gradlew :thaumcraft-universal:build`
- `./gradlew :thaumcraft-datagen:build`
- `./gradlew :thaumcraft-gametest:build`
- `./gradlew apiJar`
- `./gradlew commonJar`
- `./gradlew clientJar`
- `./gradlew serverJar`
- `./gradlew universalJar`
- `./gradlew buildVariantJars`
- `./gradlew checkVariantJars`
- `./gradlew checkSplitArchitecture`

Outputs:

- `build/libs/Thaumcraft-1.21.1-api.jar`
- `build/libs/Thaumcraft-1.21.1-common.jar`
- `build/libs/Thaumcraft-1.21.1-client.jar`
- `build/libs/Thaumcraft-1.21.1-server.jar`
- `build/libs/Thaumcraft-1.21.1-universal.jar`

All variants keep the `thaumcraft` mod id and namespace.

## Required Gate

Before merging architecture or gameplay-boundary changes, run:

```bash
./gradlew checkSplitArchitecture build
```

This gate verifies physical split projects are present, compiles the logical `api`, `common`, `client`, and `server` source sets, checks forbidden common imports, rejects reflection-based side dispatch in `api/common`, builds layer and variant jars, verifies side package exclusion, verifies ServiceLoader provider placement, and checks that protected server implementation classes do not leak into the client jar.

Because NeoGradle dev runs use `sourceSets.main`, the universal dev runtime also includes both side-specific ServiceLoader provider files. `runClient` is therefore expected to behave like the universal jar for client presentation plus integrated-server/server service dispatch.

For the full local CI equivalent, run:

```bash
./gradlew checkSplitArchitecture :thaumcraft-api:build :thaumcraft-common:build :thaumcraft-client:build :thaumcraft-server:build :thaumcraft-universal:build :thaumcraft-datagen:build :thaumcraft-gametest:build build
```

For server/common changes, also run:

```bash
./gradlew runServer
```

Dedicated server must reach `Done`. Client runtime and client-server connection checks still require an explicit `runClient` pass.

## Adding a New Mechanic

Put shared IDs, shell classes, data components, recipe serializers, and payload records in common.

Put visual state, screens, key input, particles, renderers, tooltips, and clientbound handlers in client.

Put serverbound handlers, validation, calculations, persistent state updates, and actual effects in server.

Do not duplicate gameplay logic into client/server/universal copies. The universal jar is assembled from the same classes.

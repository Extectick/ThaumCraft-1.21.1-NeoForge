# ThaumCraft 1.21.1 NeoForge Port

This repository is a work-in-progress Minecraft 1.21.1 NeoForge port of Thaumcraft, with an initial Thaumic Tinkerer addon port in the same workspace.

## Build

Use JDK 21.

```bash
./gradlew checkSplitArchitecture :thaumcraft-api:build :thaumcraft-common:build :thaumcraft-client:build :thaumcraft-server:build :thaumcraft-universal:build :thaumcraft-datagen:build :thaumcraft-gametest:build build
```

Variant outputs are written to `build/libs`:

- `Thaumcraft-1.21.1-api.jar`
- `Thaumcraft-1.21.1-common.jar`
- `Thaumcraft-1.21.1-client.jar`
- `Thaumcraft-1.21.1-server.jar`
- `Thaumcraft-1.21.1-universal.jar`

The latest CI-verified build after the Thaumic Tinkerer split fix passed on commit `9628504da2a26bb696cda9a6226fabacbba0c629`.

## Architecture Notes

- `thaumcraft.common` and `thaumictinkerer.common` must stay side-safe.
- `thaumcraft.client` and `thaumictinkerer.client` own screens, renderers, models, item property overrides, and other client-only presentation code.
- Protected Thaumcraft gameplay authority lives under `thaumcraft.server`.
- The universal jar is an aggregate of the same source sets, not a separate gameplay implementation.

See `docs/architecture/split-inventory.md` and `docs/architecture/layering-guide.md` for the current split rules and validation history.

## Current Follow-ups

- Run an explicit client smoke after the Thaumic Tinkerer merge.
- Complete Thaumic Tinkerer Item/Mob Magnet block entities, menus, filters, and GUI.
- Wire `ShareBookItem` to real Thaumcraft research sharing behavior.
- Audit advanced ichor/KAMI armor behavior for server authority and cleanup edge cases.
- Continue replacing placeholder visual/device blocks with focused behavior slices.

## References

- NeoForge documentation: https://docs.neoforged.net/
- NeoForge Discord: https://discord.neoforged.net/
- Mojang mappings license reference: https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

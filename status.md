# Thaumcraft 1.21.1 NeoForge port status

Updated: 2026-05-23

## Current milestone

Build a first playable Curios accessory slice on NeoForge 1.21.1, replacing the old Baubles integration layer with a narrow Curios helper API.

## Completed

- NeoForge MDK 1.21.1 workspace created in `sources\ThaumCraft-1.21.1-NeoForge`.
- Project metadata changed from `examplemod` to `thaumcraft`.
- Java toolchain target set by the MDK to Java 21.
- NeoForge version pinned to `21.1.230`.
- Curios version pinned to `9.5.1+1.21.1`.
- Curios Maven repository and dependencies added.
- `neoforge.mods.toml` now declares `curios` as a required dependency.
- Minimal bootstrap classes created:
  - `thaumcraft.Thaumcraft`
  - `thaumcraft.client.ThaumcraftClient`
  - `thaumcraft.common.config.ThaumcraftConfig`
  - `thaumcraft.common.registry.TCItems`
  - `thaumcraft.common.registry.TCCreativeTabs`
- Placeholder `thaumonomicon` item and Thaumcraft creative tab added.
- `gradlew.bat build` completed successfully with JDK 21.
- Build artifact created: `build\libs\thaumcraft-4.2.3.5-port.0.jar`.
- First old-code inventory counters captured in `sources\tasks\port_thaumcraft_1_21_1_inventory.md`.
- Added initial Curios slot datapack resources:
  - player gets `ring`, `necklace`, and `belt`
  - `ring` slot size is raised to `2` to match Baubles behavior
  - empty Curios item tags are ready for migrated accessories
- Added `ThaumcraftCuriosCompat` as the single helper layer for future Baubles replacements.
- `gradlew.bat build` completed successfully again after Curios helper/data files were added.
- Added the first Curios item slice:
  - runic rings, amulets, and girdles
  - vis amulets
  - hover girdle
  - blank baubles
  - primal vis discount rings
  - focus pouch
- Added temporary 1.21.1 item IDs for old metadata variants so Curios tags can target them directly.
- Added basic API placeholders for `IRunicArmor`, `IVisDiscountGear`, and primal `Aspect`.
- `gradlew.bat build` completed successfully after Curios items were registered.
- Copied old textures for the current Curios item slice.
- Added generated item models for the current Curios item slice.
- `gradlew.bat build` completed successfully after models/textures were added.
- Jar now includes Curios classes, Curios data files, item models, and copied item textures.
- Ran `gradlew.bat runClient` with Curios on the runtime classpath.
- Fixed a creative tooltip crash caused by mutating Curios' immutable tooltip list.
- Updated item models/resources to the modern `textures/item` path and removed the old duplicate `textures/items` folder.
- Added Curios helper methods for:
  - total runic charge
  - vis discount by primal aspect
  - equipped focus pouch lookup
  - equipped hover girdle lookup
- `gradlew.bat build` completed successfully after the Curios helper and resource fixes.
- Ran `gradlew.bat runServer`; the dedicated server reached `Done`, and Curios loaded 10 slots plus 1 entity.
- Added `RunicShieldEvents` on the NeoForge game event bus:
  - server-side runic charge is collected from armor plus Curios
  - charge starts empty and recharges over time like the old handler
  - incoming health damage is reduced by available runic charge
  - emergency amulet recharge is represented
- Added common config keys for runic recharge speed and post-break recharge delay.
- `gradlew.bat build` completed successfully after the runic shield handler was added.
- Dedicated server smoke still reaches `Done` after the runic shield handler and config changes.
- Added `RunicShielding` helper for final charge calculation and the legacy `RS.HARDEN` custom data key.
- Added the old `Runic shield +N` tooltip line for Curios runic items.
- Added `item.runic.charge` to `en_us.json`.
- `gradlew.bat build` completed successfully after runic tooltip and hardening support.
- Client smoke reached an integrated world after the tooltip changes without the previous Curios tooltip crash.

## Next checks

- Expand inventory into full registry id lists before moving large legacy subsystems.
- Complete behavior for the first Curios slice:
  - add client HUD/network sync for current runic charge
  - add write path for hardening augment recipes
  - vis amulet storage and wand transfer
  - focus pouch inventory and wand lookup
  - research-gated equip rules

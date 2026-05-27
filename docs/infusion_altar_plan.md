# Infusion Altar Implementation Notes

## Current Focus
- [x] Plan the large Infusion Altar block.
- [x] Start with visual/model foundation.
- [x] Add Arcane Pedestal inventory behavior.
- [x] Render pedestal item above the block.
- [x] Replace placeholder `runic_matrix` and `infusion_pillar` cube models with altar-specific block models.
- [x] Make `infusion_pillar` an internal multiblock block, not a public item.
- [x] Add Runic Matrix block entity and old-style 3D renderer.
- [x] Add wand activation that creates the Infusion Altar multiblock from the old 3x3x3 blueprint.
- [x] Add Runic Matrix structure scan.
- [x] Add Infusion recipe type and serializer.
- [x] Add initial crafting start and matrix recipe state.
- [x] Add first crafting progress/completion logic.
- [x] Add shared `AspectList` foundation for old-style essentia costs.
- [x] Replace infusion recipe essentia placeholder with `AspectList`.
- [x] Add first old-style essentia drain pass from nearby containers.
- [ ] Add instability and symmetry checks.
- [ ] Add Thaumonomicon/JEI display for infusion recipes.

## Implemented
- `arcane_pedestal` is no longer a passive simple block.
- `ArcanePedestalBlockEntity` stores one item stack.
- Right-click behavior:
  - with item in hand and empty pedestal: insert one item.
  - with empty hand and occupied pedestal: take stored item.
  - stored item drops when the pedestal is broken.
- Client renderer draws the stored item above the pedestal.
- `runic_matrix` world rendering is now a safe block entity renderer using the existing item model, with active rotation and a small crafting ramp. The old full cube jitter/halo pass is still pending.
- `runic_matrix` keeps a separate static 8-cube item model because the placed block model is intentionally invisible for the renderer.
- `runic_matrix` renderer is registered client-side, uses old-style gradual `startUp`, and suppresses the placed block's static model so the animated block entity renderer owns world rendering.
- `runic_matrix` has a client ticker for old-style active spin/tilt ramping and `craftCount` ramp state.
- `infusion_pillar` now has a dedicated JSON model instead of a full placeholder cube.
- `infusion_pillar` has no BlockItem/creative entry. If broken, it drops `arcane_stone_bricks` as a temporary closest match to the old metadata behavior.
- `infusion_pillar` now stores its altar corner and upper/lower half in blockstate. The lower half renders the original old `pillar.obj` through the NeoForge OBJ loader with the old orientation mapping, while the upper half is a hidden technical support block like old metadata `4`.
- `infusion_pillar` collision mirrors the old practical behavior without duplicating collisions in the modern engine: the lower half uses a full-width half-height box, and the upper technical half has no shape/collision. Removing either half removes the paired block without an extra drop.
- Breaking any `infusion_pillar` half collapses the formed altar: the broken pillar pair drops the two original source blocks, the other pillar pairs revert to `arcane_stone_bricks` lower blocks and `arcane_stone` upper blocks, and the matrix is marked inactive.
- Wand activation now searches the old 3x3x3 altar blueprint around the clicked block and transforms the corner blocks into internal `infusion_pillar` blocks.
- The activation shape mirrors old `WandManager.fitInfusionAltar`:
  - top center: `runic_matrix`
  - bottom center: `arcane_pedestal`
  - middle layer corners: `arcane_stone`, replacing old `blockCosmeticSolid` metadata `6`
  - bottom layer corners: `arcane_stone_bricks`, replacing old `blockCosmeticSolid` metadata `7`
- Activation consumes 25 vis of each primal aspect from the wand, represented as `2500` centivis in the new wand storage.
- On activation the existing `RunicMatrixBlockEntity` is marked active. Crafting is still intentionally not started here.
- `RunicMatrixBlockEntity` now scans its surroundings server-side like the old `TileInfusionMatrix`:
  - verifies the central pedestal two blocks below the matrix.
  - verifies the four lower `infusion_pillar` blocks at the altar corners.
  - discovers surrounding pedestals in the old search volume.
  - calculates pedestal symmetry from mirrored pedestal positions and stored items.
  - deactivates the matrix if an active altar becomes structurally invalid.
- `thaumcraft:infusion` recipes are registered as data-driven recipes with:
  - central `catalyst`
  - unordered surrounding `components`
  - `result`
  - `instability`
  - optional old-style multi-aspect `essentia` map using aspect tags.
- A wand click on an active, valid, non-crafting matrix now starts the first matching infusion recipe using the central pedestal and discovered surrounding pedestals.
- During crafting the matrix now consumes matched component items from surrounding pedestals and replaces the central catalyst with the recipe result when no ingredients remain.
- Client-side `infuserstart` / `infuser` sounds are played from the matrix while crafting, mirroring the old client effect loop.
- The new `thaumcraft.api.aspects.AspectList` foundation exists for multi-aspect recipe costs and item aspect data. It has old-style operations, JSON codec, network codec, and NBT helpers.
- `ObjectAspectRegistry` now provides old-style `getObjectTags(ItemStack)` and `getBonusTags(ItemStack, AspectList)` behavior. Item aspects are loaded from `data/*/thaumcraft/item_aspects/*.json`, with exact item ids and modern item tags supported, missing entries can be generated from crafting/arcane/infusion recipes, and dynamic bonuses are added for tools, armor, bows, enchantments, potion effects, and essentia-storing items.
- Holding Shift over an item in inventory now renders assigned aspects as icons with amounts, using the bonus-aware item aspect registry.
- `InfusionRecipe` and `RunicMatrixBlockEntity` now use `AspectList` for remaining recipe essentia instead of the temporary `PrimalVisStorage` placeholder.
- During crafting the matrix now drains one required essentia at a time from nearby `IEssentiaContainer` block entities in radius 12 before it consumes pedestal items. This is the first drain pass and does not yet implement old tube suction/pathing.
- Warded and void jars now participate in old-style essentia transport: jars pull from the tube above them, void jars accept overflow, labels lock jars to an aspect, filtered jars render the old label/aspect marker, and filled/labeled jar item stacks preserve their contents/filter when broken and placed again.
- Initial test infusion recipes exist for `thaumium_wand_cap_infusion` and `void_wand_cap_infusion` so the start path can be tested in-game before completion logic is added.

## Old Thaumcraft Behavior Notes
- Players crafted `Infusion Matrix` and `Arcane Pedestal`; they did not craft/place `Infusion Pillar` directly.
- Wand activation transformed the altar structure into internal stone-device metadata:
  - matrix stayed metadata `2`
  - pillar bottom used metadata `3` / `TileInfusionPillar`
  - pillar top used metadata `4`
- Breaking old pillar metadata dropped source decorative blocks, not a pillar item.

## Next Step
Add explicit essentia costs to migrated infusion recipes once jar/tube filling is stable, then port source-to-matrix FX and the remaining instability/symmetry behavior. Continue expanding item aspect data from old `ConfigAspects` as new ported items/blocks appear. The detailed aspect/essentia port plan is tracked in `docs/essentia_aspect_port_plan.md`.

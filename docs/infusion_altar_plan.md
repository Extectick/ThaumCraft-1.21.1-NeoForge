# Infusion Altar Implementation Notes

## Current Focus
- [x] Plan the large Infusion Altar block.
- [x] Start with visual/model foundation.
- [x] Add Arcane Pedestal inventory behavior.
- [x] Render pedestal item above the block.
- [x] Replace placeholder `runic_matrix` and `infusion_pillar` cube models with altar-specific block models.
- [x] Make `infusion_pillar` an internal multiblock block, not a public item.
- [x] Add Runic Matrix block entity and old-style 3D renderer.
- [ ] Add Runic Matrix structure scan.
- [ ] Add Infusion recipe type and serializer.
- [ ] Add crafting start/progress/completion logic.
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
- `runic_matrix` world rendering is now a block entity renderer based on the old TESR: 8 sub-cubes using `textures/models/infuser.png`, with active rotation and glow overlay.
- `runic_matrix` keeps a separate static 8-cube item model because the placed block model is intentionally invisible for the renderer.
- `infusion_pillar` now has a dedicated JSON model instead of a full placeholder cube.
- `infusion_pillar` has no BlockItem/creative entry. If broken, it drops `arcane_stone_bricks` as a temporary closest match to the old metadata behavior.

## Old Thaumcraft Behavior Notes
- Players crafted `Infusion Matrix` and `Arcane Pedestal`; they did not craft/place `Infusion Pillar` directly.
- Wand activation transformed the altar structure into internal stone-device metadata:
  - matrix stayed metadata `2`
  - pillar bottom used metadata `3` / `TileInfusionPillar`
  - pillar top used metadata `4`
- Breaking old pillar metadata dropped source decorative blocks, not a pillar item.

## Next Step
Implement Runic Matrix structure/pedestal discovery. This should not start crafting yet; it should only validate altar shape and expose stable data for later crafting logic.

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
- [x] Preserve active infusion recipe state through world reloads.
- [x] Port old delayed ingredient consumption cycle.
- [x] Add first essentia source FX from source container to matrix.
- [x] Replace first essentia FX with old-style client-side 15 tick source trail payload.
- [x] Add old-style instability and symmetry checks.
- [x] Add first old-style instability event pass.
- [x] Add old-style item absorption FX payload.
- [x] Add fail/cancel behavior for missing catalyst and disrupted ingredients.
- [x] Replace remaining temporary crafting particles with old infusion matrix FX paths.
- [x] Add first old-style flux goo/gas and warp storage hooks for instability.
- [x] Add JEI display for infusion recipes.
- [ ] Add Thaumonomicon research pages for infusion recipes.

## Implemented
- `arcane_pedestal` is no longer a passive simple block.
- `ArcanePedestalBlockEntity` stores one item stack.
- Right-click behavior:
  - with item in hand and empty pedestal: insert one item.
  - with empty hand and occupied pedestal: take stored item.
  - stored item drops when the pedestal is broken.
- Client renderer draws the stored item above the pedestal.
- `runic_matrix` world rendering is handled by a block entity renderer with active rotation, startup/crafting ramps, glowing runes, internal glow, and the current infusion FX layers.
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
- During crafting the matrix now drains one required essentia at a time from nearby `IEssentiaContainer` block entities in radius 12 before it consumes pedestal items. The failed-drain instability growth now follows the old recipe-instability based chance and clamps at 25.
- If a required ingredient is missing after the recipe essentia has been fully drained, the matrix now keeps the original recipe aspect set and periodically adds one random required aspect back into `recipeEssentia`, matching the old zero-amount `AspectList` behavior. If essentia is unavailable, the matrix tries the other required aspects before waiting and can still raise instability.
- Active infusion crafting state now saves and reloads the catalyst, remaining ingredients, output, essentia, recipe id, player name, delay counters, and recipe instability like old `TileInfusionMatrix` did.
- Pedestal ingredients now use the old delayed absorption behavior: the matrix starts the absorption cycle, waits several craft ticks, then removes the item or leaves its crafting remainder.
- Successful essentia drain now emits a color-matched particle trail from the drained source to the matrix using the old `PacketFXEssentiaSource` timing model: the server sends a source FX payload, the client keeps a 15 tick entry, refreshes duplicate trails, spawns the trail each client tick, and shrinks the effect over the last 5 ticks. The old mechanic drained the jar and reduced `recipeEssentia` immediately; the trail was visual-only, so no delayed matrix fill was added.
- Surrounding scan now includes old-style stabilizer symmetry:
  - pedestals and their held items contribute instability when unpaired.
  - mirrored pedestals/items reduce that penalty.
  - candles and skulls are treated as stabilizers and apply the old `+0.1 / -0.2` mirrored stabilizer rule.
- Crafting cycles now follow the old disruption order:
  - the central catalyst is checked first.
  - if the catalyst is missing/wrong, an instability event can fire before the craft fails.
  - if instability rolls during a valid craft, the event fires and the cycle stops without progressing.
  - missing side ingredients no longer immediately fail the recipe; they can add a random remaining essentia requirement and increase instability, matching the old behavior.
- Initial old instability events are implemented:
  - random pedestal item ejection/removal.
  - pedestal ejection/removal now emits the old pedestal block-event sparkle burst above the affected pedestal, alongside the matrix-to-pedestal zap.
  - pedestal explosions.
  - matrix-to-target zap FX and magic damage. The zap now uses a modern port of the old `FXLightningBolt` path with the old `p_large.png` / `p_small.png` two-pass purple bolt textures, replacing the temporary vanilla electric-spark line.
  - harmful player/entity effects.
  - player warp through the ported permanent/sticky/temporary warp storage.
- Old `PacketFXInfusionSource` behavior has a modern payload:
  - pedestal item absorption starts a 60 tick client effect.
  - the server removes the ingredient only after the old 5 craft-tick countdown.
  - the client renders old-style `FXBoreParticles`/`FXBoreSparkle` equivalents from the source pedestal toward the matrix: 1/3 purple sparkle pass, otherwise two 1/4 texture fragments from the item or block particle icon, using the old lifetime, pull strength, shrink, and speed clamp behavior.
- Client crafting loop now emits old `FXBlockRunes`-style rune particles while crafting, using the old call from `TileInfusionMatrix.doEffects`: position `matrix.y - 2`, color range `0.5..0.7 / 0.1 / 0.7..1.0`, lifetime `3 * 25`, gravity `-0.03`, random rune index `224..239`, and the old `particles.png` rune UVs.
- Active unstable crafting now emits the old random background `nodeBolt` around the matrix when `rand.nextInt(200) <= instability`, using the same 2 block random offset volume and the modern port of old `FXLightningBolt`.
- Successful infusion completion now sends the old pedestal block event `12` equivalent on the central pedestal: nested `particleCount(10)` / `blockSparkle(..., -9999, 2)` random-color sparkle bursts above the pedestal.
- Pedestal instability sparkle event `11` now follows the old nested particle count behavior instead of the earlier single-pass approximation.
- Infusion instability now uses real ported flux/warp hooks instead of the previous stand-ins:
  - ejection type `1/3` places `flux_goo` level `7` above the affected pedestal and plays the old `game.neutral.swim` equivalent.
  - ejection type `2/4` places `flux_gas` level `7` above the affected pedestal and plays the old fizz sound path.
  - `flux_goo`/`flux_gas` now use the old finite-fluid volume model: blockstate level maps to old metadata `0..7`, `metadata + 1` is the quanta volume, goo flows downward, gas flows upward, and horizontal neighbours are equalized like old Forge `BlockFluidFinite`.
  - Goo keeps the old decay/emission hooks: low/full pools can consume themselves on open air, otherwise periodically lose metadata and may create level `0` flux gas above. Thaumic slime and taint-fibre conversion are still waiting on those old subsystems.
  - warp instability chooses a player within the old 10 block area, then applies `sticky +1` with 25% chance or temporary `1..5` warp otherwise.
  - player warp is stored as permanent/sticky/temporary/counter data, copied on death and synced to the client.
- Not yet exact: downstream full warp event scheduling and the complete taint/flux ecology are broader old subsystems. The infusion-facing placement/storage hooks are now present.
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
Next infusion work should expand the original recipe/research set and replace
the remaining direct-radius essentia lookup with the exact old tube-aware
source pathing. Continue expanding item aspect data from old `ConfigAspects`
as new ported items and blocks appear. The detailed aspect/essentia port plan
is tracked in `docs/essentia_aspect_port_plan.md`.

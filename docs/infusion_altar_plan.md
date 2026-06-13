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
- [x] Add Thaumonomicon research pages for currently ported infusion recipes.
- [x] Add ResearchRegistry entries for every currently data-driven recipe research key.
- [x] Port the first broad old infusion recipe batch for currently registered items/blocks.

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
- During crafting the matrix now drains one required essentia at a time from nearby `IAspectSource` block entities in radius 12 before it consumes pedestal items, matching old `EssentiaHandler.drainEssentia(..., ForgeDirection.UNKNOWN, 12)`. Tubes do not get path-found by the matrix directly; they fill jars/buffers through their own old-style transport ticks, and the matrix drains those source containers. The failed-drain instability growth now follows the old recipe-instability based chance and clamps at 25.
- If a required ingredient is missing after the recipe essentia has been fully drained, the matrix now keeps the original recipe aspect set and periodically adds one random required aspect back into `recipeEssentia`, matching the old zero-amount `AspectList` behavior. If essentia is unavailable, the matrix tries the other required aspects before waiting and can still raise instability.
- `AspectList.reduce()` now preserves an aspect entry at `0` like the old API, so drained infusion requirements still retain their aspect keys for missing-ingredient penalties and reload-safe recovery.
- Active infusion crafting state now saves and reloads the catalyst, remaining ingredients, output, essentia, recipe id, player name, delay counters, and recipe instability like old `TileInfusionMatrix` did.
- Pedestal ingredients now use the old delayed absorption behavior: the matrix starts the absorption cycle, waits several craft ticks, then removes the item or leaves its crafting remainder.
- Successful essentia drain now emits a color-matched particle trail from the drained source to the matrix using the old `PacketFXEssentiaSource` timing model: the server sends a source FX payload, the client keeps a 15 tick entry, refreshes duplicate trails, spawns the trail each client tick, uses the old collision-enabled `FXEssentiaTrail` motion, and shrinks the effect over the last 5 ticks. The old mechanic drained the jar and reduced `recipeEssentia` immediately; the trail was visual-only, so no delayed matrix fill was added.
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
  - the client renders old-style `FXBoreParticles`/`FXBoreSparkle` equivalents from the source pedestal toward the matrix: 1/3 purple sparkle pass, otherwise two 1/4 texture fragments from the item or block particle icon, using the old alpha, lifetime, pull strength, shrink, and speed clamp behavior.
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
- Initial infusion recipes exist for the currently ported infusion items and blocks:
  - `silver_wand_cap_infusion`: old `WandCapSilver`, gated by `CAP_silver`, catalyst inert silver cap, two salis mundus, instability `4`, essentia `potentia 8` / `auram 4` from the old silver cap craft cost `4`.
  - `thaumium_wand_cap_infusion`: old `WandCapThaumium`, gated by `CAP_thaumium`, catalyst inert thaumium cap, three salis mundus, instability `5`, essentia `potentia 12` / `auram 6`.
  - `void_wand_cap_infusion`: old `WandCapVoid`, gated by `CAP_void`, catalyst inert void cap, four salis mundus, instability `8`, essentia `potentia 18` / `vacuos 18` / `alienis 18` / `auram 18`.
  - `brain_in_a_jar_infusion`: old `JarBrain`, gated by `JARBRAIN`, catalyst warded jar, zombie brain / spider eye / water bucket / spider eye, instability `4`, essentia `cognitio 10` / `sensus 10` / `exanimis 20`.
  - `advanced_node_stabilizer`: old `NodeStabilizerAdv`, gated by `NODESTABILIZERADV`, catalyst node stabilizer, nitor / redstone block / alumentum / redstone block mirrored, instability `10`, essentia `auram 32` / `praecantatio 16` / `ordo 16` / `potentia 16`.
  - `wand_recharge_pedestal`: old `WandPed`, gated by `WANDPED`, catalyst arcane pedestal, gold ingot / diamond / primordial pearl / diamond, instability `3`, essentia `auram 10` / `praecantatio 15` / `permutatio 15`.
  - `compound_recharge_focus`: old `WandPedFocus`, gated by `WANDPEDFOC`, catalyst comparator, earth shards and vis filters mirrored, instability `4`, essentia `ordo 10` / `praecantatio 15` / `permutatio 10`.
  - `obsidian_wand_rod`: old `WandRodObsidian`, gated by `ROD_obsidian`, catalyst obsidian, balanced shard / earth shard, instability `3`, essentia `terra 12` / `praecantatio 6` / `tenebrae 6`.
  - `ice_wand_rod`: old `WandRodIce`, gated by `ROD_ice`, catalyst ice, balanced shard / water shard, instability `3`, essentia `aqua 12` / `praecantatio 6` / `gelum 6`.
  - `quartz_wand_rod`: old `WandRodQuartz`, gated by `ROD_quartz`, catalyst quartz block, balanced shard / order shard, instability `3`, essentia `ordo 12` / `praecantatio 6` / `vitreus 6`.
  - `reed_wand_rod`: old `WandRodReed`, gated by `ROD_reed`, catalyst sugar cane, balanced shard / air shard, instability `3`, essentia `aer 12` / `praecantatio 6` / `motus 6`.
  - `blaze_wand_rod`: old `WandRodBlaze`, gated by `ROD_blaze`, catalyst blaze rod, balanced shard / fire shard, instability `3`, essentia `ignis 12` / `praecantatio 6` / `bestia 6`.
  - `bone_wand_rod`: old `WandRodBone`, gated by `ROD_bone`, catalyst bone, balanced shard / entropy shard, instability `3`, essentia `perditio 12` / `praecantatio 6` / `exanimis 6`.
  - `silverwood_wand_rod`: old `WandRodSilverwood`, gated by `ROD_silverwood`, catalyst silverwood log, balanced + all six primal shards, instability `5`, essentia each primal `9` plus `praecantatio 9`.
  - `primal_staff_core`: old `WandRodPrimalStaff`, gated by `ROD_primal_staff`, catalyst silverwood wand rod, two primal charms plus obsidian/ice/quartz/reed/blaze/bone rods, instability `8`, essentia each primal `32` plus `praecantatio 64`.
  - `focus_hellbat`: old `FocusHellbat`, gated by `FOCUSHELLBAT`, catalyst magma cream, quartz / fire shard / quartz / air shard / quartz / entropy shard, instability `3`, essentia `ignis 25` / `aer 15` / `bestia 15` / `perditio 25`.
  - `focus_portable_hole`: old `FocusPortableHole`, gated by `FOCUSPORTABLEHOLE`, catalyst ender pearl, quartz / earth shard / quartz / air shard / quartz / entropy shard, instability `3`, essentia `iter 25` / `alienis 10` / `permutatio 10` / `perditio 25`.
  - `focus_warding`: old `FocusWarding`, gated by `FOCUSWARDING`, catalyst nether star, quicksilver / earth shard / quartz / order shard mirrored, instability `4`, essentia `terra 25` / `tutamen 25` / `ordo 25` / `cognitio 10`.
  - `runic_ring`: old `RunicRing`, gated by `RUNICARMOR`, catalyst bauble ring, primal charm / amber / enchanted fabric / nitor / scribing tools, instability `3`, essentia `tutamen 10` / `praecantatio 25` / `potentia 25`.
  - `runic_amulet`: old `RunicAmulet`, gated by `RUNICARMOR`, catalyst bauble amulet, primal charm / amber / enchanted fabric / nitor / nitor / scribing tools, instability `4`, essentia `tutamen 20` / `praecantatio 35` / `potentia 35`.
  - `runic_girdle`: old `RunicGirdle`, gated by `RUNICARMOR`, catalyst bauble belt, primal charm / amber / enchanted fabric / nitor / nitor / nitor / scribing tools, instability `4`, essentia `tutamen 30` / `praecantatio 50` / `potentia 50`.
  - `runic_amulet_emergency`: old `RunicAmuletEmergency`, gated by `RUNICEMERGENCY`, catalyst runic amulet, balanced shard / earth shards and old potion damage `8233` mapped through modern `minecraft:strong_strength`, instability `7`, essentia `tutamen 20` / `praecantatio 35` / `terra 32` / `vacuos 32`.
  - `runic_ring_charged`: old `RunicRingCharged`, gated by `RUNICCHARGED`, catalyst runic ring, balanced shard / fire shards and old potion damage `8226` mapped through modern `minecraft:strong_swiftness`, instability `6`, essentia `tutamen 16` / `praecantatio 16` / `potentia 64`.
  - `runic_ring_regen`: old `RunicRingHealing`, gated by `RUNICHEALING`, catalyst runic ring, balanced shard / water shards and old potion damage `8257` mapped through modern `minecraft:long_regeneration`, instability `6`, essentia `tutamen 16` / `praecantatio 16` / `aqua 32` / `sano 32`.
  - `runic_girdle_kinetic`: old `RunicGirdleKinetic`, gated by `RUNICKINETIC`, catalyst runic girdle, balanced shard / air shards and old splash potion damages `16428` / `24620` mapped to the same modern `minecraft:strong_harming` splash potion, instability `7`, essentia `tutamen 33` / `praecantatio 55` / `aer 64`.
  - `vis_amulet`: old `VisAmulet`, gated by `VISAMULET`, catalyst bauble amulet, primal charms and balanced shards mirrored, instability `6`, essentia `auram 24` / `potentia 64` / `praecantatio 64` / `vacuos 24`.
  - `boots_traveller`: old `BootsTraveller`, gated by `BOOTSTRAVELLER`, catalyst leather boots, air shards / enchanted fabric / feather / fish tag, instability `1`, essentia `volatus 25` / `iter 25`.
  - `hover_girdle`: old `HoverGirdle`, gated by `HOVERGIRDLE`, catalyst bauble belt, air shard / feather / gold ingot / order shard / feather / gold ingot, instability `8`, essentia `volatus 16` / `potentia 32` / `aer 32` / `iter 16`. The original research parent was `HOVERHARNESS`; the new tree currently gates it through `BOOTSTRAVELLER` because the harness item/system is not registered yet.
  - `magic_mirror`: old `Mirror`, gated by `MIRROR`, catalyst mirrored glass, gold ingots and ender pearl, instability `1`, essentia `iter 8` / `tenebrae 8` / `permutatio 8`.
  - `essentia_mirror`: old `MirrorEssentia`, gated by `MIRRORESSENTIA`, catalyst mirrored glass, iron ingots and ender pearl, instability `2`, essentia `iter 8` / `aqua 8` / `permutatio 8`.
  - `lamp_growth`: old `LampGrowth`, gated by `LAMPGROWTH`, catalyst arcane lamp, gold / bone meal / earth shard mirrored, instability `4`, essentia `herba 16` / `lux 8` / `victus 16`.
  - `lamp_fertility`: old `LampFertility`, gated by `LAMPFERTILITY`, catalyst arcane lamp, gold / wheat / fire shard / gold / carrot / fire shard, instability `4`, essentia `bestia 16` / `victus 16` / `lux 8`.
  - `elemental_axe`: old `ElementalAxe`, gated by `ELEMENTALAXE`, catalyst thaumium axe, water shards / diamond / greatwood log, instability `1`, essentia `aqua 16` / `arbor 8`.
  - `elemental_pickaxe`: old `ElementalPick`, gated by `ELEMENTALPICK`, catalyst thaumium pickaxe, fire shards / diamond / greatwood log, instability `1`, essentia `ignis 8` / `perfodio 8` / `sensus 8`.
  - `elemental_shovel`: old `ElementalShovel`, gated by `ELEMENTALSHOVEL`, catalyst thaumium shovel, earth shards / diamond / greatwood log, instability `1`, essentia `terra 16` / `fabrico 8`.
  - `elemental_hoe`: old `ElementalHoe`, gated by `ELEMENTALHOE`, catalyst thaumium hoe, order shard / entropy shard / diamond / greatwood log, instability `1`, essentia `meto 8` / `herba 8` / `terra 8`.
  - `fortress_helmet`, `fortress_chestplate`, `fortress_leggings`: old `ThaumiumFortressHelm/Chest/Legs`, gated by `ARMORFORTRESS`, catalysts thaumium armor pieces, old component layout and exact old essentia costs.
  - `helm_goggles`: old `HelmGoggles`, gated by `HELMGOGGLES`, catalyst fortress helmet, slime ball + goggles, instability `5`, essentia `sensus 32` / `auram 16` / `tutamen 16`. The result uses the new `thaumcraft:fortress_goggles` data component to mirror the old NBT `{goggles:1}` upgrade.
  - `mask_grinning_devil`, `mask_angry_ghost`, `mask_sipping_fiend`: old fortress helmet faceplate upgrades, gated by `MASKGRINNINGDEVIL`, `MASKANGRYGHOST`, and `MASKSIPPINGFIEND`. They use the old catalyst, mirrored component layouts, instability `8`, old essentia costs, old research icons, and the new `thaumcraft:fortress_mask` data component to mirror old `mask` NBT. Infusion output preserves the catalyst stack and overlays the upgrade component so goggles and masks can coexist like the old NBT upgrades.
  - `void_robe_helmet`, `void_robe_chestplate`, `void_robe_leggings`: old `VoidRobeHelm/Chest/Legs`, gated by `ARMORVOIDFORTRESS`, catalysts void armor pieces, old component layout and exact old essentia costs.
  - `primal_crusher`: old `PrimalCrusher`, gated by `PRIMALCRUSHER`, catalyst primordial pearl, primal charms plus void/elemental pickaxe and shovel pairs, instability `6`, essentia `perfodio 24` / `instrumentum 24` / `perditio 16` / `vacuos 16` / `telum 16` / `alienis 16` / `lucrum 16`.
  These recipes use the completed server-side drain, ingredient absorption, instability, and completion path.
- The old wand/staff and first broad infusion research branches are now registered for the transferred recipes:
  `BASICTHAUMATURGY`, `CAP_gold`, `CAP_silver`, `CAP_thaumium`, `CAP_void`,
  `ROD_greatwood`, all six special wand rods, `ROD_silverwood`, the matching
  staff core researches, `FOCUSFIRE`, `FOCUSFROST`, `FOCUSSHOCK`,
  `FOCUSEXCAVATION`, `FOCUSTRADE`, `FOCUSHELLBAT`, `FOCUSWARDING`,
  `FOCUSPORTABLEHOLE`, `FOCUSPRIMAL`, `ELDRITCHMINOR`, `ELDRITCHMAJOR`,
  `VOIDMETAL`, `ROD_primal_staff`, `RUNICARMOR`, `RUNICCHARGED`,
  `RUNICHEALING`, `RUNICKINETIC`, `RUNICEMERGENCY`, `VISAMULET`,
  `THAUMOMETER`, `GOGGLES`, `BOOTSTRAVELLER`, `HOVERGIRDLE`, `MIRROR`, `MIRRORESSENTIA`,
  `ARCANELAMP`, `LAMPGROWTH`, `LAMPFERTILITY`, all currently registered
  `ELEMENTAL*` tools, `INFUSIONENCHANTMENT`, `ARMORFORTRESS`, and
  `HELMGOGGLES`, the three fortress faceplate researches,
  `PRIMPEARL`, `PRIMALCRUSHER`, and
  `ARMORVOIDFORTRESS`.
  The special staff cores remain arcane-worktable recipes like old `ConfigRecipes`;
  only the old infusion rod/core recipes were moved to `thaumcraft:infusion`.
- All data-driven recipe `research` keys now have matching `ResearchRegistry`
  entries, including base alchemy support keys needed by infusion dependencies:
  `ALUMENTUM`, `NITOR`, `TALLOW`, `THAUMIUM`, `TUBES`, `CENTRIFUGE`, and
  `JARVOID`.
- The old arcane-worktable focus recipes that unlock the infusion focus branch
  are also data-driven now: `focus_fire`, `focus_frost`, `focus_shock`,
  `focus_trade`, `focus_excavation`, and `focus_primal`.
- The old `GOGGLES` arcane-worktable prerequisite has been restored with the
  original pattern and primal vis costs so the fortress helmet goggles infusion
  unlock path matches the old `THAUMOMETER -> GOGGLES -> HELMGOGGLES` chain.
- The old `ENCHFABRIC` arcane-worktable prerequisite chain has been restored so
  the infusion recipes that consume enchanted fabric are craftable from the same
  old unlock path:
  - `enchanted_fabric`: old `EnchantedFabric`, string around any wool, primal
    vis `1` each.
  - `robe_chestplate`: old `RobeChest`, enchanted fabric pattern, `Aer 5`.
  - `robe_leggings`: old `RobeLegs`, enchanted fabric pattern, `Aqua 5`.
  - `robe_boots`: old `RobeBoots`, enchanted fabric pattern, `Terra 3`.
  These recipes are attached to the `ENCHFABRIC` research entry before the
  existing explanatory page, matching their role as prerequisites for runic,
  traveller, and void robe infusion recipes.
- The old `ELEMENTALSWORD` infusion tail has been restored as `elemental_sword`:
  same thaumium sword catalyst, two air shards, diamond, greatwood log,
  instability `1`, and `Aer/Motus/Potentia` essentia costs from old
  `ConfigRecipes`.
- The old `SANITYCHECK` infusion tail has been restored as `sanity_checker`:
  same thaumometer catalyst, balanced shard, zombie brain, diamond,
  instability `4`, and `Cognitio/Sensus/Alienis` essentia costs from old
  `ConfigRecipes`. The item itself matches the old item-level behavior: a
  non-stacking uncommon relic item; the old sanity HUD is a separate client
  overlay concern.
- The old `SINSTONE` infusion tail has been restored as `sinister_lodestone`:
  flint catalyst, quicksilver, order shard, knowledge fragment, entropy shard,
  instability `5`, and `Sensus/Tenebrae/Alienis/Auram` essentia costs from old
  `ConfigRecipes`. The item matches the old rare one-stack behavior, carries
  `1` item warp, and uses a client-side dark-node sight cache to switch to the
  old active icon while a dark/sinister node is inside the original 256-block
  view cone.
- The old `MIRRORHAND` infusion tail has been restored as `hand_mirror`:
  `magic_mirror` catalyst, stick, compass, map, instability `5`, and
  `Instrumentum/Iter` essentia costs from old `ConfigRecipes`. The item uses the
  old uncommon one-stack behavior, the old `mirrorhand` texture, the old
  `guihandmirror` one-slot interface, links to a placed magic mirror with the
  old jar sound and chat messages, and sends inserted stacks out of the linked
  mirror with the old portal sound and outward item motion.
- Infusion start/fail sounds now follow the old matrix path: `craftstart` is played only when a recipe starts, `craftfail` is played on invalid/cancelled crafting, and successful completion relies on the old pedestal sparkle event instead of replaying `craftstart`.
- Thaumonomicon research entries now include the old-style `INFUSION` entry with matrix and pedestal pages, plus the currently ported infusion recipe pages for wand caps, wand rods, primal staff core, brain jar, advanced node stabilizer, wand recharge pedestal, and compound recharge focus. The modern book screen renders infusion recipe pages with the old structure: output, catalyst, circular components, instability text, and essentia costs.
- Infusion enchantment is now a real data-driven recipe type:
  `thaumcraft:infusion_enchantment`. Runtime matching follows the old
  `InfusionEnchantmentRecipe` path: normal infusion is checked first, then
  enchantment infusion; the recipe rejects max-level and incompatible
  enchantments, consumes nearby player XP before essentia, scales essentia
  costs by current enchantment level and other enchantments, then consumes
  components and applies the next enchantment level.
- The old vanilla infusion enchantment recipe set is ported as JSON recipes:
  protection, fire protection, blast protection, projectile protection,
  feather falling, respiration, aqua affinity, thorns, sharpness, smite,
  bane of arthropods, knockback, fire aspect, looting, efficiency, silk touch,
  unbreaking, fortune, power, punch, flame, and infinity. Component layouts,
  instability, essentia, and XP formula are taken from old `ConfigRecipes`.
- Infusion enchantment display is connected to both the Thaumonomicon
  `INFUSIONENCHANTMENT` entry and a separate JEI category. The XP drain FX now
  uses an entity-source variant with the old green `FXBoreSparkle` color path,
  while pedestal item absorption keeps the old block/item-source particle path.
- The old `Repair` and `Haste` infusion enchantments are intentionally not
  registered yet because the corresponding custom enchantments are not ported
  in the new branch. They should be added together with their actual enchantment
  behavior, not as dummy recipes.

## Old Thaumcraft Behavior Notes
- Players crafted `Infusion Matrix` and `Arcane Pedestal`; they did not craft/place `Infusion Pillar` directly.
- Wand activation transformed the altar structure into internal stone-device metadata:
  - matrix stayed metadata `2`
  - pillar bottom used metadata `3` / `TileInfusionPillar`
  - pillar top used metadata `4`
- Breaking old pillar metadata dropped source decorative blocks, not a pillar item.

## Next Step
Next infusion work should expand the original recipe/research set as more
items and blocks are ported. The remaining old infusion recipes are blocked by
missing objects or missing broader subsystems in the new branch, including
advanced golems and golem cores, arcane bore, travel trunk,
essentia reservoir, hover harness,
eldritch eye/oculus, and full infusion enchantment
recipes for custom `Repair`/`Haste` enchantments. The matrix essentia lookup is intentionally the old direct
radius-12 `IAspectSource` scan; tubes fill jars and buffers through their own
transport ticks, and the matrix drains those nearby sources. Continue expanding
item aspect data from old `ConfigAspects` as new ported items and blocks appear.
The detailed aspect/essentia port plan is tracked in
`docs/essentia_aspect_port_plan.md`.

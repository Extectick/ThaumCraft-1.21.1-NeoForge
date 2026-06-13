# Aura Node Port Plan

## Primary old-source references

- `thaumcraft/api/nodes/INode.java`
- `thaumcraft/api/nodes/NodeType.java`
- `thaumcraft/api/nodes/NodeModifier.java`
- `thaumcraft/common/tiles/TileNode.java`
- `thaumcraft/client/renderers/tile/TileNodeRenderer.java`
- `thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java`
- `thaumcraft/common/lib/research/ScanManager.java`

## Original behavior summary

- A natural node stores current and base aspect amounts, a persistent id, node type, and optional modifier.
- Types: normal, unstable, dark, tainted, hungry, pure.
- Modifiers: bright, pale, fading.
- Default recharge interval is 600 ticks; bright is 400, pale is 900, and fading does not recharge.
- Empty aspects lose base capacity every 1200 ticks and can disappear. Repeated degradation can weaken the modifier.
- Stronger nearby natural nodes can drain weaker nodes. Hungry nodes pull entities and destroy softer blocks.
- Node rendering uses `textures/misc/nodes.png`: a 32 by 32 frame grid. Node layers use strips 0-6 and the destruction burst uses strip 31.
- A thaumometer reveals full node rendering and scans a node as a phenomenon.

## Implementation phases

### Phase 1 - Natural node foundation

- [x] API types and node interface.
- [x] Invisible, non-colliding natural node block and synced block entity.
- [x] Persistent current/base aspects, id, type, and modifier.
- [x] Recharge, degradation, unstable loss, nearby-node drain, and hungry-node behavior.
- [x] Original animated node texture renderer.
- [x] Original creative-only random node placement item.
- [x] Original hit sparkle, destruction burst, destruction sound, and inter-node drain zap.
- [x] Thaumometer target overlay and phenomenon scan aspects.
- [x] Deterministic operator command for testing node types and modifiers.

### Phase 2 - Exact natural generation

- [x] Port biome aura strength and biome-tag mapping through NeoForge biome dictionary equivalents.
- [x] Port nearby block/environment contribution rules.
- [x] Port exact type/modifier rarity configuration.
- [x] Port natural chunk-generation placement in Overworld and Nether.
- [ ] Port structure-specific nodes.

Natural generation now uses the original `1 / nodeRarity` chunk roll, random
in-chunk coordinates, the old Y=5 upward air search, the original vertical
offset roll, and the existing exact type/modifier/environment generator.
Structure-owned nodes remain separate because their placement is tied to the
structures that create them.

### Phase 3 - Wand interaction

- [x] Port node tapping and vis transfer into wands.
- [x] Port research gates, drain rates, preservation, and tapper behavior.
- [x] Port the original drain visual effect. The old tapping path does not
  play a dedicated drain sound, so no replacement sound was invented.

#### Completed implementation stage: node tapping

Node tapping was completed before natural chunk generation, so newly generated
nodes already expose their primary wand interaction.

Old-source references:

- `thaumcraft/common/tiles/TileNode.java`
  - `onWandRightClick`
  - `onUsingWandTick`
  - `onWandStoppedUsing`
  - `chooseRandomFilteredFromSource`
- `thaumcraft/common/items/wands/ItemWandCasting.java`
- `thaumcraft/client/renderers/tile/TileNodeRenderer.java`
- `thaumcraft/common/lib/research/ResearchManager.java`

Implementation order:

1. **Shared target and held-use state** - completed
   - Start draining only while a casting wand is held on the aura node.
   - Keep the original continuous-use interaction instead of draining on a
     single click.
   - Stop immediately when the player releases use, changes item, exceeds
     reach, looks away, or the raycast no longer resolves to the same node.
   - Reuse one server-authoritative target calculation for interaction and FX.

2. **Exact vis selection and transfer** - completed
   - Run the drain attempt every 5 use ticks.
   - Base tap amount is `1` vis.
   - `NODETAPPER1` raises the amount to `2`.
   - `NODETAPPER2` raises the amount to `3`.
   - Build the candidate set from node aspects for which the wand has room.
   - Select one candidate randomly, exactly as the old node did.
   - Clamp transfer by the node amount and the wand's remaining capacity.
   - Store wand vis in centivis consistently: one old vis equals `100`
     centivis.
   - Remove only the amount that was actually accepted by the wand.

3. **Node preservation** - completed
   - `NODEPRESERVE` leaves at least one point of the selected aspect.
   - Sneaking disables preservation and permits full depletion.
   - Preservation does not work for the original low-tier combination:
     ordinary wood rod or iron caps.
   - Rod/cap checks must use the existing `WandParts` data instead of item-id
     assumptions.

4. **Research integration** - completed
   - Read completion state for `NODETAPPER1`, `NODETAPPER2`, and
     `NODEPRESERVE` from the current player research attachment.
   - Do not grant the bonuses merely because the entries are visible.
   - Preserve the original parent relationships already registered in
     `ResearchRegistry`.

5. **Client effects and sound** - completed
   - Synchronize active drainer, hit position, and current aspect color.
   - Port the old color-smoothed beam/stream from the node to the wand.
   - Keep the last successful aspect color and interpolate toward it rather
     than snapping each frame.
   - Stop the effect immediately when draining fails or use ends.
   - Old-source verification found no dedicated sound call in
     `TileNode.onUsingWandTick`, `onWandRightClick`, or
     `onWandStoppedUsing`; parity therefore means keeping this interaction
     silent.

6. **Persistence and synchronization** - completed
   - Node current aspects remain server authoritative.
   - Mark and sync the node only after a successful transfer.
   - Draining state is transient and must not resume after world reload.
   - Wand component changes must synchronize to the client inventory without
     replacing the stack or losing focus/rod/cap data.

7. **Validation matrix**
   - Empty wand and partially filled wand.
   - Full wand and one full primal channel.
   - Node containing one aspect and several aspects.
   - Transfer rates of `1`, `2`, and `3`.
   - Preservation with normal use and with sneaking.
   - Wooden/iron starter wand ignoring preservation.
   - Looking away, walking out of reach, switching slots, dropping the wand,
     disconnecting, and reloading the world during use.
   - Two players attempting to drain the same node.
   - Depleted aspect degradation and later recharge remain unchanged.

Completion criteria:

- The wand receives exactly the same amount per 5 ticks as Thaumcraft 4.
- The node loses exactly the amount accepted by the wand.
- Research and preservation produce the same edge-case behavior as the old
  `TileNode`.
- Beam color, sound cadence, interruption, and multiplayer synchronization are
  visibly stable.

### Phase 4 - Full node world effects

- [x] Port hungry-node pull, damage, entity aspect absorption, vis replenishment,
  base-capacity growth, and soft-block destruction.
- [x] Port the original hungry-node block-consumption particles.
- [ ] Port pure, dark, and tainted biome mutation after biome systems exist.
- [ ] Port dark-node giant brainy zombie spawning after that entity exists.
- [ ] Port unstable-node aspect-orb discharge after aspect orbs exist.
- [x] Port stabilizer lock effects on unstable and fading nodes with the node
  machine stage.
- [x] Port inter-node drain visual effects and natural-node sounds.

### Phase 5 - Node capture

- [x] Port node-in-a-jar multiblock and capture result.
- [x] Preserve node id, type, modifier, and the captured aspect state.
- [x] Port capture instability and break behavior.

#### Completed implementation stage: node in a jar

This stage completes the portable-node lifecycle before stabilizers,
transducers, and energized vis networks are introduced.

Old-source references:

- `thaumcraft/common/items/wands/WandManager.java`
  - `createNodeJar`
  - `fitNodeJar`
  - `replaceNodeJar`
- `thaumcraft/common/tiles/TileJarNode.java`
- `thaumcraft/common/blocks/ItemJarNode.java`
- `thaumcraft/common/blocks/BlockJar.java`
- `thaumcraft/client/renderers/tile/TileJarRenderer.java`
- `thaumcraft/client/renderers/tile/ItemJarNodeRenderer.java`
- `thaumcraft/common/config/ConfigRecipes.java`
- `thaumcraft/common/config/ConfigResearch.java`

Implementation order:

1. **Portable node data**
   - [x] Add one network-synchronized item data component containing node id,
     type, optional modifier, current aspects, and base aspects.
   - [x] Reuse the same serializable value for the placed jar block entity.
   - [x] Match the old capture path by copying the node's current aspects into
     both the current and base lists when it is sealed.
   - [x] Keep the item non-stackable.

2. **Node jar block entity**
   - [x] Replace the current `SimpleJarBlock` registration for
     `node_in_a_jar` with a dedicated block and block entity.
   - [x] A captured node is frozen: no recharge, degradation, node bullying,
     hungry behavior, biome mutation, or wand tapping.
   - [x] Synchronize all node data for rendering, tooltips, save/reload, block
     picking, and drops.
   - [x] Use the existing jar collision and placement behavior unless old block
     bounds prove different during visual validation.

3. **Exact multiblock detection**
   - [x] Search the volume around the clicked glass block exactly as old
     `createNodeJar` did.
   - [x] Match a `3 x 4 x 3` structure:
     - top layer: nine blocks accepted by the common wooden-slab tag;
     - lower three layers: glass;
     - aura node: center of the middle glass layer.
   - [x] Reject captured nodes and any incomplete or substituted structure.
   - [x] Keep detection server authoritative and make all replacement operations
     atomic.

4. **Activation and vis cost**
   - [x] Route glass interaction through the shared wand interaction system.
   - [x] Require completed `NODEJAR` research.
   - [x] Consume `70` old vis from each primal channel using
     the existing crafting discount path. In the current centivis storage this
     means a base cost of `7000` centivis per primal before discounts.
   - [x] Do not consume vis or alter blocks when validation fails.

5. **Capture transformation**
   - [x] Snapshot node id, type, modifier, and current aspects before
     removing the natural node.
   - [x] Apply the original `75%` modifier damage roll:
     - no modifier becomes `PALE`;
     - `BRIGHT` becomes no modifier;
     - `PALE` becomes `FADING`;
     - `FADING` remains `FADING`.
   - [x] Remove all 35 construction blocks and replace only the node position with
     the populated node jar.
   - [x] Play the original wand sound, block sparkles, and one-second shrink
     animation.

6. **Breaking, item form, and placement**
   - [x] Breaking the jar drops exactly one populated node-jar item.
   - [x] The drop keeps the complete node state and does not duplicate ordinary
     jar drops.
   - [x] Placing the item restores the same captured node state.
   - [x] Creative pick-block and middle-click preserve the component as well.
   - [x] Tooltips show localized type, modifier, and only those aspects the player
     has discovered, matching the old item behavior.

7. **Releasing the node**
   - [x] Wand right-click on a placed node jar replaces it with a natural aura
     node at the same position.
   - [x] Restore id, type, modifier, current aspects, and base aspects.
   - [x] Destroy the jar without dropping it.
   - [x] Play the original glass break particles, glass sound with randomized
     pitch, and player swing animation.
   - [x] Releasing requires no additional vis cost in the old implementation.

8. **Rendering parity**
   - [x] Render the actual captured node inside both the placed jar and item,
     using its aspects, type, and modifier rather than a generic brine cube.
   - [x] Port the old three-orientation item-node rendering so the layered node
     remains visible from inventory and held-item angles.
   - [x] Port the capture shrink from scale `3.0` down to `1.0` over one second.
   - Verify translucent ordering from every side and through the thaumometer.

9. **Validation matrix**
   - Every node type with no modifier, `BRIGHT`, `PALE`, and `FADING`.
   - Successful and failed `75%` damage rolls with deterministic test hooks.
   - Partially drained node where current and base amounts differ.
   - Insufficient vis, missing research, malformed structure, and wrong slab
     or glass blocks.
   - Capture, break, inventory transport, place, save/reload, and release.
   - Survival and creative drops, pick-block, multiplayer observation, and
     simultaneous activation attempts.

Completion criteria:

- The multiblock, cost, modifier damage, sounds, particles, and shrink timing
  match Thaumcraft 4.
- No node data changes across capture, item transport, save/reload, placement,
  and release except the original modifier damage roll.
- A jarred node is entirely inert and cannot be drained.
- The block and item show the captured node's real visual layers without
  translucent flicker or generic placeholder contents.

### Phase 6 - Node machines and energized aura

Detailed route: `docs/node_machines_energized_plan.md`.

- [x] Node stabilizer and advanced stabilizer.
- [x] Node transducer and full energized-node conversion.
- [x] Energized-node persistent state and conversion helpers.
- [ ] Vis relays and energized vis network.
- [x] Node bullying and stabilizer lock state.
- [x] Machine-specific rendering for transducer and energized node.
- [ ] Machine-specific rendering for relays.

#### Completed implementation stage: node stabilizers

- [x] Ordinary lock blocks both outgoing and incoming node drain, doubles
  recharge time, prevents unstable vis loss, and retains the original rare
  unstable/fading recovery rolls.
- [x] Advanced lock protects its node while still allowing it to drain weaker
  nodes, uses the original improved recovery rolls, and multiplies recharge
  time by twenty.
- [x] A redstone signal at the stabilizer disables every lock effect.
- [x] Port the original `lock` and four animated `piston` OBJ groups, the
  37-tick extension, pulsing overlay, white/red node bubble, block item render,
  and original crafting recipes.
- [x] Preserve the original absence of a looping operational sound; placement,
  breaking, and stepping continue to use the stone-device sound type.

### Phase 7 - Validation

- [x] Compare all type/modifier render combinations with the old client.
- [ ] Validate save/reload and client/server synchronization.
- [ ] Validate generation distribution across representative biomes.
- [ ] Validate tapping, capture, stabilizers, and energized networks.

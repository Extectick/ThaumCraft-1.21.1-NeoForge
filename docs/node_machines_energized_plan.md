# Node Machines and Energized Aura Port Plan

## Scope

This document covers the next aura-node milestone after natural nodes, wand
tapping, node jars, and node stabilizers:

- Node Stabilizer and Advanced Node Stabilizer parity audit.
- Node Transducer.
- Energized node conversion and reversal.
- Energized node state, rendering, sounds, and persistence.
- First pass of the centivis source/relay network needed by later machines.

The goal is not to invent a new 1.21-only power system. The first target is to
preserve the old Thaumcraft 4 behavior, then expose a small modern API that
later machines can consume.

## Current project state

- `node_stabilizer` and `advanced_node_stabilizer` are registered as
  `NodeStabilizerBlock` with `NodeStabilizerBlockEntity`.
- `AuraNodeBlockEntity` already reads a stabilizer below the node and stores
  `stabilizerLock`:
  - `0`: unlocked.
  - `1`: ordinary stabilizer.
  - `2`: advanced stabilizer.
- Stabilizers already block or change natural-node drain/recharge behavior.
- Stabilizer OBJ rendering and item rendering already exist through
  `NodeStabilizerRenderer`.
- `node_transducer` now has a dedicated block, BlockEntity, renderer, copied old
  converter textures, generated status-tinted overlay assets, server-side
  conversion progress, redstone control, natural/energized switching, sync,
  completion sound, conversion bolts, and the old invalid-stack explosion/flux
  failure path.
- `vis_relay` is currently only a directional visual block. It has no relay
  BlockEntity or centivis network logic.
- `AuraNodeBlockEntity` now has a persistent energized-node mode with
  `auraBase`, `cvBase`, current per-tick `cv`, conversion helpers, and debug
  commands for manual conversion. Energized rendering uses `auraBase` plus the
  old lightning-ring visual; `cvBase` remains the energy-production state.

## Primary old-source references

- `thaumcraft/common/tiles/TileNodeStabilizer.java`
- `thaumcraft/common/tiles/TileNodeConverter.java`
- `thaumcraft/common/tiles/TileNodeEnergized.java`
- `thaumcraft/common/tiles/TileVisRelay.java`
- `thaumcraft/api/visnet/TileVisNode.java`
- `thaumcraft/api/visnet/VisNetHandler.java`
- `thaumcraft/client/renderers/tile/TileNodeRenderer.java`
- `thaumcraft/client/renderers/tile/TileVisRelayRenderer.java`
- `thaumcraft/common/blocks/BlockAiry.java`
- `thaumcraft/common/blocks/BlockMetalDevice.java`

## Original behavior summary

### Stabilizers

- The stabilizer must be directly below a natural node.
- A redstone signal applied to the stabilizer disables the lock.
- Ordinary stabilizer:
  - prevents the protected node from draining other nodes;
  - prevents other nodes from draining the protected node;
  - doubles recharge time;
  - prevents unstable vis loss;
  - can rarely turn an unstable node normal;
  - can rarely improve a fading modifier to pale.
- Advanced stabilizer:
  - protects the node from incoming drain;
  - still allows the protected node to drain weaker nearby nodes;
  - multiplies recharge time by twenty;
  - uses stronger recovery odds through lock value `2`.

### Transducer conversion

Old `TileNodeConverter` uses this vertical stack:

```text
node_transducer
natural aura node
node_stabilizer or advanced_node_stabilizer
```

- The transducer only starts conversion while it receives redstone power.
- Conversion from natural node to energized node requires:
  - natural node directly below the transducer;
  - active stabilizer directly below the node;
  - no redstone signal disabling the stabilizer.
- The transducer progress counter runs from `0` to `1000`.
- While powered and converting, progress increases by `1` each tick.
- During forward conversion, the transducer drains `1` vis from a random
  current aspect on the natural node every tick when any aspect is present.
- At progress `1000`, the natural node is replaced with an energized node.
- The energized node receives:
  - original node id;
  - original node type;
  - original node modifier;
  - original base aspect list as `auraBase`.
- After conversion, a burst effect and `craftfail` sound are played at the node.
- Cutting redstone power makes progress count down.
- When reversing from energized node back to natural node and progress reaches
  `50` or lower, the energized node is replaced with a natural node:
  - same type and modifier;
  - base aspects copied from energized `auraBase`;
  - current aspects immediately drained to zero.
- If an energized node is left without the required active stabilizer while
  progress is above `50`, the old code explodes/damages the node.

### Energized node

- Energized nodes are vis-network sources, not wand-drainable natural nodes.
- They no longer recharge natural vis and cannot be tapped by wands.
- Each server tick, their produced CV store resets to the computed base value.
- Produced CV is calculated from `auraBase` reduced to primals:
  - bright modifier multiplies by `1.2`;
  - pale modifier multiplies by `0.8`;
  - fading modifier multiplies by `0.5`;
  - final amount per primal is `floor(sqrt(adjustedAmount))`;
  - unstable nodes apply a random `-2..+2` offset per primal during setup.
- Unstable energized nodes can occasionally clear their production base and
  recalculate it.
- Energized nodes have range `8` and are source nodes for the relay graph.

### Vis relay network

- Relays are non-source vis nodes with range `8`.
- A relay can have one parent and multiple child relays/devices.
- Parent selection chooses the closest visible source or relay inside range.
- Attunement controls compatibility:
  - `-1` means untuned;
  - `0..5` correspond to primal aspects;
  - relays connect if either side is untuned or both have the same attunement.
- A wand right-click on a relay cycles attunement from `-1` through the six
  primals, clears parent/children, and forces a network refresh.
- Consumers drain CV through the closest visible relay/source inside range.
- Successful drain emits a primal-colored pulse along the relay chain.

## Development path

### Phase 1 - Stabilizer parity audit

- [x] Add focused notes/tests around the already implemented stabilizer behavior
  before building transducer logic on top of it.
- [x] Confirm ordinary stabilizer blocks both incoming and outgoing drain.
- [x] Confirm advanced stabilizer blocks incoming drain but allows outgoing
  drain from its protected node.
- [x] Confirm redstone signal on the stabilizer disables the lock for natural
  node logic and client animation.
- [x] Confirm recharge multipliers are exactly `2` and `20`.
- [x] Confirm unstable and fading recovery rolls use the old lock values.

Implementation note: this pass verified the current code path in
`AuraNodeBlockEntity` and `NodeStabilizerBlockEntity`. It does not add a
dedicated GameTest yet; the validation matrix remains for later in-game
coverage once transducer conversion exists.

Completion criteria:

- The stabilizer stack can be trusted as a dependency for transducer conversion.
- Any remaining mismatch is fixed before the transducer is implemented.

### Phase 2 - Energized-node representation

Recommended implementation: extend `AuraNodeBlockEntity` with a persistent
node mode instead of adding a second invisible block, unless renderer or block
interaction constraints force a split.

- [x] Add a persistent mode/state for natural versus energized node.
- [x] Persist and sync energized fields:
  - `auraBase`;
  - computed `cvBase`;
  - current per-tick `cv`;
  - type, modifier, and node id.
- [x] Keep natural-node current/base aspects untouched for ordinary nodes.
- [x] Disable natural behavior while energized:
  - no recharge;
  - no natural degradation;
  - no natural node bullying;
  - no hungry pull/destruction;
  - no wand tapping.
- [x] Add conversion helpers:
  - `convertToEnergizedFromNatural()`;
  - `convertToDrainedNaturalFromEnergized()`;
  - `setupEnergizedNode()`.

Implementation note: `/tc node energize` and `/tc node natural` provide the
temporary debug path until the transducer owns conversion.

Completion criteria:

- A command or debug path can convert a node to energized and back without
  losing id, type, modifier, or base aspects.
- Energized nodes are inert with respect to old natural-node mechanics.

### Phase 3 - Node Transducer block and BlockEntity

- [x] Replace `TCBlocks.NODE_TRANSDUCER = stoneDevice("node_transducer")` with a
  dedicated `NodeTransducerBlock`.
- [x] Register `NodeTransducerBlockEntity`.
- [x] Tick server and client state:
  - `count`, clamped to `0..1000`;
  - `status`: idle, forward conversion, reverse conversion;
  - previous status for synchronization and effects.
- [x] Implement `checkStatus()` equivalent:
  - powered transducer + natural node below + active stabilizer below node:
    forward conversion;
  - energized node below: reverse-capable state with count initialized to
    `1000`;
  - invalid energized stack above progress `50`: trigger the old failure path.
- [x] Drain one random current aspect from the natural node each powered tick
  during forward conversion.
- [x] At `count >= 1000`, convert natural node to energized.
- [x] At `count <= 50` during reverse conversion, convert energized node to a
  drained natural node.
- [x] Trigger burst and `craftfail` sound on both conversion boundaries.
- [x] Sync `count` and `status` to client for rendering and particles.

Implementation note: the first pass uses block-break particles plus `craftfail`
and temporary zap packets. A dedicated transducer renderer remains in Phase 4.

Completion criteria:

- The vertical stack behaves like old `TileNodeConverter`.
- The node visibly loses stored vis during forward conversion.
- Redstone on the transducer controls progress up and down.

### Phase 4 - Transducer and energized-node visuals

- [x] Reuse existing block model/item model for `node_transducer`.
- [x] Add client particles/bolts after progress `50`:
  - transducer to node;
  - stabilizer to node when the stabilizer is active.
- [x] Use existing zap/bolt rendering infrastructure where possible instead of
  adding a one-off effect system.
- [x] Reuse `AuraNodeRenderer` for energized node layers, with an energized
  visual distinction if the old renderer had one for airy metadata `5`.
- [x] Play the original burst and `craftfail` sound on conversion completion.

Implementation note: `NodeTransducerRenderer` ports the old
`TileNodeConverterRenderer` model animation using the stabilizer OBJ geometry,
old converter textures, 50-tick piston extension scale, and status-colored
overlays. Energized nodes render from `auraBase` and add the old
`lightningringv.png` effect.

Completion criteria:

- During conversion the player can visually tell that power is moving between
  transducer, node, and stabilizer.
- Completion has the same short failure-like burst/sound as the old mod.

### Completed implementation stage: Node Transducer

- [x] Dedicated block and BlockEntity.
- [x] Old `count`/`status` persistence and synchronization.
- [x] Powered forward conversion requiring active stabilizer below the node.
- [x] One random current-aspect drain per powered forward tick.
- [x] Natural node converts to energized at progress `1000`.
- [x] Energized node reverses to drained natural node at progress `50`.
- [x] Missing/disabled stabilizer under an energized node triggers explosion and
  flux goo/gas spread instead of a quiet fallback.
- [x] Old converter model animation and status colors are represented in the
  new renderer.
- [x] Energized node visual uses `auraBase` plus the lightning-ring overlay.
- [x] `gradlew.bat build` passes.

### Phase 5 - Centivis API

- [ ] Add a server-side API for machines to request CV:
  - `drainVis(Level level, BlockPos consumer, Aspect aspect, int amount)`;
  - returns amount actually drained.
- [ ] Store and consume CV in centivis units, matching the rest of the port:
  - `100` centivis equals one old vis;
  - old energized-node amounts are already CV-scale values in old machine
    costs, so avoid multiplying them again without verifying each consumer.
- [ ] Keep the API aspect-specific and primal-only for the first pass.
- [ ] Emit a consume effect packet when drain succeeds.
- [ ] Do not require chunk-global static weak references in the new design;
  prefer per-level graph rebuild/cache invalidation that is safe across chunk
  unload and server reload.

Completion criteria:

- A test consumer can request CV from an energized node directly or through a
  relay and receive the same limited per-tick supply.

### Phase 6 - Vis Relay block and network

- [ ] Replace visual-only `vis_relay` with a dedicated block and BlockEntity.
- [ ] Persist:
  - facing/orientation;
  - attunement `-1..5`;
  - parent position;
  - transient pulse color/timer for client rendering.
- [ ] Implement range `8` parent discovery.
- [ ] Implement line-of-sight using modern raycast while ignoring the source
  block in the same way old `ThaumcraftApiHelper.rayTraceIgnoringSource` did.
- [ ] Rebuild parent/children links every `40` ticks or on invalidation.
- [ ] On wand right-click, cycle attunement and force graph refresh.
- [ ] Render beams from parent to relay and primal-colored pulses on consume.

Completion criteria:

- Relays form a branching graph from energized nodes.
- Attunement gates connections correctly.
- CV can be drained through the nearest valid relay/source.

### Phase 7 - Research and gameplay gates

- [ ] Confirm existing data has `NODESTABILIZER` and `VISPOWER` research pages.
- [ ] Gate crafting/activation consistently with current research-system
  capabilities.
- [ ] Ensure the transducer cannot convert a node before `VISPOWER` is
  completed if recipe gating alone is not enough.
- [ ] Confirm recipes:
  - `node_stabilizer`;
  - `advanced_node_stabilizer`;
  - `node_transducer`;
  - `vis_relay`.

Completion criteria:

- Survival players reach this system through the intended research path instead
  of creative-only placement.

### Phase 8 - Validation matrix

- Natural node with no modifier, bright, pale, and fading.
- Normal, unstable, hungry, pure, dark, and tainted node types.
- Ordinary stabilizer versus advanced stabilizer.
- Stabilizer powered off during conversion.
- Transducer powered, unpowered, and toggled mid-conversion.
- Forward conversion with full, partial, and empty current aspects.
- Reverse conversion from energized node to drained natural node.
- Invalid energized stack without active stabilizer.
- Save/reload during:
  - count `0`;
  - count between `50` and `1000`;
  - fully energized state;
  - reverse conversion.
- Multiplayer observation of progress, effects, sounds, and relay pulses.
- Relay graph with:
  - direct source;
  - one relay;
  - relay chain;
  - branching relays;
  - blocked line of sight;
  - mismatched attunement;
  - chunk unload/reload.

## Suggested implementation order

1. Stabilizer audit and fixes.
2. Energized state on aura nodes.
3. Node transducer BlockEntity and conversion.
4. Conversion effects and sounds.
5. Minimal CV source API.
6. Vis relay BlockEntity and graph.
7. Machine consumers such as recharge pedestal, advanced alchemical furnace,
   arcane bore, focal manipulator, and flux scrubber.

This keeps the first playable result narrow: place stabilizer, place node,
place transducer above it, power the transducer, and get an energized node. The
relay network can then be layered on without risking the base conversion path.

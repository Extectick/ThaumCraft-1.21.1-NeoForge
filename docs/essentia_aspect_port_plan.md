# Essentia And Aspect Port Plan

## Source Of Truth
- Old implementation lives in `sources/ThaumCraftOld/recovered-project`.
- Primary old classes:
  - `thaumcraft.api.aspects.Aspect`
  - `thaumcraft.api.aspects.AspectList`
  - `thaumcraft.common.tiles.TileJarFillable`
  - `thaumcraft.common.lib.events.EssentiaHandler`
  - `thaumcraft.common.lib.crafting.ThaumcraftCraftingManager`
  - `thaumcraft.common.config.ConfigAspects`

## Implemented Foundation
- [x] New `thaumcraft.api.aspects.AspectList` API class.
- [x] Supports old-style multi-aspect operations:
  - `add`
  - `merge`
  - `remove`
  - `reduce`
  - `getAmount`
  - `visSize`
  - `copy`
  - sorted aspect views
- [x] Supports modern persistence/sync:
  - `Codec<AspectList>` for data-driven JSON.
  - `StreamCodec<ByteBuf, AspectList>` for network sync.
  - NBT helpers compatible with the old `"Aspects"` list shape.

## Next Stages

### Immediate Next Implementation Order
- [ ] Port old jar label crafting:
  - label + essentia phial assigns the label's aspect without consuming the phial essentia;
  - label alone clears the assigned aspect;
  - filled jar can still overwrite the label aspect when applied.
- [ ] Add old infusion essentia source pathing instead of direct radius draining:
  - scan valid source containers;
  - respect tube suction/pathing and blocked tube sides;
  - preserve missing-source delay behavior.
- [ ] Add source-to-matrix FX and sound pass for essentia drain.
- [ ] Port bellows:
  - furnace smelting speed modifier;
  - essentia buffer suction/choke modifier.
- [ ] Start crucible mechanics on top of the same `ObjectAspectRegistry`.

- [x] Replace `PrimalVisStorage essentia` in `InfusionRecipe` with `AspectList`.
- [x] Update the infusion recipe serializer.
- Update recipe JSON from primal vis fields to old aspect tags:
  ```json
  "essentia": {
    "praecantatio": 16,
    "metallum": 8,
    "ordo": 8
  }
  ```
- Existing test infusion recipes remain valid without an `essentia` field, so they still craft without jars until explicit costs are assigned.

### 2. Migrate Runic Matrix Recipe State
- [x] Replace `RunicMatrixBlockEntity.recipeEssentia` with `AspectList`.
- [x] Save and sync remaining recipe essentia.
- [x] Remove the temporary behavior that clears essentia without draining it.
- [x] Keep old crafting order:
  1. validate central catalyst;
  2. drain essentia;
  3. consume pedestal components;
  4. create result.

### 3. Add Essentia Source Drain
- [x] Add a modern helper equivalent to old `EssentiaHandler.drainEssentia(tile, aspect, UNKNOWN, 12)`.
- [x] First pass scans nearby block entities in radius 12 and drains one unit from `IEssentiaContainer`.
- [x] The first pass ignores old tube pathing and suction rules.
- Later pass should add:
  - source cache;
  - missing-source delay;
  - mirror exclusion;
  - tube pathing/suction fidelity;
  - source-to-matrix FX.

### 4. Align Jars With Old Behavior
- [x] `WardedJarBlockEntity` stores one `Aspect + amount` with capacity 64.
- [x] Add old optional `AspectFilter` state, including old-compatible NBT key.
- [x] Add void jar transport behavior:
  - keeps only up to 64 stored essentia;
  - accepts matching overflow and voids excess;
  - uses weaker labeled suction than a normal labeled jar.
- [x] Add jar label/filter behavior:
  - applying a `jar_label` to a filled jar locks it to the stored aspect;
  - applying an aspect-carrying label to an empty jar locks it to that aspect;
  - Shift-right-clicking the label side with empty hand removes the filter and drops a label;
  - Shift-right-clicking other sides with empty hand clears stored essentia.
- [x] Store jar contents/filter on dropped and picked jar item stacks with modern data components.
- [x] Restore stored contents/filter when placing a jar item.
- [x] Render a label and aspect icon on the filtered jar side.
- Keep `EssentiaStorage` for single-aspect containers and phials.
- Use `AspectList` only where multiple aspects are needed.
- Later pass should add:
  - jar label crafting with phials, so labels can be pre-assigned to aspects like old `JARLABEL`;
  - exact old crooked label rotation config if the old config system is ported.

### 5. Add Item Aspect Registry
- [x] Port the first layer of old `ThaumcraftCraftingManager.getObjectTags(ItemStack)` behavior.
- [x] Add `ObjectAspectRegistry.getObjectTags(ItemStack)`.
- [x] Add data-driven item aspect JSON loading from `data/*/thaumcraft/item_aspects/*.json`.
- [x] Support exact item entries:
  ```json
  {
    "item": "minecraft:iron_ingot",
    "aspects": {
      "metallum": 4
    }
  }
  ```
- [x] Support item tag entries:
  ```json
  {
    "tag": "minecraft:logs",
    "aspects": {
      "arbor": 4
    }
  }
  ```
- [x] Add a small verified starter data set for current Minecraft/Thaumcraft item ids.
- [x] Expand the explicit item aspect data set from old `ConfigAspects` into current Minecraft/Thaumcraft ids.
- [x] Validate the item aspect JSON set and current `thaumcraft:*` item ids.
- [x] Add fallback recipe-derived aspect generation:
  - exact item data remains first priority;
  - tag data remains second priority;
  - generated recipe data is used only when no explicit entry matches;
  - generation sums ingredient aspects, applies the old `0.75 / resultCount` reduction, and caps aspects at 64;
  - arcane worktable primal vis and infusion essentia contribute additional generated aspects.
- [x] Add `/tc aspects hand` for in-game validation.
- [x] Add old-style inventory tooltip layer: holding Shift over an item renders aspect icons with amounts.
- [x] Port old `ThaumcraftCraftingManager.getBonusTags(ItemStack, AspectList)` behavior into `ObjectAspectRegistry`:
  - stored essentia on item stacks is merged first;
  - armor, weapons, bows, tools, shears/hoes add their old dynamic aspect bonuses;
  - regular and stored enchantments add the old matching aspect bonuses plus total `MAGIC`;
  - final aspect lists are culled to the old six-aspect limit using old-style compound/primal weighting.
- [x] Route current aspect consumers through bonus-aware tags:
  - Shift tooltip;
  - `/tc aspects hand`;
  - alchemical furnace input checks and smelting.
- [x] Port potion aspect modifiers from old `getObjectTags`:
  - potion contents add `WATER`, `MAGIC`, and effect-specific aspects;
  - splash/lingering potions add old `ENTROPY` bonus;
  - current 1.21.1 effect ids are mapped to the old aspect table.
- Later stage can port:
  - more entries from old `ConfigAspects`;
  - common tag groups after verifying current 1.21.1 tag names;
  - generated aspects from future crucible/alchemy recipes once those recipe types exist.

### 6. Integrate With Future Systems
- [x] Add the first alchemical furnace + arcane alembic server mechanics from the old source:
  - furnace has the old two slots: input and fuel;
  - valid input is any item with `ObjectAspectRegistry.getObjectTags`;
  - internal furnace buffer is capped at 50 total essentia;
  - smelt time is `aspect total * 10` before bellows support;
  - fuel uses modern NeoForge burn time, with alumentum keeping the old transfer speed boost hook;
  - every 40 ticks, or 20 with alumentum, the furnace pushes one essentia upward into a vertical stack of up to five alembics;
  - occupied alembics are filled with matching essentia first, then empty alembics receive a random non-excluded aspect like old `takeRandomAspect`;
  - alembic capacity is 32 and stores exactly one aspect.
- [x] Add old comparator output for the alchemical furnace using the modern container redstone signal.
- [x] Add alembic facing/filter wand controls and rendering.
- [x] Add old tube suction/pathing basics from alembics into jars.
- Later pass should add:
  - bellows speed reduction;
  - bellows suction influence on essentia buffers;
  - essentia source cache and source-to-destination FX.
- Crucible should read item aspects from the same registry.
- Scanning/research should read the same registry.
- Infusion recipe essentia should stay explicit recipe data, like old infusion recipes.

## Manual Test Targets
- Fill a warded jar with a required aspect.
- Apply a jar label to a filled jar and confirm the visible label appears on the side facing the player.
- Shift-right-click the labeled side with an empty hand and confirm the label drops and the filter clears.
- Break and replace a filled/labeled warded jar and void jar, then confirm contents/filter survive in the item stack.
- Start an infusion recipe that requires that aspect.
- Confirm the matrix drains one essentia per cycle.
- Confirm crafting waits if the required aspect is missing.
- Confirm crafting consumes pedestal items only after essentia is fully paid.
- Confirm the central pedestal receives the result only after both essentia and item stages complete.
- Place an alchemical furnace, stack one or more arcane alembics directly above it, insert fuel and an item with aspects, and confirm alembics fill one unit at a time.
- Hold Shift over enchanted tools/armor, potion variants, and essentia-storing items to confirm dynamic bonus aspects appear and stay capped to six aspect types.
- Put a comparator against the alchemical furnace and confirm signal follows slot fullness.

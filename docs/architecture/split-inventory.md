# Thaumcraft Architecture Split Inventory

Baseline date: 2026-05-28
Last updated: 2026-06-12

This inventory records the split migration state. The project is still one NeoForge Gradle project, but it now enforces side boundaries through logical `api`, `common`, `client`, and `server` source sets and produces client, server, and universal variant jars. The workspace now contains the base `thaumcraft` mod and the ported `thaumictinkerer` addon namespace.

## Gradle and Entrypoints

- `settings.gradle` includes physical split entrypoint projects: `thaumcraft-api`, `thaumcraft-common`, `thaumcraft-client`, `thaumcraft-server`, `thaumcraft-universal`, `thaumcraft-datagen`, and `thaumcraft-gametest`.
- `build.gradle` applies `java-library`, `maven-publish`, and `net.neoforged.gradle.userdev`.
- `build.gradle` defines logical source sets: `api`, `common`, `client`, and `server`. These compile separately through `checkLogicalSourceSets`.
- Variant jars now assemble from logical source set outputs rather than from `sourceSets.main.output` with package filtering.
- Physical subproject `build` tasks delegate to the root logical source-set jars so source remains written once and NeoGradle run configs remain stable.
- `sourceSets.main.resources` includes `src/client/resources` and `src/server/resources` so NeoGradle `runClient`/`runServer` dev runs have the same ServiceLoader providers as the universal jar.
- `sourceSets.main.resources` includes `src/generated/resources`, but that directory is currently absent.
- `runs.client`, `runs.server`, `runs.gameTestServer`, and `runs.data` still use `project.sourceSets.main` for local development runtime.
- Main mod entrypoint: `thaumcraft.Thaumcraft`, registered with `@Mod(Thaumcraft.MODID)`.
- Client entrypoint: `thaumcraft.client.ThaumcraftClient`, registered with `@Mod(value = Thaumcraft.MODID, dist = Dist.CLIENT)`.
- Thaumic Tinkerer entrypoint: `thaumictinkerer.ThaumicTinkerer`, registered with `@Mod(ThaumicTinkerer.MODID)`.
- Thaumic Tinkerer client event subscriber: `thaumictinkerer.client.TTClientSetup`, registered on the mod event bus for `Dist.CLIENT`.
- All runtime variants currently share the same `META-INF/neoforge.mods.toml`; it declares `thaumcraft` plus `thaumictinkerer`.

## Client-only Classes

Client-only code is already grouped under `thaumcraft.client`:

- `thaumcraft.client.ThaumcraftClient`
- `thaumcraft.client.fx.*`
- `thaumcraft.client.hud.*`
- `thaumcraft.client.input.*`
- `thaumcraft.client.lib.PlayerNotifications`
- `thaumcraft.client.network.TCClientPayloadHandler`
- `thaumcraft.client.renderers.block.*`
- `thaumcraft.client.renderers.item.*`
- `thaumcraft.client.screens.*`
- `thaumcraft.client.tooltip.*`

The client entrypoint registers render layers, item properties, GUI layers, key mappings, menu screens, item renderers, block entity renderers, color handlers, FX tick handlers, and tooltip component handlers.

Thaumic Tinkerer client-only code is grouped under `thaumictinkerer.client`:

- `thaumictinkerer.client.TTClientSetup`
- `thaumictinkerer.client.TTItemRenderers`
- `thaumictinkerer.client.gui.IchorPouchScreen`
- `thaumictinkerer.client.models.KamiArmorModel`

The Thaumic Tinkerer client layer registers item property overrides, the ichor pouch screen, the Kami armor model layer, and advanced ichor armor client extensions.

## Server-authoritative Classes

These common classes are now shared shells/facades or helpers. Protected gameplay authority has moved behind server services where validation, mutation, progression, recipe execution, flux, warp, infusion, essentia transport, and generated aspects are decided:

- `thaumcraft.common.lib.crafting.InfusionAltarScan`
- `thaumcraft.common.lib.crafting.ArcaneWorktableRecipes`
- `thaumcraft.common.items.wands.WandCastingItem`
- `thaumcraft.common.items.wands.WandVisHelper`
- `thaumcraft.common.items.wands.WandFocusHelper`
- `thaumcraft.common.blockentities.RunicMatrixBlockEntity`
- `thaumcraft.server.wands.ServerWandService` now owns wand `use`/`useOn` authority previously embedded in `WandCastingItem`.
- `thaumcraft.server.infusion.InfusionCrafting` now owns infusion recipe start authority previously under `common.lib.crafting`.
- `thaumcraft.server.infusion.InfusionAltarBuilder` now owns infusion altar activation/build authority previously under `common.lib.crafting`.
- `thaumcraft.server.infusion.InfusionAltarScan` now owns infusion altar scan/symmetry authority previously under `common.lib.crafting`.
- `thaumcraft.server.infusion.ServerInfusionRuntime` now owns Runic Matrix craft-cycle authority: essentia drain, ingredient pulls, instability rolls, flux/warp side effects, and server FX packet emission.
- `thaumcraft.server.essentia.ServerEssentiaService` now owns essentia source scanning/draining and essentia source FX emission.
- `thaumcraft.server.crafting.ServerArcaneWorktableService` now owns Arcane Worktable result/ingredient/vis consumption authority.
- `thaumcraft.server.flux.ServerFluxService` now owns flux flow, flux placement/addition, and server-side flux entity effects.
- `thaumcraft.server.essentia.ServerEssentiaTransportService` now owns essentia tube suction/equalization/buffer pulls, alchemical furnace smelt/transfer runtime, and warded jar fill runtime.
- `thaumcraft.server.crafting.ServerObjectAspectRegistry` now owns item aspect JSON reload and generated-aspect recipe inference formulas. `ObjectAspectRegistry` remains a common facade for callers.
- `thaumcraft.server.research.ServerResearchService` now owns research mutation authority; `ResearchManager` remains a common read/facade contract for UI and common call sites.
- `thaumcraft.server.research.ServerResearchService` also owns research table environmental bonus scanning.
- `thaumcraft.server.warp.ServerWarpService` now owns warp mutation authority; `WarpManager` remains a common read/facade contract.

The physical multi-module split is still future work, but the logical server source set now owns the protected authority classes listed above.

## Shared/common Classes

These are valid common-layer candidates when they remain side-safe:

- Registry holders: `thaumcraft.common.registry.TCBlocks`, `TCItems`, `TCBlockEntities`, `TCEntityTypes`, `TCMenuTypes`, `TCRecipeTypes`, `TCRecipeSerializers`, `TCSoundEvents`, `TCParticleTypes`, `TCDataAttachments`, `TCDataComponents`, `TCCreativeTabs`.
- Thaumic Tinkerer registry holders: `thaumictinkerer.common.registry.TTBlocks`, `TTItems`, `TTTabs`, `TTDataComponents`, `TTMenuTypes`, and `TTRecipeSerializers`; armor material registration lives in `thaumictinkerer.common.items.equipment.TTArmorMaterials`.
- Public-ish contracts and data: `thaumcraft.api.aspects.*`, `thaumcraft.api.wands.*`, `thaumcraft.common.research.ResearchEntry`, `ResearchCategory`, `ResearchPage`, `ResearchTrigger`, `ResearchStatus`, `ResearchNoteData`, `ResearchKnowledgeData`, `AspectPoolData`, `WarpData`.
- Block/item/block entity/menu shells under `thaumcraft.common.blocks`, `thaumcraft.common.items`, `thaumcraft.common.blockentities`, and `thaumcraft.common.menus`, provided they avoid client imports and delegate authority to server services.
- Thaumic Tinkerer block/item/menu/recipe shells under `thaumictinkerer.common.blocks`, `thaumictinkerer.common.items`, `thaumictinkerer.common.menus`, and `thaumictinkerer.common.recipes`, provided they avoid client imports and delegate authority to server services as behavior matures.
- Packet payload records under `thaumcraft.common.network`, after handlers are split out.

## Mixed Classes that Must Be Split

- `WandCastingItem` is now closer to a common shell. This pass removed its direct `net.minecraft.client.gui.screens.Screen` import via a side-safe client state hook and moved wand focus equip/remove, table-to-worktable conversion, Thaumonomicon bookshelf conversion, and wand-triggered infusion entrypoints behind `server/wands/ServerWandService`.
- `RunicMatrixBlockEntity` is now primarily a shared block entity state/persistence/sync shell. Its server tick delegates to `ServerInfusionRuntime`; client tick still owns local animation and sound presentation.
- `ArcaneWorktableRecipes` remains a shared recipe lookup and UI cost helper. Craft execution and inventory/vis mutation dispatch to `ServerArcaneWorktableService`.
- `EssentiaHandler` remains a compatibility facade. Source lookup, drain cache, and FX emission dispatch to `ServerEssentiaService`.
- `FluxBlock` remains a shared block/state shell. Flow, placement/addition, and server entity effects dispatch to `ServerFluxService`.
- `EssentiaTubeBlockEntity`, `AlchemicalFurnaceBlockEntity`, and `WardedJarBlockEntity` are now shared storage/container/interface shells; periodic transport/smelt/fill authority delegates to `ServerEssentiaTransportService`.
- `ResearchTableBlockEntity` is now a shared inventory/state shell for bonus aspects; environmental bonus calculation delegates to `ServerResearchService`.
- `ObjectAspectRegistry` is now a shared facade only. The generation algorithm, potion/enchantment/recipe weighting, JSON reload listener, and generated aspect caches live in `ServerObjectAspectRegistry` and are absent from the client variant jar.
- `ResearchManager` and `WarpManager` are now common read/facade classes. Their mutations dispatch to `server.research.ServerResearchService` and `server.warp.ServerWarpService`.
- `InfusionCrafting` and `InfusionAltarBuilder` are now invoked by `ServerWandService` and live under `server.infusion`, so they are absent from the client variant jar.
- `InfusionAltarScan` now lives under `server.infusion`; `RunicMatrixBlockEntity` refreshes surroundings through `ServerInfusionHooks`.
- `Thaumcraft.java` is now a thin universal entrypoint. `ThaumcraftCommonBootstrap` registers common registries, network payloads, and config; `ThaumcraftServerBootstrap` delegates server event registration to `ThaumcraftServerServices`.
- Server commands and Runic Shield event handlers now live under `thaumcraft.server.events` and are absent from the client variant jar.
- `AwakenedIchorclothArmorItem` is now a side-safe common item shell. Its advanced armor model registration moved to `thaumictinkerer.client.TTItemRenderers`.
- `MagnetBlock` is still a placeholder common shell. It returns no block entity yet and still needs Item/Mob Magnet block entities, menus, filters, redstone behavior, and screen wiring.
- `ShareBookItem` still has a TODO for real Thaumcraft research sharing integration.

## Packet Contracts

Current packet payload records:

- `CycleWandFocusPayload`
- `ResearchTablePlaceAspectPayload`
- `ResearchTableCombineAspectPayload`
- `ThaumonomiconCreateNotePayload`
- `EssentiaSourceFxPayload`
- `InfusionSourceFxPayload`
- `BlockZapFxPayload`
- `PedestalSparkleFxPayload`
- `WarpMessagePayload`
- `ResearchCompleteNotificationPayload`

Each payload currently owns its `TYPE`, `STREAM_CODEC`, and a static `handle` method. Phase 2 should keep `TYPE` and `STREAM_CODEC` in payload records but move handlers to side-specific packages.

## Packet Handlers

- `TCNetwork` registers protocol version `"1"` and binds payload records to `TCPayloadHandlerBridge`.
- Clientbound payload records no longer contain client handling logic.
- Serverbound payload records no longer contain gameplay handling logic.
- `thaumcraft.client.network.TCClientPayloadHandler` owns clientbound handling.
- `thaumcraft.server.network.TCServerPayloadHandler` owns serverbound handling.
- `TCPayloadHandlerBridge` is a common registration bridge. It dispatches through `ClientServices` and `ServerServices`, both backed by Java `ServiceLoader` providers from the side-specific source sets.

Target split:

- `thaumcraft.common.network.payload` for records/codecs.
- `thaumcraft.client.network` for clientbound handlers.
- `thaumcraft.server.network` for serverbound handlers.
- `thaumcraft.common.network.TCNetwork` as the registration bridge only.

## Risky Imports

Initial scan found one forbidden direct client import in common:

- `src/main/java/thaumcraft/common/items/wands/WandCastingItem.java`: `net.minecraft.client.gui.screens.Screen`
- `src/main/java/thaumictinkerer/common/items/equipment/AwakenedIchorclothArmorItem.java`: `net.minecraft.client.*` and `thaumictinkerer.client.*`

These direct imports have been removed. `checkCommonSideSafety` now fails on future client-only imports in `thaumcraft.api`, `thaumcraft.common`, and `thaumictinkerer.common`.

Current side-boundary notes:

- `rg -n "Class\.forName|CLIENT_HANDLER|SERVER_HANDLER|SERVICE_CLASS|SERVER_.*SERVICE|SERVER_INFUSION_RUNTIME" src/main/java/thaumcraft/common src/main/java/thaumcraft/api` has no matches after the service-boundary pass.
- Common-to-side dispatch uses `ThaumcraftClientServices` and `ThaumcraftServerServices` interfaces plus service-provider files in `src/client/resources/META-INF/services` and `src/server/resources/META-INF/services`.
- `TCPayloadHandlerBridge` no longer reflectively loads client/server payload handlers.
- `RunicMatrixBlockEntity` no longer reflectively loads client FX classes; client FX calls dispatch through `ClientServices`.
- `ThaumonomiconItem` opens client screens through `ClientScreenHooks`, a side-safe no-op common hook registered by the client entrypoint.
- `WandCastingItem`, `ResearchManager`, `WarpManager`, `RunicMatrixBlockEntity`, `EssentiaHandler`, `ArcaneWorktableRecipes`, `FluxBlock`, essentia transport block entities, `ObjectAspectRegistry`, and `ResearchTableBlockEntity` now use side-safe server service dispatch for authority that has moved out of common.
- Future physical subprojects can replace `ServiceLoader` with direct module wiring or explicit service registration.

## Variant Jar Notes

Current split tasks:

- `apiJar`: api source set output.
- `commonJar`: api + common source set outputs.
- `clientJar`: api + common + client source set outputs.
- `serverJar`: api + common + server source set outputs.
- `universalJar`: api + common + client + server.
- `buildVariantJars`: builds all three jars.
- `checkVariantJars`: verifies client/server implementation package exclusion and universal aggregation.
- `checkLogicalSourceSets`: compiles api/common/client/server logical source sets independently and catches layer violations before packaging.
- `checkNoCommonReflectionSideDispatch`: fails if `api/common` reintroduces string/reflection based side dispatch.
- `checkVariantServiceProviders`: verifies side-specific ServiceLoader provider files are present only in the correct variants.
- `checkDevRuntimeServiceProviders`: verifies both side ServiceLoader provider files are present in `sourceSets.main` resources for universal dev runs.
- `checkProtectedClientJar`: fails if protected server implementation classes leak into the client variant jar.
- `checkPhysicalSubprojects`: verifies all physical Gradle split entrypoint projects are included.
- `checkSplitArchitecture`: runs the full split architecture gate.
- The side package checks now cover both `thaumcraft/client|server` and `thaumictinkerer/client`.

Current outputs:

- `build/libs/Thaumcraft-1.21.1-api.jar`
- `build/libs/Thaumcraft-1.21.1-common.jar`
- `build/libs/Thaumcraft-1.21.1-client.jar`
- `build/libs/Thaumcraft-1.21.1-server.jar`
- `build/libs/Thaumcraft-1.21.1-universal.jar`

Jar inspection after this pass:

- Client jar: contains `thaumcraft/client`, contains no `thaumcraft/server`, and contains `META-INF/services/thaumcraft.common.services.ThaumcraftClientServices`.
- Client jar: contains `thaumictinkerer/client` and `assets/thaumictinkerer`.
- Server jar: contains `thaumcraft/server`, contains no `thaumcraft/client`, contains no `thaumictinkerer/client`, and contains `META-INF/services/thaumcraft.common.services.ThaumcraftServerServices`.
- Universal jar: contains both `thaumcraft/client`, `thaumcraft/server`, and `thaumictinkerer/client` plus both service-provider files.
- Client jar: contains client assets and data resources.
- Server jar: contains data resources and mod metadata, but excludes `assets/thaumcraft` and `assets/thaumictinkerer`.
- All variants preserve `META-INF/neoforge.mods.toml` and the declared mod ids `thaumcraft` and `thaumictinkerer`.

Protected gameplay note: high-value systems listed above have moved into `thaumcraft.server`. Common now uses service interfaces for side dispatch, so the logical source sets enforce compile boundaries while keeping the single-project Gradle layout.

## Resources/datagen Notes

- `src/main/resources/assets/thaumcraft` currently contains 1062 files.
- `src/main/resources/data/thaumcraft` currently contains 284 files.
- `src/main/resources/assets/thaumictinkerer` contains the initial Thaumic Tinkerer client asset port.
- `src/main/resources/data/thaumictinkerer` contains the initial Thaumic Tinkerer recipe and loot table port.
- `src/generated/resources` is configured but currently missing.
- Existing resources use the stable `thaumcraft` and `thaumictinkerer` namespaces and should remain namespace-compatible across client, server, and universal variants.
- Client variants will eventually need assets, language files, models, textures, particles, and client display resources.
- Server variants should keep data packs, recipes, loot tables, tags, and mod metadata, and should not require client-only assets at runtime.

## Baseline Check Commands

Required checks for this phase:

- `./gradlew build`
- `./gradlew runServer`
- `./gradlew runClient`

Recorded results:

- `./gradlew checkCommonSideSafety`: passed.
- `rg -n "import net\.minecraft\.client|import com\.mojang\.blaze3d|import net\.minecraft\.client\.gui|import net\.minecraft\.client\.renderer" src/main/java/thaumcraft/common src/main/java/thaumcraft/api`: no matches.
- `./gradlew build`: passed.
- `./gradlew runServer`: launched interactively; `run/server/logs/latest.log` reached `Done (5.524s)!`.
- `./gradlew runClient`: launched interactively; `run/client/logs/latest.log` reached client resource, sound, texture atlas, and Thaumcraft bootstrap initialization without a recorded crash before manual stop.
- After the networking split, `./gradlew build`: passed.
- After the networking split, `./gradlew runServer`: launched interactively; `run/server/logs/latest.log` reached `Done (5.409s)!`.
- After the networking split, `./gradlew runClient`: launched interactively; `run/client/logs/latest.log` reached client resource, sound, texture atlas, and Thaumcraft bootstrap initialization without a recorded crash before manual stop.
- `./gradlew buildVariantJars checkVariantJars`: passed.
- Final `./gradlew build`: passed with `checkCommonSideSafety` and `checkVariantJars`.
- After moving wand use authority to `ServerWandService`, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars`: passed.
- After moving wand use authority to `ServerWandService`, `./gradlew build`: passed.
- After moving wand use authority to `ServerWandService`, `./gradlew runServer`: launched interactively; `run/server/logs/latest.log` reached `Done (3.372s)!`.
- Jar inspection confirmed `ServerWandService` is absent from `Thaumcraft-1.21.1-client.jar` and present in server/universal jars.
- After moving `InfusionCrafting` and `InfusionAltarBuilder` to `server.infusion`, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars build`: passed.
- After moving `InfusionCrafting` and `InfusionAltarBuilder` to `server.infusion`, `./gradlew runServer`: launched interactively; `run/server/logs/latest.log` reached `Done (11.078s)!`.
- Jar inspection confirmed `InfusionCrafting` and `InfusionAltarBuilder` are absent from `Thaumcraft-1.21.1-client.jar` and present in server/universal jars.
- After moving `InfusionAltarScan` to `server.infusion`, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars build`: passed.
- After moving `InfusionAltarScan` to `server.infusion`, `./gradlew runServer`: launched interactively; `run/server/logs/latest.log` reached `Done (7.896s)!`.
- Jar inspection confirmed `InfusionAltarScan` is absent from `Thaumcraft-1.21.1-client.jar` and present in server/universal jars.
- After moving research/warp mutations to server services, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars build`: passed.
- After moving research/warp mutations to server services, `./gradlew runServer`: launched interactively; `run/server/logs/latest.log` reached `Done (3.025s)!`.
- Jar inspection confirmed `ServerResearchService` and `ServerWarpService` are absent from `Thaumcraft-1.21.1-client.jar` and present in server/universal jars.
- After moving essentia source lookup/drain to `ServerEssentiaService`, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars build`: passed.
- Jar inspection confirmed `ServerEssentiaService` is absent from `Thaumcraft-1.21.1-client.jar` and present in server/universal jars.
- After moving Runic Matrix craft-cycle authority to `ServerInfusionRuntime`, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars build`: passed.
- Jar inspection confirmed `ServerInfusionRuntime` is absent from `Thaumcraft-1.21.1-client.jar` and present in server/universal jars.
- After moving Arcane Worktable mutation authority to `ServerArcaneWorktableService`, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars build`: passed.
- Jar inspection confirmed `ServerArcaneWorktableService` is absent from `Thaumcraft-1.21.1-client.jar` and present in server/universal jars.
- After moving flux flow/effects to `ServerFluxService`, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars build`: passed.
- After moving flux flow/effects to `ServerFluxService`, jar inspection confirmed `ServerFluxService`, `ServerArcaneWorktableService`, `ServerInfusionRuntime`, and `ServerEssentiaService` are absent from `Thaumcraft-1.21.1-client.jar` and present in server/universal jars.
- After the latest protected gameplay migration, `./gradlew runServer`: launched as a smoke check; `run/server/logs/latest.log` reached `Done (3.007s)!`.
- After moving essentia transport/furnace/jar runtime to `ServerEssentiaTransportService` and research table bonus scanning to `ServerResearchService`, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars build`: passed.
- Jar inspection confirmed `ServerEssentiaTransportService`, `ServerEssentiaService`, `ServerResearchService`, and `ServerFluxService` are absent from `Thaumcraft-1.21.1-client.jar` and present in server/universal jars.
- After moving item aspect reload/generation to `ServerObjectAspectRegistry`, `./gradlew checkCommonSideSafety buildVariantJars checkVariantJars build`: passed.
- Jar inspection confirmed `ServerObjectAspectRegistry` is absent from `Thaumcraft-1.21.1-client.jar`; the common `ObjectAspectRegistry` facade remains in all variants.
- After moving item aspect reload/generation to `ServerObjectAspectRegistry`, `./gradlew runServer`: launched as a smoke check; `run/server/logs/latest.log` reached `Done (3.202s)!`.
- After adding logical `api/common/client/server` Gradle source sets, `./gradlew checkCommonSideSafety checkLogicalSourceSets buildVariantJars checkVariantJars build`: passed.
- The logical source-set compile initially caught an API-to-common dependency in `ItemFocusBasic`; this was fixed by moving `FOCUS_FRUGAL` component access into common wand logic.
- Jar inspection after logical source-set packaging confirmed client/server/universal resource and implementation package composition.
- After replacing common reflection dispatch with `ClientServices`/`ServerServices`, `rg -n "Class\.forName|CLIENT_HANDLER|SERVER_HANDLER|SERVICE_CLASS|SERVER_.*SERVICE|SERVER_INFUSION_RUNTIME" src/main/java/thaumcraft/common src/main/java/thaumcraft/api`: no matches.
- After replacing common reflection dispatch, `./gradlew checkCommonSideSafety checkLogicalSourceSets buildVariantJars checkVariantJars build`: passed.
- Jar inspection confirmed client/server provider files are present only in their side variants and both are present in the universal jar.
- After replacing common reflection dispatch, `./gradlew runServer --no-daemon`: reached `Done (3.711s)!`.
- `./gradlew runClient` was intentionally not run in the final pass per request; client runtime coverage remains compile/package-only until an explicit client smoke check is allowed.
- After adding the consolidated split gate, `./gradlew checkSplitArchitecture build`: passed.
- CI now runs `./gradlew checkSplitArchitecture build --no-daemon`, runs a dedicated server smoke until `Done`, and uploads the client/server/universal variant jars.
- After adding physical split subprojects and bootstrap separation, `./gradlew checkSplitArchitecture :thaumcraft-api:build :thaumcraft-common:build :thaumcraft-client:build :thaumcraft-server:build :thaumcraft-universal:build :thaumcraft-datagen:build :thaumcraft-gametest:build build`: passed.
- CI now runs the same physical subproject build targets and uploads api/common/client/server/universal jars.
- After fixing universal dev resources, `./gradlew checkSplitArchitecture build`: passed and `build/resources/main/META-INF/services` contains both client and server service-provider files.
- After merging the Thaumic Tinkerer port, GitHub Actions initially caught a `compileClientJava` failure caused by client-only armor model registration in `thaumictinkerer.common`.
- After extending logical source sets and side-safety checks to `thaumictinkerer`, moving advanced ichor armor model registration into `thaumictinkerer.client.TTItemRenderers`, and fixing the generic model copy call, GitHub Actions `Build` passed for commit `9628504da2a26bb696cda9a6226fabacbba0c629`.
- Successful CI run: `https://github.com/Extectick/ThaumCraft-1.21.1-NeoForge/actions/runs/27425784629`.
- Local Windows verification of the same Gradle command was blocked before project compilation by repeated `Read timed out` failures downloading `net.neoforged.gradle:*:7.1.36` from `maven.neoforged.net`.

This pass keeps the original universal-style `jar` task for development and adds physical split project entrypoints plus layer and variant jar tasks.

## Current Follow-up Work

- Run an explicit client smoke after the Thaumic Tinkerer merge and check item property overrides, `IchorPouchScreen`, and `KamiArmorModel` rendering.
- Implement Thaumic Tinkerer Item/Mob Magnet block entities, menus, filters, redstone modes, and client screen.
- Replace the `ShareBookItem` TODO with real Thaumcraft research sharing behavior.
- Audit KAMI armor flight and step-height behavior for server authority, cleanup on unequip/logout/death, and compatibility with other mods that grant flight.
- Validate `data/thaumictinkerer` recipes and loot tables against the current Thaumcraft item IDs, research keys, and recipe serializers.
- Consider extending protected-client checks once Thaumic Tinkerer gains server-authoritative behavior classes.

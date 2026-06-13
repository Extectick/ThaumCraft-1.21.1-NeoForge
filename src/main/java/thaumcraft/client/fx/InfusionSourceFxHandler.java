package thaumcraft.client.fx;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import thaumcraft.common.blockentities.ArcanePedestalBlockEntity;

public final class InfusionSourceFxHandler {
    private static final Map<String, SourceFx> SOURCE_FX = new HashMap<>();

    private InfusionSourceFxHandler() {
    }

    public static void add(BlockPos target, BlockPos source, int color, int ticks, int sourceEntityId) {
        String key = target.getX() + ":" + target.getY() + ":" + target.getZ() + ":"
                + source.getX() + ":" + source.getY() + ":" + source.getZ() + ":" + color + ":"
                + sourceEntityId;
        SOURCE_FX.put(key, new SourceFx(target.immutable(), source.immutable(), ticks, color, sourceEntityId));
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            SOURCE_FX.clear();
            return;
        }

        for (String key : SOURCE_FX.keySet().toArray(String[]::new)) {
            SourceFx fx = SOURCE_FX.get(key);
            if (fx == null || fx.ticks() <= 0) {
                SOURCE_FX.remove(key);
                continue;
            }
            spawnParticles(level, fx);
            SOURCE_FX.put(key, new SourceFx(fx.target(), fx.source(), fx.ticks() - 1, fx.color(),
                    fx.sourceEntityId()));
        }
    }

    private static void spawnParticles(ClientLevel level, SourceFx fx) {
        Vec3 target = new Vec3(fx.target().getX() + 0.5D, fx.target().getY() - 0.5D, fx.target().getZ() + 0.5D);
        if (fx.sourceEntityId() >= 0) {
            Entity entity = level.getEntity(fx.sourceEntityId());
            if (entity == null) {
                return;
            }
            for (int i = 0; i < 2; i++) {
                double x = entity.getX() + (level.random.nextFloat() - level.random.nextFloat()) * entity.getBbWidth();
                double y = entity.getBoundingBox().minY + level.random.nextFloat() * entity.getBbHeight();
                double z = entity.getZ() + (level.random.nextFloat() - level.random.nextFloat()) * entity.getBbWidth();
                Minecraft.getInstance().particleEngine.add(new InfusionBoreSparkleParticle(level, x, y, z,
                        target.x, target.y, target.z,
                        0.2F, 0.6F + level.random.nextFloat() * 0.3F, 0.3F));
            }
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(fx.source());
        if (blockEntity instanceof ArcanePedestalBlockEntity pedestal && !pedestal.getStoredItem().isEmpty()) {
            ItemStack stack = pedestal.getStoredItem();
            if (level.random.nextInt(3) == 0) {
                double x = fx.source().getX() + level.random.nextDouble();
                double y = fx.source().getY() + level.random.nextDouble() + 1.0D;
                double z = fx.source().getZ() + level.random.nextDouble();
                Minecraft.getInstance().particleEngine.add(new InfusionBoreSparkleParticle(level, x, y, z,
                        target.x, target.y, target.z));
                return;
            }

            if (stack.getItem() instanceof BlockItem blockItem) {
                BlockState state = blockItem.getBlock().defaultBlockState();
                for (int i = 0; i < 2; i++) {
                    double x = fx.source().getX() + level.random.nextDouble();
                    double y = fx.source().getY() + level.random.nextDouble() + 1.0D;
                    double z = fx.source().getZ() + level.random.nextDouble();
                    Minecraft.getInstance().particleEngine.add(InfusionBoreParticle.block(level, x, y, z,
                            target.x, target.y, target.z, state, fx.source()));
                }
            } else {
                for (int i = 0; i < 2; i++) {
                    double x = fx.source().getX() + 0.4D + level.random.nextDouble() * 0.2D;
                    double y = fx.source().getY() + 1.23D + level.random.nextDouble() * 0.2D;
                    double z = fx.source().getZ() + 0.4D + level.random.nextDouble() * 0.2D;
                    Minecraft.getInstance().particleEngine.add(InfusionBoreParticle.item(level, x, y, z,
                            target.x, target.y, target.z, stack));
                }
            }
        }
    }

    private record SourceFx(BlockPos target, BlockPos source, int ticks, int color, int sourceEntityId) {
    }
}

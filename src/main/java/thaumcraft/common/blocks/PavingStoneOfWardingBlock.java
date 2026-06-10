package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.blockentities.WardingStoneBlockEntity;
import thaumcraft.client.fx.RuneParticle;
import thaumcraft.common.registry.TCBlockEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class PavingStoneOfWardingBlock extends BaseEntityBlock {

    public static final MapCodec<PavingStoneOfWardingBlock> CODEC = simpleCodec(PavingStoneOfWardingBlock::new);

    public PavingStoneOfWardingBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    @ParametersAreNonnullByDefault
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WardingStoneBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(type, TCBlockEntities.WARDING_STONE.get(), WardingStoneBlockEntity::serverTick);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            WardingStoneBlockEntity.onRemove(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        if (level.hasNeighborSignal(pos)) {

            if (random.nextFloat() < 0.05F) {
                spawnRune(level, x, y, z, random, 0.2F, 0.5F, 0.9F, -0.02F);
            }
        } else {

            if (random.nextFloat() < 0.08F) {
                spawnRune(level, x, y, z, random, 0.9F, 0.15F, 0.3F, -0.02F);
            }

            if (random.nextFloat() < 0.1F) {
                AABB searchBox = new AABB(pos).expandTowards(0, 2, 0).inflate(1.0, 1.0, 1.0);
                List<LivingEntity> mobs = level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> !(e instanceof Player));
                if (!mobs.isEmpty()) {
                    LivingEntity mob = mobs.get(0);
                    double eyeY = y + 0.6 + random.nextFloat() * Math.max(0.8, mob.getEyeHeight());
                    double px = x + (random.nextFloat() - 0.5) * 0.6;
                    double pz = z + (random.nextFloat() - 0.5) * 0.6;
                    RuneParticle rune = RuneParticle.createDirect((net.minecraft.client.multiplayer.ClientLevel) level, px, eyeY, pz,
                            0.6F + random.nextFloat() * 0.4F, 0.0F, 0.3F + random.nextFloat() * 0.7F, 20, 0.0F);
                    rune.setNoClip(true);
                    Minecraft.getInstance().particleEngine.add(rune);
                }
            }
        }
    }

    private static void spawnRune(Level level, double x, double y, double z, RandomSource random, float r, float g, float b, float gravity) {
        double px = x + (random.nextFloat() - 0.5) * 0.6;
        double py = y + 0.7;
        double pz = z + (random.nextFloat() - 0.5) * 0.6;
        RuneParticle rune = RuneParticle.createDirect((net.minecraft.client.multiplayer.ClientLevel) level, px, py, pz, r, g, b, 20, gravity);
        rune.setNoClip(true);
        Minecraft.getInstance().particleEngine.add(rune);
    }
}






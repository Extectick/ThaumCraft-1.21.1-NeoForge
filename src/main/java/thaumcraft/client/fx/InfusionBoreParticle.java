package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class InfusionBoreParticle extends TextureSheetParticle {
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final float uo;
    private final float vo;

    private InfusionBoreParticle(ClientLevel level, double x, double y, double z,
                                 double targetX, double targetY, double targetZ,
                                 TextureAtlasSprite sprite) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.setSprite(sprite);
        this.rCol = this.gCol = this.bCol = 0.6F;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.3F + 0.4F);
        this.gravity = 0.2F;
        this.hasPhysics = true;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;

        double dx = targetX - this.x;
        double dy = targetY - this.y;
        double dz = targetZ - this.z;
        int base = (int)(Math.sqrt(dx * dx + dy * dy + dz * dz) * 3.0F);
        if (base < 1) {
            base = 1;
        }
        this.lifetime = base / 2 + this.random.nextInt(base);

        float startJitter = 0.01F;
        this.xd = this.random.nextGaussian() * startJitter;
        this.yd = this.random.nextGaussian() * startJitter;
        this.zd = this.random.nextGaussian() * startJitter;

        if (Minecraft.getInstance().cameraEntity != null) {
            int visibleDistance = Minecraft.getInstance().options.graphicsMode().get().getKey().equals("fast") ? 32 : 64;
            if (Minecraft.getInstance().cameraEntity.distanceToSqr(x, y, z) > visibleDistance * visibleDistance) {
                this.lifetime = 0;
            }
        }
    }

    public static InfusionBoreParticle item(ClientLevel level, double x, double y, double z,
                                            double targetX, double targetY, double targetZ,
                                            ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        var model = minecraft.getItemRenderer().getModel(stack, level, null, 0);
        TextureAtlasSprite sprite = model.getOverrides()
                .resolve(model, stack, level, null, 0)
                .getParticleIcon(ModelData.EMPTY);
        InfusionBoreParticle particle = new InfusionBoreParticle(level, x, y, z, targetX, targetY, targetZ, sprite);
        int color = minecraft.getItemColors().getColor(stack, 0);
        if (color != -1) {
            particle.multiplyColor(color);
        }
        return particle;
    }

    public static InfusionBoreParticle block(ClientLevel level, double x, double y, double z,
                                             double targetX, double targetY, double targetZ,
                                             BlockState state, BlockPos colorPos) {
        Minecraft minecraft = Minecraft.getInstance();
        TextureAtlasSprite sprite = minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(state);
        InfusionBoreParticle particle = new InfusionBoreParticle(level, x, y, z, targetX, targetY, targetZ, sprite);
        if (IClientBlockExtensions.of(state).areBreakingParticlesTinted(state, level, colorPos)) {
            int color = minecraft.getBlockColors().getColor(state, level, colorPos, 0);
            if (color != -1) {
                particle.multiplyColor(color);
            }
        }
        return particle;
    }

    public static boolean isBlockParticle(ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime
                || (Mth.floor(this.x) == Mth.floor(this.targetX)
                && Mth.floor(this.y) == Mth.floor(this.targetY)
                && Mth.floor(this.z) == Mth.floor(this.targetZ))) {
            this.remove();
            return;
        }

        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.985D;
        this.yd *= 0.985D;
        this.zd *= 0.985D;

        double dx = this.targetX - this.x;
        double dy = this.targetY - this.y;
        double dz = this.targetZ - this.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance <= 1.0E-6D) {
            this.remove();
            return;
        }

        double pull = 0.3D;
        if (distance < 4.0D) {
            this.quadSize *= 0.9F;
            pull = 0.6D;
        }

        this.xd += dx / distance * pull;
        this.yd += dy / distance * pull;
        this.zd += dz / distance * pull;
        this.xd = Mth.clamp(this.xd, -0.35D, 0.35D);
        this.yd = Mth.clamp(this.yd, -0.35D, 0.35D);
        this.zd = Mth.clamp(this.zd, -0.35D, 0.35D);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0F) / 4.0F);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0F);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0F);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0F) / 4.0F);
    }

    private void multiplyColor(int color) {
        this.rCol *= (color >> 16 & 0xFF) / 255.0F;
        this.gCol *= (color >> 8 & 0xFF) / 255.0F;
        this.bCol *= (color & 0xFF) / 255.0F;
    }
}

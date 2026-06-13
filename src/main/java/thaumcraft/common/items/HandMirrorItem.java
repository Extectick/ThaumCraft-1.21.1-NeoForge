package thaumcraft.common.items;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.blocks.SimpleMirrorBlock;
import thaumcraft.common.menus.HandMirrorMenu;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCSoundEvents;

public class HandMirrorItem extends Item {
    private static final String LINK_X = "linkX";
    private static final String LINK_Y = "linkY";
    private static final String LINK_Z = "linkZ";
    private static final String LINK_DIM = "linkDim";
    private static final String DIM_NAME = "dimname";

    public HandMirrorItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(TCBlocks.MAGIC_MIRROR.get())) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        if (level.isClientSide) {
            if (player != null) {
                player.swing(context.getHand());
            }
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                CustomData.of(createLinkTag((ServerLevel) level, pos)));
        level.playSound(null, pos, TCSoundEvents.JAR.get(), SoundSource.BLOCKS, 1.0F, 2.0F);
        if (player != null) {
            player.displayClientMessage(Component.translatable("tc.handmirrorlinked")
                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC), false);
            player.containerMenu.broadcastChanges();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && hasLink(stack)) {
            if (!isLinkedMirrorPresent(stack, serverPlayer.serverLevel().getServer())) {
                clearBrokenLink(stack, level, player);
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("item.thaumcraft.hand_mirror");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player menuPlayer) {
                    return new HandMirrorMenu(id, inventory, stack);
                }
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasLink(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        LinkData link = getLink(stack);
        if (link != null) {
            tooltip.add(Component.translatable("tc.handmirrorlinkedto")
                    .append(" " + link.pos.getX() + "," + link.pos.getY() + "," + link.pos.getZ()
                            + " in " + link.dimensionName)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    public static boolean transport(ItemStack mirror, ItemStack items, Player player, Level sourceLevel) {
        if (!(player instanceof ServerPlayer serverPlayer) || items.isEmpty()) {
            return false;
        }
        ServerLevel targetLevel = getLinkedLevel(mirror, serverPlayer.serverLevel().getServer());
        LinkData link = getLink(mirror);
        if (targetLevel == null || link == null) {
            return false;
        }
        BlockState state = targetLevel.getBlockState(link.pos);
        if (!state.is(TCBlocks.MAGIC_MIRROR.get())) {
            clearBrokenLink(mirror, sourceLevel, player);
            return false;
        }
        Direction facing = state.getValue(SimpleMirrorBlock.FACING);
        Vec3 offset = Vec3.atLowerCornerOf(facing.getNormal());
        ItemEntity entity = new ItemEntity(targetLevel,
                link.pos.getX() + 0.5D - offset.x * 0.3D,
                link.pos.getY() + 0.5D - offset.y * 0.3D,
                link.pos.getZ() + 0.5D - offset.z * 0.3D,
                items.copy());
        entity.setDeltaMovement(offset.scale(0.15D));
        entity.setPickUpDelay(20);
        targetLevel.addFreshEntity(entity);
        sourceLevel.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.1F, 1.0F);
        targetLevel.levelEvent(2003, link.pos, 0);
        return true;
    }

    public static boolean hasLink(ItemStack stack) {
        return getLink(stack) != null;
    }

    private static CompoundTag createLinkTag(ServerLevel level, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(LINK_X, pos.getX());
        tag.putInt(LINK_Y, pos.getY());
        tag.putInt(LINK_Z, pos.getZ());
        tag.putString(LINK_DIM, level.dimension().location().toString());
        tag.putString(DIM_NAME, level.dimension().location().toString());
        return tag;
    }

    private static LinkData getLink(ItemStack stack) {
        CustomData data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (data == null || data.isEmpty()) {
            return null;
        }
        CompoundTag tag = data.copyTag();
        if (!tag.contains(LINK_X) || !tag.contains(LINK_Y) || !tag.contains(LINK_Z) || !tag.contains(LINK_DIM)) {
            return null;
        }
        return new LinkData(new BlockPos(tag.getInt(LINK_X), tag.getInt(LINK_Y), tag.getInt(LINK_Z)),
                tag.getString(LINK_DIM), tag.getString(DIM_NAME));
    }

    private static ServerLevel getLinkedLevel(ItemStack stack, net.minecraft.server.MinecraftServer server) {
        LinkData link = getLink(stack);
        if (link == null) {
            return null;
        }
        ResourceLocation id = ResourceLocation.parse(link.dimension);
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, id));
    }

    private static boolean isLinkedMirrorPresent(ItemStack stack, net.minecraft.server.MinecraftServer server) {
        ServerLevel targetLevel = getLinkedLevel(stack, server);
        LinkData link = getLink(stack);
        return targetLevel != null && link != null && targetLevel.getBlockState(link.pos).is(TCBlocks.MAGIC_MIRROR.get());
    }

    private static void clearBrokenLink(ItemStack stack, Level level, Player player) {
        stack.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        level.playSound(null, player.blockPosition(), TCSoundEvents.ZAP.get(), SoundSource.PLAYERS, 1.0F, 0.8F);
        player.displayClientMessage(Component.translatable("tc.handmirrorerror")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC), false);
    }

    private record LinkData(BlockPos pos, String dimension, String dimensionName) {
    }
}

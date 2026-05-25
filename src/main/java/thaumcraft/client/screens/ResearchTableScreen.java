package thaumcraft.client.screens;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2i;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.menus.ResearchTableMenu;
import thaumcraft.common.network.ResearchTableCombineAspectPayload;
import thaumcraft.common.network.ResearchTablePlaceAspectPayload;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.AspectPoolData;
import thaumcraft.common.research.ResearchNoteData;
import thaumcraft.common.research.ResearchRegistry;
import thaumcraft.common.lib.utils.HexUtils;

public class ResearchTableScreen extends AbstractContainerScreen<ResearchTableMenu> {
    private static final ResourceLocation BACKGROUND = Thaumcraft.id("textures/gui/guiresearchtable2.png");
    private static final ResourceLocation PARCHMENT = Thaumcraft.id("textures/misc/parchment3.png");
    private static final ResourceLocation PARTICLES = Thaumcraft.id("textures/misc/particles.png");
    private static final ResourceLocation SCRIPT = Thaumcraft.id("textures/misc/script.png");
    private static final ResourceLocation HEX = Thaumcraft.id("textures/gui/hex1.png");
    private static final ResourceLocation HEX_HIGHLIGHT = Thaumcraft.id("textures/gui/hex2.png");
    private static final ResourceLocation UNKNOWN_ASPECT = Thaumcraft.id("textures/aspects/_unknown.png");
    private static final int ASPECT_TEXTURE_FALLBACK_SIZE = 16;
    private static final int ASPECT_PANEL_ICON_SIZE = 16;
    private static final int ASPECT_SELECTED_ICON_SIZE = 16;
    private static final int ASPECT_NOTE_ICON_SIZE = 16;
    private static final int ASPECT_DRAG_ICON_SIZE = 16;
    private static final int ASPECT_GRID_CELL_SIZE = 16;
    private static final Map<ResourceLocation, Integer> ASPECT_TEXTURE_SIZE_CACHE = new HashMap<>();
    private static final int ASPECT_GRID_X = 10;
    private static final int ASPECT_GRID_Y = 40;
    private static final int ASPECT_GRID_COLUMNS = 5;
    private static final int ASPECT_GRID_ROWS = 5;
    private static final int ASPECTS_PER_PAGE = ASPECT_GRID_COLUMNS * ASPECT_GRID_ROWS;
    private static final int SELECT_1_X = 13;
    private static final int SELECT_2_X = 71;
    private static final int SELECT_Y = 139;
    private static final int PREVIOUS_PAGE_X = 27;
    private static final int NEXT_PAGE_X = 51;
    private static final int PAGE_BUTTON_Y = 121;
    private static final int COMBINE_X = 35;
    private static final int COMBINE_Y = 139;
    private static final int NOTE_SHEET_X = 94;
    private static final int NOTE_SHEET_Y = 8;
    private static final int NOTE_SHEET_WIDTH = 252;
    private static final int NOTE_SHEET_HEIGHT = 252;
    private static final int NOTE_CENTER_X = 169;
    private static final int NOTE_CENTER_Y = 83;
    private static final int NOTE_PARCHMENT_TEXTURE_SIZE = 150;
    private static final int NOTE_PARCHMENT_EDGE = 18;
    private static final int NOTE_CONNECTION_CORE_WIDTH = 1;
    private static final int NOTE_CONNECTION_GLOW_WIDTH = 2;
    private static final int NOTE_CONNECTION_CORE_COLOR = 0xEAF1DE;
    private static final int NOTE_CONNECTION_GLOW_COLOR = 0xB6F4F0;
    private static final int NOTE_CONNECTION_SHIMMER_COLOR = 0xFFFFFF;
    private static final int NOTE_CONNECTION_SHIMMER_LENGTH = 7;
    private static final ClientTooltipPositioner CENTERED_TOOLTIP = (screenWidth, screenHeight, centerX, centerY,
            tooltipWidth, tooltipHeight) -> new Vector2i(centerX - tooltipWidth / 2, centerY - tooltipHeight / 2);

    private Aspect selectedAspect = Aspect.AIR;
    private Aspect select1;
    private Aspect select2;
    private Aspect draggedAspect;
    private int aspectPage;
    private long lastRuneCheck;
    private final Map<String, Rune> runes = new HashMap<>();
    private final Random runeRandom = new Random();

    public ResearchTableScreen(ResearchTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 255;
        this.imageHeight = 255;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, 255, 167, 256, 256);
        guiGraphics.blit(BACKGROUND, this.leftPos + 40, this.topPos + 167, 0, 166, 184, 88, 256, 256);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        this.renderAspectPanel(guiGraphics);
        this.renderResearchNote(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderNoInkWarning(guiGraphics);
        if (this.draggedAspect != null) {
            renderAspectOrb(guiGraphics, this.draggedAspect, mouseX - ASPECT_DRAG_ICON_SIZE / 2,
                    mouseY - ASPECT_DRAG_ICON_SIZE / 2, 1.0F, ASPECT_DRAG_ICON_SIZE);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderAspectTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Aspect clicked = this.getClickedAspect(mouseX, mouseY);
            if (clicked != null && this.canUseAspect(clicked)) {
                this.draggedAspect = clicked;
                this.playUiSound(TCSoundEvents.HHOFF.get(), this.randomPitch(), 0.2F);
                return true;
            }
        }

        if (this.handleAspectPanelClick(mouseX, mouseY, button)) {
            return true;
        }

        if (this.handleResearchHexClick(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.draggedAspect != null) {
            Aspect dragged = this.draggedAspect;
            this.draggedAspect = null;
            if (this.dropDraggedAspect(mouseX, mouseY, dragged)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void renderAspectPanel(GuiGraphics guiGraphics) {
        int x = this.leftPos + ASPECT_GRID_X;
        int y = this.topPos + ASPECT_GRID_Y;
        List<Aspect> aspects = this.getVisibleAspects();
        int start = this.aspectPage * ASPECT_GRID_COLUMNS;
        for (int i = 0; i < ASPECTS_PER_PAGE && start + i < aspects.size(); i++) {
            Aspect aspect = aspects.get(start + i);
            int drawX = x + i / ASPECT_GRID_ROWS * ASPECT_GRID_CELL_SIZE;
            int drawY = y + i % ASPECT_GRID_ROWS * ASPECT_GRID_CELL_SIZE;
            int amount = this.getAspectAmount(aspect);
            renderAspectTag(guiGraphics, aspect, drawX, drawY, amount > 0 || this.hasCreativeAspects() ? 1.0F : 0.33F,
                    ASPECT_PANEL_ICON_SIZE);
            if (amount > 0) {
                drawAspectAmount(guiGraphics, amount, drawX, drawY, ASPECT_PANEL_ICON_SIZE);
            }
        }

        if (this.aspectPage > 0) {
            guiGraphics.blit(BACKGROUND, this.leftPos + PREVIOUS_PAGE_X, this.topPos + PAGE_BUTTON_Y, 184, 208, 24, 8,
                    256, 256);
        }
        if (this.aspectPage < this.getLastAspectPage()) {
            guiGraphics.blit(BACKGROUND, this.leftPos + NEXT_PAGE_X, this.topPos + PAGE_BUTTON_Y, 208, 208, 24, 8,
                    256, 256);
        }

        if (this.select1 != null) {
            renderAspectTag(guiGraphics, this.select1, this.leftPos + SELECT_1_X, this.topPos + SELECT_Y, 1.0F,
                    ASPECT_SELECTED_ICON_SIZE);
        }
        if (this.select2 != null) {
            renderAspectTag(guiGraphics, this.select2, this.leftPos + SELECT_2_X, this.topPos + SELECT_Y, 1.0F,
                    ASPECT_SELECTED_ICON_SIZE);
        }

        Optional<Aspect> result = this.getCombinationResult();
        if (this.select1 != null && this.select2 != null) {
            guiGraphics.blit(BACKGROUND, this.leftPos + COMBINE_X, this.topPos + COMBINE_Y, 184, 184, 32, 16, 256, 256);
            this.renderCombineOrb(guiGraphics, this.leftPos + 43, this.topPos + COMBINE_Y);
        }
    }

    private boolean handleAspectPanelClick(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        if (this.handleAspectPageButtonClick(mouseX, mouseY)) {
            return true;
        }

        if (this.handleSelectedAspectSlotClick(mouseX, mouseY)) {
            return true;
        }

        if (mouseX >= this.leftPos + COMBINE_X && mouseY >= this.topPos + COMBINE_Y
                && mouseX < this.leftPos + COMBINE_X + 32 && mouseY < this.topPos + COMBINE_Y + 16) {
            if (this.canUseSelectedComponents()) {
                Optional<Aspect> result = this.getCombinationResult();
                boolean discoveredBeforeClick = result.map(this::isAspectDiscovered).orElse(false);
                PacketDistributor.sendToServer(new ResearchTableCombineAspectPayload(this.select1, this.select2));
                this.playUiSound(TCSoundEvents.CAMERACLACK.get(), 1.0F, 0.4F);
                this.playUiSound(TCSoundEvents.HHON.get(), 1.0F, 0.3F);
                if (result.isPresent() && !discoveredBeforeClick) {
                    this.playUiSound(TCSoundEvents.LEARN.get(), 1.0F, 0.45F);
                }
                result.ifPresent(aspect -> {
                    if (!discoveredBeforeClick) {
                        PlayerNotifications.addDiscovery(aspect);
                    }
                    PlayerNotifications.addResearchPoints(aspect,
                            discoveredBeforeClick ? 1 : AspectPoolData.FIRST_DISCOVERY_BONUS + 1);
                });
                result.filter(this::isAspectDiscovered).ifPresent(aspect -> this.selectedAspect = aspect);
                this.aspectPage = Math.min(this.aspectPage, this.getLastAspectPage());
                return true;
            }
        }

        Aspect clicked = this.getClickedAspect(mouseX, mouseY);
        if (clicked != null && this.canUseAspect(clicked)) {
            this.selectedAspect = clicked;
            if (this.select1 == null) {
                this.select1 = clicked;
                this.playUiSound(TCSoundEvents.HHOFF.get(), this.randomPitch(), 0.2F);
            } else {
                if (this.select2 == null) {
                    this.select2 = clicked;
                    this.playUiSound(TCSoundEvents.HHOFF.get(), this.randomPitch(), 0.2F);
                }
            }
            return true;
        }

        return false;
    }

    private boolean dropDraggedAspect(double mouseX, double mouseY, Aspect aspect) {
        if (this.dropOnResearchHex(mouseX, mouseY, aspect)) {
            return true;
        }
        if (this.dropOnSelectedSlot(mouseX, mouseY, aspect)) {
            return true;
        }
        if (this.getClickedAspect(mouseX, mouseY) == aspect) {
            if (this.select1 == null) {
                this.select1 = aspect;
                return true;
            }
            if (this.select2 == null) {
                this.select2 = aspect;
                return true;
            }
        }
        return false;
    }

    private boolean dropOnResearchHex(double mouseX, double mouseY, Aspect aspect) {
        ItemStack notes = this.menu.getSlot(ResearchTableMenu.NOTES_MENU_SLOT).getItem();
        ResearchNoteData data = getDisplayData(notes);
        if (data.isEmpty() || data.complete()) {
            return false;
        }

        ResearchNoteData.HexEntry entry = findHexEntryAt(data, mouseX, mouseY);
        if (entry == null || entry.type() != 0) {
            return false;
        }

        if (!this.hasUsableInk()) {
            this.playNoInkSound();
            return true;
        }

        PacketDistributor.sendToServer(new ResearchTablePlaceAspectPayload(entry.hex().q(), entry.hex().r(),
                Optional.of(aspect)));
        this.playUiSound(TCSoundEvents.HHON.get(), 1.0F, 0.3F);
        this.playUiSound(TCSoundEvents.WRITE.get(), 1.0F, 0.2F);
        return true;
    }

    private boolean dropOnSelectedSlot(double mouseX, double mouseY, Aspect aspect) {
        if (mouseY < this.topPos + SELECT_Y - 2 || mouseY >= this.topPos + SELECT_Y + 18) {
            return false;
        }
        if (mouseX >= this.leftPos + SELECT_1_X - 2 && mouseX < this.leftPos + SELECT_1_X + 18) {
            this.select1 = aspect;
            this.playUiSound(TCSoundEvents.HHOFF.get(), this.randomPitch(), 0.2F);
            return true;
        }
        if (mouseX >= this.leftPos + SELECT_2_X - 2 && mouseX < this.leftPos + SELECT_2_X + 18) {
            this.select2 = aspect;
            this.playUiSound(TCSoundEvents.HHOFF.get(), this.randomPitch(), 0.2F);
            return true;
        }
        return false;
    }

    private boolean handleAspectPageButtonClick(double mouseX, double mouseY) {
        if (mouseY < this.topPos + PAGE_BUTTON_Y || mouseY >= this.topPos + PAGE_BUTTON_Y + 8) {
            return false;
        }

        if (mouseX >= this.leftPos + PREVIOUS_PAGE_X && mouseX < this.leftPos + PREVIOUS_PAGE_X + 24
                && this.aspectPage > 0) {
            this.aspectPage--;
            this.playUiSound(TCSoundEvents.KEY.get(), 1.0F, 0.3F);
            return true;
        }
        if (mouseX >= this.leftPos + NEXT_PAGE_X && mouseX < this.leftPos + NEXT_PAGE_X + 24
                && this.aspectPage < this.getLastAspectPage()) {
            this.aspectPage++;
            this.playUiSound(TCSoundEvents.KEY.get(), 1.0F, 0.3F);
            return true;
        }
        return false;
    }

    private boolean handleSelectedAspectSlotClick(double mouseX, double mouseY) {
        if (mouseY < this.topPos + SELECT_Y || mouseY >= this.topPos + SELECT_Y + 16) {
            return false;
        }

        if (mouseX >= this.leftPos + SELECT_1_X && mouseX < this.leftPos + SELECT_1_X + 16 && this.select1 != null) {
            this.select1 = null;
            this.playUiSound(TCSoundEvents.HHOFF.get(), this.randomPitch(), 0.2F);
            return true;
        }
        if (mouseX >= this.leftPos + SELECT_2_X && mouseX < this.leftPos + SELECT_2_X + 16 && this.select2 != null) {
            this.select2 = null;
            this.playUiSound(TCSoundEvents.HHOFF.get(), this.randomPitch(), 0.2F);
            return true;
        }
        return false;
    }

    private Aspect getClickedAspect(double mouseX, double mouseY) {
        int x = this.leftPos + ASPECT_GRID_X;
        int y = this.topPos + ASPECT_GRID_Y;
        List<Aspect> aspects = this.getVisibleAspects();
        int start = this.aspectPage * ASPECT_GRID_COLUMNS;
        for (int i = 0; i < ASPECTS_PER_PAGE && start + i < aspects.size(); i++) {
            int drawX = x + i / ASPECT_GRID_ROWS * ASPECT_GRID_CELL_SIZE;
            int drawY = y + i % ASPECT_GRID_ROWS * ASPECT_GRID_CELL_SIZE;
            if (mouseX >= drawX && mouseY >= drawY && mouseX < drawX + ASPECT_GRID_CELL_SIZE
                    && mouseY < drawY + ASPECT_GRID_CELL_SIZE) {
                return aspects.get(start + i);
            }
        }
        return null;
    }

    private void renderAspectTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Aspect hovered = this.getClickedAspect(mouseX, mouseY);
        if (hovered == null) {
            hovered = this.getSelectedSlotAspect(mouseX, mouseY);
        }
        if (hovered == null) {
            return;
        }

        java.util.List<Component> lines = new java.util.ArrayList<>();
        lines.add(Component.literal(this.getAspectDisplayName(hovered)).withStyle(ChatFormatting.WHITE));
        lines.add(Component.literal(hovered.getTag()).withStyle(ChatFormatting.DARK_GRAY));
        if (!hovered.isPrimal()) {
            Aspect[] components = hovered.getComponents();
            lines.add(Component.literal(this.getAspectDisplayName(components[0]) + " + "
                    + this.getAspectDisplayName(components[1])).withStyle(ChatFormatting.GRAY));
        }
        int amount = this.getAspectAmount(hovered);
        if (!this.hasCreativeAspects()) {
            lines.add(Component.literal("Amount: " + amount).withStyle(amount > 0 ? ChatFormatting.YELLOW : ChatFormatting.RED));
            int bonus = this.menu.getBonusAspectAmount(hovered);
            if (bonus > 0) {
                lines.add(Component.literal("Table bonus: +" + bonus).withStyle(ChatFormatting.AQUA));
            }
        }
        guiGraphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
    }

    private Aspect getSelectedSlotAspect(double mouseX, double mouseY) {
        if (mouseY < this.topPos + SELECT_Y || mouseY >= this.topPos + SELECT_Y + 16) {
            return null;
        }

        if (mouseX >= this.leftPos + SELECT_1_X && mouseX < this.leftPos + SELECT_1_X + 16) {
            return this.select1;
        }
        if (mouseX >= this.leftPos + SELECT_2_X && mouseX < this.leftPos + SELECT_2_X + 16) {
            return this.select2;
        }
        return null;
    }

    private String getAspectDisplayName(Aspect aspect) {
        String tag = aspect.getTag();
        return Character.toUpperCase(tag.charAt(0)) + tag.substring(1);
    }

    private Optional<Aspect> getCombinationResult() {
        if (this.select1 == null || this.select2 == null) {
            return Optional.empty();
        }

        for (Aspect aspect : Aspect.getCompoundAspects()) {
            Aspect[] components = aspect.getComponents();
            if (components != null && components.length == 2
                    && (components[0] == this.select1 && components[1] == this.select2
                            || components[0] == this.select2 && components[1] == this.select1)) {
                return Optional.of(aspect);
            }
        }
        return Optional.empty();
    }

    private int getLastAspectPage() {
        int aspectCount = this.getVisibleAspects().size();
        return Math.max(0, (aspectCount - ASPECT_GRID_ROWS * (ASPECT_GRID_COLUMNS - 1)) / ASPECT_GRID_COLUMNS);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= this.leftPos + ASPECT_GRID_X && mouseX < this.leftPos + ASPECT_GRID_X + 80
                && mouseY >= this.topPos + ASPECT_GRID_Y && mouseY < this.topPos + ASPECT_GRID_Y + 80) {
            int nextPage = this.aspectPage + (scrollY < 0 ? 1 : -1);
            nextPage = Math.max(0, Math.min(this.getLastAspectPage(), nextPage));
            if (nextPage != this.aspectPage) {
                this.aspectPage = nextPage;
                this.playUiSound(TCSoundEvents.KEY.get(), 1.0F, 0.3F);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean canUseAspect(Aspect aspect) {
        return aspect != null && (this.hasCreativeAspects() || this.getAspectAmount(aspect) > 0);
    }

    private boolean canUseSelectedComponents() {
        if (this.select1 == null || this.select2 == null || this.hasCreativeAspects()) {
            return this.select1 != null && this.select2 != null;
        }
        if (this.select1 == this.select2) {
            return this.getAspectAmount(this.select1) >= 2;
        }
        return this.getAspectAmount(this.select1) > 0 && this.getAspectAmount(this.select2) > 0;
    }

    private int getAspectAmount(Aspect aspect) {
        if (this.minecraft == null || this.minecraft.player == null) {
            return 0;
        }
        return this.minecraft.player.getData(TCDataAttachments.ASPECT_POOL).get(aspect)
                + this.menu.getBonusAspectAmount(aspect);
    }

    private boolean isAspectDiscovered(Aspect aspect) {
        return this.hasCreativeAspects() || this.getAspectPool().isDiscovered(aspect);
    }

    private AspectPoolData getAspectPool() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return AspectPoolData.EMPTY;
        }
        return this.minecraft.player.getData(TCDataAttachments.ASPECT_POOL);
    }

    private List<Aspect> getVisibleAspects() {
        if (this.hasCreativeAspects()) {
            return Arrays.asList(Aspect.values());
        }
        AspectPoolData pool = this.getAspectPool();
        return Arrays.stream(Aspect.values()).filter(pool::isDiscovered).toList();
    }

    private boolean hasCreativeAspects() {
        return this.minecraft != null && this.minecraft.player != null && this.minecraft.player.getAbilities().instabuild;
    }

    private boolean handleResearchHexClick(double mouseX, double mouseY, int button) {
        ItemStack notes = this.menu.getSlot(ResearchTableMenu.NOTES_MENU_SLOT).getItem();
        ResearchNoteData data = getDisplayData(notes);
        if (data.isEmpty() || data.complete()) {
            return false;
        }

        ResearchNoteData.HexEntry entry = findClickedEntry(data, mouseX, mouseY);
        if (entry == null || entry.type() == 1) {
            return false;
        }

        if (button == 1 && entry.type() == 2) {
            if (!this.hasUsableInk()) {
                this.playNoInkSound();
                return true;
            }
            PacketDistributor.sendToServer(new ResearchTablePlaceAspectPayload(entry.hex().q(), entry.hex().r(),
                    java.util.Optional.empty()));
            this.playUiSound(TCSoundEvents.HHON.get(), 1.0F, 0.3F);
            this.playUiSound(TCSoundEvents.ERASE.get(), this.randomPitch(), 0.2F);
            return true;
        }

        return false;
    }

    private void renderResearchNote(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ItemStack notes = this.menu.getSlot(ResearchTableMenu.NOTES_MENU_SLOT).getItem();
        ResearchNoteData data = getDisplayData(notes);
        if (data.isEmpty()) {
            return;
        }

        int sheetX = this.leftPos + NOTE_SHEET_X;
        int sheetY = this.topPos + NOTE_SHEET_Y;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.96F);
        blitParchment(guiGraphics, sheetX, sheetY, NOTE_SHEET_WIDTH, NOTE_SHEET_HEIGHT);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        this.renderRunes(guiGraphics, data);

        int centerX = this.leftPos + NOTE_CENTER_X;
        int centerY = this.topPos + NOTE_CENTER_Y;
        ResearchNoteData.HexEntry hovered = data.complete() ? null : findClickedEntry(data, mouseX, mouseY);
        Set<String> connected = this.getVisibleConnectedHexes(data);
        this.renderConnectionLines(guiGraphics, data, centerX, centerY, connected);

        for (ResearchNoteData.HexEntry entry : data.entries()) {
            HexUtils.Pixel pixel = entry.hex().toPixel(9);
            int x = centerX + (int) Math.round(pixel.x()) - 8;
            int y = centerY + (int) Math.round(pixel.y()) - 8;
            if (hovered != null && hovered.hex().equals(entry.hex()) && entry.type() != 1) {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                guiGraphics.blit(HEX_HIGHLIGHT, x, y, 0, 0, 16, 16, 16, 16);
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (entry.type() != 1 && !data.complete()) {
                RenderSystem.enableBlend();
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.25F);
                guiGraphics.blit(HEX, x, y, 0, 0, 16, 16, 16, 16);
                RenderSystem.disableBlend();
            } else if (entry.type() == 1) {
                entry.aspect().ifPresent(aspect -> this.renderCombineOrb(guiGraphics, x, y, aspect.getColor()));
            }
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            entry.aspect().ifPresent(aspect -> {
                if (!this.isAspectDiscovered(aspect)) {
                    renderUnknownAspect(guiGraphics, x, y, ASPECT_NOTE_ICON_SIZE);
                } else if (entry.type() == 1 || connected.contains(entry.hex().toString())) {
                    renderAspectTag(guiGraphics, aspect, x, y, 1.0F, ASPECT_NOTE_ICON_SIZE);
                } else if (entry.type() == 2) {
                    renderAspectTagDark(guiGraphics, aspect, x, y, 0.66F, ASPECT_NOTE_ICON_SIZE);
                }
            });
        }
    }

    private void renderRunes(GuiGraphics guiGraphics, ResearchNoteData data) {
        long time = System.currentTimeMillis();
        if (this.lastRuneCheck < time) {
            this.lastRuneCheck = time + 250L;
            int x = this.runeRandom.nextInt(120) - 60;
            int y = this.runeRandom.nextInt(120) - 60;
            HexUtils.Hex hex = new HexUtils.Pixel(x, y).toHex(9);
            if (!this.runes.containsKey(hex.toString()) && !data.entryMap().containsKey(hex.toString())) {
                this.runes.put(hex.toString(), new Rune(hex, time,
                        this.lastRuneCheck + 15000L + this.runeRandom.nextInt(10000), this.runeRandom.nextInt(16)));
            }
        }

        List<Rune> expired = new ArrayList<>();
        for (Rune rune : this.runes.values()) {
            if (rune.decay() < time) {
                expired.add(rune);
                continue;
            }
            HexUtils.Pixel pixel = rune.hex().toPixel(9);
            float progress = (float) (time - rune.start()) / (float) (rune.decay() - rune.start());
            float alpha = 0.5F;
            if (progress < 0.25F) {
                alpha = progress * 2.0F;
            } else if (progress > 0.5F) {
                alpha = 1.0F - progress;
            }
            this.renderRune(guiGraphics, this.leftPos + NOTE_CENTER_X + pixel.x(),
                    this.topPos + NOTE_CENTER_Y + pixel.y(), rune.index(), alpha * 0.66F);
        }
        expired.forEach(rune -> this.runes.remove(rune.hex().toString()));
    }

    private void renderConnectionLines(GuiGraphics guiGraphics, ResearchNoteData data, int centerX, int centerY,
            Set<String> connected) {
        Map<String, ResearchNoteData.HexEntry> entries = data.entryMap();
        for (ResearchNoteData.HexEntry entry : data.entries()) {
            if (entry.type() < 1 || entry.aspect().isEmpty()) {
                continue;
            }

            String entryKey = entry.hex().toString();
            boolean entryConnected = entry.type() == 1 || connected.contains(entryKey);
            if (!entryConnected) {
                continue;
            }

            for (int direction = 0; direction < 6; direction++) {
                HexUtils.Hex neighbourHex = entry.hex().getNeighbour(direction);
                String neighbourKey = neighbourHex.toString();
                if (entryKey.compareTo(neighbourKey) >= 0) {
                    continue;
                }

                ResearchNoteData.HexEntry neighbour = entries.get(neighbourKey);
                if (neighbour == null || neighbour.type() < 1 || neighbour.aspect().isEmpty()) {
                    continue;
                }

                boolean neighbourConnected = neighbour.type() == 1 || connected.contains(neighbourKey);
                if (neighbourConnected && this.isAspectDiscovered(entry.aspect().get())
                        && this.isAspectDiscovered(neighbour.aspect().get())
                        && ResearchNoteData.aspectsConnect(entry.aspect().get(), neighbour.aspect().get())) {
                    HexUtils.Pixel start = entry.hex().toPixel(9);
                    HexUtils.Pixel end = neighbour.hex().toPixel(9);
                    drawLine(guiGraphics, centerX + start.x(), centerY + start.y(), centerX + end.x(),
                            centerY + end.y(), this.minecraft.player.tickCount);
                }
            }
        }
    }

    private Set<String> getVisibleConnectedHexes(ResearchNoteData data) {
        Set<String> checked = new java.util.HashSet<>();
        Set<String> connected = new java.util.HashSet<>();
        Map<String, ResearchNoteData.HexEntry> entries = data.entryMap();
        for (ResearchNoteData.HexEntry entry : data.entries()) {
            if (entry.type() == 1 && entry.aspect().isPresent() && this.isAspectDiscovered(entry.aspect().get())) {
                this.collectVisibleConnections(entry.hex(), entries, checked, connected);
            }
        }
        return connected;
    }

    private void collectVisibleConnections(HexUtils.Hex hex, Map<String, ResearchNoteData.HexEntry> entries,
            Set<String> checked, Set<String> connected) {
        checked.add(hex.toString());
        ResearchNoteData.HexEntry source = entries.get(hex.toString());
        if (source == null || source.aspect().isEmpty() || !this.isAspectDiscovered(source.aspect().get())) {
            return;
        }

        for (int direction = 0; direction < 6; direction++) {
            HexUtils.Hex targetHex = hex.getNeighbour(direction);
            String targetKey = targetHex.toString();
            ResearchNoteData.HexEntry target = entries.get(targetKey);
            if (target == null || checked.contains(targetKey) || target.type() < 1 || target.aspect().isEmpty()
                    || !this.isAspectDiscovered(target.aspect().get())) {
                continue;
            }

            if (ResearchNoteData.aspectsConnect(source.aspect().get(), target.aspect().get())) {
                connected.add(targetKey);
                this.collectVisibleConnections(targetHex, entries, checked, connected);
            }
        }
    }

    private static ResearchNoteData getDisplayData(ItemStack notes) {
        return notes.getOrDefault(TCDataComponents.RESEARCH_NOTE, ResearchNoteData.EMPTY);
    }

    private ResearchNoteData.HexEntry findClickedEntry(ResearchNoteData data, double mouseX, double mouseY) {
        int centerX = this.leftPos + NOTE_CENTER_X;
        int centerY = this.topPos + NOTE_CENTER_Y;
        ResearchNoteData.HexEntry closest = null;
        double closestDistance = 11.0D * 11.0D;
        for (ResearchNoteData.HexEntry entry : data.entries()) {
            HexUtils.Pixel pixel = entry.hex().toPixel(9);
            double dx = mouseX - (centerX + pixel.x());
            double dy = mouseY - (centerY + pixel.y());
            double distance = dx * dx + dy * dy;
            if (distance <= closestDistance) {
                closest = entry;
                closestDistance = distance;
            }
        }
        return closest;
    }

    private ResearchNoteData.HexEntry findHexEntryAt(ResearchNoteData data, double mouseX, double mouseY) {
        int centerX = this.leftPos + NOTE_CENTER_X;
        int centerY = this.topPos + NOTE_CENTER_Y;
        HexUtils.Hex hex = new HexUtils.Pixel(mouseX - centerX, mouseY - centerY).toHex(9);
        return data.entryMap().get(hex.toString());
    }

    private static void renderAspectTag(GuiGraphics guiGraphics, Aspect aspect, int x, int y, float alpha) {
        renderAspectTag(guiGraphics, aspect, x, y, alpha, ASPECT_NOTE_ICON_SIZE);
    }

    private static void renderAspectTag(GuiGraphics guiGraphics, Aspect aspect, int x, int y, float alpha, int size) {
        ResourceLocation texture = aspectTexture(aspect.getTag());
        int textureSize = getSquareTextureSize(texture);
        int color = aspect.getColor();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F, alpha);
        guiGraphics.blit(texture, x, y, size, size, 0, 0, textureSize, textureSize, textureSize, textureSize);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    private static void renderUnknownAspect(GuiGraphics guiGraphics, int x, int y) {
        renderUnknownAspect(guiGraphics, x, y, ASPECT_NOTE_ICON_SIZE);
    }

    private static void renderUnknownAspect(GuiGraphics guiGraphics, int x, int y, int size) {
        int textureSize = getSquareTextureSize(UNKNOWN_ASPECT);
        RenderSystem.enableBlend();
        guiGraphics.setColor(0.0F, 0.0F, 0.0F, 0.5F);
        guiGraphics.blit(UNKNOWN_ASPECT, x, y, size, size, 0, 0, textureSize, textureSize, textureSize, textureSize);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    private static void blitParchment(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int textureSize = NOTE_PARCHMENT_TEXTURE_SIZE;
        if (width == textureSize && height == textureSize) {
            guiGraphics.blit(PARCHMENT, x, y, 0, 0, textureSize, textureSize, textureSize, textureSize);
            return;
        }

        int edge = Math.min(NOTE_PARCHMENT_EDGE, Math.min(width, height) / 2);
        int centerWidth = Math.max(0, width - edge * 2);
        int centerHeight = Math.max(0, height - edge * 2);
        int sourceCenter = textureSize - edge * 2;

        guiGraphics.blit(PARCHMENT, x, y, edge, edge, 0, 0, edge, edge, textureSize, textureSize);
        guiGraphics.blit(PARCHMENT, x + width - edge, y, edge, edge, textureSize - edge, 0, edge, edge,
                textureSize, textureSize);
        guiGraphics.blit(PARCHMENT, x, y + height - edge, edge, edge, 0, textureSize - edge, edge, edge,
                textureSize, textureSize);
        guiGraphics.blit(PARCHMENT, x + width - edge, y + height - edge, edge, edge, textureSize - edge,
                textureSize - edge, edge, edge, textureSize, textureSize);

        if (centerWidth > 0) {
            guiGraphics.blit(PARCHMENT, x + edge, y, centerWidth, edge, edge, 0, sourceCenter, edge, textureSize,
                    textureSize);
            guiGraphics.blit(PARCHMENT, x + edge, y + height - edge, centerWidth, edge, edge, textureSize - edge,
                    sourceCenter, edge, textureSize, textureSize);
        }
        if (centerHeight > 0) {
            guiGraphics.blit(PARCHMENT, x, y + edge, edge, centerHeight, 0, edge, edge, sourceCenter, textureSize,
                    textureSize);
            guiGraphics.blit(PARCHMENT, x + width - edge, y + edge, edge, centerHeight, textureSize - edge, edge,
                    edge, sourceCenter, textureSize, textureSize);
        }
        if (centerWidth > 0 && centerHeight > 0) {
            guiGraphics.blit(PARCHMENT, x + edge, y + edge, centerWidth, centerHeight, edge, edge, sourceCenter,
                    sourceCenter, textureSize, textureSize);
        }
    }

    private static void renderAspectTagDark(GuiGraphics guiGraphics, Aspect aspect, int x, int y, float alpha,
            int size) {
        ResourceLocation texture = aspectTexture(aspect.getTag());
        int textureSize = getSquareTextureSize(texture);
        RenderSystem.enableBlend();
        guiGraphics.setColor(0.1F, 0.1F, 0.1F, alpha * 0.8F);
        guiGraphics.blit(texture, x, y, size, size, 0, 0, textureSize, textureSize, textureSize, textureSize);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    private void renderNoInkWarning(GuiGraphics guiGraphics) {
        ItemStack notes = this.menu.getSlot(ResearchTableMenu.NOTES_MENU_SLOT).getItem();
        ResearchNoteData data = getDisplayData(notes);
        if (data.isEmpty() || data.complete() || this.hasUsableInk()) {
            return;
        }

        List<Component> lines = List.of(Component.translatable("tile.researchtable.noink.0"),
                Component.translatable("tile.researchtable.noink.1"));
        int centerX = this.leftPos + NOTE_SHEET_X + NOTE_SHEET_WIDTH / 2;
        int centerY = this.topPos + NOTE_SHEET_Y + NOTE_SHEET_HEIGHT / 2;
        this.renderCenteredTooltip(guiGraphics, lines, centerX, centerY);
    }

    private void renderCenteredTooltip(GuiGraphics guiGraphics, List<Component> lines, int centerX, int centerY) {
        List<FormattedCharSequence> orderedLines = lines.stream().map(Component::getVisualOrderText).toList();
        guiGraphics.renderTooltip(this.font, orderedLines, CENTERED_TOOLTIP, centerX, centerY);
    }

    private boolean hasUsableInk() {
        ItemStack tools = this.menu.getSlot(ResearchTableMenu.SCRIBING_TOOLS_MENU_SLOT).getItem();
        if (!tools.is(TCItems.SCRIBING_TOOLS.get())) {
            return false;
        }
        return !tools.isDamageableItem() || tools.getDamageValue() < tools.getMaxDamage();
    }

    private void playNoInkSound() {
        this.playUiSound(TCSoundEvents.CRAFTFAIL.get(), 0.85F, 0.35F);
    }

    private void renderCombineOrb(GuiGraphics guiGraphics, int x, int y) {
        this.renderCombineOrb(guiGraphics, x, y, -1);
    }

    private void renderCombineOrb(GuiGraphics guiGraphics, double x, double y, int colorOverride) {
        int ticks = this.minecraft != null && this.minecraft.player != null ? this.minecraft.player.tickCount : 0;
        float red = 0.85F + Mth.sin((float) ((ticks + x) / 10.0F)) * 0.15F;
        float green = 0.85F + Mth.sin((float) ((ticks + x + y) / 11.0F)) * 0.15F;
        float blue = 0.85F + Mth.sin((float) ((ticks + y) / 12.0F)) * 0.15F;
        if (colorOverride >= 0) {
            red = ((colorOverride >> 16) & 0xFF) / 255.0F;
            green = ((colorOverride >> 8) & 0xFF) / 255.0F;
            blue = (colorOverride & 0xFF) / 255.0F;
        }
        int frame = ticks % 8;
        int sourceX = frame < 4 ? 128 + frame * 32 : (frame - 4) * 32;
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        guiGraphics.setColor(red, green, blue, 1.0F);
        guiGraphics.blit(PARTICLES, (int) Math.round(x), (int) Math.round(y), sourceX, 128, 16, 16, 256, 256);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private static void renderAspectOrb(GuiGraphics guiGraphics, Aspect aspect, double x, double y, float alpha) {
        renderAspectOrb(guiGraphics, aspect, x, y, alpha, ASPECT_DRAG_ICON_SIZE);
    }

    private static void renderAspectOrb(GuiGraphics guiGraphics, Aspect aspect, double x, double y, float alpha,
            int size) {
        int color = aspect.getColor();
        int ticks = net.minecraft.client.Minecraft.getInstance().player != null
                ? net.minecraft.client.Minecraft.getInstance().player.tickCount
                : 0;
        int frame = ticks % 8;
        int sourceX = frame < 4 ? 128 + frame * 32 : (frame - 4) * 32;
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        guiGraphics.setColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F, alpha);
        guiGraphics.blit(PARTICLES, (int) Math.round(x), (int) Math.round(y), size, size, sourceX, 128, 16, 16,
                256, 256);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private static void drawAspectAmount(GuiGraphics guiGraphics, int amount, int x, int y) {
        drawAspectAmount(guiGraphics, amount, x, y, ASPECT_PANEL_ICON_SIZE);
    }

    private static void drawAspectAmount(GuiGraphics guiGraphics, int amount, int x, int y, int iconSize) {
        String text = Integer.toString(amount);
        // keep the same half-scale count placement as TC4's drawTag.
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, text,
                (x + iconSize) * 2 - net.minecraft.client.Minecraft.getInstance().font.width(text),
                (y + iconSize) * 2 - net.minecraft.client.Minecraft.getInstance().font.lineHeight,
                0xFFFFFF, false);
        guiGraphics.pose().popPose();
    }

    private void renderRune(GuiGraphics guiGraphics, double x, double y, int rune, float alpha) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((float) x, (float) y, 0.0F);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-90.0F));
        RenderSystem.enableBlend();
        guiGraphics.setColor(0.0F, 0.0F, 0.0F, alpha);
        guiGraphics.blit(SCRIPT, -5, -5, rune * 16, 0, 10, 10, 256, 16);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
    }

    private static void drawLine(GuiGraphics guiGraphics, double startX, double startY, double endX, double endY,
            int ticks) {
        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length <= 0.0D) {
            return;
        }

        int lineLength = (int) Math.round(length);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((float) startX, (float) startY, 0.0F);
        guiGraphics.pose().mulPose(Axis.ZP.rotation((float) Math.atan2(dy, dx)));
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        drawRoundedConnectionLayer(guiGraphics, lineLength, NOTE_CONNECTION_GLOW_WIDTH, 0.04F,
                NOTE_CONNECTION_GLOW_COLOR);
        drawRoundedConnectionLayer(guiGraphics, lineLength, NOTE_CONNECTION_CORE_WIDTH + 1, 0.08F,
                NOTE_CONNECTION_CORE_COLOR);
        drawRoundedConnectionLayer(guiGraphics, lineLength, NOTE_CONNECTION_CORE_WIDTH, 0.62F,
                NOTE_CONNECTION_CORE_COLOR);
        drawConnectionShimmer(guiGraphics, lineLength, ticks, startX);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
    }

    private static void drawConnectionShimmer(GuiGraphics guiGraphics, int length, int ticks, double seed) {
        if (length <= 2) {
            return;
        }

        int travel = length + NOTE_CONNECTION_SHIMMER_LENGTH * 2;
        int offset = Math.floorMod((int) (ticks * 2 + Math.round(seed)), travel) - NOTE_CONNECTION_SHIMMER_LENGTH;
        int shimmerStart = Math.max(0, offset);
        int shimmerEnd = Math.min(length, offset + NOTE_CONNECTION_SHIMMER_LENGTH);
        if (shimmerEnd <= shimmerStart) {
            return;
        }

        drawConnectionSegment(guiGraphics, shimmerStart, shimmerEnd, NOTE_CONNECTION_CORE_WIDTH, 0.45F,
                NOTE_CONNECTION_SHIMMER_COLOR);
    }

    private static void drawRoundedConnectionLayer(GuiGraphics guiGraphics, int length, int width, float alpha,
            int rgb) {
        int clampedAlpha = Mth.clamp((int) (alpha * 255.0F), 0, 255);
        if (clampedAlpha <= 0 || length <= 0 || width <= 0) {
            return;
        }

        int color = clampedAlpha << 24 | rgb;
        int top = -width / 2;
        int bottom = top + width;
        int radius = Math.max(1, width / 2);
        if (length > radius * 2) {
            guiGraphics.fill(radius, top, length - radius, bottom, color);
        }

        for (int y = top; y < bottom; y++) {
            double normalized = (y + 0.5D) / Math.max(1.0D, width * 0.5D);
            int cap = Math.max(0, (int) Math.round(Math.sqrt(Math.max(0.0D, 1.0D - normalized * normalized)) * radius));
            guiGraphics.fill(radius - cap, y, radius + 1, y + 1, color);
            guiGraphics.fill(length - radius - 1, y, length - radius + cap, y + 1, color);
        }
    }

    private static void drawConnectionSegment(GuiGraphics guiGraphics, int start, int end, int width, float alpha,
            int rgb) {
        int clampedAlpha = Mth.clamp((int) (alpha * 255.0F), 0, 255);
        if (clampedAlpha <= 0 || end <= start || width <= 0) {
            return;
        }

        int color = clampedAlpha << 24 | rgb;
        int top = -width / 2;
        guiGraphics.fill(start, top, end, top + width, color);
    }

    private static ResourceLocation aspectTexture(String name) {
        return Thaumcraft.id("textures/aspects/" + name + ".png");
    }

    private static int getSquareTextureSize(ResourceLocation texture) {
        return ASPECT_TEXTURE_SIZE_CACHE.computeIfAbsent(texture, ResearchTableScreen::readSquareTextureSize);
    }

    private static int readSquareTextureSize(ResourceLocation texture) {
        return Minecraft.getInstance().getResourceManager().getResource(texture).map(resource -> {
            try (InputStream stream = resource.open(); NativeImage image = NativeImage.read(stream)) {
                return Math.max(image.getWidth(), image.getHeight());
            } catch (IOException ignored) {
                return ASPECT_TEXTURE_FALLBACK_SIZE;
            }
        }).orElse(ASPECT_TEXTURE_FALLBACK_SIZE);
    }

    private float randomPitch() {
        return 1.0F + (this.minecraft != null && this.minecraft.level != null ? this.minecraft.level.random.nextFloat() * 0.1F : 0.0F);
    }

    private void playUiSound(SoundEvent sound, float pitch, float volume) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
        }
    }

    private record Rune(HexUtils.Hex hex, long start, long decay, int index) {
    }
}

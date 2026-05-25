package thaumcraft.client.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.network.ThaumonomiconCreateNotePayload;
import thaumcraft.common.registry.TCDataAttachments;
import thaumcraft.common.registry.TCSoundEvents;
import thaumcraft.common.research.ResearchCategory;
import thaumcraft.common.research.ResearchEntry;
import thaumcraft.common.research.ResearchEntry.ResearchFlag;
import thaumcraft.common.research.ResearchEntry.ResearchIcon;
import thaumcraft.common.research.ResearchKnowledgeData;
import thaumcraft.common.research.ResearchManager;
import thaumcraft.common.research.ResearchPage;
import thaumcraft.common.research.ResearchRegistry;

public class ThaumonomiconScreen extends Screen {
    private static final ResourceLocation GUI = Thaumcraft.id("textures/gui/gui_research.png");
    private static final ResourceLocation BOOK = Thaumcraft.id("textures/gui/gui_researchbook.png");
    private static final ResourceLocation BOOK_OVERLAY = Thaumcraft.id("textures/gui/gui_researchbook_overlay.png");
    private static final int PANE_WIDTH = 256;
    private static final int PANE_HEIGHT = 230;
    private static final int BOOK_WIDTH = 256;
    private static final int BOOK_HEIGHT = 181;
    private static final int BOOK_TEXTURE_SCALE = 2;
    private static final float BOOK_SCALE = 1.3F;
    private static final float PAGE_TEXT_SCALE = 1.0F;
    private static final int MAP_X = 16;
    private static final int MAP_Y = 17;
    private static final int MAP_WIDTH = 224;
    private static final int MAP_HEIGHT = 196;
    private static final int PAGE_SPACING = 152;
    private static final int PAGE_TEXT_WIDTH = 139;
    private static final int PAGE_TEXT_LINES = 16;
    private static final int PAGE_TEXT_LINES_WITH_TITLE = 12;
    private static final int NODE_STEP = 24;
    private static final int NODE_SIZE = 22;
    private static final int FRAME_SIZE = 26;
    private static final int CATEGORY_TAB_SIZE = 24;

    private static String selectedCategory;
    private static int lastColumn = -5;
    private static int lastRow = -6;
    private static boolean hasStoredMapPosition;

    private double mapX;
    private double mapY;
    private double targetMapX;
    private double targetMapY;
    private boolean draggingMap;
    private double dragDistance;
    private ResearchEntry pressedEntry;
    private ResearchEntry hoveredEntry;
    private ResearchEntry openedEntry;
    private int openedPage;

    public ThaumonomiconScreen() {
        super(Component.translatable("item.thaumcraft.thaumonomicon"));
        if (selectedCategory == null) {
            selectedCategory = ResearchRegistry.categories().stream()
                    .map(ResearchCategory::key)
                    .findFirst()
                    .orElse(ResearchRegistry.BASICS);
        }
        this.mapX = this.targetMapX = lastColumn * NODE_STEP - 141 / 2.0D - 12.0D;
        this.mapY = this.targetMapY = lastRow * NODE_STEP - 141 / 2.0D;
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new ThaumonomiconScreen());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        if (!hasStoredMapPosition) {
            ResearchRegistry.category(selectedCategory)
                    .ifPresent(category -> this.centerCategory(category, this.knowledge()));
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public void tick() {
        this.mapX = this.targetMapX;
        this.mapY = this.targetMapY;
    }

    @Override
    public void onClose() {
        lastColumn = (int) ((this.mapX + 141 / 2.0D + 12.0D) / NODE_STEP);
        lastRow = (int) ((this.mapY + 141 / 2.0D) / NODE_STEP);
        hasStoredMapPosition = true;
        super.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.hoveredEntry = null;

        int left = this.left();
        int top = this.top();
        ResearchKnowledgeData knowledge = this.knowledge();
        if (this.openedEntry != null) {
            renderOpenedResearch(guiGraphics, mouseX, mouseY, partialTick, this.openedEntry, knowledge);
            return;
        }

        ResearchCategory category = ResearchRegistry.category(selectedCategory)
                .orElseGet(() -> ResearchRegistry.categories().iterator().next());
        this.clampMap(category);

        guiGraphics.enableScissor(left + MAP_X, top + MAP_Y, left + MAP_X + MAP_WIDTH, top + MAP_Y + MAP_HEIGHT);
        renderMapBackground(guiGraphics, left, top, category);
        renderConnections(guiGraphics, left, top, partialTick, knowledge);
        renderNodes(guiGraphics, left, top, mouseX, mouseY, knowledge);
        guiGraphics.disableScissor();
        renderCategoryTabs(guiGraphics, left, top, mouseX, mouseY);
        renderFrame(guiGraphics, left, top);

        if (this.hoveredEntry != null) {
            renderEntryTooltip(guiGraphics, mouseX, mouseY, this.hoveredEntry, knowledge);
        }
    }

    private void renderMapBackground(GuiGraphics guiGraphics, int left, int top, ResearchCategory category) {
        int minX = category.minDisplayColumn() * NODE_STEP - 85;
        int maxX = category.maxDisplayColumn() * NODE_STEP - 112;
        int minY = category.minDisplayRow() * NODE_STEP - 112;
        int maxY = category.maxDisplayRow() * NODE_STEP - 61;
        int sourceX = scaleBackgroundSource((int) this.mapX, minX, maxX, 288);
        int sourceY = scaleBackgroundSource((int) this.mapY, minY, maxY, 316);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(2.0F, 2.0F, 1.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(category.background(), (left + MAP_X) / 2, (top + MAP_Y) / 2,
                sourceX / 2, sourceY / 2, MAP_WIDTH / 2, MAP_HEIGHT / 2, 256, 256);
        guiGraphics.pose().popPose();
    }

    private void renderFrame(GuiGraphics guiGraphics, int left, int top) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(GUI, left, top, 0, 0, PANE_WIDTH, MAP_Y, 256, 256);
        guiGraphics.blit(GUI, left, top + 213, 0, 213, PANE_WIDTH, PANE_HEIGHT - 213, 256, 256);
        guiGraphics.blit(GUI, left, top + MAP_Y, 0, MAP_Y, MAP_X, 196, 256, 256);
        guiGraphics.blit(GUI, left + 240, top + MAP_Y, 240, MAP_Y, PANE_WIDTH - 240, 196, 256, 256);
        RenderSystem.disableBlend();
    }

    private void renderConnections(GuiGraphics guiGraphics, int left, int top, float partialTick,
            ResearchKnowledgeData knowledge) {
        for (ResearchEntry entry : ResearchRegistry.visibleEntriesInCategory(selectedCategory, knowledge)) {
            if (entry.hasFlag(ResearchFlag.VIRTUAL)) {
                continue;
            }
            for (String parentKey : entry.parents()) {
                ResearchRegistry.get(parentKey)
                        .filter(parent -> parent.category().equals(selectedCategory))
                        .filter(parent -> !parent.hasFlag(ResearchFlag.VIRTUAL))
                        .ifPresent(parent -> renderResearchLine(guiGraphics, left, top, partialTick, entry, parent,
                                knowledge, false));
            }
            for (String siblingKey : entry.siblings()) {
                ResearchRegistry.get(siblingKey)
                        .filter(sibling -> sibling.category().equals(selectedCategory))
                        .filter(sibling -> !sibling.hasFlag(ResearchFlag.VIRTUAL))
                        .filter(sibling -> !sibling.parents().contains(entry.key()))
                        .ifPresent(sibling -> renderResearchLine(guiGraphics, left, top, partialTick, entry, sibling,
                                knowledge, true));
            }
        }
    }

    private void renderResearchLine(GuiGraphics guiGraphics, int left, int top, float partialTick, ResearchEntry from,
            ResearchEntry to, ResearchKnowledgeData knowledge, boolean sibling) {
        int startX = nodeScreenX(left, from) + NODE_SIZE / 2;
        int startY = nodeScreenY(top, from) + NODE_SIZE / 2;
        int endX = nodeScreenX(left, to) + NODE_SIZE / 2;
        int endY = nodeScreenY(top, to) + NODE_SIZE / 2;
        boolean fromComplete = isResearchComplete(knowledge, from);
        boolean toComplete = isResearchComplete(knowledge, to);
        if (fromComplete) {
            if (sibling) {
                drawResearchThread(guiGraphics, startX, startY, endX, endY, 0.1F, 0.1F, 0.2F, partialTick, false);
            } else {
                drawResearchThread(guiGraphics, startX, startY, endX, endY, 0.1F, 0.1F, 0.1F, partialTick, false);
            }
        } else if (ResearchManager.canStart(this.minecraft.player, from)) {
            if (toComplete) {
                drawResearchThread(guiGraphics, startX, startY, endX, endY, 0.0F, 1.0F, 0.0F, partialTick, true);
            } else {
                drawResearchThread(guiGraphics, startX, startY, endX, endY, 0.0F, 0.0F, 1.0F, partialTick, true);
            }
        } else if (to.isVisibleInBook(knowledge)) {
            if (toComplete) {
                drawResearchThread(guiGraphics, startX, startY, endX, endY, 0.0F, 0.45F, 0.0F, partialTick, true);
            } else {
                drawResearchThread(guiGraphics, startX, startY, endX, endY, 0.0F, 0.0F, 0.45F, partialTick, true);
            }
        }
    }

    private void renderNodes(GuiGraphics guiGraphics, int left, int top, int mouseX, int mouseY,
            ResearchKnowledgeData knowledge) {
        for (ResearchEntry entry : ResearchRegistry.visibleEntriesInCategory(selectedCategory, knowledge)) {
            if (entry.hasFlag(ResearchFlag.VIRTUAL)) {
                continue;
            }
            int x = nodeScreenX(left, entry);
            int y = nodeScreenY(top, entry);
            if (x < left + MAP_X - FRAME_SIZE || y < top + MAP_Y - FRAME_SIZE
                    || x > left + MAP_X + MAP_WIDTH || y > top + MAP_Y + MAP_HEIGHT) {
                continue;
            }

            boolean complete = isResearchComplete(knowledge, entry);
            boolean canUnlock = ResearchManager.canStart(this.minecraft.player, entry) || complete;
            float tint = complete ? 1.0F : canUnlock
                    ? 0.75F + (Mth.sin((System.currentTimeMillis() % 600L) / 600.0F * Mth.TWO_PI) * 0.25F)
                    : 0.3F;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(tint, tint, tint, 1.0F);
            blitNodeFrame(guiGraphics, entry, x - 2, y - 2);
            if (entry.hasFlag(ResearchFlag.SPECIAL)) {
                guiGraphics.blit(GUI, x - 2, y - 2, 26, 230, FRAME_SIZE, FRAME_SIZE, 256, 256);
            }
            RenderSystem.setShaderColor(tint, tint, tint, 1.0F);
            renderEntryIcon(guiGraphics, entry, x + 3, y + 3, canUnlock || complete);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            if (mouseX >= x && mouseX <= x + NODE_SIZE && mouseY >= y && mouseY <= y + NODE_SIZE) {
                this.hoveredEntry = entry;
            }
        }
    }

    private void blitNodeFrame(GuiGraphics guiGraphics, ResearchEntry entry, int x, int y) {
        if (entry.hasFlag(ResearchFlag.ROUND)) {
            guiGraphics.blit(GUI, x, y, 54, 230, FRAME_SIZE, FRAME_SIZE, 256, 256);
        } else if (entry.hasFlag(ResearchFlag.HIDDEN)) {
            guiGraphics.blit(GUI, x, y, 86, 230, FRAME_SIZE, FRAME_SIZE, 256, 256);
        } else if (entry.hasFlag(ResearchFlag.SECONDARY)) {
            guiGraphics.blit(GUI, x, y, 110, 230, FRAME_SIZE, FRAME_SIZE, 256, 256);
        } else {
            guiGraphics.blit(GUI, x, y, 0, 230, FRAME_SIZE, FRAME_SIZE, 256, 256);
        }
    }

    private void renderEntryIcon(GuiGraphics guiGraphics, ResearchEntry entry, int x, int y, boolean visible) {
        if (!visible) {
            RenderSystem.setShaderColor(0.2F, 0.2F, 0.2F, 1.0F);
        }
        Optional<ResearchIcon> icon = entry.icon();
        if (icon.isPresent() && icon.get() instanceof ResearchIcon.CyclingItemIcon cyclingIcon) {
            int tick = this.minecraft != null && this.minecraft.player != null
                    ? this.minecraft.player.tickCount
                    : (int) (System.currentTimeMillis() / 50L);
            ItemStack stack = cyclingIcon.stackAt(tick);
            guiGraphics.renderItem(stack, x, y);
        } else if (icon.isPresent() && icon.get() instanceof ResearchIcon.ItemIcon itemIcon) {
            ItemStack stack = itemIcon.stack();
            guiGraphics.renderItem(stack, x, y);
        } else if (icon.isPresent() && icon.get() instanceof ResearchIcon.TextureIcon textureIcon) {
            guiGraphics.blit(textureIcon.texture(), x, y, 16, 16, 0, 0, 16, 16, 16, 16);
        } else {
            ResourceLocation aspectTexture = Thaumcraft.id("textures/aspects/" + entry.primaryAspect().getTag() + ".png");
            guiGraphics.blit(aspectTexture, x, y, 16, 16, 0, 0, 32, 32, 32, 32);
        }
    }

    private void renderCategoryTabs(GuiGraphics guiGraphics, int left, int top, int mouseX, int mouseY) {
        List<ResearchCategory> categories = new ArrayList<>(ResearchRegistry.categories());
        for (int index = 0; index < categories.size(); index++) {
            ResearchCategory category = categories.get(index);
            boolean rightSide = index >= 9;
            int row = rightSide ? index - 9 : index;
            boolean selected = category.key().equals(selectedCategory);
            int x = rightSide ? left + 256 : left - CATEGORY_TAB_SIZE;
            int y = top + row * CATEGORY_TAB_SIZE;
            int tabU = rightSide ? 176 : 152;
            if (!selected) {
                tabU += 24;
            }
            int tabV = 232;
            int overlayU = rightSide ? 224 : 200;
            int iconOffset = selected ? 5 : 13;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(GUI, x, y, tabU, tabV, CATEGORY_TAB_SIZE, CATEGORY_TAB_SIZE, 256, 256);
            guiGraphics.blit(category.icon(), x + iconOffset, y + 4, 16, 16, 0, 0, 16, 16, 16, 16);
            if (!selected) {
                guiGraphics.blit(GUI, x, y, overlayU, 232, CATEGORY_TAB_SIZE, CATEGORY_TAB_SIZE, 256, 256);
            }
            RenderSystem.disableBlend();

            if (mouseX >= x && mouseX < x + CATEGORY_TAB_SIZE && mouseY >= y && mouseY < y + CATEGORY_TAB_SIZE) {
                guiGraphics.renderTooltip(this.font, Component.translatable(category.nameTranslationKey()), mouseX, mouseY);
            }
        }
    }

    private void renderEntryTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, ResearchEntry entry,
            ResearchKnowledgeData knowledge) {
        boolean complete = isResearchComplete(knowledge, entry);
        boolean available = ResearchManager.canStart(this.minecraft.player, entry);
        if (!complete && !available) {
            renderLockedEntryTooltip(guiGraphics, mouseX, mouseY, entry);
            return;
        }
        renderAvailableEntryTooltip(guiGraphics, mouseX, mouseY, entry, complete, available);
    }

    private void renderAvailableEntryTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, ResearchEntry entry,
            boolean complete, boolean available) {
        Component title = Component.translatable(entry.nameTranslationKey())
                .withColor(researchTooltipTitleColor(entry, complete, available));
        Component subtitle = Component.translatable(entry.textTranslationKey()).withStyle(ChatFormatting.ITALIC)
                .withColor(0x9696FF);
        Component action = available && !complete
                ? researchNoteHint(entry)
                : Component.empty();
        int x = mouseX + 6;
        int y = mouseY - 4;
        int width = Math.max(this.font.width(title), this.font.width(subtitle));
        if (!action.getString().isEmpty()) {
            width = Math.max(width, Math.round(this.font.width(action) * 0.5F));
        }
        int height = action.getString().isEmpty() ? 18 : 29;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
        guiGraphics.fillGradient(x - 3, y - 3, x + width + 3, y + height, 0xC0100010, 0xC0100010);
        guiGraphics.drawString(this.font, title, x, y, 0xFFFFFFFF, true);
        drawScaledString(guiGraphics, subtitle, x, y + 12, 0xFF9696FF, 0.5F, true);
        if (!action.getString().isEmpty()) {
            drawScaledString(guiGraphics, action, x, y + 21, 0xFFFFFFFF, 0.5F, true);
        }
        guiGraphics.pose().popPose();
    }

    private Component researchNoteHint(ResearchEntry entry) {
        if (!ResearchManager.hasScribingToolsAndPaper(this.minecraft.player)) {
            return Component.translatable("tc.research.shortprim").withColor(0xDB0C1C);
        }
        if (ResearchManager.hasResearchNote(this.minecraft.player, entry.key())) {
            return Component.translatable("tc.research.hasnote").withColor(0xFFAA00);
        }
        return Component.translatable("tc.research.getprim").withColor(0x87D3EB);
    }

    private void renderLockedEntryTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, ResearchEntry entry) {
        Component title = Component.translatable(entry.nameTranslationKey())
                .withColor(researchTooltipTitleColor(entry, false, false))
                .copy()
                .withStyle(style -> style.withFont(ResourceLocation.withDefaultNamespace("alt")));
        Component missing = Component.translatable("tc.researchmissing").withStyle(ChatFormatting.DARK_GRAY);
        int x = mouseX + 6;
        int y = mouseY - 4;
        int titleWidth = this.font.width(title);
        int missingWidth = this.font.width(missing);
        int width = Math.max(titleWidth, missingWidth);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
        guiGraphics.fillGradient(x - 3, y - 3, x + width + 3, y + 25, 0xC0100010, 0xC0100010);
        guiGraphics.drawString(this.font, title, x, y, 0xFFFFFFFF, true);
        guiGraphics.drawString(this.font, missing, x, y + 13, 0xFF707070, true);
        guiGraphics.pose().popPose();
    }

    private static int researchTooltipTitleColor(ResearchEntry entry, boolean complete, boolean available) {
        if (entry.hasFlag(ResearchFlag.SPECIAL)) {
            return available || complete ? 0xFFFF80 : 0x807F00;
        }
        return available || complete ? 0xFFFFFF : 0x808080;
    }

    private void renderOpenedResearch(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, ResearchEntry entry,
            ResearchKnowledgeData knowledge) {
        int bookLeft = bookLeft();
        int bookTop = bookTop();
        blitBook(guiGraphics, bookLeft, bookTop, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, scaledBookWidth(),
                scaledBookHeight());

        List<BookPageView> pages = visibleBookPages(entry, knowledge);
        if (pages.isEmpty()) {
            pages = visibleBookPages(entry, knowledge, List.of(ResearchPage.text(entry.textTranslationKey())));
        }
        this.openedPage = Mth.clamp(this.openedPage - this.openedPage % 2, 0, Math.max(0, pages.size() - 1));
        renderResearchPage(guiGraphics, entry, pages.get(this.openedPage), 0);
        if (this.openedPage + 1 < pages.size()) {
            renderResearchPage(guiGraphics, entry, pages.get(this.openedPage + 1), 1);
        }

        float age = this.minecraft != null && this.minecraft.player != null
                ? this.minecraft.player.tickCount + partialTick
                : (System.currentTimeMillis() % 60000L) / 50.0F;
        float bob = Mth.sin(age / 3.0F) * 0.07F + 0.07F;
        int contentLeft = bookContentLeft();
        int contentTop = bookContentTop();
        if (this.openedPage > 0) {
            blitBookScaled(guiGraphics, contentLeft - 16, contentTop + 190, 0, 184, 12, 8, 1.0F + bob);
        }
        if (this.openedPage + 2 < pages.size()) {
            blitBookScaled(guiGraphics, contentLeft + 262, contentTop + 190, 12, 184, 12, 8, 1.0F + bob);
        }
    }

    private void renderResearchPage(GuiGraphics guiGraphics, ResearchEntry entry, BookPageView view, int side) {
        ResearchPage page = view.page();
        int baseX = bookContentLeft();
        int x = baseX - 15 + side * PAGE_SPACING;
        int y = bookContentTop();
        int width = PAGE_TEXT_WIDTH;
        if (view.drawTitle()) {
            Component title = Component.translatable(entry.nameTranslationKey());
            blitBook(guiGraphics, baseX + 4, y - 13, 24, 184, 96, 4);
            blitBook(guiGraphics, baseX + 4, y + 4, 24, 184, 96, 4);
            drawCenteredFittedString(guiGraphics, title, baseX + 52, y - 6, 130, 0xFF302130, PAGE_TEXT_SCALE);
            y += 26;
        }

        switch (page.type()) {
            case TEXT, TEXT_CONCEALED -> renderTextResearchPage(guiGraphics, view, x, y - 10, width);
            case IMAGE -> renderImageResearchPage(guiGraphics, page, x, y - 10, width);
            case ASPECTS -> renderAspectResearchPage(guiGraphics, page, x + 7, y - 8);
            case TEXT_AND_RECIPE -> {
                renderTextResearchPage(guiGraphics, view, x, y - 10, width);
                renderRecipeReferencePage(guiGraphics, page, x, y + 82, width);
            }
            case NORMAL_CRAFTING, ARCANE_CRAFTING, CRUCIBLE_CRAFTING, INFUSION_CRAFTING, INFUSION_ENCHANTMENT, SMELTING,
                    COMPOUND_CRAFTING -> renderRecipeReferencePage(guiGraphics, page, x + 11, y - 8, width);
        }
    }

    private void renderTextResearchPage(GuiGraphics guiGraphics, BookPageView view, int x, int y, int width) {
        String rawText = rawPageText(view.page());
        if (hasCenteredLegacyHeading(rawText)) {
            drawLegacyResearchText(guiGraphics, rawText, x, y, width, 0xFF4F3922, PAGE_TEXT_SCALE);
            return;
        }
        if (view.textLines().isEmpty()) {
            drawScaledWordWrap(guiGraphics, pageText(view.page()), x, y, width, 0xFF4F3922, PAGE_TEXT_SCALE);
            return;
        }
        drawScaledLines(guiGraphics, view.textLines(), x, y, 0xFF4F3922, PAGE_TEXT_SCALE);
    }

    private static MutableComponent pageText(ResearchPage page) {
        if (page.textKey().isBlank()) {
            return Component.translatable("tc.thaumonomicon.page.missing");
        }
        return Component.translatable(page.textKey());
    }

    private static String rawPageText(ResearchPage page) {
        if (page.textKey().isBlank()) {
            return "";
        }
        return I18n.get(page.textKey());
    }

    private static boolean hasCenteredLegacyHeading(String rawText) {
        return rawText.contains("\u00a7l") && rawText.contains("\u00a7n");
    }

    private static boolean isCenteredLegacyHeading(String line) {
        return line.contains("\u00a7l") && line.contains("\u00a7n");
    }

    private static String normalizeCenteredLegacyHeading(String line) {
        return line.replaceAll("(?i)(\u00a7[0-9A-FK-OR])\\s+", "$1").trim();
    }

    private void drawLegacyResearchText(GuiGraphics guiGraphics, String rawText, int x, int y, int width, int color,
            float scale) {
        int lineY = 0;
        for (String rawLine : rawText.split("\\n", -1)) {
            if (rawLine.isBlank()) {
                lineY += this.font.lineHeight;
                continue;
            }
            if (isCenteredLegacyHeading(rawLine)) {
                Component heading = Component.literal(normalizeCenteredLegacyHeading(rawLine));
                drawCenteredFittedString(guiGraphics, heading, x + width / 2, y + lineY, width, color, scale);
                lineY += this.font.lineHeight + 2;
                continue;
            }
            Component line = Component.literal(rawLine.stripLeading());
            List<FormattedCharSequence> wrapped = this.font.split(line, Math.round(width / scale));
            drawScaledLines(guiGraphics, wrapped, x, y + lineY, color, scale);
            lineY += wrapped.size() * this.font.lineHeight;
        }
    }

    private void renderImageResearchPage(GuiGraphics guiGraphics, ResearchPage page, int x, int y, int width) {
        page.image().ifPresent(image -> guiGraphics.blit(image, x + 12, y + 6, width - 24, width - 24, 0.0F, 0.0F, 128,
                128, 128, 128));
        drawScaledWordWrap(guiGraphics, pageText(page), x, y + width - 10, width, 0xFF4F3922, PAGE_TEXT_SCALE);
    }

    private void renderAspectResearchPage(GuiGraphics guiGraphics, ResearchPage page, int x, int y) {
        int row = 0;
        for (Aspect aspect : page.aspects()) {
            renderAspectIcon(guiGraphics, aspect, x, y + row * 38, 24, 1.0F);
            guiGraphics.drawString(this.font, Component.translatable("tc.aspect." + aspect.getTag()), x + 32,
                    y + 8 + row * 38, 0xFF4F3922, false);
            row++;
        }
    }

    private void renderRecipeReferencePage(GuiGraphics guiGraphics, ResearchPage page, int x, int y, int width) {
        guiGraphics.blit(BOOK_OVERLAY, x + width / 2 - 52, y + 32, 104, 104, 112, 15, 52, 52, 512, 512);
        if (page.recipeOutput().isPresent()) {
            guiGraphics.renderItem(page.recipeOutput().get(), x + width / 2 - 8, y + 50);
        } else if (!page.recipeIds().isEmpty()) {
            guiGraphics.drawCenteredString(this.font, Component.literal(page.recipeIds().getFirst().getPath()),
                    x + width / 2, y + 55, 0xFF4F3922);
        } else {
            guiGraphics.drawCenteredString(this.font, Component.translatable("tc.thaumonomicon.recipe.placeholder"),
                    x + width / 2, y + 55, 0xFF4F3922);
        }
        guiGraphics.drawCenteredString(this.font, Component.translatable("tc.thaumonomicon.recipe." + page.type().name().toLowerCase()),
                x + width / 2, y + 8, 0xFF505030);
    }

    private void drawScaledWordWrap(GuiGraphics guiGraphics, Component text, int x, int y, int width, int color,
            float scale) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawWordWrap(this.font, text, 0, 0, Math.round(width / scale), color);
        guiGraphics.pose().popPose();
    }

    private void drawScaledLines(GuiGraphics guiGraphics, List<FormattedCharSequence> lines, int x, int y, int color,
            float scale) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        int lineY = 0;
        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(this.font, line, 0, lineY, color, false);
            lineY += this.font.lineHeight;
        }
        guiGraphics.pose().popPose();
    }

    private void drawScaledString(GuiGraphics guiGraphics, Component text, int x, int y, int color, float scale,
            boolean shadow) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawString(this.font, text, 0, 0, color, shadow);
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (this.handleCategoryClick(mouseX, mouseY)) {
                return true;
            }
            if (this.openedEntry != null) {
                if (this.handleOpenedPageClick(mouseX, mouseY)) {
                    return true;
                }
                return true;
            }
            if (this.inMap(mouseX, mouseY)) {
                this.draggingMap = true;
                this.dragDistance = 0.0D;
                this.pressedEntry = this.hoveredEntry;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.minecraft != null && this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        if (this.openedEntry != null && keyCode == 256) {
            this.openedEntry = null;
            this.playUiSound(TCSoundEvents.PAGE.get(), 0.9F, 0.35F);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.draggingMap) {
            this.targetMapX -= dragX;
            this.targetMapY -= dragY;
            ResearchRegistry.category(selectedCategory).ifPresent(this::clampMap);
            this.mapX = this.targetMapX;
            this.mapY = this.targetMapY;
            this.dragDistance += Math.abs(dragX) + Math.abs(dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.draggingMap) {
            if (this.dragDistance < 4.0D && this.pressedEntry != null) {
                ResearchKnowledgeData knowledge = this.knowledge();
                if (isResearchComplete(knowledge, this.pressedEntry)) {
                    this.openedEntry = this.pressedEntry;
                    this.openedPage = 0;
                    this.playUiSound(TCSoundEvents.PAGE.get(), 1.0F, 0.35F);
                } else if (ResearchManager.canStart(this.minecraft.player, this.pressedEntry)) {
                    PacketDistributor.sendToServer(new ThaumonomiconCreateNotePayload(this.pressedEntry.key()));
                    boolean canCreateNote = !ResearchManager.hasResearchNote(this.minecraft.player, this.pressedEntry.key())
                            && ResearchManager.hasScribingToolsAndPaper(this.minecraft.player);
                    this.playUiSound(canCreateNote ? TCSoundEvents.WRITE.get() : TCSoundEvents.HHOFF.get(),
                            canCreateNote ? 0.7F : 0.45F, 1.0F);
                }
            }
            this.draggingMap = false;
            this.dragDistance = 0.0D;
            this.pressedEntry = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean handleCategoryClick(double mouseX, double mouseY) {
        int left = this.left();
        int top = this.top();
        List<ResearchCategory> categories = new ArrayList<>(ResearchRegistry.categories());
        for (int index = 0; index < categories.size(); index++) {
            boolean rightSide = index >= 9;
            int row = rightSide ? index - 9 : index;
            ResearchCategory category = categories.get(index);
            int x = rightSide ? left + 256 : left - CATEGORY_TAB_SIZE;
            int y = top + row * CATEGORY_TAB_SIZE;
            if (mouseX >= x && mouseX < x + CATEGORY_TAB_SIZE && mouseY >= y && mouseY < y + CATEGORY_TAB_SIZE) {
                selectedCategory = category.key();
                this.openedEntry = null;
                this.centerCategory(category, this.knowledge());
                this.playUiSound(TCSoundEvents.PAGE.get(), 1.0F, 0.25F);
                this.clampMap(category);
                return true;
            }
        }
        return false;
    }

    private void centerCategory(ResearchCategory category, ResearchKnowledgeData knowledge) {
        List<ResearchEntry> entries = ResearchRegistry.visibleEntriesInCategory(category.key(), knowledge).stream()
                .filter(entry -> !entry.hasFlag(ResearchFlag.VIRTUAL))
                .toList();
        if (entries.isEmpty()) {
            this.targetMapX = category.minDisplayColumn() * NODE_STEP + 12.0D - MAP_WIDTH / 2.0D;
            this.targetMapY = category.minDisplayRow() * NODE_STEP + 12.0D - MAP_HEIGHT / 2.0D;
        } else {
            double sumColumn = 0.0D;
            double sumRow = 0.0D;
            for (ResearchEntry entry : entries) {
                sumColumn += entry.displayColumn();
                sumRow += entry.displayRow();
            }
            this.targetMapX = sumColumn / entries.size() * NODE_STEP + NODE_SIZE / 2.0D - MAP_WIDTH / 2.0D;
            this.targetMapY = sumRow / entries.size() * NODE_STEP + NODE_SIZE / 2.0D - MAP_HEIGHT / 2.0D;
        }
        this.mapX = this.targetMapX;
        this.mapY = this.targetMapY;
    }

    private boolean handleOpenedPageClick(double mouseX, double mouseY) {
        if (this.openedEntry == null) {
            return false;
        }
        List<BookPageView> pages = visibleBookPages(this.openedEntry, this.knowledge());
        int contentLeft = bookContentLeft();
        int contentTop = bookContentTop();
        if (mouseY < contentTop + 184 || mouseY > contentTop + 204) {
            return false;
        }
        if (mouseX >= contentLeft - 17 && mouseX < contentLeft - 3 && this.openedPage > 0) {
            this.openedPage = Math.max(0, this.openedPage - 2);
            this.playUiSound(TCSoundEvents.PAGE.get(), 0.95F, 0.35F);
            return true;
        }
        if (mouseX >= contentLeft + 261 && mouseX < contentLeft + 275
                && this.openedPage + 2 < pages.size()) {
            this.openedPage += 2;
            this.playUiSound(TCSoundEvents.PAGE.get(), 1.05F, 0.35F);
            return true;
        }
        return false;
    }

    private List<ResearchPage> visiblePages(ResearchEntry entry, ResearchKnowledgeData knowledge) {
        return entry.pages().stream()
                .filter(page -> !page.isConcealedFor(knowledge))
                .toList();
    }

    private List<BookPageView> visibleBookPages(ResearchEntry entry, ResearchKnowledgeData knowledge) {
        return visibleBookPages(entry, knowledge, visiblePages(entry, knowledge));
    }

    private List<BookPageView> visibleBookPages(ResearchEntry entry, ResearchKnowledgeData knowledge,
            List<ResearchPage> sourcePages) {
        List<BookPageView> pages = new ArrayList<>();
        for (ResearchPage page : sourcePages) {
            if (page.type() == ResearchPage.PageType.TEXT || page.type() == ResearchPage.PageType.TEXT_CONCEALED
                    || page.type() == ResearchPage.PageType.TEXT_AND_RECIPE) {
                List<FormattedCharSequence> lines = this.font.split(pageText(page), PAGE_TEXT_WIDTH);
                if (lines.isEmpty()) {
                    pages.add(new BookPageView(page, List.of(), pages.isEmpty()));
                    continue;
                }
                int index = 0;
                while (index < lines.size()) {
                    boolean drawTitle = pages.isEmpty();
                    int lineLimit = drawTitle ? PAGE_TEXT_LINES_WITH_TITLE : PAGE_TEXT_LINES;
                    int next = Math.min(lines.size(), index + lineLimit);
                    pages.add(new BookPageView(page, List.copyOf(lines.subList(index, next)), drawTitle));
                    index = next;
                }
            } else {
                pages.add(new BookPageView(page, List.of(), pages.isEmpty()));
            }
        }
        return pages;
    }

    private int bookLeft() {
        return (this.width - scaledBookWidth()) / 2;
    }

    private int bookTop() {
        return (this.height - scaledBookHeight()) / 2;
    }

    private int bookContentLeft() {
        return (this.width - BOOK_WIDTH) / 2;
    }

    private int bookContentTop() {
        return (this.height - BOOK_HEIGHT) / 2;
    }

    private static int scaledBookWidth() {
        return Math.round(BOOK_WIDTH * BOOK_SCALE);
    }

    private static int scaledBookHeight() {
        return Math.round(BOOK_HEIGHT * BOOK_SCALE);
    }

    private static void blitBook(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height) {
        blitBook(guiGraphics, x, y, u, v, width, height, width, height);
    }

    private static void blitBook(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height,
            int destinationWidth, int destinationHeight) {
        guiGraphics.blit(BOOK, x, y, destinationWidth, destinationHeight, u * BOOK_TEXTURE_SCALE,
                v * BOOK_TEXTURE_SCALE, width * BOOK_TEXTURE_SCALE, height * BOOK_TEXTURE_SCALE, 512, 512);
    }

    private static void blitBookScaled(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height,
            float scale) {
        int scaledWidth = Math.round(width * scale);
        int scaledHeight = Math.round(height * scale);
        blitBook(guiGraphics, x - (scaledWidth - width) / 2, y - (scaledHeight - height) / 2, u, v, width, height,
                scaledWidth, scaledHeight);
    }

    private void drawCenteredFittedString(GuiGraphics guiGraphics, Component text, int centerX, int y, int maxWidth,
            int color, float baseScale) {
        int textWidth = this.font.width(text);
        float scale = Math.min(baseScale, maxWidth / (float) textWidth);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX - textWidth * scale / 2.0F, y, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawString(this.font, text, 0, 0, color, false);
        guiGraphics.pose().popPose();
    }

    private static void renderAspectIcon(GuiGraphics guiGraphics, Aspect aspect, int x, int y, int size, float alpha) {
        int color = aspect.getColor();
        guiGraphics.setColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F, alpha);
        guiGraphics.blit(Thaumcraft.id("textures/aspects/" + aspect.getTag() + ".png"), x, y, size, size, 0, 0, 32, 32,
                32, 32);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void clampMap(ResearchCategory category) {
        int minX = category.minDisplayColumn() * NODE_STEP - 85;
        int maxX = category.maxDisplayColumn() * NODE_STEP - 112;
        int minY = category.minDisplayRow() * NODE_STEP - 112;
        int maxY = category.maxDisplayRow() * NODE_STEP - 61;
        this.targetMapX = Mth.clamp(this.targetMapX, minX, Math.max(minX, maxX - 1));
        this.targetMapY = Mth.clamp(this.targetMapY, minY, Math.max(minY, maxY - 1));
        this.mapX = Mth.clamp(this.mapX, minX, Math.max(minX, maxX - 1));
        this.mapY = Mth.clamp(this.mapY, minY, Math.max(minY, maxY - 1));
    }

    private boolean inMap(double mouseX, double mouseY) {
        int left = this.left();
        int top = this.top();
        return mouseX >= left + MAP_X && mouseX < left + MAP_X + MAP_WIDTH
                && mouseY >= top + MAP_Y && mouseY < top + MAP_Y + MAP_HEIGHT;
    }

    private int nodeScreenX(int left, ResearchEntry entry) {
        return left + MAP_X + entry.displayColumn() * NODE_STEP - (int) this.mapX;
    }

    private int nodeScreenY(int top, ResearchEntry entry) {
        return top + MAP_Y + entry.displayRow() * NODE_STEP - (int) this.mapY;
    }

    private int left() {
        return (this.width - PANE_WIDTH) / 2;
    }

    private int top() {
        return (this.height - PANE_HEIGHT) / 2;
    }

    private ResearchKnowledgeData knowledge() {
        return this.minecraft != null && this.minecraft.player != null
                ? this.minecraft.player.getData(TCDataAttachments.RESEARCH_KNOWLEDGE)
                : ResearchKnowledgeData.EMPTY;
    }

    private static boolean isResearchComplete(ResearchKnowledgeData knowledge, ResearchEntry entry) {
        return knowledge.isComplete(entry.key()) || entry.hasFlag(ResearchFlag.AUTO_UNLOCK);
    }

    private record BookPageView(ResearchPage page, List<FormattedCharSequence> textLines, boolean drawTitle) {
    }

    private static int scaleBackgroundSource(int value, int min, int max, int textureSpan) {
        int span = Math.max(1, Math.abs(min - max));
        return (int) ((float) (value - min) / span * textureSpan);
    }

    private void drawResearchThread(GuiGraphics guiGraphics, int x, int y, int x2, int y2, float red, float green,
            float blue, float partialTick, boolean wiggle) {
        float count = (this.minecraft != null && this.minecraft.player != null
                ? this.minecraft.player.tickCount
                : (System.currentTimeMillis() / 50L) % 120000L) + partialTick;
        double deltaX = x - x2;
        double deltaY = y - y2;
        float distance = Mth.sqrt((float) (deltaX * deltaX + deltaY * deltaY));
        if (distance <= 0.0F) {
            return;
        }
        int increments = Math.max(1, (int) (distance / 2.0F));
        float dx = (float) (deltaX / increments);
        float dy = (float) (deltaY / increments);
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            dx *= 2.0F;
        } else {
            dy *= 2.0F;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Matrix4f matrix = guiGraphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_COLOR);
        float previousX = x;
        float previousY = y;
        float previousRed = red;
        float previousGreen = green;
        float previousBlue = blue;
        float previousAlpha = wiggle ? 0.0F : 0.6F;
        for (int index = 0; index <= increments; index++) {
            float pointRed = red;
            float pointGreen = green;
            float pointBlue = blue;
            float mx = 0.0F;
            float my = 0.0F;
            float alpha = 0.6F;
            if (wiggle) {
                float phase = (float) index / increments;
                mx = Mth.sin((count + index) / 7.0F) * 5.0F * (1.0F - phase);
                my = Mth.sin((count + index) / 5.0F) * 5.0F * (1.0F - phase);
                pointRed *= 1.0F - phase;
                pointGreen *= 1.0F - phase;
                pointBlue *= 1.0F - phase;
                alpha *= phase;
            }
            float pointX = x - dx * index + mx;
            float pointY = y - dy * index + my;
            if (index > 0) {
                addResearchThreadSegment(buffer, matrix, previousX, previousY, pointX, pointY,
                        previousRed, previousGreen, previousBlue, previousAlpha,
                        pointRed, pointGreen, pointBlue, alpha);
            }
            previousX = pointX;
            previousY = pointY;
            previousRed = pointRed;
            previousGreen = pointGreen;
            previousBlue = pointBlue;
            previousAlpha = alpha;
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                dx *= 1.0F - 1.0F / (increments * 3.0F / 2.0F);
            } else {
                dy *= 1.0F - 1.0F / (increments * 3.0F / 2.0F);
            }
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }

    private static void addResearchThreadSegment(BufferBuilder buffer, Matrix4f matrix, float startX, float startY,
            float endX, float endY, float startRed, float startGreen, float startBlue, float startAlpha,
            float endRed, float endGreen, float endBlue, float endAlpha) {
        float dx = endX - startX;
        float dy = endY - startY;
        float length = Mth.sqrt(dx * dx + dy * dy);
        if (length <= 0.001F) {
            return;
        }
        float halfWidth = 0.45F;
        float normalX = -dy / length * halfWidth;
        float normalY = dx / length * halfWidth;
        buffer.addVertex(matrix, startX - normalX, startY - normalY, 0.0F)
                .setColor(startRed, startGreen, startBlue, startAlpha);
        buffer.addVertex(matrix, startX + normalX, startY + normalY, 0.0F)
                .setColor(startRed, startGreen, startBlue, startAlpha);
        buffer.addVertex(matrix, endX + normalX, endY + normalY, 0.0F)
                .setColor(endRed, endGreen, endBlue, endAlpha);
        buffer.addVertex(matrix, endX - normalX, endY - normalY, 0.0F)
                .setColor(endRed, endGreen, endBlue, endAlpha);
    }

    private void playUiSound(SoundEvent sound, float pitch, float volume) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
        }
    }
}

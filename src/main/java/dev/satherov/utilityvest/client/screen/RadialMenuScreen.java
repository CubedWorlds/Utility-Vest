package dev.satherov.utilityvest.client.screen;

import dev.satherov.utilityvest.client.input.UVKeybindManager;
import dev.satherov.utilityvest.common.capabilities.UVVestCapability;
import dev.satherov.utilityvest.core.lang.UVLanguage;
import dev.satherov.utilityvest.network.UVNetworking;

import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RadialMenuScreen extends Screen {
    
    private static final int INNER_RADIUS = 20;
    private static final int OUTER_RADIUS = 80;
    private static final int HOVER_EXTEND = 20;
    private static final int CENTER_DEAD_ZONE = 20;
    
    private final List<RadialMenuItem> menuItems = new ArrayList<>();
    private final int banks;
    private final ItemStack vest;
    
    private int hoveredIndex = -1;
    private int row = 0;
    
    public RadialMenuScreen(ItemStack vest, int banks) {
        super(Component.literal("Radial Menu"));
        this.vest = vest;
        this.banks = banks;
        this.updateDisplay();
    }
    
    public void addMenuItem(ItemStack stack, Consumer<Boolean> action) {
        this.menuItems.add(new RadialMenuItem(stack, action));
    }
    
    @Override
    protected void init() {
        super.init();
        this.closeIfInvalid();
    }
    
    private void closeIfInvalid() {
        if (!this.validate()) {
            this.onClose();
            this.minecraft.setScreen(null);
        }
    }
    
    private boolean validate() {
        if (this.menuItems.isEmpty()) {
            if (++this.row >= this.banks) return false;
            this.updateDisplay();
            return this.validate();
        }
        return true;
    }
    
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.drawRadialOverlay(graphics, centerX, centerY);
        
        super.render(graphics, mouseX, mouseY, partialTick);
        
        this.hoveredIndex = this.getHoveredSection(mouseX, mouseY, centerX, centerY);
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        graphics.renderItem(Items.BARRIER.getDefaultInstance(), centerX - 8, centerY - 8);
        if (this.hoveredIndex < 0) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                List<Component> lines = new ArrayList<>();
                lines.add(UVLanguage.TOOLTIP_MENU_INSERT.translate(
                        ComponentUtils.wrapInSquareBrackets(InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_LEFT).getDisplayName().copy().withStyle(ChatFormatting.GOLD)),
                        player.getMainHandItem().getHoverName()
                ).withStyle(ChatFormatting.DARK_GRAY));
                lines.add(UVLanguage.TOOLTIP_MENU_INSERT.translate(
                        ComponentUtils.wrapInSquareBrackets(InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_RIGHT).getDisplayName().copy().withStyle(ChatFormatting.GOLD)),
                        player.getOffhandItem().getHoverName()
                ).withStyle(ChatFormatting.DARK_GRAY));
                lines.add(UVLanguage.TOOLTIP_MENU_CYCLE.translate(
                        ComponentUtils.wrapInSquareBrackets(UVLanguage.INPUT_WHEEL_UP.translate().withStyle(ChatFormatting.GOLD)),
                        ComponentUtils.wrapInSquareBrackets(UVLanguage.INPUT_WHEEL_DOWN.translate().withStyle(ChatFormatting.GOLD))
                ).withStyle(ChatFormatting.DARK_GRAY));
                
                graphics.renderComponentTooltip(
                        this.font,
                        lines,
                        mouseX,
                        mouseY
                );
            }
        }
        
        for (int i = 0; i < this.menuItems.size(); i++) {
            boolean isHovered = i == this.hoveredIndex;
            this.renderSection(graphics, mouseX, mouseY, centerX, centerY, i, this.menuItems.size(), isHovered, this.menuItems.get(i).stack());
        }
        
        RenderSystem.disableBlend();
    }
    
    private void drawRadialOverlay(GuiGraphics graphics, int centerX, int centerY) {
        Matrix4f matrix = graphics.pose().last().pose();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        
        buffer.addVertex(matrix, centerX, centerY, 0).setColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        int segments = 64;
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            float x = centerX + (float) Math.cos(angle);
            float y = centerY + (float) Math.sin(angle);
            
            buffer.addVertex(matrix, x, y, 0).setColor(0.0f, 0.0f, 0.0f, 0.6f);
        }
        
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }
    
    private void renderSection(GuiGraphics graphics, int mouseX, int mouseY, int centerX, int centerY, int index, int totalSections, boolean isHovered, ItemStack stack) {
        float anglePerSection = 360.0f / totalSections;
        float start = anglePerSection * index - 90;
        
        float midRad = (float) Math.toRadians(start + anglePerSection / 2);
        
        this.drawLines(graphics, centerX, centerY, start);
        
        int outerRadius = isHovered ? RadialMenuScreen.OUTER_RADIUS + RadialMenuScreen.HOVER_EXTEND : RadialMenuScreen.OUTER_RADIUS;
        
        float labelRadius = RadialMenuScreen.INNER_RADIUS + (outerRadius - RadialMenuScreen.INNER_RADIUS) * 0.6f;
        
        int x = (int) (centerX + (Math.cos(midRad) * labelRadius));
        int y = (int) (centerY + (Math.sin(midRad) * labelRadius));
        
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(this.font, stack, x, y, String.valueOf(stack.getCount()));
        
        if (!isHovered) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        
        List<Component> lines = new ArrayList<>();
        lines.add(UVLanguage.TOOLTIP_MENU_SWAP.translate(
                ComponentUtils.wrapInSquareBrackets(InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_LEFT).getDisplayName().copy().withStyle(ChatFormatting.GOLD)),
                player.getMainHandItem().getHoverName()
        ).withStyle(ChatFormatting.DARK_GRAY));
        lines.add(UVLanguage.TOOLTIP_MENU_SWAP.translate(
                ComponentUtils.wrapInSquareBrackets(InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_RIGHT).getDisplayName().copy().withStyle(ChatFormatting.GOLD)),
                player.getOffhandItem().getHoverName()
        ).withStyle(ChatFormatting.DARK_GRAY));
        lines.add(UVLanguage.TOOLTIP_MENU_CYCLE.translate(
                ComponentUtils.wrapInSquareBrackets(UVLanguage.INPUT_WHEEL_UP.translate().withStyle(ChatFormatting.GOLD)),
                ComponentUtils.wrapInSquareBrackets(UVLanguage.INPUT_WHEEL_DOWN.translate().withStyle(ChatFormatting.GOLD))
        ).withStyle(ChatFormatting.DARK_GRAY));
        
        graphics.renderComponentTooltip(
                this.font,
                lines,
                mouseX,
                mouseY
        );
    }
    
    private void drawLines(GuiGraphics graphics, float centerX, float centerY, float angle) {
        Matrix4f matrix = graphics.pose().last().pose();
        
        int argb = 0xFF878787;
        
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        
        float rad = (float) Math.toRadians(angle);
        float x1 = centerX + (float) (Math.cos(rad) * RadialMenuScreen.INNER_RADIUS);
        float y1 = centerY + (float) (Math.sin(rad) * RadialMenuScreen.INNER_RADIUS);
        float x2 = centerX + (float) (Math.cos(rad) * RadialMenuScreen.OUTER_RADIUS);
        float y2 = centerY + (float) (Math.sin(rad) * RadialMenuScreen.OUTER_RADIUS);
        
        buffer.addVertex(matrix, x1, y1, 0).setColor(argb);
        buffer.addVertex(matrix, x2, y2, 0).setColor(argb);
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(2.5f);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.lineWidth(1.0f);
    }
    
    private int getHoveredSection(int mouseX, int mouseY, int centerX, int centerY) {
        float dx = mouseX - centerX;
        float dy = mouseY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance < RadialMenuScreen.CENTER_DEAD_ZONE || distance > RadialMenuScreen.OUTER_RADIUS + RadialMenuScreen.HOVER_EXTEND) {
            return -1;
        }
        
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        
        float anglePerSection = 360.0f / this.menuItems.size();
        int section = (int) (angle / anglePerSection);
        
        return section % this.menuItems.size();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.hoveredIndex < 0) {
            UVNetworking.doSwap(this.minecraft.player, button == 0, this.vest, ItemStack.EMPTY);
            this.updateDisplay();
            return true;
        }
        
        if (this.hoveredIndex > this.menuItems.size()) return super.mouseClicked(mouseX, mouseY, button);
        
        if (button == 0 || button == 1) {
            RadialMenuItem item = this.menuItems.get(this.hoveredIndex);
            item.action().accept(button == 0);
            this.updateDisplay();
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mx, double my, double dx, double dy) {
        this.row = (int) ((Math.clamp(this.row + Math.signum(dy), 0, Integer.MAX_VALUE)) % this.banks);
        this.updateDisplay();
        this.closeIfInvalid();
        return super.mouseScrolled(mx, my, dx, dy);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (UVKeybindManager.RADIAL_KEY.matches(keyCode, scanCode)) {
            this.onClose();
        }
        return true;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private void updateDisplay() {
        this.menuItems.clear();
        
        var handler = this.vest.getCapability(Capabilities.ItemHandler.ITEM);
        if (!(handler instanceof UVVestCapability capability)) {
            this.closeIfInvalid();
            return;
        }
        
        NonNullList<ItemStack> stacks = capability.getStorage();
        if (stacks.isEmpty()) {
            this.closeIfInvalid();
            return;
        }
        
        final int startIndex = this.row * 9;
        
        for (int i = 0; i < 9; i++) {
            if (startIndex + i < stacks.size()) {
                ItemStack stack = stacks.get(startIndex + i);
                if (stack.isEmpty()) continue;
                this.addMenuItem(stack, dir -> {
                    UVNetworking.doSwap(this.minecraft.player, dir, this.vest, stack);
                    this.updateDisplay();
                });
            }
        }
    }
    
    private record RadialMenuItem(ItemStack stack, Consumer<Boolean> action) { }
}
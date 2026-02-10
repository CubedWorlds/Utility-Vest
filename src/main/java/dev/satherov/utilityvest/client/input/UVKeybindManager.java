package dev.satherov.utilityvest.client.input;

import dev.satherov.utilityvest.client.screen.RadialMenuScreen;
import dev.satherov.utilityvest.common.item.UVVestItem;
import dev.satherov.utilityvest.common.menu.UVVestMenu;
import dev.satherov.utilityvest.core.lang.UVLanguage;
import dev.satherov.utilityvest.network.OpenVestPayload;
import dev.satherov.utilityvest.network.RestockPayload;
import dev.satherov.utilityvest.network.SaveLoadPayload;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.glfw.GLFW;

public class UVKeybindManager {
    
    public static final KeyMapping GUI_KEY = register(UVLanguage.KEY_GUI, GLFW.GLFW_KEY_R);
    public static final KeyMapping RADIAL_KEY = register(UVLanguage.KEY_RADIAL, GLFW.GLFW_KEY_G);
    public static final KeyMapping RESTOCK = register(UVLanguage.KEY_RESTOCK, GLFW.GLFW_KEY_X);
    public static final KeyMapping LOAD = register(UVLanguage.KEY_LOAD, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyMapping SAVE = register(UVLanguage.KEY_SAVE, GLFW.GLFW_KEY_LEFT_ALT);
    
    private static KeyMapping register(UVLanguage key, int keyCode) {
        return new KeyMapping(key.getTranslationKey(), keyCode, UVLanguage.KEY_CATEGORY.getTranslationKey());
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        
        if (UVKeybindManager.RESTOCK.consumeClick()) {
            PacketDistributor.sendToServer(new RestockPayload(player.isCrouching()));
            return;
        }
        
        if (RADIAL_KEY.matches(event.getKey(), event.getScanCode())) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                ItemStack stack = UVVestItem.getVest(player, true);
                if (!stack.isEmpty() && stack.getItem() instanceof UVVestItem vest) {
                    RadialMenuScreen screen = new RadialMenuScreen(stack, vest.getMaxBanks());
                    mc.setScreen(screen);
                }
            }
            return;
        }
        
        if (GUI_KEY.consumeClick()) {
            if (player.containerMenu instanceof UVVestMenu) {
                player.closeContainer();
            } else {
                ItemStack stack = UVVestItem.getVest(player, true);
                if (!stack.isEmpty() && stack.getItem() instanceof UVVestItem vest) {
                    PacketDistributor.sendToServer(new OpenVestPayload(player.isCrouching(), vest.getMaxBanks()));
                }
            }
            return;
        }
        
        if (UVKeybindManager.SAVE.isDown()) {
            for (int row = 0; row < 5; row++) {
                if (event.getKey() == GLFW.GLFW_KEY_1 + row && event.getAction() == GLFW.GLFW_PRESS) {
                    PacketDistributor.sendToServer(new SaveLoadPayload(true, row));
                    return;
                }
            }
        }
        
        if (UVKeybindManager.LOAD.isDown()) {
            for (int row = 0; row < 5; row++) {
                if (event.getKey() == GLFW.GLFW_KEY_1 + row && event.getAction() == GLFW.GLFW_PRESS) {
                    PacketDistributor.sendToServer(new SaveLoadPayload(false, row));
                    return;
                }
            }
        }
    }
}


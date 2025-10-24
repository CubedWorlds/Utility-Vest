package dev.satherov.utilityvest.core.lang;

import dev.satherov.utilityvest.UtilityVest;

import net.minecraft.Util;

public enum UVLanguage implements ILangEntry {
    ITEM_GROUP("itemGroup.utilityvest"),

    NETWORK_SAVE_LOAD_FAILED("network", "save_load.failed"),
    NETWORK_OPEN_MENU_FAILED("network", "open_menu.failed"),
    NETWORK_RESTOCK_FAILED("network", "restock.failed"),

    CONTAINER_UTILITY_VEST("container", "utility_vest"),
    CONTAINER_FILTERS("container", "filters"),

    TOOLTIP_VEST_FILTER("tooltip", "vest.filter"),
    TOOLTIP_VEST_INVENTORY("tooltip", "vest.inventory"),
    TOOLTIP_VEST_HOTBAR("tooltip", "vest.hotbar"),
    TOOLTIP_VEST_RESTOCK("tooltip", "vest.restock"),

    CHAT_SAVED("chat", "saved"),
    CHAT_LOADED("chat", "loaded"),
    CHAT_RESTOCKED("chat", "restocked"),

    KEY_CATEGORY("key", "category"),
    KEY_GUI("key", "gui"),
    KEY_RESTOCK("key", "restock"),
    KEY_LOAD("key", "load"),
    KEY_SAVE("key", "save"),

    ERROR_REJECTED("error", "rejected");

    private final String key;

    UVLanguage(String type, String key) {
        this(Util.makeDescriptionId(type, UtilityVest.rl(key)));
    }

    UVLanguage(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}

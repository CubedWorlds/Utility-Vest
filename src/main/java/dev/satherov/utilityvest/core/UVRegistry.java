package dev.satherov.utilityvest.core;

import dev.satherov.utilityvest.UtilityVest;
import dev.satherov.utilityvest.common.item.UVVestItem;
import dev.satherov.utilityvest.common.menu.UVFilterMenu;
import dev.satherov.utilityvest.common.menu.UVInventoryMenu;
import dev.satherov.utilityvest.datagen.data.UVShapedRecipe;

import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class UVRegistry {
    
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UtilityVest.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, UtilityVest.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, UtilityVest.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, UtilityVest.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, UtilityVest.MOD_ID);
    
    public static final DeferredHolder<Item, UVVestItem> LEATHER_UTILITY_VEST = ITEMS.register("leather_utility_vest", () -> new UVVestItem(new Item.Properties(), 1));
    public static final DeferredHolder<Item, UVVestItem> IRON_UTILITY_VEST = ITEMS.register("iron_utility_vest", () -> new UVVestItem(new Item.Properties(), 2));
    public static final DeferredHolder<Item, UVVestItem> GOLD_UTILITY_VEST = ITEMS.register("gold_utility_vest", () -> new UVVestItem(new Item.Properties(), 3));
    public static final DeferredHolder<Item, UVVestItem> DIAMOND_UTILITY_VEST = ITEMS.register("diamond_utility_vest", () -> new UVVestItem(new Item.Properties(), 4));
    public static final DeferredHolder<Item, UVVestItem> NETHERITE_UTILITY_VEST = ITEMS.register("netherite_utility_vest", () -> new UVVestItem(new Item.Properties().fireResistant(), 5));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("utilityvest", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable(String.format("itemGroup.%s", UtilityVest.MOD_ID)))
                    .icon(() -> UVRegistry.NETHERITE_UTILITY_VEST.get().asItem().getDefaultInstance())
                    .displayItems((params, output) -> {
                        ITEMS.getEntries().stream()
                                .map(Supplier::get)
                                .map(Item::getDefaultInstance)
                                .forEach(output::accept);
                    })
                    .build()
    );
    public static final DeferredHolder<MenuType<?>, MenuType<UVInventoryMenu>> INVENTORY_MENU_ONE = MENU_TYPES.register("inventory_menu_one", () -> IMenuTypeExtension.create((id, inv, data) -> new UVInventoryMenu(id, inv, 1)));
    public static final DeferredHolder<MenuType<?>, MenuType<UVInventoryMenu>> INVENTORY_MENU_TWO = MENU_TYPES.register("inventory_menu_two", () -> IMenuTypeExtension.create((id, inv, data) -> new UVInventoryMenu(id, inv, 2)));
    public static final DeferredHolder<MenuType<?>, MenuType<UVInventoryMenu>> INVENTORY_MENU_THREE = MENU_TYPES.register("inventory_menu_three", () -> IMenuTypeExtension.create((id, inv, data) -> new UVInventoryMenu(id, inv, 3)));
    public static final DeferredHolder<MenuType<?>, MenuType<UVInventoryMenu>> INVENTORY_MENU_FOUR = MENU_TYPES.register("inventory_menu_four", () -> IMenuTypeExtension.create((id, inv, data) -> new UVInventoryMenu(id, inv, 4)));
    public static final DeferredHolder<MenuType<?>, MenuType<UVInventoryMenu>> INVENTORY_MENU_FIVE = MENU_TYPES.register("inventory_menu_five", () -> IMenuTypeExtension.create((id, inv, data) -> new UVInventoryMenu(id, inv, 5)));
    
    public static final DeferredHolder<MenuType<?>, MenuType<UVFilterMenu>> FILTER_MENU_ONE = MENU_TYPES.register("filter_menu_one", () -> IMenuTypeExtension.create((id, inv, data) -> new UVFilterMenu(id, inv, 1)));
    public static final DeferredHolder<MenuType<?>, MenuType<UVFilterMenu>> FILTER_MENU_TWO = MENU_TYPES.register("filter_menu_two", () -> IMenuTypeExtension.create((id, inv, data) -> new UVFilterMenu(id, inv, 2)));
    public static final DeferredHolder<MenuType<?>, MenuType<UVFilterMenu>> FILTER_MENU_THREE = MENU_TYPES.register("filter_menu_three", () -> IMenuTypeExtension.create((id, inv, data) -> new UVFilterMenu(id, inv, 3)));
    public static final DeferredHolder<MenuType<?>, MenuType<UVFilterMenu>> FILTER_MENU_FOUR = MENU_TYPES.register("filter_menu_four", () -> IMenuTypeExtension.create((id, inv, data) -> new UVFilterMenu(id, inv, 4)));
    public static final DeferredHolder<MenuType<?>, MenuType<UVFilterMenu>> FILTER_MENU_FIVE = MENU_TYPES.register("filter_menu_five", () -> IMenuTypeExtension.create((id, inv, data) -> new UVFilterMenu(id, inv, 5)));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> VEST_INVENTORY = DATA_COMPONENT_TYPES.register("vest_inventory", () ->
            DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .build()
    );
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> ITEM_INVENTORY = DATA_COMPONENT_TYPES.register("item_inventory", () ->
            DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .build()
    );
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> FILTER_INVENTORY = DATA_COMPONENT_TYPES.register("filter_inventory", () ->
            DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .build()
    );
    
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<UVShapedRecipe>> UPGRADE_SERIALIZER = RECIPE_SERIALIZERS.register("upgrade", UVShapedRecipe.Serializer::new);
}

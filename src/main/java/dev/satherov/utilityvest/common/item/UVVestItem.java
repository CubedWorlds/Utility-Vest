package dev.satherov.utilityvest.common.item;

import dev.satherov.utilityvest.client.input.UVKeybindManager;
import dev.satherov.utilityvest.common.menu.UVFilterMenu;
import dev.satherov.utilityvest.common.menu.UVInventoryMenu;
import dev.satherov.utilityvest.core.annotations.NothingNull;
import dev.satherov.utilityvest.core.lang.UVLanguage;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.Optional;

@NothingNull
public class UVVestItem extends Item {
    
    private final int maxBanks;
    
    public UVVestItem(Properties props, int maxBanks) {
        super(props.stacksTo(1));
        this.maxBanks = maxBanks;
    }
    
    public static ItemStack getVest(Player player, boolean checkHand) {
        return getVest(player.getInventory(), checkHand);
    }
    
    public static ItemStack getVest(Inventory inventory, boolean checkHand) {
        Player player = inventory.player;
        if (checkHand && player.getMainHandItem().getItem() instanceof UVVestItem) {
            return player.getMainHandItem();
        } else if (checkHand && player.getOffhandItem().getItem() instanceof UVVestItem) {
            return player.getOffhandItem();
        } else {
            return CuriosApi.getCuriosInventory(player)
                    .flatMap(inv -> {
                        var handler = inv.getEquippedCurios();
                        for (int i = 0; i < handler.getSlots(); i++) {
                            ItemStack s = handler.getStackInSlot(i);
                            if (s.getItem() instanceof UVVestItem) return Optional.of(s);
                        }
                        return Optional.empty();
                    }).orElse(ItemStack.EMPTY);
        }
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(UVLanguage.TOOLTIP_VEST_FILTER.translate(UVKeybindManager.GUI_KEY.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(UVLanguage.TOOLTIP_VEST_INVENTORY.translate(UVKeybindManager.GUI_KEY.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(UVLanguage.TOOLTIP_VEST_RESTOCK.translate(UVKeybindManager.RESTOCK.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(UVLanguage.TOOLTIP_VEST_RADIAL.translate(UVKeybindManager.RADIAL_KEY.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(UVLanguage.TOOLTIP_VEST_HOTBAR.translate(UVKeybindManager.SAVE.getKey().getDisplayName(), UVKeybindManager.LOAD.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        IItemHandler handler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler != null) {
            if (player.isCrouching()) {
                player.openMenu(this.getFilterMenu());
            } else {
                player.openMenu(this.getInventoryMenu());
            }
            return InteractionResultHolder.success(stack);
            
        }
        return super.use(level, player, hand);
    }
    
    public int getMaxBanks() {
        return maxBanks;
    }
    
    public MenuProvider getInventoryMenu() {
        return new SimpleMenuProvider((id, inventory, player) -> new UVInventoryMenu(id, inventory, getMaxBanks()), UVLanguage.CONTAINER_UTILITY_VEST.translate());
    }
    
    public MenuProvider getFilterMenu() {
        return new SimpleMenuProvider((id, inventory, player) -> new UVFilterMenu(id, inventory, getMaxBanks()), UVLanguage.CONTAINER_FILTERS.translate());
    }
}
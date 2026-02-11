package dev.satherov.utilityvest.common.menu;

import dev.satherov.utilityvest.common.capabilities.UVVestCapability;
import dev.satherov.utilityvest.common.item.UVVestItem;
import dev.satherov.utilityvest.core.UVRegistry;
import dev.satherov.utilityvest.core.annotations.NothingNull;

import net.neoforged.neoforge.items.SlotItemHandler;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@NothingNull
public class UVInventoryMenu extends UVVestMenu {
    
    public UVInventoryMenu(int containerId, Inventory inventory, int rows) {
        super(getMenuProvider(rows), containerId, inventory, rows);
    }
    
    private static MenuType<?> getMenuProvider(int rows) {
        return switch (rows) {
            case 1 -> UVRegistry.INVENTORY_MENU_ONE.get();
            case 2 -> UVRegistry.INVENTORY_MENU_TWO.get();
            case 3 -> UVRegistry.INVENTORY_MENU_THREE.get();
            case 4 -> UVRegistry.INVENTORY_MENU_FOUR.get();
            case 5 -> UVRegistry.INVENTORY_MENU_FIVE.get();
            default -> throw new IllegalArgumentException("Invalid row count: " + rows);
        };
    }
    
    @Override
    protected void addVestSlots(Inventory inventory, UVVestCapability handler, int yOffset) {
        
        // Vest Inventory
        for (int j = 0; j < this.rows; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new SlotItemHandler(handler.storage, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        
        super.addVestSlots(inventory, handler, yOffset);
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        final SlotItemHandler slot = (SlotItemHandler) this.slots.get(index);
        
        if (!slot.hasItem()) return ItemStack.EMPTY;
        
        final ItemStack original = slot.getItem().copy();
        ItemStack result = slot.getItem().copy();
        
        if (result.getItem() instanceof UVVestItem) {
            return ItemStack.EMPTY;
        }
        
        if (index < this.rows * 9) {
            int idx = this.rows * 9;
            while (idx < this.slots.size() && !result.isEmpty()) {
                SlotItemHandler target = (SlotItemHandler) this.slots.get(idx);
                
                if (target.mayPlace(result)) {
                    ItemStack remainder = target.getItemHandler().insertItem(target.getSlotIndex(), result, false);
                    int inserted = result.getCount() - remainder.getCount();
                    result = remainder;
                    
                    if (inserted > 0) target.setChanged();
                }
                idx++;
            }
        }
        // Move from player inventory to vest
        else {
            int idx = 0;
            while (idx < this.rows * 9 && !result.isEmpty()) {
                SlotItemHandler target = (SlotItemHandler) this.slots.get(idx);
                
                if (target.mayPlace(result)) {
                    ItemStack remainder = target.getItemHandler().insertItem(target.getSlotIndex(), result, false);
                    int inserted = result.getCount() - remainder.getCount();
                    result = remainder;
                    
                    if (inserted > 0) target.setChanged();
                }
                idx++;
            }
        }
        
        int moved = original.getCount() - result.getCount();
        if (moved > 0) {
            slot.getItemHandler().extractItem(slot.getSlotIndex(), moved, false);
            slot.setChanged();
        }
        
        return original;
    }
    
    @Override
    public void clicked(int slotId, int button, ClickType type, Player player) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            final Slot slot = this.slots.get(slotId);
            final ItemStack stack = slot.getItem();
            final ItemStack carried = this.getCarried();
            if (stack.getItem() instanceof UVVestItem) return;
            
            if (type == ClickType.SWAP && (button >= 0 && button < 9 || button == 40)) {
                ItemStack target = player.getInventory().getItem(button);
                if (target.getItem() instanceof UVVestItem) return;
            }
            
            if (slotId < (this.rows * 9)) {
                ClickAction action = button == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
                if (stack.overrideStackedOnOther(slot, action, player) || stack.overrideOtherStackedOnMe(carried, slot, action, player, this.createAccess())) return;
            }
        }
        
        super.clicked(slotId, button, type, player);
    }
    
    private SlotAccess createAccess() {
        return new SlotAccess() {
            @Override
            public ItemStack get() {
                return UVInventoryMenu.this.getCarried();
            }
            
            @Override
            public boolean set(ItemStack stack) {
                UVInventoryMenu.this.setCarried(stack);
                return true;
            }
        };
    }
}

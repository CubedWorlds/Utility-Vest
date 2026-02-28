package dev.satherov.utilityvest.network;

import dev.satherov.utilityvest.UtilityVest;
import dev.satherov.utilityvest.common.capabilities.UVVestCapability;
import dev.satherov.utilityvest.common.item.UVVestItem;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record SwapToolPayload(boolean main, int idx) implements CustomPacketPayload {
    
    public static final StreamCodec<RegistryFriendlyByteBuf, SwapToolPayload> STREAM_CODEC =
            CustomPacketPayload.codec(SwapToolPayload::encode, SwapToolPayload::new);
    
    public static final Type<SwapToolPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(UtilityVest.MOD_ID, "swap_tool")
    );
    
    private SwapToolPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readInt());
    }
    
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(this.main());
        buf.writeInt(this.idx());
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static class Handler {
        public static void handle(SwapToolPayload msg, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                    Handler.execute(player, msg.main(), UVVestItem.getVest(player, false), msg.idx());
                }
            });
        }
        
        public static void execute(Player player, boolean main, ItemStack stack, int idx) {
            IItemHandler handler = stack.getCapability(Capabilities.ItemHandler.ITEM);
            if (!stack.isEmpty() && stack.getItem() instanceof UVVestItem && handler instanceof UVVestCapability capability) {
                if (main) {
                    ItemStack held = player.getMainHandItem();
                    if (held.getItem() instanceof UVVestItem) return;
                    player.setItemInHand(InteractionHand.MAIN_HAND, capability.swap(held, idx));
                } else {
                    ItemStack held = player.getOffhandItem();
                    if (held.getItem() instanceof UVVestItem) return;
                    player.setItemInHand(InteractionHand.OFF_HAND, capability.swap(held, idx));
                }
            }
        }
    }
}

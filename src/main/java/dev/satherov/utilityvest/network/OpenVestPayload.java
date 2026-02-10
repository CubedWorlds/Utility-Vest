package dev.satherov.utilityvest.network;

import dev.satherov.utilityvest.UtilityVest;
import dev.satherov.utilityvest.common.item.UVVestItem;
import dev.satherov.utilityvest.core.lang.UVLanguage;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record OpenVestPayload(boolean filter, int maxBanks) implements CustomPacketPayload {
    
    public static final StreamCodec<FriendlyByteBuf, OpenVestPayload> STREAM_CODEC =
            CustomPacketPayload.codec(OpenVestPayload::encode, OpenVestPayload::new);
    
    public static final Type<OpenVestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(UtilityVest.MOD_ID, "open_vest_menu")
    );
    
    private OpenVestPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readInt());
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.filter);
        buf.writeInt(this.maxBanks);
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static class Handler {
        public static void handle(OpenVestPayload msg, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (!ctx.flow().isServerbound() || !(ctx.player() instanceof ServerPlayer player)) {
                    return;
                }
                
                ItemStack stack = UVVestItem.getVest(player, true);
                
                if (!stack.isEmpty() && stack.getItem() instanceof UVVestItem vest) {
                    if (msg.filter) {
                        player.openMenu(vest.getFilterMenu());
                    } else {
                        player.openMenu(vest.getInventoryMenu());
                    }
                }
                
            }).exceptionally(e -> {
                ctx.disconnect(UVLanguage.NETWORK_OPEN_MENU_FAILED.translate(e.getMessage()));
                return null;
            });
        }
    }
}
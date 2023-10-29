package ca.lukegrahamlandry.eternalstructures.network.serverbound;

import ca.lukegrahamlandry.eternalstructures.game.tile.ProtectionTile;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenProtectionSettings;
import ca.lukegrahamlandry.eternalstructures.protect.ProtectionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SaveProtectionSettings {
    BlockPos pos;
    ProtectionInstance.Settings data;

    public SaveProtectionSettings(BlockPos pos, ProtectionInstance.Settings data){
        this.pos = pos;
        this.data = data;
    }

    public static SaveProtectionSettings decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        String json = buf.readUtf(OpenProtectionSettings.MAX_LEN);
        ProtectionInstance.Settings data = JsonHelper.get().fromJson(json, ProtectionInstance.Settings.class);
        return new SaveProtectionSettings(pos, data);
    }

    public static void encode(SaveProtectionSettings packet, PacketBuffer buf) {
        buf.writeBlockPos(packet.pos);
        String json = JsonHelper.get().toJson(packet.data);
        buf.writeUtf(json, OpenProtectionSettings.MAX_LEN);
    }

    public static void handle(SaveProtectionSettings packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) return;
            if (!player.isCreative()){
                player.displayClientMessage(new StringTextComponent("Only creative mode players can edit protection settings."), true);
                return;
            }

            TileEntity tile = player.level.getBlockEntity(packet.pos);
            if (tile instanceof ProtectionTile) {
                String msg = packet.data.validate();
                if (!msg.isEmpty()) {
                    player.displayClientMessage(new StringTextComponent(msg), false);
                }

                ((ProtectionTile) tile).setSettings(packet.data);
                player.displayClientMessage(new StringTextComponent("Protection settings have been updated."), true);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

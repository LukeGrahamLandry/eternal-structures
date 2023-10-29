package ca.lukegrahamlandry.eternalstructures.network.serverbound;

import ca.lukegrahamlandry.eternalstructures.game.tile.SummoningTile;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenProtectionSettings;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SaveSummonSettings {
    BlockPos pos;
    SummoningTile.Settings data;

    public SaveSummonSettings(BlockPos pos, SummoningTile.Settings data){
        this.pos = pos;
        this.data = data;
    }

    public static SaveSummonSettings decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        String json = buf.readUtf(OpenProtectionSettings.MAX_LEN);
        SummoningTile.Settings data = JsonHelper.get().fromJson(json, SummoningTile.Settings.class);
        return new SaveSummonSettings(pos, data);
    }

    public static void encode(SaveSummonSettings packet, PacketBuffer buf) {
        buf.writeBlockPos(packet.pos);
        String json = JsonHelper.get().toJson(packet.data);
        buf.writeUtf(json, OpenProtectionSettings.MAX_LEN);
    }

    public static void handle(SaveSummonSettings packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) return;
            if (!player.isCreative()){
                player.displayClientMessage(new StringTextComponent("Only creative mode players can edit settings."), true);
                return;
            }

            TileEntity tile = player.level.getBlockEntity(packet.pos);
            if (tile instanceof SummoningTile) {
                String msg = packet.data.validate();
                if (!msg.isEmpty()) {
                    player.displayClientMessage(new StringTextComponent(msg), false);
                }

                ((SummoningTile) tile).setSettings(packet.data);
                player.displayClientMessage(new StringTextComponent("Settings have been updated."), true);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

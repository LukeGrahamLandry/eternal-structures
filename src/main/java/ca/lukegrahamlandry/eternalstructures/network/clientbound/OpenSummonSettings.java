package ca.lukegrahamlandry.eternalstructures.network.clientbound;

import ca.lukegrahamlandry.eternalstructures.game.tile.SummoningTile;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// TODO: this is a copy-paste from protection
public class OpenSummonSettings {
    BlockPos pos;
    SummoningTile.Settings data;
    public static final int MAX_LEN = 32767;

    public OpenSummonSettings(BlockPos pos, SummoningTile.Settings data){
        this.pos = pos;
        this.data = data;
    }

    public static OpenSummonSettings decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        String json = buf.readUtf(MAX_LEN);
        SummoningTile.Settings data = JsonHelper.get().fromJson(json, SummoningTile.Settings.class);
        return new OpenSummonSettings(pos, data);
    }

    public static void encode(OpenSummonSettings packet, PacketBuffer buf) {
        buf.writeBlockPos(packet.pos);
        String json = JsonHelper.get().toJson(packet.data);
        buf.writeUtf(json, MAX_LEN);
    }

    public static void handle(OpenSummonSettings packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPacketHandlers.handle(packet));
        ctx.get().setPacketHandled(true);
    }
}

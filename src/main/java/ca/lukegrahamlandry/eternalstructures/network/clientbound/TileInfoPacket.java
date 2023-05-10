package ca.lukegrahamlandry.eternalstructures.network.clientbound;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TileInfoPacket {
    BlockPos pos;
    String data;

    public TileInfoPacket(BlockPos pos, String data){
        this.pos = pos;
        this.data = data;
    }
    public static TileInfoPacket decode(PacketBuffer buf) {
        return new TileInfoPacket(buf.readBlockPos(), buf.readUtf());
    }

    public static void encode(TileInfoPacket packet, PacketBuffer buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeUtf(packet.data);
    }

    public static void handle(TileInfoPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPacketHandlers.handle(packet));
        ctx.get().setPacketHandled(true);
    }

    public interface Receiver {
        void onInfoPacket(String s);
    }
}

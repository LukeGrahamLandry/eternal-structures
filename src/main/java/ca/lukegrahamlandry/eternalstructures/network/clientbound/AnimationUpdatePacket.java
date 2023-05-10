package ca.lukegrahamlandry.eternalstructures.network.clientbound;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AnimationUpdatePacket {
    BlockPos pos;
    int animationState;

    public AnimationUpdatePacket(BlockPos pos, int animationState){
        this.pos = pos;
        this.animationState = animationState;
    }
    public static AnimationUpdatePacket decode(PacketBuffer buf) {
        return new AnimationUpdatePacket(buf.readBlockPos(), buf.readInt());
    }

    public static void encode(AnimationUpdatePacket packet, PacketBuffer buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeInt(packet.animationState);
    }

    public static void handle(AnimationUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPacketHandlers.handle(packet));
        ctx.get().setPacketHandled(true);
    }
}

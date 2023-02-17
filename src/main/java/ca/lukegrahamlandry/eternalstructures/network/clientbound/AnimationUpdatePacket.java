package ca.lukegrahamlandry.eternalstructures.network.clientbound;

import ca.lukegrahamlandry.eternalstructures.game.tile.DungeonDoorTile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
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
        ctx.get().enqueueWork(() -> handle(packet));
        ctx.get().setPacketHandled(true);
    }

    private static void handle(AnimationUpdatePacket packet) {
        if (Minecraft.getInstance().level == null) return;
        
        TileEntity tile = Minecraft.getInstance().level.getBlockEntity(packet.pos);
        if (tile instanceof DungeonDoorTile){
            ((DungeonDoorTile) tile).setClientAnimationState(packet.animationState);
        }
    }
}

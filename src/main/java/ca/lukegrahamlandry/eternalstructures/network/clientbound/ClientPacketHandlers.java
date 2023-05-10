package ca.lukegrahamlandry.eternalstructures.network.clientbound;

import ca.lukegrahamlandry.eternalstructures.client.gui.ProtectionSettingsGui;
import ca.lukegrahamlandry.eternalstructures.game.tile.DungeonDoorTile;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

// Makes sure that client only classes are never loaded on the server.
// Forge seems to load any classes referenced in the packet handling classes even if they aren't actually invoked.
public class ClientPacketHandlers {
    public static void handle(OpenProtectionSettings packet) {
        if (Minecraft.getInstance().level == null) return;
        Minecraft.getInstance().setScreen(new ProtectionSettingsGui(packet.pos, JsonHelper.get().toJson(packet.data)));
    }

    public static void handle(TileInfoPacket packet) {
        if (Minecraft.getInstance().level == null) return;

        TileEntity tile = Minecraft.getInstance().level.getBlockEntity(packet.pos);
        if (tile instanceof TileInfoPacket.Receiver){
            ((TileInfoPacket.Receiver) tile).onInfoPacket(packet.data);
        }
    }

    public static void handle(AnimationUpdatePacket packet) {
        if (Minecraft.getInstance().level == null) return;

        TileEntity tile = Minecraft.getInstance().level.getBlockEntity(packet.pos);
        if (tile instanceof DungeonDoorTile){
            ((DungeonDoorTile) tile).setClientAnimationState(packet.animationState);
        }
    }
}

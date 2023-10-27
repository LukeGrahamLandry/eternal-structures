package ca.lukegrahamlandry.eternalstructures.network.clientbound;

import ca.lukegrahamlandry.eternalstructures.client.gui.JsonConfigGui;
import ca.lukegrahamlandry.eternalstructures.game.tile.DungeonDoorTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.SummoningTile;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.network.NetworkHandler;
import ca.lukegrahamlandry.eternalstructures.network.serverbound.SaveProtectionSettings;
import ca.lukegrahamlandry.eternalstructures.network.serverbound.SaveSummonSettings;
import ca.lukegrahamlandry.eternalstructures.protect.ProtectionInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;

// Makes sure that client only classes are never loaded on the server.
// Forge seems to load any classes referenced in the packet handling classes even if they aren't actually invoked.
public class ClientPacketHandlers {
    public static void handle(OpenProtectionSettings packet) {
        if (Minecraft.getInstance().level == null) return;
        Minecraft.getInstance().setScreen(new JsonConfigGui<ProtectionInstance.Settings>(
                packet.pos, JsonHelper.get().toJson(packet.data), ProtectionInstance.Settings.class,
                (pos, data) -> {
                    NetworkHandler.INSTANCE.sendToServer(new SaveProtectionSettings(pos, data));
                },
                new StringTextComponent("Structure Protection Settings")
        ));
    }

    public static void handle(OpenSummonSettings packet) {
        if (Minecraft.getInstance().level == null) return;
        Minecraft.getInstance().setScreen(new JsonConfigGui<SummoningTile.Settings>(
                packet.pos, JsonHelper.get().toJson(packet.data), SummoningTile.Settings.class,
                (pos, data) -> {
                    NetworkHandler.INSTANCE.sendToServer(new SaveSummonSettings(pos, data));
                },
                new StringTextComponent("Summoning Altar Settings")
        ));
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

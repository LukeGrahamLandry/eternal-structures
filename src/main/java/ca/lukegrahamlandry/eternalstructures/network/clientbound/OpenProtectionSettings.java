package ca.lukegrahamlandry.eternalstructures.network.clientbound;

import ca.lukegrahamlandry.eternalstructures.client.gui.ProtectionSettingsGui;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.protect.ProtectionInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenProtectionSettings {
    BlockPos pos;
    ProtectionInstance.Settings data;
    public static final int MAX_LEN = 32767;

    public OpenProtectionSettings(BlockPos pos, ProtectionInstance.Settings data){
        this.pos = pos;
        this.data = data;
    }

    public static OpenProtectionSettings decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        String json = buf.readUtf(MAX_LEN);
        ProtectionInstance.Settings data = JsonHelper.get().fromJson(json, ProtectionInstance.Settings.class);
        return new OpenProtectionSettings(pos, data);
    }

    public static void encode(OpenProtectionSettings packet, PacketBuffer buf) {
        buf.writeBlockPos(packet.pos);
        String json = JsonHelper.get().toJson(packet.data);
        buf.writeUtf(json, MAX_LEN);
    }

    public static void handle(OpenProtectionSettings packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> handle(packet));
        ctx.get().setPacketHandled(true);
    }

    private static void handle(OpenProtectionSettings packet) {
        if (Minecraft.getInstance().level == null) return;
        Minecraft.getInstance().setScreen(new ProtectionSettingsGui(packet.pos, JsonHelper.get().toJson(packet.data)));
    }
}

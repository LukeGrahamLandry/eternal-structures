package ca.lukegrahamlandry.eternalstructures.network;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.AnimationUpdatePacket;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenProtectionSettings;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenSummonSettings;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.TileInfoPacket;
import ca.lukegrahamlandry.eternalstructures.network.serverbound.SaveProtectionSettings;
import ca.lukegrahamlandry.eternalstructures.network.serverbound.SaveSummonSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;
    // Only needs to be changed when protocol changes but registry objects don't (because forge checks that)
    private static final String VERSION = "1.0";  // Not a semver!

    public static void registerMessages(){
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ModMain.MOD_ID, "packets"), () -> VERSION, VERSION::equals, VERSION::equals);

        // TODO: AnimationUpdatePacket and TileInfoPacket are the same thing.
        INSTANCE.registerMessage(ID++, AnimationUpdatePacket.class, AnimationUpdatePacket::encode, AnimationUpdatePacket::decode, AnimationUpdatePacket::handle);
        INSTANCE.registerMessage(ID++, TileInfoPacket.class, TileInfoPacket::encode, TileInfoPacket::decode, TileInfoPacket::handle);
        INSTANCE.registerMessage(ID++, OpenProtectionSettings.class, OpenProtectionSettings::encode, OpenProtectionSettings::decode, OpenProtectionSettings::handle);
        INSTANCE.registerMessage(ID++, SaveProtectionSettings.class, SaveProtectionSettings::encode, SaveProtectionSettings::decode, SaveProtectionSettings::handle);
        INSTANCE.registerMessage(ID++, OpenSummonSettings.class, OpenSummonSettings::encode, OpenSummonSettings::decode, OpenSummonSettings::handle);
        INSTANCE.registerMessage(ID++, SaveSummonSettings.class, SaveSummonSettings::encode, SaveSummonSettings::decode, SaveSummonSettings::handle);
    }
}

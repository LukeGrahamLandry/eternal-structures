package ca.lukegrahamlandry.eternalstructures.network;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.AnimationUpdatePacket;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.TileInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static void registerMessages(){
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ModMain.MOD_ID, "packets"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(ID++, AnimationUpdatePacket.class, AnimationUpdatePacket::encode, AnimationUpdatePacket::decode, AnimationUpdatePacket::handle);
        INSTANCE.registerMessage(ID++, TileInfoPacket.class, TileInfoPacket::encode, TileInfoPacket::decode, TileInfoPacket::handle);
    }

}
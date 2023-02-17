package ca.lukegrahamlandry.eternalstructures;

import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.network.NetworkHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

@Mod(ModMain.MOD_ID)
public class ModMain {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "eternalstructures";

    public ModMain() {
        GeckoLib.initialize();
        ModRegistry.init(FMLJavaModLoadingContext.get().getModEventBus());
        NetworkHandler.registerMessages();
    }
}

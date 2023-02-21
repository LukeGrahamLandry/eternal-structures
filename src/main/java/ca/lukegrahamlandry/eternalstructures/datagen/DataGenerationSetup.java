package ca.lukegrahamlandry.eternalstructures.datagen;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerationSetup {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existing = event.getExistingFileHelper();

        if (event.includeClient()){
            gen.addProvider(new ModBlockStateProvider(gen, existing));
        }
        if (event.includeServer()){
            gen.addProvider(new ModLootTableProvider(gen));
        }
    }
}

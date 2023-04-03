package ca.lukegrahamlandry.eternalstructures;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ESConfig {
    private static final ForgeConfigSpec.Builder server_builder = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec server_config;

    public static ForgeConfigSpec.BooleanValue allowClosingUnlockedDungeonDoors;
    public static ForgeConfigSpec.IntValue spikeDetectionRange;
    public static ForgeConfigSpec.IntValue spikeUpdateDelay;

    private static void init(){
        allowClosingUnlockedDungeonDoors = server_builder
                .comment("Lets dungeon doors be toggled like normal doors once unlocked.")
                .define("allowClosingUnlockedDungeonDoors", false);

        spikeDetectionRange = server_builder
                .comment("Radius of the cube in blocks.")
                .defineInRange("spikeDetectionRange", 3, 0, Integer.MAX_VALUE);

        spikeUpdateDelay = server_builder
                .comment("Spikes will scan for players every x ticks and update their state based on that after an x tick delay.")
                .defineInRange("spikeUpdateDelay", 5, 1, Integer.MAX_VALUE);

        server_config = server_builder.build();
    }

    public static void loadConfig(){
        init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, server_config);
        String path = FMLPaths.CONFIGDIR.get().resolve(ModMain.MOD_ID + "-server.toml").toString();
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        server_config.setConfig(file);
    }
}
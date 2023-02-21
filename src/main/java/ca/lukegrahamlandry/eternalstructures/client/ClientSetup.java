package ca.lukegrahamlandry.eternalstructures.client;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.client.render.DungeonDoorRenderer;
import ca.lukegrahamlandry.eternalstructures.client.render.LootBlockRenderer;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid=ModMain.MOD_ID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        ClientRegistry.bindTileEntityRenderer(ModRegistry.Tiles.DUNGEON_DOOR.get(), DungeonDoorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModRegistry.Tiles.LOOT.get(), LootBlockRenderer::new);

        for (RegistryObject<Block> block : ModRegistry.Blocks.SPIKES){
            RenderTypeLookup.setRenderLayer(block.get(), RenderType.cutout());
        }
    }
}

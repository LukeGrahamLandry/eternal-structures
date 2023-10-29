package ca.lukegrahamlandry.eternalstructures.client;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.client.render.GenericGeoModel;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

@Mod.EventBusSubscriber(modid=ModMain.MOD_ID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        ClientRegistry.bindTileEntityRenderer(ModRegistry.Tiles.DUNGEON_DOOR.get(), (ctx) -> new BlockRender(ctx, new GenericGeoModel<>(
                "door.png",
                "door.geo.json",
                "door.animation.json"
        )));
        ClientRegistry.bindTileEntityRenderer(ModRegistry.Tiles.LOOT.get(), (ctx) -> new BlockRender(ctx, new GenericGeoModel.Wrap<>()));
        ClientRegistry.bindTileEntityRenderer(ModRegistry.Tiles.SUMMONING_ALTAR.get(), (ctx) -> new BlockRender(ctx, new GenericGeoModel.Wrap<>()));

        for (RegistryObject<Block> block : ModRegistry.Blocks.SPIKES){
            RenderTypeLookup.setRenderLayer(block.get(), RenderType.cutout());
        }
    }


    public static class BlockRender<T extends TileEntity & IAnimatable> extends GeoBlockRenderer<T> {
        public BlockRender(TileEntityRendererDispatcher ctx, AnimatedGeoModel<T> model) {
            super(ctx, model);
        }
    }
}

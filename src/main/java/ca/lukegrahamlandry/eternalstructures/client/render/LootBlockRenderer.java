package ca.lukegrahamlandry.eternalstructures.client.render;

import ca.lukegrahamlandry.eternalstructures.game.tile.DungeonDoorTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.LootTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class LootBlockRenderer extends GeoBlockRenderer<LootTile> {
    public LootBlockRenderer(TileEntityRendererDispatcher ctx) {
        super(ctx, new GenericGeoModel.Wrap<>());
    }

    @Override
    public void render(LootTile tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(tile, partialTicks, stack, bufferIn, packedLightIn);  // fixes the weird shadow but im sure there's a better way
    }
}

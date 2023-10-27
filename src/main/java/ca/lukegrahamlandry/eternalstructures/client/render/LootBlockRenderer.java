package ca.lukegrahamlandry.eternalstructures.client.render;

import ca.lukegrahamlandry.eternalstructures.game.tile.LootTile;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class LootBlockRenderer extends GeoBlockRenderer<LootTile> {
    public LootBlockRenderer(TileEntityRendererDispatcher ctx) {
        super(ctx, new GenericGeoModel.Wrap<>());
    }
}

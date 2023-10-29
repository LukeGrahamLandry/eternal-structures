package ca.lukegrahamlandry.eternalstructures.client.render;

import ca.lukegrahamlandry.eternalstructures.game.tile.DungeonDoorTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.LootTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.SummoningTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class SummonAlterRender extends GeoBlockRenderer<SummoningTile> {
    public SummonAlterRender(TileEntityRendererDispatcher ctx) {
        super(ctx, new GenericGeoModel.Wrap<>());
    }
}

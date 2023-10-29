package ca.lukegrahamlandry.eternalstructures.client.render;

import ca.lukegrahamlandry.eternalstructures.game.tile.DungeonDoorTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class DungeonDoorRenderer extends GeoBlockRenderer<DungeonDoorTile> {
    public DungeonDoorRenderer(TileEntityRendererDispatcher ctx) {
        super(ctx, new GenericGeoModel<>(
                "door.png",
                "door.geo.json",
                "door.animation.json"
        ));
    }
}

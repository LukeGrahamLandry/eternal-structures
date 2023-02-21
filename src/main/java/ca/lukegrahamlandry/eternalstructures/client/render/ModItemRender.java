package ca.lukegrahamlandry.eternalstructures.client.render;

import ca.lukegrahamlandry.eternalstructures.game.item.GeoBlockItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class ModItemRender extends GeoItemRenderer<GeoBlockItem> {
    public ModItemRender() {
        super(new GenericGeoModel.Wrap<>());
    }
}
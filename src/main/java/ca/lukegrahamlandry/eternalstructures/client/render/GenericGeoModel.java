package ca.lukegrahamlandry.eternalstructures.client.render;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.client.IGeoInfo;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GenericGeoModel<T extends IAnimatable> extends AnimatedGeoModel<T> {
    private final ResourceLocation modelResource;
    private final ResourceLocation textureResource;
    private final ResourceLocation animationResource;

    public GenericGeoModel(String texture, String model, String animation){
        if (!texture.endsWith(".png") || !model.endsWith(".json") || !animation.endsWith(".json")){
            throw new IllegalStateException("GenericGeoModel resource paths must end with valid file extension.");
        }

        modelResource = new ResourceLocation(ModMain.MOD_ID, "geo/" + model);
        textureResource = new ResourceLocation(ModMain.MOD_ID, "textures/" + texture);
        animationResource = new ResourceLocation(ModMain.MOD_ID, "animations/" + animation);
    }

    @Override
    public ResourceLocation getModelLocation(T object) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureLocation(T object) {
        return textureResource;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T object) {
        return animationResource;
    }

    public static class Wrap<T extends IAnimatable & IGeoInfo> extends AnimatedGeoModel<T>{
        @Override
        public ResourceLocation getModelLocation(T object) {
            return object.getModelResource();
        }

        @Override
        public ResourceLocation getTextureLocation(T object) {
            return object.getTextureResource();
        }

        @Override
        public ResourceLocation getAnimationFileLocation(T object) {
            return object.getAnimationResource();
        }
    }
}

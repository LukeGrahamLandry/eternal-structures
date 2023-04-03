package ca.lukegrahamlandry.eternalstructures.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class JsonHelper {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(BlockPos.class, new WrapperLibAdapters.BlockPosTypeAdapter())
            .registerTypeAdapter(CompoundNBT.class, new WrapperLibAdapters.NbtTypeAdapter())
            .registerTypeAdapter(ItemStack.class, new WrapperLibAdapters.ItemStackTypeAdapter())
            .registerTypeAdapter(EffectInstance.class, new WrapperLibAdapters.EffectTypeAdapter())
            .create();

    public static Gson get() {
        return GSON;
    }
}

package ca.lukegrahamlandry.eternalstructures.datagen;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.game.block.SpikesBlock;
import ca.lukegrahamlandry.eternalstructures.game.item.GeoBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ModMain.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (RegistryObject<Block> block : ModRegistry.Blocks.SPIKES){
            String name = block.get().getRegistryName().getPath();

            // in/out block models with the correct texture
            models().withExistingParent(name + "_in", ModMain.MOD_ID + ":spikes_in").texture("texture", name);
            models().withExistingParent(name + "_out", ModMain.MOD_ID + ":spikes_out").texture("texture", name);

            // item model with the correct texture
            itemModels().withExistingParent(name, ModMain.MOD_ID + ":block/" + name + "_out");

            // mapping to the correct version of the model based on in/out state
            Function<BlockState, ModelFile> isOut = (state) ->
                    state.getValue(SpikesBlock.SPIKES_OUT) ?
                            models().getExistingFile(new ResourceLocation(ModMain.MOD_ID, name + "_out")) :
                            models().getExistingFile(new ResourceLocation(ModMain.MOD_ID, name + "_in"));

            // block state file that accounts for the facing direction and <isOut>
            directionalBlock(block.get(), isOut);
        }

        for (RegistryObject<Block> block : ModRegistry.Blocks.LOOT){
            itemModels().getBuilder(block.getId().getPath()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                    .transforms().transform(ModelBuilder.Perspective.GUI).translation(0, -6, 0).scale(0.75F);
        }
    }
}

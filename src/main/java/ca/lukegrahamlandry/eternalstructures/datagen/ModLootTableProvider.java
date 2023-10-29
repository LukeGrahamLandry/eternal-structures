package ca.lukegrahamlandry.eternalstructures.datagen;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(DataGenerator p_i50789_1_) {
        super(p_i50789_1_);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {

    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return Arrays.asList(
                Pair.of(BlockTables::new, LootParameterSets.BLOCK),
                Pair.of(ChestTables::new, LootParameterSets.CHEST)
        );
    }

    static class BlockTables extends BlockLootTables {
        @Override
        protected void addTables() {
            for (RegistryObject<Block> block : ModRegistry.Blocks.LOOT){
                dropSelf(block.get());
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModRegistry.Blocks.LOOT.stream().map(RegistryObject::get).collect(Collectors.toSet());
        }
    }

    static class ChestTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> tables) {
            for (RegistryObject<Block> block : ModRegistry.Blocks.LOOT){
                tables.accept(new ResourceLocation(ModMain.MOD_ID, "chests/" + block.getId().getPath()),
                        LootTable.lootTable()
                                .withPool(LootPool.lootPool().setRolls(RandomValueRange.between(5, 15))
                                        .add(ItemLootEntry.lootTableItem(Items.GOLD_INGOT).setWeight(5))
                                        .add(ItemLootEntry.lootTableItem(Items.GOLDEN_APPLE).setWeight(1))));
            }
        }
    }
}

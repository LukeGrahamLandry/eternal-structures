package ca.lukegrahamlandry.eternalstructures.game;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.game.block.*;
import ca.lukegrahamlandry.eternalstructures.game.item.DungeonKeyItem;
import ca.lukegrahamlandry.eternalstructures.game.item.GeoBlockItem;
import ca.lukegrahamlandry.eternalstructures.game.tile.DungeonDoorTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.LootTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.PlaceHolderTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.ProtectionTile;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModRegistry {
    public static void init(IEventBus bus){
        Items.REGISTRY.register(bus);
        Blocks.REGISTRY.register(bus);
        Tiles.REGISTRY.register(bus);
    }

    public static class Blocks {
        private static DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MOD_ID);

        public static RegistryObject<Block> DUNGEON_DOOR = REGISTRY.register("dungeon_door", DungeonDoorBlock::new);
        public static RegistryObject<Block> PLACE_HOLDER = REGISTRY.register("place_holder", PlaceHolderBlock::new);
        public static RegistryObject<Block> PROTECTION = REGISTRY.register("protection", ProtectionBlock::new);

        private static RegistryObject<Block> createSpike(int damage, DamageSource damageType, String name){
            return REGISTRY.register(name + "_spikes", () -> new SpikesBlock(damage, damageType));
        }

        private static void createLoot(String name, LootTile.Type type){
            LOOT.add(REGISTRY.register(name, () -> new LootBlock(type)));
        }

        public static List<RegistryObject<Block>> SPIKES = Arrays.asList(
            createSpike(2, DamageSource.GENERIC, "ice"),
            createSpike(1, DamageSource.GENERIC, "wood"),
            createSpike(4, DamageSource.MAGIC, "amethyst"),
            createSpike(5, DamageSource.GENERIC, "copper"),
            createSpike(5, DamageSource.GENERIC, "iron"),
            createSpike(3, DamageSource.GENERIC, "oxi_copper"),
            createSpike(5, DamageSource.GENERIC, "bloody_iron")
        );

        public static List<RegistryObject<Block>> LOOT = new ArrayList<>();

        static {
            Arrays.asList("clay", "marble", "mushroom", "nether", "rusted", "stone", "water").forEach((name) -> {
                createLoot(name + "_pot", LootTile.Type.POT);
            });
            createLoot("crystal_pot", LootTile.Type.CRYSTAL_POT);
            createLoot("golden_pot", LootTile.Type.GOLDEN_POT);
            createLoot("mushroom_chest", LootTile.Type.CHEST);
            createLoot("corrupted_chest", LootTile.Type.CHEST);
        }

    }

    public static class Items {
        private static DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);

        public static RegistryObject<Item> SKULL_KEY = REGISTRY.register("skull_key", DungeonKeyItem::new);
        public static RegistryObject<Item> SLIME_SKULL_KEY = REGISTRY.register("slime_skull_key", DungeonKeyItem::new);

        public static RegistryObject<Item> DUNGEON_DOOR = REGISTRY.register("dungeon_door", () -> new BlockItem(Blocks.DUNGEON_DOOR.get(), PROPS));
        public static RegistryObject<Item> PROTECTION = REGISTRY.register("protection", () -> new BlockItem(Blocks.PROTECTION.get(), PROPS));

        static {
            for (RegistryObject<Block> block : Blocks.SPIKES){
                REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), PROPS));
            }
            for (RegistryObject<Block> block : Blocks.LOOT){
                REGISTRY.register(block.getId().getPath(), () -> new GeoBlockItem(block.get(), PROPS));
            }
        }
    }

    public static class Tiles {
        private static DeferredRegister<TileEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ModMain.MOD_ID);

        public static RegistryObject<TileEntityType<?>> DUNGEON_DOOR = REGISTRY.register("dungeon_door",
                () -> TileEntityType.Builder.of(DungeonDoorTile::new, Blocks.DUNGEON_DOOR.get()).build(null));

        public static RegistryObject<TileEntityType<?>> PLACE_HOLDER = REGISTRY.register("place_holder",
                () -> TileEntityType.Builder.of(PlaceHolderTile::new, Blocks.PLACE_HOLDER.get()).build(null));

        public static RegistryObject<TileEntityType<?>> LOOT = REGISTRY.register("loot",
                () -> TileEntityType.Builder.of(LootTile::create, Blocks.LOOT.stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));

        public static RegistryObject<TileEntityType<?>> PROTECTION = REGISTRY.register("protection",
                () -> TileEntityType.Builder.of(ProtectionTile::new, Blocks.PROTECTION.get()).build(null));
    }

    public static final ItemGroup TAB = new ItemGroup(0, ModMain.MOD_ID) {
        public ItemStack makeIcon() {
            return new ItemStack(Items.SKULL_KEY.get());
        }
    };

    public static final Item.Properties PROPS = new Item.Properties().tab(ModRegistry.TAB);
}

package ca.lukegrahamlandry.eternalstructures.game.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noobanidus.mods.lootr.util.ChestUtil;

public class LootrHelper {
    public static LootTile create(){
        return new LootrLootTile();
    }

    public static void click(Block block, World level, PlayerEntity player, BlockPos pos) {
        if (player.isShiftKeyDown()) {
            ChestUtil.handleLootSneak(block, level, pos, player);
        } else {
            ChestUtil.handleLootChest(block, level, pos, player);
        }
    }
}

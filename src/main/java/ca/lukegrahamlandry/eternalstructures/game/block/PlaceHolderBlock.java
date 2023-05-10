package ca.lukegrahamlandry.eternalstructures.game.block;

import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.game.tile.DungeonDoorTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.PlaceHolderTile;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/* Forwards right clicks to a different block.
 * Used for opening the big doors by right-clicking any of the spaces it takes up. 
 */
public class PlaceHolderBlock extends ToggleBlock {
    public PlaceHolderBlock() {
        super(Properties.copy(Blocks.BEDROCK));
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModRegistry.Tiles.PLACE_HOLDER.get().create();
    }

    @Override
    public ActionResultType use(BlockState p_225533_1_, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_) {
        if (!level.isClientSide()){
            TileEntity tile = level.getBlockEntity(pos);
            if (tile instanceof PlaceHolderTile){
                BlockPos target = ((PlaceHolderTile)tile).getForwardingPosition();
                if (target != null){
                    TileEntity realTile = level.getBlockEntity(target);
                    if (realTile instanceof DungeonDoorTile){
                        ((DungeonDoorTile) realTile).onClick(player, hand);
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }
}

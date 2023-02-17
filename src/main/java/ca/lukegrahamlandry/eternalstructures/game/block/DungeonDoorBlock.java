package ca.lukegrahamlandry.eternalstructures.game.block;

import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.game.tile.PlaceHolderTile;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.block.DoorBlock.OPEN;

public class DungeonDoorBlock extends ToggleBlock {
    public DungeonDoorBlock() {
        super(AbstractBlock.Properties.copy(Blocks.BEDROCK).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(HorizontalBlock.FACING, Direction.NORTH));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModRegistry.Tiles.DUNGEON_DOOR.get().create();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
        super.setPlacedBy(level, pos, state, p_180633_4_, p_180633_5_);
        Direction facing = getFacing(state);
        for (BlockPos other : getPlaces(facing, pos)){
            level.setBlockAndUpdate(other, ModRegistry.Blocks.PLACE_HOLDER.get().defaultBlockState());
            PlaceHolderTile.setAt(level, other, pos);
            ToggleBlock.setOpen(level, other, false);
        }
        ToggleBlock.setOpen(level, pos, false);
    }

    public static BlockPos[] getPlaces(Direction facing, BlockPos pos){
        Vector3i n = facing.getClockWise().getNormal();
        BlockPos[] result = new BlockPos[8];
        int i = 0;
        for (int y=0;y<3;y++){
            for (int x=-1;x<2;x++){
                if (y == 0 && x == 0) continue;
                result[i] = pos.offset(n.getX() * x,  y, n.getZ() * x);
                i++;
            }
        }
        return result;
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.defaultBlockState().setValue(OPEN, false).setValue(HorizontalBlock.FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }


    public static Direction getFacing(BlockState blockState) {
        return blockState.getValue(HorizontalBlock.FACING);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_);
        p_206840_1_.add(HorizontalBlock.FACING);
    }
}

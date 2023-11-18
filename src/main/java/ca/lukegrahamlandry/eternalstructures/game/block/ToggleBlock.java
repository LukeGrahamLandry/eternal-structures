package ca.lukegrahamlandry.eternalstructures.game.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import static net.minecraft.block.DoorBlock.OPEN;

/* Has a block state property that lets you toggle the collision shape on and off.
 * This doesn't get registered directly. Other blocks that need this behaviour just extend it.
 */
public abstract class ToggleBlock extends Block {
    public ToggleBlock(Properties p_i48440_1_) {
        super(p_i48440_1_.dynamicShape().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, Boolean.FALSE));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        return p_220071_1_.getValue(OPEN) ? VoxelShapes.empty() : super.getCollisionShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
    }

    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        switch(p_196266_4_) {
            case LAND:  // Fallthrough
            case AIR:
                return p_196266_1_.getValue(OPEN);
            case WATER:  // Fallthrough
            default:  // Java doesn't have exhaustive switch statements, so it thinks there might be more options.
                return false;
        }
    }

    public static void setOpen(World level, BlockPos pos, boolean open){
        BlockState state = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, state.setValue(OPEN, open));
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(OPEN);
    }
}

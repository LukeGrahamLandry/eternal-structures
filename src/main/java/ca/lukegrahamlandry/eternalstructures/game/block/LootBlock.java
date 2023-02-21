package ca.lukegrahamlandry.eternalstructures.game.block;

import ca.lukegrahamlandry.eternalstructures.game.tile.LootTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.LootrHelper;
import ca.lukegrahamlandry.eternalstructures.game.tile.LootrLootTile;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;

public class LootBlock extends ContainerBlock {
    public final LootTile.Type lootType;
    public LootBlock(LootTile.Type type) {
        super(AbstractBlock.Properties.copy(Blocks.CHEST));
        this.lootType = type;
        this.registerDefaultState(this.stateDefinition.any().setValue(HorizontalBlock.FACING, Direction.NORTH));
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        if (level.isClientSide()) return ActionResultType.SUCCESS;

        if (ModList.get().isLoaded("lootr")) LootrHelper.click(this, level, player, pos);
        else {
            INamedContainerProvider container = this.getMenuProvider(state, level, pos);
            if (container != null) player.openMenu(container);
        }

        return ActionResultType.CONSUME;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return this.lootType == LootTile.Type.CHEST ? ModVoxelShapes.rotatedChest(state.getValue(HorizontalBlock.FACING)) : ModVoxelShapes.POT;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader level) {
        return LootTile.create(this.lootType);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }


    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.defaultBlockState().setValue(HorizontalBlock.FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_);
        p_206840_1_.add(HorizontalBlock.FACING);
    }
}

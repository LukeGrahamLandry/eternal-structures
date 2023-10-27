package ca.lukegrahamlandry.eternalstructures.game.block;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.game.tile.LootTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.ProtectionTile;
import ca.lukegrahamlandry.eternalstructures.game.tile.SummoningTile;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.network.NetworkHandler;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenProtectionSettings;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenSummonSettings;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

import static net.minecraft.block.DoorBlock.OPEN;

// TODO: this is a copy-paste from protection
public class SummoningBlock extends Block {
    public SummoningBlock() {
        super(Properties.copy(Blocks.BEDROCK));
        this.registerDefaultState(this.stateDefinition.any().setValue(HorizontalBlock.FACING, Direction.NORTH));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModRegistry.Tiles.SUMMONING_ALTAR.get().create();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ActionResultType use(BlockState p_225533_1_, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_) {
        if (!level.isClientSide() && hand == Hand.MAIN_HAND) {
            TileEntity tile = level.getBlockEntity(pos);
            if (tile instanceof SummoningTile) {
                if (player.isCreative()){
                    if (player.getOffhandItem().isEmpty()) {
                        NetworkHandler.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                                new OpenSummonSettings(pos, ((SummoningTile)tile).getSettings())
                        );
                    } else {
                        ((SummoningTile)tile).setLootReward(player.getOffhandItem());
                        player.displayClientMessage(new StringTextComponent("Set lootItem to " + JsonHelper.get().toJson(player.getOffhandItem())), true);
                    }
                } else {
                    ((SummoningTile)tile).rightClick((ServerPlayerEntity) player);
                }
            } else {
                player.displayClientMessage(new StringTextComponent("Invalid tile entity. Report bug to Eternal Structures."), true);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return ModVoxelShapes.rotatedSummon(state.getValue(HorizontalBlock.FACING));
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.defaultBlockState().setValue(HorizontalBlock.FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }

    public static Direction getFacing(BlockState blockState) {
        return blockState.getValue(HorizontalBlock.FACING);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_);
        p_206840_1_.add(HorizontalBlock.FACING);
    }
}

package ca.lukegrahamlandry.eternalstructures.game.block;

import ca.lukegrahamlandry.eternalstructures.ESConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SpikesBlock extends Block {
    public static final BooleanProperty SPIKES_OUT = BooleanProperty.create("spikes_out");
    // TODO: i don't remember why i wanted the reaction offset a cycle from when it detects you
    //       In any case, this is broken if a spike is at the same pos in different dimensions but like,,,, how likely is that, right?
    private static final Map<BlockPos, Boolean> targetState = new HashMap<>();

    DamageSource damageType;
    int damage;
    public SpikesBlock(int damage, DamageSource damageType) {
        super(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK).dynamicShape().noOcclusion().randomTicks());
        this.damage = damage;
        this.damageType = damageType;
        this.registerDefaultState(this.stateDefinition.any().setValue(SPIKES_OUT, Boolean.FALSE).setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!level.isClientSide() && entity instanceof LivingEntity && state.getValue(SPIKES_OUT) && level.getGameTime() % 10 == 0){
            entity.hurt(this.damageType, damage);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld level, BlockPos pos, Random p_225534_4_) {
        super.tick(state, level, pos, p_225534_4_);
        level.getBlockTicks().scheduleTick(pos, this, ESConfig.spikeUpdateDelay.get());

        if (targetState.containsKey(pos)){
            boolean detectsPlayer = targetState.remove(pos);
            level.setBlockAndUpdate(pos, state.setValue(SPIKES_OUT, detectsPlayer));
        }

        AxisAlignedBB box = new AxisAlignedBB(pos).inflate(ESConfig.spikeDetectionRange.get());
        boolean detectsPlayer = !level.getPlayers((p) -> box.intersects(p.getBoundingBox())).isEmpty();
        if (state.getValue(SPIKES_OUT) != detectsPlayer) {
            targetState.put(pos, detectsPlayer);
        }
    }

    @Override
    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld level, BlockPos pos, BlockPos p_196271_6_) {
        if (!level.isClientSide()) level.getBlockTicks().scheduleTick(pos, this, ESConfig.spikeUpdateDelay.get());
        return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, level, pos, p_196271_6_);
    }

    @Override
    public void onPlace(BlockState p_220082_1_, World level, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        super.onPlace(p_220082_1_, level, pos, p_220082_4_, p_220082_5_);
        if (!level.isClientSide()) level.getBlockTicks().scheduleTick(pos, this, ESConfig.spikeUpdateDelay.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> container) {
        super.createBlockStateDefinition(container);
        container.add(BlockStateProperties.FACING, SPIKES_OUT);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
        // state.getValue(SPIKES_OUT)
        return ModVoxelShapes.getSpikeShape(false, state.getValue(BlockStateProperties.FACING));
    }
}

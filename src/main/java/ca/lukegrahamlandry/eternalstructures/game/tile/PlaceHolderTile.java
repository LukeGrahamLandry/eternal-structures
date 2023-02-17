package ca.lukegrahamlandry.eternalstructures.game.tile;

import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class PlaceHolderTile extends TileEntity {
    public PlaceHolderTile() {
        super(ModRegistry.Tiles.PLACE_HOLDER.get());
    }

    BlockPos target = null;

    public BlockPos getForwardingPosition(){
        return target;
    }

    public void setForwardingPosition(BlockPos target){
        this.target = target;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        if (target != null){
            tag.put("forwardingposition", NBTUtil.writeBlockPos(target));
        }
        return super.save(tag);
    }

    @Override
    public void load(BlockState p_230337_1_, CompoundNBT tag) {
        super.load(p_230337_1_, tag);
        if (tag.contains("forwardingposition")){
            target = NBTUtil.readBlockPos(tag.getCompound("forwardingposition"));
        }
    }

    public static void setAt(World level, BlockPos placeholder, BlockPos target){
        TileEntity tile = level.getBlockEntity(placeholder);
        if (tile instanceof PlaceHolderTile){
            ((PlaceHolderTile)tile).setForwardingPosition(target);
        }
    }
}

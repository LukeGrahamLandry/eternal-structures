package ca.lukegrahamlandry.eternalstructures.game.tile;

import ca.lukegrahamlandry.eternalstructures.ESConfig;
import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.game.block.DungeonDoorBlock;
import ca.lukegrahamlandry.eternalstructures.game.block.ToggleBlock;
import ca.lukegrahamlandry.eternalstructures.game.item.DungeonKeyItem;
import ca.lukegrahamlandry.eternalstructures.network.NetworkHandler;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.AnimationUpdatePacket;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Random;

public class DungeonDoorTile extends TileEntity implements IAnimatable, ITickableTileEntity {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public DungeonDoorTile() {
        super(ModRegistry.Tiles.DUNGEON_DOOR.get());
    }

    int doorId = new Random().nextInt();
    boolean open = false;
    int animationTick = -1;
    boolean locked = true;

    public void onClick(PlayerEntity player, Hand hand){
        if (hand != Hand.MAIN_HAND) return;

        ModMain.LOGGER.debug(player.getScoreboardName() + " clicked door at " + this.getBlockPos());
        ItemStack stack = player.getItemInHand(hand);

        if (!(stack.getItem() instanceof DungeonKeyItem)){
            if (this.locked && !player.isCreative()){
                player.displayClientMessage(new TranslationTextComponent("eternalstructures.message.need_door_key"), true);
            } else if (ESConfig.allowClosingUnlockedDungeonDoors.get()){
                this.open = !this.open;
                if (this.open) this.sendAnimationState(ANIM_START_OPEN);
                else this.sendAnimationState(ANIM_START_CLOSE);
            }
        } else if (player.isCreative()){
            player.displayClientMessage(new TranslationTextComponent("eternalstructures.message.bound_key"), true);
            DungeonKeyItem.setKeyId(stack, this.doorId);
            player.setItemInHand(hand, stack);
            this.locked = true;
            this.open = false;
            this.sendAnimationState(ANIM_SET_CLOSE);
        } else {
            int keyId = DungeonKeyItem.getKeyId(stack);
            if (keyId != this.doorId) {
                player.displayClientMessage(new TranslationTextComponent("eternalstructures.message.wrong_key_id"), true);
                return;
            }

            player.displayClientMessage(new TranslationTextComponent("eternalstructures.message.door_unlock"), true);
            this.open = true;
            this.locked = false;
            this.sendAnimationState(ANIM_START_OPEN);
            stack.shrink(1);
            player.setItemInHand(hand, stack);
        }

        Direction facing = DungeonDoorBlock.getFacing(this.getBlockState());
        for (BlockPos other : DungeonDoorBlock.getPlaces(facing, this.getBlockPos())){
            ToggleBlock.setOpen(level, other, this.open);
        }
        ToggleBlock.setOpen(level, this.getBlockPos(), this.open);

        ModMain.LOGGER.debug("The door at " + this.getBlockPos() + " has id=" + this.doorId + " locked=" + this.locked + " open=" + this.open);
    }

    private void sendAnimationState(int state) {
        NetworkHandler.INSTANCE.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(getBlockPos())),
                new AnimationUpdatePacket(getBlockPos(), state)
        );
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putInt("doorid", this.doorId);
        tag.putBoolean("dooropen", this.open);
        tag.putBoolean("doorlocked", this.locked);
        tag.putInt("dooranimtick", this.animationTick);
        return super.save(tag);
    }

    @Override
    public void load(BlockState p_230337_1_, CompoundNBT tag) {
        if (tag.contains("doorid")) {
            this.doorId = tag.getInt("doorid");
        }
        if (tag.contains("dooropen")) {
            this.open = tag.getBoolean("dooropen");
        }
        if (tag.contains("doorlocked")) {
            this.locked = tag.getBoolean("doorlocked");
        }
        if (tag.contains("dooranimtick")) {
            this.animationTick = tag.getInt("dooranimtick");
        }
        super.load(p_230337_1_, tag);
    }

    public static final int ANIM_START_OPEN = 1;
    public static final int ANIM_START_CLOSE = 2;
    public static final int ANIM_SET_OPEN = 3;
    public static final int ANIM_SET_CLOSE = 4;
    public void setClientAnimationState(int state){
        switch (state){
            case ANIM_START_OPEN: {
                this.animationTick = 20 * 8;
                this.open = true;
                break;
            }
            case ANIM_START_CLOSE: {
                this.animationTick = 0;
                this.open = false;
                break;
            }
            case ANIM_SET_CLOSE: {
                this.animationTick = 0;
                this.open = false;
                break;
            }
            case ANIM_SET_OPEN: {
                this.animationTick = 0;
                this.open = true;
                break;
            }
        }
    }

    private static final AnimationBuilder BUILD_ANIM_OPENING = new AnimationBuilder().addAnimation("opening", ILoopType.EDefaultLoopTypes.LOOP);
    private static final AnimationBuilder BUILD_ANIM_CLOSED = new AnimationBuilder().addAnimation("is_closed", ILoopType.EDefaultLoopTypes.LOOP);
    private static final AnimationBuilder BUILD_ANIM_OPEN = new AnimationBuilder().addAnimation("is_open", ILoopType.EDefaultLoopTypes.LOOP);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.animationTick > 0){
            event.getController().setAnimation(BUILD_ANIM_OPENING);
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(this.open ? BUILD_ANIM_OPEN : BUILD_ANIM_CLOSED);
            return PlayState.CONTINUE;
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void tick() {
        if (this.animationTick > 0){
            this.animationTick--;
        }
    }

    // for resyncing when a client joins the game or re-enters the chunk when someone else changed the state
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        if (tag.contains("time")) this.animationTick = tag.getInt("time");
        if (tag.contains("open")) this.open = tag.getBoolean("open");
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.putInt("time", animationTick);
        tag.putBoolean("open", open);
        return tag;
    }
}

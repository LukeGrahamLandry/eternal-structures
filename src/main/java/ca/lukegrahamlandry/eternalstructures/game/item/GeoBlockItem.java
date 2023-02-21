package ca.lukegrahamlandry.eternalstructures.game.item;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.client.IGeoInfo;
import ca.lukegrahamlandry.eternalstructures.client.render.ModItemRender;
import ca.lukegrahamlandry.eternalstructures.game.block.LootBlock;
import ca.lukegrahamlandry.eternalstructures.game.tile.LootTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class GeoBlockItem extends BlockItem implements IAnimatable, IGeoInfo {
    public AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public GeoBlockItem(Block block, Properties properties) {
        super(block, properties.setISTER(() -> ModItemRender::new));
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, World level, @Nullable PlayerEntity p_195943_3_, ItemStack stack, BlockState p_195943_5_) {
        if (!level.isClientSide()){
            if (this.getBlock() instanceof LootBlock){
                ResourceLocation rl = new ResourceLocation(ModMain.MOD_ID, "chests/" + this.getRegistryName().getPath());
                if (stack.getOrCreateTag().contains("lootTable")){
                    rl = new ResourceLocation(stack.getOrCreateTag().getString("lootTable"));
                }
                LockableLootTileEntity.setLootTable(level, level.getRandom(), pos, rl);
            }
        }
        return super.updateCustomBlockEntityTag(pos, level, p_195943_3_, stack, p_195943_5_);
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    ResourceLocation texture;
    public ResourceLocation getTextureResource(){
        if (texture == null){
            texture = new ResourceLocation(ModMain.MOD_ID, "textures/" + this.getBlock().getRegistryName().getPath() + ".png");
        }
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource() {
        return null;
    }

    @Override
    public ResourceLocation getModelResource() {
        if (this.getBlock() instanceof LootBlock) return ((LootBlock) this.getBlock()).lootType.model;
        return null;
    }
}
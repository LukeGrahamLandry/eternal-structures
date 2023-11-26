package ca.lukegrahamlandry.eternalstructures.game.tile;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.client.IGeoInfo;
import ca.lukegrahamlandry.eternalstructures.compat.LootrHelper;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.game.block.LootBlock;
import ca.lukegrahamlandry.eternalstructures.network.NetworkHandler;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.TileInfoPacket;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
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

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class LootTile extends ChestTileEntity implements IAnimatable, ITickableTileEntity, IGeoInfo, TileInfoPacket.Receiver {
    private Type lootType;
    private int tickInterval = 0;
    protected ResourceLocation savedLootTable;
    protected UUID uuid = UUID.randomUUID();
    protected Set<UUID> openers = new HashSet<>();

    public LootTile() {
        super(ModRegistry.Tiles.LOOT.get());
    }

    public static LootTile create(){
        if (ModList.get().isLoaded("lootr")){
            return LootrHelper.create();
        } else {
            return new LootTile();
        }
    }

    public static LootTile create(Type lootType){
        LootTile tile = create();
        tile.lootType = lootType;
        return tile;
    }

    public enum Type {
        GOLDEN_POT(false, false),
        CRYSTAL_POT(false, false),
        CHEST(true, true),
        POT(false, false);

        final boolean hasOpeningAnimation;
        public final ResourceLocation anim;
        public final ResourceLocation model;
        final boolean hasIdleOpenAnimation;
        Type(boolean hasOpeningAnimation, boolean hasIdleOpenAnimation){
            this.hasOpeningAnimation = hasOpeningAnimation;
            this.hasIdleOpenAnimation = hasIdleOpenAnimation;
            this.anim = new ResourceLocation(ModMain.MOD_ID, "animations/" + this.name().toLowerCase(Locale.ROOT) + ".animation.json");
            this.model = new ResourceLocation(ModMain.MOD_ID, "geo/" + this.name().toLowerCase(Locale.ROOT) + ".geo.json");
        }
    }

    ///// Container /////

    // Since extending normal chest, it handles the item storage capability.

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent(ModMain.MOD_ID + ".container.chest");  // TODO: depend on which block
    }

    ///// Animations /////
    ResourceLocation texture;
    public ResourceLocation getTextureResource(){
        if (texture == null){
            texture = new ResourceLocation(ModMain.MOD_ID, "textures/" + getBlockState().getBlock().getRegistryName().getPath() + ".png");
        }
        return texture;
    }

    public ResourceLocation getAnimationResource(){
        return this.lootType.anim;
    }

    public ResourceLocation getModelResource(){
        return this.lootType.model;
    }

    private int animationTick = 0;
    private boolean isOpen = false;
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final AnimationBuilder ANIM_IDLE_OPEN = new AnimationBuilder().addAnimation("open_idle", ILoopType.EDefaultLoopTypes.LOOP);
    private static final AnimationBuilder ANIM_OPENING = new AnimationBuilder().addAnimation("open", ILoopType.EDefaultLoopTypes.LOOP);
    private static final AnimationBuilder ANIM_IDLE = new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.animationTick > 0 && this.lootType.hasOpeningAnimation){
            event.getController().setAnimation(ANIM_OPENING);
        } else if (this.lootType.hasIdleOpenAnimation){
            event.getController().setAnimation(this.isOpen ? ANIM_IDLE_OPEN : ANIM_IDLE);
        } else {
            event.getController().setAnimation(ANIM_IDLE);
        }
        return PlayState.CONTINUE;
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

        if (!this.level.isClientSide()){
            this.tickInterval++;
            this.openCount = getOpenCount(this.level, this, this.tickInterval, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.openCount);

            if (this.isOpen && this.openCount == 0){
                this.isOpen = false;
                NetworkHandler.INSTANCE.send(
                        PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(getBlockPos())),
                        new TileInfoPacket(getBlockPos(), AnimUpdate.ON_CLOSE.name())
                );
            } else if (!this.isOpen && this.openCount > 0) {
                this.isOpen = true;
                NetworkHandler.INSTANCE.send(
                        PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(getBlockPos())),
                        new TileInfoPacket(getBlockPos(), AnimUpdate.ON_OPEN.name())
                );
            }
        }
    }

    ///// Data Sync / Save /////

    enum AnimUpdate {
        ON_OPEN,
        ON_CLOSE
    }

    @Override
    public void onInfoPacket(String s) {
        try {
            AnimUpdate anim = AnimUpdate.valueOf(s);
            switch (anim){
                case ON_OPEN:
                    this.isOpen = true;
                    this.animationTick = (int) (1.72 * 20);
                    break;
                case ON_CLOSE:
                    this.isOpen = false;
                    this.animationTick = 0;
                    break;
            }
        } catch (IllegalArgumentException ignored){
            ModMain.LOGGER.error("Server sent unrecognised AnimUpdate name {}", s);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        // TODO: using Create schematic causes NullPointerException here.
        //       mouseClicked event handler -> confirm(SchematicPromptScreen.java:120) -> saveSchematic(SchematicAndQuillHandler.java:214) -> func_186254_a(Template.java:86)
        //       Maybe they're doing something weird but also I shouldn't be dependent on there always being a loot table set.
        //       Need to make it drop as a normal container anyway so might come up then.
        // TODO: test that this fixed it.
        if (this.savedLootTable != null) tag.putString("es_LootrSavedLootTable", this.savedLootTable.toString());


        tag.putUUID("es_id", this.uuid);
        ListNBT list = new ListNBT();
        list.addAll(this.openers.stream().map(NBTUtil::createUUID).collect(Collectors.toSet()));
        tag.put("es_openers", list);
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.lootType = ((LootBlock) state.getBlock()).lootType;
        if (tag.contains("es_LootrSavedLootTable")) this.savedLootTable = new ResourceLocation(tag.getString("es_LootrSavedLootTable"));
        if (tag.contains("es_id")) this.uuid = tag.getUUID("es_id");
        if (tag.contains("es_openers")) {
            this.openers.clear();
            this.openers.addAll(tag.getList("es_openers", 11).stream().map(NBTUtil::loadUUID).collect(Collectors.toSet()));
        }
    }

    @Override
    public void setLootTable(ResourceLocation p_189404_1_, long p_189404_2_) {
        super.setLootTable(p_189404_1_, p_189404_2_);
        this.savedLootTable = p_189404_1_;
    }

    // for resyncing when a client joins the game or re-enters the chunk when someone else changed the state
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        if (tag.contains("es_time")) this.animationTick = tag.getInt("time");
        if (tag.contains("es_open")) this.isOpen = tag.getBoolean("open");
        this.lootType = ((LootBlock) state.getBlock()).lootType;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.putInt("es_time", animationTick);
        tag.putBoolean("es_open", isOpen);
        return tag;
    }
}

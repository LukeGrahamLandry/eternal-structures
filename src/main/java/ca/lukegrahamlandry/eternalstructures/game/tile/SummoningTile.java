package ca.lukegrahamlandry.eternalstructures.game.tile;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.client.IGeoInfo;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.network.NetworkHandler;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.AnimationUpdatePacket;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SummoningTile extends TileEntity implements IAnimatable, ITickableTileEntity, IGeoInfo {
    Settings data = new Settings();
    long timeout = 0;
    UUID theBoss = null;
    UUID thePlayer = null;
    int animationTick = 0;
    int animState = 0;

    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public SummoningTile() {
        super(ModRegistry.Tiles.SUMMONING_ALTAR.get());
    }

    @Override
    public void tick() {
        if (this.animationTick > 0){
            this.animationTick--;
            if (this.animationTick == 0) {
                if (this.animState == ACTIVATE) {
                    this.animState = FIGHTING;
                    if (!this.level.isClientSide()) {
                        this.actuallySummon();
                    }
                }
                if (this.animState == DEFEATED) this.animState = IDLE;
            }
        }

        if (!this.level.isClientSide()) {
            if (this.theBoss != null) {
                Entity boss = ((ServerWorld) this.level).getEntity(this.theBoss);
                if (boss == null || !boss.isAlive()) {
                    this.onBossDefeat();
                    return;
                }
            }
            if (this.thePlayer != null) {
                Entity player = ((ServerWorld) this.level).getEntity(this.thePlayer);
                if (player == null || !player.isAlive()) {
                    this.onPlayerDie();
                    return;
                }
            }

            if (this.timeout > 0) {
                if (this.level.getGameTime() > this.timeout) {
                    this.timeout = 0;
                    this.setChanged();
                }
            }
        }
    }

    void onBossDefeat(){
        ModMain.LOGGER.debug("Boss defeated!");
        this.theBoss = null;
        this.thePlayer = null;
        this.setChanged();
        this.serverStartAnimation(DEFEATED);
    }

    void onPlayerDie(){
        ModMain.LOGGER.debug("Played died!");
        if (this.theBoss != null) {
            Entity boss = ((ServerWorld) this.level).getEntity(this.theBoss);
            if (boss != null)  boss.remove();
        }
        this.theBoss = null;
        this.thePlayer = null;
        this.setChanged();
        this.serverStartAnimation(IDLE);

    }

    public void rightClick(ServerPlayerEntity player) {
        if (this.timeout > 0) {
            long delay = (this.timeout - this.level.getGameTime()) / 20;
            player.displayClientMessage(new StringTextComponent("The boss may be summoned again in " + delay + "seconds"), true);
            return;
        }

        // What is required to summon depends on the settings.
        boolean doSummon = this.data.summonItem == null;
        if (!doSummon) {
            Item summonItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.data.summonItem));
            doSummon = summonItem != null && player.getItemInHand(Hand.MAIN_HAND).getItem() == summonItem;
            if (doSummon && this.data.consumeItem) {
                player.getItemInHand(Hand.MAIN_HAND).shrink(1);
            }
        }

        if (doSummon) {
            this.startSummonBoss(player);
        }
    }

    // TODO: this will start an animated timer
    void startSummonBoss(ServerPlayerEntity player) {
        if (this.data.summonMessage != null) {
            // TODO: show to all nearby players?
            player.displayClientMessage(new TranslationTextComponent(this.data.summonMessage), false);
        }

        this.thePlayer = player.getUUID();
        ModMain.LOGGER.debug("(summon): Watching player " + this.thePlayer);
        this.serverStartAnimation(ACTIVATE);
    }

    void actuallySummon() {
        EntityType<?> summonEntity = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.data.summonEntityType));
        if (summonEntity != null) {
            Entity boss = summonEntity.spawn((ServerWorld) this.level, null, null, this.worldPosition, SpawnReason.MOB_SUMMONED, true, true);
            if (boss != null) {
                this.theBoss = boss.getUUID();
                ModMain.LOGGER.debug("(summon): Watching boss " + this.theBoss);
                this.startTimeout();
                this.setChanged();
            }
        }
    }

    void startTimeout(){
        this.timeout = this.level.getGameTime() + (long) (this.data.timeoutMinutes * 60 * 20);
        this.setChanged();
        ModMain.LOGGER.debug("Altar at " + this.worldPosition + " is disabled until gameTime=" + this.timeout);
    }

    private static final String NBT_KEY = ModMain.MOD_ID + ":summoning_settings";
    private static final String NBT_KEY_TIMER = ModMain.MOD_ID + ":summoning_timeout";
    private static final String NBT_KEY_BOSS = ModMain.MOD_ID + ":summoning_boss";

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putString(NBT_KEY, JsonHelper.get().toJson(this.data));
        if (this.timeout != 0) {
            tag.putLong(NBT_KEY_TIMER, this.timeout);
        }
        if (this.theBoss != null) {
            tag.putUUID(NBT_KEY_BOSS, this.theBoss);
        }
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains(NBT_KEY)) {
            String json = tag.getString(NBT_KEY);
            try {
                this.data = JsonHelper.get().fromJson(json, Settings.class);
            } catch (JsonSyntaxException e) {
                ModMain.LOGGER.error("Failed to load summoning altar settings at " + this.worldPosition);
                ModMain.LOGGER.error(json);
                e.printStackTrace();
            }
        } else {
            ModMain.LOGGER.error("Missing structure summoning altar settings settings at {}. Using defaults.", this.worldPosition);
        }
        if (tag.contains(NBT_KEY_TIMER)) {
            this.timeout = tag.getLong(NBT_KEY_TIMER);
        }
        if (tag.contains(NBT_KEY_BOSS)) {
            this.theBoss = tag.getUUID(NBT_KEY_BOSS);
        }
    }

    // Called on the server by the packet sent from GUI.
    public void setSettings(Settings data) {
        this.data = data;
        this.setChanged();
    }

    public Settings getSettings() {
        return this.data;
    }


    private static final List<AnimationBuilder> ANIM = Arrays.asList(
            new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP),
            new AnimationBuilder().addAnimation("active", ILoopType.EDefaultLoopTypes.PLAY_ONCE),
            new AnimationBuilder().addAnimation("active_after", ILoopType.EDefaultLoopTypes.LOOP),
            new AnimationBuilder().addAnimation("defeat", ILoopType.EDefaultLoopTypes.PLAY_ONCE)
    );

    int IDLE = 0;
    int ACTIVATE = 1;
    int FIGHTING = 2;
    int DEFEATED = 3;

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(ANIM.get(this.animState));
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


    public void serverStartAnimation(int anim) {
        NetworkHandler.INSTANCE.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(getBlockPos())),
                new AnimationUpdatePacket(getBlockPos(), anim)
        );
        this.animState = anim;
        this.setAnimLength(this.animState);
    }

    public void setClientAnimationState(int anim) {
        this.animState = Math.max(Math.min(anim, ANIM.size() - 1), 0);
        this.setAnimLength(this.animState);
    }

    // TODO: it really sucks that you have to update this if you change the animation json. Need to figure out how to read the lengths at runtime.
    void setAnimLength(int anim) {
        if (anim == ACTIVATE) this.animationTick = (int) (4.5 * 20);
        else if (anim == DEFEATED) this.animationTick = (int) (1.0 * 20);
        else this.animationTick = 0;
    }

    static ResourceLocation texture = new ResourceLocation(ModMain.MOD_ID, "textures/summon/boss_alter.png");
    static ResourceLocation model = new ResourceLocation(ModMain.MOD_ID, "geo/boss_alter.geo.json");
    static ResourceLocation animation = new ResourceLocation(ModMain.MOD_ID, "animations/boss_alter.animation.json");

    @Override
    public ResourceLocation getTextureResource() {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource() {
        return animation;
    }

    @Override
    public ResourceLocation getModelResource() {
        return model;
    }


    public static class Settings {
        float timeoutMinutes = 20;
        String summonItem;  // = "minecraft:diamond";
        String summonMessage = "Boss summoned!";
        String summonEntityType = "minecraft:zombie";
        String lootItem; //  = "minecraft:golden_apple";
        boolean consumeItem = true;

        public String validate(){
            if (summonItem != null && !ForgeRegistries.ITEMS.containsKey(new ResourceLocation(this.summonItem))) {
                return "(summonItem) No item registered as " + new ResourceLocation(this.summonItem);
            }
            if (this.lootItem != null && !ForgeRegistries.ITEMS.containsKey(new ResourceLocation(this.lootItem))) {
                return "(lootItem) No item registered as " + new ResourceLocation(this.lootItem);
            }
            if (!ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(this.summonEntityType))) {
                return "(summonEntityType) No entity registered as " + new ResourceLocation(this.summonEntityType);
            }

            return "";
        }
    }
}

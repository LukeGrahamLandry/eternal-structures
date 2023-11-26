package ca.lukegrahamlandry.eternalstructures.game.tile;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.client.IGeoInfo;
import ca.lukegrahamlandry.eternalstructures.compat.GatewayHelper;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.network.NetworkHandler;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.AnimationUpdatePacket;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
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

import javax.annotation.Nullable;
import java.util.*;

public class SummoningTile extends TileEntity implements IAnimatable, ITickableTileEntity, IGeoInfo {
    Settings data = new Settings();
    long timeout = 0;
    @Nullable UUID theBoss = null;
    @Nullable UUID thePlayer = null;
    int animationTick = 0;
    int animState = 0;
    boolean freeSummon = false;

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

    public void setLootReward(ItemStack stack) {
        this.data.lootItemStack = stack.copy();
        this.setChanged();
    }

    void onBossDefeat(){
        ModMain.LOGGER.debug("Boss defeated!");
        if (this.thePlayer != null) {
            Entity player = ((ServerWorld) this.level).getEntity(this.thePlayer);
            if (player instanceof PlayerEntity) {
                // TODO: show to all nearby players?
                if (this.data.bossDeathMessage != null) ((PlayerEntity) player).displayClientMessage(new TranslationTextComponent(this.data.bossDeathMessage), false);
                ((PlayerEntity) player).giveExperiencePoints(this.data.xpPointsReward);
            }
        }
        this.theBoss = null;
        this.thePlayer = null;
        this.freeSummon = false;
        this.setChanged();
        this.serverStartAnimation(DEFEATED);
        this.startTimeout();
        if (this.data.lootItemStack != null) {
            Entity loot = new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 0.5, this.data.lootItemStack.copy());
            loot.setGlowing(true);
            this.level.addFreshEntity(loot);
        }
    }

    void onPlayerDie(){
        ModMain.LOGGER.debug("Played died!");
        Entity boss = this.getBoss();
        if (boss != null) boss.remove();
        this.theBoss = null;
        this.thePlayer = null;
        this.freeSummon = true;
        this.setChanged();
        this.serverStartAnimation(IDLE);

    }

    @Nullable Entity getBoss() {
        if (this.theBoss == null) return null;
        Entity boss = ((ServerWorld) this.level).getEntity(this.theBoss);
        if (boss != null && !boss.isAlive()) return null;
        return boss;
    }

    public void rightClick(ServerPlayerEntity player) {
        if (this.animState != IDLE) {
            Entity boss = this.getBoss();
            player.displayClientMessage(new StringTextComponent("The has already been summoned (" + (boss != null ? boss.blockPosition().toString() : "???")+ ")"), true);
            return;
        }
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

    @Nullable PlayerEntity getSummoner() {
        if (this.thePlayer == null) return null;
        Entity player = ((ServerWorld) this.level).getEntity(this.thePlayer);
        if (!(player instanceof PlayerEntity)) return null;
        return (PlayerEntity) player;
    }

    void actuallySummon() {
        Entity boss = null;
        if (this.data.summonEntityType != null) {
            EntityType<?> summonEntity = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.data.summonEntityType));
            if (summonEntity != null) {
                boss = summonEntity.spawn((ServerWorld) this.level, null, null, this.worldPosition, SpawnReason.MOB_SUMMONED, true, true);
            }
        }
        if (this.data.gateway != null) {
            if (this.getSummoner() == null)  {
                ModMain.LOGGER.error("Couldn't find summoner. Can't create gateway.");
                return;
            }
            if (!ModList.get().isLoaded("gateways")) {
                ModMain.LOGGER.error("Gateways to Eternity mod is not installed!");
                return;
            }

            boss = GatewayHelper.start(this.data.gateway, this.getSummoner(), this.worldPosition);
        }



        if (boss == null) {
            ModMain.LOGGER.error("Altar at " + this.getBlockPos() + " failed to summon boss!");
            return;
        }

        if (boss instanceof LivingEntity) {
            for (EffectInstance baseEffect : this.data.potionEffects){
                // Copy is required! Don't tick down the duration on the main instance.
                EffectInstance effect = new EffectInstance(baseEffect.getEffect(), baseEffect.getDuration(), baseEffect.getAmplifier());
                ((LivingEntity) boss).addEffect(effect);
            }
        }
        if (this.data.entityName != null) {
            boss.setCustomName(new TranslationTextComponent(this.data.entityName));
        }
        this.theBoss = boss.getUUID();
        ModMain.LOGGER.debug("(summon): Watching boss " + this.theBoss);
        this.setChanged();
        this.doLighting();
    }

    void doLighting() {
        LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(this.level);
        bolt.moveTo(Vector3d.atBottomCenterOf(this.worldPosition));
        bolt.setVisualOnly(false);
        this.level.addFreshEntity(bolt);
    }

    void startTimeout(){
        this.timeout = this.level.getGameTime() + (long) (this.data.timeoutMinutes * 60 * 20);
        this.setChanged();
        ModMain.LOGGER.debug("Altar at " + this.worldPosition + " is disabled until gameTime=" + this.timeout);
    }

    private static final String NBT_KEY = ModMain.MOD_ID + ":summoning_settings";
    private static final String NBT_KEY_TIMER = ModMain.MOD_ID + ":summoning_timeout";
    private static final String NBT_KEY_BOSS = ModMain.MOD_ID + ":summoning_boss";
    private static final String NBT_KEY_FREE = ModMain.MOD_ID + ":summoning_free";

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putString(NBT_KEY, JsonHelper.get().toJson(this.data));
        if (this.timeout != 0) {
            tag.putLong(NBT_KEY_TIMER, this.timeout);
        }
        if (this.theBoss != null) {
            tag.putUUID(NBT_KEY_BOSS, this.theBoss);
        }
        tag.putBoolean(NBT_KEY_FREE, this.freeSummon);
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
        if (tag.contains(NBT_KEY_TIMER)) this.timeout = tag.getLong(NBT_KEY_TIMER);
        if (tag.contains(NBT_KEY_BOSS)) this.theBoss = tag.getUUID(NBT_KEY_BOSS);
        if (tag.contains(NBT_KEY_FREE)) this.freeSummon = tag.getBoolean(NBT_KEY_FREE);
    }

    // Called on the server by the packet sent from GUI.
    public void setSettings(Settings data) {
        ModMain.LOGGER.debug("Set summoning altar at {}{} = {}", level.dimension().location(), this.worldPosition, JsonHelper.get().toJson(data));
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

    static ResourceLocation texture_idle = new ResourceLocation(ModMain.MOD_ID, "textures/summon/boss_alter.png");

    static ResourceLocation texture_animated = new ResourceLocation(ModMain.MOD_ID, "textures/summon/boss_alter_glowing_5.png");
    // TODO: Making a file called `boss_alter_glowing_animation.png.mcmeta` with `{ "animations": {}}` didn't work, just broke uvs. i must be making some dumb mistake.
    //       I could do it in code i guess but that feels silly.
    // static ResourceLocation texture_animated = new ResourceLocation(ModMain.MOD_ID, "textures/summon/boss_alter_glowing_animation.png");
    static ResourceLocation texture_defeat = new ResourceLocation(ModMain.MOD_ID, "textures/summon/boss_alter_defeat.png");
    static ResourceLocation texture_starting = new ResourceLocation(ModMain.MOD_ID, "textures/summon/boss_alter_operation.png");
    static List<ResourceLocation> TEXTURES = Arrays.asList(texture_idle, texture_starting, texture_animated, texture_defeat);

    static ResourceLocation model = new ResourceLocation(ModMain.MOD_ID, "geo/boss_alter.geo.json");
    static ResourceLocation animation = new ResourceLocation(ModMain.MOD_ID, "animations/boss_alter.animation.json");

    @Override
    public ResourceLocation getTextureResource() {
        return TEXTURES.get(this.animState);
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
        float timeoutMinutes = 20; // 0 = no delay
        @Nullable String summonItem;  // = "minecraft:diamond"; // Optional (null = free summon, just right click)
        @Nullable String summonMessage = "Boss Summoned!";  // Optional (null = no chat message)
        @Nullable String bossDeathMessage = "Boss Defeated!";  // Optional (null = no chat message)
        String summonEntityType = null;
        String gateway = null;
        @Nullable ItemStack lootItemStack; // Optional. Format: { "item": "minecraft:golden_apple", "count": 1, "tag": "..." }
        boolean consumeItem = true;
        private List<EffectInstance> potionEffects = new ArrayList<>();
        @Nullable String entityName;  // Optional
        int xpPointsReward = 0;  // Note: not measured in levels. "points" have diminishing returns as you level up

        public String validate(){
            try {
                if (summonItem != null && !ForgeRegistries.ITEMS.containsKey(new ResourceLocation(this.summonItem))) {
                    return "(summonItem) No item registered as " + new ResourceLocation(this.summonItem);
                }
                if (this.summonEntityType != null &&!ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(this.summonEntityType))) {
                    return "(summonEntityType) No entity registered as " + new ResourceLocation(this.summonEntityType);
                }

                if (this.gateway != null) {
                    if (!ModList.get().isLoaded("gateways")) {
                        return "Gateways to Eternity mod is not installed!";
                    }
                    if (!GatewayHelper.isValid(this.gateway)) {
                        return "(gateway) No gateway registered as " + this.gateway;
                    }
                }
                if (this.summonEntityType == null && this.gateway == null) {
                    return "Either summonEntityType or gateway must be set";
                }
                if (this.summonEntityType != null && this.gateway != null) {
                    return "Setting both summonEntityType and gateway is not supported.";
                }
            } catch (ResourceLocationException e) {
                return e.getMessage();
            }
            return "";
        }
    }
}

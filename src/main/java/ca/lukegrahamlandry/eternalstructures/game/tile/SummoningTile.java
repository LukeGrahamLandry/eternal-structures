package ca.lukegrahamlandry.eternalstructures.game.tile;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class SummoningTile extends TileEntity implements ITickableTileEntity {
    Settings data = new Settings();
    long timeout = 0;
    public SummoningTile() {
        super(ModRegistry.Tiles.SUMMONING_ALTAR.get());
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide()) {
            if (this.timeout > 0) {
                if (this.level.getGameTime() > this.timeout) {
                    this.timeout = 0;
                    this.setChanged();
                } else {
                    return;
                }
            }
        }
    }

    public void rightClick(ServerPlayerEntity player) {
        // What is required to summon depends on the settings.
        boolean doSummon = this.data.summonItem == null;
        if (!doSummon) {
            Item summonItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.data.summonItem));
            doSummon = summonItem != null && player.getItemInHand(Hand.MAIN_HAND).getItem() == summonItem;
        }

        if (doSummon) {
            this.startSummonBoss(player);
        }
    }

    // TODO: this will start an animated timer
    void startSummonBoss(ServerPlayerEntity player) {
        // TODO: show to all nearby players?
        player.displayClientMessage(new TranslationTextComponent(this.data.summonMessage), false);
        EntityType<?> summonEntity = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.data.summonEntityType));
        if (summonEntity != null) {
            Entity boss = summonEntity.spawn((ServerWorld) this.level, null, null, this.worldPosition, SpawnReason.MOB_SUMMONED, true, true);
            if (boss != null) {
                this.startTimeout();
            }
        }
    }

    void startTimeout(){
        this.timeout = this.level.getGameTime() + (this.data.timeoutMinutes * 60 * 20);
        this.setChanged();
        ModMain.LOGGER.debug("Altar at " + this.worldPosition + " is disabled until gameTime=" + this.timeout);
    }

    private static final String NBT_KEY = ModMain.MOD_ID + ":summoning_settings";
    private static final String NBT_KEY_TIMER = ModMain.MOD_ID + ":summoning_timeout";

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putString(NBT_KEY, JsonHelper.get().toJson(this.data));
        tag.putLong(NBT_KEY_TIMER, this.timeout);
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
        } else {
            this.timeout = 0;
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

    public static class Settings {
        long timeoutMinutes = 20;
        String summonItem;  // = "minecraft:diamond";
        String summonMessage = "Boss summoned!";
        String summonEntityType = "minecraft:zombie";
        String lootItem; //  = "minecraft:golden_apple";

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

package ca.lukegrahamlandry.eternalstructures.game.tile;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.protect.ProtectionInstance;
import ca.lukegrahamlandry.eternalstructures.protect.ProtectionManager;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;

public class ProtectionTile extends TileEntity implements ITickableTileEntity {
    ProtectionInstance.Settings data = new ProtectionInstance.Settings();
    boolean needs_update = false;
    public ProtectionTile() {
        super(ModRegistry.Tiles.PROTECTION.get());
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide() && this.needs_update) {
            this.needs_update = false;
            ProtectionManager.put((ServerWorld) this.level, this.worldPosition, this.data);
        }
    }

    private static final String NBT_KEY = ModMain.MOD_ID + ":protection_settings";

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putString(NBT_KEY, JsonHelper.get().toJson(this.data));
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains(NBT_KEY)) {
            String json = tag.getString(NBT_KEY);
            try {
                this.data = JsonHelper.get().fromJson(json, ProtectionInstance.Settings.class);
                this.needs_update = true;
            } catch (JsonSyntaxException e) {
                ModMain.LOGGER.error("Failed to load structure protection settings at " + this.worldPosition);
                ModMain.LOGGER.error(json);
                e.printStackTrace();
            }
        } else {
            ModMain.LOGGER.error("Missing structure protection settings at {}. Using defaults.", this.worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        if (!level.isClientSide()) {
            ProtectionManager.remove((ServerWorld) level, this.worldPosition);
        }
        super.setRemoved();
    }

    // Called on the server by the SaveProtectionSettings packet sent from GUI.
    public void setSettings(ProtectionInstance.Settings data) {
        this.data = data;
        this.setChanged();
        ProtectionManager.put((ServerWorld) this.level, this.worldPosition, data);
    }

    public ProtectionInstance.Settings getSettings() {
        return this.data;
    }
}

package ca.lukegrahamlandry.eternalstructures.protect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ProtectionInstance {
    public static class Settings {
        // These can't be final no matter how badly intellij wants it! Old gson version says no.
        private int radius = 5;
        private List<EffectInstance> potionEffects = new ArrayList<>();
        private boolean preventBreakAndPlace = true;
        private List<String> preventRightClick = new ArrayList<>();
        private boolean disableFlight = true;

        public String validate() {
            StringBuilder msg = new StringBuilder();
            for (String item : this.preventRightClick) {
                try {
                    ResourceLocation key = new ResourceLocation(item);
                    boolean exists = ForgeRegistries.ITEMS.containsKey(key);
                    if (!exists) {
                        msg.append("No item registered as ").append(key).append("\n");
                    }
                } catch (ResourceLocationException e) {
                    msg.append("Invalid resource location: ").append(item).append("\n");
                }
            }

            return msg.toString();
        }
    }

    private final ServerWorld level;
    private final BlockPos pos;
    private Settings settings;
    private AxisAlignedBB box;

    // Only used for triggering onPlayerEnter and onPlayerExit. Useful if I want to show a message, etc
    private final Set<UUID> trackedPlayers = new HashSet<>();  // TODO: save with world data so onPlayerEnter only happens once?

    ProtectionInstance(ServerWorld level, BlockPos pos){
        this.level = level;
        this.pos = pos;
        this.changeSettings(new Settings());
    }

    void tick(){
        for (ServerPlayerEntity player : this.level.players()){
            if (contains(player) && !ignore(player)) {
                if (trackedPlayers.add(player.getUUID())) this.onPlayerEnter(player);
                this.onPlayerTick(player);
            } else {
                if (trackedPlayers.remove(player.getUUID())) this.onPlayerExit(player);
            }
        }
    }

    private void onPlayerTick(ServerPlayerEntity player){
        // There's one instance of the class that gets parsed out of the json so if you give that to someone,
        // the duration will tick down and never be able to be applied again. Instead, make sure to use a new copy every time.
        for (EffectInstance baseEffect : this.settings.potionEffects){
            EffectInstance effect = new EffectInstance(baseEffect.getEffect(), baseEffect.getDuration(), baseEffect.getAmplifier());
            player.addEffect(effect);
        }

        if (this.settings.disableFlight){
            if (player.abilities.flying){
                player.abilities.flying = false;
                player.onUpdateAbilities();
                player.displayClientMessage(new StringTextComponent("Cannot fly in this area"), true);
            }
            if (player.isFallFlying()) {
                player.stopFallFlying();
                player.displayClientMessage(new StringTextComponent("Cannot fly in this area"), true);
            }
        }
    }

    private void onPlayerEnter(PlayerEntity player){

    }

    private void onPlayerExit(PlayerEntity player){
        for (EffectInstance effect : this.settings.potionEffects){
            player.removeEffect(effect.getEffect());
        }

        if (ignore(player)){
            // They just picked up the unlock item
        }
    }

    boolean preventBlockInteract(PlayerEntity player, BlockPos pos) {
        if (!this.contains(pos) || ignore(player)) return false;
        return this.settings.preventBreakAndPlace;
    }

    // Called for item, block or entity. The pos will be the target if applicable or the player if item clicked on air.
    boolean preventItemInteract(PlayerEntity player, BlockPos pos, ItemStack stack) {
        if (!this.contains(pos) || ignore(player)) return false;

        String item = String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem()));
        for (String s : this.settings.preventRightClick) {
            if (s.equals(item)) return true;
        }

        return false;
    }

    private boolean ignore(PlayerEntity check){
        if (check.isCreative()) return true;
        return false;  // Use for the fancy item
    }

    void changeSettings(Settings data){
        this.settings = data;
        this.box = new AxisAlignedBB(this.pos).inflate(this.settings.radius);
    }

    private boolean contains(BlockPos check){
        return this.box.contains(Vector3d.atCenterOf(check));
    }

    private boolean contains(PlayerEntity check){
        return this.box.contains(check.position());
    }

    public Settings getSettings(){
        return settings;
    }
}

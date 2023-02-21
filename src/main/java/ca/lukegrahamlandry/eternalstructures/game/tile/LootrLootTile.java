package ca.lukegrahamlandry.eternalstructures.game.tile;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import noobanidus.mods.lootr.api.tile.ILootTile;

import java.util.Set;
import java.util.UUID;

public class LootrLootTile extends LootTile implements ILootTile {
    @Override
    public void fillWithLoot(PlayerEntity player, IInventory inv, ResourceLocation forcedTable, long seed) {
        if (!this.hasLevel() || this.level.getServer() == null || player == null) return;

        ResourceLocation actual = forcedTable != null ? forcedTable : this.savedLootTable;
        LootTable table = this.level.getServer().getLootTables().get(actual);
        if (table == LootTable.EMPTY) return;

        CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayerEntity)player, actual);

        LootContext ctx = new LootContext.Builder((ServerWorld)this.level)
                .withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(this.worldPosition))
                .withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player)
                .create(LootParameterSets.CHEST);

        table.fill(inv, ctx);
    }

    @Override
    public ResourceLocation getTable() {
        return this.savedLootTable;
    }

    @Override
    public Set<UUID> getOpeners() {
        return this.openers;
    }

    @Override
    public UUID getTileId() {
        return this.uuid;
    }

    @Override
    public void updatePacketViaState() {

    }
}

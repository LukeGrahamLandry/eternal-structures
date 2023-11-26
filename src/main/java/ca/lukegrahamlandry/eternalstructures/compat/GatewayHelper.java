package ca.lukegrahamlandry.eternalstructures.compat;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shadows.gateways.entity.GatewayEntity;
import shadows.gateways.event.GateEvent;
import shadows.gateways.gate.Gateway;
import shadows.gateways.gate.GatewayManager;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ModMain.MOD_ID)
public class GatewayHelper {
    // TODO: save this in world data
    // TODO: not dimension specific but like... overlap is really unlikely so whatever
    private static final HashMap<UUID, BlockPos> activeGateways = new HashMap<>();

    public static boolean isValid(String gateway) {
        Gateway gate = GatewayManager.INSTANCE.getValue(new ResourceLocation(gateway));
        return gate != null;
    }

    public static Entity start(String gateway, PlayerEntity summoner, BlockPos summonAltarPos) {
        Gateway gate = GatewayManager.INSTANCE.getValue(new ResourceLocation(gateway));
        if (gate == null) return null;
        GatewayEntity e = new GatewayEntity(summoner.level, summoner, gate);
        e.moveTo(Vector3d.atCenterOf(summonAltarPos.above()));
        activeGateways.put(e.getUUID(), summonAltarPos);
        summoner.level.addFreshEntity(e);
        return e;
    }

    @SubscribeEvent
    public static void onWin(GateEvent.Completed event) {
        BlockPos pos = activeGateways.get(event.getEntity().getUUID());
        ModMain.LOGGER.debug("Gateway win at " + pos + ": " + event.getEntity());
        activeGateways.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLose(GateEvent.Failed event) {
        BlockPos pos = activeGateways.get(event.getEntity().getUUID());
        ModMain.LOGGER.debug("Gateway lose at " + pos + ": " + event.getEntity());
        activeGateways.remove(event.getEntity().getUUID());
    }
}

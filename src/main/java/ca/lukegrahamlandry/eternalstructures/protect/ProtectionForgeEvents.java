package ca.lukegrahamlandry.eternalstructures.protect;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ModMain.MOD_ID)
public class ProtectionForgeEvents {
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event){
        for (ProtectionInstance p : ProtectionManager.getAll()){
            if (p.preventBlockInteract(event.getPlayer(), event.getPos())) {
                event.getPlayer().displayClientMessage(new StringTextComponent("Cannot break block in structure."), true);
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event){
        if (!(event.getEntity() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntity();

        for (ProtectionInstance p : ProtectionManager.getAll()){
            if (p.preventBlockInteract(player, event.getPos())) {
                player.displayClientMessage(new StringTextComponent("Cannot place block in structure."), true);
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent event){
        if (event.getSide() == LogicalSide.CLIENT) return;
        for (ProtectionInstance p : ProtectionManager.getAll()){
            if (p.preventItemInteract(event.getPlayer(), event.getPos(), event.getItemStack())) {
                event.getPlayer().displayClientMessage(new StringTextComponent("Cannot use item in structure."), true);
                event.setCancellationResult(ActionResultType.FAIL);
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event){
        if (event.phase == TickEvent.Phase.START) return;

        for (ProtectionInstance p : ProtectionManager.getAll()){
            p.tick();
        }
    }
}

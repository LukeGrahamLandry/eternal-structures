package ca.lukegrahamlandry.eternalstructures.protect;

import ca.lukegrahamlandry.eternalstructures.ModMain;
import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProtectionManager {
    private static final Map<String, ProtectionInstance> protections = new HashMap<>();

    public static Collection<ProtectionInstance> getAll(){
        return protections.values();
    }

    public static ProtectionInstance get(ServerWorld level, BlockPos pos){
        String key = pos.toString() + level.dimension().location();
        if (!protections.containsKey(key)) {
            protections.put(key, new ProtectionInstance(level, pos));
        }
        return protections.get(key);
    }

    public static void put(ServerWorld level, BlockPos pos, ProtectionInstance.Settings settings){
        get(level, pos).changeSettings(settings);
        ModMain.LOGGER.debug("Set protection at {}{} = {}", level.dimension().location(), pos, JsonHelper.get().toJson(settings));
    }

    public static void remove(ServerWorld level, BlockPos pos) {
        String key = pos.toString() + level.dimension().location();
        protections.remove(key);
        ModMain.LOGGER.debug("Protection removed at {}", key);
    }
}

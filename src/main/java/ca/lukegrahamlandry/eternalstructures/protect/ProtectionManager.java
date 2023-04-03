package ca.lukegrahamlandry.eternalstructures.protect;

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
        String key = pos.toString() + level.dimension().location().toString();
        if (!protections.containsKey(key)) {
            protections.put(key, new ProtectionInstance(level, pos));
        }
        return protections.get(key);
    }

    public static void put(ServerWorld level, BlockPos pos, ProtectionInstance.Settings settings){
        get(level, pos).changeSettings(settings);
    }
}

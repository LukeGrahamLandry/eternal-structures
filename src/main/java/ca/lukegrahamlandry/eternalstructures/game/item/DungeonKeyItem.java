package ca.lukegrahamlandry.eternalstructures.game.item;

import ca.lukegrahamlandry.eternalstructures.game.ModRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class DungeonKeyItem extends Item {
    public DungeonKeyItem() {
        super(ModRegistry.PROPS);
    }

    public static int getKeyId(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains("keyid")){
            return tag.getInt("keyid");
        }
        return 0;
    }

    public static void setKeyId(ItemStack stack, int id) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putInt("keyid", id);
        stack.setTag(tag);
    }
}

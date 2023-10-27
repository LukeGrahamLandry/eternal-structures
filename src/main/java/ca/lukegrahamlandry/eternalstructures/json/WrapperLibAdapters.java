/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.eternalstructures.json;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;

public class WrapperLibAdapters {
    public static class BlockPosTypeAdapter implements JsonDeserializer<BlockPos>, JsonSerializer<BlockPos> {
        public BlockPos deserialize(JsonElement data, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            String values = data.getAsString();
            String[] parts = values.split(",");
            return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }

        public JsonElement serialize(BlockPos obj, Type type, JsonSerializationContext ctx) {
            String values = obj.getX() + "," + obj.getY() + "," + obj.getZ();
            return new JsonPrimitive(values);
        }
    }

    public static class ItemStackTypeAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {
        public ItemStack deserialize(JsonElement data, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject json = data.getAsJsonObject();
            ResourceLocation itemKey = new ResourceLocation(json.has("item") ? json.get("item").getAsString() : "minecraft:air");
            Item item = Registry.ITEM.get(itemKey);
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            ItemStack stack = new ItemStack(item, count);

            if (json.has("tag")){
                CompoundNBT tag = ctx.deserialize(json.get("tag"), CompoundNBT.class);
                stack.setTag(tag);
            }

            return stack;
        }

        public JsonElement serialize(ItemStack obj, Type type, JsonSerializationContext ctx) {
            JsonObject out = new JsonObject();
            out.addProperty("item", Registry.ITEM.getKey(obj.getItem()).toString());
            out.addProperty("count", obj.getCount());
            if (obj.hasTag()) out.add("tag", ctx.serialize(obj.getTag()));
            return out;
        }
    }

    public static class NbtTypeAdapter implements JsonDeserializer<CompoundNBT>, JsonSerializer<CompoundNBT> {
        public CompoundNBT deserialize(JsonElement data, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            try {
                return JsonToNBT.parseTag(data.getAsString());
            } catch (CommandSyntaxException e) {
                throw new JsonParseException(e);
            }
        }

        public JsonElement serialize(CompoundNBT obj, Type type, JsonSerializationContext ctx) {
            return new JsonPrimitive(obj.getAsString());
        }
    }

    public static class EffectTypeAdapter implements JsonDeserializer<EffectInstance>, JsonSerializer<EffectInstance> {
        private static class Info {
            String effect = "minecraft:luck";
            int duration = 100;
            int amplifier = 0;
        }

        public EffectInstance deserialize(JsonElement data, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            Info info = JsonHelper.get().fromJson(data, Info.class);

            Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(info.effect));
            if (effect == null) effect = Effects.LUCK;

            return new EffectInstance(effect, info.duration, info.amplifier);
        }

        public JsonElement serialize(EffectInstance obj, Type type, JsonSerializationContext ctx) {
            Info info = new Info();
            info.amplifier = obj.getAmplifier();
            info.duration = obj.getDuration();
            ResourceLocation effect = ForgeRegistries.POTIONS.getKey(obj.getEffect());
            if (effect != null) info.effect = effect.toString();
            return JsonHelper.get().toJsonTree(info);
        }
    }
}

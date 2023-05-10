# Eternal Structures 

- A collection of utilities that help modpack designers build more interesting custom structures.
- Commissioned by [adam](https://www.curseforge.com/members/adam98991/projects).
- Config in `world/serverconfig/eternalstructures-server.toml`
- For developers: don't forget to `grawlew runData` since generated files are not committed. 

**Dependencies**

- Required: [Geckolib](https://www.curseforge.com/minecraft/mc-mods/geckolib)
- Optional: [Lootr](https://www.curseforge.com/minecraft/mc-mods/lootr)

## Features

**Doors and Keys**

Each door in the world and each key item stack have a magic number in their nbt and if they're the same magic number it'll open. 
In creative, right-clicking on a door with a key will set the key to work on that door. 
You can put it in a chest or look at the nbt and put it in a loot table directly, etc.

Config to disable players opening/closing unlocked doors like normal ones. 

**Spikes**

Several variants of spike block that deal different amounts of damage. 
They start off retracted and extend when a player comes near (no damage is dealt for touching retracted spikes). 
Spike blocks can be moved by pistons. 

Config for their detection radius and reaction time. 

**Loot Blocks**

Several variants of chests and pots, compatible with Lootr.

Each type of container has its own default loot table. 
- ex. `eternalstructures:chests/clay_pot`

You can set a different one by using the /give command to set the lootTable nbt tag on the item 
- ex. `/give @p eternalstructures:clay_pot{lootTable:"minecraft:chests/simple_dungeon"}`


**Structure Protection block**

Creative mode players can right-click block to edit settings. 
Editing the json in the gui is currently really annoying, but you can copy/paste out into a better text editor. 

Example:
```json
{
  "radius": 10,
  "potionEffects": [
    {
      "effect": "minecraft:speed",
      "amplifier": 2,
      "duration": 200
    }
  ],
  "preventBreakAndPlace": true,
  "preventInteract": true
}
```

preventInteract currently stops all left/right clicks, I'll make it more fine-grained soon. 

## TODO (maybe?)

- Lang translations (data gen?)

**Doors**

- Variants with crimson/emerald textures
- Door sounds
- Make the voxel shape render as one big rectangle instead of separate placeholder blocks. 

**Spikes**

- Use correct model for icy and wooden spikes
- Netherite spikes 
- Toggle spikes to use redstone control
- Spike sounds
- Iron -> bloody & copper -> oxidised 
- Bloody deal poison & icy give slowness 
- Fix it dealing damage when in the block but not visually touching the spikes 

**Loot Blocks**

- Trigger chest.animation.json#closing
- Item dropped when broken should specify no loot table
  - Be a normal container instead of a lootr player specific one
- Lootr break warning event 
  - `lootr.HandleBreak` only does it for their own containers, can add to the list?
- Add to `openers` set correctly 
  - ChestUtil only does it automatically for carts. `LootrChestTileEntity#stopOpen` does it manually.
  - Who owns which version of the inventory is tracked separately by the stuff in world/data/lootr
  - Only used for `lootr:loot_count`, `/lootr openers`, different visuals for pre-opened blocks
- Fix [crash with schematics](https://github.com/adam9899/MCE2/issues/119)

**Structure Protection block**

- Customizable particle effect when trying to break blocks in a protected area (accepts entries inside the block)
- Item to unlock block protection in an area, only applies per player who has the item. 
  - Click the block in creative to set the item. 
    - Could just be in the json? 
    - Probably need to include nbt tags so being able to just click instead of needing to type it out would be nice 
- Plays a customizable sound (configure inside the block) when the necessary item is retrieved 
  - Sends a customizable message in chat.
- Places a cube of barrier blocks outlining a set range
  - Optional timer to remove the cube 
  - Option to remove the cube on the collection of a selectable item
- stop flight in the radius, creative, elytra and if possible "jetpack" type of flight
- enter items that right click won't work in a radius, eg ender pearls or special devices players could use for breaking blocks
   - data pack tag. configure per protection block.
- instant death if you enter a range without the access item
  - customize death message 
  - warning message when you get close
  - do you just forever lose your inventory if you die this way cause your items will drop inside the range?
- preset jsons in a data pack that you can select in gui, so they can be managed in git instead of structure files. 

**Additions**

- Maps that point to structures 
- Reappearing block 
  - disappears and reappears after a set time
  - vanish when player collides or just after set time interval 
  - shift click with another block in creative to disguise it to look like that
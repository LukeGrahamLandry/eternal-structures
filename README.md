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

## Potential Improvements 

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
- Deals damage when in the block but not visually touching the spikes 

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

**Not Protoblock**

- Settings can be just a blob of json instead of fancy interface
- Potion effects accept any effect by name
- Radius based block protection (breaking blocks, placing blocks)
- Customizable particle effect when trying to break blocks in a protected area (accepts entries inside the block)
- Item to unlock block protection in an area, only applies per player who has the item. 
  - Click a protoblock in creative to set the item.
- Plays a customizable sound (enterable inside the block) when the necessary item is retrieved 
  - Sends a customizable message in chat.
- Places a cube of barrier blocks within the set range of the protoblock
  - Optional timer to remove the cube 
  - Option to remove the cube on the collection of a selectable item
- stop flight in the radius, creative, elytra and if possible "jetpack" type of flight
- enter items that right click won't work in a radius, eg ender pearls or special devices players could use for breaking blocks
   - data pack tag. per block.

**Additions**

- Simplified version of the slime king 
  - jumps high and falls fast 
  - summons slimes that make the main entity immune until the slimes are killed 
  - death animation/boss bar
  - do drops by loot table
- Maps that point to structures 

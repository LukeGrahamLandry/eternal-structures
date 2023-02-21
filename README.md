# Eternal Structures 

- A collection of utilities that help modpack designers build more interesting custom structures.
- Commissioned by [adam](https://www.curseforge.com/members/adam98991/projects).
- For developers: don't forget to `grawlew runData` since generated files are not committed. 
 
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

Several variants of chests and pots, compatible with [Lootr](https://www.curseforge.com/minecraft/mc-mods/lootr). 

## Potential Improvements 

- Variants with crimson/emerald textures
- Door sounds
- Use correct model for icy and wooden spikes
- Netherite spikes 
- Toggle spikes to use redstone control
- Spike sounds
- Iron -> bloody & copper -> oxidised 
- Bloody deal poison & icy give slowness 
- Deals damage when in the block but not visually touching the spikes 

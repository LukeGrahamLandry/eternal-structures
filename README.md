# Eternal Structures 

- A collection of utilities that help modpack designers build more interesting custom structures.
- Commissioned by [adam](https://www.curseforge.com/members/adam98991/projects).
- Config in `world/serverconfig/eternalstructures-server.toml`
- For developers: don't forget to `grawlew runData` since generated files are not committed. 

**Dependencies**

- Required: [Geckolib](https://www.curseforge.com/minecraft/mc-mods/geckolib)
- Optional: [Lootr](https://www.curseforge.com/minecraft/mc-mods/lootr)

# Features

## Doors and Keys

Each door in the world and each key item stack have a magic number in their nbt and if they're the same magic number it'll open. 
In creative, right-clicking on a door with a key will set the key to work on that door. 
You can put it in a chest or look at the nbt and put it in a loot table directly, etc.

Config to disable players opening/closing unlocked doors like normal ones. 

## Spikes

Several variants of spike block that deal different amounts of damage. 
They start off retracted and extend when a player comes near (no damage is dealt for touching retracted spikes). 
Spike blocks can be moved by pistons. 

Config for their detection radius and reaction time. 

## Loot Blocks

Several variants of chests and pots, compatible with Lootr.

Each type of container has its own default loot table. 
- ex. `eternalstructures:chests/clay_pot`

You can set a different one by using the /give command to set the lootTable nbt tag on the item 
- ex. `/give @p eternalstructures:clay_pot{lootTable:"minecraft:chests/simple_dungeon"}`


## Structure Protection block

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
  "preventRightClick": ["minecraft:ender_pearl"],
  "disableFlight": true
}
```

- disableFlight = elytra and creative-like flight  

## Summoning Altar block

Survival mode players can right-click to summon the boss.

Creative mode players can right-click with an empty offhand to edit settings or with an item in their offhand to 
set that item stack (including nbt) as the lootItemStack.

Example: 
```json
{
  "timeoutMinutes": 1.0,
  "summonItem": "minecraft:diamond",
  "summonMessage": "Boss summoned!!!!!!!!!!",
  "bossDeathMessage": "You killed it",
  "summonEntityType": "minecraft:zombie",
  "lootItemStack": {
    "item": "minecraft:apple",
    "count": 1
  },
  "consumeItem": true,
  "potionEffects": [
    {
      "effect": "minecraft:slowness",
      "duration": 200000,
      "amplifier": 2
    }
  ],
  "entityName": "Evil Georg",
  "xpPointsReward": 500
}
```

- summonItem: must be in main hand to summon. 
- timeoutMinutes: how long the altar needs to recharge after the boss is defeated. 
- summonMessage & bossDeathMessage: can be lang translation keys. 

# TODO (maybe?)

- Lang translations (data gen?)
- automated testing of different config combinations
- Factor out the json config-ed tiles 
- Factor out the geo animation state machine tiles
  - don't forget handleUpdateTag for syncing on first load

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
- (maybe works? test me!) Fix [crash with schematics](https://github.com/adam9899/MCE2/issues/119)

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
- stop "jetpack" type of flight in the radius
- enter items that right click won't work in a radius, eg ender pearls or special devices players could use for breaking blocks
   - data pack tag. configure per protection block.
- instant death if you enter a range without the access item
  - customize death message 
  - warning message when you get close
  - do you just forever lose your inventory if you die this way cause your items will drop inside the range?
- preset jsons in a data pack that you can select in gui, so they can be managed in git instead of structure files. 
- don't consume when item uses get blocked 
- display an image on the players screen when within range and have it fade out after x seconds? Usecase is making titles like this appear when the player enters a special dungeon or structure.

**Summoning Altar block**

- break animation when on cooldown
- command on death
- summon minions from the altar block at specific intervals / define their equipment / status effects
- summon different minions based on the bosses health (multiple minions from a list, can specify the health of the boss before they start spawning, it needs to be the number defined or less. IE: zombie;100 (spawns right away), skeleton; 10 (only spawns when the boss has 10% health or less))
- Option to be triggered based on the players proximity to the altar (two different detection methods, box and sphere)
- Option for the alter to not require an item but instead a “sacrifice” which is just x amount of an entity killed in proximity to the altar
- Option to run a command instead of summon an entity upon triggering the altar
- Option to create a sphere or box of barrier blocks in a customizable radius around the Altar upon starting the fight (when the entity is summoned), the barrier blocks are removed upon dying, leaving the area by other means or defeating the boss.
- Summon fireworks above the altar after defeating it
- Play beacon powering up and down for starting and finishing
- Ground shakes for the player after summoning
- Fix z-fighting on the model

**Additions**

- Maps that point to structures 
- Reappearing block 
  - disappears and reappears after a set time
  - vanish when player collides or just after set time interval 
  - shift click with another block in creative to disguise it to look like that 
- Redstone Trap
  - Block that has an adjustable range per block
  - Upon detecting a player in this range, outputs a redstone signal
  - Has a “Redstone Clock” mode that upon the range being met, will output a redstone clock
- Mechanical Spawner
  - can interact in creative mode to give the block entity spawning parameters
  - Unbreakable but will auto-break after its finished spawning entities (the player must kill x number of entities before it breaks)
  - The ability to run a command upon entering a radius instead of spawning a mob. (how many times this should happen before breaking the spawner)
  - Parameters include. 
    - What entity to spawn (can load from a list). 
    - Range the player must be for entities to spawn. 
    - Status effect given to the entities. 
    - Equipment given to the entities. 
    - How many entities to kill before breaking. 
    - The ability to run a command once (useful for summoning a brutal boss)

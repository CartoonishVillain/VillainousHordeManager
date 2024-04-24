# Villainous Horde Manager

The villainous horde manager (or villainous horde library) is a mod designed to help control horde events.

There are two ways you can make your own horde events.
* If you have a mod, and would like to directly integrate into the code base for greater control over the horde systems, you can extend the horde classes and follow the examples in the VillainousHordeLibrary file.
* If you want a quick horde setup, you can follow the example set by hordeJsonData.json. Once the json is complete, you only need to place the file in the server/minecraft directory (just outside of the mods folder).
  * When you do this, you can start your horde with /hordeLibrary startJsonHorde <hordeName>, if you have cheats enabled or operator level 2 permissions.
  * The data in this case has precise requirements. If these are not followed, crashes or other assorted weird behavior may occur. Notes will be given per data point.
 
### Information points for hordes (most relevant to JSON hordes, but information can be helpful to all):
* hordeName: The name that may appear in the logs for your horde, and the name you use to start the horde. (Should be one word, no spaces.)
* maximumActiveHordeMembers: The mob spawn cap at a given moment for the horde. (Integer)
* killsRequiredForEasy(/Normal/Hard): The amount of kills required, per difficulty, to triumph over the horde event. (Integer)
* findSpawnAttempts: How many times should the game look for spawn points? The higher the number, the less likely you are to have hordes end randomly due to being unable to find a spawn point. Higher values will lead to potentially more resource usage in complex environments as it uses more time to find spawn points, though. (Integer)
* bossInfoText: What is the label of the boss bar for the horde event?
* bossInfoColor: What color is the boss bar?
  * Only supports: green, blue, pink, red, purple, and yellow. Any other value will be white.
* despawnLeftBehindMembers: An optimization toggle. If a user runs away from the horde members, and they end up out of range, do we despawn them? (true/false)
* mobdata: An array of mob data, the structure of which is below.

### Information for horde members and horde member data entry (also most relevant for JSON hordes, but information can be helpful to all):
* mobID: the ID of a given mob, such as `minecraft:creeper` for creepers. (If the mob listed is not a pathfinding mob, the game *shouldn't* crash, but it could. Instead it should just end the horde with an error in the logs.)
* spawnWeight: the likelihood this mob spawns in a horde, when compared to all other spawn weights in the horde. (Integer)
* goalPriority: the priority level the "move towards the center player" goal of the horde is in. This may take some tweaking to get right, but for vanilla mobs 2 is *usually* a safe bet. (Integer)
* goalMovementSpeed: the speed of which mobs try to get to the horde center player. 1 is usually recommended, as this is a speed multiplier, but tinker with it to your heart's content. (Floating point number (Use decimals, if you want.))
* nbtData: An array of nbt data, the structure of which is below

## Information for horde member NBT data
* type: The type of data expected from the nbt data, currently the following types are expected: "string", "int", "float", "double", "byte", "boolean", "short". The value for these instances goes in the "value" field later. There is additionally types "effect" and "attribute", with their own special handling
* key: The key of the nbt data to add, not necessary for "effect" or "attribute" types
* value: The value of the nbt data to add as a string, not necessary for "effect" or "attribute" types
* effectData: An array of effect data, the structure of which is below
* attributeData: An array of attribute data, the structure of which is also below

## Information for horde effect data
* effect: The effect's resource key, such as `minecraft:speed`
* duration: The duration of the effect in ticks
* amplifier: The potency of the effect + 1 level
* showParticles: Do particles appear?

## Information for horde attribute data
* attributeID: The attribute's resource key, such as 'minecraft:generic.attack_damage'
* modifierName: The name of the modifier
* modifierAmount: How much to modify the value by
* modifierOperation: The operation to modify the attribute by. 0 is add, 1 is multiply_base, 2 is multiply
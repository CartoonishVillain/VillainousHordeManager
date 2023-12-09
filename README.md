
# Villainous Horde Manager

The villainous horde manager (or villainous horde library) is a mod designed to help control horde events.

There are two ways you can make your own horde events.
* If you have a mod, and would like to directly integrate into the code base for greater control over the horde systems, you can extend the horde classes and follow the examples in the VillainousHordeLibrary file.
* If you want a quick horde setup, you can follow the example set by hordeJsonData.json. Once the json is complete, you only need to place the file in the version directory (outside of the mods folder).
  * When you do this, you can start your horde with /hordeLibrary startJsonHorde <hordeName>, if you have cheats enabled or operator level 2 permissions.
  * The data in this case has precise requirements. If these are not followed, crashes or other assorted weird things may occur. Notes will be given per data point.
 
### Information points for hordes (most relevant to JSON hordes, but information can be helpful to all):
* hordeName: The name that may appear in the logs for your horde, and the name you use to start the horde. (Should be one word, no spaces.)
* maximumActiveHordeMembers: The mob spawncap at a given moment for the horde. (Integer)
* killsRequiredForEasy(/Normal/Hard): The amount of kills required, per difficulty, to triumph over the horde event. (Integer)
* bossInfoText: What is the label of the boss bar for the horde event?
* bossInfoColor: What color is the boss bar?
  * Only supports: green, blue, pink, red, purple, and yellow. Any other value will be white.
* despawnLeftBehindMembers: An optimization toggle. If a user runs away from the horde members, and they end up out of range, do we despawn them? (true/false)

### Information for horde members and horde member data entry (also most relevant for JSON hordes, but information can be helpful to all):
* mobID: the ID of a given mob, such as minecraft:creeper for creepers. (If the mob listed is not a pathfinding mob, the game **will** crash.)
* spawnWeight: the likelihood this mob spawns in a horde, when compared to all other spawn weights in the horde. (Integer)
* goalPriority: the priority level the "move towards the center player" goal of the horde is in. This may take some tweaking to get right, but for vanilla mobs 2 is *usually* a safe bet. (Integer)
* goalMovementSpeed: the speed of which mobs try to get to the horde center player. 1 is usually recommended, as this is a speed multiplier, but tinker with it to your heart's content. (Floating point number (Use decimals, if you want.))


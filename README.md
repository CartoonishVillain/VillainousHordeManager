# Villainous Horde Manager

The villainous horde manager (or villainous horde library) is a mod designed to help control horde events.

There are two ways you can make your own horde events.
* If you have a mod, and would like to directly integrate into the code base for greater control over the horde systems, feel free to extend on of the horde classes.
* If you want a quick horde setup, you can follow the example set in the !EXAMPLEHORDEJSONS folder. Once a horde json is complete, you only need to place the file in the config folder, inside a subfolder called "villainoushordemanager"
  * When you do this, you can start your horde with /hordeManager startJsonHorde <hordeName>, if you have cheats enabled or operator level 2 permissions.
  * The data in this case has precise requirements. If these are not followed, crashes or other assorted weird behavior may occur.
 
### Information points for hordes (most relevant to JSON hordes, but information can be helpful to all):
* hordeName: The name that may appear in the logs for your horde, and the name you use to start the horde. (Should be one word, no spaces.)
* advancementForStartingHorde: The resource location of an advancement to give the initial anchor player for starting the horde. Leave as an empty string for no advancement
* advancementForWinningAgainstHorde: The resource location of an advancement to give to all players who are within range when a horde event is won.
* shouldClearWinningAdvancement: A boolean, when true, the winning advancement is revoked after being awarded. If the advancement has awards attached, this allows repeatable rewards.
* waves: An array of JsonWaveData objects, used to define the waves of a horde. At least one wave should be added.

### Information for JsonWaveData
* wavename: A string for the name of the wave, used internally, advised you make unique names.
* maximumActiveHordeMembers: Integer, how many horde members should be spawned in at a given time?
* killsRequiredForEasy: Integer, if on easy difficulty, how many kills are required to complete the wave?
* killsRequiredForNormal: Integer, if on normal difficulty, how many kills are required to complete the wave?
* killsRequiredForHard: Integer, if on normal difficulty, how many kills are required to complete the wave?
* bossInfoText: String, The boss bar title of the wave
* bossInfoColor: String, the boss bar color of the wave, options: green, blue, pink, red, purple, yellow, white
* despawnLeftBehindMembers: Boolean, When horde entities are ran away from effectively, and are no longer tracked by the horde, should they despawn?
* mobData: An array of mob data, see the horde member data entry below for more information
* bossMobData: An array of mob data, when the players finish the wave, if any entities are defined, they are spawned in a boss phase, and the wave will not end until each boss entity is killed. Spawn weight is ignored, and every entry is spawned once (unless abandoned, then they'll respawn)
* keepSpawningEnemiesWhileBossIsActive: Boolean, if the wave has a boss phase, should the main horde pool still spawn to support the boss?
* bossInfoTextWhenBossIsActive: bossInfoText for the wave's boss phase
* bossInfoColorWhenBossIsActive: bossInfoColor for the wave's boss phase

### Information for horde members and horde member data entry (also most relevant for JSON hordes, but information can be helpful to all):
* mobID: the ID of a given mob, such as `minecraft:creeper` for creepers. (If the mob listed is not a pathfinding mob, the game *shouldn't* crash, but it could. Instead it should just end the horde with an error in the logs.)
* spawnWeight: the likelihood this mob spawns in a horde, when compared to all other spawn weights in the horde. (Integer)
* goalPriority: the priority level the "move towards the center player" goal of the horde is in. This may take some tweaking to get right, but for vanilla mobs 2 is *usually* a safe bet. (Integer)
* goalMovementSpeed: the speed of which mobs try to get to the horde center player. 1 is usually recommended, as this is a speed multiplier, but tinker with it to your heart's content. (Floating point number (Use decimals, if you want.))
* nbtData: The string containing all the nbt data you'd like an entity to have. This should be the same syntax one would use for a summon command. 
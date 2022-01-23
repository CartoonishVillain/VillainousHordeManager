# VillainousHordeLibrary
 A library for folks who want to include Raid-like events!

 This library is work in progress! Feel free to contribute! Please try to stay within the spec guidelines <Link here>.
 Going too far out of spec or out of the spirit of the library may result in a denied PR.

 The goal of this project is to enable easier creation of events like raids within Minecraft.
 This should provide the basic starting point for a prospective mod creator to make their own raid events.
 While also being flexible, allowing for overrides to put fine details into certain things (such as special conditions for what spawns where).

Usage: 
It would be preferred if you made the CurseForge project a dependency as you base your code off of the horde system.
This however is not absolutely required. You are free to copy and paste code while following the repository's license.

By contributing to this repository you agree to have your code under the license found in the file LICENSE.

# Warning! This library is under testing at the moment. Things may be quite unstable. Check back later for a better experience!

## Step 1 - Installation
Working on a proper installation method and example. An example exists in the 1.18-Example branch, but frankly one as a seperate mod would be better

## Step 2 - Instantiation
Instantiate the Horde object (Or make your own extension of it and instantiate that) in your
main mod file. We recommend you instantiate the object with the ServerStartingEvent.

In the ServerStartingEvent after you instantiate the horde instance, you will need to pass it EntityHordeData objects for each entity you want in the horde as well.
These objects require a few items:

int goalPriority - What priority should the horde movement goal (gets the mobs to converge on a position) have?

int SpawnWeight - Weights for how likely entities are to spawn amongst other entries. Higher increases odds.

EntityType type - The entity type of the entity you're putting into the horde

Class<T> entityClass - The class of the entity you're putting into the horde

double goalMovementSpeed - The movement speed multiplier you want applied to the convergence of horde entities.

## Step 3 - Connect
Attach the horde instance's tick() method to a tick event. Preferably, the world tick event.

## Step 4 - Start
Start the horde event you have crafted by running SetUpHorde(<ServerPlayer>) where the ServerPlayer is who the event will be tracking first.

Example:
See 1.18-Example branch for an example. All necessary code for the most basic of hordes is in the CartoonishHorde Class

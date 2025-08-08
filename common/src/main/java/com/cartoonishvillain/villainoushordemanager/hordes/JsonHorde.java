package com.cartoonishvillain.villainoushordemanager.hordes;

import com.cartoonishvillain.villainoushordemanager.JsonHordeMovementGoal;
import com.cartoonishvillain.villainoushordemanager.TypeHordeMovementGoal;
import com.cartoonishvillain.villainoushordemanager.VillainousHordeManager;
import com.cartoonishvillain.villainoushordemanager.data.json.JsonMobData;
import com.cartoonishvillain.villainoushordemanager.data.json.JsonWaveData;
import com.cartoonishvillain.villainoushordemanager.hordedata.EntityTypeHordeData;
import com.cartoonishvillain.villainoushordemanager.mixin.LivingGoalAccessor;
import com.cartoonishvillain.villainoushordemanager.platform.Services;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

//TODO
// * ADD IN INITIAL ADVANCEMENT DISTRIBUTION
// * VERIFY BOSS LOGIC IS WORKING
//    * DATA CREATION ======
//    * Determine what a wave without a boss looks like json wise
//    * Does it work without a boss?
//    * Does it work with one boss?
//    * Does it work with multiple bosses
//    * FOR ALL DATA CASES ======
//    * Does it work under ideal player conditions? (Player stands and fights in an open field with no spawn obstructions)
//    * Does it work with bad spawning conditions? (Icy terrain or ocean with no land in sight)
//    * Verify that advancements are given out appropriately when winning
//    * If we're meant to revoke advancements, make sure that works too
//    * WITH ONE OR MORE BOSSES ======
//    * Does it work when the player unloads the entity (Both by using the built in unload system and by flying or teleporting out of simulation distance)
//    * Does it work when the player pushes the entity into a portal?
//    * Does it work when the player kills the entity normally.

public class JsonHorde {
    protected ServerLevel world;
    protected BlockPos center;
    protected Boolean hordeActive = false;
    protected ArrayList<JsonWaveData> waves;
    protected MinecraftServer server;
    protected int Alive = 0;
    protected int initAlive = 0;
    protected int Active = 0;
    protected int allowedActive;
    protected int updateCenter = 0;
    protected ServerPlayer hordeAnchorPlayer;
    protected ArrayList<ServerPlayer> players = new ArrayList<>();
    protected ArrayList<LivingEntity> activeHordeMembers = new ArrayList<>();
    protected ServerBossEvent bossInfo;
    protected ServerBossEvent oldBossInfo;
    protected ArrayList<EntityTypeHordeData<?>> hordeData = new ArrayList<>();
    protected String hordeName;
    protected Boolean despawnLeftBehindMembers;
    protected int easyKillCount;
    protected int normalKillCount;
    protected int hardKillCount;
    protected ArrayList<Integer> spawnWeights = new ArrayList<>();
    protected int hordeWaveNumber = 0;

    protected String advancementForStarting;
    protected String advancementForWinning;
    boolean shouldClearWinningAdvancement;

    protected ArrayList<JsonMobData> bossEntities = new ArrayList<>();
    protected ArrayList<JsonMobData> bossEntitiesTracked = new ArrayList<>();
    protected ArrayList<JsonMobData> bossEntitiesSpawned = new ArrayList<>();
    protected ServerBossEvent bossEventForBossEntities;
    protected ArrayList<LivingEntity> activeBossMembers = new ArrayList<>();
    protected boolean isSpawningBosses = false;
    protected boolean bossPhase = false;
    protected HashMap<PathfinderMob, JsonMobData> bossTracker = new HashMap<>();
    protected boolean shouldBossKeepSpawningHorde = false;

    /**
     * The enum of reasons why the Horde may end.
     */
    public enum HordeStopReasons {
        VICTORY, //Players beat the event
        DEFEAT, //Players are defeated or quit the event.
        PEACEFUL, //Server changed to peaceful mid-horde, event canceled.
        SPAWN_ERROR  //Players are in a position that causes the spawn manager to panic and shut down the event before it hangs the server.
    }

    /**
     * Constructor for the EntityTypeHorde system
     * @param server  a MinecraftServer instance helps the horde system keep track of players who should be involved in the horde process.
     */
    public JsonHorde(
            MinecraftServer server,
            ArrayList<JsonWaveData> waves,
            String advancementForStarting,
            String advancementForWinning,
            boolean shouldClearWinningAdvancement,
            String hordeName
    ) {
        this.server = server;
        this.waves = waves;
        this.hordeName = hordeName;
        this.advancementForStarting = advancementForStarting;
        this.advancementForWinning = advancementForWinning;
        this.shouldClearWinningAdvancement = shouldClearWinningAdvancement;
        getDataFromWave(waves.getFirst(), false);
    }

    /**
     * Updates the horde with the new information for a new wave.
     * @param waveData The wave data to pull from
     * @param newWave True if the wave being pulled from is not the first wave
     */
    private void getDataFromWave(JsonWaveData waveData, Boolean newWave) {
        easyKillCount = waveData.getKillsRequiredForEasy();
        normalKillCount = waveData.getKillsRequiredForNormal();
        hardKillCount = waveData.getKillsRequiredForHard();
        allowedActive = waveData.getMaximumActiveHordeMembers();
        BossEvent.BossBarColor bossColor = switch (waveData.getBossInfoColor().toLowerCase()) {
            case "green" -> BossEvent.BossBarColor.GREEN;
            case "blue" -> BossEvent.BossBarColor.BLUE;
            case "pink" -> BossEvent.BossBarColor.PINK;
            case "red" -> BossEvent.BossBarColor.RED;
            case "purple" -> BossEvent.BossBarColor.PURPLE;
            case "yellow" -> BossEvent.BossBarColor.YELLOW;
            default -> BossEvent.BossBarColor.WHITE;
        };

        BossEvent.BossBarColor bossbossColor = switch (waveData.getBossInfoColorWhenBossIsActive().toLowerCase()) {
            case "green" -> BossEvent.BossBarColor.GREEN;
            case "blue" -> BossEvent.BossBarColor.BLUE;
            case "pink" -> BossEvent.BossBarColor.PINK;
            case "red" -> BossEvent.BossBarColor.RED;
            case "purple" -> BossEvent.BossBarColor.PURPLE;
            case "yellow" -> BossEvent.BossBarColor.YELLOW;
            default -> BossEvent.BossBarColor.WHITE;
        };

        despawnLeftBehindMembers = waveData.isDespawnLeftBehindMembers();
        if (bossInfo != null) {
            oldBossInfo = bossInfo;
        }
        bossInfo = new ServerBossEvent(Component.literal(waveData.getBossInfoText()), bossColor, BossEvent.BossBarOverlay.PROGRESS);

        ArrayList<EntityTypeHordeData<?>> entityHordeDataList = new ArrayList<>();
        for (JsonMobData mobData : waveData.getMobData()) {
            Optional<EntityType<?>> type = EntityType.byString(mobData.getMobID());
            if (type.isPresent()) {
                entityHordeDataList.add(
                        new EntityTypeHordeData(
                                mobData.getGoalPriority(),
                                mobData.getGoalMovementSpeed(),
                                mobData.getSpawnWeight(),
                                type.get(),
                                mobData.getNbtData()
                        )
                );
            } else {
                Services.PLATFORM.getLOGGER().warn("VillainousHordeManager - Failed to load json mob of type: " + mobData.getMobID());
            }
        }

        bossEntitiesTracked.clear();
        bossEntitiesSpawned.clear();
        bossEventForBossEntities = null;
        activeBossMembers.clear();
        isSpawningBosses = false;
        bossPhase = false;
        bossTracker.clear();
        shouldBossKeepSpawningHorde = false;

        bossEntities.addAll(waveData.getBossMobData());
        bossEntitiesTracked.addAll(bossEntities);
        bossEventForBossEntities = new ServerBossEvent(Component.literal(waveData.getBossInfoTextWhenBossIsActive()), bossbossColor, BossEvent.BossBarOverlay.PROGRESS);
        shouldBossKeepSpawningHorde = waveData.shouldKeepSpawningEnemiesWhileBossIsActive();

        setHordeData(entityHordeDataList);

        spawnWeights = new ArrayList<>();
        for (EntityTypeHordeData<?> hordeEntry : hordeData) {
            spawnWeights.add(hordeEntry.getSpawnWeight());
        }

        if (newWave) {
            SetUpHorde(hordeAnchorPlayer);
        }
    }


    /**
     * Clears out all data for a given horde and ends it.
     * You would want to override this to clean up any additional information you're tracking.
     * @param stopReason The code for why the error ended.
     */
    public void Stop(HordeStopReasons stopReason) {
        try {
            if (stopReason == HordeStopReasons.VICTORY && !advancementForWinning.isEmpty()) {
                awardAdvancement(this.bossInfo.getPlayers(), advancementForWinning);
            }

            if (stopReason == HordeStopReasons.VICTORY && !advancementForWinning.isEmpty() && shouldClearWinningAdvancement) {
                AdvancementHolder advancement = server.getAdvancements().get(ResourceLocation.parse(advancementForWinning));
                for (ServerPlayer player : this.bossInfo.getPlayers()) {
                    AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
                    for (String s : progress.getCompletedCriteria()) {
                        player.getAdvancements().revoke(advancement, s);
                    }
                }
            }
        } catch (NullPointerException ignored) {}

        this.bossInfo.setVisible(false);
        bossInfo.removeAllPlayers();
        hordeActive = false;
        Alive = 0;
        initAlive = 0;
        Active = 0;
        hordeAnchorPlayer = null;
        activeHordeMembers.clear();
        center = null;
        players.clear();

        bossEntitiesTracked.clear();
        bossEntitiesSpawned.clear();
        bossEventForBossEntities = null;
        activeBossMembers.clear();
        isSpawningBosses = false;
        bossPhase = false;
        bossTracker.clear();
        shouldBossKeepSpawningHorde = false;

        advancementForWinning = "";
        advancementForStarting = "";

        switch (stopReason) {
            case VICTORY -> Services.PLATFORM.getLOGGER().info("Player Victory against " + hordeName);
            case DEFEAT -> Services.PLATFORM.getLOGGER().info("Player Defeat against" + hordeName);
            case SPAWN_ERROR -> Services.PLATFORM.getLOGGER().error(hordeName + " canceled! Could not locate spawn placement! (Entities are too big, or terrain is too noisy)");
            case PEACEFUL -> Services.PLATFORM.getLOGGER().info(hordeName + " canceled, server changed to peaceful!");
        }
    }

    /**
     * @return If the horde instance is currently active.
     */
    public Boolean getHordeActive() {
        return hordeActive;
    }

    /**
     * Initial phase. The EntityTypeHorde targets a specific player as it's anchor point (where horde members approach, and base their spawning off of)
     * Now would be a good time to set up additional information to track if needed.
     * @param serverPlayer A server player entity for the horde to track.
     */
    public void SetUpHorde(ServerPlayer serverPlayer) {
        if (serverPlayer.level() instanceof ServerLevel) {
            world = (ServerLevel) serverPlayer.level();

            if (serverPlayer.level().dimension().equals(world.dimension())) {
                hordeAnchorPlayer = serverPlayer;
                //Set alive counter based on difficulty
                switch (world.getDifficulty()) {
                    case EASY -> setEasyDifficultyStats();
                    case NORMAL -> setNormalDifficultyStats();
                    case HARD -> setHardDifficultyStats();
                    case PEACEFUL -> {
                        return;
                    }
                }

                if (hordeActive == false && !advancementForStarting.isEmpty()) {
                    awardAdvancement(List.of(serverPlayer), advancementForStarting);
                }

                setActiveMemberCount();
                setCenterBlock(serverPlayer.blockPosition());
                hordeActive = true;
            }
        }
    }

    /**
     * Used in initial setup to set the maximum amount of entities that are allowed to spawn in an event at once.
     * Recommend you override to set up your own way of setting this up (whether by configs or other means)
     */
    public void setActiveMemberCount() {

    }

    /**
     *   Used in initial setup to set how many entities players need to defeat before the horde ends on Easy.
     *   Recommend you override to set up your own way of setting this up (whether by configs or other means)
     */
    public void setEasyDifficultyStats() {
        Alive = easyKillCount;
        initAlive = easyKillCount;
    }

    /**
     *   Used in initial setup to set how many entities players need to defeat before the horde ends on Normal.
     *   Recommend you override to set up your own way of setting this up (whether by configs or other means)
     */
    public void setNormalDifficultyStats() {
        Alive = normalKillCount;
        initAlive = normalKillCount;
    }

    /**
     *   Used in initial setup to set how many entities players need to defeat before the horde ends on Hard.
     *   Recommend you override to set up your own way of setting this up (whether by configs or other means)
     */
    public void setHardDifficultyStats() {
        Alive = hardKillCount;
        initAlive = hardKillCount;
    }

    /**
     *   Used to update the center block to control mob spawn positions and where mobs wander towards
     *   @param centerPosition - A BlockPos to set the center variable to.
     */
    public void setCenterBlock(BlockPos centerPosition) {
        this.center = centerPosition;
    }

    /**
     *   Checks if a given player is still properly alive. This is to avoid desync issues.
     *   @param serverPlayer - ServerPlayer to check.
     */
    protected boolean checkIfPlayerIsStillValid(ServerPlayer serverPlayer) {
        return serverPlayer.getHealth() != 0.0f && !serverPlayer.isRemoved();
    }

    /**
     *   Checks during tick if peaceful difficulty is set. If it is, the horde is automatically ended.
     */
    protected void PeacefulCheck() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.Stop(HordeStopReasons.PEACEFUL);
        }
    }

    /**
     *   Automatically updates the horde center position
     */
    protected void updateCenter() {
        if (updateCenter == 0) {
            center = hordeAnchorPlayer.getOnPos();
            updateCenter = 100;
            updatePlayers();
            if (!bossPhase || shouldBossKeepSpawningHorde) updateHorde();
            if (bossPhase) updateBosses();
        } else {
            updateCenter--;
        }
    }

    /**
     *   The tick event. The heart and soul of the horde. Patch your version of the tick event into the world tick to allow the horde to function when activated!
     *   For additional or generally different functionality you can override this
     */
    public void tick() {
        if (hordeActive && bossPhase) {
            if (isSpawningBosses || !bossEntitiesTracked.isEmpty()) {
                if (hordeAnchorPlayer.level().dimensionType().equals(world.dimensionType()) && checkIfPlayerIsStillValid(hordeAnchorPlayer)) {
                    PeacefulCheck();
                    if(!hordeActive) return;

                    if (oldBossInfo != null) {
                        oldBossInfo.setVisible(false);
                        for (ServerPlayer player : oldBossInfo.getPlayers()) bossInfo.addPlayer(player);
                        oldBossInfo.removeAllPlayers();
                        oldBossInfo = null;
                    }
                    this.bossInfo.setVisible(true);

                    //spawn entities as needed/keep horde ticking
                    if (bossEntitiesSpawned.size() < bossEntitiesTracked.size()) { {
                            PathfinderMob mob = spawnBossMember(bossEntitiesTracked.get(bossEntitiesSpawned.size()));
                            if (mob != null) {
                                bossTracker.put(mob, bossEntitiesSpawned.getLast());
                            }
                    }} else isSpawningBosses = false;

                    if (shouldBossKeepSpawningHorde) {

                        //Keeps Active counter updated
                        if (Active != activeHordeMembers.size()) {
                            Active = activeHordeMembers.size();
                        }

                        //If we have room to spawn more horde members, spawn more
                        if (Active < allowedActive) {
                            spawnHordeMember();
                        }
                    }

                    //track entities
                    updateCenter();

                } else {
                    //look for viable player, or cancel.
                    updatePlayers();
                    if (players.isEmpty()) {
                        this.Stop(HordeStopReasons.DEFEAT);
                    } else {
                        bossInfo.removePlayer(hordeAnchorPlayer);
                        hordeAnchorPlayer = players.get(0);
                        players.remove(0);
                    }
                }
            } else {
                //We're no longer spawning bosses, and the activeBossMembers are dead. The battle is over.
                bossPhase = false;
                //clear the boss entities list so that we can move back to the normal logic to end this wave.
                bossEntities.clear();
            }
        }

        //Normal Horde phase
        if (hordeActive && !bossPhase) {
            if (Alive > 0) {
                if (hordeAnchorPlayer.level().dimensionType().equals(world.dimensionType()) && checkIfPlayerIsStillValid(hordeAnchorPlayer)) {
                    PeacefulCheck();
                    if(!hordeActive) return;

                    //Keeps Active counter updated
                    if (Active != activeHordeMembers.size()) {
                        Active = activeHordeMembers.size();
                    }

                    if (oldBossInfo != null) {
                        oldBossInfo.setVisible(false);
                        for (ServerPlayer player : oldBossInfo.getPlayers()) bossInfo.addPlayer(player);
                        oldBossInfo.removeAllPlayers();
                        oldBossInfo = null;
                    }
                    this.bossInfo.setVisible(true);


                    //If we have room to spawn more horde members, spawn more
                    if (Active < allowedActive) {
                        spawnHordeMember();
                    }

                    if (hordeActive) {
                        updateCenter();

                        float aliveDivision = ((float) Alive / initAlive);
                        this.bossInfo.setProgress(Mth.clamp(aliveDivision, 0.0f, 1f));

                    }
                } else {
                    //look for viable player, or cancel.
                    updatePlayers();
                    if (players.isEmpty()) {
                        this.Stop(HordeStopReasons.DEFEAT);
                    } else {
                        bossInfo.removePlayer(hordeAnchorPlayer);
                        hordeAnchorPlayer = players.get(0);
                        players.remove(0);
                    }
                }
            } else if (!bossEntitiesTracked.isEmpty()) {
                // If no alive tickets remain, and boss entities exist, start the boss phase;
                bossPhase = true;
                isSpawningBosses = true;
                oldBossInfo = bossInfo;
                bossInfo = bossEventForBossEntities;
            } else {
                // if we've somehow surpassed, or are equal to the indexed size of the waves, we declare a victory over the horde
                if (hordeWaveNumber >= waves.size()-1) {
                    this.Stop(HordeStopReasons.VICTORY);
                } else {
                    hordeWaveNumber += 1; //Otherwise, we increase the wave number, and set the new data
                    getDataFromWave(waves.get(hordeWaveNumber), true);
                }
            }
        }
    }

    /**
     * Checks if new players have entered in range of the event and adds them into the tracking.
     * Checks if players are no longer in range of the event and removes them from the tracking.
     */
    protected void updatePlayers() {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            if (this.hordeAnchorPlayer == serverPlayer) {
                bossInfo.addPlayer(serverPlayer);
                continue;
            }
            //player is not the tracked player and is in the same world as the tracked world.
            if (serverPlayer.level().dimensionType().equals(world.dimensionType()) && checkIfPlayerIsStillValid(serverPlayer)) {
                double distance = Mth.sqrt((float) serverPlayer.distanceToSqr(center.getX(), center.getY(), center.getZ()));
                if (distance < 64) {
                    if (!players.contains(serverPlayer)) {
                        bossInfo.addPlayer(serverPlayer);
                        players.add(serverPlayer);
                    }
                } else {
                    bossInfo.removePlayer(serverPlayer);
                    players.remove(serverPlayer);
                }

            } else {
                bossInfo.removePlayer(serverPlayer);
                players.remove(serverPlayer);
            }

        }
    }

    /**
     *   Takes stock of the status of horde members. Removes missing and dead members and updates tallies accordingly.
     */
    protected void updateHorde() {
        ArrayList<LivingEntity> removals = new ArrayList<>();
        ArrayList<LivingEntity> deleteMobs = new ArrayList<>();
        for (LivingEntity hordeMember : activeHordeMembers) {

            if (hordeMember.isDeadOrDying()) {
                removals.add(hordeMember);
                UnitDown();
            } else if (hordeMember.isRemoved()) {
                removals.add(hordeMember);
                UnitLost();
            }

            BlockPos hordeTarget = center;
            if (Mth.sqrt((float) hordeMember.distanceToSqr(hordeTarget.getX(), hordeTarget.getY(), hordeTarget.getZ())) > 64) {
                removeGoal((PathfinderMob) hordeMember);
                removals.add(hordeMember);
                if (despawnLeftBehindMembers) deleteMobs.add(hordeMember);
                UnitLost();
            }
        }

        for (LivingEntity removal : removals) {
            activeHordeMembers.remove(removal);
        }

        for (LivingEntity removal : deleteMobs) {
            removal.remove(Entity.RemovalReason.DISCARDED);
        }

        removals.clear();
    }

    private void updateBosses() {
        ArrayList<LivingEntity> removals = new ArrayList<>();
        ArrayList<LivingEntity> deleteMobs = new ArrayList<>();

        for (LivingEntity hordeMember : activeBossMembers) {

            if (hordeMember.isDeadOrDying()) {
                removals.add(hordeMember);
            } else if (hordeMember.isRemoved()) {
                deleteMobs.add(hordeMember);
            }

            BlockPos hordeTarget = center;
            if (Mth.sqrt((float) hordeMember.distanceToSqr(hordeTarget.getX(), hordeTarget.getY(), hordeTarget.getZ())) > 64) {
                removeGoal((PathfinderMob) hordeMember);
                if (despawnLeftBehindMembers) deleteMobs.add(hordeMember);
            }
        }

        for (LivingEntity removal : removals) {
            activeBossMembers.remove(removal);
            bossEntitiesTracked.remove(bossTracker.get((PathfinderMob) removal));
            bossTracker.remove((PathfinderMob) removal);
        }

        for (LivingEntity removal : deleteMobs) {
            bossEntitiesSpawned.remove(bossTracker.get((PathfinderMob) removal));

            //Cycle the removed entity to be the last index, since we need to spawn it again.
            bossEntitiesTracked.remove(bossTracker.get((PathfinderMob) removal));
            bossEntitiesTracked.add(bossTracker.get((PathfinderMob) removal));

            activeBossMembers.remove(removal);
            bossTracker.remove(removal);
            removal.remove(Entity.RemovalReason.DISCARDED);
        }

        deleteMobs.clear();
        removals.clear();
    }


    /**
     *   Begins the search for a valid spawnpoint for horde members.
     */
    protected Optional<BlockPos> getValidSpawn(EntityType<?> type) {
        for (int i = 0; i < 3; ++i) {
            BlockPos blockPos = this.findRandomSpawnPos(20, type);
            if (blockPos != null) return Optional.of(blockPos);
        }
        return Optional.empty();
    }

    /**
     *   Finds the random spawn position for horde members
     */
    protected BlockPos findRandomSpawnPos(int loopvar, EntityType<?> type) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int a = 0; a < loopvar; ++a) {
            double DISTANCE = -1;
            int j = Integer.MAX_VALUE, l = Integer.MAX_VALUE;
            while ((!(DISTANCE > 450 && DISTANCE < 1250))) { //check for appropriate distance from start and proper biome
                j = randFinder(this.center.getX());
                l = randFinder(this.center.getZ());
                DISTANCE = center.distSqr(new BlockPos(j, center.getY(), l));
            }

            int k = findSafeYPosition(j, l, type, false);
            if (k != world.getMinBuildHeight() - 1) {
                blockPos.set(j, k, l);
                return blockPos;
            }
        }

        //if a safe spot isn't found after loopvar tries, run the unfiltered search.
        double DISTANCE = -1;
        int j = Integer.MAX_VALUE, l = Integer.MAX_VALUE;
        while ((!(DISTANCE > 450 && DISTANCE < 1250))) { //check for appropriate distance from start and proper biome
            j = randFinder(this.center.getX());
            l = randFinder(this.center.getZ());
            DISTANCE = center.distSqr(new BlockPos(j, center.getY(), l));
        }

        int k = findSafeYPosition(j, l, type, true);
        if (k != world.getMinBuildHeight() - 1) {
            blockPos.set(j, k, l);
            return blockPos;
        }
        return null;
    }


    /**
     *   Usage: Finds the y spawnpoint for horde members.
     */
    protected int findSafeYPosition(int xValue, int zValue, EntityType<?> entityType, boolean unfiltered) {
        int maxHeight;
        int minHeight;
        if (unfiltered) {
            maxHeight = world.getMaxBuildHeight() + 1;
            minHeight = world.getMinBuildHeight() + 1;
        } else {
            maxHeight = center.getY() + 25;
            minHeight = center.getY() - 25;
        }
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        BlockState blockState;
        for (int baseYValue = center.getY(); baseYValue < maxHeight; baseYValue++) {
            blockPos.set(xValue, baseYValue - 1, zValue);
            blockState = world.getBlockState(blockPos);

            SpawnPlacementType spawnplacementtype = SpawnPlacements.getPlacementType(entityType);
            //Vexes have "No Restriction" as their type, as do many other land based mobs..
            //If we find this case, we will manually tell them to spawn on the ground.
            //Seriously, why would a ravager spawn in the sky?
            SpawnPlacementType vexType = SpawnPlacements.getPlacementType(EntityType.VEX);
            if (spawnplacementtype.getClass() == vexType.getClass()) spawnplacementtype = SpawnPlacementTypes.ON_GROUND;

            if (!spawnplacementtype.isSpawnPositionOk(world, blockPos, entityType) || blockState.equals(Blocks.BEDROCK.defaultBlockState())) {
                //if there is no floor, don't bother.
                continue;
            }
            return baseYValue;
        }

        //Look below the player if a valid spot above isn't found
        for (int baseYValue = center.getY(); baseYValue > minHeight; baseYValue--) {
            blockPos.set(xValue, baseYValue - 1, zValue);
            blockState = world.getBlockState(blockPos);

            SpawnPlacementType spawnplacementtype = SpawnPlacements.getPlacementType(entityType);
            //Vexes have "No Restriction" as their type, as do many other land based mobs..
            //If we find this case, we will manually tell them to spawn on the ground.
            //Seriously, why would a ravager spawn in the sky?
            SpawnPlacementType vexType = SpawnPlacements.getPlacementType(EntityType.VEX);
            if (spawnplacementtype.getClass() == vexType.getClass()) spawnplacementtype = SpawnPlacementTypes.ON_GROUND;

            if (!spawnplacementtype.isSpawnPositionOk(world, blockPos, entityType) || blockState.equals(Blocks.BEDROCK.defaultBlockState())) {
                //if there is no floor, don't bother.
                continue;
            }
            return baseYValue;
        }
        return world.getMinBuildHeight() - 1;
    }

    /**
     * Randomizes a coordinate based on the center coordinate
     * @param centercoord the center tracking coordinate of the horde
     * @return the randomized coordinate.
     */
    protected int randFinder(int centercoord) {
        return centercoord + (this.world.random.nextInt(25 + 25) - 25);
    }

    /**
     * Tallies the active spawn cap after spawning an entity in the horde.
     */
    public void SpawnUnit() {
        Active++;
    }

    /**
     * Tallies the active spawn cap after dragging a unit pre-existing into the horde.
     */
    public void InviteUnit() {
        Active++;
    }

    /**
     * Tallies the active and total alive entities down because the unit was killed
     */
    public void UnitDown() {
        Active--;
        Alive--;
    }

    /**
     * Tallies the active spawn cap down as a unit fell out of range.
     */
    public void UnitLost() {
        Active--;
    }

    /**
     *   Spawns horde entities.
     */
    protected void spawnHordeMember() {
        Optional<BlockPos> hordeSpawn = Optional.empty();

        int combined = 0;
        for (Integer weight : spawnWeights) combined += weight;
        Random random = new Random();
        int rng = random.nextInt(combined);
        int selected = -1;
        int counter = 0;
        for (Integer weights : spawnWeights) {
            if ((rng + 1 - weights) <= 0) {
                selected = counter;
                break;
            } else counter++;
            rng -= weights;
        }

        EntityTypeHordeData<?> entrySelected = hordeData.get(selected);
        PathfinderMob pathfinderMob;

        try {
             pathfinderMob = entrySelected.createInstance(world);
        } catch (ClassCastException e) {
            this.Stop(HordeStopReasons.SPAWN_ERROR);
            Services.PLATFORM.getLOGGER().error("Villainous Horde Manager - WARNING! One or more of the mobs in your JSON horde are not a descendant of PathfinderMob. Horde canceled due to this.");
            return;
        }


        int attempts = 0;
        while (hordeSpawn.isEmpty()) {
            hordeSpawn = this.getValidSpawn(entrySelected.getType());
            attempts++;
            if (hordeSpawn.isEmpty() && attempts >= 20) {
                return; //Abort the spawning process after trying this much to free resources, try again on the next batch.
            }
        }


        if (pathfinderMob != null) {
            pathfinderMob.setPos(hordeSpawn.get().getX(), hordeSpawn.get().getY(), hordeSpawn.get().getZ());
            injectGoal(pathfinderMob, entrySelected, entrySelected.getGoalMovementSpeed());
            Services.PLATFORM.finalizeSpawn(pathfinderMob, world, pathfinderMob.level().getCurrentDifficultyAt(pathfinderMob.getOnPos()), MobSpawnType.EVENT, null);
            world.addFreshEntity(pathfinderMob);
            SpawnUnit();
            activeHordeMembers.add(pathfinderMob);
        }

    }

    protected PathfinderMob spawnBossMember(JsonMobData entity) {
        Optional<BlockPos> hordeSpawn = Optional.empty();

        Optional<EntityType<?>> type = EntityType.byString(entity.getMobID());
        EntityTypeHordeData<?> entrySelected = new EntityTypeHordeData(
                entity.getGoalPriority(),
                entity.getGoalMovementSpeed(),
                entity.getSpawnWeight(),
                type.get(),
                entity.getNbtData()
        );

        PathfinderMob pathfinderMob;

        try {
            pathfinderMob = entrySelected.createInstance(world);
        } catch (ClassCastException e) {
            this.Stop(HordeStopReasons.SPAWN_ERROR);
            Services.PLATFORM.getLOGGER().error("Villainous Horde Manager - WARNING! One or more of the mobs in your JSON horde are not a descendant of PathfinderMob. Horde canceled due to this.");
            return null;
        }


        int attempts = 0;
        while (hordeSpawn.isEmpty()) {
            hordeSpawn = this.getValidSpawn(entrySelected.getType());
            attempts++;
            if (hordeSpawn.isEmpty() && attempts >= 20) {
                return null; //Abort the spawning process after trying this much to free resources, try again on the next batch.
            }
        }


        if (pathfinderMob != null) {
            pathfinderMob.setPos(hordeSpawn.get().getX(), hordeSpawn.get().getY(), hordeSpawn.get().getZ());
            injectGoal(pathfinderMob, entrySelected, entrySelected.getGoalMovementSpeed());
            Services.PLATFORM.finalizeSpawn(pathfinderMob, world, pathfinderMob.level().getCurrentDifficultyAt(pathfinderMob.getOnPos()), MobSpawnType.EVENT, null);
            world.addFreshEntity(pathfinderMob);
            SpawnUnit();
            activeBossMembers.add(pathfinderMob);
            bossEntitiesSpawned.add(entity);
            return pathfinderMob;
        }
        return null;
    }

    /**
     *   Returns the center of the EntityTypeHorde.
     */
    public BlockPos getCenter() {
        return center;
    }

    /**
     *   Checks if a given entity is in the roster of monsters.
     */
    public boolean isHordeMember(LivingEntity entity) {
        return activeHordeMembers.contains(entity) || activeBossMembers.contains(entity);
    }

    /**
     *   Injects the horde movement and swarming goal into the entity.
     */
    public void injectGoal(PathfinderMob entity, EntityTypeHordeData<?> entityHordeData, double movementSpeedModifier) {
        GoalSelector mobGoalSelector = ((LivingGoalAccessor) entity).cartoonishHordeGetMobGoalSelector();
        mobGoalSelector.addGoal(entityHordeData.getGoalPriority(), new JsonHordeMovementGoal<>(entity, this, movementSpeedModifier));
    }

    /**
     * Removes the horde movement and swarming goal from the entity.
     */
    public static void removeGoal(PathfinderMob entity) {
        GoalSelector mobGoalSelector = ((LivingGoalAccessor) entity).cartoonishHordeGetMobGoalSelector();
        Set<WrappedGoal> prioritizedGoals = mobGoalSelector.getAvailableGoals();
        Goal toremove = null;
        for (WrappedGoal prioritizedGoal : prioritizedGoals) {
            if (prioritizedGoal.getGoal() instanceof TypeHordeMovementGoal) {
                toremove = prioritizedGoal.getGoal();
                break;
            }
        }
        if (toremove != null) {
            mobGoalSelector.removeGoal(toremove);
        }
    }

    /**
        Sets horde entity spawning data.
     */
    public void setHordeData(ArrayList<EntityTypeHordeData<?>> entityHordeData) {
        this.hordeData.clear();
        hordeData.addAll(entityHordeData);
    }

    private void awardAdvancement(Collection<ServerPlayer> playersToAward, String advancementToAward) {
        AdvancementHolder advancement = server.getAdvancements().get(ResourceLocation.parse(advancementToAward));
        for (ServerPlayer player : playersToAward) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for(String s : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, s);
                }
            }
        }
    }
}

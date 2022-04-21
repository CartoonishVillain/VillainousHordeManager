package com.villain.cartoonishhorde;


import com.villain.cartoonishhorde.mixin.AvailableGoalsAccessor;
import com.villain.cartoonishhorde.mixin.LivingGoalAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class Horde {
    protected ServerLevel world;
    protected BlockPos center;
    protected Boolean hordeActive = false;
    protected MinecraftServer server;
    protected int Alive = 0;
    protected int initAlive = 0;
    protected int Active = 0;
    protected int allowedActive = 0;
    protected int updateCenter = 0;
    protected ServerPlayer hordeAnchorPlayer;
    protected ArrayList<ServerPlayer> players = new ArrayList<>();
    protected ArrayList<LivingEntity> activeHordeMembers = new ArrayList<>();
    protected final ServerBossEvent bossInfo = new ServerBossEvent(new TextComponent("Horde"), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);
    protected ArrayList<EntityHordeData> hordeData = new ArrayList<>();

    public enum HordeStopReasons {
        VICTORY, //Players beat the event
        DEFEAT, //Players are defeated or quit the event.
        PEACEFUL, //Server changed to peaceful mid horde, event canceled.
        SPAWN_ERROR  //Players are in a position that causes the spawn manager to panic and shut down the event before it hangs the server.
    }

    /*
        Usage: Constructor for the Horde system.
        Parameter Details: a MinecraftServer instance helps the horde system keep track of players who should be involved in the horde process.
     */
    public Horde(MinecraftServer server) {
        this.server = server;
    }


    /*
        Usage: Clears out all data for a given horde and ends it.
        Additional Notes: You would want to override this to clean up any additional information you're tracking.
     */
    public void Stop(HordeStopReasons stopReason) {
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

        switch (stopReason) {
            case VICTORY -> {
                Constants.LOG.info("Player Victory against Horde");
            }
            case DEFEAT -> {
                Constants.LOG.info("Player Defeat against Horde");
            } case SPAWN_ERROR ->  {
                Constants.LOG.error("Horde canceled! Could not locate spawn placement! (Entities are too big, or terrain is too noisy)");
            }
            case PEACEFUL -> {
                Constants.LOG.info("Horde canceled, server changed to peaceful!");
            }
        }

    }

    /*
        Usage: Returns if the given instance of a horde is currently running.
     */
    public Boolean getHordeActive() {
        return hordeActive;
    }


    /*
        Usage: Initial phase. The Horde targets a specific player as it's anchor point (where horde members approach, and base their spawning off of)
        Parameter Details: A server player entity for the horde to track.
        Additional Notes: Now would be a good time to set up additional information to track if needed.

     */
    public void SetUpHorde(ServerPlayer serverPlayer) {
        world = serverPlayer.getLevel();

        if (serverPlayer.level.dimension().equals(world.dimension())) {
            hordeAnchorPlayer = serverPlayer;
            //Set alive counter based on difficulty
            switch (world.getDifficulty()) {
                case EASY:
                    setEasyDifficultyStats();
                    break;
                case NORMAL:
                    setNormalDifficultyStats();
                    break;
                case HARD:
                    setHardDifficultyStats();
                    break;
                case PEACEFUL:
                    return;
            }

            setActiveMemberCount();
            setCenterBlock(serverPlayer.blockPosition());
            hordeActive = true;
        }
    }

    /*
        Usage: Used in initial setup to set the maximum amount of entities that are allowed to spawn in an event at once.
        Additional Notes: Recommend you override to set up your own way of setting this up (whether by configs or other means)
     */
    public void setActiveMemberCount() {
        allowedActive = 15;
    }

    /*
        Usage: Used in initial setup to set how many entities players need to defeat before the horde ends on Easy.
        Additional Notes: Recommend you override to set up your own way of setting this up (whether by configs or other means)
     */
    public void setEasyDifficultyStats() {
        Alive = 10;
        initAlive = 10;
    }

    /*
        Usage: Used in initial setup to set how many entities players need to defeat before the horde ends on Normal.
        Additional Notes: Recommend you override to set up your own way of setting this up (whether by configs or other means)
    */
    public void setNormalDifficultyStats() {
        Alive = 25;
        initAlive = 25;
    }

    /*
        Usage: Used in initial setup to set how many entities players need to defeat before the horde ends on Hard.
        Additional Notes: Recommend you override to set up your own way of setting this up (whether by configs or other means)
    */
    public void setHardDifficultyStats() {
        Alive = 40;
        initAlive = 40;
    }

    /*
        Usage: Used to update the center block to control mob spawn positions and where mobs wander towards
        Parameter Details: A BlockPos to set the center variable to.
     */
    public void setCenterBlock(BlockPos centerPosition) {
        this.center = centerPosition;
    }

    /*
        Usage: Checks if a given player is still properly alive. This is to avoid desync issues.
        Parameter Details: ServerPlayer to check.
     */
    protected boolean checkIfPlayerIsStillValid(ServerPlayer serverPlayer) {
        return serverPlayer.getHealth() != 0.0f && !serverPlayer.isRemoved();
    }

    /*
        Usage: Checks during tick if peaceful difficulty is set. If it is, the horde is automatically ended.
     */
    protected void PeacefulCheck() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.Stop(HordeStopReasons.PEACEFUL);
        }
    }

    /*
        Usage: Automatically updates the horde center position
    */
    protected void updateCenter() {
        if (updateCenter == 0) {
            center = hordeAnchorPlayer.getOnPos();
            updateCenter = 100;
            updatePlayers();
            updateHorde();
        } else {
            updateCenter--;
        }
    }

    /*
        Usage: The tick event. The heart and soul of the horde. Patch your version of the tick event into the world tick to allow the horde to function when activated!
        Additional Notes: For additional or generally different functionality you can override this
     */
    //TODO: Convert each section of the tick to it's own method for more user modularity.
    public void tick() {
        if (hordeActive) {
            if (Alive > 0) {
                if (hordeAnchorPlayer.level.dimensionType().equals(world.dimensionType()) && checkIfPlayerIsStillValid(hordeAnchorPlayer)) {
                    boolean flag = this.hordeActive;

                    PeacefulCheck();
                    if(!hordeActive) return;

                    //Keeps Active counter updated
                    if (Active != activeHordeMembers.size()) {
                        Active = activeHordeMembers.size();
                    }

                    this.bossInfo.setVisible(true);


                    //If we have room to spawn more horde members, spawn more
                    if (Active < allowedActive) {
                        spawnHordeMember();
                    }

                    updateCenter();

                    float aliveDivision = ((float) Alive / initAlive);
                    this.bossInfo.setProgress(Mth.clamp(aliveDivision, 0.0f, 1f));

                } else {
                    //look for viable player, or cancel.
                    updatePlayers();
                    if (players.size() == 0) {
                        this.Stop(HordeStopReasons.DEFEAT);
                    } else {
                        bossInfo.removePlayer(hordeAnchorPlayer);
                        hordeAnchorPlayer = players.get(0);
                        players.remove(0);
                    }
                }
            } else {
                this.Stop(HordeStopReasons.VICTORY);
            }
        }
    }

    protected void updatePlayers() {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            if (this.hordeAnchorPlayer == serverPlayer) {
                bossInfo.addPlayer(serverPlayer);
                continue;
            }
            //player is not the tracked player and is in the same world as the tracked world.
            if (serverPlayer.level.dimensionType().equals(world.dimensionType()) && checkIfPlayerIsStillValid(serverPlayer)) {
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

    /*
        Usage: Takes stock of the status of horde members. Removes missing and dead members and updates tallies accordingly.
     */
    protected void updateHorde() {
        ArrayList<LivingEntity> removals = new ArrayList<>();
        ArrayList<LivingEntity> additions = new ArrayList<>();
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
                UnitLost();
            }


            //Temporarily? disabled.
//            inviteNearbySnowmentoHorde(hordeMember, additions);
        }
        for (LivingEntity removal : removals) {
            activeHordeMembers.remove(removal);
        }
        activeHordeMembers.addAll(additions);
        removals.clear();
    }

    /*
        Invites nearby compatible entity types to join
        Temporarily? disabled.
     */
//    protected void inviteNearbySnowmentoHorde(GenericHordeMember Member, ArrayList<GenericHordeMember> additions){
//        List<GenericHordeMember> list = Member.level.getEntitiesOfClass(GenericHordeMember.class, Member.getBoundingBox().inflate(8));
//        for(GenericHordeMember snowman : list){
//            if(Member.getLocTarget() != null) {
//                if (!snowman.isHordeMember() && snowman.getTarget() == null) {
//                    snowman.toggleHordeMember(Member.getLocTarget());
//                    additions.add(snowman);
//                    InviteUnit();
//                }
//            }
//        }
//    }

    /*
        Usage: Begins the search for a valid spawnpoint for horde members.
     */
    protected Optional<BlockPos> getValidSpawn(int var, EntityType type) {
        for (int i = 0; i < 3; ++i) {
            BlockPos blockPos = this.findRandomSpawnPos(5, type);
            if (blockPos != null) return Optional.of(blockPos);
        }
        return Optional.empty();
    }

    /*
        Usage: Finds the random spawn position for horde members
     */
    protected BlockPos findRandomSpawnPos(int loopvar, EntityType type) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int a = 0; a < loopvar; ++a) {
            double DISTANCE = -1;
            int j = Integer.MAX_VALUE, l = Integer.MAX_VALUE;
            while ((DISTANCE == -1 || !(DISTANCE > 450 && DISTANCE < 1250))) { //check for appropriate distance from start and proper biome
                j = randFinder(this.center.getX());
                l = randFinder(this.center.getZ());
                DISTANCE = center.distSqr(new BlockPos(j, center.getY(), l));
            }

//            int k = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, j, l);
            int k = findSafeYPosition(j, l, type, false);
            if (k != world.getMinBuildHeight() - 1) {
                blockPos.set(j, k, l);
                return blockPos;
            }
        }

        //if a safe spot isn't found after loopvar tries, run the unfiltered search.
        double DISTANCE = -1;
        int j = Integer.MAX_VALUE, l = Integer.MAX_VALUE;
        while ((DISTANCE == -1 || !(DISTANCE > 450 && DISTANCE < 1250))) { //check for appropriate distance from start and proper biome
            j = randFinder(this.center.getX());
            l = randFinder(this.center.getZ());
            DISTANCE = center.distSqr(new BlockPos(j, center.getY(), l));
        }

//            int k = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, j, l);
        int k = findSafeYPosition(j, l, type, true);
        if (k != world.getMinBuildHeight() - 1) {
            blockPos.set(j, k, l);
            return blockPos;
        }
        return null;
    }


    /*
        Usage: Finds the y spawnpoint for horde members.
     */
    protected int findSafeYPosition(int xValue, int zValue, EntityType entityType, boolean unfiltered) {
        int height = Mth.ceil(entityType.getDimensions().height);
        int width = Mth.ceil(entityType.getDimensions().width);
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
        BlockState blockState = null;
        boolean safe = true;
        for (int baseYValue = center.getY(); baseYValue < maxHeight; baseYValue++) {
            blockPos.set(xValue, baseYValue - 1, zValue);
            blockState = world.getBlockState(blockPos);
            if (!(blockState.canOcclude() && blockState.getFluidState().isEmpty() && !(blockState.getBlock() instanceof LeavesBlock) && !(blockState.equals(Blocks.BEDROCK.defaultBlockState())))) {
                //if there is no floor, don't bother.
                continue;
            }
            for (int i = xValue - width; i < xValue + width; i++) {
                for (int j = zValue - width; j < zValue + width; j++) {
                    for (int k = baseYValue; k < baseYValue + height; k++) {
                        blockPos.set(i, k, j );
                        blockState = world.getBlockState(blockPos);
                        if (!blockState.getFluidState().isEmpty() || blockState.canOcclude() || (blockState.getBlock() instanceof LeavesBlock)) {
                            safe = false;
                        }
                    }
                }
            }
            if (safe) return baseYValue+1;
        }

        //Look below the player if a valid spot above isn't found
        for (int baseYValue = center.getY(); baseYValue > minHeight; baseYValue--) {
            blockPos.set(xValue, baseYValue - 1, zValue);
            blockState = world.getBlockState(blockPos);
            if (!(blockState.canOcclude() && blockState.getFluidState().isEmpty() && !(blockState.getBlock() instanceof LeavesBlock) && !(blockState.equals(Blocks.BEDROCK.defaultBlockState())))) {
                //if there is no floor, don't bother.
                continue;
            }
            for (int i = xValue - width; i < xValue + width; i++) {
                for (int j = zValue - width; j < zValue + width; j++) {
                    for (int k = baseYValue; k < baseYValue + height; k++) {
                        blockPos.set(i, k, j);
                        blockState = world.getBlockState(blockPos);
                        if (!blockState.getFluidState().isEmpty() || blockState.canOcclude() || (blockState.getBlock() instanceof LeavesBlock)) {
                            safe = false;
                        }
                    }
                }
            }
            if (safe) return baseYValue+1;
        }

        return world.getMinBuildHeight() - 1;
    }

    protected int randFinder(int centercoord) {
        return centercoord + (this.world.random.nextInt(25 + 25) - 25);
    }

    //when a horde member spawns
    public void SpawnUnit() {
        Active++;
    }

    //when a horde member is invited after spawning from other means.
    public void InviteUnit() {
        Active++;
    }

    //when a horde member dies
    public void UnitDown() {
        Active--;
        Alive--;
    }

    //when a horde member loses range.
    public void UnitLost() {
        Active--;
    }

    /*
        Usage: Spawns horde entities.
     */
    protected void spawnHordeMember() {
        Optional<BlockPos> hordeSpawn = Optional.empty();
        ArrayList<Integer> SpawnWeights = new ArrayList<>();
        for (EntityHordeData hordeEntry : hordeData) {
            SpawnWeights.add(hordeEntry.getSpawnWeight());
        }
        int combined = 0;
        for (Integer weight : SpawnWeights) combined += weight;
        Random random = new Random();
        int rng = random.nextInt(combined);
        int selected = -1;
        int counter = 0;
        for (Integer weights : SpawnWeights) {
            if ((rng + 1 - weights) <= 0) {
                selected = counter;
                break;
            } else counter++;
            rng -= weights;
        }

        EntityHordeData entrySelected = hordeData.get(selected);
        PathfinderMob pathfinderMob = entrySelected.createInstance(world);

        int attempts = 0;
        while (hordeSpawn.isEmpty()) {
            hordeSpawn = this.getValidSpawn(2, entrySelected.getType());
            attempts++;
            if (hordeSpawn.isEmpty() && attempts >= 5) {
                this.Stop(HordeStopReasons.SPAWN_ERROR);
                return;
            }
        }


        if (pathfinderMob != null) {
            pathfinderMob.setPos(hordeSpawn.get().getX(), hordeSpawn.get().getY(), hordeSpawn.get().getZ());
            injectGoal(pathfinderMob, entrySelected, entrySelected.getGoalMovementSpeed());
            world.addFreshEntity(pathfinderMob);
            SpawnUnit();
            activeHordeMembers.add(pathfinderMob);
        }

    }

    /*
        Usage: Returns the center of the Horde.
     */
    public BlockPos getCenter() {
        return center;
    }

    /*
        Usage: Checks if a given entity is in the roster of monsters/
     */
    public boolean isHordeMember(LivingEntity entity) {
        return activeHordeMembers.contains(entity);
    }

    /*
        Usage: Injects the horde movement and swarming goal into the entity.
     */
    public void injectGoal(PathfinderMob entity, EntityHordeData entityHordeData, double movementSpeedModifier) {
        GoalSelector mobGoalSelector = ((LivingGoalAccessor) entity).cartoonishHordeGetMobGoalSelector();
        mobGoalSelector.addGoal(entityHordeData.getGoalPriority(), new HordeMovementGoal<>(entity, this, movementSpeedModifier));
    }

    /*
        Usage: removes the horde movement and swarming goal from the entity.
     */
    public static void removeGoal(PathfinderMob entity) {
        GoalSelector mobGoalSelector = ((LivingGoalAccessor) entity).cartoonishHordeGetMobGoalSelector();
        Set<WrappedGoal> prioritizedGoals = ((AvailableGoalsAccessor) mobGoalSelector).cartoonishHordeGetAvailableGoals();
        Goal toremove = null;
        if (prioritizedGoals != null) {
            for (WrappedGoal prioritizedGoal : prioritizedGoals) {
                if (prioritizedGoal.getGoal() instanceof HordeMovementGoal) {
                    toremove = prioritizedGoal.getGoal();
                    if (toremove != null) break;
                }
            }
            if (toremove != null) {
                mobGoalSelector.removeGoal(toremove);
            }
        }
    }

    /*
        Usage: Sets horde entity spawning data.
     */
    public void setHordeData(EntityHordeData... entityHordeData) {
        this.hordeData.clear();
        hordeData.addAll(List.of(entityHordeData));
    }

}

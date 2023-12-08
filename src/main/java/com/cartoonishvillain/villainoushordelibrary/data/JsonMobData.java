package com.cartoonishvillain.villainoushordelibrary.data;

import java.io.Serializable;

public class JsonMobData implements Serializable {
    String mobID;
    Integer spawnWeight;
    Integer goalPriority;
    Integer goalMovementSpeed;

    public String getMobID() {
        return mobID;
    }

    public void setMobID(String mobID) {
        this.mobID = mobID;
    }

    public Integer getSpawnWeight() {
        return spawnWeight;
    }

    public void setSpawnWeight(Integer spawnWeight) {
        this.spawnWeight = spawnWeight;
    }

    public Integer getGoalPriority() {
        return goalPriority;
    }

    public void setGoalPriority(Integer goalPriority) {
        this.goalPriority = goalPriority;
    }

    public Integer getGoalMovementSpeed() {
        return goalMovementSpeed;
    }

    public void setGoalMovementSpeed(Integer goalMovementSpeed) {
        this.goalMovementSpeed = goalMovementSpeed;
    }
}

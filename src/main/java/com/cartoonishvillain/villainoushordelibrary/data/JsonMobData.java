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

    public Integer getSpawnWeight() {
        return spawnWeight;
    }

    public Integer getGoalPriority() {
        return goalPriority;
    }

    public Integer getGoalMovementSpeed() {
        return goalMovementSpeed;
    }

}

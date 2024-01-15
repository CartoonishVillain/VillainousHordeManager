package com.cartoonishvillain.villainoushordelibrary.data;

import java.io.Serializable;

public class JsonMobData implements Serializable {
    String mobID;
    Integer spawnWeight;
    Integer goalPriority;
    Float goalMovementSpeed;

    public String getMobID() {
        return mobID;
    }

    public Integer getSpawnWeight() {
        return spawnWeight;
    }

    public Integer getGoalPriority() {
        return goalPriority;
    }

    public Float getGoalMovementSpeed() {
        return goalMovementSpeed;
    }

}

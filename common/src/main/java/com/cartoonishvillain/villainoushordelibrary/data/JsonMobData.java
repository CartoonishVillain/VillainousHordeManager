package com.cartoonishvillain.villainoushordelibrary.data;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonMobData implements Serializable {
    String mobID;
    Integer spawnWeight;
    Integer goalPriority;
    Float goalMovementSpeed;
    String nbtData;

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

    public String getNbtData() {
        return nbtData;
    }
}

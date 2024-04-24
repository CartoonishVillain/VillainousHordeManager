package com.cartoonishvillain.villainoushordelibrary.data;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonHordeData implements Serializable {
    String hordeName;
    Integer maximumActiveHordeMembers;
    Integer killsRequiredForEasy;
    Integer killsRequiredForNormal;
    Integer killsRequiredForHard;
    String bossInfoText;
    String bossInfoColor;
    Integer findSpawnAttempts;
    boolean despawnLeftBehindMembers;
    ArrayList<JsonMobData> mobData;

    public Integer getFindSpawnAttempts() {
        return findSpawnAttempts;
    }

    public String getHordeName() {
        return hordeName;
    }

    public Integer getMaximumActiveHordeMembers() {
        return maximumActiveHordeMembers;
    }

    public Integer getKillsRequiredForEasy() {
        return killsRequiredForEasy;
    }

    public Integer getKillsRequiredForNormal() {
        return killsRequiredForNormal;
    }

    public Integer getKillsRequiredForHard() {
        return killsRequiredForHard;
    }

    public String getBossInfoText() {
        return bossInfoText;
    }

    public String getBossInfoColor() {
        return bossInfoColor;
    }

    public boolean isDespawnLeftBehindMembers() {
        return despawnLeftBehindMembers;
    }

    public ArrayList<JsonMobData> getMobData() {
        return mobData;
    }

}

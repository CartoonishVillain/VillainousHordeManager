package com.cartoonishvillain.villainoushordemanager.data;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonWaveData implements Serializable {
    String waveName;
    Integer maximumActiveHordeMembers;
    Integer killsRequiredForEasy;
    Integer killsRequiredForNormal;
    Integer killsRequiredForHard;
    String bossInfoText;
    String bossInfoColor;
    boolean despawnLeftBehindMembers;
    ArrayList<JsonMobData> mobData;

    public String getWaveName() {
        return waveName;
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

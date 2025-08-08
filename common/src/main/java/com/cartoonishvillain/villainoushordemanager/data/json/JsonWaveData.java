package com.cartoonishvillain.villainoushordemanager.data.json;

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
    boolean keepSpawningEnemiesWhileBossIsActive;
    String bossInfoTextWhenBossIsActive;
    String bossInfoColorWhenBossIsActive;
    ArrayList<JsonMobData> bossMobData;
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

    public String getBossInfoTextWhenBossIsActive() {
        return bossInfoTextWhenBossIsActive;
    }

    public String getBossInfoColorWhenBossIsActive() {
        return bossInfoColorWhenBossIsActive;
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

    public boolean shouldKeepSpawningEnemiesWhileBossIsActive() {
        return keepSpawningEnemiesWhileBossIsActive;
    }

    public ArrayList<JsonMobData> getMobData() {
        return mobData;
    }

    public ArrayList<JsonMobData> getBossMobData() {
        return bossMobData;
    }
}

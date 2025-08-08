package com.cartoonishvillain.villainoushordemanager.data;

import com.cartoonishvillain.villainoushordemanager.data.json.JsonMobData;
import com.cartoonishvillain.villainoushordemanager.hordedata.EntityTypeHordeData;

import java.io.Serializable;
import java.util.ArrayList;

public class EntityTypeWaveData  {
    String waveName;
    Integer maximumActiveHordeMembers;
    Integer killsRequiredForEasy;
    Integer killsRequiredForNormal;
    Integer killsRequiredForHard;
    String bossInfoText;
    String bossInfoColor;
    boolean despawnLeftBehindMembers;
    ArrayList<EntityTypeHordeData<?>> mobData;

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

    public ArrayList<EntityTypeHordeData<?>> getMobData() {
        return mobData;
    }
}

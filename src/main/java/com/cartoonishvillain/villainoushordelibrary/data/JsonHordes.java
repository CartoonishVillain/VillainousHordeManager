package com.cartoonishvillain.villainoushordelibrary.data;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonHordes implements Serializable {
    String hordeName;
    Integer maximumActiveHordeMembers;
    Integer killsRequiredForEasy;
    Integer killsRequiredForNormal;
    Integer killsRequiredForHard;
    String bossInfoText;
    String bossInfoColor;
    boolean despawnLeftBehindMembers;
    ArrayList<JsonMobData> mobData;

    public String getHordeName() {
        return hordeName;
    }

    public void setHordeName(String hordeName) {
        this.hordeName = hordeName;
    }

    public Integer getMaximumActiveHordeMembers() {
        return maximumActiveHordeMembers;
    }

    public void setMaximumActiveHordeMembers(Integer maximumActiveHordeMembers) {
        this.maximumActiveHordeMembers = maximumActiveHordeMembers;
    }

    public Integer getKillsRequiredForEasy() {
        return killsRequiredForEasy;
    }

    public void setKillsRequiredForEasy(Integer killsRequiredForEasy) {
        this.killsRequiredForEasy = killsRequiredForEasy;
    }

    public Integer getKillsRequiredForNormal() {
        return killsRequiredForNormal;
    }

    public void setKillsRequiredForNormal(Integer killsRequiredForNormal) {
        this.killsRequiredForNormal = killsRequiredForNormal;
    }

    public Integer getKillsRequiredForHard() {
        return killsRequiredForHard;
    }

    public void setKillsRequiredForHard(Integer killsRequiredForHard) {
        this.killsRequiredForHard = killsRequiredForHard;
    }

    public String getBossInfoText() {
        return bossInfoText;
    }

    public void setBossInfoText(String bossInfoText) {
        this.bossInfoText = bossInfoText;
    }

    public String getBossInfoColor() {
        return bossInfoColor;
    }

    public void setBossInfoColor(String bossInfoColor) {
        this.bossInfoColor = bossInfoColor;
    }

    public boolean isDespawnLeftBehindMembers() {
        return despawnLeftBehindMembers;
    }

    public void setDespawnLeftBehindMembers(boolean despawnLeftBehindMembers) {
        this.despawnLeftBehindMembers = despawnLeftBehindMembers;
    }

    public ArrayList<JsonMobData> getMobData() {
        return mobData;
    }

    public void setMobData(ArrayList<JsonMobData> mobData) {
        this.mobData = mobData;
    }
}

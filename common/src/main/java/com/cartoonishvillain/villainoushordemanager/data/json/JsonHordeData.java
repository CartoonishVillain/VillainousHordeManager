package com.cartoonishvillain.villainoushordemanager.data.json;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonHordeData implements Serializable {
    String hordeName;
    String advancementForStartingHorde;
    String advancementForWinningAgainstHorde;
    boolean shouldClearWinningAdvancement;
    ArrayList<JsonWaveData> waves;

    public String getHordeName() {
        return hordeName;
    }

    public ArrayList<JsonWaveData> getWaves() {
        return waves;
    }

    public String getAdvancementForStartingHorde() {
        return advancementForStartingHorde;
    }

    public String getAdvancementForWinningAgainstHorde() {
        return advancementForWinningAgainstHorde;
    }

    public boolean shouldClearWinningAdvancement() {
        return shouldClearWinningAdvancement;
    }
}

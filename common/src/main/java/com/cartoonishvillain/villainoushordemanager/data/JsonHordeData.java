package com.cartoonishvillain.villainoushordemanager.data;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonHordeData implements Serializable {
    String hordeName;
    ArrayList<JsonWaveData> waves;

    public String getHordeName() {
        return hordeName;
    }

    public ArrayList<JsonWaveData> getWaves() {
        return waves;
    }
}

package com.cartoonishvillain.villainoushordelibrary.data;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonNBTData implements Serializable {
    String type;
    String key;
    String value;
    ArrayList<JsonEffectData> effectData;

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public ArrayList<JsonEffectData> getEffectData() {
        return effectData;
    }
}

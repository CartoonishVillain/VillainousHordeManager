package com.cartoonishvillain.villainoushordelibrary.data;

import java.io.Serializable;

public class JsonEffectData implements Serializable {
    String effect;
    String duration;
    String amplifier;
    boolean showParticles;

    public String getEffect() {
        return effect;
    }

    public String getDuration() {
        return duration;
    }

    public String getAmplifier() {
        return amplifier;
    }

    public boolean isShowParticles() {
        return showParticles;
    }
}

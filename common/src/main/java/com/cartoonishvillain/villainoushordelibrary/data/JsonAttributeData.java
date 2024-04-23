package com.cartoonishvillain.villainoushordelibrary.data;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonAttributeData implements Serializable {
    String attributeID;
    String modifierName;
    String modifierAmount;
    String modifierOperation;

    public String getAttributeID() {
        return attributeID;
    }

    public String getModifierName() {
        return modifierName;
    }

    public String getModifierAmount() {
        return modifierAmount;
    }

    public String getModifierOperation() {
        return modifierOperation;
    }
}

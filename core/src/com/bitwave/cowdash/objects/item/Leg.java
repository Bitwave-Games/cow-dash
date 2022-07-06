package com.bitwave.cowdash.objects.item;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public enum Leg {

    NONE(t("none"), "sprites/items/costumes/none.png", (byte) 0, true),
    WOODEN_LEG(t("wooden_leg"), "sprites/items/costumes/legs/wooden_leg.png", (byte) 1, isUnlocked((byte) 1)),
    CLOWNPANTS(t("clownpants"), "sprites/items/costumes/legs/clownpants.png", (byte) 2, isUnlocked((byte) 2)),
    JEANS(t("jeans"), "sprites/items/costumes/legs/jeans.png", (byte) 3, isUnlocked((byte) 3)),
    BROWN_JEANS(t("brown_jeans"), "sprites/items/costumes/body/brown_jeans.png", (byte) 4, isUnlocked((byte) 4)),
    RED_JEANS(t("red_jeans"), "sprites/items/costumes/body/red_jeans.png", (byte) 5, isUnlocked((byte) 5)),
    WINDUP_PANTS(t("windup_pants"), "sprites/items/costumes/legs/windup_pants.png", (byte) 6, isUnlocked((byte) 6));

    public static final byte AMOUNT = (byte) values().length;

    private final byte id;
    private final boolean unlocked;
    private final String name;
    private final Pixmap pixMap;

    Leg(String name, String path, byte id, boolean unlocked) {
        this.id = id;
        this.unlocked = unlocked;
        this.name = name;
        this.pixMap = (Pixmap) Assets.getInstance().get(path);
    }

    public static Leg getValue(int item_no) {
        for (Leg leg : values()) {
            if (leg.id == item_no) {
                return leg;
            }
        }
        return null;
    }

    public static Leg getValue(String name) {
        for (Leg legs : values()) {
            if (legs.name.equalsIgnoreCase(name)) {
                return legs;
            }
        }
        return null;
    }

    private static boolean isUnlocked(byte id) {
        return CowPreferences.getInstance().isItemUnlocked(ItemUtils.LEG, id);
    }

    private static String t(String key) {
        return Translator.getInstance().t(key);
    }

    public Pixmap getPixmap() {
        return pixMap;
    }

    @Override
    public String toString() {
        return name;
    }

    public byte getId() {
        return id;
    }

    public boolean unlocked() {
        return this.unlocked;
    }

    public Array<Leg> getUnlockedItems() {
        Array<Leg> unlockedItems = new Array<Leg>();
        for (Leg leg : values()) {
            if (leg.unlocked) {
                unlockedItems.add(leg);
            }
        }
        return unlockedItems;
    }
}

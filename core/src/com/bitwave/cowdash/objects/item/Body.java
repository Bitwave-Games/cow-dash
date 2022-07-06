package com.bitwave.cowdash.objects.item;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public enum Body {

    NONE(t("none"), "sprites/items/costumes/none.png", (byte) 0, true),
    ASTRO_SUIT(t("astro_suit"), "sprites/items/costumes/body/astrosuit.png", (byte) 1, isUnlocked((byte) 1)),
    NINJA_OUTFIT(t("ninja_outfit"), "sprites/items/costumes/body/ninjaoutfit.png", (byte) 2, isUnlocked((byte) 2)),
    BANE_JACKET(t("bane_jacket"), "sprites/items/costumes/body/banejacket.png", (byte) 3, isUnlocked((byte) 3)),
    TANKTOP_COLORIZED(t("tanktop_colorized"), "sprites/items/costumes/body/tanktop_colorized.png", (byte) 4, isUnlocked((byte) 4)),
    BALLET_DRESS(t("ballet_dress"), "sprites/items/costumes/body/balletdress.png", (byte) 5, isUnlocked((byte) 5)),
    CYBORG(t("cyborg"), "sprites/items/costumes/body/cyborg.png", (byte) 6, isUnlocked((byte) 6)),
    BLACK_VEST(t("black_vest"), "sprites/items/costumes/body/black_vest.png", (byte) 7, isUnlocked((byte) 7)),
    WINDUP_ARMOR(t("windup_armor"), "sprites/items/costumes/body/windup_armor.png", (byte) 8, isUnlocked((byte) 8));

    public static final byte AMOUNT = (byte) values().length;

    private final byte id;
    private final boolean unlocked;
    private final String name;
    private final Pixmap pixMap;

    Body(String name, String path, byte id, boolean unlocked) {
        this.id = id;
        this.unlocked = unlocked;
        this.name = name;
        this.pixMap = (Pixmap) Assets.getInstance().get(path);
    }

    public static Body getValue(int item_no) {
        for (Body body : values()) {
            if (body.id == item_no) {
                return body;
            }
        }
        return null;
    }

    public static Body getValue(String name) {
        for (Body body : values()) {
            if (body.name.equalsIgnoreCase(name)) {
                return body;
            }
        }
        return null;
    }

    private static boolean isUnlocked(byte id) {
        return CowPreferences.getInstance().isItemUnlocked(ItemUtils.BODY, id);
    }

    private static String t(String key) {
        return Translator.getInstance().t(key);
    }

    public Pixmap getPixmap() {
        return pixMap;
    }

    public byte getId() {
        return id;
    }

    public boolean unlocked() {
        return this.unlocked;
    }

    @Override
    public String toString() {
        return name;
    }

    public Array<Body> getUnlockedItems() {
        Array<Body> unlockedItems = new Array<Body>();
        for (Body body : values()) {
            if (body.unlocked) {
                unlockedItems.add(body);
            }
        }
        return unlockedItems;
    }
}

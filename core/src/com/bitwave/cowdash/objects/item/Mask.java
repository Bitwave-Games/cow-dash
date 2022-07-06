package com.bitwave.cowdash.objects.item;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;


public enum Mask {

    NONE(t("none"), "sprites/items/costumes/none.png", (byte) 0, true),
    CLOWN_NOSE(t("clown_nose"), "sprites/items/costumes/mask/clown_nose.png", (byte) 1, isUnlocked((byte) 1)),
    EYEPATCH(t("eye_patch"), "sprites/items/costumes/mask/eypatch.png", (byte) 2, isUnlocked((byte) 2)),
    MONOCULAR(t("monocular"), "sprites/items/costumes/mask/monocular.png", (byte) 3, isUnlocked((byte) 3)),
    NOSE_RING(t("nose_ring"), "sprites/items/costumes/mask/nosering.png", (byte) 4, isUnlocked((byte) 4)),
    PAPERBAG(t("paper_bag"), "sprites/items/costumes/mask/paperbag.png", (byte) 5, isUnlocked((byte) 5)),
    SUNGLASSES(t("sunglasses"), "sprites/items/costumes/mask/sunglasses.png", (byte) 6, isUnlocked((byte) 6)),
    TIKI_MASK(t("tiki_mask"), "sprites/items/costumes/mask/tikimask.png", (byte) 7, isUnlocked((byte) 7)),
    CENSORED(t("censored"), "sprites/items/costumes/mask/censored.png", (byte) 8, isUnlocked((byte) 8)),
    BANE_MASK(t("bane_mask"), "sprites/items/costumes/mask/bane_mask.png", (byte) 9, isUnlocked((byte) 9)),
    CHRISTOPHER_GLASSES(t("christopher_glasses"), "sprites/items/costumes/mask/christopherglasses.png", (byte) 10, isUnlocked((byte) 10)),
    RAGNAR_MASK(t("ragnar_mask"), "sprites/items/costumes/mask/ragnarmask.png", (byte) 11, isUnlocked((byte) 11)),
    TONY_MASK(t("tony_mask"), "sprites/items/costumes/mask/tonymask.png", (byte) 12, isUnlocked((byte) 12)),
    CUCUMBER_EYES(t("cucumber_eyes"), "sprites/items/costumes/mask/cucumber_eyes.png", (byte) 13, isUnlocked((byte) 13)),
    MAKEUP(t("makeup"), "sprites/items/costumes/mask/makeup.png", (byte) 14, isUnlocked((byte) 14)),
    NOSE(t("potato_nose"), "sprites/items/costumes/mask/nose.png", (byte) 15, isUnlocked((byte) 15)),
    DEMON_EYES(t("demon_eyes"), "sprites/items/costumes/mask/demoneyes2.png", (byte) 16, isUnlocked((byte) 16));

    public static final byte AMOUNT = (byte) values().length;

    private final byte id;
    private final boolean unlocked;
    private final String name;
    private final Pixmap pixmap;

    Mask(String name, String path, byte id, boolean unlocked) {
        this.id = id;
        this.unlocked = unlocked;
        this.name = name;
        this.pixmap = (Pixmap) Assets.getInstance().get(path);
    }

    public static Mask getValue(int item_no) {
        for (Mask mask : values()) {
            if (mask.id == item_no) {
                return mask;
            }
        }
        return null;
    }

    public static Mask getValue(String name) {
        for (Mask mask : values()) {
            if (mask.name.equalsIgnoreCase(name)) {
                return mask;
            }
        }
        return null;
    }

    private static boolean isUnlocked(byte id) {
        return CowPreferences.getInstance().isItemUnlocked(ItemUtils.MASK, id);
    }

    private static String t(String key) {
        return Translator.getInstance().t(key);
    }

    public byte getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    public Pixmap getPixmap() {
        return pixmap;
    }

    public boolean unlocked() {
        return this.unlocked;
    }

    public Array<Mask> getUnlockedItems() {
        Array<Mask> unlockedItems = new Array<Mask>();
        for (Mask mask : values()) {
            if (mask.unlocked) {
                unlockedItems.add(mask);
            }
        }
        return unlockedItems;
    }
}

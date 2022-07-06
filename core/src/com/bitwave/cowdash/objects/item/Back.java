package com.bitwave.cowdash.objects.item;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public enum Back {

    NONE(t("none"), "sprites/items/costumes/none.png", (byte) 0, true),
    CAPE(t("cape"), "sprites/items/costumes/back/cape.png", (byte) 1, isUnlocked((byte) 1)),
    ANGEL_WINGS(t("angel_wings"), "sprites/items/costumes/back/angelwings.png", (byte) 2, isUnlocked((byte) 2)),
    WINDUP_SPRING(t("windup_spring"), "sprites/items/costumes/back/windup_spring.png", (byte) 3, isUnlocked((byte) 3)),
    TURTLE_SHELL(t("turtle_shell"), "sprites/items/costumes/back/turtle_shell.png", (byte) 4, isUnlocked((byte) 4)),
    SWIMGRING(t("swimring"), "sprites/items/costumes/back/swimring.png", (byte) 5, isUnlocked((byte) 5)),
    PROPELLER(t("propeller"), "sprites/items/costumes/back/propeller.png", (byte) 6, isUnlocked((byte) 6)),
    HANGGLIDER(t("hangglider"), "sprites/items/costumes/back/hangglider.png", (byte) 7, isUnlocked((byte) 7)),
    BOOMBOX(t("boombox"), "sprites/items/costumes/back/boombox.png", (byte) 8, isUnlocked((byte) 8)),
    SADDLE(t("saddle"), "sprites/items/costumes/back/saddle.png", (byte) 9, isUnlocked((byte) 9));

    public static final byte AMOUNT = (byte) values().length;

    private byte id;
    private boolean unlocked;
    private final String name;
    private final Pixmap pixMap;

    Back(String name, String path, byte id, boolean unlocked) {
        this.id = id;
        this.unlocked = unlocked;
        this.name = name;
        this.pixMap = (Pixmap) Assets.getInstance().get(path);
    }

    Back(String name, String path) {
        this.name = name;
        this.pixMap = (Pixmap) Assets.getInstance().get(path);
    }

    public static Back getValue(int item_no) {
        for (Back back : values()) {
            if (back.id == item_no) {
                return back;
            }
        }
        return null;
    }

    public static Back getValue(String name) {
        for (Back back : values()) {
            if (back.name.equalsIgnoreCase(name)) {
                return back;
            }
        }
        return null;
    }

    private static boolean isUnlocked(byte id) {
        return CowPreferences.getInstance().isItemUnlocked(ItemUtils.BACK, id);
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

    public Array<Back> getUnlockedItems() {
        Array<Back> unlockedItems = new Array<Back>();
        for (Back back : values()) {
            if (back.unlocked) {
                unlockedItems.add(back);
            }
        }
        return unlockedItems;
    }


}

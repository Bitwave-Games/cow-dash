package com.bitwave.cowdash.objects.item;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public enum Head {

    NONE(t("none"), "sprites/items/costumes/none.png", (byte) 0, true),
    ASTRO_HELMET(t("astro_helmet"), "sprites/items/costumes/head/astrohelmet.png", (byte) 1, isUnlocked((byte) 1)),
    BANDANA(t("bandana"), "sprites/items/costumes/head/bandana.png", (byte) 2, isUnlocked((byte) 2)),
    BUNNY_EARS(t("bunny_ears"), "sprites/items/costumes/head/bunnyears.png", (byte) 3, isUnlocked((byte) 3)),
    CLOWN_WIG(t("clown_wig"), "sprites/items/costumes/head/clownwig.png", (byte) 4, isUnlocked((byte) 4)),
    COWBOY_HAT(t("cowboy_hat"), "sprites/items/costumes/head/cowboyhat.png", (byte) 5, isUnlocked((byte) 5)),
    HELMET(t("helmet"), "sprites/items/costumes/head/helmet.png", (byte) 6, isUnlocked((byte) 6)),
    KYLE_HAT(t("kyle_hat"), "sprites/items/costumes/head/kylehat.png", (byte) 7, isUnlocked((byte) 7)),
    MAGIC_HAT(t("magic_hat"), "sprites/items/costumes/head/magichat.png", (byte) 8, isUnlocked((byte) 8)),
    MOHAWK(t("mohawk"), "sprites/items/costumes/head/mohawk.png", (byte) 9, isUnlocked((byte) 9)),
    NEWSPAPER(t("newspaper"), "sprites/items/costumes/head/newspaper.png", (byte) 10, isUnlocked((byte) 10)),
    NINJA_HEADBAND(t("ninja_headband"), "sprites/items/costumes/head/ninjaheadband.png", (byte) 11, isUnlocked((byte) 11)),
    PEACH_HAT(t("peach_hat"), "sprites/items/costumes/head/peachhat.png", (byte) 12, isUnlocked((byte) 12)),
    PIMP_HAT(t("pimp_hat"), "sprites/items/costumes/head/pimphat.png", (byte) 13, isUnlocked((byte) 13)),
    PIRATE_HAT(t("pirate_hat"), "sprites/items/costumes/head/piratehat.png", (byte) 14, isUnlocked((byte) 14)),
    RADAR(t("radar"), "sprites/items/costumes/head/radar.png", (byte) 15, isUnlocked((byte) 15)),
    RASTA_HAT(t("rasta_hat"), "sprites/items/costumes/head/rasta_hat.png", (byte) 16, isUnlocked((byte) 16)),
    SANTA_HAT(t("santa_hat"), "sprites/items/costumes/head/santahat.png", (byte) 17, isUnlocked((byte) 17)),
    STAN_HAT(t("stan_hat"), "sprites/items/costumes/head/stanhat.png", (byte) 18, isUnlocked((byte) 18)),
    FEATHER_HAR(t("feather_hat"), "sprites/items/costumes/head/feather_hat.png", (byte) 19, isUnlocked((byte) 19)),
    CHRISTOPHER_HAT(t("christopher_hat"), "sprites/items/costumes/head/christopherhat.png", (byte) 20, isUnlocked((byte) 20)),
    DONNA_WIG(t("donna_wig"), "sprites/items/costumes/head/donnawig.png", (byte) 21, isUnlocked((byte) 21)),
    ELVIS_WIG(t("elvis_wig"), "sprites/items/costumes/head/elviswig.png", (byte) 22, isUnlocked((byte) 22)),
    NAM_HELMET(t("nam_helmet"), "sprites/items/costumes/head/namhelmet.png", (byte) 23, isUnlocked((byte) 23)),
    RAGNAR_HAT(t("ragnar_hat"), "sprites/items/costumes/head/ragnarhat.png", (byte) 24, isUnlocked((byte) 24)),
    RIBBON(t("ribbon"), "sprites/items/costumes/head/ribbon.png", (byte) 25, isUnlocked((byte) 25)),
    SOMBRERO(t("sombrero"), "sprites/items/costumes/head/sombrero.png", (byte) 26, isUnlocked((byte) 26)),
    BATMASK(t("batmask"), "sprites/items/costumes/head/batmanmask.png", (byte) 27, isUnlocked((byte) 27)),
    HASSELHOF(t("hasselhof"), "sprites/items/costumes/head/hasselhoff_wig.png", (byte) 28, isUnlocked((byte) 28)),
    BIGHORN(t("bighorn"), "sprites/items/costumes/head/bighorns.png", (byte) 29, isUnlocked((byte) 29)),
    WINDUP_HELMET(t("windup_helmet"), "sprites/items/costumes/head/windup_helmet.png", (byte) 30, isUnlocked((byte) 30));

    public static final byte AMOUNT = (byte) values().length;

    private final byte id;
    private final boolean unlocked;
    private final String name;
    private final Pixmap pixMap;

    Head(String name, String path, byte id, boolean unlocked) {
        this.id = id;
        this.unlocked = unlocked;
        this.name = name;
        this.pixMap = (Pixmap) Assets.getInstance().get(path);
    }

    public static Head getValue(int item_no) {
        for (Head head : values()) {
            if (head.id == item_no) {
                return head;
            }
        }
        return null;
    }

    public static Head getValue(String name) {
        for (Head head : values()) {
            if (head.name.equalsIgnoreCase(name)) {
                return head;
            }
        }
        return null;
    }

    private static boolean isUnlocked(byte id) {
        return CowPreferences.getInstance().isItemUnlocked(ItemUtils.HEAD, id);
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

    public Array<Head> getUnlockedItems() {
        Array<Head> unlockedItems = new Array<Head>();
        for (Head head : values()) {
            if (head.unlocked) {
                unlockedItems.add(head);
            }
        }
        return unlockedItems;
    }
}

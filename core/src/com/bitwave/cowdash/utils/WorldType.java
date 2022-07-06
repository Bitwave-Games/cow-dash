package com.bitwave.cowdash.utils;

import com.bitwave.cowdash.utils.persistance.CowPreferences;

public enum WorldType {

    GRASS("grass-world", "world_select_gras_button", "sprites/in/level_select_Screen_1.png", "grass_fg.png", "grass_bg.png", 0),
    BEACH("beach-world", "world_select_beach_button", "sprites/in/world_select_screen_beach.png", "beach_fg.png", "beach_bg.png", 40),
    GRASS_PREMIUM("grass-world-premium", "world_select_gras_button_2", "sprites/in/world_select_scren_long.png", "grass_fg.png", "grass_bg.png", 20);

    private final String DEFAULT_BG = "sprites/backgrounds/grass-world/grass_bg.png";
    private final String DEFAULT_FG = "sprites/backgrounds/grass-world/grass_fg.png";

    private final String IMAGE_PATH = "sprites/backgrounds/";

    private final String displayName;
    private final String nameOfWorldSelectButton;
    private final String nameOfForegroundImage;
    private final String nameOfBackgroundImage;
    private final String nameOfLevelSelectImage;
    private boolean isPremiumWorld;
    private final int amountOfMedalsRequiredToUnlock;

    WorldType(String displayName, String nameOfWorldSelectButton, String nameOfLevelSelectImage, String nameOfForegroundImage, String nameOfBackgroundImage, int amountOfMedalsRequiredToUnlock) {
        this.displayName = displayName;
        this.nameOfLevelSelectImage = nameOfLevelSelectImage;

        this.nameOfWorldSelectButton = nameOfWorldSelectButton;

        if (nameOfForegroundImage != null && nameOfBackgroundImage != null) {
            String nameOfWorld = displayName;
            if (isPremiumWorld) {
                String[] base = displayName.split("-");
                nameOfWorld = base[0] + "-" + base[1];
            }
            this.nameOfForegroundImage = IMAGE_PATH + nameOfWorld + "/" + nameOfForegroundImage;
            this.nameOfBackgroundImage = IMAGE_PATH + nameOfWorld + "/" + nameOfBackgroundImage;
        } else {
            this.nameOfForegroundImage = DEFAULT_BG;
            this.nameOfBackgroundImage = DEFAULT_FG;
        }
        this.amountOfMedalsRequiredToUnlock = amountOfMedalsRequiredToUnlock;
    }

    public static WorldType getValue(String name) {
        for (WorldType worldType : values()) {
            if (worldType.displayName.equalsIgnoreCase(name)) {
                return worldType;
            }
        }
        return GRASS;
    }

    public String getNameOfWorldSelectButton() {
        if (!CowPreferences.getInstance().isWorldUnlocked(this) && this.getAmountOfMedalsRequiredToUnlock() > 0) {
            if (this.getDisplayName() == "beach-world") {
                return "world_select_beach_premium_locked_button";
            }

            if (this.getDisplayName() == "grass-world-premium") {
                return "world_select_grass_premium_locked_button";
            }

        }

        return this.nameOfWorldSelectButton;

    }

    public String getNameOfTrackForWorld() {
        if (displayName.contains("premium")) {
            return displayName.substring(0, displayName.lastIndexOf('-'));
        }
        return displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPathToBackgroundPNG() {
        return nameOfBackgroundImage;
    }

    public String getPathToForegroundPng() {
        return nameOfForegroundImage;
    }

    public String getNameOfLevelSelectImage() {
        return nameOfLevelSelectImage;
    }

    public int getAmountOfMedalsRequiredToUnlock() {
        return amountOfMedalsRequiredToUnlock;
    }


}

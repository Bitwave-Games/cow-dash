package com.bitwave.cowdash.utils.persistance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bitwave.cowdash.CowDash;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.external.LevelCompleteListener;

public class CowPreferences {
    private static final String UNLOCKED = "unlocked";
    private static final String CURRENT_LEVEL = "current-level";
    private static final String CURRENT_WORLD = "current-world";
    private static final String TIME_MEDAL = "time-medal";
    private static final String CHEST_MEDAL = "chest-medal";
    private static final String VEGGIE_MEDAL = "veggie-medal";
    private static final String SFX_OFF = "sfx-off";
    private static final String MUSIC_OFF = "music-off";
    private static final String VIBRATION_OFF = "vibration-off";
    private static final String PARTICLES_OFF = "particles-off";
    private static final int LEVELS_PER_WORLD = 20;

    private static CowPreferences cowPreferences;
    private final Preferences preferences;

    private CowPreferences() {
        this.preferences = Gdx.app.getPreferences("CowDash");
        unlockWorld(WorldType.GRASS);
        if (getCurrentWorld() == null) {
            setCurrentWorld(WorldType.GRASS);
        }
    }

    public static CowPreferences getInstance() {
        if (cowPreferences == null) {
            cowPreferences = new CowPreferences();
        }
        return cowPreferences;
    }

    public void unlockLevel(byte levelIndex, WorldType worldType) {
        if (!isLevelUnlocked(levelIndex, worldType)) {
            preferences.putBoolean(getLevelUnlockedIdentifier(levelIndex, worldType), true);

            CowDash cow = (CowDash) Gdx.app.getApplicationListener();
            LevelCompleteListener levelCompleteListener = cow.getLevelCompleteListener();
            if (levelCompleteListener != null) {
                levelCompleteListener.levelCompleteEventLogging(levelIndex, worldType.getDisplayName());
            }

            preferences.flush();
        }
    }

    public boolean isLevelUnlocked(byte levelIndex, WorldType worldType) {
        return preferences.getBoolean(getLevelUnlockedIdentifier(levelIndex, worldType));
    }

    public void updateUnlockedWorlds() {
        for (WorldType worldType : WorldType.values()) {
            if (!isWorldUnlocked(worldType)) {
                int amountOfMedalsCollected = getTotalAmountOfCollectedMedals();
                int requiredAmountToUnlock = worldType.getAmountOfMedalsRequiredToUnlock();

                int amountOfMedalsRequired = getTotalAmountOfRequiredMedals(worldType);
                if (amountOfMedalsRequired <= 0) {
                    unlockWorld(worldType);
                }

                if (amountOfMedalsCollected >= requiredAmountToUnlock && requiredAmountToUnlock != 0) {
                    unlockWorld(worldType);
                }
            }
        }
    }

    public void unlockItem(String itemType, byte itemIndex) {
        preferences.putBoolean(getItemUnlockedIdentifier(itemType, itemIndex), true);
        preferences.putBoolean(getItemTypeUnlockedIdentifier(itemType), true);
        preferences.flush();
    }

    public boolean isItemTypeUnlocked(String itemType) {
        return preferences.getBoolean(getItemTypeUnlockedIdentifier(itemType), false);
    }

    public boolean isItemUnlocked(String itemType, byte itemIndex) {
        return preferences.getBoolean(getItemUnlockedIdentifier(itemType, itemIndex));
    }

    public int getTotalAmountOfCollectedMedals() {
        int amountOfMedals = 0;
        for (WorldType worldType : WorldType.values()) {
            amountOfMedals += getUnlockedMedalsForWorld(worldType);
        }
        return amountOfMedals;
    }

    public int getTotalAmountOfRequiredMedals(WorldType world) {
        return world.getAmountOfMedalsRequiredToUnlock();
    }

    public void setTimeMedalAcquired(byte levelIndex, WorldType worldType) {
        preferences.putBoolean(getLevelTimeMedalIdentifier(levelIndex, worldType), true);
        preferences.flush();
    }

    public void setChestMedalAcquired(byte levelIndex, WorldType worldType) {
        preferences.putBoolean(getLevelChestMedalIdentifier(levelIndex, worldType), true);
        preferences.flush();
    }

    public void setVeggieMedalAcquired(byte levelIndex, WorldType worldType) {
        preferences.putBoolean(getLevelVeggieMedalIdentifier(levelIndex, worldType), true);
        preferences.flush();
    }

    public boolean getTimeMedalAcquired(byte levelIndex, WorldType worldType) {
        return preferences.getBoolean(getLevelTimeMedalIdentifier(levelIndex, worldType), false);
    }

    public boolean getChestMedalAcquired(byte levelIndex, WorldType worldType) {
        return preferences.getBoolean(getLevelChestMedalIdentifier(levelIndex, worldType), false);
    }

    public boolean getVeggieMedalAcquired(byte levelIndex, WorldType worldType) {
        return preferences.getBoolean(getLevelVeggieMedalIdentifier(levelIndex, worldType), false);
    }

    public void toggleSoundFX(boolean mute) {
        preferences.putBoolean(SFX_OFF, mute);
        preferences.flush();
    }

    public boolean isSoundFXMute() {
        return preferences.getBoolean(SFX_OFF);
    }

    public void toggleMusic(boolean mute) {
        preferences.putBoolean(MUSIC_OFF, mute);
        preferences.flush();
    }

    public boolean isMusicMute() {
        return preferences.getBoolean(MUSIC_OFF);
    }

    public void toggleParticles(boolean disable) {
        preferences.putBoolean(PARTICLES_OFF, disable);
        preferences.flush();
    }

    public boolean isParticlesEnabled() {
        return !preferences.getBoolean(PARTICLES_OFF, false);
    }

    public void toggleVibration(boolean disable) {
        preferences.putBoolean(VIBRATION_OFF, disable);
        preferences.flush();
    }

    public boolean isVibrationDisabled() {
        return preferences.getBoolean(VIBRATION_OFF, true);
    }

    public void saveClothing(String itemType, byte itemIndex) {
        preferences.putInteger(itemType, itemIndex);
        preferences.flush();
    }

    public byte getSavedClothing(String itemType) {
        return (byte) preferences.getInteger(itemType);
    }

    public byte getUnlockedMedalsForWorld(WorldType worldType) {
        byte amount = 0;
        for (byte i = 0; i < LEVELS_PER_WORLD; i++) {
            if (getVeggieMedalAcquired(i, worldType)) {
                amount++;
            }
            if (getChestMedalAcquired(i, worldType)) {
                amount++;
            }
            if (getTimeMedalAcquired(i, worldType)) {
                amount++;
            }
        }
        return amount;
    }

    public void unlockWorld(WorldType worldType) {
        preferences.putBoolean(getWorldUnlockedIdentifier(worldType), true);
        preferences.flush();
    }

    public String getCurrentWorld() {
        return preferences.getString(CURRENT_WORLD, null);
    }

    public void setCurrentWorld(WorldType worldType) {
        preferences.putString(CURRENT_WORLD, worldType.getDisplayName());
        preferences.flush();
    }

    public boolean isWorldUnlocked(WorldType worldType) {
        if (CowDash.ALL_LEVELS_UNLOCKED) {
            return true;
        } else {
            return preferences.getBoolean(getWorldUnlockedIdentifier(worldType));
        }
    }

    public byte getTotalAmountOfMedalsForWorld() {
        return (byte) 60;
    }

    public byte getCurrentLevelSelection(WorldType worldType) {
        return (byte) preferences.getInteger(getCurrentLevelIdentifier(worldType));
    }

    public void saveCurrentLevelIndex(byte currentLevelIndex, WorldType worldType) {
        preferences.putInteger(getCurrentLevelIdentifier(worldType), currentLevelIndex);
        preferences.flush();
    }

    public boolean isEverythingUnlockedForLevel(byte levelIndex, WorldType worldType) {
        if (!getChestMedalAcquired(levelIndex, worldType)) {
            return false;
        }
        if (!getTimeMedalAcquired(levelIndex, worldType)) {
            return false;
        }
        return getVeggieMedalAcquired(levelIndex, worldType);
    }

    public void clearPreferences() {
        preferences.clear();
        preferences.flush();

        unlockWorld(WorldType.GRASS);
        setCurrentWorld(WorldType.GRASS);
    }

    private String getLevelUnlockedIdentifier(byte levelIndex, WorldType worldType) {
        return String.format("[%s-%d]-%s", worldType.getDisplayName(), levelIndex, UNLOCKED);
    }

    private String getWorldUnlockedIdentifier(WorldType worldType) {
        return String.format("[%s]-%s", worldType.getDisplayName(), UNLOCKED);
    }

    private String getLevelChestMedalIdentifier(byte levelIndex, WorldType worldType) {
        return String.format("[%s-%d]-%s", worldType.getDisplayName(), levelIndex, CHEST_MEDAL);
    }

    private String getLevelTimeMedalIdentifier(byte levelIndex, WorldType worldType) {
        return String.format("[%s-%d]-%s", worldType.getDisplayName(), levelIndex, TIME_MEDAL);
    }

    private String getLevelVeggieMedalIdentifier(byte levelIndex, WorldType worldType) {
        return String.format("[%s-%d]-%s", worldType.getDisplayName(), levelIndex, VEGGIE_MEDAL);
    }

    private String getItemUnlockedIdentifier(String itemType, byte itemIndex) {
        return String.format("[%s-%d]-%s", itemType, itemIndex, UNLOCKED);
    }

    private String getItemTypeUnlockedIdentifier(String itemType) {
        return String.format("[%s]-%s", itemType, UNLOCKED);
    }

    private String getCurrentLevelIdentifier(WorldType worldType) {
        return String.format("[%s]-%s", worldType.getDisplayName(), CURRENT_LEVEL);
    }

}
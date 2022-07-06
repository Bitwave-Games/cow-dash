package com.bitwave.cowdash.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

import java.util.HashMap;
import java.util.Map;


public class AudioUtils {

    private static AudioUtils audioUtils;
    private final float VOLUME_MUTE = 0.00f;
    private final float VOLUME_MAX = 1.00f;
    private final Map<String, Sound> soundEffects;
    private final Map<String, Music> tracks;

    private AudioUtils() {
        this.tracks = getTracks();
        this.soundEffects = getSoundEffects();
    }

    public static AudioUtils getInstance() {
        if (audioUtils == null) {
            audioUtils = new AudioUtils();
        }
        return audioUtils;
    }

    public void playSoundFX(String name) {
        if (!CowPreferences.getInstance().isSoundFXMute()) {
            soundEffects.get(name).play();
        }
    }

    public void resetVolume() {
        for (Music music : tracks.values()) {
            music.setVolume(VOLUME_MAX);
        }
    }

    public void crossFadeTracks(final String nameOfTrackToFadeOut, final String nameOfTrackToFadeIn, final float duration, final boolean shouldStopOnceFadingDone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                float timer = 0.0f;
                tracks.get(nameOfTrackToFadeOut).setVolume(VOLUME_MUTE);
                tracks.get(nameOfTrackToFadeIn).setVolume(VOLUME_MAX);
                playMusic(nameOfTrackToFadeIn, true);
                while (timer <= duration) {
                    try {
                        timer += Gdx.graphics.getDeltaTime();
                        tracks.get(nameOfTrackToFadeOut).setVolume(Math.abs(VOLUME_MAX - ((VOLUME_MAX / duration) * timer)));
                        tracks.get(nameOfTrackToFadeIn).setVolume(Math.abs((VOLUME_MAX / duration) * timer));
                        Thread.sleep(4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (shouldStopOnceFadingDone) {
                    tracks.get(nameOfTrackToFadeOut).stop();
                }
            }
        }).start();
    }

    public void fadeOutTrack(final String nameOfTrackToFadeOut, final float duration, final boolean shouldStopOnceFadingDone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                float timer = 0.0f;
                while (timer <= duration) {
                    try {
                        timer += Gdx.graphics.getDeltaTime();
                        tracks.get(nameOfTrackToFadeOut).setVolume(Math.abs(VOLUME_MAX - ((VOLUME_MAX / duration) * timer)));
                        Thread.sleep(4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (shouldStopOnceFadingDone) {
                    tracks.get(nameOfTrackToFadeOut).stop();
                }
            }
        }).start();
    }

    public void switchTrack(String trackToStop, String trackToPlay) {
        stopTrack(trackToStop);
        playMusic(trackToPlay, true);
    }

    public void playMusic(String name) {
        if (!CowPreferences.getInstance().isMusicMute()) {
            tracks.get(name).play();
        }
    }

    public void playMusic(String name, boolean loop) {
        tracks.get(name).setVolume(VOLUME_MAX);
        tracks.get(name).setLooping(loop);
        playMusic(name);
    }

    public void playMusic(String name, boolean loop, boolean isAlreadyPlaying) {
        if (isAlreadyPlaying) {
            playMusic(name, loop);
        }
    }

    public void stopAllMusic() {
        for (Music music : tracks.values()) {
            music.stop();
        }
    }

    public void stopTrack(String trackName) {
        tracks.get(trackName).stop();
    }

    private Map<String, Music> getTracks() {
        HashMap<String, Music> tracks = new HashMap<String, Music>();
        tracks.put("grass-world", (Music) Assets.getInstance().get("sound/music/grass.mp3"));
        tracks.put("beach-world", (Music) Assets.getInstance().get("sound/music/beach.mp3"));
        tracks.put("ghost-world", (Music) Assets.getInstance().get("sound/music/ghost.mp3"));
        tracks.put("logo", (Music) Assets.getInstance().get("sound/music/intro.mp3"));
        tracks.put("menu", (Music) Assets.getInstance().get("sound/music/menu.mp3"));
        tracks.put("boss", (Music) Assets.getInstance().get("sound/music/boss.mp3"));
        tracks.put("unlocked", (Music) Assets.getInstance().get("sound/music/bonush crash no moo.mp3"));
        return tracks;
    }

    private Map<String, Sound> getSoundEffects() {
        HashMap<String, Sound> soundEffects = new HashMap<String, Sound>();
        soundEffects.put("pig_trampoline", (Sound) Assets.getInstance().get("sound/effects/pig_high_jump.mp3"));
        soundEffects.put("button", (Sound) Assets.getInstance().get("sound/effects/button.mp3"));
        soundEffects.put("mooh", (Sound) Assets.getInstance().get("sound/effects/cow-1.mp3"));
        soundEffects.put("start-running", (Sound) Assets.getInstance().get("sound/effects/go.mp3"));
        soundEffects.put("veggie", (Sound) Assets.getInstance().get("sound/effects/pickup.mp3"));
        soundEffects.put("pig", (Sound) Assets.getInstance().get("sound/effects/pig_jump.mp3"));
        soundEffects.put("medal", (Sound) Assets.getInstance().get("sound/effects/opendoor.mp3"));
        soundEffects.put("cowdie", (Sound) Assets.getInstance().get("sound/effects/cowdie.mp3"));
        soundEffects.put("getkey", (Sound) Assets.getInstance().get("sound/effects/getkey.mp3"));
        soundEffects.put("opendoor", (Sound) Assets.getInstance().get("sound/effects/medal.mp3"));
        soundEffects.put("randomVeggie", (Sound) Assets.getInstance().get("sound/effects/EatMunch.mp3"));
        soundEffects.put("openOneWayDoor", (Sound) Assets.getInstance().get("sound/effects/FlipTile.mp3"));
        soundEffects.put("closeOneWayDoor", (Sound) Assets.getInstance().get("sound/effects/FlipTileEnd.mp3"));
        soundEffects.put("goalReached", (Sound) Assets.getInstance().get("sound/effects/WeaponBonus.mp3"));
        soundEffects.put("medalsHitBoard", (Sound) Assets.getInstance().get("sound/effects/BonusCrystal.mp3"));
        soundEffects.put("bouncewall", (Sound) Assets.getInstance().get("sound/effects/whip.mp3"));
        soundEffects.put("walljump", (Sound) Assets.getInstance().get("sound/effects/walljump.mp3"));
        soundEffects.put("teleport", (Sound) Assets.getInstance().get("sound/effects/teleport.mp3"));
        return soundEffects;
    }

}
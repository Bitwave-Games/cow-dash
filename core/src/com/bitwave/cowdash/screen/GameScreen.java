package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.bitwave.cowdash.level.BeachLevel;
import com.bitwave.cowdash.level.GrassLevel;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.screen.ingame.Dialog;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.persistance.CowPreferences;


public class GameScreen extends BaseScreen {

    public final byte FINAL_LEVEL = 19;
    public String REAL_LEVELS_PATH;
    private byte deathCounter;
    private byte playedLevels;
    private byte levelIndex = 0;
    private State state = State.PLAYING;
    private Level level;
    private WorldType worldType;

    public GameScreen(Game game, byte levelIndex) {
        super(game);
        this.initVariables();
        this.levelIndex = levelIndex;
        AudioUtils.getInstance().playMusic(nameOfTrackToPlay, true);
        setupLevel();
    }

    public void initVariables() {
        CowPreferences cowPreferences = CowPreferences.getInstance();
        this.worldType = WorldType.getValue(cowPreferences.getCurrentWorld());
        this.REAL_LEVELS_PATH = "levels/" + worldType.getDisplayName().toLowerCase() + "/level-";
    }

    public Game getGame() {
        return this.game;
    }

    public byte getLevelIndex() {
        return levelIndex;
    }

    public InputProcessor getBackInputProcessor() {
        return this.getCustomizedInput(BackButtonAlternatives.PAUSE);
    }

    public boolean isFinalLevel() {
        return levelIndex >= FINAL_LEVEL;
    }

    public void nextLevel() {
        state = State.SWITCHING_LEVEL;
        levelIndex++;
    }

    public void incrementDeathCounter() {
        deathCounter++;
    }

    public byte getDeathCounter() {
        return deathCounter;
    }

    public void resetDeathCounter() {
        deathCounter = 0;
    }

    public void replayLevel() {
        state = State.REPLAY;
    }

    public void clearLevel(ScreenType screenToGoTO) {
        switch (screenToGoTO) {
            case MY_COW:
                state = State.RETURN_TO_MYCOW;
                break;
            case LEVEL_SELECT:
                state = State.RETURN_TO_LEVEL_SELECT;
                break;
            case CREDITS:
                state = State.RETURN_TO_CREDITS;
            default:
                break;
        }
    }

    @Override
    public void render(float delta) {
        if (delta > 0.1f) {
            delta = 0.1f;
        }

        switch (state) {
            case REPLAY:
            case SWITCHING_LEVEL:
                this.switchLevel();
                break;
        }

        if (state == State.PLAYING) {
            level.tick(delta);
        }
        level.render(batch, camera);

        switch (state) {
            case RETURN_TO_LEVEL_SELECT:
                handleAudioTransitions();
                game.setScreen(new LoadingScreen(game, ScreenType.LEVEL_SELECT));
                break;
            case RETURN_TO_MYCOW:
                handleAudioTransitions();
                game.setScreen(new LoadingScreen(game, ScreenType.MY_COW));
                break;
            case RETURN_TO_CREDITS:
                game.setScreen(new LoadingScreen(game, ScreenType.CREDITS));
                break;
        }
    }

    private void handleAudioTransitions() {
        if (level.isItemUnlocked()) {
            AudioUtils.getInstance().stopTrack(nameOfTrackToPlay);
        } else {
            AudioUtils.getInstance().fadeOutTrack(nameOfTrackToPlay, 10f, true);
        }
    }

    @Override
    public void resize(int width, int height) {
        Vector2 size = Scaling.fill.apply(width, height, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        camera.setToOrtho(false, size.x, size.y);
        camera.update();
    }

    @Override
    public void pause() {
        super.pause();
        if (Dialog.screenTapped) {
            Dialog.isPaused = true;
        }
    }

    @Override
    public void resume() {
        super.resume();

        if (Dialog.screenTapped) {
            Dialog.isPaused = true;
        }
    }

    @Override
    public void hide() {
        level.dispose();
        Assets.getInstance().unloadTileMap(worldType, levelIndex);
    }

    private void switchLevel() {
        level.dispose();

        byte indexOfLevelToUnload = 0;
        switch (state) {
            case REPLAY:
                indexOfLevelToUnload = levelIndex;
                break;
            case SWITCHING_LEVEL:
                indexOfLevelToUnload = (byte) (levelIndex - 1);
            default:
                break;
        }

        Assets.getInstance().unloadTileMap(worldType, indexOfLevelToUnload);
        setupLevel();
    }

    private void setupLevel() {

        Assets.getInstance().loadTileMap(worldType, levelIndex);
        switch (worldType) {
            case GRASS:
            case GRASS_PREMIUM:
                level = new GrassLevel(REAL_LEVELS_PATH + levelIndex + ".tmx", this, worldType);
                break;
            case BEACH:
                level = new BeachLevel(REAL_LEVELS_PATH + levelIndex + ".tmx", this);
            default:
                break;
        }
        state = State.PLAYING;
    }

    private enum State {
        PLAYING,
        RETURN_TO_LEVEL_SELECT,
        RETURN_TO_MYCOW,
        REPLAY,
        SWITCHING_LEVEL,
        RETURN_TO_CREDITS
    }


}

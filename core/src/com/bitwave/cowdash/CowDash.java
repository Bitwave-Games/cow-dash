package com.bitwave.cowdash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bitwave.cowdash.screen.GameScreen;
import com.bitwave.cowdash.screen.LoadingScreen;
import com.bitwave.cowdash.screen.ScreenType;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.external.LevelCompleteListener;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

import java.util.Date;


public class CowDash extends Game {

    public static final boolean ALL_COSTUMES_UNLOCKED = false;
    public static final boolean FREE_FLYING_MODE = false;
    public static final boolean SHOULD_RENDER_BOUNDING_BOXES = false;
    public static final boolean ALL_LEVELS_UNLOCKED = false;
    public static final boolean GOD_MODE = false;
    public static final boolean DEVELOPER_MODE = false;

    private LevelCompleteListener levelCompleteListener;

    @Override
    public void create() {
        if (DEVELOPER_MODE) {
            CowPreferences.getInstance().setCurrentWorld(WorldType.GRASS_PREMIUM);
            setScreen(new GameScreen(this, (byte) 0));
        } else {
            setScreen(new LoadingScreen(this, ScreenType.SPLASH_SCREEN));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        ParticleHelper.getInstance().dispose();
        Assets.getInstance().clear();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();

        // You take screenshots by pressing S on the keyboard
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
            Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
            BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
            Date d = new Date();
            PixmapIO.writePNG(Gdx.files.external("com.bitwave.cowdash-" + d.getTime() + ".png"), pixmap);
            pixmap.dispose();
        }

        //Pressing F will change fullscreen to windowed
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            if(Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(1280,720);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }

    }

    public LevelCompleteListener getLevelCompleteListener() {
        return levelCompleteListener;
    }

    public void setLevelCompleteListener(LevelCompleteListener levelCompleteListener) {
        this.levelCompleteListener = levelCompleteListener;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

}

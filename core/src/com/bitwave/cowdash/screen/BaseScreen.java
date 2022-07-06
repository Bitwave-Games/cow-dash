package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public abstract class BaseScreen implements Screen, Disposable {
    public static final short VIEWPORT_WIDTH = 400;
    public static final short VIEWPORT_HEIGHT = 240;

    protected OrthographicCamera camera;
    protected SpriteBatch batch;
    protected Game game;

    protected InputProcessor backButtonProcessor;

    protected String nameOfTrackToPlay;

    public BaseScreen(Game game) {
        this.game = game;
        this.init();
    }

    protected BaseScreen() {
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }

    public SpriteBatch getSpriteBatch() {
        return this.batch;
    }

    public void init() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2, 0);
        camera.update();
        if (Gdx.app.getType() == ApplicationType.Desktop && Assets.getInstance().isLoaded("sprites/ui/disgusting_arrow.png")) {
        }
        this.nameOfTrackToPlay = WorldType.getValue(CowPreferences.getInstance().getCurrentWorld()).getNameOfTrackForWorld();
    }

    @Override
    public void resize(int width, int height) {
        float fixedHeight = 240f;
        Vector2 size = Scaling.fillY.apply(width, height, width, fixedHeight);

        camera.setToOrtho(false, size.x, size.y);
        camera.update();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        Assets.getInstance().finishLoading();
    }

    protected InputProcessor getCustomizedInput(final BackButtonAlternatives alternatives) {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                return true;
            }
        };
    }

}

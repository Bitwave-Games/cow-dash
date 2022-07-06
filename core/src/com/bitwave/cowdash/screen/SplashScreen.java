package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;


public class SplashScreen extends MenuBaseScreen {

    Animation playerAnimation;
    float stateTime;
    SpriteBatch spriteBatch;

    public SplashScreen(Game game) {

        super(game, BackButtonAlternatives.QUIT);
        Texture[] logoSplash = new Texture[300];
        for (int i = 0; i < 299; i++) {
            if (i < 10) {
                logoSplash[i] = (Texture) Assets.getInstance().get("splash/bitwave-splash-000" + i + ".png");
                logoSplash[i].setFilter(TextureFilter.Linear, TextureFilter.Linear);
                continue;
            }
            if (i < 100) {
                logoSplash[i] = (Texture) Assets.getInstance().get("splash/bitwave-splash-00" + i + ".png");
                logoSplash[i].setFilter(TextureFilter.Linear, TextureFilter.Linear);
                continue;
            }
            logoSplash[i] = (Texture) Assets.getInstance().get("splash/bitwave-splash-0" + i + ".png");
            logoSplash[i].setFilter(TextureFilter.Linear, TextureFilter.Linear);
            continue;
        }
        playerAnimation = new Animation(0.01667f, logoSplash);


        spriteBatch = new SpriteBatch();

        AudioUtils.getInstance().playMusic("logo");
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateTime += Gdx.graphics.getDeltaTime();
        Texture currentFrame = (Texture) playerAnimation.getKeyFrame(stateTime);
        if (currentFrame == null) {
            game.setScreen(new MainMenuScreen(game, true));
            return;
        }
        spriteBatch.begin();
        float ratio = (float) stage.getViewport().getScreenHeight() / (float) currentFrame.getHeight();
        float newHeight = (float) stage.getViewport().getScreenHeight();
        float newWidth = (float) currentFrame.getWidth() * ratio;
        float centerX = (float) stage.getViewport().getScreenWidth() / 2 - newWidth / 2;
        spriteBatch.draw(currentFrame, centerX, 0, newWidth, newHeight);
        spriteBatch.end();
        tick(delta);
    }

    @Override
    public void tick(float delta) {
        float moddeltatime = Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f);
        if (Gdx.input.isTouched()) {
            game.setScreen(new MainMenuScreen(game, true));
        }
    }

    @Override
    public void hide() {
        super.hide();
        AudioUtils.getInstance().stopTrack("logo");
    }

}
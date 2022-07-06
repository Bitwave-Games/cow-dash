package com.bitwave.cowdash.level;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bitwave.cowdash.screen.ScreenType;


interface LevelBase {

    void switchLevel();

    void restart();

    void end(ScreenType screenToGoTo);

    void tick(float deltatime);

    void render(SpriteBatch batch, OrthographicCamera camera);
}
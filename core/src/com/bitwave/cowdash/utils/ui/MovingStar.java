package com.bitwave.cowdash.utils.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.bitwave.cowdash.screen.BaseScreen;

public class MovingStar extends AnimatedActor {

    private final float speed;
    private final float worldWidth;
    private final float frameHeight;

    public MovingStar(TextureRegion region, float ANIM_SPEED, float xPos, float yPos, int frameWidth, int frameHeight, float speed) {
        super(region, ANIM_SPEED, xPos, yPos, frameWidth, frameHeight, true);
        this.speed = speed;
        this.frameHeight = frameHeight;
        this.worldWidth = BaseScreen.VIEWPORT_WIDTH;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setX(getX() - (speed * delta));
        if (getX() < 0) {
            setPosition(MathUtils.random(worldWidth, worldWidth * 1.5f), newRandomPos());
        }
    }

    private float newRandomPos() {
        return MathUtils.random(0, BaseScreen.VIEWPORT_HEIGHT - frameHeight);
    }

}

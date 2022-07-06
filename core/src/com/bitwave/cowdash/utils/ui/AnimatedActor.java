package com.bitwave.cowdash.utils.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bitwave.cowdash.utils.TextureUtils;

public class AnimatedActor extends Image {

    private final Animation animation;
    private TextureRegion currentFrame;
    private float stateTime;

    public AnimatedActor(TextureRegion region, final float ANIM_SPEED, int frameWidth, int frameHeight, boolean visible) {
        super(region);
        this.animation = getFramesFromRegion(region, ANIM_SPEED, frameWidth, frameHeight);
        this.currentFrame = (TextureRegion) animation.getKeyFrame(stateTime);
        this.setWidth(frameWidth);
        this.setHeight(frameHeight);
        this.setVisible(visible);
    }

    public AnimatedActor(TextureRegion region, final float ANIM_SPEED, float xPos, float yPos, int frameWidth, int frameHeight, boolean visible) {
        this(region, ANIM_SPEED, frameWidth, frameHeight, visible);
        this.setPosition(xPos, yPos);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        currentFrame = (TextureRegion) animation.getKeyFrame(stateTime, true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1, 1, 1, 1);
        batch.draw(currentFrame, this.getX(), this.getY(), getOriginX(), getOriginY(), this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    private Animation getFramesFromRegion(TextureRegion spriteSheet, float animSpeed, int width, int height) {
        TextureRegion[] region = TextureUtils.singleSplit(spriteSheet, width, height, false);
        return new Animation(animSpeed, region);
    }

}
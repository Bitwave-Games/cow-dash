package com.bitwave.cowdash.utils.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bitwave.cowdash.utils.Assets;

public class ScrollingImage extends Image {

    private final Sprite scrollSprite;
    private Color color;
    private float speed;
    private boolean shouldMove = true;

    public ScrollingImage(String path, float speed, float worldWidth) {
        this.speed = speed;
        this.scrollSprite = new Sprite(getScrollableImage(path));
        this.scrollSprite.setSize(worldWidth, scrollSprite.getHeight());
    }

    public ScrollingImage(String path, float speed, float u, float u2, float worldWidth) {
        this(path, speed, worldWidth);
        this.scrollSprite.setU(u);
        this.scrollSprite.setU2(u2);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getTimer() {
        return scrollSprite.getOriginX();
    }

    public void setShouldMove(boolean shouldMove) {
        this.shouldMove = shouldMove;
    }

    @Override
    public void act(float delta) {
        updateParallaxLayer(delta, speed);
    }

    public float getU() {
        return scrollSprite.getU();
    }

    public float getU2() {
        return scrollSprite.getU2();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(this.color == null ? Color.WHITE : color);
        batch.draw(scrollSprite, scrollSprite.getX(), scrollSprite.getY(), scrollSprite.getWidth(), scrollSprite.getHeight());
        batch.setColor(Color.WHITE);
    }

    public void setSpriteColor(Color color) {
        this.color = color;
    }

    @Override
    public void setPosition(float x, float y) {
        scrollSprite.setPosition(x, y);
    }

    private Texture getScrollableImage(String path) {
        Texture texture = (Texture) Assets.getInstance().get(path);
        texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        return texture;
    }

    public void setWorldWidth(float worldWidth) {
        scrollSprite.setSize(worldWidth, scrollSprite.getHeight());
    }

    @Override
    public float getPrefHeight() {
        if (scrollSprite != null) return scrollSprite.getHeight();
        return 0;
    }

    @Override
    public float getPrefWidth() {
        if (scrollSprite != null) return scrollSprite.getWidth();
        return 0;
    }

    private void updateParallaxLayer(float deltaTime, float speed) {
        scrollSprite.scroll(shouldMove ? speed * deltaTime : 1, 0);
    }

    public void scroll(float xAmount, float yAmount) {
        scrollSprite.scroll(xAmount, yAmount);
    }
}

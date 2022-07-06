package com.bitwave.cowdash.utils.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;


public class WrappedImage extends Image {

    private final Texture texture;

    public WrappedImage(Texture texture) {
        this.texture = texture;
        this.texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), 0, 0, (int) (getStage().getWidth() * 1.5f), texture.getHeight());
    }

}

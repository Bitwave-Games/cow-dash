package com.bitwave.cowdash.objects.scenery;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.objects.GameObject;
import com.bitwave.cowdash.objects.ObjectType;
import com.bitwave.cowdash.objects.Player;

public abstract class Scenery extends GameObject {

    protected static final String PATH = "sprites/objects/scenery/";
    protected Animation animation;

    public Scenery(Vector2 position, float width, float height, Level level, Animation animation) {
        super(position, level, width, height, ObjectType.SCENERY);
        this.animation = animation;
        this.currentFrame = (TextureRegion) animation.getKeyFrame(0);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y);
    }

    @Override
    public void tick(float deltaTime, Player player) {
        animate(deltaTime);
    }

    @Override
    protected void animate(float deltaTime) {
        super.animate(deltaTime);
        currentFrame = (TextureRegion) animation.getKeyFrame(stateTime, true);
    }

}
package com.bitwave.cowdash.objects.enemy;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.objects.GameObject;
import com.bitwave.cowdash.objects.ObjectType;
import com.bitwave.cowdash.objects.Player;

public abstract class Enemy extends GameObject implements Disposable {

    protected Sprite enemySprite;
    protected Vector2 newPosition;

    protected boolean isRemoveable = true;

    protected float tempvelocity;

    protected Rectangle topBounds;

    protected boolean isBoundToPlatform;

    protected boolean isAllowedToPerformHighJump;

    public Enemy(Vector2 position, Level level, float w, float h, float right, float bottom, float left, float top, ObjectType objectType) {
        super(position, level, w, h, right, bottom, left, top, objectType);
    }

    public Enemy(Vector2 position, String startDirection, float w, float h, float right, float bottom,
                 float left, float top, boolean isBoundToPlatform, Level level, float speed, ObjectType objectType) {
        super(position, level, w, h, right, bottom, left, top, objectType);
        this.speed = speed;
        this.isBoundToPlatform = isBoundToPlatform;
        this.velocity.set(speed, gravity);
        if (startDirection != null) {
            if (startDirection.equalsIgnoreCase("right")) {
                direction = DIRECTION_RIGHT;
            } else {
                direction = DIRECTION_LEFT;
            }
        } else {
            direction = DIRECTION_RIGHT;
        }
    }

    public void tick(float deltaTime, Player player, Array<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy != this && enemy.objectType.equals(ObjectType.ENEMY_BLOCK)) {
                if (bounds.overlaps(enemy.getBounds())) {
                    if (this.objectType.equals(ObjectType.ENEMY_JUMPING)) {
                        if (gravity != -JUMP) {
                            gravity = -JUMP;
                        }
                    } else {
                        enemy.switchDirection();
                        this.switchDirection();
                    }
                }
            }
        }
    }

    public Rectangle getTopBounds() {
        return topBounds;
    }

    public boolean isBoundToPlatform() {
        return isBoundToPlatform;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(enemySprite, position.x, position.y);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void tick(float deltaTime, Player player) {
    }

}
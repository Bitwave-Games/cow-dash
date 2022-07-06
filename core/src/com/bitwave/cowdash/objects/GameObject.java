package com.bitwave.cowdash.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.level.Level.Filter;
import com.bitwave.cowdash.utils.TextureUtils;


public abstract class GameObject implements GameObjectBase {

    protected static final float GAMESPEED = 1f;
    protected static final float JUMP = 216.0f * GAMESPEED;
    public final byte DIRECTION_LEFT = 0;
    public final byte DIRECTION_RIGHT = 1;
    public final byte DIRECTION_NONE = 2;
    protected final float TERMINAL_VELOCITY = 600.0f * GAMESPEED;
    protected final float ACCELERATION = 360.0f * GAMESPEED;
    public Rectangle movingBounds = new Rectangle();
    protected Vector2 position;
    protected Vector2 velocity;
    protected Rectangle bounds;
    protected TextureRegion currentFrame;
    protected Level level;
    protected ObjectType objectType;
    protected boolean isJumping;
    protected boolean isCollidedWith;
    protected boolean down;
    protected boolean onGround;
    protected float gravity;
    protected byte direction = DIRECTION_NONE;
    protected float top, left, bottom, right;
    protected float speed;
    protected float stateTime;


    public GameObject(Vector2 position, Level level, ObjectType objectType) {
        this.level = level;
        this.position = position;
        this.objectType = objectType;
        this.bounds = new Rectangle();
        this.velocity = new Vector2();
    }

    public GameObject(Vector2 position, Level level, float width, float height, ObjectType objectType) {
        this(position, level, objectType);
        this.bounds = new Rectangle(position.x, position.y, width, height);
        this.velocity = new Vector2();
    }

    public GameObject(Vector2 position, Level level, float width, float height, float right, float bottom, float left, float top, ObjectType objectType) {
        this(position, level, width, height, objectType);
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.top = top;
        this.bounds = new Rectangle(position.x + left, position.y + top, width - (left + right), height - (bottom + top));
        this.velocity = new Vector2();
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public boolean isCollidedWith() {
        return isCollidedWith;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Vector2 mul(Vector2 v) {
        return v;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        this.bounds.setPosition(position.x + left, position.y + top);
    }

    public boolean isFalling() {
        return !onGround && gravity >= 0f;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
        this.bounds.setPosition(position.x + left, position.y + top);
    }

    public float getHeight() {
        return bounds.height;
    }

    public float getWidth() {
        return this.bounds.width;
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    public void switchDirection() {
        direction = direction == DIRECTION_RIGHT ? DIRECTION_LEFT : DIRECTION_RIGHT;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isMoving() {
        return direction != DIRECTION_NONE;
    }

    public boolean isVerticalCollision(float colliderY, float collisionObjectY) {
        return colliderY < collisionObjectY;
    }

    public boolean isHeadingRight() {
        return direction == DIRECTION_RIGHT;
    }

    public boolean isHeadingLeft() {
        return direction == DIRECTION_LEFT;
    }

    public byte getDirection() {
        return direction;
    }

    protected boolean tryMove(Vector2 newPosition) {
        onGround = false;

        if (newPosition.y < position.y) {
            handleVerticalCollisionFromAbove(newPosition);
        } else {
            setPosition(position.x, newPosition.y);
        }

        handleVerticalCollisionFromBelow(newPosition);
        handleHorizontalCollision(newPosition);
        return false;
    }

    protected void handleVerticalCollisionFromAbove(Vector2 newPosition) {
        Rectangle tmpBounds = new Rectangle(bounds);
        tmpBounds.y = newPosition.y;
        tmpBounds.height = (newPosition.y - bounds.y) * -1;
        Rectangle downBounds = level.getBoundsAt(tmpBounds, (byte) (Filter.HARD | Filter.SOFT | Filter.DOOR | Filter.SAND), this);
        if (downBounds != null) {
            onGround = true;
            Vector2 groundedPosition = new Vector2(position.x, downBounds.y + downBounds.height);
            setPosition(groundedPosition);
        } else {
            onGround = false;
            setPosition(position.x, newPosition.y);
        }
    }

    protected void handleVerticalCollisionFromBelow(Vector2 newPosition) {
        Rectangle headBounds = new Rectangle(bounds);
        headBounds.y += headBounds.height;
        headBounds.height = 1;
        Rectangle topBounds = level.getBoundsAt(headBounds, (byte) (Filter.HARD | Filter.DOOR | Filter.SAND), this);
        if (topBounds != null) {
            isJumping = false;
            gravity *= -.1f;
            setPosition(position.x, topBounds.y - bounds.height - 1f);
        }
    }

    protected void handleHorizontalCollision(Vector2 newPosition) {
        Rectangle tmpHorzBounds = new Rectangle(bounds);
        tmpHorzBounds.x = newPosition.x + left;
        Rectangle horzBounds = level.getBoundsAt(tmpHorzBounds, (byte) (Filter.HARD | Filter.DOOR | Filter.SAND), this);
        if (horzBounds != null) {
            if (onGround) {
                switchDirection();
            }
            setPosition(position.x, position.y);
        } else {
            setPosition(newPosition.x, position.y);
        }
    }

    protected Animation getFramesFromRegion(TextureRegion spriteSheet, float animSpeed) {
        TextureRegion[] region = TextureUtils.singleSplit(spriteSheet, 32, 32, false);
        return new Animation(animSpeed, region);
    }

    protected Animation getFramesFromRegion(TextureRegion spriteSheet, float animSpeed, boolean flipX) {
        TextureRegion[] region = TextureUtils.singleSplit(spriteSheet, 32, 32, flipX);
        return new Animation(animSpeed, region);
    }

    protected Animation getFramesFromRegion(TextureRegion spriteSheet, float animSpeed, int frameWidth, int frameHeight) {
        TextureRegion[] region = TextureUtils.singleSplit(spriteSheet, frameWidth, frameHeight, false);
        return new Animation(animSpeed, region);
    }

    protected Animation getFramesFromRegion(TextureRegion spriteSheet, float animSpeed, int frameWidth, int frameHeight, boolean flipX) {
        TextureRegion[] region = TextureUtils.singleSplit(spriteSheet, frameWidth, frameHeight, flipX);
        return new Animation(animSpeed, region);
    }

    protected Animation getFramesFromRegion(Texture texture, float animSpeed, boolean flipX, int... positions) {
        TextureRegion[] region = new TextureRegion[positions.length];
        for (int i = 0; i < positions.length; i++) {
            region[i] = new TextureRegion(texture, positions[i] * 32, 0, 32, 32);
            region[i].flip(flipX, false);
        }
        return new Animation(animSpeed, region);
    }

    protected Animation getFramesFromRegion(Texture texture, float animSpeed, int frameWidth, int frameHeight, boolean flipX, int... positions) {
        TextureRegion[] region = new TextureRegion[positions.length];
        for (int i = 0; i < positions.length; i++) {
            region[i] = new TextureRegion(texture, positions[i] * frameWidth, 0, frameWidth, frameHeight);
            region[i].flip(flipX, false);
        }
        return new Animation(animSpeed, region);
    }

    protected void animate(float deltaTime) {
        stateTime += deltaTime;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

}

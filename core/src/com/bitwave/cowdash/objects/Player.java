package com.bitwave.cowdash.objects;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.CowDash;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.level.Level.Filter;
import com.bitwave.cowdash.objects.item.Back;
import com.bitwave.cowdash.objects.item.Body;
import com.bitwave.cowdash.objects.item.Head;
import com.bitwave.cowdash.objects.item.Leg;
import com.bitwave.cowdash.objects.item.Mask;
import com.bitwave.cowdash.screen.ingame.Dialog;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.TextureUtils;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public class Player extends GameObject implements Disposable {

    public static final byte MAX_HEALTH = 100;

    private final float ANIM_SPEED_NORMAL = 0.10f;
    private final float ANIM_SPEED_EAT = 0.66f;
    private final float ANIM_SPEED_MOOH = 0.55f;
    private final float DEFAULT_SPEED = 120f * GAMESPEED;

    private final float INVINCIBLE_ANIM_TICK = .05f;
    private final float INVINCIBLE_TIME = 1.0f;

    private final float PLAYER_JUMP = 222.0f * GAMESPEED;

    private float health;
    private float healthColorPulseTimer;

    private byte yellowKeys = 0;
    private byte redKeys = 0;
    private byte blueKeys = 0;
    private boolean isGoalReached;
    private boolean isDead;
    private boolean isCollidingWithWall;
    private boolean isVisible;
    private boolean isAbleToWallJump;
    private boolean isTeleported;
    private boolean isInvincible;
    private boolean isAlphaZero;

    private Vector2 teleportLandingPosition;

    private boolean deathFromFalling;

    private float vibrationTimer;

    private float invincibleTimer;

    private float invincibleAnimTimer;

    private float deathCloudStateTime;
    private float waitTimeForCloud;

    private byte startDirection;

    private TextureRegion cowSpriteSheet;
    private Animation leftWalk, rightWalk;
    private Animation leftJump, rightJump;
    private Animation leftFall, rightFall;
    private Animation rightEat, leftEat;
    private Animation rightHurt, leftHurt;
    private Animation rightAssDown, leftAssDown;

    private final Sprite playerSprite;

    private float tempvelocity;
    private final Color playerColor;
    private final Animation changeClothesAnimation;
    private TextureRegion currentCloudFrame;

    private boolean canEmitLandingParticles;
    private boolean isKilled;

    public Player(Vector2 position, Level level) {
        super(position, level, 32, 32, 5, 12, 5, 0, ObjectType.PLAYER);
        this.cowSpriteSheet = getWardrobe();
        this.changeClothesAnimation = new Animation(.1f, TextureUtils.singleSplit((Texture) Assets.getInstance().get("sprites/objects/cloud_strip4.png"), 32, 32, false));
        this.currentCloudFrame = (TextureRegion) changeClothesAnimation.getKeyFrame(0);
        this.setTextures();
        this.speed = DEFAULT_SPEED;
        this.velocity.set(1.0f, 0.0f);
        this.currentFrame = (TextureRegion) rightWalk.getKeyFrame(0);
        this.playerSprite = new Sprite(currentFrame);
        this.playerColor = new Color(Color.WHITE);
        this.isTeleported = false;
    }

    public void setTextures() {
        this.cowSpriteSheet = getWardrobe();
        this.rightWalk = getFramesFromRegion(ANIM_SPEED_NORMAL, false, 0, 1, 2, 3);
        this.leftWalk = getFramesFromRegion(ANIM_SPEED_NORMAL, true, 0, 1, 2, 3);
        this.rightJump = getFramesFromRegion(ANIM_SPEED_MOOH, false, 4);
        this.leftJump = getFramesFromRegion(ANIM_SPEED_MOOH, true, 4);
        this.leftFall = getFramesFromRegion(ANIM_SPEED_NORMAL, true, 6, 7);
        this.rightFall = getFramesFromRegion(ANIM_SPEED_NORMAL, false, 6, 7);
        this.rightEat = getFramesFromRegion(ANIM_SPEED_EAT, false, 18, 19, 20);
        this.leftEat = getFramesFromRegion(ANIM_SPEED_EAT, true, 18, 19, 20);
        this.rightHurt = getFramesFromRegion(ANIM_SPEED_NORMAL, false, 21);
        this.leftHurt = getFramesFromRegion(ANIM_SPEED_NORMAL, true, 21);
        this.leftAssDown = getFramesFromRegion(ANIM_SPEED_NORMAL, true, 8);
        this.rightAssDown = getFramesFromRegion(ANIM_SPEED_NORMAL, false, 8);
    }

    public void setAbleToWallJump(boolean isAbleToWallJump) {
        this.isAbleToWallJump = isAbleToWallJump;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isDeathFromFalling() {
        return deathFromFalling;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isVisible) {
            playerSprite.setColor(isDead ? Color.LIGHT_GRAY : playerColor);
            playerSprite.setAlpha(isAlphaZero ? 0 : 1);
            playerSprite.draw(batch);
        }

        if (isDead && waitTimeForCloud >= .65f) {
            isVisible = false;
            batch.draw(currentCloudFrame, position.x, position.y);
        }
    }

    public boolean isGoalReached() {
        return isGoalReached;
    }

    public void setGoalReached(boolean isGoalReached) {
        this.isGoalReached = isGoalReached;
    }

    public boolean isNotMoving() {
        return isCollidingWithWall || speed == 0.0f;
    }

    public int getTextureWidth() {
        return currentFrame == null ? 32 : currentFrame.getRegionWidth();
    }

    public int getTextureHeight() {
        return currentFrame == null ? 32 : currentFrame.getRegionHeight();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void resetToDefaultSpeed() {
        this.speed = DEFAULT_SPEED;
    }

    public void increaseHealth(float amount) {
        if (health < MAX_HEALTH) {
            this.health += amount;
        }
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void kill() {
        this.health = 0.0f;
    }

    public boolean haveBlueKeys() {
        return this.blueKeys > 0;
    }

    public boolean haveRedKeys() {
        return this.redKeys > 0;
    }

    public boolean haveYellowKeys() {
        return this.yellowKeys > 0;
    }

    public void addYellowKey() {
        this.yellowKeys++;
    }

    public void removeYellowKey() {
        this.yellowKeys--;
    }

    public void addBluewKey() {
        this.blueKeys++;
    }

    public void removeBlueKey() {
        this.blueKeys--;
    }

    public void addRedKey() {
        this.redKeys++;
    }

    public void removeRedKey() {
        this.redKeys--;
    }

    public float getBoundsY() {
        return bounds.y;
    }


    @Override
    public void tick(float deltatime, Player player) {
        if (CowDash.FREE_FLYING_MODE) {
            debugTick(deltatime);
            return;
        }

        updateHealth(deltatime);

        animate(deltatime);

        boolean isTouched = Dialog.canJump && !isGoalReached && Dialog.screenTapped;
        if (!isTouched) {
            down = false;
        } else if (isTouched && direction == DIRECTION_NONE) {
            direction = startDirection;
            gravity = 0;
        }

        if (gravity >= TERMINAL_VELOCITY) {
            gravity = TERMINAL_VELOCITY;
        } else {
            tempvelocity = ACCELERATION * deltatime;

            if (gravity >= 0 && isJumping) {
                isJumping = false;
            }
        }

        if (onGround) {
            isJumping = false;
            if (isTouched && !down) {
                down = true;
                isJumping = true;
                canEmitLandingParticles = true;
                gravity = -PLAYER_JUMP;
            }

            if (!isJumping) {
                gravity = 0;
            }
        }

        if (!isTouched && isJumping) {
            down = false;
            gravity = 0;
        }


        velocity.set(0.0f, deltatime * (gravity + tempvelocity / 2.0f));
        if (isHeadingLeft() && !isGoalReached) {
            velocity.set(deltatime * this.speed, deltatime * (gravity + tempvelocity / 2.0f));
        } else if (isHeadingRight() && !isGoalReached) {
            velocity.set(deltatime * -this.speed, deltatime * (gravity + tempvelocity / 2.0f));
        } else if (direction == DIRECTION_NONE && !isTouched || isGoalReached) {
            velocity.set(deltatime * 0.0f, deltatime * (gravity + tempvelocity / 2.0f));
        }

        if (isTeleported) {
            if (getPosition().dst(getTeleportLandingPosition()) > 32f) {
                isTeleported = false;
            }
        }

        if (isInvincible) {
            invincibleTimer += deltatime;
            invincibleAnimTimer += deltatime;
            if (invincibleAnimTimer >= INVINCIBLE_ANIM_TICK) {
                isAlphaZero = !isAlphaZero;
                invincibleAnimTimer = 0;
            }
            if (invincibleTimer > INVINCIBLE_TIME) {
                invincibleTimer = 0;
                invincibleAnimTimer = 0;
                isAlphaZero = false;
                setInvincible(false);
            }
        }

        Vector2 newPosition = new Vector2(0, 0);
        newPosition.add(this.position);
        playerSprite.setPosition(position.x, position.y);

        if (!isDead && !isGoalReached) {
            newPosition.sub(velocity);
        }

        tryMove(newPosition);

        gravity += tempvelocity;

        if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) {
            kill();
        }
    }

    public void setStartDirection(String startDir) {
        if (startDir != null) {
            if (startDir.equalsIgnoreCase("left")) {
                startDirection = DIRECTION_LEFT;
            } else {
                startDirection = DIRECTION_RIGHT;
            }
        } else {
            startDirection = DIRECTION_RIGHT;
        }
    }

    public void showLandAnimation() {
        if (isHeadingRight()) {
            currentFrame = (TextureRegion) rightAssDown.getKeyFrame(0);
        } else {
            currentFrame = (TextureRegion) leftAssDown.getKeyFrame(0);
        }
    }

    @Override
    protected void handleVerticalCollisionFromAbove(Vector2 newPosition) {
        Rectangle tmpBounds = new Rectangle(bounds);
        tmpBounds.y = newPosition.y;
        tmpBounds.height = (newPosition.y - bounds.y) * -1;
        movingBounds.set(tmpBounds.x, tmpBounds.y, tmpBounds.width, tmpBounds.height);
        Rectangle downBounds = level.getBoundsAt(tmpBounds, (byte) (Filter.HARD | Filter.SOFT | Filter.DOOR | Filter.SAND), this);
        if (downBounds != null) {
            onGround = true;
            if (canEmitLandingParticles) {
                ParticleHelper.getInstance().addLandParticleEffect(position.x + getTextureWidth() / 2f, position.y, false);
                canEmitLandingParticles = false;
            }
            Vector2 groundedPosition = new Vector2(position.x, downBounds.y + downBounds.height);
            setPosition(groundedPosition);
        } else {
            onGround = false;
            setPosition(position.x, newPosition.y);
        }
    }

    @Override
    protected void handleHorizontalCollision(Vector2 newPosition) {
        Rectangle tmpHorzBounds = new Rectangle(bounds);
        tmpHorzBounds.x = newPosition.x + left;
        Rectangle horzBounds = level.getBoundsAt(tmpHorzBounds, (byte) (Filter.HARD | Filter.HOG | Filter.DOOR | Filter.SAND), this);
        if (horzBounds != null) {
            isCollidingWithWall = true;
            if (!onGround) {
                checkIfTimeForWallJump();
            } else {
                switchDirection();
            }
            setPosition(position.x, position.y);
        } else {
            isCollidingWithWall = false;
            setPosition(newPosition.x, position.y);
        }
    }

    protected Animation getFramesFromRegion(float animSpeed, boolean flipX, int... positions) {
        TextureRegion[] region = new TextureRegion[positions.length];
        for (int i = 0; i < positions.length; i++) {
            region[i] = new TextureRegion(this.cowSpriteSheet, positions[i] * 32, 0, 32, 32);
            region[i].flip(flipX, false);
        }
        return new Animation(animSpeed, region);
    }

    @Override
    protected void animate(float deltaTime) {
        super.animate(deltaTime);
        if (speed != 0.0f) {
            if (direction == DIRECTION_NONE) {
                if (startDirection == DIRECTION_LEFT) {
                    currentFrame = (TextureRegion) leftEat.getKeyFrame(stateTime, true);
                } else {
                    currentFrame = (TextureRegion) rightEat.getKeyFrame(stateTime, true);
                }
            } else if (direction == DIRECTION_RIGHT) {
                if ((isJumping() && !isDead) || gravity < 0) {
                    currentFrame = (TextureRegion) rightJump.getKeyFrame(stateTime, false);
                } else if (isFalling() && !isDead) {
                    currentFrame = (TextureRegion) rightFall.getKeyFrame(stateTime, true);
                } else if (isDead) {
                    currentFrame = (TextureRegion) rightHurt.getKeyFrame(stateTime, false);
                } else if (isGoalReached) {
                    currentFrame = (TextureRegion) rightWalk.getKeyFrame(0, false);
                } else {
                    currentFrame = (TextureRegion) rightWalk.getKeyFrame(stateTime, true);
                }
            } else if (direction == DIRECTION_LEFT) {
                if ((isJumping() && !isDead) || gravity < 0) {
                    currentFrame = (TextureRegion) leftJump.getKeyFrame(stateTime, false);
                } else if (isFalling() && !isDead) {
                    currentFrame = (TextureRegion) leftFall.getKeyFrame(stateTime, true);
                } else if (isDead) {
                    currentFrame = (TextureRegion) leftHurt.getKeyFrame(stateTime, false);
                } else if (isGoalReached) {
                    currentFrame = (TextureRegion) leftWalk.getKeyFrame(0, false);
                } else {
                    currentFrame = (TextureRegion) leftWalk.getKeyFrame(stateTime, true);
                }
            }
        } else {
            showLandAnimation();
        }

        playerSprite.setRegion(currentFrame);
    }

    private void checkIfTimeForWallJump() {
        if (isAbleToWallJump && !down && (Dialog.canJump && !isGoalReached && Dialog.screenTapped)) {
            AudioUtils.getInstance().playSoundFX("walljump");
            down = true;
            isJumping = true;
            gravity = -PLAYER_JUMP;
            switchDirection();
        }
    }

    private void checkHealthAndVibrateAccordingly(float deltaTime) {
        vibrationTimer += deltaTime;

        if (!isDead && !isGoalReached) {
            if (health <= 50.0f && health >= 25.0f && vibrationTimer > 1.2f) {
                Gdx.input.vibrate(50);
                vibrationTimer = 0.0f;
            } else if (health < 25.0f && health >= 10.0f
                    && vibrationTimer > 0.6f) {
                Gdx.input.vibrate(120);
                vibrationTimer = 0.0f;
            } else if (health < 10.0f && vibrationTimer > 0.4f) {
                Gdx.input.vibrate(200);
                vibrationTimer = 0.0f;
            }
        }
    }

    private void updateHealth(float deltatime) {

        final float HEALTH_DEC = 6.0f * deltatime;

        if (Gdx.app.getType() == ApplicationType.Android
                && !CowPreferences.getInstance().isVibrationDisabled()
                && !isDead) {
            checkHealthAndVibrateAccordingly(deltatime);
        }

        if (health <= 0f || position.y + getTextureHeight() <= 0) {
            isDead = true;
            waitTimeForCloud += deltatime;
            if (waitTimeForCloud >= .65f) {
                deathCloudStateTime += deltatime;
                currentCloudFrame = (TextureRegion) changeClothesAnimation.getKeyFrame(deathCloudStateTime);
                if (!changeClothesAnimation.isAnimationFinished(deathCloudStateTime * 2)) {
                    if (!isKilled) {
                        AudioUtils.getInstance().playSoundFX("cowdie");
                        isKilled = true;
                    }
                    level.rumble(3.0f, .3f);
                }
            }

            if (position.y + getTextureHeight() <= 0) {
                deathFromFalling = true;
            }
            currentFrame = isHeadingRight() ? (TextureRegion) rightHurt.getKeyFrame(0) : (TextureRegion) leftHurt.getKeyFrame(0);
            return;
        }

        if (health < MAX_HEALTH / 4) {
            float green = 1;
            float blue = 1;

            healthColorPulseTimer += deltatime;
            if (healthColorPulseTimer < .5f) {
                green = Math.min(0.0f, -healthColorPulseTimer * 2f);
                blue = Math.min(0.0f, -healthColorPulseTimer * 2f);
                playerColor.lerp(1.0f, green, blue, 1.0f, deltatime);
            } else if (healthColorPulseTimer >= .5f && healthColorPulseTimer <= 1.0f) {
                green = Math.min(1.0f, healthColorPulseTimer * 2f);
                blue = Math.min(1.0f, healthColorPulseTimer * 2f);
                playerColor.lerp(1.0f, green, blue, 1.0f, deltatime);
            } else {
                healthColorPulseTimer = 0.0f;
            }
        } else {
            playerColor.set(1, 1, 1, 1);
            healthColorPulseTimer = 0.0f;
        }

        if (direction != DIRECTION_NONE && !isGoalReached && !CowDash.GOD_MODE) {
            health -= HEALTH_DEC;
        }

        if (health >= MAX_HEALTH) {
            health = MAX_HEALTH;
        }
    }


    private TextureRegion getWardrobe() {
        CowPreferences cowPreferences = CowPreferences.getInstance();
        Head head = Head.getValue(cowPreferences.getSavedClothing(ItemUtils.HEAD));
        Body body = Body.getValue(cowPreferences.getSavedClothing(ItemUtils.BODY));
        Leg leg = Leg.getValue(cowPreferences.getSavedClothing(ItemUtils.LEG));
        Back back = Back.getValue(cowPreferences.getSavedClothing(ItemUtils.BACK));
        Mask mask = Mask.getValue(cowPreferences.getSavedClothing(ItemUtils.MASK));

        Pixmap cowPixmap = new Pixmap(Gdx.files.internal("sprites/objects/cow.png"));
        cowPixmap.drawPixmap(leg.getPixmap(), 0, 0);
        cowPixmap.drawPixmap(body.getPixmap(), 0, 0);
        cowPixmap.drawPixmap(back.getPixmap(), 0, 0);
        cowPixmap.drawPixmap(mask.getPixmap(), 0, 0);
        cowPixmap.drawPixmap(head.getPixmap(), 0, 0);

        return new TextureRegion(new Texture(cowPixmap));
    }

    private void debugTick(float deltaTime) {
        velocity.set(speed, gravity);
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            direction = DIRECTION_LEFT;
            speed = -4f;
        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            direction = DIRECTION_RIGHT;
            speed = 4f;
        } else if (Gdx.input.isKeyPressed(Keys.UP)) {
            gravity = 4f;
        } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            gravity = -4f;
        } else {
            speed = 0;
            gravity = 0;
        }
        position.add(velocity);
        animate(deltaTime);
        bounds.setPosition(position);
    }

    @Override
    public void dispose() {
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isTeleported() {
        return isTeleported;
    }

    public void setTeleported(boolean isTeleported) {
        this.isTeleported = isTeleported;
    }

    public Vector2 getTeleportLandingPosition() {
        return teleportLandingPosition;
    }

    public void setTeleportLandingPosition(Vector2 teleportLandingPosition) {
        this.teleportLandingPosition = teleportLandingPosition;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public void setInvincible(boolean isInvincible) {
        this.isInvincible = isInvincible;
    }

}
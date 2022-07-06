package com.bitwave.cowdash.objects.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.CowDash;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.level.Level.Filter;
import com.bitwave.cowdash.objects.ObjectType;
import com.bitwave.cowdash.objects.Player;
import com.bitwave.cowdash.screen.ingame.Dialog;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.ParticleHelper;

public class JumpingEnemy extends Enemy {

    private final float TURNING_POINT = .10f;
    private final float AMIN_SPEED_JUMP = .1f;
    private final float WAIT_TIME = .5f;
    private final float DEATH_TIME = .1f;

    private float frameTimer;

    private float deathTime;
    private byte lastKnownDirection;

    private boolean particlesStarted;

    private boolean isTimeToDie;

    private final Animation leftJump;
    private final Animation rightJump;
    private final Animation leftIdle;
    private final Animation rightIdle;
    private final Animation leftDeath;
    private final Animation rightDeath;

    public JumpingEnemy(Vector2 position, String startDirection, Level level) {
        super(position, startDirection, 32, 64, 3, 0, 3, 38, false, level, 40f, ObjectType.ENEMY_JUMPING);
        TextureRegion region = new TextureRegion((Texture) Assets.getInstance().get("sprites/objects/enemies/spring_enemy_pink_upped_strip11.png"));
        TextureRegion idleRegion = new TextureRegion((Texture) Assets.getInstance().get("sprites/objects/enemies/LASTTTT.png"));
        this.leftJump = getFramesFromRegion(region, AMIN_SPEED_JUMP, 32, 64, true);
        this.rightJump = getFramesFromRegion(region, AMIN_SPEED_JUMP, 32, 64, false);
        this.leftIdle = getFramesFromRegion(idleRegion, 0, 32, 64, true);
        this.rightIdle = getFramesFromRegion(idleRegion, 0, 32, 64, false);
        this.leftDeath = getFramesFromRegion(new TextureRegion((Texture) Assets.getInstance().get("sprites/objects/enemies/spring_death_spritesheet_strip7.png")), DEATH_TIME, 32, 64, true);
        this.rightDeath = getFramesFromRegion(new TextureRegion((Texture) Assets.getInstance().get("sprites/objects/enemies/spring_death_spritesheet_strip7.png")), DEATH_TIME, 32, 64, false);
        this.topBounds = new Rectangle(bounds.x - bounds.width / 2, bounds.y + bounds.height, bounds.width * 2, bounds.height);

        if (startDirection != null) {
            if (startDirection.equalsIgnoreCase("left")) {
                this.direction = DIRECTION_LEFT;
                currentFrame = (TextureRegion) leftIdle.getKeyFrame(0, false);
            } else {
                this.direction = DIRECTION_RIGHT;
                currentFrame = (TextureRegion) rightIdle.getKeyFrame(0, false);
            }
        } else {
            this.direction = DIRECTION_RIGHT;
            currentFrame = (TextureRegion) rightIdle.getKeyFrame(0, false);
        }
        this.lastKnownDirection = this.direction;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y);
    }

    @Override
    public void tick(float deltaTime, Player player, Array<Enemy> enemies) {
        if (!player.isDead() && !player.isGoalReached() && Dialog.screenTapped) {
            super.tick(deltaTime, player, enemies);
            Vector2 newPosition = new Vector2(0, 0);

            animate(deltaTime);
            if (velocity.y >= TERMINAL_VELOCITY) {
                gravity = TERMINAL_VELOCITY;
            } else {
                tempvelocity = ACCELERATION * deltaTime;
            }

            if (onGround) {
                this.gravity = 0;
            }

            handleJump(deltaTime);

            if (!isTimeToDie) {
                velocity.set(0.0f, gravity);
                if (direction == DIRECTION_LEFT) {
                    velocity.set(this.speed * deltaTime, deltaTime * (gravity + tempvelocity / 2));
                } else if (direction == DIRECTION_RIGHT) {
                    velocity.set(-this.speed * deltaTime, deltaTime * (gravity + tempvelocity / 2));
                } else if (direction == DIRECTION_NONE) {
                    velocity.set(0.0f, deltaTime * (gravity + tempvelocity / 2));
                }
            } else {
                velocity.set(0.0f, 0.0f);
            }

            newPosition.add(this.position);
            newPosition.sub(velocity);
            topBounds.setPosition(bounds.x - bounds.width / 2, bounds.y + bounds.height);
            tryMove(newPosition);

            gravity += tempvelocity;

            if (player.getBounds().overlaps(topBounds)) {
                if (Gdx.input.justTouched()) {
                    isAllowedToPerformHighJump = true;
                }
                if (player.getBounds().overlaps(bounds) && player.isFalling() && !particlesStarted) {
                    player.setSpeed(0.0f);
                    player.showLandAnimation();
                    if (deathTime < TURNING_POINT) {
                        bounds.y -= 2f;
                    } else {
                        bounds.y += 2f;
                        player.resetToDefaultSpeed();
                        if (isAllowedToPerformHighJump) {
                            player.setGravity(-Player.JUMP * 1.5f);
                            player.resetToDefaultSpeed();
                            AudioUtils.getInstance().playSoundFX("pig_trampoline");
                            isAllowedToPerformHighJump = false;
                        } else {
                            player.resetToDefaultSpeed();
                            player.setGravity(-Player.JUMP / 1.7f);
                            AudioUtils.getInstance().playSoundFX("pig");
                            isAllowedToPerformHighJump = false;
                        }
                    }
                    player.getPosition().y = bounds.y + bounds.height;
                    isTimeToDie = true;
                }
            } else if (player.getBounds().overlaps(bounds)) {
                if (!CowDash.GOD_MODE && !isTimeToDie && !player.isGoalReached() && !player.isInvincible()) {
                    player.kill();
                }
            }

            if (finishedDying() && !particlesStarted) {
                ParticleHelper.getInstance().addEnemyEffect(position.x + (currentFrame.getRegionWidth() / 2), position.y + bounds.height * 2, true);
                particlesStarted = true;
            }

            if (finishedDying()) {
                isCollidedWith = true;
            }
        }
    }

    public void setLastKnownDirection(byte lastKnownDirection) {
        this.lastKnownDirection = lastKnownDirection;
    }

    @Override
    protected void handleHorizontalCollision(Vector2 newPosition) {
        Rectangle tmpHorzBounds = new Rectangle(bounds);
        tmpHorzBounds.x = newPosition.x + left;
        tmpHorzBounds.y += bounds.height / 2;
        tmpHorzBounds.height = 1;
        Rectangle horzBounds = level.getBoundsAt(tmpHorzBounds, (byte) (Filter.HARD | Filter.DOOR | Filter.SAND), this);
        if (horzBounds != null) {
            switchDirection();
            lastKnownDirection = direction;
            setPosition(position.x, newPosition.y);
        } else {
            setPosition(newPosition.x, position.y);
        }
    }

    @Override
    protected void handleVerticalCollisionFromAbove(Vector2 newPosition) {
        Rectangle tmpBounds = new Rectangle(bounds);
        tmpBounds.y -= 3;
        movingBounds.set(tmpBounds.x, tmpBounds.y, tmpBounds.width, tmpBounds.height);
        Rectangle downBounds = level.getBoundsAt(tmpBounds, (byte) (Filter.HARD | Filter.SOFT | Filter.DOOR | Filter.SAND), this);
        if (downBounds != null) {
            onGround = true;
            Vector2 groundedPosition = new Vector2(position.x, downBounds.y + downBounds.height);
            if (position == groundedPosition) {
                setPosition(groundedPosition);
            }
        } else {
            setPosition(position.x, newPosition.y);
        }
    }


    private void handleJump(float deltaTime) {
        if (onGround) {
            isJumping = false;
            if (frameTimer > 0) {
                direction = lastKnownDirection;
                frameTimer = 0f;
                gravity = -JUMP;
            } else {
                direction = DIRECTION_NONE;
                frameTimer += deltaTime;
            }
        } else {
            isJumping = true;
        }
    }

    @Override
    protected void animate(float deltaTime) {
        stateTime += deltaTime;
        if (!isTimeToDie) {
            if (onGround) {
                if (isHeadingRight()) {
                    currentFrame = (TextureRegion) rightIdle.getKeyFrame(0, true);
                } else if (isHeadingLeft()) {
                    currentFrame = (TextureRegion) leftIdle.getKeyFrame(0, true);
                }
            } else if (isJumping && frameTimer < WAIT_TIME) {
                if (isHeadingRight()) {
                    currentFrame = (TextureRegion) rightJump.getKeyFrame(stateTime, true);
                } else if (isHeadingLeft()) {
                    currentFrame = (TextureRegion) leftJump.getKeyFrame(stateTime, true);
                }
            }
        } else {
            deathTime += deltaTime;
            if (isHeadingRight()) {
                currentFrame = (TextureRegion) rightDeath.getKeyFrame(deathTime, false);
            } else if (isHeadingLeft()) {
                currentFrame = (TextureRegion) leftDeath.getKeyFrame(deathTime, false);
            }
        }

    }

    private boolean finishedDying() {
        return isTimeToDie && (leftDeath.isAnimationFinished(deathTime) || rightDeath.isAnimationFinished(deathTime));
    }

}
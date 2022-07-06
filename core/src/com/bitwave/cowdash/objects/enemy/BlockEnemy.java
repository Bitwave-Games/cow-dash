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

public class BlockEnemy extends Enemy {

    private final byte SOLID = 0;

    private final byte LIQUID = 1;

    private final float TURNING_POINT = .10f;
    private final float AMIN_SPEED_WALK = .1f;
    private final float ANIM_SPEED_DEATH = .2f;
    private byte state;
    private final Animation leftWalk;
    private final Animation rightWalk;
    private final Animation leftDeath;
    private final Animation rightDeath;
    private float deathTime;
    private boolean isTimeToDie;

    private float curveYPos;

    public BlockEnemy(Vector2 position, String startDirection, boolean isBoundToPlatform, Level level, byte state) {
        super(position, startDirection, 32, 32, 0, 0, 0, 0, isBoundToPlatform, level, 20.0f, ObjectType.ENEMY_BLOCK);
        this.state = state;
        this.isRemoveable = false;
        TextureRegion walk = new TextureRegion((Texture) Assets.getInstance().get("sprites/objects/enemies/blockguy_sepia_strip13.png"));
        TextureRegion death = new TextureRegion((Texture) Assets.getInstance().get("sprites/objects/enemies/blockguy_death_sepia_strip11.png"));
        this.leftWalk = getFramesFromRegion(walk, AMIN_SPEED_WALK, true);
        this.rightWalk = getFramesFromRegion(walk, AMIN_SPEED_WALK, false);
        this.leftDeath = getFramesFromRegion(death, ANIM_SPEED_DEATH, true);
        this.rightDeath = getFramesFromRegion(death, ANIM_SPEED_DEATH, false);
        this.topBounds = new Rectangle(bounds.x - bounds.width / 2, bounds.y + bounds.height, bounds.width * 2, bounds.height);
        if (startDirection != null) {
            if (startDirection.equalsIgnoreCase("left")) {
                currentFrame = (TextureRegion) leftWalk.getKeyFrame(0, true);
            } else {
                currentFrame = (TextureRegion) rightWalk.getKeyFrame(0, true);
            }
        } else {
            currentFrame = (TextureRegion) rightWalk.getKeyFrame(0, true);
        }

        if (state == SOLID) {
            currentFrame.setRegion(death, 320, 0, 32, 32);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y);
    }

    public void tick(float deltaTime, Player player, Array<Enemy> enemies) {
        if (!player.isDead() && !player.isGoalReached() && Dialog.screenTapped) {
            super.tick(deltaTime, player, enemies);

            Vector2 newPosition = new Vector2(0, 0);

            if (state == LIQUID) {
                animate(deltaTime);
            }

            if (gravity >= TERMINAL_VELOCITY) {
                gravity = TERMINAL_VELOCITY;
            } else {
                tempvelocity = ACCELERATION * deltaTime;
            }

            if (onGround) {
                gravity = 0;
            }

            if (!isTimeToDie && state == LIQUID) {
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
                if (Gdx.input.justTouched() && state == LIQUID) {
                    isAllowedToPerformHighJump = true;
                }
                if (player.getBounds().overlaps(bounds) && player.isFalling()) {
                    isTimeToDie = true;
                    if (state == LIQUID) {
                        player.setSpeed(0.0f);
                        if (deathTime < TURNING_POINT) {
                            curveYPos -= .5f;
                        } else {
                            curveYPos += .5f;
                            if (isAllowedToPerformHighJump) {
                                player.resetToDefaultSpeed();
                                player.setGravity(-Player.JUMP * 1.5f);
                                AudioUtils.getInstance().playSoundFX("pig_trampoline");
                                isAllowedToPerformHighJump = false;
                            } else {
                                player.resetToDefaultSpeed();
                                player.setGravity(-Player.JUMP / 1.7f);
                                AudioUtils.getInstance().playSoundFX("pig");
                                isAllowedToPerformHighJump = false;
                            }
                        }

                        player.getPosition().y = curveYPos;
                    } else {
                        player.setOnGround(true);
                        player.getPosition().y = curveYPos;
                    }
                }
            } else if (player.getBounds().overlaps(bounds)) {
                if (state == LIQUID) {
                    if (!CowDash.GOD_MODE && !isTimeToDie && !player.isGoalReached() && !player.isInvincible()) {
                        player.kill();
                    } else if (player.isInvincible()) {

                    } else {
                        player.switchDirection();
                    }
                } else {
                    player.switchDirection();
                }
            } else {
                curveYPos = bounds.y + bounds.height;
            }

            if (finishedDying()) {
                isCollidedWith = true;
                if (state != SOLID) {
                    level.addHardBlockAt(position);
                    state = SOLID;
                }
            }
        }
    }

    @Override
    protected void animate(float deltaTime) {
        super.animate(deltaTime);
        if (isHeadingRight()) {
            if (isTimeToDie) {
                deathTime += deltaTime;
                currentFrame = (TextureRegion) rightDeath.getKeyFrame(deathTime, false);
            } else {
                currentFrame = (TextureRegion) rightWalk.getKeyFrame(stateTime, true);
            }
        } else if (isHeadingLeft()) {
            if (isTimeToDie) {
                deathTime += deltaTime;
                currentFrame = (TextureRegion) leftDeath.getKeyFrame(deathTime, false);
            } else {
                currentFrame = (TextureRegion) leftWalk.getKeyFrame(stateTime, true);
            }
        }
    }


    @Override
    protected void handleVerticalCollisionFromAbove(Vector2 newPosition) {
        Rectangle tmpBounds = new Rectangle(bounds);
        tmpBounds.y = newPosition.y;
        tmpBounds.height = (newPosition.y - bounds.y) * -1;
        if (isBoundToPlatform) {
            tmpBounds.width = 1;
            tmpBounds.y -= 1;
            tmpBounds.height = 2;
            if (isHeadingRight()) {
                tmpBounds.x += bounds.width + 2f;
            } else {
                tmpBounds.x -= 2f;
            }
            Rectangle turnPoint = level.getBoundsAt(tmpBounds, (byte) (Filter.HARD | Filter.SOFT | Filter.DOOR | Filter.SAND), this);
            if (turnPoint == null) {
                switchDirection();
            } else {
                onGround = true;
            }
        } else {
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
        movingBounds.set(tmpBounds.x, tmpBounds.y, tmpBounds.width, tmpBounds.height);
    }

    private boolean finishedDying() {
        return isTimeToDie && (leftDeath.isAnimationFinished(deathTime) || rightDeath.isAnimationFinished(deathTime));
    }

    @Override
    public void tick(float deltaTime, Player player) {
    }

}

package com.bitwave.cowdash.objects.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.objects.ObjectType;
import com.bitwave.cowdash.objects.Player;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public class TrampolineHog extends Enemy {

    private final float ANIM_SPEED_IDLE = .5f;
    private final float ANIM_SPEED_AWOKEN = .08f;
    private final float ANIM_SPEED_RETURNING_TO_SLEEP = .25f;

    private final float TURNING_POINT = .16f;

    private final Animation idle;
    private final Animation awoken;
    private final Animation returnToSleep;
    private float squeelTime;
    private float relaxTime;
    private boolean isJumpedOn;

    public TrampolineHog(Vector2 position, Level level) {
        super(position, level, 32, 32, 0, 0, 0, 0, ObjectType.ENEMY_TRAMP_HOG);
        this.isRemoveable = false;

        Texture texture = null;
        WorldType worldType = WorldType.getValue(CowPreferences.getInstance().getCurrentWorld());
        switch (worldType) {
            case BEACH:
                texture = (Texture) Assets.getInstance().get("sprites/objects/enemies/beach_pig_all_strip11.png");
                break;
            default:
                texture = (Texture) Assets.getInstance().get("sprites/objects/enemies/grass_pig_all_strip11.png");
                break;
        }

        this.idle = getFramesFromRegion(texture, ANIM_SPEED_IDLE, false, 0, 1, 2, 3, 4);
        this.awoken = getFramesFromRegion(texture, ANIM_SPEED_AWOKEN, false, 5, 6, 7);
        this.returnToSleep = getFramesFromRegion(texture, ANIM_SPEED_RETURNING_TO_SLEEP, false, 8, 9, 10);
        this.currentFrame = (TextureRegion) idle.getKeyFrame(stateTime);
        this.topBounds = new Rectangle(bounds.x - (bounds.height / 2), bounds.y + bounds.height, bounds.width * 2, bounds.height);
    }

    @Override
    protected void animate(float deltaTime) {
        super.animate(deltaTime);
        if (isJumpedOn) {
            if (!awoken.isAnimationFinished(squeelTime)) {
                squeelTime += deltaTime;
                currentFrame = (TextureRegion) awoken.getKeyFrame(squeelTime, false);
            } else {
                relaxTime += deltaTime;
                currentFrame = (TextureRegion) returnToSleep.getKeyFrame(relaxTime, false);
            }
        } else {
            relaxTime = 0;
            squeelTime = 0;
            currentFrame = (TextureRegion) idle.getKeyFrame(stateTime, true);
        }
    }

    private boolean finishedSqueeling() {
        return awoken.isAnimationFinished(squeelTime) && returnToSleep.isAnimationFinished(relaxTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y);
    }

    @Override
    public void tick(float deltaTime, Player player) {

    }

    public void tick(float deltaTime, Player player, Array<Enemy> enemies) {
        super.tick(deltaTime, player, enemies);

        stateTime += deltaTime;
        animate(deltaTime);
        if (player.getBounds().overlaps(topBounds) && player.isFalling()) {
            if (Gdx.input.justTouched()) {
                isAllowedToPerformHighJump = true;
            }
            if (player.getBounds().overlaps(bounds)) {
                player.setSpeed(0.0f);
                isJumpedOn = true;

                if (squeelTime < TURNING_POINT) {
                    bounds.y -= .5f;
                } else {
                    bounds.y += .5f;
                    player.resetToDefaultSpeed();
                    if (isAllowedToPerformHighJump) {
                        if (player.isFalling()) {
                            AudioUtils.getInstance().playSoundFX("pig_trampoline");
                        }
                        player.setGravity(-Player.JUMP * 2f);
                    } else {
                        if (player.isFalling()) {
                            AudioUtils.getInstance().playSoundFX("pig");
                        }
                        player.setGravity(-(Player.JUMP + 12));
                    }
                }

                player.getPosition().y = bounds.y + player.getTextureHeight();
            }
        } else if (player.getBounds().overlaps(bounds) && !isJumpedOn) {
            bounds.y = position.y;
        } else {
            bounds.y = position.y;
            isAllowedToPerformHighJump = false;
        }

        if (finishedSqueeling()) {
            isJumpedOn = false;
        }

        for (Enemy enemy : enemies) {
            if (enemy.getObjectType().equals(ObjectType.ENEMY_BLOCK)) {
                if (enemy.getBounds().overlaps(bounds)) {
                    enemy.switchDirection();
                }
            }
        }
    }

}
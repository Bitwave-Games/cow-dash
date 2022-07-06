package com.bitwave.cowdash.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;

public class BouncingWall extends GameObject {

    private final float SPEED = .1f;
    private final Animation animation;


    public BouncingWall(Vector2 position, Level level, float width, float height, String direction) {
        super(position, level, width, height, 8, 8, 8, 8, ObjectType.OBJECT_BOUNCING_WALL);
        boolean isLeftDirection = false;
        if (direction != null) {
            if (direction.equalsIgnoreCase("right")) {
                isLeftDirection = false;
                position.x -= 20;
                bounds.x -= 20;
                super.direction = 1;
            } else {
                super.direction = 0;
                isLeftDirection = true;
            }
        } else {
            super.direction = 1;
            position.x -= 20;
            bounds.x -= 20;
            isLeftDirection = false;
        }
        Texture texture = (Texture) Assets.getInstance().get("sprites/objects/wall_flipper.png");
        this.animation = getFramesFromRegion(texture, SPEED, 48, 32, isLeftDirection, 0, 1, 2, 1, 0);
        this.currentFrame = (TextureRegion) animation.getKeyFrame(0);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y);
    }

    @Override
    public void tick(float deltaTime, Player player) {
        if (bounds.overlaps(player.getBounds()) && !isCollidedWith) {
            AudioUtils.getInstance().playSoundFX("bouncewall");
            AudioUtils.getInstance().playSoundFX("openOneWayDoor");
            if (super.direction != player.getDirection()) {
                player.switchDirection();
            }
            player.setOnGround(false);
            player.setGravity(-Player.JUMP * 2);
            player.isJumping = false;
            isCollidedWith = true;
        }
        animate(deltaTime);
    }

    @Override
    protected void animate(float deltaTime) {
        if (stateTime < SPEED * 5 && isCollidedWith) {
            stateTime += deltaTime;
            currentFrame = (TextureRegion) animation.getKeyFrame(stateTime, false);
        } else {
            isCollidedWith = false;
            stateTime = 0;
        }
    }


}

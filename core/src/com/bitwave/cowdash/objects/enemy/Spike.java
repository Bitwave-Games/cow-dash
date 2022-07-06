package com.bitwave.cowdash.objects.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.objects.ObjectType;
import com.bitwave.cowdash.objects.Player;
import com.bitwave.cowdash.tween.TweenHandler;
import com.bitwave.cowdash.utils.Assets;

import aurelienribon.tweenengine.TweenEquations;


public class Spike extends Enemy {

    private final Animation blinkAnimation;
    private final TweenHandler tweenHandler;

    public Spike(Vector2 position, Level level) {
        super(position, level, 16, 16, 4, 4, 4, 4, ObjectType.ENEMY_SPIKE);
        this.blinkAnimation = getFramesFromRegion(new TextureRegion((Texture) Assets.getInstance().get("sprites/objects/enemies/cactus_blink_strip2.png")), MathUtils.random(.4f, .8f), 16, 16);
        this.currentFrame = (TextureRegion) blinkAnimation.getKeyFrame(stateTime);
        this.enemySprite = new Sprite(currentFrame);
        this.enemySprite.setPosition(position.x, position.y);
        this.tweenHandler = new TweenHandler();
        this.tweenHandler.createCircularHoveringEffect(enemySprite, TweenEquations.easeInOutSine, new Rectangle(bounds.x - right, bounds.y - bottom, bounds.width, bounds.height), MathUtils.random(0.2f, 0.36f), MathUtils.random(0.35f, 0.86f));
    }

    @Override
    public void tick(float deltaTime, Player player, Array<Enemy> enemies) {
        tweenHandler.update(deltaTime);
        animate(deltaTime);
        if (player.getBounds().overlaps(bounds) && !player.isGoalReached()) {
            player.kill();
        }
    }

    @Override
    protected void animate(float deltaTime) {
        super.animate(deltaTime);
        currentFrame = (TextureRegion) blinkAnimation.getKeyFrame(stateTime, true);
        enemySprite.setRegion(currentFrame);
    }

    @Override
    public void render(SpriteBatch batch) {
        enemySprite.draw(batch);
    }


}
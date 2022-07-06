package com.bitwave.cowdash.objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.tween.TweenHandler;
import com.bitwave.cowdash.utils.Assets;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;


public class Birdy extends GameObject implements Disposable {

    protected TweenHandler tweenHandler;
    protected Rectangle tweenBounds;
    private boolean isFlapping;
    private boolean isStanding;
    private boolean isFinishedTween;
    private final Animation animation;
    private final Sprite birdSprite;

    public Birdy(Vector2 position, Level level) {
        super(position, level, 25, 25, ObjectType.OBJECT_BIRDY);
        this.animation = getFramesFromRegion(new TextureRegion((Texture) Assets.getInstance().get("sprites/objects/birdsheet.png")), 0.5f, 8, 8);
        this.currentFrame = (TextureRegion) animation.getKeyFrame(0);
        this.birdSprite = new Sprite(currentFrame);
        this.birdSprite.setPosition(position.x, position.y);
        this.tweenHandler = new TweenHandler();
    }

    @Override
    public void render(SpriteBatch batch) {
        birdSprite.draw(batch);
    }

    @Override
    public void tick(float deltaTime, Player player) {
        this.tweenHandler.update(deltaTime);
        birdSprite.setRegion(currentFrame);
        animate(deltaTime);

        if (player.getBounds().overlaps(bounds) && !isFlapping) {
            tweenBird(player);
            isFlapping = true;
        }
    }

    @Override
    public void dispose() {
        tweenHandler.killTween(birdSprite);
    }

    @Override
    protected void animate(float deltaTime) {
        super.animate(deltaTime);
        if (isFlapping) {
            currentFrame = (TextureRegion) animation.getKeyFrame(stateTime, true);
        }
    }

    protected void tweenBird(Player player) {
        OrthographicCamera camera = level.getCamera();
        float targetX = MathUtils.random(position.x - 75, position.x + 75);
        float targetY = camera.position.y - camera.viewportHeight / 2 - 10;
        tweenHandler.tweenToTarget(birdSprite, TweenEquations.easeOutSine, targetX, targetY, MathUtils.random(1, 1.33f), getBirdyCallback(player, targetX, targetY));

    }

    protected TweenCallback getBirdyCallback(final Player player, final float targetX, final float targetY) {
        return new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                isFinishedTween = true;
            }
        };
    }

    public boolean isFinishedTween() {
        return isFinishedTween;
    }

    public boolean isStanding() {
        return isStanding;
    }

    public boolean isFlapping() {
        return false;
    }
}

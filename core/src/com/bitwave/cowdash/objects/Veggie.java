package com.bitwave.cowdash.objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.TextureUtils;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

public class Veggie extends GameObject implements Disposable {

    private final byte MAX_VEGGIE = 3;
    private final byte EAT_MUNCH_TIME = (byte) MathUtils.random(3);

    protected boolean isEaten;
    protected Sprite sprite;
    protected TweenHandler tweenHandler;
    protected Rectangle tweenBounds;

    protected boolean veggieSoundPlayed;

    protected boolean isIncreaseHealth;
    private byte randCount;

    public Veggie(Vector2 position, Level level) {
        super(position, level, 16, 16, 0, 0, 0, 0, ObjectType.OBJECT_VEGGIE);
        Texture texture = null;
        switch (level.getWorldType()) {
            case BEACH:
                texture = (Texture) Assets.getInstance().get("sprites/objects/beach_veggies_16.png");
                break;
            default:
                texture = (Texture) Assets.getInstance().get("sprites/objects/Pickup_sheet_2.png");
                break;
        }
        TextureRegion[] sprites = TextureUtils.singleSplit(texture, 16, 16, false);
        this.sprite = new Sprite(sprites[MathUtils.random(0, MAX_VEGGIE)]);
        this.tweenHandler = new TweenHandler();
        this.sprite.setPosition(position.x, position.y);
        this.tweenBounds = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height / 3.33f);
        this.tweenHandler.createHoveringEffect(sprite, TweenEquations.easeInOutSine, tweenBounds, MathUtils.random(0.9f, 1.43f));
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isEaten) {
            sprite.draw(batch);
        }
    }

    @Override
    public void tick(float deltaTime, Player player) {
        this.tweenHandler.update(deltaTime);

        if (player.getBounds().overlaps(bounds) && !isEaten && !isIncreaseHealth) {
            player.increaseHealth(10.0f);
            tweenToHealthBar(player);
            isIncreaseHealth = true;
        }
    }

    @Override
    public void dispose() {
        tweenHandler.killTween(sprite);
    }

    public boolean isEaten() {
        return isEaten;
    }

    public boolean isIncreaseHealth() {
        return isIncreaseHealth;
    }

    protected void tweenToHealthBar(Player player) {
        if (!veggieSoundPlayed) {
            level.addVeggieCollected();
            if (level.isAllVeggiesCollected() && !CowPreferences.getInstance().getVeggieMedalAcquired(level.getCurrentLevelIndex(), level.getWorldType())) {
                AudioUtils.getInstance().playSoundFX("medal");
                ParticleHelper.getInstance().addChestEffect(position.x + (sprite.getWidth() / 2), position.y + (sprite.getHeight() / 2), true);
            } else {
                AudioUtils.getInstance().playSoundFX("veggie");
                if (randCount == EAT_MUNCH_TIME) {
                    AudioUtils.getInstance().playSoundFX("randomVeggie");
                    randCount = 0;
                } else {
                    randCount++;
                }
            }
            veggieSoundPlayed = true;
        }

        tweenHandler.killTween(sprite);
        OrthographicCamera camera = level.getCamera();
        float targetX = camera.position.x - (camera.viewportWidth / 2) + 100;
        float targetY = camera.position.y + (camera.viewportHeight / 2) - 20;
        tweenHandler.tweenToTarget(sprite, TweenEquations.easeNone, targetX, targetY, getVeggieCallback(player, targetX, targetY));

    }

    protected TweenCallback getVeggieCallback(final Player player, final float targetX, final float targetY) {
        return new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                ParticleHelper.getInstance().addVeggieEffect(targetX, targetY, true);
                isEaten = true;
            }
        };
    }

}
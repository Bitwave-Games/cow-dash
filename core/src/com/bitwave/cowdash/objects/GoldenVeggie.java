package com.bitwave.cowdash.objects;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.tween.TweenHandler;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;

import aurelienribon.tweenengine.TweenEquations;

public class GoldenVeggie extends Veggie {

    public GoldenVeggie(Vector2 position, Level level) {
        super(position, level);
        this.level = level;
        Texture texture = (Texture) Assets.getInstance().get("sprites/objects/golden_veggie_16x16.png");
        this.tweenHandler = new TweenHandler();
        this.sprite = new Sprite(texture);
        this.sprite.setPosition(position.x, position.y);
        this.tweenBounds = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height / 3.33f);
        this.tweenHandler.createHoveringEffect(sprite, TweenEquations.easeInOutSine, tweenBounds, MathUtils.random(0.9f, 1.43f));
    }

    @Override
    public void tick(float deltaTime, Player player) {
        this.tweenHandler.update(deltaTime);

        if (player.getBounds().overlaps(bounds) && !isEaten && !isIncreaseHealth) {
            player.setHealth(Player.MAX_HEALTH);
            tweenToHealthBar(player);
            isIncreaseHealth = true;
        }
    }

    @Override
    public void tweenToHealthBar(Player player) {
        if (!veggieSoundPlayed) {
            AudioUtils.getInstance().playSoundFX("veggie");
            veggieSoundPlayed = true;
        }
        tweenHandler.killTween(sprite);
        OrthographicCamera camera = level.getCamera();
        float targetX = camera.position.x - (camera.viewportWidth / 2) + 100;
        float targetY = camera.position.y + (camera.viewportHeight / 2) - 20;
        tweenHandler.tweenToTarget(sprite, TweenEquations.easeNone, targetX, targetY, getVeggieCallback(player, targetX, targetY));
    }


}

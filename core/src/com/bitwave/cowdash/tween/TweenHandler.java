package com.bitwave.cowdash.tween;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;

public class TweenHandler {

    private final TweenManager manager;

    public TweenHandler() {
        this.manager = new TweenManager();
    }

    public void createHoveringEffect(Sprite sprite, TweenEquation equation, Rectangle bounds, float duration) {
        SpriteAccessor spriteAccessor = new SpriteAccessor();
        int spriteXYOperation = spriteAccessor.XY;
        Tween.registerAccessor(Sprite.class, spriteAccessor);
        Timeline.createSequence().push(
                        Tween.to(sprite, spriteXYOperation, duration).target(bounds.x, bounds.y - (bounds.height)).ease(equation)).push(
                        Tween.to(sprite, spriteXYOperation, duration).target(bounds.x, bounds.y).ease(equation)).
                repeat(RepeatAction.FOREVER, 0).start(manager);
    }

    public void tweenToTarget(Sprite sprite, TweenEquation equation, float targetX, float targetY, TweenCallback callback) {
        SpriteAccessor spriteAccessor = new SpriteAccessor();

        Vector2 spritePos = new Vector2(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
        Vector2 targetPos = new Vector2(targetX, targetY);
        final float SPEED = .002f;
        float distance = spritePos.dst(targetPos);
        float duration = distance * SPEED;
        Tween.registerAccessor(Sprite.class, spriteAccessor);
        Timeline.createSequence().push(
                Tween.to(sprite, spriteAccessor.XY, duration).target(targetX, targetY).ease(equation)).start(manager).setCallback(callback);
    }

    public void tweenToTarget(Sprite sprite, TweenEquation equation, float targetX, float targetY, float duration, TweenCallback callback) {
        SpriteAccessor spriteAccessor = new SpriteAccessor();

        Tween.registerAccessor(Sprite.class, spriteAccessor);
        Timeline.createSequence().push(
                Tween.to(sprite, spriteAccessor.XY, duration).target(targetX, targetY).ease(equation)).start(manager).setCallback(callback);
    }

    public void createCircularHoveringEffect(Sprite sprite, TweenEquation equation, Rectangle bounds, float duration, float distance) {
        SpriteAccessor spriteAccessor = new SpriteAccessor();
        int spriteXYOperation = spriteAccessor.XY;

        Tween.registerAccessor(Sprite.class, spriteAccessor);
        Timeline.createSequence().push(Tween.set(sprite, spriteXYOperation).target(bounds.x, bounds.y - distance)).push(
                        Tween.to(sprite, spriteXYOperation, duration).target(bounds.x + distance, bounds.y).ease(equation)).push(
                        Tween.to(sprite, spriteXYOperation, duration).target(bounds.x, bounds.y + distance).ease(equation)).push(
                        Tween.to(sprite, spriteXYOperation, duration).target(bounds.x - distance, bounds.y).ease(equation)).push(
                        Tween.to(sprite, spriteXYOperation, duration).target(bounds.x, bounds.y - distance).ease(equation)).
                repeat(RepeatAction.FOREVER, 0).start(manager);
    }

    public void killTween(Object target) {
        if (manager.containsTarget(target)) {
            manager.killTarget(target);
        }
    }

    public void update(float deltaTime) {
        manager.update(deltaTime);
    }


}

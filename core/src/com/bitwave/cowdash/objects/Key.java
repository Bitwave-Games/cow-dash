package com.bitwave.cowdash.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.tween.TweenHandler;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.ParticleHelper;

import aurelienribon.tweenengine.TweenEquations;

public class Key extends GameObject implements Disposable {

    private final String typeOfKey;
    private final Sprite keySprite;
    private final TweenHandler tweenHandler;

    private boolean isTaken;

    public Key(Vector2 position, Level level, String typeOfKey) {
        super(position, level, 16, 16, ObjectType.OBJECT_KEY);
        this.typeOfKey = typeOfKey;
        this.keySprite = getKeySprite();
        this.keySprite.setPosition(position.x, position.y);
        this.tweenHandler = new TweenHandler();
        this.tweenHandler.createHoveringEffect(keySprite, TweenEquations.easeInOutSine, new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height / 4.33f), MathUtils.random(0.9f, 1.43f));
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isTaken) {
            keySprite.draw(batch);
        }
    }


    @Override
    public void tick(float deltaTime, Player player) {
        this.tweenHandler.update(deltaTime);

        if (player.getBounds().overlaps(bounds)) {
            if (!isTaken) {
                if (typeOfKey.equalsIgnoreCase("blue")) {
                    player.addBluewKey();
                    ParticleHelper.getInstance().addKeyBlueEffect(position.x + keySprite.getWidth() / 2, position.y + keySprite.getHeight() / 2, true);
                } else if (typeOfKey.equalsIgnoreCase("red")) {
                    player.addRedKey();
                    ParticleHelper.getInstance().addKeyRedEffect(position.x + keySprite.getWidth() / 2, position.y + keySprite.getHeight() / 2, true);
                } else if (typeOfKey.equalsIgnoreCase("yellow")) {
                    player.addYellowKey();
                    ParticleHelper.getInstance().addKeyYellowEffect(position.x + keySprite.getWidth() / 2, position.y + keySprite.getHeight() / 2, true);
                }

                AudioUtils.getInstance().playSoundFX("getkey");
                isTaken = true;
            }
        }
    }

    private Sprite getKeySprite() {
        String texturePath = null;

        if (typeOfKey.equalsIgnoreCase("blue")) {
            texturePath = "sprites/objects/keys/key_blue.png";
        } else if (typeOfKey.equalsIgnoreCase("red")) {
            texturePath = "sprites/objects/keys/key_red.png";
        } else if (typeOfKey.equalsIgnoreCase("yellow")) {
            texturePath = "sprites/objects/keys/key_yellow.png";
        }
        return Assets.getInstance().getSprite(texturePath);
    }

    @Override
    public void dispose() {

    }

}
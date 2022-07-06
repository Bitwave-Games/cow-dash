package com.bitwave.cowdash.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.tween.TweenHandler;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.ParticleHelper;

import aurelienribon.tweenengine.TweenEquations;

public class Ruby extends GameObject implements Disposable {

    private final float ANIM_SPEED_SPARKLE = .2f;

    private final Animation sparkleAnimation;
    private final Sprite rubySprite;
    private final Sprite shaderSprite;
    private final Rectangle innerBounds;
    private final TweenHandler tweenHandler;

    public Ruby(Level level, Vector2 position) {
        super(position, level, ObjectType.OBJECT_RUBY);
        this.sparkleAnimation = getFramesFromRegion(new TextureRegion((Texture) Assets.getInstance().get("sprites/objects/win_orb_spritesheet_bw_strip9.png")), ANIM_SPEED_SPARKLE, 32, 32, false);
        this.currentFrame = (TextureRegion) sparkleAnimation.getKeyFrame(0);
        this.rubySprite = new Sprite(currentFrame);
        this.rubySprite.setColor(0.7607843137254902f, 0.3529411764705882f, 0.9098039215686275f, 1);
        this.bounds.set(position.x + 8, position.y + 8, 16, 16);
        this.innerBounds = new Rectangle(position.x - 12, position.y - 12, 56, 56);
        this.shaderSprite = new Sprite((Texture) Assets.getInstance().get("sprites/objects/portal_gradient.png"));
        this.shaderSprite.setColor(rubySprite.getColor());
        this.rubySprite.setPosition(position.x, position.y);

        ParticleHelper.getInstance().addPortalEffect(bounds.x + rubySprite.getWidth() / 3f, bounds.y + rubySprite.getHeight() / 3f, true);

        this.shaderSprite.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        this.tweenHandler = new TweenHandler();
        this.tweenHandler.createHoveringEffect(rubySprite, TweenEquations.easeInOutSine, new Rectangle(bounds.x - 8, bounds.y - 8, 0, 8), 1.53f);
    }

    @Override
    public void render(SpriteBatch batch) {
        shaderSprite.draw(batch);
        rubySprite.draw(batch);
    }

    public Rectangle getInnerBounds() {
        return innerBounds;
    }

    @Override
    public void tick(float deltatime, Player player) {
        animate(deltatime);
        tweenHandler.update(deltatime);
        rubySprite.setRegion(currentFrame);
    }

    @Override
    protected void animate(float deltaTime) {
        super.animate(deltaTime);
        currentFrame = (TextureRegion) sparkleAnimation.getKeyFrame(stateTime, true);
    }

    @Override
    public void dispose() {

    }

}
package com.bitwave.cowdash.utils.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bitwave.cowdash.CowDash;
import com.bitwave.cowdash.objects.AICow;
import com.bitwave.cowdash.utils.TextureUtils;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public class ButtonWithACow extends Image {

    private boolean allowedToDrawLevelMedals = true;

    private AICow aiCow;

    private final Animation hoveringAnimation;

    private TextureRegion currentFrame;

    private final TextureRegion stillImage;

    private float stateTime;

    private final TextureRegion timeMedalImage;
    private final TextureRegion veggieMedalImage;
    private final TextureRegion treasureMedalImage;

    public ButtonWithACow(WorldType worldType, Skin skin, byte levelIndex) {

        this.aiCow = new AICow(this.getX(), this.getY(), 32, 32, true);
        this.aiCow.setVisible(false);

        byte stillImageIndex = 4;
        TextureRegion buttonSpriteSheet = null;
        CowPreferences cowPreferences = CowPreferences.getInstance();

        TextureRegion unlocked = new TextureRegion(skin.getRegion("level_select_small_medal_taken"));
        TextureRegion locked = new TextureRegion(skin.getRegion("level_select_small_medal_not_taken"));

        if (cowPreferences.getTimeMedalAcquired(levelIndex, worldType)) {
            this.timeMedalImage = new TextureRegion(unlocked);
        } else {
            this.timeMedalImage = new TextureRegion(locked);
        }

        if (cowPreferences.getVeggieMedalAcquired(levelIndex, worldType)) {
            this.veggieMedalImage = new TextureRegion(unlocked);
        } else {
            this.veggieMedalImage = new TextureRegion(locked);
        }

        if (cowPreferences.getChestMedalAcquired(levelIndex, worldType)) {
            this.treasureMedalImage = new TextureRegion(unlocked);
        } else {
            this.treasureMedalImage = new TextureRegion(locked);
        }

        if (levelIndex == 0 || cowPreferences.isLevelUnlocked(levelIndex, worldType) || CowDash.ALL_LEVELS_UNLOCKED) {
            if (cowPreferences.isEverythingUnlockedForLevel(levelIndex, worldType)) {
                buttonSpriteSheet = new TextureRegion(skin.getRegion("level_select_button_gold_strip6"));
            } else {
                buttonSpriteSheet = new TextureRegion(skin.getRegion("level_select_button_grey_strip6"));
            }
        } else {
            this.allowedToDrawLevelMedals = false;
            buttonSpriteSheet = new TextureRegion(skin.getRegion("level_select_button_unfinnished"));
            stillImageIndex = 0;
            this.setTouchable(Touchable.disabled);
        }

        this.hoveringAnimation = getFramesFromRegion(buttonSpriteSheet, .122f, 32, 32);
        this.currentFrame = (TextureRegion) hoveringAnimation.getKeyFrame(0);
        this.stillImage = (TextureRegion) hoveringAnimation.getKeyFrames()[stillImageIndex];
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        aiCow.setPosition(x, y);
    }

    public void reloadTexture() {
        this.aiCow = new AICow(this.getX(), this.getY(), 32, 32, true);
        this.aiCow.setVisible(false);
    }

    public boolean isCowVisible() {
        return this.aiCow.isVisible();
    }

    public void setCowVisible(boolean visible) {
        this.aiCow.setVisible(visible);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!aiCow.isVisible()) {
            batch.draw(currentFrame, this.getX(), this.getY());
        } else {
            batch.draw(stillImage, this.getX(), this.getY());
            batch.draw(aiCow.getCurrentFrame(), this.getX(), this.getY() + 8);
        }

        if (allowedToDrawLevelMedals) {
            batch.draw(timeMedalImage, this.getX() + 8, this.getY() - 12);
            batch.draw(veggieMedalImage, this.getX() - 8, this.getY() - 12);
            batch.draw(treasureMedalImage, this.getX() + 24, this.getY() - 12);
        }
    }

    @Override
    public void act(float delta) {
        aiCow.act(delta);
        animate(delta);
    }

    private void animate(float delta) {
        stateTime += delta;
        currentFrame = (TextureRegion) hoveringAnimation.getKeyFrame(stateTime, true);
    }

    private Animation getFramesFromRegion(TextureRegion spriteSheet, float animSpeed, int width, int height) {
        TextureRegion[] region = TextureUtils.singleSplit(spriteSheet, width, height, false);
        return new Animation(animSpeed, region);
    }


}
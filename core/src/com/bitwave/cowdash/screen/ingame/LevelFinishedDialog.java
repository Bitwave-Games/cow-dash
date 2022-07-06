package com.bitwave.cowdash.screen.ingame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.objects.AICow;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.CustomButton;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;
import com.bitwave.cowdash.utils.ui.AnimatedActor;
import com.bitwave.cowdash.utils.ui.MovingStar;

public class LevelFinishedDialog extends Dialog {

    private final byte currentLevelIndex;
    private AICow aICow;
    private Image medalShadow1;
    private Image medalShadow2;
    private Image medalShadow3;
    private Image largeVeggieMedalImage;
    private Image largeTimeMedalImage;
    private Image largeTreasureMedalImage;

    private boolean isVeggieMedalAlreadyTaken;
    private boolean isTimeMedalAlreadyTaken;
    private boolean isTreasureMedalAlreadyTaken;

    private AnimatedActor veggieMedalActor;
    private AnimatedActor timeMedalActor;
    private AnimatedActor treasureMedalActor;
    private Label timeLimitLabel;

    private final float dialogLabelYPos;

    public LevelFinishedDialog(Level level) {
        super(level);

        this.fadeOutAndHideWhiteCanvas(0.0f);
        this.dialogLabelYPos = 201.5f;
        this.currentLevelIndex = level.getCurrentLevelIndex();
        this.dialogImage = new Image(skin.getDrawable("win_screen_bg"));

        if (!level.isFinalLevel()) {
            CowPreferences.getInstance().saveCurrentLevelIndex((byte) (1 + level.getCurrentLevelIndex()), level.getWorldType());
            CowPreferences.getInstance().unlockLevel((byte) (level.getCurrentLevelIndex() + 1), level.getWorldType());
        }

        if (level.isItemUnlocked()) {
            showItemUnlocked();
        } else {
            setupDefaultStage();
        }

        persistData();
    }

    @Override
    public void tick(float deltaTime) {
        super.tick(deltaTime);
    }

    @Override
    public void resetContext() {
        showItemUnlocked();
    }

    @Override
    protected void setInitialPositions() {
        float width = worldWidth / 2;

        float size = 800;
        float halfSize = size / 2;

        if (level.isAllVeggiesCollected() && !isVeggieMedalAlreadyTaken) {
            largeVeggieMedalImage.setSize(size, size);
            largeVeggieMedalImage.setPosition(width - halfSize, width - halfSize);
        }
        if (level.isTimeBeaten() && !isTimeMedalAlreadyTaken) {
            largeTimeMedalImage.setSize(size, size);
            largeTimeMedalImage.setPosition(width - halfSize, width - halfSize);
        }
        if (level.isItemUnlocked() && !isTreasureMedalAlreadyTaken) {
            largeTreasureMedalImage.setSize(size, size);
            largeTreasureMedalImage.setPosition(width - halfSize, width - halfSize);
        }

        this.dialogImage.setPosition(width - dialogImage.getPrefWidth() / 2, worldHeight - dialogImage.getHeight() + worldHeight);
        this.onDialogLabel.setPosition(width - (onDialogLabel.getPrefWidth() / 2), worldHeight - dialogImage.getHeight() + dialogLabelYPos + worldHeight);
        this.backToLevelSelectButton.setPosition(dialogImage.getX() + 194, worldHeight - dialogImage.getHeight() + 59 + worldHeight);
        this.replayLevelButton.setPosition(dialogImage.getX() + 258, worldHeight - dialogImage.getHeight() + 59 + worldHeight);
        this.nextLevelButton.setPosition(dialogImage.getX() + 322, worldHeight - dialogImage.getHeight() + 59 + worldHeight);
        this.myCowButton.setPosition(dialogImage.getX() + 34, worldHeight - dialogImage.getHeight() + 59 + worldHeight);

        this.medalShadow1.setPosition(dialogImage.getX() + 64, worldHeight - dialogImage.getHeight() + 124 + worldHeight);
        this.medalShadow2.setPosition(dialogImage.getX() + 160, worldHeight - dialogImage.getHeight() + 124 + worldHeight);
        this.medalShadow3.setPosition(dialogImage.getX() + 256, worldHeight - dialogImage.getHeight() + 124 + worldHeight);

        this.veggieMedalActor.setPosition(dialogImage.getX() + 64, worldHeight - dialogImage.getHeight() + 124 + worldHeight);
        this.timeMedalActor.setPosition(dialogImage.getX() + 160, worldHeight - dialogImage.getHeight() + 124 + worldHeight);
        this.treasureMedalActor.setPosition(dialogImage.getX() + 256, worldHeight - dialogImage.getHeight() + 124 + worldHeight);

        veggieMedalActor.setVisible(isVeggieMedalAlreadyTaken);
        timeMedalActor.setVisible(isTimeMedalAlreadyTaken);
        treasureMedalActor.setVisible(isTreasureMedalAlreadyTaken);


        timeLimitLabel.setZIndex(1);
        timeLimitLabel.setVisible(true);

        this.timeLimitLabel.setPosition(width - timeLimitLabel.getPrefWidth() / 2 - 8.75f, worldHeight - dialogImage.getHeight() + 181 + worldHeight);
    }

    @Override
    protected void animateStage(boolean down, final byte ACTION) {
        this.largeVeggieMedalImage.addAction(Actions.fadeOut(0.0f));
        this.largeTimeMedalImage.addAction(Actions.fadeOut(0.0f));
        this.largeTreasureMedalImage.addAction(Actions.fadeOut(0.0f));
        this.largeVeggieMedalImage.setVisible(false);
        this.largeTimeMedalImage.setVisible(false);
        this.largeTreasureMedalImage.setVisible(false);

        final float duration = 0.75f;
        Interpolation interpolation = Interpolation.sine;

        this.dialogImage.addAction(Actions.moveTo(dialogImage.getX(), down ? worldHeight - dialogImage.getHeight() : worldHeight, duration, interpolation));
        this.onDialogLabel.addAction(Actions.moveTo(onDialogLabel.getX(), down ? worldHeight - dialogImage.getHeight() + dialogLabelYPos : dialogLabelYPos + worldHeight, duration, interpolation));

        this.backToLevelSelectButton.addAction(Actions.moveTo(backToLevelSelectButton.getX(), down ? worldHeight - dialogImage.getHeight() + 59 : 59 + worldHeight, duration, interpolation));
        this.replayLevelButton.addAction(Actions.moveTo(replayLevelButton.getX(), down ? worldHeight - dialogImage.getHeight() + 59 : 59 + worldHeight, duration, interpolation));
        this.nextLevelButton.addAction(Actions.moveTo(nextLevelButton.getX(), down ? worldHeight - dialogImage.getHeight() + 59 : 59 + worldHeight, duration, interpolation));
        this.myCowButton.addAction(Actions.moveTo(myCowButton.getX(), down ? worldHeight - dialogImage.getHeight() + 59 : 59 + worldHeight, duration, interpolation));

        this.medalShadow1.addAction(Actions.moveTo(medalShadow1.getX(), down ? worldHeight - dialogImage.getHeight() + 124 : 124 + worldHeight, duration, interpolation));
        this.medalShadow2.addAction(Actions.moveTo(medalShadow2.getX(), down ? worldHeight - dialogImage.getHeight() + 124 : 124 + worldHeight, duration, interpolation));
        this.medalShadow3.addAction(Actions.moveTo(medalShadow3.getX(), down ? worldHeight - dialogImage.getHeight() + 124 : 124 + worldHeight, duration, interpolation));

        this.veggieMedalActor.addAction(Actions.moveTo(veggieMedalActor.getX(), down ? worldHeight - dialogImage.getHeight() + 124 : 124 + worldHeight, duration, interpolation));
        this.timeMedalActor.addAction(Actions.moveTo(timeMedalActor.getX(), down ? worldHeight - dialogImage.getHeight() + 124 : 124 + worldHeight, duration, interpolation));
        this.treasureMedalActor.addAction(Actions.moveTo(treasureMedalActor.getX(), down ? worldHeight - dialogImage.getHeight() + 124 : 124 + worldHeight, duration, interpolation));

        this.timeLimitLabel.addAction(Actions.sequence(Actions.moveTo(timeLimitLabel.getX(), down ? worldHeight - dialogImage.getHeight() + 128 : 128 + worldHeight, duration, interpolation), Actions.run(new Runnable() {

            @Override
            public void run() {
                if (ACTION != NONE) {
                    persistData();
                    fadeOutScreen(ACTION);
                } else {
                    final AudioUtils audioUtils = AudioUtils.getInstance();
                    final float medalDuration = .55f;
                    final Interpolation medalInterpolation = Interpolation.sine;
                    final Interpolation fadeInterPolation = Interpolation.exp5Out;

                    largeVeggieMedalImage.setVisible(level.isAllVeggiesCollected() && !isVeggieMedalAlreadyTaken);
                    largeVeggieMedalImage.addAction(Actions.fadeIn(isVeggieMedalAlreadyTaken ? 0.0f : medalDuration, fadeInterPolation));
                    largeVeggieMedalImage.addAction(Actions.moveTo(veggieMedalActor.getX(), worldHeight - dialogImage.getHeight() + 124, isVeggieMedalAlreadyTaken ? 0.0f : medalDuration));
                    largeVeggieMedalImage.addAction(Actions.sequence(Actions.sizeTo(64, 64, isVeggieMedalAlreadyTaken ? 0.0f : medalDuration, medalInterpolation), Actions.run(new Runnable() {

                        @Override
                        public void run() {

                            if (largeVeggieMedalImage.isVisible()) {
                                audioUtils.playSoundFX("medalsHitBoard");
                                ParticleHelper.getInstance().addMedalEffect(veggieMedalActor.getX() + 32, worldHeight - dialogImage.getHeight() + 156, true);
                            }
                            veggieMedalActor.setVisible(level.isAllVeggiesCollected() || isVeggieMedalAlreadyTaken);
                            largeTimeMedalImage.setVisible(level.isTimeBeaten() && !isTimeMedalAlreadyTaken);
                            largeTimeMedalImage.addAction(Actions.fadeIn(isTimeMedalAlreadyTaken ? 0.0f : medalDuration, fadeInterPolation));
                            largeTimeMedalImage.addAction(Actions.moveTo(timeMedalActor.getX(), worldHeight - dialogImage.getHeight() + 124, isTimeMedalAlreadyTaken ? 0.0f : medalDuration));
                            largeTimeMedalImage.addAction(Actions.sequence(Actions.sizeTo(64, 64, isTimeMedalAlreadyTaken ? 0.0f : medalDuration, medalInterpolation), Actions.run(new Runnable() {

                                @Override
                                public void run() {
                                    if (largeTimeMedalImage.isVisible()) {
                                        audioUtils.playSoundFX("medalsHitBoard");
                                        ParticleHelper.getInstance().addMedalEffect(largeTimeMedalImage.getX() + 32, worldHeight - dialogImage.getHeight() + 156, true);
                                    }
                                    timeMedalActor.setVisible(level.isTimeBeaten() || isTimeMedalAlreadyTaken);
                                    timeLimitLabel.setVisible(true);
                                    largeTreasureMedalImage.setVisible(level.isItemUnlocked() && !isTreasureMedalAlreadyTaken);
                                    largeTreasureMedalImage.addAction(Actions.fadeIn(isTreasureMedalAlreadyTaken ? 0.0f : medalDuration, fadeInterPolation));
                                    largeTreasureMedalImage.addAction(Actions.moveTo(treasureMedalActor.getX(), worldHeight - dialogImage.getHeight() + 124, isTreasureMedalAlreadyTaken ? 0.0f : medalDuration));
                                    largeTreasureMedalImage.addAction(Actions.sequence(Actions.sizeTo(64, 64, isTreasureMedalAlreadyTaken ? 0.0f : medalDuration, medalInterpolation), Actions.run(new Runnable() {

                                        @Override
                                        public void run() {
                                            if (largeTreasureMedalImage.isVisible()) {
                                                audioUtils.playSoundFX("medalsHitBoard");
                                                ParticleHelper.getInstance().addMedalEffect(largeTreasureMedalImage.getX() + 32, worldHeight - dialogImage.getHeight() + 156, true);
                                            }

                                            timeLimitLabel.setZIndex(stage.getActors().size - 1);
                                            treasureMedalActor.setVisible(level.isItemUnlocked() || isTreasureMedalAlreadyTaken);
                                        }
                                    })));
                                }
                            })));
                        }
                    })));
                }
            }
        })));
    }

    private void setupDefaultStage() {
        CowPreferences cowPrefs = CowPreferences.getInstance();

        this.medalShadow1 = new Image(skin.getDrawable("medal_shadow"));
        this.medalShadow2 = new Image(skin.getDrawable("medal_shadow"));
        this.medalShadow3 = new Image(skin.getDrawable("medal_shadow"));

        this.largeVeggieMedalImage = new Image(skin.getDrawable("veggie_medal_1frame"));
        this.largeTimeMedalImage = new Image(skin.getDrawable("time_medal_1frame"));
        this.largeTreasureMedalImage = new Image(skin.getDrawable("treasure_medal_1frame"));

        WorldType worldType = level.getWorldType();
        this.isVeggieMedalAlreadyTaken = cowPrefs.getVeggieMedalAcquired(currentLevelIndex, worldType);
        this.isTimeMedalAlreadyTaken = cowPrefs.getTimeMedalAcquired(currentLevelIndex, worldType);
        this.isTreasureMedalAlreadyTaken = cowPrefs.getChestMedalAcquired(currentLevelIndex, worldType);

        this.veggieMedalActor = new AnimatedActor(skin.getRegion("medal_veggie_spritesheet_colored"), .35f, 64, 64, false);
        this.timeMedalActor = new AnimatedActor(skin.getRegion("medal_time_spritesheet_colored"), .23f, 64, 64, false);
        this.treasureMedalActor = new AnimatedActor(skin.getRegion("medal_treasure_spritesheet_colored"), .20f, 64, 64, false);

        Translator translator = Translator.getInstance();
        byte indexToDisplay = (byte) (1 + level.getCurrentLevelIndex());
        float scale = 1.0f;
        String translatedLevelName = translator.t("level");
        String translatedClearedName = translator.t("cleared") + "!";

        LabelStyle brownColorLabelStyle_16 = new LabelStyle((BitmapFont) Assets.getInstance().get("fonts/cowfont_16.fnt"), new Color(101 / 255f, 79 / 255f, 33 / 255f, 1f));
        this.onDialogLabel = new Label(translatedLevelName + " " + indexToDisplay + " " + translatedClearedName, brownColorLabelStyle_16);
        this.onDialogLabel.setFontScale(scale);
        this.nextLevelButton = new CustomButton(skin.getDrawable("winscreen_next_level_normal"), skin.getDrawable("winscreen_next_level"));
        this.backToLevelSelectButton = new CustomButton(skin.getDrawable("winscreen_level_select_normal"), skin.getDrawable("winscreen_level_select"));
        this.myCowButton = new CustomButton(skin.getDrawable("winscreen_my_cow_normal"), skin.getDrawable("winscreen_my_cow"));
        this.replayLevelButton = new CustomButton(skin.getDrawable("winscreen_replay_normal"), skin.getDrawable("winscreen_replay"));

        Image blackImage = new Image(skin.getDrawable("whiterect"));
        blackImage.setColor(0, 0, 0, 0.5f);
        blackImage.setBounds(0, 0, worldWidth, worldHeight);
        this.stage.addActor(blackImage);

        String stringTime = "" + level.getTimeToBeat() + "0";

        if (stringTime.length() > 5) {
            stringTime = stringTime.substring(0, stringTime.length() - 2);
        }

        String secs = stringTime.substring(0, stringTime.indexOf('.'));
        float timeLimit = level.getTimeToBeat() * 1000;

        int seconds = Integer.parseInt(secs);
        int hundreds = (int) ((timeLimit / 10) % 100);

        BitmapFont bitmapFont_10 = (BitmapFont) Assets.getInstance().get("fonts/vag-10.fnt");
        LabelStyle whiteColorTimeLabelStyle_10 = new LabelStyle(bitmapFont_10, Color.WHITE);
        LabelStyle blackColorTimeLabelStyle_10 = new LabelStyle(bitmapFont_10, Color.BLACK);

        LabelStyle timeLabelStyle = whiteColorTimeLabelStyle_10;
        if (level.isTimeBeaten() || isTimeMedalAlreadyTaken) {
            timeLabelStyle = blackColorTimeLabelStyle_10;
        }
        this.timeLimitLabel = new Label(String.format("", seconds, hundreds), timeLabelStyle);

        setButtonListener(nextLevelButton, NEXT_INCREASE_LEVEL_COUNTER);
        setButtonListener(replayLevelButton, REPLAY_INCREASE_LEVEL_COUNTER);
        setButtonListener(backToLevelSelectButton, LEVEL_SELECT_INCREASE_LEVEL_COUNTER);
        setButtonListener(myCowButton, MY_COW);

        addActorsToStage(dialogImage, backToLevelSelectButton, replayLevelButton, nextLevelButton, onDialogLabel, myCowButton,
                medalShadow1, medalShadow2, medalShadow3, timeLimitLabel, largeTimeMedalImage, largeTreasureMedalImage, largeVeggieMedalImage,
                veggieMedalActor, timeMedalActor, treasureMedalActor, whiteCanvasImage);

        this.setInitialPositions();

        this.animateStage(true, NONE);
    }

    private void persistData() {
        CowPreferences cowPrefs = CowPreferences.getInstance();
        WorldType worldType = level.getWorldType();

        if (level.isAllVeggiesCollected()) {
            if (!cowPrefs.getVeggieMedalAcquired(currentLevelIndex, worldType)) {
                cowPrefs.setVeggieMedalAcquired(currentLevelIndex, worldType);
            }
        }
        if (level.isTimeBeaten()) {
            if (!cowPrefs.getTimeMedalAcquired(currentLevelIndex, worldType)) {
                cowPrefs.setTimeMedalAcquired(currentLevelIndex, worldType);
            }
        }
        if (level.isItemUnlocked()) {
            if (!cowPrefs.getChestMedalAcquired(currentLevelIndex, worldType)) {
                cowPrefs.setChestMedalAcquired(currentLevelIndex, worldType);
            }
        }

        cowPrefs.updateUnlockedWorlds();
    }

    private void showItemUnlocked() {
        float width = worldWidth / 2;

        AudioUtils.getInstance().crossFadeTracks(nameOfTrackToPlay, "boss", 10f, false);
        AudioUtils.getInstance().playSoundFX("mooh");

        final Image blackBackGroundImage = new Image(skin.getDrawable("whiterect"));
        final Image whiteBannerImage = new Image(skin.getDrawable("unlock_screen_bw"));

        String[] sparkleType = {
                "sparkly_sparkle_red_16x16_strip8",
                "sparkly_sparkle_yellow_16x16_strip8",
                "sparkly_sparkle_green_16x16_strip8"
        };

        final Array<MovingStar> sparkleActors = new Array<MovingStar>();
        float x = 16f;
        float y = 16f;
        for (byte i = 0; i < 20; i++) {
            sparkleActors.add(new MovingStar(skin.getRegion(sparkleType[MathUtils.random(2)]), MathUtils.random(.025f, .1f), x, y, 16, 16, 600f));
            sparkleActors.get(i).setPosition(x, y);
            x += 32;
            y += 32;
            if (x > worldWidth - 16) {
                x = worldWidth;
            }
            if (y > worldHeight - 16) {
                y = 16;
            }
        }

        blackBackGroundImage.setColor(0, 0, 0, 1);
        blackBackGroundImage.setBounds(0, 0, worldWidth, worldHeight);
        String itemUnlockedType = level.getItemUnlockedType();
        byte itemIndex = level.getItemUnlockedNr();

        Translator translator = Translator.getInstance();
        String unlockedLabelText = translator.t("unlocked");
        final Label headerLabel = new Label(unlockedLabelText, whiteColorLabelStyle_16);
        final Label itemUnlockedLabel = new Label(ItemUtils.getItem(itemUnlockedType, itemIndex).toString() + "!", whiteColorLabelStyle_16);
        headerLabel.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.fadeOut(0.1f), Actions.delay(0.6f), Actions.fadeIn(0.1f), Actions.delay(0.6f))));
        itemUnlockedLabel.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.fadeOut(0.1f), Actions.delay(0.6f), Actions.fadeIn(0.1f), Actions.delay(0.6f))));

        this.aICow = new AICow(width - 16, (worldHeight / 2) - 16, itemUnlockedType, itemIndex, true, true);
        whiteBannerImage.setBounds(0, aICow.getY() - 20, worldWidth, whiteBannerImage.getHeight());

        headerLabel.setPosition(width - (headerLabel.getPrefWidth() / 2), getCorectY(headerLabel, whiteBannerImage.getY() - (headerLabel.getPrefHeight() / 2)));
        itemUnlockedLabel.setPosition(width - (itemUnlockedLabel.getPrefWidth() / 2),
                whiteBannerImage.getY() - (itemUnlockedLabel.getPrefHeight() * 1.4f));

        CowPreferences.getInstance().unlockItem(level.getItemUnlockedType(), level.getItemUnlockedNr());

        stage.addActor(blackBackGroundImage);
        for (AnimatedActor animatedActor : sparkleActors) {
            stage.addActor(animatedActor);
        }
        addActorsToStage(whiteBannerImage, aICow, headerLabel, itemUnlockedLabel);
        blackBackGroundImage.setZIndex(0);


        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stage.removeListener(this);
                setupDefaultStage();
                return true;
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
    }


}
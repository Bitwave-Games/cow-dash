package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.bitwave.cowdash.CowDash;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;
import com.bitwave.cowdash.utils.ui.ButtonWithACow;

public class LevelSelectScreen extends MenuBaseScreen {

    private final byte NR_OF_BUTTONS = 20;
    private final byte PLAY = 0;
    private final byte BACK = 1;
    private final byte NO_ACTION = -1;
    private final WorldType worldType;
    private boolean[] buttonsClicked;
    private boolean levelButtonTouched;
    private boolean isScrollPaneSet;
    private byte currentIndex;
    private ScrollPane scrollPane;
    private ButtonWithACow[] buttons;
    private Label levelSpecificLabel;
    private Image topImage;
    private Image bottomImage;
    private Image backBoardImage;
    private Image levelBoardImage;
    private Image whiteCanvasImage;
    private Button playButton;
    private Button backToMenuButton;

    public LevelSelectScreen(Game game, boolean shouldPlayMusic) {
        super(game, BackButtonAlternatives.BACK_TO_WORLD_SELECT);
        CowPreferences cowPreferences = CowPreferences.getInstance();
        this.worldType = WorldType.getValue(cowPreferences.getCurrentWorld());
        this.currentIndex = cowPreferences.getCurrentLevelSelection(worldType);
        if (shouldPlayMusic) {
            AudioUtils.getInstance().playMusic("menu", true);
        }
        this.setupButtons();
        stage.setKeyboardFocus(buttons[this.currentIndex]);
        mountainsImage.setSpeed(0f);
        grassHillsImage.setSpeed(0f);
    }

    @Override
    public void resume() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].reloadTexture();
        }
        buttons[currentIndex].setCowVisible(true);
        super.resume();
    }

    @Override
    public void tick(float delta) {
        float previousScrollPercentX = scrollPane.getVisualScrollX() / scrollPane.getMaxX();
        stage.act(delta);
        float newScrollPercentX = scrollPane.getVisualScrollX() / scrollPane.getMaxX();
        float amountScrollPercentX = newScrollPercentX - previousScrollPercentX;
        mountainsImage.scroll(amountScrollPercentX * 0.8f, 0f);
        grassHillsImage.scroll(amountScrollPercentX, 0f);

        if (!isScrollPaneSet) {
            scrollPane.scrollTo(getVisibleCowsXPosition(), worldHeight / 2, 32, 32, true, false);
            isScrollPaneSet = true;
        }

        if (buttons != null && buttons.length > 0) {
            displayOrHideLevelInfo();
        }
        ParticleHelper.getInstance().tick(delta, true);
    }


    private void displayOrHideLevelInfo() {
        if (scrollPane.isFlinging() || scrollPane.isPanning()) {

            levelButtonTouched = false;
            levelSpecificLabel.setVisible(false);
            showOrHideCowOnButton(currentIndex, false);

            this.animateSpecificParts(false);

        } else {
            if (levelButtonTouched && !scrollPane.isFlinging() && !scrollPane.isPanning()) {
                Translator translator = Translator.getInstance();
                String levelLabelText = translator.t("level") + " " + (1 + currentIndex);
                for (byte i = 0; i < NR_OF_BUTTONS; i++) {
                    if (buttonsClicked[i]) {
                        if (!levelSpecificLabel.getText().toString().equals(levelLabelText)) {
                            levelSpecificLabel.setText(levelLabelText);
                        }
                        levelSpecificLabel.setVisible(true);
                        showOrHideCowOnButton(currentIndex, true);
                        return;
                    }
                }
            }
        }
    }

    private void setButtonListener(final Actor actor, final byte ACTION) {
        actor.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y,
                                int pointer, int button) {
                if (event.getListenerActor().hit(x, y, true) != null) {
                    ParticleHelper.getInstance().addWoodenEffect(event.getStageX(), event.getStageY(), true);
                    AudioUtils.getInstance().playSoundFX("button");
                    switch (ACTION) {
                        case PLAY:
                            animateStage(false, PLAY);
                            break;
                        case BACK:
                            scrollPane.setTouchable(Touchable.disabled);
                            if (scrollPane.isFlinging() || scrollPane.isDragging()) {
                                scrollPane.cancel();
                                stage.getActors().removeValue(levelBoardImage, true);
                                stage.getActors().removeValue(levelSpecificLabel, true);
                            }
                            animateStage(false, BACK);
                            break;
                        default:
                            break;
                    }
                }
            }


        });
    }

    private void fadeOutAndSwitchScreen(final byte actionPerformedOnceFinished) {
        whiteCanvasImage.setVisible(true);
        whiteCanvasImage.setColor(1, 1, 1, 0);
        whiteCanvasImage.setZIndex(stage.getActors().size - 1);
        whiteCanvasImage.addAction(Actions.sequence(Actions.fadeIn(.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                switch (actionPerformedOnceFinished) {
                    case BACK:
                        game.setScreen(new WorldSelectScreen(game, true));
                        break;
                    case PLAY:
                        AudioUtils.getInstance().fadeOutTrack("menu", 10f, true);
                        game.setScreen(new LoadingScreen(game, ScreenType.GAME_SCREEN, currentIndex));
                        break;
                }
            }
        })));
    }

    private void setupButtons() {
        CowPreferences cowPreferences = CowPreferences.getInstance();
        Assets assets = Assets.getInstance();

        Table table = new Table();
        Drawable bg = new Image((Texture) assets.get(worldType.getNameOfLevelSelectImage())).getDrawable();
        bg.setMinWidth(2800);
        bg.setMinHeight(479);
        table.setBackground(bg);

        buttonsClicked = new boolean[NR_OF_BUTTONS];
        buttons = new ButtonWithACow[NR_OF_BUTTONS];

        for (byte i = 0; i < NR_OF_BUTTONS; i++) {
            buttons[i] = new ButtonWithACow(worldType, skin, i);
        }

        buttons[cowPreferences.getCurrentLevelSelection(worldType)].setCowVisible(true);

        setLevelButtonPositions(32, 32);
        for (byte i = 0; i < NR_OF_BUTTONS; i++) {
            table.addActor(buttons[i]);
            makeLevelButtonTouchable(buttons[i], i);
        }

        this.scrollPane = new ScrollPane(table, new ScrollPaneStyle());
        this.scrollPane.setScrollingDisabled(false, true);

        this.scrollPane.setSize(worldWidth + 100, bg.getMinHeight());
        this.scrollPane.setPosition(-50, 0);

        this.topImage = new Image(skin.getDrawable("level_select_top"));
        this.backBoardImage = new Image(skin.getDrawable("level_select_back_bg"));
        this.levelBoardImage = new Image(skin.getDrawable("level_select_level_name"));
        this.levelSpecificLabel = getCustomizedLabel(Translator.getInstance().t("level") + " " + (1 + currentIndex), labelStyle_19_brownColor, -1, -1, true);
        this.bottomImage = getCustomizedImage("level_select_bottom_cutout", -1, -1, true);
        this.backToMenuButton = new Button(skin.getDrawable("level_select_back_normal"), skin.getDrawable("level_select_back"));
        this.playButton = getCustomizedButton("level_select_play_green", "level_select_play", -1, -1, true);

        setButtonListener(playButton, PLAY);
        setButtonListener(backToMenuButton, BACK);

        new Image(skin.getDrawable("medal_veggie_spritesheet_colored"));
        new Image(skin.getDrawable("medal_time_spritesheet_colored"));
        new Image(skin.getDrawable("medal_treasure_spritesheet_colored"));

        this.setInitialPositions();

        this.whiteCanvasImage = new Image(skin.getDrawable("whiterect"));
        this.whiteCanvasImage.setBounds(0, 0, worldWidth, worldHeight);
        this.whiteCanvasImage.addAction(Actions.sequence(Actions.fadeOut(1f), Actions.run(new Runnable() {
            @Override
            public void run() {
                whiteCanvasImage.setVisible(false);
            }
        })));

        addActorsToStage(scrollPane, backBoardImage, levelBoardImage, levelSpecificLabel, topImage, bottomImage, playButton, backToMenuButton, whiteCanvasImage);

        this.animateStage(true, NO_ACTION);
    }

    private void setInitialPositions() {
        this.topImage.setPosition(centerX - topImage.getPrefWidth() / 2, worldHeight);
        this.levelBoardImage.setPosition(centerX - 116, 144 + worldHeight);
        this.levelSpecificLabel.setPosition(centerX - (levelBoardImage.getPrefWidth() / 2.66f) + (levelSpecificLabel.getPrefWidth() / 2) + 13,
                getCorrectY(levelSpecificLabel, 157 - worldHeight - 100));
        this.backBoardImage.setPosition(0, -160 - backBoardImage.getPrefHeight());
        this.bottomImage.setPosition(worldWidth - bottomImage.getWidth(), -168 - bottomImage.getPrefHeight());
        this.backToMenuButton.setPosition(-18, -171 - backToMenuButton.getPrefHeight());
        this.playButton.setPosition(this.bottomImage.getX() + bottomImage.getWidth() - this.playButton.getPrefWidth() - 16, -160 - playButton.getPrefHeight());
    }

    private void animateStage(final boolean down, final byte action) {
        final float duration = 1f;
        final Interpolation interpolation = Interpolation.sineOut;
        float topImageTargetY = down ? worldHeight - topImage.getPrefHeight() : worldHeight;
        topImage.addAction(Actions.moveTo(topImage.getX(), topImageTargetY, duration, interpolation));
        levelBoardImage.addAction(Actions.moveTo(levelBoardImage.getX(), topImageTargetY, duration, interpolation));
        levelSpecificLabel.addAction(Actions.moveTo(levelSpecificLabel.getX(), topImageTargetY + 13f, duration, interpolation));
        backBoardImage.addAction(Actions.moveTo(backBoardImage.getX(), down ? 0 : -worldHeight, duration, interpolation));
        bottomImage.addAction(Actions.moveTo(bottomImage.getX(), down ? 0 : -worldHeight, duration, interpolation));
        backToMenuButton.addAction(Actions.moveTo(backToMenuButton.getX(), down ? 5 : 5 - worldHeight, duration, interpolation));
        playButton.addAction(Actions.moveTo(playButton.getX(), down ? 16 : 16 - worldHeight, duration, interpolation));
        if (!down) {
            stage.addAction(Actions.delay(0.15f, Actions.run(new Runnable() {
                @Override
                public void run() {
                    fadeOutAndSwitchScreen(action);
                }
            })));
        }
    }

    private void animateSpecificParts(boolean down) {
        final float distance = worldHeight / 3f;
        final float duration = down ? .55f : .22f;
        final Interpolation interpolation = down ? Interpolation.bounceOut : Interpolation.sine;

        levelBoardImage.addAction(Actions.moveTo(levelBoardImage.getX(), down ? topImage.getY() : topImage.getY() + 40, duration, interpolation));
        levelSpecificLabel.addAction(Actions.moveTo(levelSpecificLabel.getX(), down ? topImage.getY() + 13 : topImage.getY() + 13 + 50, duration, interpolation));

        bottomImage.addAction(Actions.moveTo(bottomImage.getX(), down ? 0 : -distance, duration, interpolation));
        playButton.addAction(Actions.moveTo(playButton.getX(), down ? 16 : 16 - distance, duration, interpolation));
    }


    private void setLevelButtonPositions(float width, float height) {

        float yPos = 72;

        buttons[0].setPosition(172, yPos);
        buttons[1].setPosition(316, yPos);
        buttons[2].setPosition(439, yPos);
        buttons[3].setPosition(562, yPos);
        buttons[4].setPosition(692, yPos);
        buttons[5].setPosition(848, yPos - 5);
        buttons[6].setPosition(941, yPos);
        buttons[7].setPosition(1061, yPos);
        buttons[8].setPosition(1204, yPos);
        buttons[9].setPosition(1324, yPos);
        buttons[10].setPosition(1449, yPos + 3);
        buttons[11].setPosition(1578, yPos + 3);
        buttons[12].setPosition(1715, yPos);
        buttons[13].setPosition(1841, yPos);
        buttons[14].setPosition(1964, yPos);
        buttons[15].setPosition(2092, yPos);
        buttons[16].setPosition(2225, yPos);
        buttons[17].setPosition(2347, yPos);
        buttons[18].setPosition(2468, yPos);
        buttons[19].setPosition(2602, yPos);

        for (byte i = 0; i < buttons.length; i++) {
            buttons[i].setSize(width, height);
        }
    }


    private void makeLevelButtonTouchable(final ButtonWithACow buttonToAdd, final byte index) {
        buttonToAdd.addListener(new InputListener() {

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
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (event.getListenerActor().hit(x, y, true) != null) {
                    if ((index == 0 || CowPreferences.getInstance().isLevelUnlocked(index, worldType) || CowDash.ALL_LEVELS_UNLOCKED)) {
                        buttonsClicked[index] = true;
                        currentIndex = index;
                        levelButtonTouched = true;
                        CowPreferences.getInstance().saveCurrentLevelIndex(currentIndex, worldType);

                        if (!scrollPane.isFlinging() && !scrollPane.isPanning()) {
                            AudioUtils.getInstance().playSoundFX("button");
                            scrollPane.setScrollX(buttons[currentIndex].getX() - 32 - camera.viewportWidth / 2);

                            animateSpecificParts(true);

                            if (levelSpecificLabel.isVisible()) {
                                String levelLabelText = levelSpecificLabel.getText().toString();
                                byte labelTextLevelIndex = Byte.parseByte(levelLabelText.substring(levelLabelText.indexOf(' ') + 1));

                                if (labelTextLevelIndex - 1 == index) {
                                    fadeOutAndSwitchScreen(PLAY);
                                }
                            }
                        }
                        return;
                    }
                    buttonsClicked[index] = false;
                }
            }
        });
    }

    private void showOrHideCowOnButton(byte currentIndex, boolean visible) {
        for (byte i = 0; i < buttons.length; i++) {
            if (i != currentIndex) {
                buttons[i].setCowVisible(false);
            }
        }
        buttons[currentIndex].setCowVisible(visible);
    }

    private float getVisibleCowsXPosition() {
        for (ButtonWithACow bwc : buttons) {
            if (bwc.isCowVisible()) {
                return bwc.getX();
            }
        }
        return 0.0f;
    }

}
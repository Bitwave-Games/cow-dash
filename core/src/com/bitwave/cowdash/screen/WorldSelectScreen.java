package com.bitwave.cowdash.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.BackButtonAlternatives;
import com.bitwave.cowdash.utils.CustomButton;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.language.FontFinder;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;
import com.bitwave.cowdash.utils.ui.CustomInterpolations;

public class WorldSelectScreen extends MenuBaseScreen {

    private final byte NONE = -1;
    private final byte BACK = 0;
    private final byte LEVEL_SELECT = 1;

    private final Image whiteCanvasImage;
    private final Image backgroundImage;
    private final Image topBoardImage;
    private final Image backBoardImage;
    private final Button backButton;

    private final World grassWorld_1;
    private final World beachWorld_1;
    private final World grassWorld_premium;
    private final Label collectedMedalsLabel;
    private final LabelStyle worldLabelStyle;
    private final LabelStyle totalLabelStyle;
    private final Array<Button> buttons = new Array<Button>();

    public WorldSelectScreen(Game game, boolean arrivingFromLevelSelect) {
        super(game, BackButtonAlternatives.BACK_TO_MENU);

        this.whiteCanvasImage = new Image(skin.getDrawable("whiterect"));
        if (arrivingFromLevelSelect) {
            whiteCanvasImage.setBounds(0, 0, worldWidth, worldHeight);
            whiteCanvasImage.addAction(Actions.sequence(Actions.fadeOut(.66f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    whiteCanvasImage.setVisible(false);
                }
            })));
        }

        this.topBoardImage = new Image(skin.getDrawable("world_select_bg_title"));
        this.backgroundImage = new Image(skin.getDrawable("world_select_bg_icons"));
        this.backBoardImage = new Image(skin.getDrawable("level_select_back_bg"));
        this.backButton = new Button(skin.getDrawable("level_select_back_normal"), skin.getDrawable("level_select_back"));

        this.totalLabelStyle = new LabelStyle(FontFinder.getFont("cowfont", 10), Color.BROWN);
        this.collectedMedalsLabel = new Label("" + CowPreferences.getInstance().getTotalAmountOfCollectedMedals(), totalLabelStyle);

        this.setButtonListener(backButton, BACK);
        this.addActorsToStage(topBoardImage, backgroundImage, backBoardImage, backButton, collectedMedalsLabel);

        this.worldLabelStyle = new LabelStyle(FontFinder.getFont("cowfont", 16), Color.BROWN);
        this.grassWorld_1 = new World(WorldType.GRASS);
        this.beachWorld_1 = new World(WorldType.BEACH);
        this.grassWorld_premium = new World(WorldType.GRASS_PREMIUM);

        buttons.add(this.grassWorld_1.worldButton);
        buttons.add(this.beachWorld_1.worldButton);
        buttons.add(this.grassWorld_premium.worldButton);

        this.stage.addActor(whiteCanvasImage);

        this.stage.setKeyboardFocus(this.grassWorld_1.worldButton);

        setInitialPositions();
        animateStage(true, NONE);
    }

    @Override
    public void tick(float deltaTime) {
        stage.act(deltaTime);
        ParticleHelper.getInstance().tick(deltaTime, true);
    }

    private void setInitialPositions() {
        this.topBoardImage.setPosition(centerX - topBoardImage.getPrefWidth() / 2.f, worldHeight + topBoardImage.getPrefHeight());

        this.backgroundImage.setPosition(centerX - backgroundImage.getPrefWidth() / 2.f, -backgroundImage.getPrefHeight());
        float xInc = 93f;
        this.grassWorld_1.setPosition(backgroundImage.getX() + xInc, 88f - backgroundImage.getPrefHeight());
        this.grassWorld_premium.setPosition(backgroundImage.getX() + (xInc * 2), 88f - backgroundImage.getPrefHeight());
        this.beachWorld_1.setPosition(backgroundImage.getX() + (xInc * 3), 88f - backgroundImage.getPrefHeight());

        this.backBoardImage.setPosition(0, -backBoardImage.getPrefHeight());
        this.backButton.setPosition(-18, 5 - backBoardImage.getPrefHeight());

        this.collectedMedalsLabel.setPosition(backgroundImage.getX() + 32 - collectedMedalsLabel.getWidth() / 2f, worldHeight / 2f - 10f - worldHeight);
    }

    private void animateStage(final boolean down, final byte action) {
        if (action != BACK && action != LEVEL_SELECT && action != NONE) {
            fadeOutAndSwitchScreen(action);
            return;
        }

        float duration = 0.4f;
        Interpolation interpolation = down ? Interpolation.exp5Out : Interpolation.exp5In;
        topBoardImage.addAction(Actions.moveTo(topBoardImage.getX(), down ? worldHeight - topBoardImage.getPrefHeight() : worldHeight, duration, interpolation));

        Interpolation worldBoardInterpolation = down ? CustomInterpolations.swingOut : CustomInterpolations.swingIn;
        float backgroundImageTargetY = down ? (worldHeight - backgroundImage.getPrefHeight()) / 2f : -backgroundImage.getPrefHeight();
        backgroundImage.addAction(Actions.moveTo(backgroundImage.getX(), backgroundImageTargetY, duration, worldBoardInterpolation));
        collectedMedalsLabel.addAction(Actions.moveTo(collectedMedalsLabel.getX(), down ? worldHeight / 2 - 16f : (worldHeight / 2 - worldHeight), duration, worldBoardInterpolation));
        grassWorld_1.moveTo(backgroundImageTargetY + 88f, duration, worldBoardInterpolation);
        beachWorld_1.moveTo(backgroundImageTargetY + 88f, duration, worldBoardInterpolation);
        grassWorld_premium.moveTo(backgroundImageTargetY + 88f, duration, worldBoardInterpolation);

        backBoardImage.addAction(Actions.moveTo(backBoardImage.getX(), getCorrectY(backBoardImage, down ? worldHeight - backBoardImage.getPrefHeight() : worldHeight * 2), duration, interpolation));

        if (action == BACK) {
            backButton.addAction(Actions.sequence(Actions.moveTo(backButton.getX(), getCorrectY(backButton, down ? worldHeight - backButton.getPrefHeight() - 5 : worldHeight * 2), duration, interpolation), Actions.run(new Runnable() {
                @Override
                public void run() {
                    fadeOutAndSwitchScreen(action);
                }
            })));
        } else {
            backButton.addAction(Actions.moveTo(backButton.getX(), getCorrectY(backButton, down ? worldHeight - backButton.getPrefHeight() - 5 : worldHeight * 2), duration, interpolation));
            if (!down) {
                stage.addAction(Actions.delay(0.15f, Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        fadeOutAndSwitchScreen(action);
                    }
                })));
            }
        }
    }


    private void setButtonListener(final Actor button, final byte ACTION) {
        button.addListener(new InputListener() {

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                AudioUtils.getInstance().playSoundFX("button");
                ParticleHelper.getInstance().addWoodenEffect(event.getStageX(), event.getStageY(), true);
                animateStage(false, ACTION);
            }
        });
    }

    protected void fadeOutAndSwitchScreen(final byte ACTION) {
        saveUValuesForParallax();
        switch (ACTION) {
            case BACK:
                game.setScreen(new MainMenuScreen(game, false));
                break;
            case LEVEL_SELECT:
                whiteCanvasImage.setVisible(true);
                whiteCanvasImage.setColor(1, 1, 1, 0);
                whiteCanvasImage.setBounds(0, 0, worldWidth, worldHeight);
                whiteCanvasImage.addAction(Actions.sequence(Actions.fadeIn(.5f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new LevelSelectScreen(game, false));
                    }
                })));
                break;
        }

    }

    private class World {

        private static final float worldButtonOffsetY = 20f;
        private static final float statisticsLabelOffsetX = 4f;
        private final CustomButton worldButton;
        private final Label statisticsLabel;

        public World(final WorldType worldType) {
            final CowPreferences cowPreferences = CowPreferences.getInstance();
            boolean isUnlocked = cowPreferences.isWorldUnlocked(worldType);
            final byte unlockedMedals = cowPreferences.getUnlockedMedalsForWorld(worldType);
            final byte amountOfUnlockeableMedals = cowPreferences.getTotalAmountOfMedalsForWorld();

            Translator translator = Translator.getInstance();

            this.worldButton = new CustomButton(skin.getDrawable(worldType.getNameOfWorldSelectButton()));
            this.statisticsLabel = new Label("", worldLabelStyle);


            if (isUnlocked) {
                statisticsLabel.setText(unlockedMedals + "/" + amountOfUnlockeableMedals);
            } else {
                statisticsLabel.setText(worldType.getAmountOfMedalsRequiredToUnlock() - cowPreferences.getTotalAmountOfCollectedMedals());
                worldButton.setTouchable(Touchable.disabled);
            }

            this.worldButton.addListener(new InputListener() {

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
                    worldButton.addAction(Actions.sizeBy(-8, -8, .13f, Interpolation.sineIn));
                    worldButton.addAction(Actions.moveBy(4, 4, .13f, Interpolation.sineIn));
                    if (cowPreferences.isWorldUnlocked(worldType)) {
                        cowPreferences.setCurrentWorld(worldType);
                    }
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (event.getListenerActor().hit(x, y, true) != null) {
                        AudioUtils.getInstance().playSoundFX("button");
                        ParticleHelper.getInstance().addWoodenEffect(event.getStageX(), event.getStageY(), true);
                        worldButton.addAction(Actions.moveBy(-4, -4, .13f, Interpolation.sineOut));
                        worldButton.addAction(Actions.sequence(Actions.sizeBy(8, 8, .13f, Interpolation.sineOut), Actions.run(new Runnable() {
                            @Override
                            public void run() {

                                animateStage(false, LEVEL_SELECT);

                            }
                        })));
                    } else {
                        worldButton.addAction(Actions.moveBy(-4, -4, .13f, Interpolation.sineOut));
                        worldButton.addAction(Actions.sequence(Actions.sizeBy(8, 8, .13f, Interpolation.sineOut)));
                    }

                }
            });

            addActorsToStage(worldButton, statisticsLabel);
        }

        public void setPosition(float xPos, float yPos) {
            worldButton.setPosition(xPos, yPos + worldButtonOffsetY);
            statisticsLabel.setPosition(worldButton.getX() + statisticsLabelOffsetX + (worldButton.getPrefWidth() - statisticsLabel.getPrefWidth()) / 2f, yPos);
        }

        public void moveTo(float targetY, float duration, Interpolation interpolation) {
            worldButton.addAction(Actions.moveTo(worldButton.getX(), targetY + worldButtonOffsetY, duration, interpolation));
            statisticsLabel.addAction(Actions.moveTo(statisticsLabel.getX(), targetY, duration, interpolation));
        }

        public void changeButtonImage(WorldType worldType) {
            worldButton.getStyle().down = (skin.getDrawable(worldType.getNameOfWorldSelectButton()));
            statisticsLabel.setText("" + CowPreferences.getInstance().getUnlockedMedalsForWorld(worldType) +
                    " / " + CowPreferences.getInstance().getTotalAmountOfMedalsForWorld());
            statisticsLabel.setX(worldButton.getX() + worldButton.getPrefWidth() / 4);
        }
    }
}

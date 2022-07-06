package com.bitwave.cowdash.screen.ingame;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.CustomButton;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;
import com.bitwave.cowdash.utils.ui.AnimatedActor;
import com.bitwave.cowdash.utils.ui.CustomInterpolations;

public class StartAndPauseDialog extends Dialog {

    private final Stack clickToStartStack;
    private boolean tutorialImageTapped;
    private CustomButton pauseButton;
    private CustomButton resumeButton;
    private CustomButton restartButton;
    private CustomButton returnToLevelSelectButton;
    private Image pauseImage;
    private Label levelInfoLabel;
    private AnimatedActor timeMedalImage;
    private AnimatedActor veggieMedalImage;
    private AnimatedActor treasureMedalImage;
    private boolean timeToShowDialog;

    public StartAndPauseDialog(final Level level) {
        super(level);

        this.whiteCanvasImage.setBounds(0, 0, worldWidth, worldHeight);

        final Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setTouchable(Touchable.enabled);
        clickToStartStack = new Stack();
        rootTable.add(clickToStartStack).expand();

        String clickToStartText = Gdx.app.getType() == ApplicationType.Desktop ? "click to start!" : Translator.getInstance().t("start");
        Label onScreenLabelShadow = new Label(clickToStartText, brownColorLabelStyle_32);
        Container<Label> onScreenLabelShadowContainer = new Container<>(onScreenLabelShadow);
        onScreenLabelShadowContainer.padTop(4f);
        Label onScreenLabel = new Label(clickToStartText, orangeColorLabelStyle_32);
        Container<Label> onScreenLabelContainer = new Container<>(onScreenLabel);

        clickToStartStack.add(onScreenLabelShadowContainer);
        clickToStartStack.add(onScreenLabelContainer);

        createFlashingAnimation(clickToStartStack);
        rootTable.addListener(new InputListener() {

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (!level.isTutorialLevel() || (level.isTutorialLevel() && tutorialImageTapped)) {
                    AudioUtils.getInstance().playSoundFX("start-running");
                    screenTapped = true;
                    rootTable.remove();
                    stage.removeListener(this);
                    stage.unfocusAll();
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!level.isTutorialLevel() || (level.isTutorialLevel() && tutorialImageTapped)) {
                    AudioUtils.getInstance().playSoundFX("start-running");
                    screenTapped = true;
                    rootTable.remove();
                    stage.removeListener(this);
                }
                return true;
            }
        });

        whiteCanvasImage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                whiteCanvasImage.setVisible(false);
                if (level.isTutorialLevel()) {
                    addTutorialDialog();
                }
            }
        })));

        stage.addActor(rootTable);
        setUpPauseDialog();
        setUpPauseButton();
        this.stage.addActor(whiteCanvasImage);

        setPauseButtonVisible(true);
        setPauseDialogVisible(true);

        stage.setKeyboardFocus(rootTable);

        this.multiPlexer = new InputMultiplexer(stage, getCustomizedInput());
        Gdx.input.setInputProcessor(multiPlexer);
    }


    @Override
    public void tick(float deltaTime) {
        super.tick(deltaTime);
        if (Dialog.isPaused && timeToShowDialog) {
            animateStage(true, NONE);
            timeToShowDialog = false;
        } else {
            if (!Dialog.isPaused) {
                timeToShowDialog = true;
            }
        }
    }

    private void setPauseDialogVisible(boolean visible) {
        resumeButton.setTouchable(visible ? Touchable.enabled : Touchable.disabled);
        restartButton.setTouchable(visible ? Touchable.enabled : Touchable.disabled);
        returnToLevelSelectButton.setTouchable(visible ? Touchable.enabled : Touchable.disabled);
    }


    private void setPauseButtonVisible(boolean visible) {
        this.pauseButton.setTouchable(visible ? Touchable.enabled : Touchable.disabled);
    }

    @Override
    protected void setInitialPositions() {
        float startX = worldWidth / 2;
        this.pauseImage.setPosition(startX - pauseImage.getPrefWidth() / 2, worldHeight - pauseImage.getHeight() + worldHeight);
        this.resumeButton.setPosition(pauseImage.getX() + 328, worldHeight - pauseImage.getHeight() + 58 + worldHeight);
        this.restartButton.setPosition(pauseImage.getX() + 105, worldHeight - pauseImage.getHeight() + 58 + worldHeight);
        this.returnToLevelSelectButton.setPosition(pauseImage.getX() + 41, worldHeight - pauseImage.getHeight() + 58 + worldHeight);
        this.levelInfoLabel.setPosition(pauseImage.getX() + 130, worldHeight + 192);
        this.timeMedalImage.setPosition(pauseImage.getX() + 207, worldHeight + 89);
        this.veggieMedalImage.setPosition(pauseImage.getX() + 143, worldHeight + 89);
        this.treasureMedalImage.setPosition(pauseImage.getX() + 271, worldHeight + 89);
    }

    @Override
    protected void animateStage(final boolean down, final byte actionPerformedOnceFinished) {
        float duration = .46f;
        Interpolation interpolation = Interpolation.pow2;

        if (actionPerformedOnceFinished == NONE || actionPerformedOnceFinished == CONTINUE) {
            this.pauseButton.addAction(Actions.moveTo(pauseButton.getX(), down ? worldHeight : worldHeight - pauseButton.getHeight(), duration, Interpolation.sine));
        }
        this.pauseImage.addAction(Actions.moveTo(pauseImage.getX(), down ? worldHeight - pauseImage.getHeight() : worldHeight - 58, duration, interpolation));
        this.resumeButton.addAction(Actions.moveTo(resumeButton.getX(), down ? worldHeight - pauseImage.getHeight() + 58 : worldHeight, duration, interpolation));
        this.restartButton.addAction(Actions.moveTo(restartButton.getX(), down ? worldHeight - pauseImage.getHeight() + 58 : worldHeight, duration, interpolation));
        this.levelInfoLabel.addAction(Actions.moveTo(levelInfoLabel.getX(), down ? worldHeight - pauseImage.getHeight() + 196 : worldHeight + 192, duration, interpolation));
        this.timeMedalImage.addAction(Actions.moveTo(timeMedalImage.getX(), down ? worldHeight - pauseImage.getHeight() + 89 : worldHeight + 40, duration, interpolation));
        this.veggieMedalImage.addAction(Actions.moveTo(veggieMedalImage.getX(), down ? worldHeight - pauseImage.getHeight() + 89 : worldHeight + 40, duration, interpolation));
        this.treasureMedalImage.addAction(Actions.moveTo(treasureMedalImage.getX(), down ? worldHeight - pauseImage.getHeight() + 89 : worldHeight + 40, duration, interpolation));

        if (actionPerformedOnceFinished == CONTINUE) {
            this.returnToLevelSelectButton.addAction(Actions.sequence(Actions.moveTo(returnToLevelSelectButton.getX(), down ? worldHeight - pauseImage.getHeight() + 58 : worldHeight, duration, interpolation),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            if (!down) {
                                fadeOutScreen(actionPerformedOnceFinished);
                            }
                        }
                    })));
        } else {
            this.returnToLevelSelectButton.addAction(Actions.moveTo(returnToLevelSelectButton.getX(), down ? worldHeight - pauseImage.getHeight() + 58 : worldHeight, duration, interpolation));
            stage.addAction(Actions.delay(duration / 2f,
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            if (!down) {
                                fadeOutScreen(actionPerformedOnceFinished);
                            }
                        }
                    })));
        }
    }

    private void setUpPauseDialog() {
        this.pauseImage = new Image(skin.getDrawable("pause_screen"));
        this.resumeButton = new CustomButton(skin.getDrawable("pause_screen_resume_normal"), skin.getDrawable("pause_screen_resume"));
        this.restartButton = new CustomButton(skin.getDrawable("pause_screen_replay_normal"), skin.getDrawable("pause_screen_replay"));
        this.returnToLevelSelectButton = new CustomButton(skin.getDrawable("pause_screen_level_select_normal"), skin.getDrawable("pause_screen_level_select"));
        this.levelInfoLabel = new Label(Translator.getInstance().t("level") + " " + (level.getCurrentLevelIndex() + 1), brownColorLabelStyle_32);


        CowPreferences cowPreferences = CowPreferences.getInstance();
        WorldType worldType = WorldType.getValue(cowPreferences.getCurrentWorld());
        byte levelIndex = level.getCurrentLevelIndex();
        this.timeMedalImage = new AnimatedActor(skin.getRegion("medal_time_spritesheet_colored"), MathUtils.random(.24f, .44f), 64, 64, cowPreferences.getTimeMedalAcquired(levelIndex, worldType));
        this.veggieMedalImage = new AnimatedActor(skin.getRegion("medal_veggie_spritesheet_colored"), MathUtils.random(.24f, .44f), 64, 64, cowPreferences.getVeggieMedalAcquired(levelIndex, worldType));
        this.treasureMedalImage = new AnimatedActor(skin.getRegion("medal_treasure_spritesheet_colored"), MathUtils.random(.24f, .44f), 64, 64, cowPreferences.getChestMedalAcquired(levelIndex, worldType));

        this.setInitialPositions();

        setButtonListener(returnToLevelSelectButton, LEVEL_SELECT);
        setButtonListener(restartButton, REPLAY);
        setButtonListener(resumeButton, CONTINUE);

        this.whiteCanvasImage.setBounds(0, 0, worldWidth, worldHeight);


        addActorsToStage(pauseImage, timeMedalImage, veggieMedalImage, treasureMedalImage, resumeButton, restartButton, returnToLevelSelectButton,
                levelInfoLabel, whiteCanvasImage);

    }

    private void setUpPauseButton() {
        this.pauseButton = new CustomButton(skin.getDrawable("pause_button"));

        pauseButton.setPosition(stage.getViewport().getWorldWidth() - pauseButton.getWidth(), worldHeight - pauseButton.getHeight() + 500);
        pauseButton.addAction(Actions.moveBy(0, -500, .9f, Interpolation.sine));
        pauseButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                AudioUtils.getInstance().playSoundFX("button");
                ParticleHelper.getInstance().addWoodenEffect(event.getStageX(), event.getStageY(), true);
                Dialog.isPaused = true;
                animateStage(true, NONE);
                return true;
            }
        });
        stage.addActor(pauseButton);
    }

    private void addTutorialDialog() {
        final byte JUMP = 0;
        final byte JUMP_HIGH = 1;
        final byte JUMP_PIG = 2;
        final byte BEACH = 3;
        final float SPEED = .5f;

        String path = "";
        switch (level.getTutorialLevelIndex()) {
            case JUMP:
                path = "sprites/in/tutorial_1.png";
                break;
            case JUMP_HIGH:
                path = "sprites/in/tutorial_jump_long_strip4" + ".png";
                break;
            case JUMP_PIG:
                path = "sprites/in/tutorial_pig_colored" + ".png";
                break;
            case BEACH:
                path = "sprites/in/tutorial_walljump_strip5" + ".png";
                break;
        }

        TextureRegion region = new TextureRegion((Texture) Assets.getInstance().get(path));

        final AnimatedActor slideShow = new AnimatedActor(region, SPEED, 400, 240, true);
        slideShow.addAction(Actions.parallel(
                Actions.scaleTo(1f, 1f, .35f, CustomInterpolations.swingOut),
                Actions.fadeIn(0.35f)
        ));
        slideShow.setPosition(worldWidth / 2 - 200, worldHeight / 2 - slideShow.getHeight() / 2);
        slideShow.setScale(0f);
        slideShow.setOrigin(Align.center);
        slideShow.addListener(new InputListener() {

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
                tutorialImageTapped = true;
                slideShow.addAction(Actions.sequence(Actions.moveBy(0, worldHeight, .35f, CustomInterpolations.swingIn), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        stage.getActors().removeValue(slideShow, true);
                    }
                })));

            }
        });
        this.stage.setKeyboardFocus(slideShow);

        this.stage.addActor(slideShow);
    }

    private InputProcessor getCustomizedInput() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                return true;
            }
        };
    }

}
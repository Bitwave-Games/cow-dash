package com.bitwave.cowdash.screen.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.screen.BaseScreen;
import com.bitwave.cowdash.screen.ScreenType;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.CustomButton;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.language.LanguageBasedSkin;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public abstract class Dialog implements Disposable {


    public static boolean screenTapped;
    public static boolean isPaused;
    public static boolean canJump;
    protected final byte NONE = -1;
    protected final byte NEXT = 1;
    protected final byte REPLAY = 2;
    protected final byte LEVEL_SELECT = 3;
    protected final byte MY_COW = 4;
    protected final byte CONTINUE = 5;
    protected final byte NEXT_INCREASE_LEVEL_COUNTER = 6;
    protected final byte REPLAY_INCREASE_LEVEL_COUNTER = 7;
    protected final byte LEVEL_SELECT_INCREASE_LEVEL_COUNTER = 8;
    protected boolean alreadyEntered;
    protected LanguageBasedSkin skin;
    protected Level level;
    protected Stage stage;
    protected Image dialogImage;
    protected Image whiteCanvasImage;
    protected Label onDialogLabel;
    protected Label onScreenLabel;

    protected LabelStyle brownColorLabelStyle_32;
    protected LabelStyle orangeColorLabelStyle_32;
    protected LabelStyle whiteColorLabelStyle_16;

    protected CustomButton nextLevelButton;
    protected CustomButton replayLevelButton;
    protected CustomButton backToLevelSelectButton;
    protected CustomButton myCowButton;

    protected float worldWidth;
    protected float worldHeight;

    protected WorldType worldType;

    protected InputMultiplexer multiPlexer;

    protected String nameOfTrackToPlay;

    public Dialog(Level level) {
        this.level = level;
        this.worldType = level.getWorldType();
        this.nameOfTrackToPlay = WorldType.getValue(CowPreferences.getInstance().getCurrentWorld()).getNameOfTrackForWorld();

        Vector2 size = Scaling.fill.apply(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), BaseScreen.VIEWPORT_WIDTH, BaseScreen.VIEWPORT_HEIGHT);
        this.stage = new Stage(new FillViewport(size.x, size.y));
        this.stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        this.worldHeight = stage.getViewport().getWorldHeight();
        this.worldWidth = stage.getViewport().getWorldWidth();

        TextureAtlas atlas;
        atlas = (TextureAtlas) Assets.getInstance().get("sprites/ui/dialogs.pack");
        this.skin = new LanguageBasedSkin(atlas);

        BitmapFont bitmapFont_32 = (BitmapFont) Assets.getInstance().get("fonts/cowfont_32.fnt");
        BitmapFont bitmapFont_16 = (BitmapFont) Assets.getInstance().get("fonts/cowfont_16.fnt");
        Color orangeColor = new Color(.996078431f, .749019608f, .058823529f, 1);
        Color brownColor = new Color(101 / 255f, 79 / 255f, 33 / 255f, 1f);
        this.brownColorLabelStyle_32 = new LabelStyle(bitmapFont_32, brownColor);
        this.orangeColorLabelStyle_32 = new LabelStyle(bitmapFont_32, orangeColor);
        this.whiteColorLabelStyle_16 = new LabelStyle(bitmapFont_16, Color.WHITE);

        this.whiteCanvasImage = new Image(skin.getDrawable("whiterect"));


        this.multiPlexer = new InputMultiplexer(stage, level.getBackButtonHandler());

        if (!(this instanceof StartAndPauseDialog)) {
            Gdx.input.setInputProcessor(multiPlexer);
        }

        screenTapped = false;
    }

    protected void animateStage(boolean down, byte actionPerformedOnceFinished) {
    }

    protected void setInitialPositions() {
    }


    public void render() {
        stage.draw();

        SpriteBatch batch = (SpriteBatch) stage.getBatch();
        batch.begin();
        ParticleHelper.getInstance().render(batch, true);
        batch.end();
    }

    public void tick(float deltaTime) {
        stage.act(deltaTime);

        canJump = Gdx.input.isTouched();

        ParticleHelper.getInstance().tick(deltaTime, true);
    }


    protected float getCorectY(Button button, float desiredYPos) {
        return worldHeight - button.getPrefHeight() - desiredYPos;
    }

    protected float getCorectY(Label button, float desiredYPos) {
        return worldHeight - button.getPrefHeight() - desiredYPos;
    }

    protected float getCorectY(Image image, float desiredYPos) {
        return worldHeight - image.getPrefHeight() - desiredYPos;
    }

    protected void createFlashingAnimation(Actor actor) {
        actor.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.fadeOut(0.1f), Actions.delay(0.6f), Actions.fadeIn(0.1f), Actions.delay(0.6f))));
    }

    protected void addActorsToStage(Actor... actors) {
        for (Actor actor : actors) {
            stage.addActor(actor);
        }
    }

    protected void fadeOutAndHideWhiteCanvas(float fadeTime) {
        whiteCanvasImage.addAction(Actions.sequence(Actions.fadeOut(fadeTime), Actions.run(new Runnable() {
            @Override
            public void run() {
                whiteCanvasImage.setVisible(false);
            }
        })));
    }

    protected void setButtonListener(Button button, final byte ACTION) {
        button.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (event.getListenerActor().hit(x, y, true) != null) {
                    ParticleHelper.getInstance().addWoodenEffect(event.getStageX(), event.getStageY(), true);
                    AudioUtils.getInstance().playSoundFX("button");

                    if (ACTION != CONTINUE) {
                        disableStage();
                        for (Actor actor : stage.getActors()) {
                            if (actor.getActions().size > 0) {
                                actor.getActions().clear();
                            }
                        }
                    }

                    animateStage(false, ACTION);
                }
            }
        });
    }

    protected void flashScreen() {
        if (!alreadyEntered) {
            alreadyEntered = true;
            whiteCanvasImage.setVisible(true);
            whiteCanvasImage.addAction(Actions.sequence(Actions.fadeIn(0.0f), Actions.fadeOut(1.2f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    whiteCanvasImage.setVisible(false);
                }
            })));
        }
    }

    protected void fadeOutScreen(final byte actionPerformedOnceFinished) {
        if (actionPerformedOnceFinished == CONTINUE) {
            Dialog.isPaused = false;
        } else {
            if (level.isItemUnlocked()) {
                AudioUtils.getInstance().crossFadeTracks("boss", nameOfTrackToPlay, 10f, true);
            }
            whiteCanvasImage.setVisible(true);
            whiteCanvasImage.setBounds(0, 0, worldWidth, worldHeight);
            whiteCanvasImage.addAction(Actions.sequence(Actions.fadeIn(.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    switch (actionPerformedOnceFinished) {
                        case NEXT:
                            goToNextLevel();
                            break;
                        case NEXT_INCREASE_LEVEL_COUNTER:
                            goToNextLevel();
                            break;
                        case REPLAY:
                            replayLevel();
                            break;
                        case REPLAY_INCREASE_LEVEL_COUNTER:
                            replayLevel();
                            break;
                        case LEVEL_SELECT:
                            goBackToLevelSelect();
                            break;
                        case LEVEL_SELECT_INCREASE_LEVEL_COUNTER:
                            goBackToLevelSelect();
                            break;
                        case MY_COW:
                            level.end(ScreenType.MY_COW);
                            break;
                    }
                }
            })));
        }
    }

    private void goBackToLevelSelect() {


        Dialog.isPaused = false;
        level.end(ScreenType.LEVEL_SELECT);
    }

    private void replayLevel() {
        Dialog.isPaused = false;
        level.restart();
    }

    private void goToNextLevel() {
        if (level.isFinalLevel()) {
            level.end(ScreenType.CREDITS);
        } else {
            level.switchLevel();
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        ParticleHelper.getInstance().dispose();
    }

    protected void disableStage() {
        for (Actor actor : stage.getActors()) {
            actor.setTouchable(Touchable.disabled);
        }
    }


    public void resetContext() {
    }


}
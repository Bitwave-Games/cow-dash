package com.bitwave.cowdash.screen.ingame;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.screen.ScreenType;
import com.bitwave.cowdash.utils.LabelFormatter;

public class DeathDialog extends Dialog {

    private final byte REPLAY = 0;
    private final byte BACK = 1;
    private final byte TIME_TO_FADE = 1;
    private float deathTime;
    private float yPosForHint;
    private float yPosForHeaderLabel;
    private boolean isCalled;
    private LabelFormatter formatter;

    public DeathDialog(Level level) {
        super(level);
        this.whiteCanvasImage.setBounds(0, 0, worldWidth, worldHeight);
        this.stage.addActor(whiteCanvasImage);
        this.whiteCanvasImage.addAction(Actions.sequence(Actions.fadeOut(0.0f), Actions.run(new Runnable() {
            @Override
            public void run() {
                whiteCanvasImage.setVisible(false);
            }
        })));
    }

    @Override
    public void tick(float deltaTime) {
        super.tick(deltaTime);
        if (deathTime < TIME_TO_FADE) {
            deathTime += deltaTime;
        } else {
            if (!isCalled) {
                fadeOutScreen(REPLAY);
                isCalled = true;
            }
        }
    }

    @Override
    protected void fadeOutScreen(final byte actionPerformedOnceFinished) {
        whiteCanvasImage.setVisible(true);
        whiteCanvasImage.setZIndex(stage.getActors().size - 1);
        whiteCanvasImage.addAction(Actions.sequence(Actions.fadeIn(.5f), Actions.run(new Runnable() {
            @Override
            public void run() {


                switch (actionPerformedOnceFinished) {
                    case REPLAY:
                        Dialog.isPaused = false;
                        level.restart();
                        break;
                    case BACK:
                        Dialog.isPaused = false;
                        level.end(ScreenType.LEVEL_SELECT);
                        break;
                }
            }
        })));
        disableStage();
    }

    @Override
    protected void setInitialPositions() {
        float width = worldWidth / 2;
        this.dialogImage.setPosition(width - dialogImage.getPrefWidth() / 2, worldHeight - dialogImage.getHeight() + worldHeight);
        this.onDialogLabel.setPosition(width - (onDialogLabel.getPrefWidth() / 2), worldHeight - dialogImage.getHeight() + yPosForHeaderLabel + worldHeight);

        byte rows = formatter.getAmountOfLineBreaks();
        if (rows <= 1) {
            yPosForHint = 120;
        } else {
            yPosForHint = 110;
        }


        this.onScreenLabel.setPosition(dialogImage.getX() + 35, worldHeight - dialogImage.getHeight() + yPosForHint + worldHeight);
        this.backToLevelSelectButton.setPosition(dialogImage.getX() + 213, worldHeight - dialogImage.getHeight() + 57 + worldHeight);
        this.replayLevelButton.setPosition(dialogImage.getX() + 277, worldHeight - dialogImage.getHeight() + 57 + worldHeight);
    }

    @Override
    protected void animateStage(final boolean down, final byte actionPerformedOnceFinished) {
        float duration = .66f;
        Interpolation interpolation = Interpolation.pow2;
        this.dialogImage.addAction(Actions.moveTo(dialogImage.getX(), down ? worldHeight - dialogImage.getHeight() : worldHeight, duration, interpolation));
        this.onDialogLabel.addAction(Actions.moveTo(onDialogLabel.getX(), down ? worldHeight - dialogImage.getHeight() + yPosForHeaderLabel : 193 + worldHeight, duration, interpolation));
        this.onScreenLabel.addAction(Actions.moveTo(onScreenLabel.getX(), down ? worldHeight - dialogImage.getHeight() + yPosForHint : 120 + worldHeight, duration, interpolation));
        this.backToLevelSelectButton.addAction(Actions.moveTo(backToLevelSelectButton.getX(), down ? worldHeight - dialogImage.getHeight() + 57 : 57 + worldHeight, duration, interpolation));

        this.replayLevelButton.addAction(Actions.sequence(Actions.moveTo(replayLevelButton.getX(), down ? worldHeight - dialogImage.getHeight() + 57 : 57 + worldHeight, duration, interpolation), Actions.run(new Runnable() {
            @Override
            public void run() {
                if (!down) {
                    fadeOutScreen(actionPerformedOnceFinished);
                }
            }
        })));
    }


}
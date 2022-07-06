package com.bitwave.cowdash.screen.ingame;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.level.Level;

public class DialogHandler implements Disposable {

    private final StopWatch stopWatch;
    private final Level level;
    private Dialog dialog;
    private float timer;
    private boolean startBoxAlreadySet;
    private boolean deathBoxAlreadySet;
    private boolean levelFinishedBoxAlreadyShown;


    public DialogHandler(Level level) {
        this.level = level;
        this.stopWatch = new StopWatch(level);
    }

    private void showLevelFinishedBox() {
        if (!levelFinishedBoxAlreadyShown) {
            this.dialog = new LevelFinishedDialog(level);
            levelFinishedBoxAlreadyShown = true;
        }
    }


    public void showDeathBox() {
        if (!deathBoxAlreadySet) {
            this.dialog = new DeathDialog(level);
            deathBoxAlreadySet = true;
        }
    }


    public void showStartBox() {
        if (!startBoxAlreadySet) {
            this.dialog = new StartAndPauseDialog(level);
            startBoxAlreadySet = true;
        }
    }

    public void render() {
        if (dialog != null) {
            dialog.render();
        }
        if (!level.isRubyTouched()) {
            stopWatch.render();
        }
    }

    public void tick(float deltaTime, float timeToDisplayDialog) {
        if (!Dialog.screenTapped) {
            showStartBox();
        }

        if (Dialog.screenTapped && !Dialog.isPaused && !level.isRubyTouched()) {
            stopWatch.tick(deltaTime);
        }

        if (dialog != null) {
            dialog.tick(deltaTime);
        }

        if (level.isRubyTouched()) {
            timer += deltaTime;
            dialog.flashScreen();
        } else {
            timer = 0;
        }
        if (level.isRubyTouched() && timer >= timeToDisplayDialog && finishedActing(dialog.whiteCanvasImage)) {
            showLevelFinishedBox();
        }
    }

    public void resetContext() {
        if (dialog != null) {
            dialog.resetContext();
        }
    }

    public float getCompletionTime() {
        return stopWatch.getCompletionTime();
    }

    @Override
    public void dispose() {
        if (dialog != null) {
            dialog.dispose();
        }
    }

    private boolean finishedActing(Actor actor) {
        return actor.getActions().size <= 0;
    }

}
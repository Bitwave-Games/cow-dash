package com.bitwave.cowdash.screen.ingame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.utils.Assets;

public class StopWatch extends Dialog {

    private float timeLimit;
    private int hundreds;
    private int seconds;

    private float realTimeTaken;

    public StopWatch(Level level) {
        super(level);

        String stringTime = "" + level.getTimeToBeat() + "0";

        if (stringTime.length() > 5) {
            stringTime = stringTime.substring(0, stringTime.length() - 2);
        }

        String secs = stringTime.substring(0, stringTime.indexOf('.'));
        this.timeLimit = level.getTimeToBeat() * 1000;

        this.seconds = Integer.parseInt(secs);
        this.hundreds = (int) ((timeLimit / 10) % 100);

        BitmapFont bitmapFont_12 = (BitmapFont) Assets.getInstance().get("fonts/cowfont_12.fnt");
        LabelStyle orangeColorLabelStyle_12 = new LabelStyle(bitmapFont_12, new Color(.996078431f, .749019608f, .058823529f, 1));
        this.onScreenLabel = new Label(String.format("%02d:%02d", seconds, hundreds), orangeColorLabelStyle_12);
        this.onScreenLabel.setPosition(worldWidth - onScreenLabel.getPrefWidth() - 20f, 4f);
        this.stage.addActor(onScreenLabel);
    }

    @Override
    public void tick(float deltaTime) {
        super.tick(deltaTime);
        timeLimit -= deltaTime * 1000;
        seconds = (int) ((timeLimit / 1000) % 60);
        hundreds = (int) ((timeLimit / 10) % 100);

        if (timeLimit <= 0) {
            onScreenLabel.setColor(Color.RED);
            onScreenLabel.setText(String.format("+%02d:%02d", Math.abs(seconds), Math.abs(hundreds)));
        } else {
            onScreenLabel.setText(String.format("%02d:%02d", seconds, hundreds));
        }

        realTimeTaken += deltaTime;
    }

    public float getCompletionTime() {
        return realTimeTaken;
    }
}
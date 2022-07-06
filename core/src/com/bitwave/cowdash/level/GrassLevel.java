package com.bitwave.cowdash.level;

import com.badlogic.gdx.graphics.Color;
import com.bitwave.cowdash.screen.GameScreen;
import com.bitwave.cowdash.utils.WorldType;


public class GrassLevel extends Level {


    public GrassLevel(String levelPath, GameScreen gameScreen, WorldType worldType) {
        super(levelPath, gameScreen, worldType);
        this.backgroundColor = new Color(0.682352941f, 0.807843137f, 0.831372549f, 1);
    }

}

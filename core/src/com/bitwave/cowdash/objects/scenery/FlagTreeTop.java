package com.bitwave.cowdash.objects.scenery;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.TextureUtils;

public class FlagTreeTop extends Scenery {

    public FlagTreeTop(Vector2 position, Level level) {
        super(position, 32, 32, level, new Animation(MathUtils.random(.19f, .27f), TextureUtils.singleSplit((Texture) Assets.getInstance().get(PATH + "flag_strip3.png"), 32, 32, false)));
    }

}

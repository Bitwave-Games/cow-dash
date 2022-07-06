package com.bitwave.cowdash.objects.scenery;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.TextureUtils;

public class PalmTreeTop extends Scenery {

    public PalmTreeTop(Vector2 position, Level level) {
        super(position, 32, 32, level, new Animation(MathUtils.random(.27f, .32f), TextureUtils.singleSplit((Texture) Assets.getInstance().get(PATH + "palm_treetop_32x32_strip4.png"), 32, 32, false)));
    }

}

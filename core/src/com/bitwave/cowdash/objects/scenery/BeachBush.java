package com.bitwave.cowdash.objects.scenery;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.TextureUtils;

public class BeachBush extends Scenery {

    public BeachBush(Vector2 position, Level level) {
        super(position, 32, 32, level, new Animation(MathUtils.random(.49f, .57f), TextureUtils.singleSplit((Texture) Assets.getInstance().get(PATH + "windy_weed_strip4.png"), 32, 32, true)));

    }

}

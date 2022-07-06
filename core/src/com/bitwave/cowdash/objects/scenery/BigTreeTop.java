package com.bitwave.cowdash.objects.scenery;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.TextureUtils;

public class BigTreeTop extends Scenery {

    public BigTreeTop(Vector2 position, Level level) {
        super(position, 96, 64, level, new Animation(MathUtils.random(.57f, .76f), TextureUtils.singleSplit((Texture) Assets.getInstance().get(PATH + "treetops_big_96.png"), 96, 64, true)));
    }

}

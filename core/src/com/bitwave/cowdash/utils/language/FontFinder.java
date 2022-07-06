package com.bitwave.cowdash.utils.language;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.bitwave.cowdash.utils.Assets;


public class FontFinder {

    public static BitmapFont getFont(String fontName, int fontSize) {
        BitmapFont font = (BitmapFont) Assets.getInstance().get("fonts/" + fontName + "_" + fontSize + ".fnt");
        return font;
    }

}

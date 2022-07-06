package com.bitwave.cowdash.utils.language;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class LanguageBasedSkin extends Skin {

    public LanguageBasedSkin(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public Drawable getDrawable(String name) {
        String nameWithLanguageSuffix = name + "en";
        if (getAtlas().findRegion(nameWithLanguageSuffix) == null) {
            return super.getDrawable(name);
        }
        return super.getDrawable(nameWithLanguageSuffix);
    }

    @Override
    public TextureRegion getRegion(String name) {
        String nameWithLanguageSuffix = name + "en";
        if (getAtlas().findRegion(nameWithLanguageSuffix) == null) {
            return super.getRegion(name);
        }
        return super.getRegion(nameWithLanguageSuffix);
    }

}

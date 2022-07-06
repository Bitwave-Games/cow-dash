package com.bitwave.cowdash.utils.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bitwave.cowdash.objects.item.Back;
import com.bitwave.cowdash.objects.item.Body;
import com.bitwave.cowdash.objects.item.Head;
import com.bitwave.cowdash.objects.item.Leg;
import com.bitwave.cowdash.objects.item.Mask;
import com.bitwave.cowdash.utils.ItemUtils;

public class CustomCheckBox extends CheckBox {

    private final byte BROWN = 0;
    private final byte COLORED = 1;

    private final byte STATES = 2;

    private final TextureRegion[] currentFrames;
    private final Color brownishColor;

    public CustomCheckBox(Skin skin, String type, byte index) {
        super("", new CheckBoxStyle(skin.getDrawable("my_cow_cow_selector_normal"), skin.getDrawable("my_cow_cow_selector_highlighted"), new BitmapFont(), null));
        this.currentFrames = new TextureRegion[STATES];
        this.brownishColor = new Color(209 / 255f, 168 / 255f, 79 / 255f, 1f);

        Pixmap brownNakedPixmap = new Pixmap(Gdx.files.internal("sprites/objects/cow.png"));
        Pixmap coloredNakedPixmap = new Pixmap(Gdx.files.internal("sprites/objects/cow.png"));
        if (type.equalsIgnoreCase(ItemUtils.HEAD)) {
            brownNakedPixmap.drawPixmap(Head.getValue(index).getPixmap(), 0, 0);
            coloredNakedPixmap.drawPixmap(Head.getValue(index).getPixmap(), 0, 0);
        } else if (type.equalsIgnoreCase(ItemUtils.MASK)) {
            brownNakedPixmap.drawPixmap(Mask.getValue(index).getPixmap(), 0, 0);
            coloredNakedPixmap.drawPixmap(Mask.getValue(index).getPixmap(), 0, 0);
        } else if (type.equalsIgnoreCase(ItemUtils.BODY)) {
            brownNakedPixmap.drawPixmap(Body.getValue(index).getPixmap(), 0, 0);
            coloredNakedPixmap.drawPixmap(Body.getValue(index).getPixmap(), 0, 0);
        } else if (type.equalsIgnoreCase(ItemUtils.LEG)) {
            brownNakedPixmap.drawPixmap(Leg.getValue(index).getPixmap(), 0, 0);
            coloredNakedPixmap.drawPixmap(Leg.getValue(index).getPixmap(), 0, 0);
        } else if (type.equalsIgnoreCase(ItemUtils.BACK)) {
            brownNakedPixmap.drawPixmap(Back.getValue(index).getPixmap(), 0, 0);
            coloredNakedPixmap.drawPixmap(Back.getValue(index).getPixmap(), 0, 0);
        }

        this.currentFrames[BROWN] = new TextureRegion(new Texture(brownNakedPixmap), 0, 0, 32, 32);
        this.currentFrames[COLORED] = new TextureRegion(new Texture(coloredNakedPixmap), 0, 0, 32, 32);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!isChecked()) {
            this.getStyle().checkboxOff.draw(batch, getX(), getY(), getWidth(), getHeight());
            batch.setColor(brownishColor);
            batch.draw(currentFrames[BROWN], getX() + 8, getY() + 20, 48, 48);
            batch.setColor(Color.WHITE);
        } else {
            this.getStyle().checkboxOn.draw(batch, getX(), getY(), getWidth(), getHeight());
            batch.draw(currentFrames[COLORED], getX() + 8, getY() + 20, 48, 48);
        }
    }

}
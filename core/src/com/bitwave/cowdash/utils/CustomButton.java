package com.bitwave.cowdash.utils;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class CustomButton extends Button {

    private boolean gpBased = false;
    private boolean pressed = false;

    public CustomButton(Drawable up, Drawable down) {
        super(up, down);
    }

    public CustomButton(Drawable drawable) {
        super(drawable);
    }

    @Override
    public boolean isPressed() {
        if (this.gpBased) {
            return this.pressed;
        }
        return super.isPressed();
    }

    public void setPressed(boolean down) {
        this.gpBased = true;
        this.pressed = down;
    }


}

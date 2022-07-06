package com.bitwave.cowdash.tween;

import com.badlogic.gdx.graphics.g2d.Sprite;

import aurelienribon.tweenengine.TweenAccessor;

public class SpriteAccessor implements TweenAccessor<Sprite> {

    public final int X = 1;
    public final int Y = 2;
    public final int XY = 3;

    public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case X:
                returnValues[0] = target.getX();
                return 1;
            case Y:
                returnValues[0] = target.getY();
                return 1;
            case XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            default:
                assert false;
                return 0;
        }
    }

    public void setValues(Sprite target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case X:
                target.setX(newValues[0]);
                break;
            case Y:
                target.setY(newValues[1]);
                break;
            case XY:
                target.setX(newValues[0]);
                target.setY(newValues[1]);
                break;
            default:
                assert false;
                break;
        }
    }

}

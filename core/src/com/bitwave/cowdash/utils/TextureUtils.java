package com.bitwave.cowdash.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureUtils {

    public static TextureRegion[] singleSplit(Texture texture, int width, int height, boolean flipX) {
        int xSlices = texture.getWidth() / width;
        int ySlices = texture.getHeight() / height;
        TextureRegion[][] tmp = new TextureRegion[xSlices][ySlices];
        TextureRegion[] res = new TextureRegion[xSlices * ySlices];

        int index = 0;
        for (int y = 0; y < ySlices; y++) {
            for (int x = 0; x < xSlices; x++) {
                tmp[x][y] = new TextureRegion(texture, x * width, y * height, width, height);
                res[index] = tmp[x][y];
                res[index].flip(flipX, false);
                index++;
            }
        }
        return res;
    }

    public static TextureRegion[] singleSplit(TextureRegion textureRegion, int width, int height, boolean flipX) {
        int xSlices = textureRegion.getRegionWidth() / width;
        int ySlices = textureRegion.getRegionHeight() / height;
        TextureRegion[][] tmp = new TextureRegion[xSlices][ySlices];
        TextureRegion[] res = new TextureRegion[xSlices * ySlices];

        int index = 0;
        for (int x = 0; x < xSlices; x++) {
            for (int y = 0; y < ySlices; y++) {
                tmp[x][y] = new TextureRegion(textureRegion, x * width, y * height, width, height);
                res[index] = tmp[x][y];
                res[index].flip(flipX, false);
                index++;
            }
        }

        return res;
    }


}
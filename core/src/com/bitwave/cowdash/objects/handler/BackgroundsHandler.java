package com.bitwave.cowdash.objects.handler;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.objects.Player;
import com.bitwave.cowdash.screen.ingame.Dialog;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public class BackgroundsHandler {

    private final byte FRONT_CLOUD = 0;
    private final byte BACK_CLOUD = 1;

    private final float MOUNTAINS_X_SPEED = 30.33f;
    private final float GRASSHILLS_X_SPEED = 10.33f;
    private final float CLOUD_X_SPEED = MathUtils.random(1.212f, 1.5f);

    private final OrthographicCamera camera;
    private final Sprite[] clouds;
    private final Sprite[] cloudSprites;
    private final Vector2[] cloudPositions;

    private float randomCloudXPosition;
    private float randomCloudYPosition;
    private final float mapWidth;
    private final float mapHeight;

    private final Sprite scrollableMountainsSprite;
    private final Sprite scrollableGrassHillsSprite;

    public BackgroundsHandler(OrthographicCamera camera, int mapWidth, int mapHeight) {
        this.camera = camera;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        this.clouds = new Sprite[2];
        this.cloudSprites = new Sprite[2];
        this.cloudPositions = new Vector2[2];

        this.setUpClouds();
        WorldType worldType = WorldType.getValue(CowPreferences.getInstance().getCurrentWorld());
        this.scrollableMountainsSprite = getScrollableSprite(worldType.getPathToBackgroundPNG());
        this.scrollableGrassHillsSprite = getScrollableSprite(worldType.getPathToForegroundPng());

        this.scrollableGrassHillsSprite.setPosition(camera.position.x, 0);
        this.scrollableMountainsSprite.setPosition(camera.position.x, (scrollableGrassHillsSprite.getHeight() / 3) - 256f);
    }

    private Sprite getScrollableSprite(String path) {
        Texture texture = (Texture) Assets.getInstance().get(path);
        texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        Sprite scrollableSprite = new Sprite(texture);
        scrollableSprite.setSize(mapWidth, scrollableSprite.getHeight());
        scrollableSprite.setX(0);
        scrollableSprite.scroll(1, 0);
        return scrollableSprite;
    }

    private void setUpClouds() {
        WorldType worldType = WorldType.getValue(CowPreferences.getInstance().getCurrentWorld());
        switch (worldType) {
            case BEACH:
                clouds[0] = Assets.getInstance().getSprite("sprites/backgrounds/beach_cloud.png");
                clouds[1] = Assets.getInstance().getSprite("sprites/backgrounds/sunset_cloud.png");
                break;
            default:
                clouds[0] = Assets.getInstance().getSprite("sprites/backgrounds/mediumcloud.png");
                clouds[1] = Assets.getInstance().getSprite("sprites/backgrounds/smallcloud.png");
                break;
        }


        cloudSprites[FRONT_CLOUD] = clouds[(int) (Math.random() * clouds.length)];
        cloudSprites[BACK_CLOUD] = clouds[(int) (Math.random() * clouds.length)];

        for (int i = 0; i < cloudPositions.length; i++) {
            randomCloudXPosition = MathUtils.random(camera.viewportWidth, camera.viewportWidth * 2);
            randomCloudYPosition = MathUtils.random(mapHeight / 2 + cloudSprites[FRONT_CLOUD].getHeight() / 2, mapHeight - cloudSprites[i].getHeight());

            cloudPositions[i] = new Vector2(randomCloudXPosition, randomCloudYPosition);
            cloudSprites[i].setPosition(cloudPositions[i].x, cloudPositions[i].y);
        }

    }

    public void render(SpriteBatch batch) {
        scrollableMountainsSprite.draw(batch);
        scrollableGrassHillsSprite.draw(batch);
        batch.draw(cloudSprites[BACK_CLOUD], cloudPositions[BACK_CLOUD].x, cloudPositions[BACK_CLOUD].y);
        batch.draw(cloudSprites[FRONT_CLOUD], cloudPositions[FRONT_CLOUD].x, cloudPositions[FRONT_CLOUD].y);
    }

    public void tick(Player player, float deltaTime) {
        updateParallaxLayer(deltaTime, scrollableMountainsSprite, player, MOUNTAINS_X_SPEED);
        updateParallaxLayer(deltaTime, scrollableGrassHillsSprite, player, GRASSHILLS_X_SPEED);
        tickClouds();
    }

    private void tickClouds() {
        float centerX = camera.viewportWidth / 2;
        randomCloudXPosition = MathUtils.random(camera.position.x + centerX, camera.position.x + (centerX * 1.5f));
        randomCloudYPosition = MathUtils.random(mapHeight / 2 + cloudSprites[0].getHeight() / 2, mapHeight - cloudSprites[0].getHeight());

        for (int i = 0; i < cloudPositions.length; i++) {
            cloudPositions[i].x -= CLOUD_X_SPEED;

            if (cloudPositions[i].x + cloudSprites[i].getWidth() < 0) {
                cloudSprites[i] = clouds[MathUtils.random(clouds.length - 1)];
                cloudPositions[i].set(randomCloudXPosition, randomCloudYPosition);
                cloudSprites[i].setPosition(cloudPositions[i].x, cloudPositions[i].y);
            }
        }
    }

    private void updateParallaxLayer(float deltaTime, Sprite parallaxSprite, Player player, float speed) {
        if (canScrollHorizontally(player)) {
            parallaxSprite.scroll(player.isHeadingRight() ? deltaTime / speed : -(deltaTime / speed), 0);
        }

        parallaxSprite.setX(camera.position.x - (camera.viewportWidth / 2) - 5);
    }

    private boolean canScrollHorizontally(Player player) {
        return (camera.position.x - (camera.viewportWidth / 2) > 0) &&
                (camera.position.x + (camera.viewportWidth / 2) < mapWidth) &&
                (!player.isDead() && !player.isGoalReached() &&
                        !player.isNotMoving() && Dialog.screenTapped && player.isMoving());
    }


}

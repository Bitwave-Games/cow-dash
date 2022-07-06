package com.bitwave.cowdash.level;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.CowDash;
import com.bitwave.cowdash.objects.BouncingWall;
import com.bitwave.cowdash.objects.Door;
import com.bitwave.cowdash.objects.GameObject;
import com.bitwave.cowdash.objects.Player;
import com.bitwave.cowdash.objects.Ruby;
import com.bitwave.cowdash.objects.Teleport;
import com.bitwave.cowdash.objects.enemy.Enemy;
import com.bitwave.cowdash.objects.handler.BackgroundsHandler;
import com.bitwave.cowdash.objects.handler.GameObjectsHandler;
import com.bitwave.cowdash.screen.GameScreen;
import com.bitwave.cowdash.screen.ScreenType;
import com.bitwave.cowdash.screen.ingame.Dialog;
import com.bitwave.cowdash.screen.ingame.DialogHandler;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.language.Translator;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

import java.util.Iterator;
import java.util.Random;


public abstract class Level implements LevelBase, Disposable {

    public static final float MAX_ZOOM = 0.4f;

    protected int mapWidth;

    protected int mapHeight;

    protected TiledMap tileMap;
    protected TiledMapTileLayer backgroundMapLayer;
    protected TiledMapTileLayer hiddenMapLayer;
    protected TiledMapTileLayer mainLayer;
    protected TiledMapTileLayer foregroundMapLayer;
    protected OrthogonalTiledMapRenderer mapRenderer;
    protected Player player;
    protected Ruby ruby;
    protected Teleport teleport;


    protected Sprite healthSprite;

    protected Sprite healthBarSprite;


    protected ShapeRenderer debugRenderer;

    protected Color healthBarColor;
    protected Color backgroundColor;
    protected BackgroundsHandler backgroundsHandler;
    protected GameScreen gameScreen;
    protected DialogHandler dialogHandler;
    protected GameObjectsHandler gameObjectsHandler;
    protected Array<String> hints;
    protected String itemUnlockedType;

    protected boolean displayStartDialog = true;
    protected boolean isRubyTouched;
    protected boolean isItemUnlocked;
    protected float tickSpeed = 1.0f;
    protected float zoomLevel = 1.0f;
    protected byte tutorialLevelIndex;
    protected byte itemUnlockedNr;
    protected Array<RectangleMapObject> doorMapObjects;
    protected Array<RectangleMapObject> hogMapObjects;
    protected Array<RectangleMapObject> hardBlocks;
    protected Array<Rectangle> softBlocks;
    protected OrthographicCamera camera;

    protected float timeToBeat;
    protected short amountOfVeggiesForLevel;
    protected short veggiesCollected;
    protected boolean isAllVeggiesCollected;
    protected boolean isTimeBeaten;

    protected Random random = new Random();
    protected float rumbleX;
    protected float rumbleY;
    protected float rumbleTime = 0;
    protected float currentRumbleTime = 1;
    protected float rumblePower = 0;
    protected float currentRumblePower = 0;

    private final WorldType worldType;


    public Level(String levelPath, GameScreen gameScreen, WorldType worldType) {
        this.gameScreen = gameScreen;
        this.worldType = worldType;
        this.camera = gameScreen.getCamera();

        this.tileMap = (TiledMap) Assets.getInstance().get(levelPath);
        this.mapRenderer = new OrthogonalTiledMapRenderer(tileMap, gameScreen.getSpriteBatch());
        this.hints = getHintsFromXML(tileMap);
        this.mapWidth = tileMap.getProperties().get("width", Integer.class) * tileMap.getProperties().get("tilewidth", Integer.class);
        this.mapHeight = tileMap.getProperties().get("height", Integer.class) * tileMap.getProperties().get("tileheight", Integer.class);

        this.gameObjectsHandler = new GameObjectsHandler(tileMap, this);
        this.backgroundsHandler = new BackgroundsHandler(camera, mapWidth, mapHeight);

        String beatTime = tileMap.getProperties().get("time_to_beat", String.class);
        this.timeToBeat = beatTime == null ? 0 : Float.parseFloat(beatTime);
        this.amountOfVeggiesForLevel = gameObjectsHandler.getAmountOfVeggiesForLevel();
        this.dialogHandler = new DialogHandler(this);

        String tutorial = tileMap.getProperties().get("tutorial", String.class);
        this.tutorialLevelIndex = tutorial == null ? -1 : Byte.parseByte(tutorial);

        this.player = new Player(gameObjectsHandler.getSpawnPoint(), this);
        this.player.setHealth(gameObjectsHandler.getHealthProperties(tileMap));
        this.player.setLevel(this);
        this.player.setStartDirection(gameObjectsHandler.getPlayerStartDirection());

        this.healthBarColor = new Color(Color.WHITE);

        this.healthBarSprite = Assets.getInstance().getSprite("sprites/objects/health_bar_2.png");
        this.healthSprite = new Sprite((Texture) Assets.getInstance().get("sprites/in/whitetexture.png"));

        this.ruby = new Ruby(this, gameObjectsHandler.getGoalPoint());

        if (CowDash.FREE_FLYING_MODE || CowDash.SHOULD_RENDER_BOUNDING_BOXES) {
            this.debugRenderer = new ShapeRenderer();
        }

        this.backgroundMapLayer = (TiledMapTileLayer) tileMap.getLayers().get("background");
        this.hiddenMapLayer = (TiledMapTileLayer) tileMap.getLayers().get("hidden");
        this.mainLayer = (TiledMapTileLayer) tileMap.getLayers().get("main");
        this.foregroundMapLayer = (TiledMapTileLayer) tileMap.getLayers().get("foreground");

        this.doorMapObjects = gameObjectsHandler.getDoorMapObjects();
        this.hogMapObjects = gameObjectsHandler.getHogMapObjects();
        this.hardBlocks = tileMap.getLayers().get("hard-blocks").getObjects().getByType(RectangleMapObject.class);
        this.softBlocks = new Array<Rectangle>();
        Array<RectangleMapObject> softMapBlocks = tileMap.getLayers().get("soft-blocks").getObjects().getByType(RectangleMapObject.class);
        for (int i = 0; i < softMapBlocks.size; i++) {
            RectangleMapObject rmobj = softMapBlocks.get(i);
            float y = rmobj.getProperties().get("y", Float.class);
            Rectangle rect = softMapBlocks.get(i).getRectangle();
            rmobj.getProperties().put("y", y + rect.getHeight() - 1);
            rmobj.getProperties().put("height", 1);

            rect.y = rmobj.getProperties().get("y", Float.class);
            rect.height = rmobj.getProperties().get("height", Integer.class);
            softBlocks.add(rect);
        }
        this.setDoorYValues(-Door.HEIGHT);

        System.gc();
    }


    public WorldType getWorldType() {
        return worldType;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void rumble(float power, float time) {
        rumblePower = power;
        rumbleTime = time;
        currentRumbleTime = 0;
    }

    public Game getGame() {
        return gameScreen.getGame();
    }


    @Override
    public void switchLevel() {
        gameScreen.nextLevel();
    }

    public InputProcessor getBackButtonHandler() {
        return gameScreen.getBackInputProcessor();
    }

    @Override
    public void restart() {
        gameScreen.replayLevel();
    }

    public void resetPlayerContext() {
        player.setTextures();
        dialogHandler.resetContext();
    }

    @Override
    public void end(ScreenType screenToGoTo) {
        gameScreen.clearLevel(screenToGoTo);
    }

    public boolean isItemUnlocked() {
        return isItemUnlocked;
    }

    public boolean isRubyTouched() {
        return isRubyTouched;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public short getAmountOfVeggiesForThisLevel() {
        return amountOfVeggiesForLevel;
    }

    public short getVeggiesCollected() {
        return veggiesCollected;
    }

    public float getTimeToBeat() {
        return timeToBeat;
    }

    public boolean isAllVeggiesCollected() {
        return this.isAllVeggiesCollected;
    }

    public boolean isTimeBeaten() {

        return isTimeBeaten;
    }

    public void addVeggieCollected() {
        veggiesCollected++;
        if (veggiesCollected >= amountOfVeggiesForLevel) {
            isAllVeggiesCollected = true;
        }
    }

    public void setItemUnlocked(String itemType, byte itemUnlockedNr) {
        this.itemUnlockedType = itemType;
        this.itemUnlockedNr = itemUnlockedNr;
        this.isItemUnlocked = true;
    }

    public String getItemUnlockedType() {
        return this.itemUnlockedType;
    }

    public byte getItemUnlockedNr() {
        return itemUnlockedNr;
    }

    public boolean isFinalLevel() {
        return gameScreen.isFinalLevel();
    }

    public byte getCurrentLevelIndex() {
        return gameScreen.getLevelIndex();
    }

    public boolean isTutorialLevel() {
        return tutorialLevelIndex >= 0;
    }

    public byte getTutorialLevelIndex() {
        return tutorialLevelIndex;
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        Gdx.gl20.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1.0f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        mapRenderer.setView(camera);
        backgroundsHandler.render(batch);
        mapRenderer.renderTileLayer(backgroundMapLayer);
        mapRenderer.renderTileLayer(mainLayer);

        gameObjectsHandler.renderObjectsBehindPlayer(batch);
        player.render(batch);
        gameObjectsHandler.renderObjectsInFrontOfPlayer(batch);
        mapRenderer.renderTileLayer(foregroundMapLayer);
        ParticleHelper.getInstance().render(batch, false);
        renderHiddenLayer();
        ruby.render(batch);
        if (zoomLevel == 1f) {
            renderHealthBar(camera, batch);
        }
        gameObjectsHandler.renderObjectsInFrontOfHealthBar(batch);
        batch.end();

        dialogHandler.render();

        if (CowDash.FREE_FLYING_MODE || CowDash.SHOULD_RENDER_BOUNDING_BOXES) {
            debugRender(camera);
        }
    }

    @Override
    public void tick(float deltatime) {
        moveCamera(player, camera, deltatime);
        if (!Dialog.isPaused) {
            if (!player.isVisible() && !player.isGoalReached() && !player.isDead()) {
                player.setVisible(true);
            }
            ruby.tick(deltatime, null);
            player.tick(deltatime, null);
            gameObjectsHandler.tick(deltatime, player);
            ParticleHelper.getInstance().tick(deltatime, false);
            backgroundsHandler.tick(player, deltatime);
        }

        checkLevelStatus();
        dialogHandler.tick(deltatime, .2f);
    }

    public Rectangle getBoundsAt(Rectangle bounds, byte filter, GameObject collider) {
        if ((filter & Filter.HOG) == Filter.HOG) {
            for (RectangleMapObject block : hogMapObjects) {
                Rectangle hogRect = block.getRectangle();
                if (hogRect.overlaps(bounds)) {
                    return hogRect;
                }
            }
        }

        if ((filter & Filter.HARD) == Filter.HARD) {
            for (RectangleMapObject block : hardBlocks) {
                Rectangle hardRect = block.getRectangle();
                if (hardRect.overlaps(bounds)) {
                    return hardRect;
                }
            }
        }

        if ((filter & Filter.SOFT) == Filter.SOFT) {
            for (Rectangle softRect : softBlocks) {
                if (softRect.overlaps(bounds)) {
                    return softRect;
                }
            }
        }

        if ((filter & Filter.DOOR) == Filter.DOOR && doorMapObjects != null) {
            Iterator<RectangleMapObject> iterator = doorMapObjects.iterator();
            while (iterator.hasNext()) {
                RectangleMapObject block = iterator.next();
                Rectangle blockRectangle = block.getRectangle();
                blockRectangle.y = block.getProperties().get("y", Float.class);
                blockRectangle.width = block.getProperties().get("width", Integer.class);
                blockRectangle.height = block.getProperties().get("height", Integer.class);
                boolean isPlayer = collider instanceof Player;

                if (blockRectangle.overlaps(bounds)) {
                    String doorLevel = block.getProperties().get("color", String.class);
                    boolean vibrationEnabled = !CowPreferences.getInstance().isVibrationDisabled();
                    if (doorLevel.equalsIgnoreCase("blue") && player.haveBlueKeys() && isPlayer) {
                        if (vibrationEnabled) {
                            Gdx.input.vibrate(120);
                        }
                        player.removeBlueKey();
                        gameObjectsHandler.removeDoorAt(block.getRectangle());
                        doorMapObjects.removeValue(block, true);
                        return null;
                    } else if (doorLevel.equalsIgnoreCase("red") && player.haveRedKeys() && isPlayer) {
                        if (vibrationEnabled) {
                            Gdx.input.vibrate(120);
                        }
                        player.removeRedKey();
                        gameObjectsHandler.removeDoorAt(block.getRectangle());
                        doorMapObjects.removeValue(block, true);
                        return null;
                    } else if (doorLevel.equalsIgnoreCase("yellow") && player.haveYellowKeys() && isPlayer) {
                        if (vibrationEnabled) {
                            Gdx.input.vibrate(120);
                        }
                        player.removeYellowKey();
                        gameObjectsHandler.removeDoorAt(block.getRectangle());
                        doorMapObjects.removeValue(block, true);
                        return null;
                    }

                    if (doorLevel.equalsIgnoreCase("oneway")) {
                        String direction = block.getProperties().get("direction", String.class);
                        if (direction.equalsIgnoreCase("left") && collider.isHeadingLeft()) {
                            return null;
                        } else if (direction.equalsIgnoreCase("right") && collider.isHeadingRight()) {
                            return null;
                        } else if (direction.equalsIgnoreCase("down") && collider.isFalling()) {
                            return null;
                        }
                    }
                    return block.getRectangle();
                }
            }
        }

        return null;
    }

    public void addHardBlockAt(Vector2 position) {
        this.hardBlocks.add(new RectangleMapObject(position.x, position.y, 32, 32));
    }

    public void moveCamera(Player player, OrthographicCamera camera, float deltatime) {
        camera.zoom = zoomLevel;
        float centerX = (camera.viewportWidth * zoomLevel) / 2;
        float centerY = (camera.viewportHeight * zoomLevel) / 2;


        if (isRubyTouched) {
            camera.position.set(ruby.getPosition().x + 16, ruby.getPosition().y + 16, 0);
        } else {
            camera.position.set(player.getPosition().x + 16, player.getPosition().y + 16, 0);
        }

        if (camera.position.x - centerX <= 0)
            camera.position.x = centerX;
        if (camera.position.x + centerX >= mapWidth)
            camera.position.x = mapWidth - centerX;
        if (camera.position.y - centerY <= 0)
            camera.position.y = centerY;
        if (camera.position.y + centerY >= mapHeight)
            camera.position.y = mapHeight - centerY;

        if (currentRumbleTime <= rumbleTime) {
            currentRumblePower = rumblePower * ((rumbleTime - currentRumbleTime) / rumbleTime);

            rumbleX = (random.nextFloat() - 0.5f) * 2 * currentRumblePower;
            rumbleY = (random.nextFloat() - 0.5f) * 2 * currentRumblePower;

            if (camera.position.x + centerX + rumbleX >= mapWidth) {
                rumbleX *= -1;
            }
            if (camera.position.y + centerY + rumbleY >= mapHeight) {
                rumbleY *= -1;
            }
            if (camera.position.x - centerX + rumbleX <= 0) {
                rumbleX = 0;
            }
            if (camera.position.y - centerY + rumbleY <= 0) {
                rumbleY = 0;
            }

            camera.translate(rumbleX, rumbleY);
            currentRumbleTime += deltatime;
        }

        camera.update();
    }

    public float getCompletionTime() {
        return dialogHandler.getCompletionTime();
    }

    public void death() {
        tickSpeed = 1;
        if (!player.isDeathFromFalling()) {
            zoomLevel = MAX_ZOOM;
        }
        dialogHandler.showDeathBox();
    }

    protected void renderHiddenLayer() {
        if (hiddenMapLayer == null)
            return;

        Rectangle playerBounds = player.getBounds();
        int cellWidth = tileMap.getProperties().get("tilewidth", Integer.class);
        int cellHeight = tileMap.getProperties().get("tileheight", Integer.class);
        TiledMapTileLayer.Cell cell = hiddenMapLayer.getCell((int) (playerBounds.x + (player.getTextureWidth() / 2)) / cellWidth,
                (int) (playerBounds.y + (player.getTextureHeight() / 2)) / cellHeight);

        if (cell != null) {
            hiddenMapLayer.setOpacity(0.5f);
        } else {
            hiddenMapLayer.setOpacity(1f);
        }
        mapRenderer.renderTileLayer(hiddenMapLayer);
    }

    protected void checkLevelStatus() {
        if (player.isDead()) {
            death();
        }

        Vector2 playerCenter = new Vector2(player.getPosition().x + (player.getTextureWidth() / 2), player.getPosition().y + (player.getTextureHeight() / 2));
        Vector2 rubyCenter = new Vector2(ruby.getPosition().x + (ruby.getWidth() / 2), ruby.getPosition().y + (ruby.getHeight() / 2));
        float distance = playerCenter.dst(rubyCenter);
        final float EVENT_HORIZON = 40.0f;

        if (distance < EVENT_HORIZON && !isRubyTouched && !player.isDead()) {
            if (player.getBounds().overlaps(ruby.getBounds())) {
                AudioUtils audioUtils = AudioUtils.getInstance();
                audioUtils.playSoundFX("goalReached");
                player.setVisible(false);
                if (dialogHandler.getCompletionTime() <= timeToBeat) {
                    if (!CowPreferences.getInstance().getTimeMedalAcquired(getCurrentLevelIndex(), worldType)) {
                        ParticleHelper.getInstance().addChestEffect(ruby.getPosition().x + (32 / 2), ruby.getPosition().y + (32 / 2), true);
                        audioUtils.playSoundFX("medal");
                    }
                    isTimeBeaten = true;
                }

                isRubyTouched = true;
                player.setGoalReached(true);
                zoomLevel = MAX_ZOOM;
            }
        }
        if (distance > EVENT_HORIZON && !isRubyTouched) {
            if (zoomLevel <= 1) {
                zoomLevel += .01f;

                if (zoomLevel > 1) {
                    zoomLevel = 1;
                }
            }
        }

    }

    @Override
    public void dispose() {
        dialogHandler.dispose();
        if (CowDash.FREE_FLYING_MODE || CowDash.SHOULD_RENDER_BOUNDING_BOXES) {
            debugRenderer.dispose();
        }
        gameObjectsHandler.dispose();
        ruby.dispose();
        mapRenderer.dispose();
    }

    public Array<String> getHintsFromXML(TiledMap map) {
        String hintProperties = map.getProperties().get("hints", String.class);
        String[] hints = hintProperties.split(",");
        return Translator.getInstance().getHints(hints);
    }

    private void setDoorYValues(float value) {
        if (doorMapObjects != null) {
            for (int i = 0; i < doorMapObjects.size; i++) {
                if (doorMapObjects.get(i).getProperties().get("width") == null) {
                    doorMapObjects.get(i).getProperties().put("width", 32);
                }
                if (doorMapObjects.get(i).getProperties().get("height") == null) {
                    doorMapObjects.get(i).getProperties().put("height", 32);
                }
            }
        }
    }

    private void renderHealthBar(OrthographicCamera camera, SpriteBatch batch) {
        healthBarSprite.setPosition(camera.position.x - (camera.viewportWidth / 2) + 4f, camera.position.y + ((camera.viewportHeight / 2) - healthSprite.getHeight() * 1.4f) - 4f);

        float playerHealth = player.getHealth();
        if (playerHealth > 75)
            healthBarColor.set(Color.GREEN);
        else if (playerHealth <= 75 && playerHealth > 50)
            healthBarColor.set(Color.YELLOW);
        else
            healthBarColor.set(Color.ORANGE);
        float currentHealth = (Player.MAX_HEALTH - playerHealth);

        healthSprite.setBounds(healthBarSprite.getX(), healthBarSprite.getY() + (healthBarSprite.getHeight() * 0.125f),
                playerHealth < 100 ? healthBarSprite.getWidth() * (playerHealth / 100) : healthBarSprite.getWidth(),
                (healthBarSprite.getHeight() * 0.708333333f));
        healthSprite.setColor(healthBarColor.lerp(Color.RED, currentHealth / Player.MAX_HEALTH));
        healthSprite.draw(batch);
        healthBarSprite.draw(batch);
    }

    private void debugRender(OrthographicCamera camera) {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeType.Line);

        debugRenderer.setColor(Color.CYAN);
        debugRenderer.rect(player.getBounds().x, player.getBounds().y, player.getBounds().width, player.getBounds().height);
        debugRenderer.setColor(Color.WHITE);
        debugRenderer.rect(ruby.getBounds().x, ruby.getBounds().y, ruby.getBounds().width, ruby.getBounds().height);
        debugRenderer.rect(ruby.getInnerBounds().x, ruby.getInnerBounds().y, ruby.getInnerBounds().width, ruby.getInnerBounds().height);

        for (Door door : gameObjectsHandler.getDoors()) {
            Rectangle r = door.getBounds();
            debugRenderer.rect(r.x, r.y, r.width, r.height);
        }
        for (Enemy enemy : gameObjectsHandler.getEnemies()) {
            Rectangle r = enemy.getBounds();
            Rectangle b = enemy.movingBounds;
            debugRenderer.rect(r.x, r.y, r.width, r.height);
            debugRenderer.rect(b.x, b.y, b.width, b.height);
        }
        debugRenderer.setColor(Color.WHITE);
        for (Teleport teleport : gameObjectsHandler.getTeleport()) {
            Rectangle r = teleport.getBounds();
            debugRenderer.rect(r.x, r.y, r.width, r.height);
        }

        debugRenderer.setColor(Color.BLUE);
        for (Rectangle r : softBlocks) {
            debugRenderer.rect(r.x, r.y, r.width, r.height);
        }
        debugRenderer.setColor(Color.RED);
        for (RectangleMapObject mo : hardBlocks) {
            Rectangle r = mo.getRectangle();
            debugRenderer.rect(r.x, r.y, r.width, r.height);
        }
        for (BouncingWall wall : gameObjectsHandler.getBouncingWalls()) {
            Rectangle r = wall.getBounds();
            debugRenderer.rect(r.x, r.y, r.width, r.height);
        }
        debugRenderer.setColor(Color.YELLOW);
        Rectangle r = player.movingBounds;
        debugRenderer.rect(r.x, r.y, r.width, r.height);

        debugRenderer.end();
    }

    public static class Filter {
        public static final byte HARD = 1;
        public static final byte SOFT = 2;
        public static final byte DOOR = 4;
        public static final byte SAND = 8;
        public static final byte HOG = 16;
    }
}
package com.bitwave.cowdash.objects.handler;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.objects.Birdy;
import com.bitwave.cowdash.objects.BouncingWall;
import com.bitwave.cowdash.objects.Chest;
import com.bitwave.cowdash.objects.Door;
import com.bitwave.cowdash.objects.GoldenVeggie;
import com.bitwave.cowdash.objects.Key;
import com.bitwave.cowdash.objects.ObjectType;
import com.bitwave.cowdash.objects.Player;
import com.bitwave.cowdash.objects.Teleport;
import com.bitwave.cowdash.objects.Veggie;
import com.bitwave.cowdash.objects.enemy.BlockEnemy;
import com.bitwave.cowdash.objects.enemy.Enemy;
import com.bitwave.cowdash.objects.enemy.JumpingEnemy;
import com.bitwave.cowdash.objects.enemy.Spike;
import com.bitwave.cowdash.objects.enemy.TrampolineHog;
import com.bitwave.cowdash.objects.enemy.WalkingEnemy;
import com.bitwave.cowdash.objects.scenery.BeachBush;
import com.bitwave.cowdash.objects.scenery.BigTreeTop;
import com.bitwave.cowdash.objects.scenery.Bush;
import com.bitwave.cowdash.objects.scenery.FlagTreeTop;
import com.bitwave.cowdash.objects.scenery.PalmTreeTop;
import com.bitwave.cowdash.objects.scenery.Scenery;
import com.bitwave.cowdash.objects.scenery.SmallBirchTreeTop;
import com.bitwave.cowdash.objects.scenery.SmallMapleTreeTop;
import com.bitwave.cowdash.utils.AudioUtils;

public class GameObjectsHandler implements Disposable {

    private Level level;
    private Array<Door> doors;
    private Array<Key> keys;
    private Array<Veggie> veggies;
    private Array<GoldenVeggie> goldenVeggies;
    private Array<Teleport> teleporters;
    private Array<Scenery> frontScenery;
    private Array<Scenery> backScenery;
    private Array<Enemy> enemies;
    private Array<Chest> chests;
    private Array<Birdy> birdy;
    private Array<BouncingWall> bouncingWalls;
    private Array<RectangleMapObject> doorMapObjects;
    private Array<RectangleMapObject> hogMapObjects;
    private Vector2 spawnPoint;
    private Vector2 goalPoint;
    private String playerStartDirection;
    private float playerStartingHealth;
    private short amountOfVeggiesForLevel;

    public GameObjectsHandler(TiledMap map, Level level) {
        this.level = level;
        this.spawnPoint = new Vector2();
        this.goalPoint = new Vector2();
        this.doorMapObjects = new Array<RectangleMapObject>();

        this.birdy = new Array<Birdy>();
        this.keys = new Array<Key>();
        this.veggies = new Array<Veggie>();
        this.goldenVeggies = new Array<GoldenVeggie>();
        this.teleporters = new Array<Teleport>();
        this.doors = new Array<Door>();
        this.enemies = new Array<Enemy>();
        this.chests = new Array<Chest>();
        this.frontScenery = new Array<Scenery>();
        this.backScenery = new Array<Scenery>();
        this.bouncingWalls = new Array<BouncingWall>();
        this.hogMapObjects = new Array<RectangleMapObject>();
        this.getObjectsFromMapById(map, "objects-32px", "objects-16px", "treetop_big");
    }

    protected GameObjectsHandler() {
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Array<Teleport> getTeleport() {
        return teleporters;
    }

    public short getAmountOfVeggiesForLevel() {
        return amountOfVeggiesForLevel;
    }

    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    public Vector2 getGoalPoint() {
        return goalPoint;
    }

    public String getPlayerStartDirection() {
        return playerStartDirection;
    }

    public Array<RectangleMapObject> getDoorMapObjects() {
        return doorMapObjects;
    }

    public Array<Door> getDoors() {
        return doors;
    }

    public void renderObjectsBehindPlayer(SpriteBatch batch) {
        for (Key key : keys) {
            key.render(batch);
        }
        for (Chest chest : chests) {
            chest.render(batch);
        }
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
        for (Scenery scenery : backScenery) {
            scenery.render(batch);
        }
        for (BouncingWall bouncingWall : bouncingWalls) {
            bouncingWall.render(batch);
        }
    }

    public void renderObjectsInFrontOfPlayer(SpriteBatch batch) {
        for (Door door : doors) {
            door.render(batch);
        }
        for (Scenery scenery : frontScenery) {
            scenery.render(batch);
        }
        for (Birdy bird : birdy) {
            bird.render(batch);
        }
        for (Veggie veggie : veggies) {
            if (!veggie.isIncreaseHealth()) {
                veggie.render(batch);
            }
        }
        for (GoldenVeggie goldenVeggie : goldenVeggies) {
            if (!goldenVeggie.isIncreaseHealth()) {
                goldenVeggie.render(batch);
            }
        }
        for (Teleport teleport : teleporters) {
            teleport.render(batch);
        }
    }

    public void renderParticles(SpriteBatch batch) {
    }

    public void renderObjectsInFrontOfHealthBar(SpriteBatch batch) {
        for (GoldenVeggie goldenVeggie : goldenVeggies) {
            if (goldenVeggie.isIncreaseHealth()) {
                goldenVeggie.render(batch);
            }
        }
        for (Veggie veggie : veggies) {
            if (veggie.isIncreaseHealth()) {
                veggie.render(batch);
            }
        }
    }

    public void removeDoorAt(Rectangle rectangle) {
        for (Door door : doors) {
            if (door.getBounds().x == rectangle.x && door.getBounds().y == rectangle.y) {
                AudioUtils.getInstance().playSoundFX("opendoor");
                door.startParticleEffect();
                level.rumble(3.0f, .3f);
            }
        }
    }

    public void tick(float deltaTime, Player player) {
        for (Veggie veggie : veggies) {
            veggie.tick(deltaTime, player);
            if (veggie.isCollidedWith()) {
                veggies.removeValue(veggie, true);
            }
        }
        for (Key key : keys) {
            key.tick(deltaTime, player);
            if (key.isCollidedWith()) {
                keys.removeValue(key, true);
            }
        }
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).tick(deltaTime, player, enemies);
            if (enemies.get(i).isCollidedWith()
                    && !enemies.get(i).getObjectType().equals(ObjectType.ENEMY_BLOCK)
                    && !enemies.get(i).getObjectType().equals(ObjectType.ENEMY_TRAMP_HOG)) {
                enemies.removeValue(enemies.get(i), true);
            }
        }
        for (Chest chest : chests) {
            chest.tick(deltaTime, player);
            if (chest.isCollidedWith()) {
                level.setItemUnlocked(chest.getType(), chest.getNr());
            }
        }
        for (Scenery scenery : frontScenery) {
            scenery.tick(deltaTime, player);
        }
        for (Scenery scenery : backScenery) {
            scenery.tick(deltaTime, player);
        }
        for (Door door : doors) {
            if (door.isOneWayDoor()) {
                door.tick(deltaTime, player, enemies);
            } else {
                if (door.isCollidedWith()) {
                    doors.removeValue(door, true);
                }
                door.tick(deltaTime, player);
            }
        }
        for (GoldenVeggie goldenVeggie : goldenVeggies) {
            goldenVeggie.tick(deltaTime, player);
            if (goldenVeggie.isCollidedWith()) {
                goldenVeggies.removeValue(goldenVeggie, true);
            }
        }
        for (Birdy bird : birdy) {
            if (bird.isFinishedTween()) {
                birdy.removeValue(bird, true);
            }
            bird.tick(deltaTime, player);
        }
        for (BouncingWall bouncingWall : bouncingWalls) {
            bouncingWall.tick(deltaTime, player);
        }

        for (Teleport teleport : teleporters) {
            teleport.tick(deltaTime, player);
        }

    }

    private void getObjectsFromMapById(TiledMap map, String... layerNames) {
        for (String layerName : layerNames) {
            MapObjects mapObjects = map.getLayers().get(layerName).getObjects();

            for (int i = 0; i < mapObjects.getCount(); i++) {
                MapObject mapObject = mapObjects.get(i);
                String objectName = mapObject.getName();
                float x = mapObject.getProperties().get("x", Float.class);
                float y = mapObject.getProperties().get("y", Float.class);

                if (objectName != null)
                    if (objectName.equalsIgnoreCase("piggie")) {
                        RectangleMapObject rmo = new RectangleMapObject(x, y, 32, 32);
                        rmo.getProperties().put("x", x);
                        rmo.getProperties().put("y", y);
                        this.enemies.add(new TrampolineHog(new Vector2(x, y), level));
                        this.hogMapObjects.add(rmo);
                    } else if (objectName.equalsIgnoreCase("chest")) {
                        String item = mapObject.getProperties().get("item_no", String.class);
                        String typeOfItem = mapObject.getProperties().get("type", String.class);
                        byte itemNr = Byte.parseByte(item);
                        this.chests.add(new Chest(new Vector2(x, y), level, itemNr, typeOfItem));
                    } else if (objectName.equalsIgnoreCase("walking_enemy")) {
                        String boundToPlatForm = mapObject.getProperties().get("bound_to_platform", String.class);
                        String direction = mapObject.getProperties().get("direction", String.class);
                        boolean b = false;
                        if (boundToPlatForm != null) {
                            b = boundToPlatForm.equalsIgnoreCase("true");
                        }
                        this.enemies.add(new WalkingEnemy(new Vector2(x, y), direction, b, level));
                    } else if (objectName.equalsIgnoreCase("jumping_enemy")) {
                        String direction = mapObject.getProperties().get("direction", String.class);
                        this.enemies.add(new JumpingEnemy(new Vector2(x, y), direction, level));
                    } else if (objectName.equalsIgnoreCase("door")) {
                        String color = mapObject.getProperties().get("color", String.class);
                        String directionOpenedFrom = mapObject.getProperties().get("direction", String.class);
                        this.doors.add(new Door(new Vector2(x, y), level, color, directionOpenedFrom));
                        RectangleMapObject rmo = new RectangleMapObject(x, y, 32, 32);
                        rmo.getProperties().put("x", x);
                        rmo.getProperties().put("y", y);
                        rmo.getProperties().put("color", color);
                        rmo.getProperties().put("direction", directionOpenedFrom);
                        this.doorMapObjects.add(rmo);
                    } else if (objectName.equalsIgnoreCase("key")) {
                        String color = mapObject.getProperties().get("color", String.class);
                        this.keys.add(new Key(new Vector2(x, y), level, color));
                    } else if (objectName.equalsIgnoreCase("spike")) {
                        this.enemies.add(new Spike(new Vector2(x, y), level));
                    } else if (objectName.equalsIgnoreCase("veggie")) {
                        amountOfVeggiesForLevel++;
                        this.veggies.add(new Veggie(new Vector2(x, y), level));
                    } else if (objectName.equalsIgnoreCase("start")) {
                        this.spawnPoint.set(x, y);
                        this.playerStartDirection = mapObject.getProperties().get("direction", String.class);
                        this.playerStartingHealth = Float.parseFloat(mapObject.getProperties().get("health", String.class));
                        if (playerStartDirection == null) {
                            playerStartDirection = "right";
                        }
                    } else if (objectName.equalsIgnoreCase("goal")) {
                        this.goalPoint.set(x, y);
                    } else if (objectName.equalsIgnoreCase("golden_veggie")) {
                        this.goldenVeggies.add(new GoldenVeggie(new Vector2(x, y), level));
                    } else if (isSceneryLayer(objectName)) {
                        addSceneryToCorrectLayer(mapObject, objectName);
                    } else if (objectName.equalsIgnoreCase("bird")) {
                        this.birdy.add(new Birdy(new Vector2(x, y), level));
                    } else if (objectName.equalsIgnoreCase("block_enemy")) {
                        String boundToPlatForm = mapObject.getProperties().get("bound_to_platform", String.class);
                        String direction = mapObject.getProperties().get("direction", String.class);
                        boolean b = false;
                        if (boundToPlatForm != null) {
                            b = boundToPlatForm.equalsIgnoreCase("true");
                        }
                        String state = mapObject.getProperties().get("state", String.class);
                        byte s = 0;
                        if (state != null) {
                            if (state.equalsIgnoreCase("solid")) {
                                s = 0;
                            } else {
                                s = 1;
                            }
                        } else {
                            s = 1;
                        }
                        this.enemies.add(new BlockEnemy(new Vector2(x, y), direction, b, level, s));
                    } else if (objectName.equalsIgnoreCase("bouncing_wall")) {
                        this.bouncingWalls.add(new BouncingWall(new Vector2(x, y), level, 32, 16, mapObject.getProperties().get("direction", String.class)));
                    } else if (objectName.equalsIgnoreCase("teleport")) {

                        int mapHeight = map.getProperties().get("height", Integer.class) * 32;
                        float newX = Float.parseFloat(mapObject.getProperties().get("newX", String.class));
                        float newY = mapHeight - Float.parseFloat(mapObject.getProperties().get("newY", String.class));
                        this.teleporters.add(new Teleport(new Vector2(x, y), level, new Vector2(newX, newY)));
                    }
            }
        }
    }

    private void addSceneryToCorrectLayer(MapObject sceneryMapObject, String objectName) {
        Scenery scenery = null;
        float x = sceneryMapObject.getProperties().get("x", Float.class);
        float y = sceneryMapObject.getProperties().get("y", Float.class);

        if (objectName.equalsIgnoreCase("bushes") || objectName.equalsIgnoreCase("bush")) {
            scenery = new Bush(new Vector2(x, y), level);
        } else if (objectName.equalsIgnoreCase("treetops-big") || objectName.equalsIgnoreCase("treetop_big")) {
            scenery = new BigTreeTop(new Vector2(x, y), level);
        } else if (objectName.equalsIgnoreCase("treetops_small_maple") || objectName.equalsIgnoreCase("treetop_maple")) {
            scenery = new SmallMapleTreeTop(new Vector2(x, y), level);
        } else if (objectName.equalsIgnoreCase("treetops_small_birch") || objectName.equalsIgnoreCase("treetop_birch")) {
            scenery = new SmallBirchTreeTop(new Vector2(x, y), level);
        } else if (objectName.equalsIgnoreCase("treetop_palm")) {
            scenery = new PalmTreeTop(new Vector2(x, y), level);
        } else if (objectName.equalsIgnoreCase("beach_bush")) {
            scenery = new BeachBush(new Vector2(x, y), level);
        } else if (objectName.equalsIgnoreCase("treetop_flag")) {
            scenery = new FlagTreeTop(new Vector2(x, y), level);
        }

        String position = sceneryMapObject.getProperties().get("position", String.class);
        if (position != null) {
            if (position.equalsIgnoreCase("front")) {
                this.frontScenery.add(scenery);
            } else {
                this.backScenery.add(scenery);
            }
        } else {
            this.backScenery.add(scenery);
        }
    }

    private boolean isSceneryLayer(String objectName) {
        return objectName.equalsIgnoreCase("treetops-big") ||
                objectName.equalsIgnoreCase("bush") ||
                objectName.equalsIgnoreCase("treetop_maple") ||
                objectName.equalsIgnoreCase("treetop_birch") ||
                objectName.equalsIgnoreCase("treetop_big") ||
                objectName.equalsIgnoreCase("treetop_palm") ||
                objectName.equalsIgnoreCase("beach_bush") ||
                objectName.equalsIgnoreCase("treetop_flag");
    }

    public float getHealthProperties(TiledMap map) {
        return playerStartingHealth == 0 ? Player.MAX_HEALTH : playerStartingHealth;
    }

    public Array<BouncingWall> getBouncingWalls() {
        return bouncingWalls;
    }

    @Override
    public void dispose() {
        for (Door door : doors) {
            door.dispose();
        }
        for (Key key : keys) {
            key.dispose();
        }
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        for (Veggie veggie : veggies) {
            veggie.dispose();
        }
    }

    public Array<RectangleMapObject> getHogMapObjects() {
        return hogMapObjects;
    }

}
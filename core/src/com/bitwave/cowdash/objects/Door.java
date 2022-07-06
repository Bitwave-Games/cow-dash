package com.bitwave.cowdash.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.objects.enemy.Enemy;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.ParticleHelper;

public class Door extends GameObject implements Disposable {

    public static final byte HEIGHT = 32;

    private final byte CLOSED = 0;
    private final byte OPENED = 1;
    private final byte DOOR_FRAMES = 2;
    private byte status = CLOSED;
    private boolean isOneWayDoor;
    private boolean isOpened;
    private TextureRegion[] frames;
    private int type;

    public Door(Vector2 position, Level level, String typeOfDoor, String directionUnlockedFrom) {
        super(position, level, 32, 32, ObjectType.OBJECT_DOOR);
        this.currentFrame = getDoorLevelSprite(directionUnlockedFrom, typeOfDoor);
        if(typeOfDoor.equalsIgnoreCase("oneway")) {
            this.frames = getDoorFrames(directionUnlockedFrom);
        }
    }

    public boolean isOneWayDoor() {
        return isOneWayDoor;
    }

    @Override
    public void render(SpriteBatch batch) {
        if(isOneWayDoor) {
            batch.draw(frames[status], position.x, position.y);
        } else {
            if(!isOpened) {
                batch.draw(currentFrame, position.x, position.y);
            }
        }
    }

    @Override
    public void tick(float deltaTime, Player player) {

    }

    public void tick(float deltaTime, Player player, Array<Enemy> enemies) {
        boolean collided = false;
        if(player.getBounds().overlaps(bounds) && isOneWayDoor && !collided) {
            collided = true;
        }

        for(Enemy enemy : enemies) {
            if(enemy.getBounds().overlaps(bounds) && isOneWayDoor && !collided) {
                collided = true;
            }
        }

        if(collided) {
            if(status != OPENED) {
                AudioUtils.getInstance().playSoundFX("openOneWayDoor");
            }
            status = OPENED;
        } else {
            if(status != CLOSED) {
                AudioUtils.getInstance().playSoundFX("closeOneWayDoor");
            }
            status = CLOSED;
        }
    }

    public void startParticleEffect() {
        if(!isOpened) {
            if (!isOneWayDoor) {
                switch(type) {
                    case 0:
                        ParticleHelper.getInstance().addDoorBlueEffect(position.x + 16, position.y + 16, true);
                        break;
                    case 1:
                        ParticleHelper.getInstance().addDoorRedEffect(position.x + 16, position.y + 16, true);
                        break;
                    case 2:
                        ParticleHelper.getInstance().addDoorYellowEffect(position.x + 16, position.y + 16, true);
                        break;
                }
            }
            isOpened = true;
        }
    }

    private Sprite getDoorLevelSprite(String directionUnlockedFrom, String typeOfDoor) {
        String texturePath = null;
        if(!typeOfDoor.equalsIgnoreCase("oneway")) {

            if(typeOfDoor.equalsIgnoreCase("blue")) {
                texturePath = "sprites/objects/doors/block_door_blue.png";
                type = 0;
            } else if(typeOfDoor.equalsIgnoreCase("red")) {
                texturePath = "sprites/objects/doors/block_door_red.png";
                type = 1;
            } else if(typeOfDoor.equalsIgnoreCase("yellow")) {
                texturePath = "sprites/objects/doors/block_door_yellow.png";
                type = 2;
            }
        } else {
            this.isOneWayDoor = true;
            if(directionUnlockedFrom.equalsIgnoreCase("left")) {
                texturePath = "sprites/objects/doors/LEFTonewaydoor_spritesheet.png";
            } else if(directionUnlockedFrom.equalsIgnoreCase("right")) {
                texturePath = "sprites/objects/doors/RIGHTonewaydoor_spritesheet.png";
            } else if(directionUnlockedFrom.equalsIgnoreCase("down")) {
                texturePath = "sprites/objects/doors/DOWNonewaydoor_spritesheet.png";
            }
        }
        return Assets.getInstance().getSprite(texturePath);
    }

    private TextureRegion[] getDoorFrames(String directionUnlockedFrom) {
        TextureRegion reg = new TextureRegion(currentFrame.getTexture());
        float offset = 16f;
        if(directionUnlockedFrom.equalsIgnoreCase("right")) {
            return getOneWayDoor(reg, 0, false, 0f, -offset, offset, offset, -offset, 0f);
        } else if(directionUnlockedFrom.equalsIgnoreCase("left")) {
            return getOneWayDoor(reg, 0, false, -offset, -offset, offset, offset, -offset, 0f);
        } else if(directionUnlockedFrom.equalsIgnoreCase("down")) {
            return getOneWayDoor(reg, 0, false, -offset, -offset, offset, offset, 0f, -offset);
        }
        return null;
    }

    private TextureRegion[] getOneWayDoor(TextureRegion reg, int startXInSpriteSheet, boolean flipX,
                                          float positionOffsetX, float positionOffsetY, float boundsOffsetX, float boundsOffsetY, float boundsOffsetWidth, float boundsOffsetHeight) {
        TextureRegion[] tmp = new TextureRegion[DOOR_FRAMES];
        int width = reg.getRegionWidth() / 2;
        int height = reg.getRegionHeight();
        for(int i = 0; i < tmp.length; i++) {
            tmp[i] = new TextureRegion(reg, startXInSpriteSheet, 0, width, height);
            tmp[i].flip(flipX, false);
            startXInSpriteSheet += width;
        }
        setPosition(position.x + positionOffsetX, position.y + positionOffsetY);
        bounds.set(position.x + boundsOffsetX, position.y + boundsOffsetY, bounds.width + boundsOffsetWidth, bounds.height + boundsOffsetHeight);
        return tmp;
    }

    @Override
    public void dispose() {

    }

}
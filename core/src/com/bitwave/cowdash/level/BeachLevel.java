package com.bitwave.cowdash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bitwave.cowdash.objects.GameObject;
import com.bitwave.cowdash.objects.Player;
import com.bitwave.cowdash.screen.GameScreen;
import com.bitwave.cowdash.utils.WorldType;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public class BeachLevel extends Level {

    private final Array<RectangleMapObject> sandBlocks;

    public BeachLevel(String levelPath, GameScreen gameScreen) {
        super(levelPath, gameScreen, WorldType.BEACH);
        this.backgroundColor = new Color(254f / 255f, 126f / 255f, 126f / 255f, 1.0f);
        this.sandBlocks = tileMap.getLayers().get("sand-blocks").getObjects().getByType(RectangleMapObject.class);
    }

    @Override
    public Rectangle getBoundsAt(Rectangle bounds, byte filter, GameObject collider) {
        if ((filter & Filter.HARD) == Filter.HARD) {
            for (RectangleMapObject block : hardBlocks) {
                Rectangle hardRect = block.getRectangle();
                if (hardRect.overlaps(bounds)) {
                    player.setAbleToWallJump(false);
                    return hardRect;
                }
            }
        }

        if ((filter & Filter.SOFT) == Filter.SOFT) {
            for (Rectangle softRect : softBlocks) {
                if (softRect.overlaps(bounds)) {
                    player.setAbleToWallJump(false);
                    return softRect;
                }
            }
        }

        if ((filter & Filter.SAND) == Filter.SAND && sandBlocks != null) {
            for (RectangleMapObject block : sandBlocks) {
                Rectangle sandRect = block.getRectangle();
                if (sandRect.overlaps(bounds)) {
                    player.setAbleToWallJump(true);
                    return sandRect;
                }
            }
        }

        if ((filter & Filter.DOOR) == Filter.DOOR && doorMapObjects != null) {
            for (RectangleMapObject block : doorMapObjects) {
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

                    player.setAbleToWallJump(false);
                    return block.getRectangle();
                }
            }
        }

        return null;
    }



}

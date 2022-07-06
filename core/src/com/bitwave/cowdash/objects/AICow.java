package com.bitwave.cowdash.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bitwave.cowdash.objects.item.Back;
import com.bitwave.cowdash.objects.item.Body;
import com.bitwave.cowdash.objects.item.Head;
import com.bitwave.cowdash.objects.item.Leg;
import com.bitwave.cowdash.objects.item.Mask;
import com.bitwave.cowdash.utils.ItemUtils;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public class AICow extends Image {

    private final float ANIM_SPEED_NORMAL = 0.10f;
    private final boolean showUnlockedItem;
    private final float y;
    private final float speed = 360f;
    private boolean shouldRun;
    private boolean shouldMove;
    private float stateTime;
    private float x;
    private float width;
    private float height;
    private Color color;
    private TextureRegion cowSpriteSheet;
    private TextureRegion currentFrame;
    private Animation rightWalk;

    public AICow(float x, float y, String itemTypeUnlocked, byte itemIndex, boolean showUnlockedItem, boolean shouldRun) {
        this.x = x;
        this.y = y;
        this.showUnlockedItem = showUnlockedItem;
        this.shouldRun = shouldRun;
        if (showUnlockedItem) {
            this.cowSpriteSheet = getSpriteSheetWithUnlockedItem(itemTypeUnlocked, itemIndex);
        } else {
            this.cowSpriteSheet = getCustomizedSpriteSheet(itemTypeUnlocked, itemIndex);
        }
        this.rightWalk = getFramesFromRegion();
        this.currentFrame = (TextureRegion) rightWalk.getKeyFrame(stateTime, true);
    }

    public AICow(float x, float y, float width, float height, boolean shouldRun) {
        this(x, y, null, (byte) 0, false, shouldRun);
        this.width = width;
        this.height = height;
        this.currentFrame.flip(false, false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(color == null ? Color.WHITE : color);
        super.draw(batch, parentAlpha);
        batch.draw(currentFrame, x - (width / 2), y - (height / 2), width > 32 ? width : 32, height > 32 ? height : 32);
        batch.setColor(Color.WHITE);
    }

    public void draw(Batch batch, float width, float height) {
        super.draw(batch, 1f);
        batch.draw(currentFrame, x - (width / 2), y - (height / 2), width, height);
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public void setCowColor(Color color) {
        this.color = color;
    }

    public void setShouldMove(boolean shouldMove) {
        this.shouldRun = shouldMove;
        this.shouldMove = shouldMove;
    }

    @Override
    public float getPrefWidth() {
        return 32f;
    }

    @Override
    public float getPrefHeight() {
        return 32f;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (shouldRun) {
            if (shouldMove) {
                x += speed * delta;
            }
            stateTime += delta;
            currentFrame = (TextureRegion) rightWalk.getKeyFrame(stateTime, true);
        } else {
            currentFrame = (TextureRegion) rightWalk.getKeyFrame(0, false);
        }
    }

    public void changeClothing(String itemTypeUnlocked, byte itemNr) {
        this.cowSpriteSheet = getCustomizedSpriteSheet(itemTypeUnlocked, itemNr);
        this.rightWalk = getFramesFromRegion();
        this.currentFrame = (TextureRegion) rightWalk.getKeyFrame(0);
        this.currentFrame.flip(false, false);
    }

    public TextureRegion getWardrobe(Mask mask, Body body, Back back, Head head, Leg leg) {
        Pixmap nakedPixmap = new Pixmap(Gdx.files.internal("sprites/objects/cow.png"));
        Pixmap legPixmap = null;
        Pixmap bodyPixmap = null;
        Pixmap maskPixmap = null;
        Pixmap backPixmap = null;
        Pixmap headPixmap = null;

        if (leg != null) {
            legPixmap = leg.getPixmap();
        }
        if (body != null) {
            bodyPixmap = body.getPixmap();
        }
        if (back != null) {
            backPixmap = back.getPixmap();
        }
        if (mask != null) {
            maskPixmap = mask.getPixmap();
        }
        if (head != null) {
            headPixmap = head.getPixmap();
        }

        if (!showUnlockedItem) {
            CowPreferences cowPrefs = CowPreferences.getInstance();
            cowPrefs.saveClothing(ItemUtils.MASK, mask.getId());
            cowPrefs.saveClothing(ItemUtils.BODY, body.getId());
            cowPrefs.saveClothing(ItemUtils.BACK, back.getId());
            cowPrefs.saveClothing(ItemUtils.HEAD, head.getId());
            cowPrefs.saveClothing(ItemUtils.LEG, leg.getId());
        }

        if (legPixmap != null) {
            nakedPixmap.drawPixmap(legPixmap, 0, 0);
        }
        if (bodyPixmap != null) {
            nakedPixmap.drawPixmap(bodyPixmap, 0, 0);
        }
        if (backPixmap != null) {
            nakedPixmap.drawPixmap(backPixmap, 0, 0);
        }
        if (headPixmap != null) {
            nakedPixmap.drawPixmap(headPixmap, 0, 0);
        }
        if (maskPixmap != null) {
            nakedPixmap.drawPixmap(maskPixmap, 0, 0);
        }

        return new TextureRegion(new Texture(nakedPixmap));
    }

    private TextureRegion getCustomizedSpriteSheet(String costumeType, byte itemIndex) {
        CowPreferences cowPrefs = CowPreferences.getInstance();

        Head head = (Head) ItemUtils.getItem(ItemUtils.HEAD, cowPrefs.getSavedClothing(ItemUtils.HEAD));
        Body body = (Body) ItemUtils.getItem(ItemUtils.BODY, cowPrefs.getSavedClothing(ItemUtils.BODY));
        Leg leg = (Leg) ItemUtils.getItem(ItemUtils.LEG, cowPrefs.getSavedClothing(ItemUtils.LEG));
        Back back = (Back) ItemUtils.getItem(ItemUtils.BACK, cowPrefs.getSavedClothing(ItemUtils.BACK));
        Mask mask = (Mask) ItemUtils.getItem(ItemUtils.MASK, cowPrefs.getSavedClothing(ItemUtils.MASK));

        if (costumeType != null) {
            if (costumeType.equalsIgnoreCase(ItemUtils.MASK)) {
                mask = (Mask) ItemUtils.getItem(costumeType, itemIndex);
            } else if (costumeType.equalsIgnoreCase(ItemUtils.HEAD)) {
                head = (Head) ItemUtils.getItem(costumeType, itemIndex);
            } else if (costumeType.equalsIgnoreCase(ItemUtils.BODY)) {
                body = (Body) ItemUtils.getItem(costumeType, itemIndex);
            } else if (costumeType.equalsIgnoreCase(ItemUtils.LEG)) {
                leg = (Leg) ItemUtils.getItem(costumeType, itemIndex);
            } else if (costumeType.equalsIgnoreCase(ItemUtils.BACK)) {
                back = (Back) ItemUtils.getItem(costumeType, itemIndex);
            }
        }
        return getWardrobe(mask, body, back, head, leg);
    }

    private TextureRegion getSpriteSheetWithUnlockedItem(String costumeTypeUnlocked, byte itemIndex) {
        Head head = null;
        Body body = null;
        Leg leg = null;
        Back back = null;
        Mask mask = null;

        if (costumeTypeUnlocked != null) {
            if (costumeTypeUnlocked.equalsIgnoreCase(ItemUtils.HEAD)) {
                head = (Head) ItemUtils.getItem(costumeTypeUnlocked, itemIndex);
            } else if (costumeTypeUnlocked.equalsIgnoreCase(ItemUtils.BODY)) {
                body = (Body) ItemUtils.getItem(costumeTypeUnlocked, itemIndex);
            } else if (costumeTypeUnlocked.equalsIgnoreCase(ItemUtils.LEG)) {
                leg = (Leg) ItemUtils.getItem(costumeTypeUnlocked, itemIndex);
            } else if (costumeTypeUnlocked.equalsIgnoreCase(ItemUtils.BACK)) {
                back = (Back) ItemUtils.getItem(costumeTypeUnlocked, itemIndex);
            } else if (costumeTypeUnlocked.equalsIgnoreCase(ItemUtils.MASK)) {
                mask = (Mask) ItemUtils.getItem(costumeTypeUnlocked, itemIndex);
            }
        }
        return getWardrobe(mask, body, back, head, leg);
    }

    private Animation getFramesFromRegion() {
        final int FRAMES = 4;
        TextureRegion[] region = new TextureRegion[FRAMES];
        for (int i = 0; i < region.length; i++) {
            region[i] = new TextureRegion(this.cowSpriteSheet, i * 32, 0, 32, 32);
        }
        return new Animation(ANIM_SPEED_NORMAL, region);
    }


}

package com.bitwave.cowdash.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.ParticleHelper;
import com.bitwave.cowdash.utils.TextureUtils;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public class Chest extends GameObject implements Disposable {

    private final byte CLOSED = 0;
    private final byte OPEN = 1;
    private final byte itemNr;

    private final Sprite chestSprite;
    private final String typeOfItem;

    public Chest(Vector2 position, Level level, byte itemNr, String typeOfItem) {
        super(position, level, 32, 32, ObjectType.OBJECT_CHEST);
        this.itemNr = itemNr;
        this.typeOfItem = typeOfItem;
        this.chestSprite = new Sprite(getChestImages("sprites/objects/chest_sprite_sheet.png")[(hasAlreadyBeenOpened(typeOfItem, this.itemNr) ? OPEN : CLOSED)]);
        this.chestSprite.setPosition(position.x, position.y);

    }

    public String getType() {
        return typeOfItem;
    }

    public byte getNr() {
        return this.itemNr;
    }

    @Override
    public void render(SpriteBatch batch) {
        chestSprite.draw(batch);
    }

    @Override
    public void tick(float deltaTime, Player player) {
        if (player.getBounds().overlaps(bounds) && !hasAlreadyBeenOpened(typeOfItem, itemNr) && !isCollidedWith) {
            if (!CowPreferences.getInstance().isVibrationDisabled()) {
                Gdx.input.vibrate(120);
            }
            level.rumble(12.0f, .6f);
            AudioUtils.getInstance().playSoundFX("medal");

            ParticleHelper.getInstance().addChestEffect(position.x + (chestSprite.getWidth() / 2), position.y + (chestSprite.getHeight() / 2), true);
            chestSprite.setRegion(getChestImages("sprites/objects/chest_sprite_sheet.png")[OPEN]);
            isCollidedWith = true;
        }
    }

    private boolean hasAlreadyBeenOpened(String type, short nr) {
        return CowPreferences.getInstance().isItemUnlocked(type, itemNr);
    }

    private TextureRegion[] getChestImages(String texturePath) {
        Texture tex = (Texture) Assets.getInstance().get(texturePath);
        TextureRegion[] region = TextureUtils.singleSplit(tex, 32, 32, false);
        return region;
    }

    @Override
    public void dispose() {

    }


}

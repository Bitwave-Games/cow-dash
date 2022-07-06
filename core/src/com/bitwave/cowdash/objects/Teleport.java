package com.bitwave.cowdash.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.bitwave.cowdash.level.Level;
import com.bitwave.cowdash.utils.Assets;
import com.bitwave.cowdash.utils.AudioUtils;
import com.bitwave.cowdash.utils.ParticleHelper;

public class Teleport extends GameObject {

    private final Sprite teleportSprite;
    private final Vector2 teleportationPosition;

    public Teleport(Vector2 position, Level level, Vector2 teleportationPosition) {
        super(position, level, 32, 32, 10, 12, 10, 0, ObjectType.OBJECT_TELEPORT);
        this.position = position;
        this.teleportationPosition = teleportationPosition;
        this.teleportSprite = new Sprite((Texture) Assets.getInstance().get("sprites/objects/teleporter.png"));
        this.teleportSprite.setPosition(position.x, position.y);
        this.bounds.set(position.x + 8, position.y, 16, 10);

        ParticleHelper.getInstance().addTeleportEffect(bounds.x + teleportSprite.getWidth() / 5f, bounds.y + 8 + teleportSprite.getHeight() / 2f, true);
    }

    @Override
    public void render(SpriteBatch batch) {
        teleportSprite.draw(batch);
    }

    @Override
    public void tick(float deltaTime, Player player) {
        if (player.getBounds().overlaps(bounds) && !player.isTeleported()) {
            AudioUtils.getInstance().playSoundFX("teleport");
            player.setInvincible(true);
            player.setTeleportLandingPosition(teleportationPosition);
            player.setPosition(teleportationPosition.x, teleportationPosition.y);
            player.setTeleported(true);
        }
    }
}
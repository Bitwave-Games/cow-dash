package com.bitwave.cowdash.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.bitwave.cowdash.utils.persistance.CowPreferences;

public class ParticleHelper implements Disposable {

    private static ParticleHelper instance;
    private final float speed = 1f;
    private final Array<PooledEffect> ingameEffects;
    private final Array<PooledEffect> menuEffects;
    private final ParticleEffectPool keyBlueEffectPool;
    private final ParticleEffectPool keyRedEffectPool;
    private final ParticleEffectPool keyYellowEffectPool;
    private final ParticleEffectPool chestEffectPool;
    private final ParticleEffectPool enemyEffectPool;
    private final ParticleEffectPool doorRedEffectPool;
    private final ParticleEffectPool doorBlueEffectPool;
    private final ParticleEffectPool doorYellowEffectPool;
    private final ParticleEffectPool veggieEffectPool;
    private final ParticleEffectPool landParticlesEffectPool;
    private final ParticleEffectPool portalEffectPool;
    private final ParticleEffectPool teleportEffectPool;
    private ParticleEffectPool woodenEffectPool;
    private final ParticleEffectPool changeClothesEffectPool;

    private ParticleHelper() {
        ingameEffects = new Array<ParticleEffectPool.PooledEffect>();
        menuEffects = new Array<ParticleEffectPool.PooledEffect>();
        ParticleEffect effect;

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/wood_effect_bigger_darker_additive"), Gdx.files.internal("effects"));
        woodenEffectPool = new ParticleEffectPool(effect, 2, 4);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/change_clothes_icon_press_even_bigger"), Gdx.files.internal("effects"));
        changeClothesEffectPool = new ParticleEffectPool(effect, 2, 4);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/chest_particles"), Gdx.files.internal("effects"));
        chestEffectPool = new ParticleEffectPool(effect, 1, 2);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/block_door_blue"), Gdx.files.internal("effects"));
        doorBlueEffectPool = new ParticleEffectPool(effect, 1, 2);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/block_door_red"), Gdx.files.internal("effects"));
        doorRedEffectPool = new ParticleEffectPool(effect, 1, 2);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/block_door_yellow"), Gdx.files.internal("effects"));
        doorYellowEffectPool = new ParticleEffectPool(effect, 1, 2);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/new_blobs1"), Gdx.files.internal("effects"));
        enemyEffectPool = new ParticleEffectPool(effect, 1, 4);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/key_blue"), Gdx.files.internal("effects"));
        keyBlueEffectPool = new ParticleEffectPool(effect, 1, 2);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/key_red"), Gdx.files.internal("effects"));
        keyRedEffectPool = new ParticleEffectPool(effect, 1, 2);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/key_yellow"), Gdx.files.internal("effects"));
        keyYellowEffectPool = new ParticleEffectPool(effect, 1, 2);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/veggie_particles"), Gdx.files.internal("effects"));
        veggieEffectPool = new ParticleEffectPool(effect, 2, 5);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/land_particles"), Gdx.files.internal("effects"));
        landParticlesEffectPool = new ParticleEffectPool(effect, 1, 3);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/portal particles_no_glow"), Gdx.files.internal("effects"));
        portalEffectPool = new ParticleEffectPool(effect, 1, 3);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/teleport_particles_no_glow"), Gdx.files.internal("effects"));
        teleportEffectPool = new ParticleEffectPool(effect, 1, 2);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/wood_effect_bigger_darker_additive"), Gdx.files.internal("effects"));
        woodenEffectPool = new ParticleEffectPool(effect, 1, 3);
    }

    public static ParticleHelper getInstance() {
        if (instance == null) {
            instance = new ParticleHelper();
        }
        return instance;
    }

    public void render(SpriteBatch batch, boolean menu) {
        if (menu) {
            for (PooledEffect pooledEffect : menuEffects) {
                pooledEffect.draw(batch);
            }
        } else {
            for (PooledEffect pooledEffect : ingameEffects) {
                pooledEffect.draw(batch);
            }
        }
    }

    public void tick(float deltaTime, boolean menu) {
        if (menu) {
            for (PooledEffect pooledEffect : menuEffects) {
                if (pooledEffect.isComplete()) {
                    pooledEffect.free();
                    menuEffects.removeValue(pooledEffect, true);
                } else {
                    pooledEffect.update(deltaTime * speed);
                }
            }
        } else {
            for (PooledEffect pooledEffect : ingameEffects) {
                if (pooledEffect.isComplete()) {
                    pooledEffect.free();
                    ingameEffects.removeValue(pooledEffect, true);
                } else {
                    pooledEffect.update(deltaTime * speed);
                }
            }
        }
    }

    @Override
    public void dispose() {
        if (instance != null) {
            for (PooledEffect effect : ingameEffects) {
                effect.dispose();
            }
            for (PooledEffect effect : menuEffects) {
                effect.dispose();
            }
            keyBlueEffectPool.clear();
            keyRedEffectPool.clear();
            keyYellowEffectPool.clear();
            chestEffectPool.clear();
            enemyEffectPool.clear();
            doorRedEffectPool.clear();
            doorBlueEffectPool.clear();
            doorYellowEffectPool.clear();
            veggieEffectPool.clear();
            landParticlesEffectPool.clear();
            portalEffectPool.clear();
            teleportEffectPool.clear();
            woodenEffectPool.clear();
            changeClothesEffectPool.clear();

            instance = null;
        }
    }

    public void addVeggieEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = veggieEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addKeyBlueEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = keyBlueEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addKeyRedEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = keyRedEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addKeyYellowEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = keyYellowEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addChestEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = chestEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addMedalEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = chestEffectPool.obtain();
            effect.setPosition(x, y);
            menuEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addEnemyEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = enemyEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addDoorBlueEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = doorBlueEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addDoorRedEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = doorRedEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addDoorYellowEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = doorYellowEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addLandParticleEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = landParticlesEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addPortalEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = portalEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addTeleportEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = teleportEffectPool.obtain();
            effect.setPosition(x, y);
            ingameEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addWoodenEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = woodenEffectPool.obtain();
            effect.setPosition(x, y);
            menuEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    public void addChangeClothesEffect(float x, float y, boolean startRightAway) {
        if (enabled()) {
            PooledEffect effect = changeClothesEffectPool.obtain();
            effect.setPosition(x, y);
            menuEffects.add(effect);
            if (startRightAway) {
                effect.start();
            }
        }
    }

    private boolean enabled() {
        return CowPreferences.getInstance().isParticlesEnabled();
    }

}

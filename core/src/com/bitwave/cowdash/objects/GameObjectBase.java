package com.bitwave.cowdash.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

interface GameObjectBase {

    void render(SpriteBatch batch);

    void tick(float deltaTime, Player player);

}
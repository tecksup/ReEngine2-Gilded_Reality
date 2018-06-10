package com.thecubecast.ReEngine.GameStates.Levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Scene {

    void draw(LevelsFSM entity, SpriteBatch g, int height, int width, float Time);

    void HandleInput(LevelsFSM entity);

    void reSize();
}

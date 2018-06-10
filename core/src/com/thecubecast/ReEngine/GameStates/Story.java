// GameState that shows Main Menu.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.GameStates.Levels.LevelsFSM;

import java.awt.*;

public class Story extends GameState {

    boolean StoryDebug;

    LevelsFSM Level;

    public Story(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        Level = new LevelsFSM(gsm);

    }

    public void update() {

        Level.Update();
        handleInput();

    }

    public void draw(SpriteBatch bbg, int height, int width, float Time) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Level.Draw(bbg, height, width, Time);

    }

    public void handleInput() {
        Level.HandleInput();
    }

    public void reSize(SpriteBatch g, int H, int W) {
        Level.reSize();
    }

    @Override
    public void Shutdown() {}

    @Override
    public void dispose() {}

}
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

    OrthographicCamera cameraGui;
    LevelsFSM Level;

    public Story(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        cameraGui = new OrthographicCamera();

        Level = new LevelsFSM(cameraGui, gsm);

    }

    public void update() {

        Level.Update();

        handleInput();

        cameraGui.update();
    }

    public void draw(SpriteBatch bbg, int height, int width, float Time) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraGui.setToOrtho(false, width, height);
        bbg.setProjectionMatrix(cameraGui.combined);
        bbg.begin();

        Level.Draw(bbg, height, width, Time);

        bbg.end();
    }

    public void handleInput() {
        Level.HandleInput();
    }

    public void reSize(SpriteBatch g, int H, int W) {
        //stage.getViewport().update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), true);
        cameraGui.setToOrtho(false);
        Level.reSize(cameraGui);
        //Menus.reSize(H, W, cameraGui);
    }

    @Override
    public void Shutdown() {}

    @Override
    public void dispose() {}

}
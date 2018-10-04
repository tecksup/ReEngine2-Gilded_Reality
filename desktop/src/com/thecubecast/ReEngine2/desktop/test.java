package com.thecubecast.ReEngine2.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class test extends ApplicationAdapter
{
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1024, 768);
        new Lwjgl3Application(new test(), config);
    }

    private Stage stage;
    private FrameBuffer fbo;

    private SpriteBatch batch;

    /** > 1 for blur/pixelized, < 1 for antialias (over sampling) */
    private float decimate = 1f;

    @Override
    public void create() {

        batch = new SpriteBatch();

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        Skin skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));

        Table root = new Table(skin);

        SelectBox<String> sb = new SelectBox<String>(skin);
        sb.setItems("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t");

        root.add(sb);

        stage.addActor(root);
        root.setFillParent(true);

        Gdx.input.setInputProcessor(stage);

        fbo = new FrameBuffer(Format.RGBA8888, (int)(Gdx.graphics.getWidth()/decimate), (int)(Gdx.graphics.getHeight()/decimate), false);

        // optional : blur or pixelize
        fbo.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        boolean useFBO = true;

        if(useFBO){

            fbo.begin();

            stage.getViewport().update(fbo.getWidth(), fbo.getHeight(), true);

            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            stage.act();
            stage.draw();

            stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

            fbo.end();

            batch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
            batch.begin();
            batch.draw(fbo.getColorBufferTexture(), 0, 0, 1, 1, 0, 0, 1, 1);
            batch.end();
        }
        else{
            stage.act();
            stage.draw();
        }

    }
}
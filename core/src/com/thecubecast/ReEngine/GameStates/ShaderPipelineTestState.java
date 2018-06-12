// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.OGMO.OelGridLayer;
import com.thecubecast.ReEngine.Data.OGMO.OelMap;
import com.thecubecast.ReEngine.Data.OGMO.OelMapRenderer;
import com.thecubecast.ReEngine.Data.collision;
import com.thecubecast.ReEngine.Data.controlerManager;
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;
import com.thecubecast.ReEngine.Graphics.PipelineTexture;
import com.thecubecast.ReEngine.Graphics.RePipeline;
import com.thecubecast.ReEngine.worldObjects.NPC;
import com.thecubecast.ReEngine.worldObjects.Player;
import com.thecubecast.ReEngine.worldObjects.WorldObject;
import com.thecubecast.ReEngine.worldObjects.WorldObjectComp;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.mainclass.MasterFBO;

public class ShaderPipelineTestState extends GameState {

    float playerx,playery,playerz;

    private List<WorldObject> Entities = new ArrayList<>();

    OrthographicCamera camera;

    OelMap testMap = new OelMap("Saves/OGMO/test.oel");
    OelMapRenderer testRenderer = new OelMapRenderer("Saves/OGMO/test.oep");

    public ShaderPipelineTestState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        //SETUP CAMERA SPRITEBATCH AND MENU
        camera = new OrthographicCamera();
        camera.position.set(0, 0, camera.position.z);

        playerx = 0;
        playery = 50;
        playerz = 0;
        camera.position.set(playerx,playery,playerz);

    }

    public void update() {
        handleInput();
        camera.update();

    }

    public void draw(SpriteBatch g, int height, int width, float Time) {
        Gdx.gl.glClearColor(0/255f, 0/255f, 45/255f, 1);
        RenderCam();

        camera.setToOrtho(false, width, height);
        camera.position.set(camera.viewportWidth/2 + playerx, camera.viewportHeight/2 + playery,playerz);
        camera.update();
        g.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0/255f, 0/255f, 45/255f, 1);
        g.begin();

        testRenderer.setView(camera);
        testRenderer.renderLayer(g, testMap, 1);

        for(int i = 0; i < Entities.size(); i++) {
            WorldObjectComp temp = new WorldObjectComp();
            Entities.sort(temp);
            Entities.get(i).draw(g, Time);
        }

        g.end();


    }


    private void handleInput() {

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f || Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerx += 10;
        } else if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) < -0.2f || Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerx -= 10;
        }

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyPressed(Input.Keys.S)) {
            playery -= 10;
        } else if (gsm.ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyPressed(Input.Keys.W)) {
            playery += 10;
        }

    }

    public void RenderCam() {
        camera.update();
    }


    public void reSize(SpriteBatch g, int H, int W) {
        float posX = camera.position.x;
        float posY = camera.position.y;
        float posZ = camera.position.z;
        camera.setToOrtho(false);
        camera.position.set(posX, posY, posZ);

        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, W, H);
    }

    @Override
    public void Shutdown() {

    }

}
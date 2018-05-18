// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
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

    int debugVal = 0;

    BitwiseTiles tiledBits;
    RePipeline Repipe;

    PipelineTexture PipelineTexturetest;
    PipelineTexture PipelineTexturetest2;

    private List<WorldObject> Entities = new ArrayList<>();

    OrthographicCamera camera;


    public ShaderPipelineTestState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        TiledMap tiledMap = new TmxMapLoader().load("Saves/BITWISE/EmptyRoom/map.tmx");
        tiledBits = new BitwiseTiles(tiledMap);

        Repipe = new RePipeline();

        //SETUP CAMERA SPRITEBATCH AND MENU
        camera = new OrthographicCamera();
        camera.position.set(0, 0, camera.position.z);

        PipelineTexturetest = new PipelineTexture("rock.png", "rock_n.png");
        PipelineTexturetest2 = new PipelineTexture("Sprites/proto/proto_0.png");

        WorldObject Desk = new WorldObject((int) 4*16, (int) 6*16, new Vector3(16, 16, 0)) {
            PipelineTexture DeskSprite = new PipelineTexture("Sprites/Tiles/Desk.png", "Sprites/Tiles/Desk_n.png");
            PipelineTexture ChairSprite = new PipelineTexture("Sprites/Tiles/Chair.png", "Sprites/Tiles/Chair_n.png");

            @Override
            public void init(int Width, int Height) {

            }

            @Override
            public void update(float delta, List<collision> Colls) {

            }

            @Override
            public void draw(SpriteBatch batch, float Time) {
            }

            @Override
            public void draw(RePipeline batch, float Time) {
                batch.draw(ChairSprite, getPosition().x + 6, getPosition().y - 15);
                batch.draw(DeskSprite, getPosition().x, getPosition().y - 16);
            }
        };

        Entities.add(Desk);

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
        Repipe.begin(g);

        //tiledBits.drawLayer(Repipe, 16, Time,0, 0, null);

        Repipe.draw(tiledBits.getBitTiles().get(1)[1], 3*16, 5*16);

        for(int i = 0; i < Entities.size(); i++) {
            WorldObjectComp temp = new WorldObjectComp();
            Entities.sort(temp);
            Entities.get(i).draw(Repipe, Time);
        }

        Texture temp = Repipe.end();
        Gdx.gl.glClearColor(0/255f, 0/255f, 45/255f, 1);
        MasterFBO.bind();
        MasterFBO.begin();
        g.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //g.draw(temp,camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2 + height, width, -height);
        if(debugVal == 0)
            g.draw(Repipe.getFboT(),camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2 + height, width, -height);
        else if(debugVal == 1)
            g.draw(Repipe.getFboNormalsT(),camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2 + height, width, -height);
        else if(debugVal == 2)
            g.draw(Repipe.getFboLightingT(),camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2 + height, width, -height);

        //To draw above the gamelayer go beneath this line

        gsm.Render.GUIDrawText(g, (int) (camera.position.x - camera.viewportWidth/2), (int) ((camera.position.y - camera.viewportHeight/2) + height), "debugVal: " + debugVal);


        g.end();


    }


    private void handleInput() {

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f || Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerx += 1;
        } else if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) < -0.2f || Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerx -= 1;
        }

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyPressed(Input.Keys.S)) {
            playery -= 1;
        } else if (gsm.ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyPressed(Input.Keys.W)) {
            playery += 1;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            debugVal -= 1;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            debugVal += 1;
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
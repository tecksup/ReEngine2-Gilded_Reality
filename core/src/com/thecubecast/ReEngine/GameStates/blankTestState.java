// GameState that shows logo.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.oldPlayer;
import com.thecubecast.ReEngine.Data.controlerManager;
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;
import com.thecubecast.ReEngine.Graphics.Scene2D.TkTextButton;
import com.thecubecast.ReEngine.worldObjects.NPC;
import com.thecubecast.ReEngine.worldObjects.WorldObject;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;

import java.util.ArrayList;
import java.util.List;

public class blankTestState extends GameState {

    oldPlayer player;
    private List<Rectangle> Collisions = new ArrayList<>();
    private List<WorldObject> Entities = new ArrayList<>();

    OrthographicCamera camera;

    ScreenShakeCameraController shaker;

    OrthographicCamera Guicamera;
    SpriteBatch guiBatch;

    private Skin skin;
    private Stage stage;
    private Table table;

    TiledMap tiledMap;
    BitwiseTiles tiledBits;

    public blankTestState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {
        MenuInit();

        player = new oldPlayer(16, gsm);
        gsm.DiscordManager.setPresenceDetails("topdown Demo - Level 1");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;;

        //SETUP TILEDMAP
        tiledMap = new TmxMapLoader().load("Saves/BITWISE/map.tmx");
        tiledBits = new BitwiseTiles(tiledMap);

        for (int y = 0; y < tiledBits.getBitTileObject(2).realTile.size(); y++) {
            for(int x = 0; x < tiledBits.getBitTileObject(2).realTile.get(y).length; x++) {
                if (tiledBits.getBitTileObject(2).realTile.get(y)[x] == 1) {

                } else if ((tiledBits.getBitTileObject(2).realTile.get(y)[x] == 5)) { //fence
                    Collisions.add(new Rectangle(x+.25f, y, .4f, 0.25f));
                } else if ((tiledBits.getBitTileObject(2).realTile.get(y)[x] == 4)) { //Rock
                    Collisions.add(new Rectangle(x, y, 1, 0.5f));
                }

                else if ((tiledBits.getBitTileObject(2).realTile.get(y)[x] == 3)) { //Bush (stupid grass block)
                    Collisions.add(new Rectangle(x, y, 1, 1));
                }
            }
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, gsm.Width, gsm.Height);

        shaker = new ScreenShakeCameraController(camera);

        guiBatch = new SpriteBatch();
        Guicamera = new OrthographicCamera();
        Guicamera.setToOrtho(false, gsm.Width, gsm.Height);

        //JukeBox.load("/Music/bgmusic.wav", "LogoSound");
        //JukeBox.play("LogoSound");
    }

    public void update() {

        handleInput();
        player.update(gsm.DeltaTime, Collisions);

        FollowCam(camera, player.Coords.x, player.Coords.y, 1f);
        camera.update();
    }

    public void draw(SpriteBatch g, int height, int width, float Time) {
        shaker.update(gsm.DeltaTime);

        //camera.setToOrtho(false, width, height);
        g.setProjectionMatrix(shaker.getCombinedMatrix());
        g.begin();
        Gdx.gl.glClearColor(255f, 255f, 255f, 1);


        tiledBits.drawLayer(g, 16, Time,0, 0, false);
        tiledBits.drawLayer(g, 16, Time,1, 0, false);
        player.draw(g, Time);
        for(int i = 0; i < Entities.size(); i++) {
            Entities.get(i).draw(g, Time);
        }
        tiledBits.drawLayer(g, 16, Time,2, 0, false);

        g.end();

        guiBatch.setProjectionMatrix(Guicamera.combined);
        guiBatch.begin();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.getRoot().draw(guiBatch, 1);
        guiBatch.end();

        if (gsm.Debug) {
            gsm.Render.debugRenderer.setProjectionMatrix(shaker.getCombinedMatrix());
            gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            gsm.Render.debugRenderer.setColor(Color.RED);
            for(int i = 0; i < Entities.size(); i++) {
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().x, Entities.get(i).getHitbox().y, Entities.get(i).getHitbox().width, Entities.get(i).getHitbox().height);
            }
            gsm.Render.debugRenderer.rect(player.getHitbox().x*16, player.getHitbox().y*16, player.getHitbox().width*16, player.getHitbox().height*16);
            gsm.Render.debugRenderer.setColor(Color.RED);
            Collisions.forEach( number -> gsm.Render.debugRenderer.rect(number.x*16, number.y*16, (number.width)*16, (number.height)*16));

            gsm.Render.debugRenderer.end();

        }
    }

    public void handleInput() {

        oldPlayer.Direction[] temp = new oldPlayer.Direction[4];
        boolean moving = false;

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f || Gdx.input.isKeyPressed(Keys.D)) {
            temp[3] = oldPlayer.Direction.East;
            moving = true;
            player.MovePlayerVelocity(oldPlayer.Direction.East,5, gsm.DeltaTime);
        } else if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) < -0.2f || Gdx.input.isKeyPressed(Keys.A)) {
            temp[2] = oldPlayer.Direction.West;
            moving = true;
            player.MovePlayerVelocity(oldPlayer.Direction.West,5, gsm.DeltaTime);
        }

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyPressed(Keys.S)) {
            temp[1] = oldPlayer.Direction.South;
            moving = true;
            player.MovePlayerVelocity(oldPlayer.Direction.South,5, gsm.DeltaTime);
        } else if (gsm.ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyPressed(Keys.W)) {
            temp[0] = oldPlayer.Direction.North;
            moving = true;
            player.MovePlayerVelocity(oldPlayer.Direction.North,5, gsm.DeltaTime);
        }

        if (gsm.ctm.isButtonJustDown(1, controlerManager.buttons.BUTTON_START)){
            Common.print("Player 2 joined the game!!");
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_START) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
            Common.print("Escape!!");
            //gsm.ctm.newController("template");
        }

        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
            gsm.setState(GameStateManager.State.MENU);
            //gsm.ctm.newController("template");
        }

        //We send the player the correct cardinal direction
        oldPlayer.Direction finalDirect = player.playerDirection;;

        //Do the calculations
        if (temp[0] != null && temp[1] != null) {
            temp[0] = null;
            temp[1] = null;
        }
        if (temp[2] != null && temp[3] != null) {
            temp[2] = null;
            temp[3] = null;
        }

        if (temp[0] != null) { //NORTH
            if (temp[2] != null) { // WEST
                finalDirect = oldPlayer.Direction.NorthWest;
            }
            if (temp[3] != null) { //EAST
                finalDirect = oldPlayer.Direction.NorthEast;
            }
        } else if (temp[1] != null) { //SOUTH
            if (temp[2] != null) { // WEST
                finalDirect = oldPlayer.Direction.SouthWest;
            }
            if (temp[3] != null) { //EAST
                finalDirect = oldPlayer.Direction.SouthEast;
            }
        } else {
            finalDirect = player.playerDirection;
        }

        if (moving) {
            player.MovePlayerVelocity(finalDirect,5, gsm.DeltaTime);
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyPressed(Keys.K)){
            shaker.addDamage(.4f);
        }

    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
    }

    public void MenuInit() {

        setupSkin();

        stage = new Stage(new StretchViewport(gsm.Width, gsm.Height));
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.top().left();
        stage.addActor(table);

        final Label Label = new Label("Test Text",skin);
        Label.setPosition(100, 20);
        stage.addActor(Label);

        final TkTextButton button1 = new TkTextButton("test this button like its the end of the world",skin);
        table.add(button1);
        table.row();

    }

    public void reSize(SpriteBatch g, int h, int w) {
        float posX = camera.position.x;
        float posY = camera.position.y;
        float posZ = camera.position.z;
        camera.setToOrtho(false, w, h);
        camera.position.set(posX, posY, posZ);

        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, w, h);
        guiBatch.setProjectionMatrix(matrix);
        shaker.reSize(camera);
    }

    public void FollowCam(OrthographicCamera cam, float playerx, float playery, float lerp) {
        int mapBoundX = 10000;
        int mapBoundY = 10000;

        float tempx = cam.position.x + (playerx*16 - cam.position.x) * lerp * Gdx.graphics.getDeltaTime();
        float tempy = cam.position.y + (playery*16 - cam.position.y) * lerp * Gdx.graphics.getDeltaTime();

        Rectangle cameraBounds = new Rectangle(cam.position.x - cam.viewportWidth/2 ,cam.position.y - cam.viewportHeight/2, cam.viewportWidth, cam.viewportHeight);
/*
        if (tempx >= 0) {
            if(tempx + cameraBounds.getWidth() <= tiledBits.getBitTileObject(0).width*16) {
                position.x += (playerx*16 - position.x) * lerp * deltaTime;
            }
        }
        if (tempy >= 0) {
            if (tempy + cameraBounds.getHeight() <= tiledBits.getBitTileObject(0).height*16) {
                position.y += (playery*16 - position.y) * lerp * deltaTime;
            }
        }
*/
        //    float PosibleX = position.x + (playerx - position.x) * lerp * deltaTime;
        //    if (PosibleX - (gsm.Width/2) >= 0 && PosibleX - (gsm.Width/2) <= mapBoundX) {
        //        position.x += (playerx - position.x) * lerp * deltaTime;
        //    }

        //    float PosibleY = position.y + (playery - position.y) * lerp * deltaTime;
        //    if (PosibleY - (gsm.Height/2) >= 0 && PosibleY - (gsm.Height/2) <= mapBoundY) {
        //        position.y += (playery - position.y) * lerp * deltaTime;
        //    } else if (PosibleY - (gsm.Height/2) >= mapBoundY) {
        //        position.y += (playery+160 - position.y) * lerp * deltaTime;
        //    }

        cam.position.x += (playerx*16 - cam.position.x) * lerp * Gdx.graphics.getDeltaTime();
        cam.position.y += (playery*16 - cam.position.y) * lerp * Gdx.graphics.getDeltaTime();

        cam.position.x = (int) cam.position.x;
        cam.position.y = (int) cam.position.y;

        //cam.position.set(position.x, position.y, cam.position.z);
        cam.update();
    }

}
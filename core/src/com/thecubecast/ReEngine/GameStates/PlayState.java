// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.Data.Common.updategsmValues;

public class PlayState extends GameState {

    Player player;

    private Skin skin;
    private Stage stage;
    private Table table;

    private Vector3 position;
    private long last_time;
    private int deltaTime;

    private List<Rectangle> Collisions = new ArrayList<>();

    TiledMap tiledMap;
    BitwiseTiles tiledBits;
    TiledMapRenderer tiledMapRenderer;

    OrthographicCamera camera;
    Rectangle cameraBounds;
    ScreenShakeCameraController shaker;

    SpriteBatch guiBatch;

    ParticleEffect pe;
    Body body;

    private List<Achievement> Achievements = new ArrayList<>();

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    public void AddAchievement(String text, int IconID, float Time, float Durration, boolean Anim) {
        Achievement temp = new Achievement(text, IconID, Time,  Durration, Anim);
        Achievements.add(Achievements.size(), temp);
        Common.print("Added Achievement: " + text);
    }

    public void init() {

        player = new Player(64, gsm);
        gsm.DiscordManager.setPresenceDetails("topdown Demo - Level 1");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;;

        //SETUP TILEDMAP
        tiledMap = new TmxMapLoader().load("Saves/BITWISE/map.tmx");
        tiledBits = new BitwiseTiles(tiledMap);

        for (int y = 0; y < tiledBits.getBitTileObject(1).realTile.size(); y++) {
            for(int x = 0; x < tiledBits.getBitTileObject(0).realTile.get(y).length; x++) {
                if (tiledBits.getBitTileObject(1).realTile.get(y)[x] == 1) {

                } else if ((tiledBits.getBitTileObject(1).realTile.get(y)[x] == 4)) { //Rock
                    Collisions.add(new Rectangle(x, y, 1, 0.5f));
                }

                else if ((tiledBits.getBitTileObject(1).realTile.get(y)[x] == 3)) { //Bush (stupid grass block)
                    Collisions.add(new Rectangle(x, y, 1, 1));
                }
            }
        }

        //Collisions.add(new Rectangle(-10, 0, 20, 1));
        //Collisions.add(new Rectangle(3, 1, 2, 1));
        //Collisions.add(new Rectangle(6, 2, 2, 1));

        //gsm.Audio.playMusic("Rain", true);


        //SETUP CAMERA SPRITEBATCH AND MENU
        guiBatch = new SpriteBatch();

        //ShaderInit(guiBatch);

        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        camera.position.set((player.Coords.x*65), (player.Coords.y*65), camera.position.z);
        position = camera.position;

        shaker = new ScreenShakeCameraController(camera);

        MenuInit();

        //SETUP SCENE2D INPUT
        Gdx.input.setInputProcessor(stage);

        //SETUP THE PARTICLES
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("particles/fire.p"),Gdx.files.internal(""));
        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        pe.start();

    }

    public void update() {
        shaker.update(gsm.DeltaTime);
        handleInput();

        long time = System.nanoTime();
        deltaTime = (int) ((time - last_time) / 1000000);
        last_time = time;

        player.update(gsm.DeltaTime, Collisions);

        camera.update();

        for(int l=0; l< Achievements.size(); l++){
            if (Achievements.get(l).getTime() >= Achievements.get(l).getDuration()) {
                Achievements.remove(l);
            }
        }

    }

    public void draw(SpriteBatch g, int width, int height, float Time) {
        Gdx.gl.glClearColor(13/255f, 32/255f, 48/255f, 1);
        RenderCam();

        g.begin();
        g.setProjectionMatrix(shaker.getCombinedMatrix());
        //g.setProjectionMatrix(camera.combined);

        tiledBits.drawLayer(g, 64, Time,0);

        player.draw(g, Time);

        tiledBits.drawLayer(g, 64, Time,1);

        pe.update(gsm.DeltaTime);
        //g.setShader(shaderProgram);
        pe.draw(g);
        //g.setShader(null);
        pe.setPosition(gsm.MouseX, gsm.MouseY);
        if (pe.isComplete())
            pe.reset();

        g.end();

        //Overlay Layer
        guiBatch.begin();
        //guiBatch.setProjectionMatrix(cameraGui.combined);

        gsm.Render.GUIDeco(guiBatch, 0, height-80, "Multiplayer test");
        //gsm.Render.HUDNotification(guiBatch, width/2, height-100, 300 ,"Hey does this really wrap itself it would be so cool if it did so now i have to write a realllllllly long string to fill it up and make it wrap", gsm.ticks);

        if (Achievements.size() != 0) {
            for(int l=0; l< Achievements.size(); l++){
                Achievements.get(l).setTime(Time);
                gsm.Render.HUDAchievement(guiBatch, width-260, (70 * l), Achievements.get(l).getText(), Achievements.get(l).getIconID(), Achievements.get(l).getOpacity(), Achievements.get(l).getAnim(), Time);
            }

        }

        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) { //KeyHit
            int TileID;

            gsm.Cursor = 2;

            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);

        } else {
            gsm.Cursor = 0;
        }

        MenuDraw(guiBatch, width, height, Time);

        //gsm.Render.GUIDrawText(guiBatch, 50, 50, null, "" + network.GetClient());

        //ShaderDraw(guiBatch, gsm.MouseX, gsm.MouseY, gsm.Width, gsm.Height);

        guiBatch.end();

        //gsm.Render.DrawDebugLine(new Vector2(network.GetClient().x, network.GetClient().y), new Vector2(gsm.MouseX, gsm.MouseY), 1, Color.RED, camera.combined);
        //gsm.Render.DrawDebugPoint(center, 2, Color.RED, camera.combined);

        int size = 64;

        Rectangle playerrect = new Rectangle(player.Coords.x, player.Coords.y, 1, 1);
        playerrect.setCenter(playerrect.x + playerrect.getWidth()/2, playerrect.y + playerrect.getHeight()/2);

        if (gsm.Debug) {
            gsm.Render.debugRenderer.setProjectionMatrix(camera.combined);
            gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            gsm.Render.debugRenderer.setColor(Color.GREEN);
            gsm.Render.debugRenderer.rect(playerrect.x*64, playerrect.y*64, playerrect.width*64, playerrect.height*64);
            gsm.Render.debugRenderer.setColor(Color.YELLOW);
            gsm.Render.debugRenderer.rect(cameraBounds.x, cameraBounds.y, (cameraBounds.width), (cameraBounds.height));
            gsm.Render.debugRenderer.setColor(Color.RED);
            Collisions.forEach( number -> gsm.Render.debugRenderer.rect(number.x *64, number.y *64, (number.width)*64, (number.height)*64));

            gsm.Render.debugRenderer.end();

        }
    }

    public void RenderCam() {
        camera.update();
        //tiledMapRenderer.setView(camera);
        //tiledMapRenderer.render();
    }

    public void FollowCam(OrthographicCamera cam, float playerx, float playery, float lerp) {
        int mapBoundX = 10000;
        int mapBoundY = 10000;

        float tempx = position.x + (playerx*64 - position.x) * lerp * deltaTime;
        float tempy = position.y + (playery*64 - position.y) * lerp * deltaTime;

        cameraBounds = new Rectangle(camera.position.x - camera.viewportWidth/2 ,camera.position.y - camera.viewportHeight/2, camera.viewportWidth, camera.viewportHeight);

        if (tempx >= 0) {
            if(tempx + cameraBounds.getWidth() <= tiledBits.getBitTileObject(0).width*64*4) {
                position.x += (playerx*64 - position.x) * lerp * deltaTime;
            }
        }
        if (tempy >= 0) {
            if (tempy + cameraBounds.getHeight() <= tiledBits.getBitTileObject(0).height*64*4) {
                position.y += (playery*64 - position.y) * lerp * deltaTime;
            }
        }

        //    float PosibleX = position.x + (playerx - position.x) * lerp * deltaTime;
        //    if (PosibleX - (Gdx.graphics.getWidth()/2) >= 0 && PosibleX - (Gdx.graphics.getWidth()/2) <= mapBoundX) {
        //        position.x += (playerx - position.x) * lerp * deltaTime;
        //    }

        //    float PosibleY = position.y + (playery - position.y) * lerp * deltaTime;
        //    if (PosibleY - (Gdx.graphics.getHeight()/2) >= 0 && PosibleY - (Gdx.graphics.getHeight()/2) <= mapBoundY) {
        //        position.y += (playery - position.y) * lerp * deltaTime;
        //    } else if (PosibleY - (Gdx.graphics.getHeight()/2) >= mapBoundY) {
        //        position.y += (playery+160 - position.y) * lerp * deltaTime;
        //    }

        //position.x += ((player.getLocation()[0]*64)+40 - position.x) * lerp * deltaTime;
        //position.y += ((player.getLocation()[1]*64)+40 - position.y) * lerp * deltaTime;

        cam.position.set(position.x, position.y, cam.position.z);
        cam.update();
    }

    private void handleInput() {

        Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
        camera.unproject(pos);
        updategsmValues(gsm, pos);

        Vector2 center = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        Vector2 MousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        player.angle = Common.GetAngle(center, MousePos);


        Player.Direction[] temp = new Player.Direction[4];
        boolean moving = false;

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f) {
            Common.print("right");
            temp[3] = Player.Direction.East;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.East,5, gsm.DeltaTime);
        } else if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) < -0.2f) {
            Common.print("left");
            temp[2] = Player.Direction.West;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.West,5, gsm.DeltaTime);
        }

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) > 0.2f) {
            Common.print("down");
            temp[1] = Player.Direction.South;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.South,5, gsm.DeltaTime);
        } else if (gsm.ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) < -0.2f) {
            Common.print("up");
            temp[0] = Player.Direction.North;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.North,5, gsm.DeltaTime);
        }

        gsm.ctm.testInput();

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A)){
            shaker.addDamage(.2f);
        }

        if (gsm.ctm.isButtonDown(0, controlerManager.buttons.BUTTON_START) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
            //gsm.ctm.newController("xbox");
        }

        if (Gdx.input.isKeyPressed(Keys.W)) { //KeyHit
            temp[0] = Player.Direction.North;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.North,5, gsm.DeltaTime);
        }

        if (Gdx.input.isKeyPressed(Keys.S)) { //KeyHit
            temp[1] = Player.Direction.South;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.South,5, gsm.DeltaTime);
        }

        if (Gdx.input.isKeyPressed(Keys.A)) { //KeyHit
            temp[2] = Player.Direction.West;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.West,5, gsm.DeltaTime);
        }
        if (Gdx.input.isKeyPressed(Keys.D)) { //KeyHit
            temp[3] = Player.Direction.East;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.East,5, gsm.DeltaTime);
        }

        //We send the player the correct cardinal direction
        Player.Direction finalDirect = player.playerDirection;;

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
                finalDirect = Player.Direction.NorthWest;
            }
            if (temp[3] != null) { //EAST
                finalDirect = Player.Direction.NorthEast;
            }
        } else if (temp[1] != null) { //SOUTH
            if (temp[2] != null) { // WEST
                finalDirect = Player.Direction.SouthWest;
            }
            if (temp[3] != null) { //EAST
                finalDirect = Player.Direction.SouthEast;
            }
        } else {
            finalDirect = player.playerDirection;
        }

        player.MovePlayerVelocity(finalDirect,5, gsm.DeltaTime);

        //if (moving) {
        //    player.MovePlayerVelocity(finalDirect,5, gsm.DeltaTime);
        //}


        FollowCam(camera, player.Coords.x, player.Coords.y, 0.01f);
    }

    public void reSize(SpriteBatch g, int H, int W) {
        float posX = camera.position.x;
        float posY = camera.position.y;
        float posZ = camera.position.z;
        camera.setToOrtho(false);
        camera.position.set(posX, posY, posZ);

        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, W, H);
        guiBatch.setProjectionMatrix(matrix);
        shaker.reSize(camera);
        //ShaderResize(W, H);
        //cameraGui.setToOrtho(false);
    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/flat-earth/skin/flat-earth-ui.json"));
    }

    public void MenuInit() {

        setupSkin();
        stage = new Stage();

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.bottom();
        final TextField text1 = new TextField("", skin);
        table.add(text1).fillX();
        table.row();

        text1.addListener(new InputListener() {
            public boolean keyUp(InputEvent event, int keycode) {
                //Common.print("typed started at" + keycode);
                if (keycode == 66) {// Enter
                    //network.Send(text1.getText());
                    text1.setText("");
                    stage.setKeyboardFocus(null);
                }
                //if (keycode == 66) // Tab
                //Do nothing as of right now
                return false;
            }
        });


    }

    public void MenuDraw(SpriteBatch bbg, int width, int height, float Time) {
        stage.act(gsm.DeltaTime);
        stage.getRoot().draw(bbg, 1);
    }

    //Ends the Gui Shit

}
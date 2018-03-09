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
import com.thecubecast.ReEngine.Data.Achievement;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.PlayerPlatformer;
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;

import java.util.ArrayList;
import java.util.List;

public class PlatformerTestState extends GameState {

    PlayerPlatformer Player;

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
    ShaderProgram shaderProgram;
    SpriteBatch guiBatch;

    ParticleEffect pe;
    Body body;

    private List<Achievement> Achievements = new ArrayList<>();

    public PlatformerTestState(GameStateManager gsm) {
        super(gsm);
    }

    public void AddAchievement(String text, int IconID, float Time, float Durration, boolean Anim) {
        Achievement temp = new Achievement(text, IconID, Time,  Durration, Anim);
        Achievements.add(Achievements.size(), temp);
        Common.print("Added Achievement: " + text);
    }

    public void init() {
        Player = new PlayerPlatformer(64, new Vector2(0, -9), gsm);
        gsm.DiscordManager.setPresenceDetails("Multiplayer Demo - Level 1");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;;

        //SETUP TILEDMAP
        tiledMap = new TmxMapLoader().load("Saves/BITWISE/map2.tmx");
        tiledBits = new BitwiseTiles(tiledMap);

        for (int y = 0; y < tiledBits.getBitTileObject(0).realTile.size(); y++) {
            for(int x = 0; x < tiledBits.getBitTileObject(0).realTile.get(y).length; x++) {
                if (tiledBits.getBitTileObject(0).realTile.get(y)[x] == 1) {

                } else if ((tiledBits.getBitTileObject(0).realTile.get(y)[x] == 3)) {
                    Collisions.add(new Rectangle(x, y, 1, 0.99f));
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

        camera.position.set((Player.Coords.x*65), (Player.Coords.y*65), camera.position.z);
        position = camera.position;

        MenuInit();

        //SETUP SCENE2D INPUT
        Gdx.input.setInputProcessor(stage);

        //Setup the Shaders
        String vertexShader = Gdx.files.internal("testShader/vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("testShader/fragment.glsl").readString();
        shaderProgram = new ShaderProgram(vertexShader,fragmentShader);

        //SETUP THE PARTICLES
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("particles/fire.p"),Gdx.files.internal(""));
        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        pe.start();

    }

    public void update() {
        handleInput();

        long time = System.nanoTime();
        deltaTime = (int) ((time - last_time) / 1000000);
        last_time = time;

        Player.update(Gdx.graphics.getDeltaTime(), Collisions);

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
        g.setProjectionMatrix(camera.combined);

        //tiledBits.draw(g, 64, Time);

        //gsm.Render.DrawAny(g, tile, "Tiles", Common.roundDown((player.getLocation()[0]+1*64)),  Common.roundDown((player.getLocation()[1]*64)));
        //gsm.Render.GUIDrawText(g, Common.roundDown((player.getLocation()[0]+1*64)),  Common.roundDown((player.getLocation()[1]*64)-40), "" + tile);
        //g.draw(gsm.Render.Images[03], 0, 0, width, height);

        /*
        if (network.GetUsers().size() != 0) {
            for(int l=0; l< network.GetUsers().size(); l++){
                gsm.Render.GUIDrawText(g, Common.roundDown((network.GetUsers().get(l).x*64)), Common.roundDown((network.GetUsers().get(l).y*64)), Color.BLACK, network.GetUsers().get(l).username);
                g.draw(gsm.Render.GUI[24], network.GetUsers().get(l).x*64,	network.GetUsers().get(l).y*64,	gsm.Render.GUI[00].getWidth()/2, gsm.Render.GUI[00].getWidth()/2, (gsm.Render.GUI[00].getWidth()), (gsm.Render.GUI[00].getWidth()), 1, 1, network.GetUsers().get(l).angle, 0, 0, (gsm.Render.GUI[00].getWidth()), (gsm.Render.GUI[00].getWidth()), false, false);
            }

        }
        */

        //g.draw(gsm.Render.Tiles[80], Player.Coords.x*64,	Player.Coords.y*64,	gsm.Render.GUI[00].getWidth()/2, gsm.Render.GUI[00].getWidth()/2, (gsm.Render.GUI[00].getWidth()), (gsm.Render.GUI[00].getWidth()), 1, 1, Player.angle, 0, 0, (gsm.Render.GUI[00].getWidth()), (gsm.Render.GUI[00].getWidth()), false, false);
        //g.draw(gsm.Render.Tiles[80], Player.Coords.x*64,	Player.Coords.y*64,	gsm.Render.GUI[00].getWidth()/2, gsm.Render.GUI[00].getWidth()/2, (gsm.Render.GUI[00].getWidth()), (gsm.Render.GUI[00].getWidth()), 1, 1, 0, 0, 0, (gsm.Render.GUI[00].getWidth()), (gsm.Render.GUI[00].getWidth()), false, false);

        Player.draw(g, Time);

        pe.update(Gdx.graphics.getDeltaTime());
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

        Rectangle player = new Rectangle(Player.Coords.x, Player.Coords.y, 1, 1);
        player.setCenter(player.x + player.getWidth()/2, player.y + player.getHeight()/2);

        if (gsm.Debug) {
            gsm.Render.debugRenderer.setProjectionMatrix(camera.combined);
            gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            gsm.Render.debugRenderer.setColor(Color.GREEN);
            gsm.Render.debugRenderer.rect(player.x*64, player.y*64, player.width*64, player.height*64);
            gsm.Render.debugRenderer.setColor(Color.YELLOW);
            gsm.Render.debugRenderer.rect(cameraBounds.x, cameraBounds.y, (cameraBounds.width), (cameraBounds.height));
            gsm.Render.debugRenderer.setColor(Color.RED);
            Collisions.forEach( number -> gsm.Render.debugRenderer.rect(number.x *64, number.y *64, (number.width)*64, (number.height)*64));
            for(int i = 0; i < Collisions.size(); i++) {
                //gsm.Render.debugRenderer.rect(Collisions.get(i).x *64, Collisions.get(i).y *64, (Collisions.get(i).width)*64, (Collisions.get(i).height)*64);
            }

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

        gsm.MouseX = (int) pos.x;
        gsm.MouseY = (int) pos.y;
        gsm.MouseClick[1] = (int) pos.x;
        gsm.MouseClick[2] = (int) pos.y;
        gsm.MouseDrag[1] = (int) pos.x;
        gsm.MouseDrag[2] = (int) pos.y;

        Vector2 Location = new Vector2(Player.Coords.x, Player.Coords.y);

        Vector2 center = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        Vector2 MousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        float angle = Common.GetAngle(center, MousePos);

        Vector2 move = new Vector2(0,0);

        if (Gdx.input.isKeyPressed(Keys.W)) { //KeyHit
            //Player.MovePlayerVelocity(new Vector2(0, 10), 5, Gdx.graphics.getDeltaTime());
            if (gsm.Debug) {
                Player.Coords.y += 2;
            } else {
                //if (Player.ifColliding(new Vector2(0, -0.5f)))
                //Player.MovePlayerVelocity(PlayerPlatformer.Direction.up, Gdx.graphics.getDeltaTime());
            }
        }
        if (Gdx.input.isKeyPressed(Keys.S)) { //KeyHit
            //Player.MovePlayerVelocity(new Vector2(0, -1), 1, Gdx.graphics.getDeltaTime());
        }

        if (Gdx.input.isKeyPressed(Keys.A)) { //KeyHit
            Player.MovePlayerVelocity(PlayerPlatformer.Direction.left, Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.D)) { //KeyHit
            Player.MovePlayerVelocity(PlayerPlatformer.Direction.right, Gdx.graphics.getDeltaTime());
        }

        if (Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.W)) { //KeyHit
            Player.MovePlayerVelocity(PlayerPlatformer.Direction.up, Gdx.graphics.getDeltaTime());
            //if (Player.ifColliding(new Vector2(0, -0.5f)))
            //    Player.MovePlayerVelocity(PlayerPlatformer.Direction.up, Gdx.graphics.getDeltaTime());
        }

        if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) {
            String vertexShader = Gdx.files.internal("testShader/vertex.glsl").readString();
            String fragmentShader = Gdx.files.internal("testShader/fragment.glsl").readString();
            shaderProgram = new ShaderProgram(vertexShader,fragmentShader);
        }

        //move(new Vector2(move.x, move.y), Location);

        //Player.Coords.x = Location.x;
        //Player.Coords.y = Location.y;
        Player.angle = angle;

        FollowCam(camera, Player.Coords.x, Player.Coords.y, 0.01f);
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
        stage.act(Gdx.graphics.getDeltaTime());
        stage.getRoot().draw(bbg, 1);
    }

    //Ends the Gui Shit

}
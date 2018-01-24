// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thecubecast.ReEngine.Data.Achievement;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import kryoNetwork.KryoClient;

import com.thecubecast.ReEngine.Data.KeysDown;

public class TestState2 extends GameState {

    KryoClient network;

    private Skin skin;
    private Stage stage;
    private Table table;

    float lerp = 0.005f;
    Vector3 position;
    long last_time;
    int deltaTime;

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    OrthographicCamera camera;
    SpriteBatch guiBatch;

    ParticleEffect pe;
    Body body;

    World world;
    Box2DDebugRenderer debugRenderer;

    //HUD Elements
    public boolean menuOpen = false;
    int tics = 0;
    boolean flashfuel = false;
    List<Achievement> Achievements = new ArrayList<Achievement>();
    List<Achievement> MoneyFeedback = new ArrayList<Achievement>();

    //Controls
    List<KeysDown> KeysDw = new ArrayList<KeysDown>();

    public TestState2(GameStateManager gsm) {
        super(gsm);
    }

    public void AddAchievement(String text, int IconID, float Time, float Durration, boolean Anim) {
        Achievement temp = new Achievement(text, IconID, Time,  Durration, Anim);
        Achievements.add(Achievements.size(), temp);
        Common.print("Added Achievement: " + text);
    }

    public void AddMoneyFeedback(String text, float Time, float Durration) {
        Achievement temp = new Achievement(text, 0, Time,  Durration, false);
        gsm.Audio.play("CashGet");
        MoneyFeedback.add(MoneyFeedback.size(), temp);
        Common.print("Added MoneyFeedback: " + text);
    }

    public void init() {

        //SETUP NETWORK CONNECTION
        try {
            network = new KryoClient(gsm.Username, gsm.IpAdress, 54555, 54777);
            while(!network.established) {
                Common.sleep(5);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //SETUP TILEDMAP
        tiledMap = new AtlasTmxMapLoader().load("Saves/Save1/map.tmx");
        if (tiledMap.hashCode() != network.GetTiledMap())
            Common.print("Does not match!");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap , 5f);

        //gsm.Audio.playMusic("Rain", true);


        //SETUP CAMERA SPRITEBATCH AND MENU
        guiBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        camera.position.set((network.GetClient().x*80)+40, (network.GetClient().y*80)+40, camera.position.z);
        position = camera.position;

        last_time = System.nanoTime();

        network.Update();

        MenuInit();

        //SETUP SCENE2D INPUT
        Gdx.input.setInputProcessor(stage);


        //SETUP THE PARTICLES
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("particles/fire.p"),Gdx.files.internal(""));
        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        pe.start();

        //SETUP BOX2D
        Box2DInit();

    }

    public void update() {
        Box2DUpdate();
        handleInput();

        long time = System.nanoTime();
        deltaTime = (int) ((time - last_time) / 1000000);
        last_time = time;

        camera.update();

        for(int l=0; l< Achievements.size(); l++){
            if (Achievements.get(l).getTime() >= Achievements.get(l).getDuration()) {
                Achievements.remove(l);
            }
        }

        for(int l=0; l< MoneyFeedback.size(); l++){
            if (MoneyFeedback.get(l).getTime() >= MoneyFeedback.get(l).getDuration()) {
                MoneyFeedback.remove(l);
            }
        }
        network.Update();
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

        Vector2 Location = new Vector2(network.GetClient().x, network.GetClient().y);

        Vector2 center = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        Vector2 MousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        float angle = Common.GetAngle(center, MousePos);

        //Common.print("Angle: " + Location.angle(MousePos));
        //Common.print("Start: " + Location + " Mouse: " + MousePos);

        if (Gdx.input.isKeyPressed(Keys.W)) { //KeyHit
            move(new Vector2(0, 1));
            Location.y += 1;
        }
        if (Gdx.input.isKeyPressed(Keys.S)) { //KeyHit
            Location.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Keys.A)) { //KeyHit
            Location.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) { //KeyHit
            Location.x += 1;
        }

        network.updateClientPos(Location, angle);
        camera.position.set((network.GetClient().x*40)+40, (network.GetClient().y*40)+40, camera.position.z);
        //FollowCam(camera, Common.roundDown(x), Common.roundDown(y));
    }

    public void draw(SpriteBatch g, int width, int height, float Time) {
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        RenderCam();

        g.begin();
        g.setProjectionMatrix(camera.combined);

        //gsm.Render.DrawAny(g, tile, "Tiles", Common.roundDown((player.getLocation()[0]+1*80)),  Common.roundDown((player.getLocation()[1]*80)));
        //gsm.Render.GUIDrawText(g, Common.roundDown((player.getLocation()[0]+1*80)),  Common.roundDown((player.getLocation()[1]*80)-40), "" + tile);
        //g.draw(gsm.Render.Images[03], 0, 0, width, height);

        if (network.GetUsers().size() != 0) {
            for(int l=0; l< network.GetUsers().size(); l++){
                gsm.Render.GUIDrawText(g, Common.roundDown((network.GetUsers().get(l).x*80)), Common.roundDown((network.GetUsers().get(l).y*80)), Color.BLACK, network.GetUsers().get(l).username);
                g.draw(gsm.Render.GUI[24], network.GetUsers().get(l).x*40,	network.GetUsers().get(l).y*40,	gsm.Render.Tiles[59].getWidth()/2, gsm.Render.Tiles[59].getWidth()/2, (gsm.Render.Tiles[59].getWidth()), (gsm.Render.Tiles[59].getWidth()), 1, 1, network.GetUsers().get(l).angle, 0, 0, (gsm.Render.Tiles[59].getWidth()), (gsm.Render.Tiles[59].getWidth()), false, false);
            }

        }

        pe.update(Gdx.graphics.getDeltaTime());
        pe.draw(g);
        pe.setPosition(gsm.MouseX, gsm.MouseY);
        if (pe.isComplete())
            pe.reset();

        debugRenderer.render(world, camera.combined);

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

        if (MoneyFeedback.size() != 0) {
            for(int l=0; l< MoneyFeedback.size(); l++){
                MoneyFeedback.get(l).setTime(Time);
                gsm.Render.MoneyFeedback(guiBatch, width/2 + 40, height/2 + 40 + (30 * l), MoneyFeedback.get(l).getText(), MoneyFeedback.get(l).getDuration()/MoneyFeedback.get(l).getTime());
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

        guiBatch.end();

        //gsm.Render.DrawDebugLine(new Vector2(network.GetClient().x, network.GetClient().y), new Vector2(gsm.MouseX, gsm.MouseY), 1, Color.RED, camera.combined);
        //gsm.Render.DrawDebugPoint(center, 2, Color.RED, camera.combined);
    }

    public void RenderCam() {
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    public void FollowCam(OrthographicCamera cam, int playerx, int playery) {
        int mapBoundX = 10000;
        int mapBoundY = 10000;

        float PosibleX = position.x + (playerx - position.x) * lerp * deltaTime;
        if (PosibleX - (Gdx.graphics.getWidth()/2) >= 0 && PosibleX - (Gdx.graphics.getWidth()/2) <= mapBoundX) {
            position.x += (playerx - position.x) * lerp * deltaTime;
        }

        float PosibleY = position.y + (playery - position.y) * lerp * deltaTime;
        if (PosibleY - (Gdx.graphics.getHeight()/2) >= 0 && PosibleY - (Gdx.graphics.getHeight()/2) <= mapBoundY) {
            position.y += (playery - position.y) * lerp * deltaTime;
        } else if (PosibleY - (Gdx.graphics.getHeight()/2) >= mapBoundY) {
            position.y += (playery+160 - position.y) * lerp * deltaTime;
        }

        //position.x += ((player.getLocation()[0]*80)+40 - position.x) * lerp * deltaTime;
        //position.y += ((player.getLocation()[1]*80)+40 - position.y) * lerp * deltaTime;

        cam.position.set(position.x, position.y, cam.position.z);
        cam.update();
    }

    public void move(Vector2 pos) {
        if (pos.x < 0) { //Moving left

        } else if (pos.x > 0) { // Moving right

        }

        if (pos.y < 0) { // Moving down

        } else if (pos.y > 0) {

        }
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
                    network.Send(text1.getText());
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

    public void Box2DInit() {
        Box2D.init();
        world = new World(new Vector2(0, -10), true);

        debugRenderer = new Box2DDebugRenderer();

        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the world
        bodyDef.position.set(50, 300);

        // Create our body in the world using our body definition
        body = world.createBody(bodyDef);

        // Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(6f);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();




        // Create our body definition
        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(new Vector2(gsm.Render.Tiles.length*2, gsm.Render.Tiles.length/2));

        // Create a body from the defintion and add it to the world
        Body groundBody = world.createBody(groundBodyDef);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(gsm.Render.Tiles.length*5, gsm.Render.Tiles.length/2);
        // Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0.0f);
        // Clean up after ourselves
        groundBox.dispose();
    }

    public void Box2DUpdate() {
        world.step(1/30f, 6, 6);
    }


}
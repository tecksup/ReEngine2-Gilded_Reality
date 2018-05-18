// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;
import kryoNetwork.KryoClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MultiplayerTestState extends GameState {

    private KryoClient network;

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
    ShaderProgram shaderProgram;
    SpriteBatch guiBatch;

    ParticleEffect pe;
    Body body;

    private List<Achievement> Achievements = new ArrayList<>();

    public MultiplayerTestState(GameStateManager gsm) {
        super(gsm);
    }

    public void AddAchievement(String text, int IconID, float Time, float Durration, boolean Anim) {
        Achievement temp = new Achievement(text, IconID, Time,  Durration, Anim);
        Achievements.add(Achievements.size(), temp);
        Common.print("Added Achievement: " + text);
    }

    public void init() {

        gsm.DiscordManager.setPresenceDetails("Multiplayer Demo - Level 1");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;;

        //SETUP NETWORK CONNECTION
        try {
            network = new KryoClient("username", "IP", 54555, 54777);
            while(!network.established) {
                Common.sleep(5);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //SETUP TILEDMAP
        tiledMap = new TmxMapLoader().load("Saves/BITWISE/map.tmx");
        tiledBits = new BitwiseTiles(tiledMap);

        if (tiledMap.hashCode() != network.GetTiledMap())
            Common.print("Does not match!");

//        for(int i = 0; i < tiledMap.getLayers().get("Walls").getObjects().getCount(); i++) {
//            //tiledMap.getLayers().get("Walls").getObjects().
//            Collisions.add(new Rectangle(2, 2, 2, 2));
//        }

        Collisions.add(new Rectangle(2, 2, 2, 2));
        Collisions.add(new Rectangle(4, 7, 1, 4));

        //gsm.Audio.playMusic("Rain", true);


        //SETUP CAMERA SPRITEBATCH AND MENU
        guiBatch = new SpriteBatch();

        //ShaderInit(guiBatch);

        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        camera.position.set((network.GetClient().x*64), (network.GetClient().y*64), camera.position.z);
        position = camera.position;

        //Setup Network
        network.Update();

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

        camera.update();

        for(int l=0; l< Achievements.size(); l++){
            if (Achievements.get(l).getTime() >= Achievements.get(l).getDuration()) {
                Achievements.remove(l);
            }
        }

        network.Update();
    }

    public void draw(SpriteBatch g, int width, int height, float Time) {
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        RenderCam();

        g.begin();
        g.setProjectionMatrix(camera.combined);

        //tiledBits.draw(g, 64, , Time);

        //gsm.Render.DrawAny(g, tile, "Tiles", Common.roundDown((player.getLocation()[0]+1*64)),  Common.roundDown((player.getLocation()[1]*64)));
        //gsm.Render.GUIDrawText(g, Common.roundDown((player.getLocation()[0]+1*64)),  Common.roundDown((player.getLocation()[1]*64)-40), "" + tile);
        //g.draw(gsm.Render.Images[03], 0, 0, width, height);

        if (network.GetUsers().size() != 0) {
            for(int l=0; l< network.GetUsers().size(); l++){
                gsm.Render.GUIDrawText(g, Common.roundDown((network.GetUsers().get(l).x*64)), Common.roundDown((network.GetUsers().get(l).y*64)), network.GetUsers().get(l).username);
                //g.draw(gsm.Render.GUI[24], network.GetUsers().get(l).x*64,	network.GetUsers().get(l).y*64,	gsm.Render.GUI[00].getWidth()/2, gsm.Render.GUI[00].getWidth()/2, (gsm.Render.GUI[00].getWidth()), (gsm.Render.GUI[00].getWidth()), 1, 1, network.GetUsers().get(l).angle, 0, 0, (gsm.Render.GUI[00].getWidth()), (gsm.Render.GUI[00].getWidth()), false, false);
            }

        }

        pe.update(Gdx.graphics.getDeltaTime());
        g.setShader(shaderProgram);
        pe.draw(g);
        g.setShader(null);
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

        gsm.Render.GUIDrawText(guiBatch, 50, 50, "" + network.GetClient());

        //ShaderDraw(guiBatch, gsm.MouseX, gsm.MouseY, gsm.Width, gsm.Height);

        guiBatch.end();

        //gsm.Render.DrawDebugLine(new Vector2(network.GetClient().x, network.GetClient().y), new Vector2(gsm.MouseX, gsm.MouseY), 1, Color.RED, camera.combined);
        //gsm.Render.DrawDebugPoint(center, 2, Color.RED, camera.combined);

        int size = 16; // gsm.Render.GUI[00].getWidth()
        Rectangle player = new Rectangle(network.GetClient().x, network.GetClient().y, size, size);
        player.setCenter(network.GetClient().x + size/2, network.GetClient().y + size/2);

        //Rectangle player = new Rectangle(posx, posy, size, size);
        //layer.setCenter(posx + size/2, posy + size/2);

        //Common.print("hit: " + player.overlaps(Collision));

        /*
        gsm.Render.debugRenderer.setProjectionMatrix(camera.combined);
        gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        gsm.Render.debugRenderer.setColor(Color.GREEN);
        gsm.Render.debugRenderer.rect(player.x*64, player.y*64, player.width, player.height);
        gsm.Render.debugRenderer.setColor(Color.RED);
        for(int i = 0; i < Collisions.size(); i++) {
            gsm.Render.debugRenderer.rect(Collisions.get(i).x *64, Collisions.get(i).y *64, (Collisions.get(i).width)*64, (Collisions.get(i).height)*64);
        }
        gsm.Render.debugRenderer.end();
        */
    }

    public void RenderCam() {
        camera.update();
        //tiledMapRenderer.setView(camera);
        //tiledMapRenderer.render();
    }

    public void FollowCam(OrthographicCamera cam, int playerx, int playery, float lerp) {
        int mapBoundX = 10000;
        int mapBoundY = 10000;

        position.x += (playerx*64 - position.x) * lerp * deltaTime;
        position.y += (playery*64 - position.y) * lerp * deltaTime;

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

        Vector2 Location = new Vector2(network.GetClient().x, network.GetClient().y);

        Vector2 center = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        Vector2 MousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        float angle = Common.GetAngle(center, MousePos);

        //Common.print("Angle: " + Location.angle(MousePos));
        //Common.print("Start: " + Location + " Mouse: " + MousePos);

            Vector2 move = new Vector2(0,0);

        if (Gdx.input.isKeyPressed(Keys.W)) { //KeyHit
            move.y += 1;
            //move(new Vector2(0, 1), Location);
        }
        if (Gdx.input.isKeyPressed(Keys.S)) { //KeyHit
            move.y -= 1;
            //move(new Vector2(0, -1), Location);
        }
        if (Gdx.input.isKeyPressed(Keys.A)) { //KeyHit
            move.x -= 1;
            //move(new Vector2(-1, 0), Location);
        }
        if (Gdx.input.isKeyPressed(Keys.D)) { //KeyHit
            move.x += 1;
            //move(new Vector2(1, 0), Location);
        }

        if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) {
            String vertexShader = Gdx.files.internal("testShader/vertex.glsl").readString();
            String fragmentShader = Gdx.files.internal("testShader/fragment.glsl").readString();
            shaderProgram = new ShaderProgram(vertexShader,fragmentShader);
        }

        move(new Vector2(move.x, move.y), Location);

        network.updateClientPos(Location, angle);
        //camera.position.set((network.GetClient().x*40)+40, (network.GetClient().y*40)+40, camera.position.z);
        FollowCam(camera, Common.roundDown(Location.x), Common.roundDown(Location.y), 0.01f);
    }

    public void move(Vector2 pos, Vector2 Location) {
        if (pos.x < 0) { //Moving left
            if (checkCollision(Location.x - (pos.x*-1), Location.y)) {
                //Cant move
            } else {
                Location.x -= (pos.x*-1);
            }
        } else if (pos.x > 0) { // Moving right
            if (checkCollision(Location.x + pos.x, Location.y)) {
                //Cant move
            } else {
                Location.x += (pos.x);
            }
        }

        if (pos.y < 0) { // Moving down
            if (checkCollision(Location.x, Location.y - (pos.y*-1))) {
                //Cant move
            } else {
                Location.y -= (pos.y*-1);
            }
        } else if (pos.y > 0) {
            if (checkCollision(Location.x, Location.y + pos.y)) {
                //Cant move
            } else {
                Location.y += pos.y;
            }
        }
    }

    public boolean checkCollision(float posx, float posy) {
      for(int i = 0; i < Collisions.size(); i++) {
          if (posx >= Collisions.get(i).getX() && posx < (Collisions.get(i).getX() + Collisions.get(i).getWidth()) && posy >= Collisions.get(i).getY() && posy < (Collisions.get(i).getY() + Collisions.get(i).getHeight())) {
              return true; // Dont move
          }
      }
      return false;
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
/*
    public void ShaderInit(SpriteBatch batch) {
        rock = new Texture(Gdx.files.internal("rock.png"));
        rockNormals = new Texture(Gdx.files.internal("rock_n.png"));

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(VERT, FRAG);
        //ensure it compiled
        if (!shader.isCompiled())
            throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());
        //print any warnings
        if (shader.getLog().length()!=0)
            System.out.println(shader.getLog());

        //setup default uniforms
        shader.begin();

        //our normal map
        shader.setUniformi("u_normals", 1); //GL_TEXTURE1

        //light/ambient colors
        //LibGDX doesn't have Vector4 class at the moment, so we pass them individually...
        shader.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
        shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
        shader.setUniformf("Falloff", FALLOFF);

        //LibGDX likes us to end the shader program
        shader.end();

    }

    public void ShaderDraw(SpriteBatch batch, int MX, int MY, int W, int H) {
        batch.setProjectionMatrix(camera.combined);
        batch.setShader(shader);

        //reset light Z
        if (Gdx.input.isTouched()) {
            LIGHT_POS.z = DEFAULT_LIGHT_Z;
            System.out.println("New light Z: "+LIGHT_POS.z);
        }

        //shader will now be in use...

        //update light position, normalized to screen resolution
        float x = MX / (float) W;
        float y = MY / (float) H;

        LIGHT_POS.x = x;
        LIGHT_POS.y = y;

        //send a Vector4f to GLSL
        shader.setUniformf("LightPos", LIGHT_POS);

        //bind normal map to texture unit 1
        rockNormals.bind(1);

        //bind diffuse color to texture unit 0
        //important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
        rock.bind(0);

        //draw the texture unit 0 with our shader effect applied
        batch.draw(rock, 0, 0);

        //batch.setShader(null);
    }

    public void ShaderResize(int width, int height) {
        shader.begin();
        shader.setUniformf("Resolution", width, height);
        shader.end();
    }

    public void ShaderDispose() {
        rock.dispose();
        rockNormals.dispose();
        shader.dispose();
    }
    */

    @Override
    public void Shutdown() {

    }
}
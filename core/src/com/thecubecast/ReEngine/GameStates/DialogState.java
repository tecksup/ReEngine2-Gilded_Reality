// GameState that shows logo.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;
import com.thecubecast.ReEngine.worldObjects.NPC;
import com.thecubecast.ReEngine.worldObjects.Player;
import com.thecubecast.ReEngine.worldObjects.WorldObject;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.loadAnim;

public class DialogState extends GameState {

    Player player;
    NPC hank;
    private List<collision> Collisions = new ArrayList<>();
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

    public DialogState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {
        MenuInit();

        player = new Player(20*16,20*16, new Vector3(16, 16, 16));
        Entities.add(player);
        gsm.DiscordManager.setPresenceDetails("topdown Demo - Level 1");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;;

        //SETUP TILEDMAP
        tiledMap = new TmxMapLoader().load("Saves/BITWISE/EmptyRoom/map.tmx");
        tiledBits = new BitwiseTiles(tiledMap);

        for (int y = 0; y < tiledBits.getBitTileObject(1).realTile.size(); y++) {
            for(int x = 0; x < tiledBits.getBitTileObject(1).realTile.get(y).length; x++) {
                if (tiledBits.getBitTileObject(1).realTile.get(y)[x] == 1) {

                } else if ((tiledBits.getBitTileObject(1).realTile.get(y)[x] == 6)) { //Bush (stupid grass block)
                    Rectangle tempRect = new Rectangle(x*16, y*16, 16, 16);
                    Collisions.add(new collision(tempRect, tempRect.hashCode()));
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

        hank = new NPC("Hank", 20*16, 23*16, new Vector3(32, 32, 4), 100) {
            Texture sprite = new Texture(Gdx.files.internal("Sprites/8direct/south.png"));

            private Animation<TextureRegion> idle;
            Label NameLabel;
            Group stage;
            ProgressBar HealthBar;
            @Override
            public void draw(SpriteBatch batch, float Time) {
                TextureRegion currentFrame = idle.getKeyFrame(Time, true);

                batch.draw(currentFrame, getPosition().x, getPosition().y);
                stage.draw(batch, 1);
            }

            @Override
            public void interact() {
                Common.print("Hank got activated!");
            }

            @Override
            public void init(int Width, int Height){
                idle = new Animation<TextureRegion>(0.1f, loadAnim(sprite, "Sprites/8direct/south.png", 4, 1));
                Skin skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));

                stage = new Group();

                NameLabel = new Label(getName(),skin, "white");
                HealthBar = new ProgressBar(0f, 10f, 0.1f, false, skin, "Health_npc");
                HealthBar.setValue(getHealth()/10);
                HealthBar.setWidth(40);
                stage.addActor(NameLabel);
                stage.addActor(HealthBar);
            }

            @Override
            public void update(float delta, List<collision> Colls) {
                for(int i = 0; i < Colls.size(); i++) {
                    if (Colls.get(i).getHash() == this.hashCode()) {
                        Rectangle hankbox = new Rectangle((int) hank.getHitbox().x, (int) hank.getHitbox().y, (int) hank.getHitbox().width,(int) hank.getHitbox().height);
                        Colls.get(i).setRect(hankbox);
                    }
                }
                super.update(delta, Colls);
                stage.act(Gdx.graphics.getDeltaTime());
                NameLabel.setPosition((int) getPosition().x+20-(NameLabel.getWidth()/2), (int) getPosition().y+50);
                HealthBar.setValue(getHealth()/10);
                HealthBar.setPosition((int) getPosition().x+20-(HealthBar.getWidth()/2), (int) getPosition().y+44);
            }
        };
        hank.init(gsm.Width, gsm.Height);
        Rectangle hankbox = new Rectangle((int) hank.getHitbox().x, (int) hank.getHitbox().y,(int) hank.getHitbox().width,(int) hank.getHitbox().height);
        Collisions.add(new collision(hankbox, hank.hashCode()));

        WorldObject Random = new WorldObject(18*16, 20*16, new Vector3(32, 32, 4)) {
            @Override
            public void init(int Width, int Height) {

            }

            @Override
            public void update(float delta, List<collision> Colls) {

            }

            @Override
            public void draw(SpriteBatch batch, float Time) {

            }
        };
        Entities.add(Random);
        Entities.add(hank);
    }

    public void update() {
        for(int i = 0; i < Entities.size(); i++) {
            Entities.get(i).update(Gdx.graphics.getDeltaTime(), Collisions);
        }

        handleInput();
        player.update(gsm.DeltaTime, Collisions);

        camera.position.set((int) (player.getPosition().x),(int) (player.getPosition().y), 0);
        camera.update();
    }

    public void draw(SpriteBatch g, int height, int width, float Time) {
        shaker.update(gsm.DeltaTime);

        //camera.setToOrtho(false, width, height);
        g.setProjectionMatrix(shaker.getCombinedMatrix());
        g.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);

        tiledBits.drawLayer(g, 16, Time,0, 0, false);
        tiledBits.drawLayer(g, 16, Time,1, 0, false);
        for(int i = 0; i < Entities.size(); i++) {
            Entities.get(i).draw(g, Time);
        }
        g.end();

        guiBatch.setProjectionMatrix(Guicamera.combined);
        guiBatch.begin();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.getRoot().draw(guiBatch, 1);
        guiBatch.end();

        if (gsm.Debug) {
            gsm.Render.debugRenderer.setProjectionMatrix(shaker.getCombinedMatrix());
            gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            gsm.Render.debugRenderer.setColor(Color.GREEN);
            for(int i = 0; i < Entities.size(); i++) {
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().x, Entities.get(i).getHitbox().y, Entities.get(i).getHitbox().width, Entities.get(i).getHitbox().height);
            }
            gsm.Render.debugRenderer.setColor(Color.GREEN);
            gsm.Render.debugRenderer.rect(player.getIntereactBox().x, player.getIntereactBox().y, player.getIntereactBox().width, player.getIntereactBox().height);
            gsm.Render.debugRenderer.setColor(Color.RED);
            Collisions.forEach( number -> gsm.Render.debugRenderer.rect(number.getRect().x, number.getRect().y, (number.getRect().width), (number.getRect().height)));

            gsm.Render.debugRenderer.end();

        }

    }

    public void handleInput() {

        oldPlayer.Direction[] temp = new oldPlayer.Direction[4];
        boolean moving = false;

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f || Gdx.input.isKeyPressed(Keys.D)) {
            temp[3] = oldPlayer.Direction.East;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.East,5, gsm.DeltaTime);
        } else if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) < -0.2f || Gdx.input.isKeyPressed(Keys.A)) {
            temp[2] = oldPlayer.Direction.West;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.West,5, gsm.DeltaTime);
        }

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyPressed(Keys.S)) {
            temp[1] = oldPlayer.Direction.South;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.South,5, gsm.DeltaTime);
        } else if (gsm.ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyPressed(Keys.W)) {
            temp[0] = oldPlayer.Direction.North;
            moving = true;
            player.MovePlayerVelocity(Player.Direction.North,5, gsm.DeltaTime);
        }

        if (gsm.ctm.isButtonJustDown(1, controlerManager.buttons.BUTTON_START)){
            Common.print("Player 2 joined the game!!");
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_START) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
            Common.print("Escape!!");
            player.setPosition(hank.getHitbox().x, hank.getHitbox().y);
            Common.print("player position" + player.getPosition());
            //gsm.ctm.newController("template");
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

        if (moving) {
            player.MovePlayerVelocity(finalDirect,5, gsm.DeltaTime);
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Keys.R)){
            for(int i = 0; i < Entities.size(); i++) {
                if(Entities.get(i).ifColliding(player.getIntereactBox())){
                    if(Entities.get(i) instanceof NPC) {
                        NPC Entitemp = (NPC) Entities.get(i);
                        Entitemp.interact();
                    }
                }
            }
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


}
// GameState that shows logo.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.Graphics.Scene2D.Dialog;
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;
import com.thecubecast.ReEngine.worldObjects.NPC;
import com.thecubecast.ReEngine.worldObjects.Player;
import com.thecubecast.ReEngine.worldObjects.WorldObject;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;
import com.thecubecast.ReEngine.worldObjects.WorldObjectComp;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.loadAnim;
import static com.thecubecast.ReEngine.worldObjects.WorldObject.polyoverlap;

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
    private Stage Guistage;
    private Table table;

    private List<ParticleEffect> ParticleEffects = new ArrayList<>();

    Boolean DialogOpen = false;
    float DialogTime = 0;
    private List<Dialog> DialogCache = new ArrayList<>();
    Window dialogBox;
    Label dialogBoxText;

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

        hank = new NPC("Hank", 20*16, 23*16, new Vector3(32, 32, 4), .1f, 100) {
            Texture sprite = new Texture(Gdx.files.internal("Sprites/8direct/south.png"));

            private Animation<TextureRegion> idle;
            Label NameLabel;
            Group stage;
            ProgressBar HealthBar;
            @Override
            public void draw(SpriteBatch batch, float Time) {
                TextureRegion currentFrame = idle.getKeyFrame(Time, true);

                batch.draw(currentFrame, getPosition().x-8, getPosition().y-4);
                stage.draw(batch, 1);
            }

            @Override
            public void interact() {
                Common.print("Hank got activated!");
                AddDialog(getName(), "Hey, I can talk now!");
                Dialog temp = new Dialog(getName(), "I COULD do custom stuff now") {
                    @Override
                    public void exit() {

                    }
                };
                AddDialog(temp);
                AddDialog(getName(), "When will i be aloud to walk around and do stuff?");
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
                        //Rectangle hankbox = new Rectangle();
                        //Colls.get(i).setRect(hankbox);
                    }
                }
                super.update(delta, Colls);
                stage.act(Gdx.graphics.getDeltaTime());
                NameLabel.setText(getName());
                NameLabel.setPosition((int) getPosition().x + 3, (int) getPosition().y+50);
                HealthBar.setValue(getHealth()/10);
                HealthBar.setPosition((int) getPosition().x + 15 - (HealthBar.getWidth()/2), (int) getPosition().y+44);
            }
        };
        hank.init(gsm.Width, gsm.Height);
        hank.setVelocity(0, -1);
        Rectangle hankbox = new Rectangle((int) hank.getHitbox().x, (int) hank.getHitbox().y,(int) hank.getHitbox().width,(int) hank.getHitbox().height);
        //Collisions.add(new collision(hankbox, hank.hashCode()));

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
        UpdateParticles();
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
            if(Entities.get(i).ifColliding(player.getIntereactBox())){
                if(Entities.get(i) instanceof NPC) {
                    NPC Entitemp = (NPC) Entities.get(i);
                    Color temp = g.getColor();
                    g.setColor(Color.YELLOW);
                    Entitemp.drawHighlight(g, Time);
                    g.setColor(temp);
                    Entities.get(i).draw(g, Time);
                }
            } else {
                WorldObjectComp temp = new WorldObjectComp();
                Entities.sort(temp);
                Entities.get(i).draw(g, Time);
            }
        }
        DrawParticleEffects(g);
        g.end();

        guiBatch.setProjectionMatrix(Guicamera.combined);
        guiBatch.begin();
        MenuDraw(Gdx.graphics.getDeltaTime());
        guiBatch.end();

        if (gsm.Debug) {
            gsm.Render.debugRenderer.setProjectionMatrix(shaker.getCombinedMatrix());
            gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            gsm.Render.debugRenderer.setColor(Color.GREEN);
            for (int i = 0; i < Entities.size(); i++) {
                gsm.Render.debugRenderer.polygon(Entities.get(i).getHitboxPoly().getVertices());
            }
            gsm.Render.debugRenderer.setColor(Color.ORANGE);
            gsm.Render.debugRenderer.polygon(player.getAttackBox().getVertices());
            gsm.Render.debugRenderer.setColor(Color.YELLOW);
            gsm.Render.debugRenderer.rect(player.getIntereactBox().x, player.getIntereactBox().y, player.getIntereactBox().width, player.getIntereactBox().height);
            gsm.Render.debugRenderer.setColor(Color.RED);
            Collisions.forEach(number -> gsm.Render.debugRenderer.rect(number.getRect().x, number.getRect().y, (number.getRect().width), (number.getRect().height)));

            gsm.Render.debugRenderer.end();

        }

    }

    public void handleInput() {

        Player.Direction[] temp = new Player.Direction[4];
        boolean moving = false;

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f || Gdx.input.isKeyPressed(Keys.D)) {
            temp[3] = Player.Direction.East;
            moving = true;
            if (DialogOpen) {
                //Dont move
            } else
                player.MovePlayerVelocity(Player.Direction.East,5, gsm.DeltaTime);
        } else if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) < -0.2f || Gdx.input.isKeyPressed(Keys.A)) {
            temp[2] = Player.Direction.West;
            moving = true;
            if (DialogOpen) {
                //Dont move
            } else
                player.MovePlayerVelocity(Player.Direction.West,5, gsm.DeltaTime);
        }

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyPressed(Keys.S)) {
            temp[1] = Player.Direction.South;
            moving = true;
            if (DialogOpen) {
                //Dont move
            } else
                player.MovePlayerVelocity(Player.Direction.South,5, gsm.DeltaTime);
        } else if (gsm.ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyPressed(Keys.W)) {
            temp[0] = Player.Direction.North;
            moving = true;
            if (DialogOpen) {
                //Dont move
            } else
                player.MovePlayerVelocity(Player.Direction.North,5, gsm.DeltaTime);
        }

        if (gsm.ctm.isButtonJustDown(1, controlerManager.buttons.BUTTON_START)){
            Common.print("Player 2 joined the game!!");
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_START) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
            Common.print("Escape!!");
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
        }

        if (moving) {
            if (DialogOpen) {
                //Dont move
            } else
                player.MovePlayerVelocity(finalDirect,5, gsm.DeltaTime);
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Keys.R)){
            if (DialogOpen) {
                if(System.nanoTime()-DialogTime > 1) {
                    if (DialogCache.size() > 0) {
                        DialogCache.get(0).exit();
                        DialogCache.remove(0);
                        UpdateDialogBox();
                        if(DialogCache.size() == 0) {
                            DialogOpen = false;
                        }
                    } else {
                        DialogOpen = false;
                    }
                }
            } else {
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

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_X)){
            Common.print("Attacked");
            AddParticleEffect("Health", player.getIntereactBox().x + player.getIntereactBox().width/2, player.getIntereactBox().y + player.getIntereactBox().height/2);
            for(int i = 0; i < Entities.size(); i++) {
                if(polyoverlap(player.getAttackBox(), Entities.get(i).getHitbox())){
                    if(Entities.get(i) instanceof NPC) {
                        NPC Entitemp = (NPC) Entities.get(i);

                        float HitVelocity = 40;

                        Vector2 hitDirection = new Vector2(player.VecDirction().x*HitVelocity, player.VecDirction().y*HitVelocity);
                        Entitemp.damage(10, hitDirection);
                    }
                }
            }
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_L3)){
            Common.print("Healed");
            for(int i = 0; i < Entities.size(); i++) {
                if(polyoverlap(player.getAttackBox(), Entities.get(i).getHitbox())){
                    if(Entities.get(i) instanceof NPC) {
                        NPC Entitemp = (NPC) Entities.get(i);
                        Entitemp.heal(10);
                        AddParticleEffect("HealthArea", Entitemp.getPosition().x + Entitemp.getSize().x/2, Entitemp.getPosition().y + Entitemp.getSize().y/2);
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

        Guistage = new Stage(new StretchViewport(gsm.Width, gsm.Height));
        Gdx.input.setInputProcessor(Guistage);

        table = new Table();
        table.setFillParent(true);
        Guistage.addActor(table);

        dialogBox = new Window("", skin, "Dialog");
        dialogBoxText = new Label("", skin, "white");
        dialogBox.add(dialogBoxText);
        table.add(dialogBox).bottom().fillX().expand();

    }

    public void AddDialog(Dialog object) {
        DialogCache.add(object);

        DialogOpen = true;
        DialogTime = System.nanoTime();
    }

    public void AddDialog(String Speaker, String Conversation) {
        Dialog temp = new Dialog(Speaker, Conversation) {
            public void exit() {}
        };

        DialogCache.add(temp);
        DialogOpen = true;
        DialogTime = System.nanoTime();
    }


    public void UpdateDialogBox() {
        if(DialogCache.size() > 0) {
            dialogBox.getTitleLabel().setText(DialogCache.get(0).getSpeaker());
            dialogBoxText.setText(DialogCache.get(0).getText() + "\n \n");
        }
        Guistage.act();
    }

    public void MenuDraw(float Delta) {
        Guistage.act(Delta);
        Guistage.setDebugAll(gsm.Debug);

        UpdateDialogBox();
        table.setVisible(DialogOpen);

        Guistage.draw();
    }

    public void AddParticleEffect(String ParticleName, Vector2 pos) {
        //SETUP THE PARTICLES
        ParticleEffect temp = new ParticleEffect();
        temp.load(Gdx.files.internal("Tkparticles/" + ParticleName + ".p"),Gdx.files.internal("Tkparticles"));
        temp.setPosition(pos.x, pos.y);
        temp.start();
        ParticleEffects.add(temp);
    }

    public void AddParticleEffect(String ParticleName, float x, float y) {
        //SETUP THE PARTICLES
        ParticleEffect temp = new ParticleEffect();
        temp.load(Gdx.files.internal("Tkparticles/" + ParticleName + ".p"),Gdx.files.internal("Tkparticles"));
        temp.setPosition(x, y);
        temp.start();
        ParticleEffects.add(temp);
    }

    public void UpdateParticles() {
        for(int i = 0; i < ParticleEffects.size(); i++) {
            if(ParticleEffects.get(i).isComplete()) {
                ParticleEffects.get(i).dispose();
                ParticleEffects.remove(i);
            }
        }
    }

    public void DrawParticleEffects(SpriteBatch batch) {
        for(int i = 0; i < ParticleEffects.size(); i++) {
            ParticleEffects.get(i).draw(batch, Gdx.graphics.getDeltaTime());
        }
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
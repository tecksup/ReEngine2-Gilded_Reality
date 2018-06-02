package com.thecubecast.ReEngine.GameStates.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.GameStates.GameState;
import com.thecubecast.ReEngine.GameStates.blankTestState;
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;
import com.thecubecast.ReEngine.Graphics.RePipeTextureRegionDrawable;
import com.thecubecast.ReEngine.Graphics.RePipeline;
import com.thecubecast.ReEngine.Graphics.Scene2D.Dialog;
import com.thecubecast.ReEngine.Graphics.Scene2D.TkLabel;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;
import com.thecubecast.ReEngine.worldObjects.*;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledGraph;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledNode;
import com.thecubecast.ReEngine.worldObjects.EntityPrefabs.Hank;
import com.thecubecast.ReEngine.worldObjects.EntityPrefabs.Male_Student;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.mainclass.MasterFBO;
import static com.thecubecast.ReEngine.worldObjects.WorldObject.polyoverlap;

public class CarScene extends GameState {

    Player player;
    private List<WorldObject> Entities = new ArrayList<>();

    OrthographicCamera camera;

    ScreenShakeCameraController shaker;

    RePipeline Repipe;

    OrthographicCamera Guicamera;
    SpriteBatch guiBatch;

    private Skin skin;
    private Stage Guistage;
    private Table table;

    private List<ParticleEffect> ParticleEffects = new ArrayList<>();

    Boolean DialogOpen = false;
    int DialogTics = 0;
    private List<Dialog> DialogCache = new ArrayList<>();
    TkLabel dialogBoxTitle;
    Image dialogBoxFace;
    TkLabel dialogBoxText;

    public CarScene(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        Repipe = new RePipeline();

        MenuInit();

        player = new Player(13*16,1*16, new Vector3(16, 16, 16));
        Entities.add(player);
        gsm.DiscordManager.setPresenceDetails("Gilded Reality - Heading to Camp");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, gsm.Width, gsm.Height);

        shaker = new ScreenShakeCameraController(camera);

        guiBatch = new SpriteBatch();
        Guicamera = new OrthographicCamera();
        Guicamera.setToOrtho(false, gsm.Width, gsm.Height);

        //JukeBox.load("/Music/bgmusic.wav", "LogoSound");
        //JukeBox.play("LogoSound");

        Hank hank = new Hank(21*16, 15*16) {
            @Override
            public void interact() {
                //this.FocusStrength = 2;
                Common.print("Hank got activated!");
                AddDialog(getName(), "Hey!", 30);
                AddDialog(getName(), "I COULD do custom stuff now...");
                Dialog temp = new Dialog(getName(), "Soon I will be aloud to walk around!") {
                    @Override
                    public void exit() {
                        //FocusStrength = 0.35f;
                    }
                };
                temp.setSpeakerImage(new Texture(Gdx.files.internal("Sprites/Gunter.png")));
                temp.setCooldown(60);
                AddDialog(temp);
            }
        };
        hank.init(gsm.Width, gsm.Height);
        hank.setVelocity(0, -1);

        Entities.add(hank);

        //camera.position.set((int) (player.getPosition().x),(int) (player.getPosition().y), 0);
    }

    public void update() {
        UpdateParticles();
        for (int i = 0; i < Entities.size(); i++) {
            Entities.get(i).update(Gdx.graphics.getDeltaTime(), null);

            if(Entities.get(i) instanceof HiddenArea) {
                if(Entities.get(i).ifColliding(player.getHitbox()))
                {
                    HiddenArea Entitemp = (HiddenArea) Entities.get(i);
                    Entitemp.reveal();
                    if (!Entitemp.isNeverDiscovered()) {
                        AddParticleEffect("sparkle", player.getPosition());
                        Entitemp.setNeverDiscovered(true);
                    }
                } else {
                    HiddenArea Entitemp = (HiddenArea) Entities.get(i);
                    Entitemp.hide();
                }
            }
        }

        handleInput();
        player.update(gsm.DeltaTime, null);

        cameraUpdate(player, camera);

    }

    private void cameraUpdate(WorldObject mainFocus, OrthographicCamera cam) {
        //Set inArea to true to override camera room Lock

        Vector2 FocalPoint = new Vector2(mainFocus.getPosition().x, mainFocus.getPosition().y);
        float totalFocusPoints = 1;

        for (int i = 0; i < Entities.size(); i++) {
            if (Entities.get(i).FocusStrength != 0) {
                if(mainFocus.getPosition().dst(Entities.get(i).getPosition()) <= 200) {
                    float tempX = Entities.get(i).getPosition().x;
                    float tempY = Entities.get(i).getPosition().y;

                    double dist = mainFocus.getPosition().dst(Entities.get(i).getPosition());

                    double influence = -((dist-200)/200)*1;

                    FocalPoint.x += (tempX * (Entities.get(i).FocusStrength*influence));
                    FocalPoint.y += (tempY * (Entities.get(i).FocusStrength*influence));
                    totalFocusPoints += Entities.get(i).FocusStrength*influence;
                }
            }
        }

        cam.position.set((int) (FocalPoint.x/totalFocusPoints),(int) (FocalPoint.y/totalFocusPoints), 0);

        cam.update();
    }

    public void draw(SpriteBatch g, int height, int width, float Time) {
        shaker.update(gsm.DeltaTime);
        g.setProjectionMatrix(shaker.getCombinedMatrix());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Repipe.begin(g);

        Rectangle camView = new Rectangle(camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2, camera.viewportWidth, camera.viewportHeight);
        Rectangle drawView = new Rectangle(camera.position.x - camera.viewportWidth/2 - camera.viewportWidth/4, camera.position.y - camera.viewportHeight/2  - camera.viewportHeight/4, camera.viewportWidth + camera.viewportWidth/4, camera.viewportHeight + camera.viewportHeight/4);

        for (int i = 0; i < Entities.size(); i++) {
            if(Entities.get(i).ifColliding(player.getIntereactBox())){
                if(Entities.get(i) instanceof NPC) {
                    NPC Entitemp = (NPC) Entities.get(i);
                    if(drawView.overlaps(new Rectangle(Entitemp.getPosition().x, Entitemp.getPosition().y, Entitemp.getHitbox().width, Entitemp.getHitbox().height))) {
                        Entitemp.drawHighlight(Repipe, Time);
                    }
                } else {
                    WorldObjectComp temp = new WorldObjectComp();
                    Entities.sort(temp);
                    if(drawView.overlaps(new Rectangle(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, Entities.get(i).getHitbox().width, Entities.get(i).getHitbox().height))) {
                        Entities.get(i).draw(Repipe, Time);
                    }
                }
            } else {
                WorldObjectComp temp = new WorldObjectComp();
                Entities.sort(temp);
                if(drawView.overlaps(new Rectangle(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, Entities.get(i).getHitbox().width, Entities.get(i).getHitbox().height))) {
                    Entities.get(i).draw(Repipe, Time);
                }
            }
        }

        Repipe.end();

        MasterFBO.bind();
        MasterFBO.begin();

        Gdx.gl.glClearColor(46, 77, 46, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        g.begin();

        g.draw(Repipe.getFboT(),camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2 + height, width, -height);

        g.setProjectionMatrix(shaker.getCombinedMatrix());
        DrawParticleEffects(g);

        for (int i = 0; i < Entities.size(); i++) {
            if(Entities.get(i) instanceof NPC) {
                NPC Entitemp = (NPC) Entities.get(i);
                if(drawView.overlaps(new Rectangle(Entitemp.getPosition().x, Entitemp.getPosition().y, Entitemp.getHitbox().width, Entitemp.getHitbox().height))) {
                    Entitemp.drawGui(g, Time);
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
            gsm.Cursor = 2;

            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);
            gsm.Render.GUIDrawText(g, Common.roundDown(pos.x)-5, Common.roundDown(pos.y)-5, "X: " + ((int)pos.x/16) + " Y: " + ((int)pos.y/16));
        } else {
            gsm.Cursor = 0;
        }

        g.end();

        guiBatch.setProjectionMatrix(Guicamera.combined);
        guiBatch.begin();
        gsm.Render.GUIDrawText(guiBatch, (int) 5, (int) 10, "player velocity: ( " + ((int)player.getVelocity().x) + " , " + ((int)player.getVelocity().y) + ")");
        MenuDraw(Gdx.graphics.getDeltaTime());
        //gsm.Render.GUIDrawText(g, 5, height-25, "X: " + ((int)pos.x/16) + " Y: " + ((int)pos.y/16));
        guiBatch.end();

        gsm.Render.debugRenderer.setProjectionMatrix(shaker.getCombinedMatrix());
        gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (gsm.Debug) {
            gsm.Render.debugRenderer.setColor(Color.GREEN);
            for (int i = 0; i < Entities.size(); i++) {
                gsm.Render.debugRenderer.polygon(Entities.get(i).getHitboxPoly().getVertices());
            }
            gsm.Render.debugRenderer.setColor(Color.ORANGE);
            gsm.Render.debugRenderer.polygon(player.getAttackBox().getVertices());
            gsm.Render.debugRenderer.setColor(Color.YELLOW);
            gsm.Render.debugRenderer.rect(player.getIntereactBox().x, player.getIntereactBox().y, player.getIntereactBox().width, player.getIntereactBox().height);

            gsm.Render.debugRenderer.setColor(Color.FIREBRICK);
            for (int i = 0; i < Entities.size(); i++) {
                if(Entities.get(i) instanceof Student) {
                    Student temp = (Student) Entities.get(i);
                    int nodeCount = temp.getPath().getCount();
                    for (int j = 0; j < nodeCount; j++) {
                        FlatTiledNode node = temp.getPath().nodes.get(j);
                        gsm.Render.debugRenderer.rect(node.x * 16 + 4, node.y * 16 + 4, 4, 4);
                    }
                }
            }

            gsm.Render.debugRenderer.setColor(Color.FOREST);
            for (int i = 0; i < Entities.size(); i++) {
                if(Entities.get(i) instanceof Student) {
                    Student temp = (Student) Entities.get(i);
                    gsm.Render.debugRenderer.rect(temp.getDestination().x+2, temp.getDestination().y+2, 12, 12);
                }
            }

        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);
            gsm.Render.debugRenderer.setColor(Color.WHITE);
            gsm.Render.debugRenderer.rect(((int)pos.x/16)*16, ((int)pos.y/16)*16, 16, 16);
        }

        gsm.Render.debugRenderer.end();

    }

    public void handleInput() {

        Player.Direction[] temp = new Player.Direction[4];
        boolean moving = false;
        Vector2 speedPercent = new Vector2(1, 1);

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f || Gdx.input.isKeyPressed(Input.Keys.D)) {
            temp[3] = Player.Direction.East;
            moving = true;
            if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f)
                speedPercent.x = gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X);
        } else if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) < -0.2f || Gdx.input.isKeyPressed(Input.Keys.A)) {
            temp[2] = Player.Direction.West;
            moving = true;
            if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > -0.2f)
                speedPercent.x = gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X);
        }

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyPressed(Input.Keys.S)) {
            temp[1] = Player.Direction.South;
            moving = true;
            if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > -0.2f)
                speedPercent.y = gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y);
        } else if (gsm.ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyPressed(Input.Keys.W)) {
            temp[0] = Player.Direction.North;
            moving = true;
            if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f)
                speedPercent.y = gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y);
        }

        if (gsm.ctm.isButtonJustDown(1, controlerManager.buttons.BUTTON_START)){
            Common.print("Player 2 joined the game!!");
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_START) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            Common.print("Escape!!");
            //gsm.ctm.newController("template");
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.T)){
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);
            player.setPosition((int)pos.x, (int)pos.y);
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
            } else if (temp[3] != null) { //EAST
                finalDirect = Player.Direction.NorthEast;
            } else {
                finalDirect = Player.Direction.North;
            }
        } else if (temp[1] != null) { //SOUTH
            if (temp[2] != null) { // WEST
                finalDirect = Player.Direction.SouthWest;
            } else if (temp[3] != null) { //EAST
                finalDirect = Player.Direction.SouthEast;
            } else {
                finalDirect = Player.Direction.South;
            }
        } else if (temp[2] != null) { //WEST
            finalDirect = Player.Direction.West;
        } else if (temp[3] != null) { //EAST
            finalDirect = Player.Direction.East;
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.R)){
            if (DialogOpen) {
                if(DialogTics > DialogCache.get(0).getCooldown()) {
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
                } else {
                    dialogBoxText.endScroll();
                }
            } else {
                for (int i = 0; i < Entities.size(); i++) {
                    if(Entities.get(i).ifColliding(player.getIntereactBox())){
                        if(Entities.get(i) instanceof NPC) {
                            NPC Entitemp = (NPC) Entities.get(i);
                            Entitemp.interact();
                        }
                    }
                }
            }
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_X) || Gdx.input.isKeyJustPressed(Input.Keys.C) ){ // ATTACK
            if(player.AttackTime < .1f) {

                AddParticleEffect("sparkle", player.getIntereactBox().x + player.getIntereactBox().width/2, player.getIntereactBox().y + player.getIntereactBox().height/2);
                for (int i = 0; i < Entities.size(); i++) {
                    if(polyoverlap(player.getAttackBox(), Entities.get(i).getHitbox())){
                        if(Entities.get(i) instanceof NPC) {
                            NPC Entitemp = (NPC) Entities.get(i);

                            float HitVelocity = 40;

                            Vector2 hitDirection = new Vector2(player.VecDirction().x*HitVelocity, player.VecDirction().y*HitVelocity);
                            Entitemp.damage(10, hitDirection);
                        }
                    }
                }

                player.AttackTime += 0.5f;
            } else {
                moving = false;
            }
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_L3)){ // THE HEALING BUTTON RIGHT NOW
            Common.print("Healed");
            for (int i = 0; i < Entities.size(); i++) {
                if(polyoverlap(player.getAttackBox(), Entities.get(i).getHitbox())){
                    if(Entities.get(i) instanceof NPC) {
                        NPC Entitemp = (NPC) Entities.get(i);
                        Entitemp.heal(10);
                        AddParticleEffect("HealthArea", Entitemp.getPosition().x + Entitemp.getSize().x/2, Entitemp.getPosition().y + Entitemp.getSize().y/2);
                    }
                }
            }
        }

        if (moving) {
            if (DialogOpen) {
                //Dont move
            } else
                //player.setPlayerDirection(finalDirect);
                player.MovePlayerVelocity(finalDirect,(int) (10), gsm.DeltaTime);
        }

    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
        skin.getFont("Mecha").getData().markupEnabled = true;
        skin.getFont("Pixel").getData().markupEnabled = true;
    }

    public void MenuInit() {

        setupSkin();

        Guistage = new Stage(new StretchViewport(gsm.Width, gsm.Height));
        Gdx.input.setInputProcessor(Guistage);

        table = new Table();
        table.setFillParent(true);
        Guistage.addActor(table);

        Table dialogBox = new Table(skin);
        dialogBox.setBackground("Table_dialog");
        Table dialogBoxTitleT = new Table();
        Table dialogBoxTextT = new Table();

        dialogBox.add(dialogBoxTitleT).left().expandX().padLeft(3).padTop(-2).row();
        dialogBox.add(dialogBoxTextT).expandX().left().padLeft(3);

        dialogBoxTitle = new TkLabel("", skin);
        dialogBoxTitle.setAlignment(Align.left);
        dialogBoxFace = new Image(new Texture(Gdx.files.internal("Sprites/face.png")));
        dialogBoxFace.setSize(20,20);
        dialogBoxText= new TkLabel("", skin);
        dialogBoxText.setAlignment(Align.left);
        dialogBoxText.setScrolling(true);

        dialogBoxTitleT.add(dialogBoxTitle).left().fillX();
        dialogBoxTextT.add(dialogBoxFace).expandX().left().padRight(5);
        dialogBoxTextT.add(dialogBoxText).expandX().center();

        table.add(dialogBox).bottom().fillX().expand();

    }

    public void AddDialog(Dialog object) {
        DialogCache.add(object);

        DialogOpen = true;
        UpdateDialogBox();
    }

    public void AddDialog(String Speaker, String Conversation) {
        Dialog temp = new Dialog(Speaker, Conversation) {
            public void exit() {}
        };

        DialogCache.add(temp);
        DialogOpen = true;
        UpdateDialogBox();
    }

    public void AddDialog(String Speaker, String Conversation, int Cooldown) {
        Dialog temp = new Dialog(Speaker, Conversation) {
            public void exit() {}
        };

        temp.setCooldown(Cooldown);

        DialogCache.add(temp);
        DialogOpen = true;
        UpdateDialogBox();
    }

    public void UpdateDialogBox() {
        DialogTics = 0;
        if(DialogCache.size() > 0) {
            dialogBoxTitle.setText(DialogCache.get(0).getSpeaker());
            dialogBoxText.setText(DialogCache.get(0).getText());
            dialogBoxFace.setDrawable(new TextureRegionDrawable(new TextureRegion(DialogCache.get(0).getSpeakerImage())));
        }
        Guistage.act();
    }

    public void MenuDraw(float Delta) {
        if(DialogTics < 1000)
            DialogTics++;
        Guistage.act(Delta);
        Guistage.setDebugAll(gsm.Debug);

        //UpdateDialogBox();
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

    public void Shutdown() {

    }

    public void dispose() {
        int total = Entities.size();
        for (int i = 0; i < total; i++) {
            Entities.get(0).dispose();
            Entities.remove(0);
        }
    }
}

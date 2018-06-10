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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.collision;
import com.thecubecast.ReEngine.Data.controlerManager;
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;
import com.thecubecast.ReEngine.Graphics.RePipeline;
import com.thecubecast.ReEngine.Graphics.Scene2D.Dialog;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledGraph;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledNode;
import com.thecubecast.ReEngine.worldObjects.*;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.OutlineShader;
import static com.thecubecast.ReEngine.Graphics.Draw.loadAnim;
import static com.thecubecast.ReEngine.Graphics.Draw.setOutlineShaderColor;
import static com.thecubecast.ReEngine.worldObjects.WorldObject.polyoverlap;

public class DialogState extends GameState {

    Player player;
    NPC hank;
    private List<collision> Collisions = new ArrayList<>();
    public static List<Area> Areas = new ArrayList<>();
    private List<WorldObject> Entities = new ArrayList<>();

    public class Area {
        public String Name;
        public Rectangle Rect;

        public Area(String Name, Rectangle Rect) {
            this.Name = Name;
            this.Rect = Rect;
        }
    }

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

    FlatTiledGraph MapGraph;

    public DialogState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        MenuInit();

        player = new Player(21*16,21*16, new Vector3(16, 16, 16));
        Entities.add(player);
        gsm.DiscordManager.setPresenceDetails("Gilded Reality - level 1");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;;

        //SETUP TILEDMAP
        tiledMap = new TmxMapLoader().load("Saves/BITWISE/School/map.tmx");
        tiledBits = new BitwiseTiles(tiledMap);
        if(tiledMap.getLayers().get("Rooms") != null) {
            for(int rooms = 0; rooms < tiledMap.getLayers().get("Rooms").getObjects().getCount(); rooms++) {
                if (tiledMap.getLayers().get("Rooms").getObjects().get(rooms) instanceof RectangleMapObject) {
                    RectangleMapObject temp = (RectangleMapObject) tiledMap.getLayers().get("Rooms").getObjects().get(rooms);
                    Areas.add(new Area(temp.getName(), temp.getRectangle()));
                } else {
                    MapObject temp = tiledMap.getLayers().get("Rooms").getObjects().get(rooms);
                }

            }
        }

        if(tiledMap.getLayers().get("Objects") != null) {
            for(int rooms = 0; rooms < tiledMap.getLayers().get("Objects").getObjects().getCount(); rooms++) {
                if (tiledMap.getLayers().get("Objects").getObjects().get(rooms) instanceof RectangleMapObject) {
                    RectangleMapObject tempObj = (RectangleMapObject) tiledMap.getLayers().get("Objects").getObjects().get(rooms);
                    if (tempObj.getName().equals("Desk")) {
                        WorldObject Desk = new WorldObject((int) tempObj.getRectangle().x, (int) tempObj.getRectangle().y, new Vector3(tempObj.getRectangle().width, tempObj.getRectangle().height, 0)) {
                            Texture DeskSprite = new Texture(Gdx.files.internal("Sprites/Tiles/Desk.png"));
                            Texture ChairSprite = new Texture(Gdx.files.internal("Sprites/Tiles/Chair.png"));

                            @Override
                            public void init(int Width, int Height) {

                            }

                            @Override
                            public void update(float delta, List<collision> Colls) {

                            }

                            @Override
                            public void draw(SpriteBatch batch, float Time) {
                                batch.draw(ChairSprite, getPosition().x+5, getPosition().y-15);
                                batch.draw(DeskSprite, getPosition().x, getPosition().y-16);
                            }

                            @Override
                            public void draw(RePipeline batch, float Time) {

                            }
                        };

                        Entities.add(Desk);
                        Collisions.add(new collision(new Rectangle(Desk.getHitbox().x, Desk.getHitbox().y-5, Desk.getHitbox().width, Desk.getHitbox().height), Desk.hashCode()));

                    } else if (tempObj.getName().equals("Lockers")) {
                        WorldObject Desk = new WorldObject((int) tempObj.getRectangle().x, (int) tempObj.getRectangle().y, new Vector3(tempObj.getRectangle().width, tempObj.getRectangle().height, 0)) {

                            @Override
                            public void init(int Width, int Height) {

                            }

                            @Override
                            public void update(float delta, List<collision> Colls) {

                            }

                            @Override
                            public void draw(SpriteBatch batch, float Time) {
                                batch.draw(tiledBits.getBitTiles().get(10)[1].getDiffuse(),getPosition().x ,getPosition().y);
                                batch.draw(tiledBits.getBitTiles().get(10)[4].getDiffuse(),getPosition().x ,getPosition().y+16);
                                batch.draw(tiledBits.getBitTiles().get(10)[1].getDiffuse(),getPosition().x ,getPosition().y+32);
                                batch.draw(tiledBits.getBitTiles().get(10)[5].getDiffuse(),getPosition().x ,getPosition().y+48);
                                batch.draw(tiledBits.getBitTiles().get(10)[5].getDiffuse(),getPosition().x ,getPosition().y+64);
                                batch.draw(tiledBits.getBitTiles().get(10)[5].getDiffuse(),getPosition().x ,getPosition().y+80);
                                batch.draw(tiledBits.getBitTiles().get(10)[5].getDiffuse(),getPosition().x ,getPosition().y+96);
                                batch.draw(tiledBits.getBitTiles().get(10)[5].getDiffuse(),getPosition().x ,getPosition().y+112);
                                batch.draw(tiledBits.getBitTiles().get(10)[4].getDiffuse(),getPosition().x ,getPosition().y+128);
                            }

                            @Override
                            public void draw(RePipeline batch, float Time) {

                            }
                        };

                        Entities.add(Desk);
                        Collisions.add(new collision(new Rectangle(Desk.getHitbox().x, Desk.getHitbox().y-5, Desk.getHitbox().width, Desk.getHitbox().height), Desk.hashCode()));


                    }
                }

            }
        }

        MapGraph = new FlatTiledGraph(tiledBits);
        MapGraph.init(tiledBits);



        for (int y = 0; y < tiledBits.getBitTileObject(1).realTile.size(); y++) {
            for(int x = 0; x < tiledBits.getBitTileObject(1).realTile.get(y).length; x++) {
                if (tiledBits.getBitTileObject(1).realTile.get(y)[x] == 1) {

                } else if ((tiledBits.getBitTileObject(1).realTile.get(y)[x] == 6)) { //Bush (stupid grass block)
                    Rectangle tempRect = new Rectangle(x*16, y*16, 16, 16);
                    Collisions.add(new collision(tempRect, tempRect.hashCode()));
                } else if ((tiledBits.getBitTileObject(1).realTile.get(y)[x] == 8)) { //Bush (stupid grass block)
                    Rectangle tempRect;
                    if(tiledBits.getBitTileObject(1).BitTiles.get(y)[x] == 17) {
                        tempRect = new Rectangle(x*16, y*16+8, 16, 8);
                    } else {
                        tempRect = new Rectangle(x*16, y*16, 16, 16);
                    }
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

        hank = new NPC("Hank", 21*16, 15*16, new Vector3(32, 32, 4), .1f, 100) {
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
            public void draw(RePipeline batch, float Time) {

            }

            @Override
            public void drawHighlight(SpriteBatch batch, float Time) {
                TextureRegion currentFrame = idle.getKeyFrame(Time, true);

                setOutlineShaderColor(Color.YELLOW, 0.8f);

                batch.setShader(OutlineShader);
                batch.draw(currentFrame, getPosition().x-8, getPosition().y-4);
                batch.setShader(null);
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

        Student Random = new Student("Student",18*16, 100*16, new Vector3(16, 16, 4), 1, 100, NPC.intractability.Talk, MapGraph) {
            @Override
            public void init(int Width, int Height) {
                super.init(Width, Height);
                super.setDestination(new Vector2(26*16,97*16));
            }

            @Override
            public void update(float delta, List<collision> Colls) {
                super.update(delta, Colls);
            }

            @Override
            public void draw(SpriteBatch batch, float Time) {

            }
        };
        Random.init(0,0);

        WorldObject H_closet = new HiddenArea(13*16, 12*16, new Vector3(6*16, 7*16, 0), WorldObject.type.Static, true) {

            @Override
            public void init(int Width, int Height) {
                setFadeSpeed(0.1f, 0.1f);
            }

            @Override
            public void update(float delta, List<collision> Colls) {
                if(isDiscovered()) {
                    fadeOut();
                } else {
                    fadeIn();
                }
            }

            @Override
            public void draw(SpriteBatch batch, float Time) {
                Color temp = batch.getColor();
                batch.setColor(temp.r, temp.g, temp.b, getOpacity());
                batch.draw(tiledBits.getBitTiles().get(7)[7].getDiffuse() ,getPosition().x ,getPosition().y);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+16 ,getPosition().y);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+32 ,getPosition().y);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+48 ,getPosition().y);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+64 ,getPosition().y);
                batch.draw(tiledBits.getBitTiles().get(7)[13].getDiffuse(),getPosition().x+80 ,getPosition().y);

                batch.draw(tiledBits.getBitTiles().get(7)[7].getDiffuse(),getPosition().x ,getPosition().y+16);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+16 ,getPosition().y+16);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+32 ,getPosition().y+16);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+48 ,getPosition().y+16);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+64 ,getPosition().y+16);
                batch.draw(tiledBits.getBitTiles().get(7)[13].getDiffuse(),getPosition().x+80 ,getPosition().y+16);

                batch.draw(tiledBits.getBitTiles().get(7)[7].getDiffuse(),getPosition().x ,getPosition().y+32);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+16 ,getPosition().y+32);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+32 ,getPosition().y+32);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+48 ,getPosition().y+32);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+64 ,getPosition().y+32);
                batch.draw(tiledBits.getBitTiles().get(7)[13].getDiffuse(),getPosition().x+80 ,getPosition().y+32);

                batch.draw(tiledBits.getBitTiles().get(7)[7].getDiffuse(),getPosition().x ,getPosition().y+48);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+16 ,getPosition().y+48);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+32 ,getPosition().y+48);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+48 ,getPosition().y+48);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+64 ,getPosition().y+48);
                batch.draw(tiledBits.getBitTiles().get(7)[13].getDiffuse(),getPosition().x+80 ,getPosition().y+48);

                batch.draw(tiledBits.getBitTiles().get(7)[7].getDiffuse(),getPosition().x ,getPosition().y+64);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+16 ,getPosition().y+64);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+32 ,getPosition().y+64);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+48 ,getPosition().y+64);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+64 ,getPosition().y+64);
                batch.draw(tiledBits.getBitTiles().get(7)[13].getDiffuse(),getPosition().x+80 ,getPosition().y+64);

                batch.draw(tiledBits.getBitTiles().get(7)[7].getDiffuse(),getPosition().x ,getPosition().y+80);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+16 ,getPosition().y+80);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+32 ,getPosition().y+80);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+48 ,getPosition().y+80);
                batch.draw(tiledBits.getBitTiles().get(7)[15].getDiffuse(),getPosition().x+64 ,getPosition().y+80);
                batch.draw(tiledBits.getBitTiles().get(7)[13].getDiffuse(),getPosition().x+80 ,getPosition().y+80);

                batch.draw(tiledBits.getBitTiles().get(7)[6].getDiffuse(),getPosition().x ,getPosition().y+96);
                batch.draw(tiledBits.getBitTiles().get(7)[14].getDiffuse(),getPosition().x+16 ,getPosition().y+96);
                batch.draw(tiledBits.getBitTiles().get(7)[14].getDiffuse(),getPosition().x+32 ,getPosition().y+96);
                batch.draw(tiledBits.getBitTiles().get(7)[14].getDiffuse(),getPosition().x+48 ,getPosition().y+96);
                batch.draw(tiledBits.getBitTiles().get(7)[14].getDiffuse(),getPosition().x+64 ,getPosition().y+96);
                batch.draw(tiledBits.getBitTiles().get(7)[14].getDiffuse(),getPosition().x+80 ,getPosition().y+96);
                batch.setColor(temp);
            }

            @Override
            public void draw(RePipeline batch, float Time) {

            }
        };

        H_closet.init(0,0);
        Rectangle H_closetbox = new Rectangle((int) hank.getHitbox().x, (int) hank.getHitbox().y,(int) hank.getHitbox().width,(int) hank.getHitbox().height);
        Collisions.add(new collision(H_closetbox, H_closet.hashCode()));

        Entities.add(Random);
        Entities.add(H_closet);
        Entities.add(hank);

        camera.position.set((int) (player.getPosition().x),(int) (player.getPosition().y), 0);
    }

    public void update() {
        UpdateParticles();
        for(int i = 0; i < Entities.size(); i++) {
            Entities.get(i).update(Gdx.graphics.getDeltaTime(), Collisions);

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

        for (int i = 0; i < Areas.size(); i++) {
            if (player.getHitbox().overlaps(Areas.get(i).Rect)) {
                //if(Areas.get(i).Name.equals("Hallway")) {
                //Common.print("In Hallway");
                //'}
            }
        }

        handleInput();
        player.update(gsm.DeltaTime, Collisions);

        cameraUpdate(player, camera);

    }

    private void cameraUpdate(WorldObject mainFocus, OrthographicCamera cam) {
        //Set inArea to true to override camera room Lock
        boolean inArea = true;
        for (int i = 0; i < Areas.size(); i++) {
            Rectangle mostOverlaped;
            if (mainFocus.getHitbox().overlaps(Areas.get(i).Rect)) { // Lock the camera into the AREA, still follow the player
                Vector3 camPos = cam.position;
                Rectangle camView = new Rectangle(cam.position.x - cam.viewportWidth/2, cam.position.y - cam.viewportHeight/2, cam.viewportWidth, cam.viewportHeight);

                Rectangle overlaper = new Rectangle(0,0,1,1);
                //This rectangle is the are between the room and camView
                //Compare the difference in height, then divide by two to center the screen over the room rectangle
                Intersector.intersectRectangles(Areas.get(i).Rect, camView, overlaper);

                float newPosX = cam.position.x, newPosY = cam.position.y;

                //Width
                if (Areas.get(i).Rect.width < camView.width) { //if the area does not fit all the width in the screen at once

                    float difference = Areas.get(i).Rect.width - camView.width;

                    float tempDeltaX = mainFocus.getPosition().x;
                    if (tempDeltaX < camView.x) { // If you try to go past the left border
                        newPosX = camView.x;
                    } else {
                        newPosX = tempDeltaX;
                    }

                    if (tempDeltaX > camView.x + camView.width) { // If you try to go past the right border
                        newPosX = camView.x + camView.width;
                    } else {
                        newPosX = tempDeltaX;
                    }

                    //newPosX = difference/2;

                } else { //Follow the player left and right
                    Rectangle fakeView = camView;
                    fakeView.setX(mainFocus.getPosition().x);
                    if (fakeView.x > Areas.get(i).Rect.x && fakeView.x < Areas.get(i).Rect.width) { // sets up the boundries of the camera
                        newPosX = mainFocus.getPosition().x;
                    }
                }

                cam.position.set((int) (newPosX),(int) (newPosY), 0);

                break;
            }
        }

        if (inArea) { //Follow the player if he is not in an AREA
            cam.position.set((int) (mainFocus.getPosition().x),(int) (mainFocus.getPosition().y), 0);
        }

        cam.update();
    }

    public void draw(SpriteBatch g, int height, int width, float Time) {
        shaker.update(gsm.DeltaTime);

        //camera.setToOrtho(false, width, height);
        g.setProjectionMatrix(shaker.getCombinedMatrix());
        g.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);


        Rectangle camView = new Rectangle(camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2, camera.viewportWidth, camera.viewportHeight);
        tiledBits.drawLayer(g, 16, Time,0, player.getPosition().y, camView);
        tiledBits.drawLayer(g, 16, Time,1, player.getPosition().y, camView);
        tiledBits.drawLayer(g, 16, Time,2, player.getPosition().y, camView);

        //tiledBits.drawLayer(g, 16, Time,0, player.getPosition().y, new Rectangle(camView.x + 20, camView.y + 20, camView.width-40, camView.height-40));
        //tiledBits.drawLayer(g, 16, Time,1, player.getPosition().y, new Rectangle(camView.x + 20, camView.y + 20, camView.width-40, camView.height-40));

        for(int i = 0; i < Entities.size(); i++) {
            if(Entities.get(i).ifColliding(player.getIntereactBox())){
                if(Entities.get(i) instanceof NPC) {
                    NPC Entitemp = (NPC) Entities.get(i);
                    Entitemp.draw(g, Time);
                    Entitemp.drawHighlight(g, Time);
                } else {
                    WorldObjectComp temp = new WorldObjectComp();
                    Entities.sort(temp);
                    Entities.get(i).draw(g, Time);
                }
            } else {
                WorldObjectComp temp = new WorldObjectComp();
                Entities.sort(temp);
                Entities.get(i).draw(g, Time);
            }
        }

        DrawParticleEffects(g);

        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) { //KeyHit
            gsm.Cursor = GameStateManager.CursorType.Question;

            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);
            gsm.Render.GUIDrawText(g, Common.roundDown(pos.x)-5, Common.roundDown(pos.y)-5, "X: " + ((int)pos.x/16) + " Y: " + ((int)pos.y/16));
        } else {
            gsm.Cursor = GameStateManager.CursorType.Normal;
        }

        g.end();

        guiBatch.setProjectionMatrix(Guicamera.combined);
        guiBatch.begin();
        MenuDraw(Gdx.graphics.getDeltaTime());
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

            if (false) //ONLY SET TO TRUE IF YOU NEED TO VIEW AI GRAPH
            {
                gsm.Render.debugRenderer.setColor(Color.RED);
                Collisions.forEach(number -> gsm.Render.debugRenderer.rect(number.getRect().x, number.getRect().y, (number.getRect().width), (number.getRect().height)));

                for (int y = 0; y < tiledBits.bitTileObjectLayers.get(0).realTile.size(); y++) {
                    for (int x = 0; x < tiledBits.bitTileObjectLayers.get(0).realTile.size(); x++) {
                        switch (MapGraph.getNode(x, y).type) {
                            case FlatTiledNode.GROUND:
                                gsm.Render.debugRenderer.setColor(Color.GREEN);
                                //gsm.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                                break;
                            case FlatTiledNode.COLLIDABLE:
                                gsm.Render.debugRenderer.setColor(Color.SALMON);
                                gsm.Render.debugRenderer.rect(x * 16 + 1, y * 16 + 1, 16-2, 16-2);
                                break;
                            default:
                                //gsm.Render.debugRenderer.setColor(Color.WHITE);
                                //gsm.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                                break;
                        }
                    }
                }
            }

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

            for (int i = 0; i < Areas.size(); i++) {
                gsm.Render.debugRenderer.setColor(Color.BLUE);
                gsm.Render.debugRenderer.rect(Areas.get(i).Rect.x+1, Areas.get(i).Rect.y+1, Areas.get(i).Rect.width-2, Areas.get(i).Rect.height-2);
            }

        }

        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) { //KeyHit
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

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f || Gdx.input.isKeyPressed(Keys.D)) {
            temp[3] = Player.Direction.East;
            moving = true;
            if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f)
                speedPercent.x = gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X);
        } else if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) < -0.2f || Gdx.input.isKeyPressed(Keys.A)) {
            temp[2] = Player.Direction.West;
            moving = true;
            if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > -0.2f)
                speedPercent.x = gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X);
        }

        if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyPressed(Keys.S)) {
            temp[1] = Player.Direction.South;
            moving = true;
            if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > -0.2f)
                speedPercent.y = gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y);
        } else if (gsm.ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyPressed(Keys.W)) {
            temp[0] = Player.Direction.North;
            moving = true;
            if (gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f)
                speedPercent.y = gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y);
        }

        if (gsm.ctm.isButtonJustDown(1, controlerManager.buttons.BUTTON_START)){
            Common.print("Player 2 joined the game!!");
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_START) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
            Common.print("Escape!!");
            //gsm.ctm.newController("template");
        }

        if (Gdx.input.isKeyJustPressed(Keys.NUM_8)){
            Common.print("Reloaded Bitwise Images!!");
            tiledBits.reLoadImages();
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

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_X)){ // ATTACK
            if(player.AttackTime < .1f) {

                AddParticleEffect("sparkle", player.getIntereactBox().x + player.getIntereactBox().width/2, player.getIntereactBox().y + player.getIntereactBox().height/2);
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

                player.AttackTime += 0.5f;
            } else {
                moving = false;
            }
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_L3)){ // THE HEALING BUTTON RIGHT NOW
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

    @Override
    public void Shutdown() {

    }

}
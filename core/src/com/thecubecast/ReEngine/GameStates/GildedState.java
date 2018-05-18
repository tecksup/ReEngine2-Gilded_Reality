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
import com.thecubecast.ReEngine.Graphics.Scene2D.Dialog;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledGraph;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledNode;
import com.thecubecast.ReEngine.worldObjects.*;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.loadAnim;
import static com.thecubecast.ReEngine.worldObjects.WorldObject.polyoverlap;

public abstract class GildedState extends GameState {

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

    public OrthographicCamera camera;

    public ScreenShakeCameraController shaker;

    public OrthographicCamera Guicamera;
    private SpriteBatch guiBatch;

    private Skin skin;
    private Stage Guistage;
    private Table table;

    private List<ParticleEffect> ParticleEffects = new ArrayList<>();

    public Boolean DialogOpen = false;
    public float DialogTime = 0;
    private List<Dialog> DialogCache = new ArrayList<>();
    private Window dialogBox;
    private Label dialogBoxText;

    private TiledMap tiledMap;
    private BitwiseTiles tiledBits;

    private FlatTiledGraph MapGraph;

    public GildedState(GameStateManager gsm) {
        super(gsm);
        init();
    }

    public void init() {

        MenuInit();

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
    }

    public void update() {
        UpdateParticles();
        for(int i = 0; i < Entities.size(); i++) {
            Entities.get(i).update(Gdx.graphics.getDeltaTime(), Collisions);
        }

        handleInput();

    }

    public abstract void draw(SpriteBatch g, int height, int width, float Time);

    public abstract void handleInput();

    private void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
    }

    private void MenuInit() {

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

    private void UpdateParticles() {
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

    public List<collision> getCollisions() {
        return Collisions;
    }

    public static List<Area> getAreas() {
        return Areas;
    }

    public List<WorldObject> getEntities() {
        return Entities;
    }

    public SpriteBatch getGuiBatch() {
        return guiBatch;
    }

    public Stage getGuistage() {
        return Guistage;
    }

    public List<ParticleEffect> getParticleEffects() {
        return ParticleEffects;
    }

    public List<Dialog> getDialogCache() {
        return DialogCache;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public BitwiseTiles getTiledBits() {
        return tiledBits;
    }

    public FlatTiledGraph getMapGraph() {
        return MapGraph;
    }
}
// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.Data.TkMap.TkMap;
import com.thecubecast.ReEngine.Graphics.Scene2D.TkTextButton;
import com.thecubecast.ReEngine.Graphics.Scene2D.UIFSM;
import com.thecubecast.ReEngine.Graphics.Scene2D.UI_state;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;
import com.thecubecast.ReEngine.worldObjects.*;

import javax.swing.*;
import java.time.chrono.Era;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thecubecast.ReEngine.Data.Common.updategsmValues;
import static com.thecubecast.ReEngine.Graphics.Draw.FillColorShader;
import static com.thecubecast.ReEngine.Graphics.Draw.setFillColorShaderColor;
import static com.thecubecast.ReEngine.Graphics.Draw.setOutlineShaderColor;

public class EditorState extends GameState {

    int TileIDSelected = 0;
    boolean Erasing = false;
    private enum selection {
        Ground, Forground, Object, None
    }
    private selection selected = selection.None;

    private boolean SelectionDragging = false;
    private Vector2[] SelectedArea;
    private List<WorldObject> SelectedObjects = new ArrayList<>();


    WorldObject CameraFocusPointEdit = new WorldObject() {
        @Override
        public void init(int Width, int Height) {

        }

        @Override
        public void update(float delta, List<Cube> Colls) {

        }

        @Override
        public void draw(SpriteBatch batch, float Time) {

        }
    };

    //GUI
    UIFSM UI;

    public static HashMap<Integer, Craftable> CraftingRecipes;

    //Camera
    OrthographicCamera GuiCam;
    public static OrthographicCamera camera;
    ScreenShakeCameraController shaker;

    Vector3 StartDrag;
    boolean Dragging = false;
    WorldObject MainCameraFocusPoint;

    //Particles
    public static ParticleHandler Particles;

    //GameObjects
    public static List<Cube> Collisions = new ArrayList<>();
    public List<Area> Areas = new ArrayList<>();
    public static List<WorldObject> Entities = new ArrayList<>();

    //Map Variables
    String SaveNameText = "Level";
    TkMap tempshitgiggle;

    Skin skin;
    Stage UIStage;
    Table InfoTable;
    Table EditorTable;

    public EditorState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        tempshitgiggle = new TkMap("Saves/CubeEditor/" + SaveNameText + ".cube");
        ArrayList<WorldObject> tempobjsshit = tempshitgiggle.getObjects();
        for (int i = 0; i < tempobjsshit.size(); i++) {
            Entities.add(tempobjsshit.get(i));
            if (tempobjsshit.get(i).isCollidable()) {
                Vector3 tempVec = tempobjsshit.get(i).getPosition();
                Vector3 tempVecOffset = tempobjsshit.get(i).getHitboxOffset();
                Vector3 tempVecSize = tempobjsshit.get(i).getSize();
                Cube tempCube = new Cube((int)tempVec.x + (int)tempVecOffset.x, (int)tempVec.y + (int)tempVecOffset.y, (int)tempVec.z + (int)tempVecOffset.z, (int)tempVecSize.x, (int)tempVecSize.y, (int)tempVecSize.z );
                Entities.get(i).CollisionHashID = Collisions.size();
                Collisions.add(tempCube);
                //System.out.println(tempshitgiggle.getObjects().get(i).getPosition());
            }
        }

        for (int x = 0; x < tempshitgiggle.getWidth(); x++) {
            for (int y = 0; y < tempshitgiggle.getHeight(); y++) {
                if (tempshitgiggle.getCollision()[x][y]) {
                    Collisions.add(new Cube(x * 16, y * 16, 0, 16, 16, 16 ));
                }
            }
        }

        CraftingRecipes = new HashMap<>();

        JsonParser tempparser = new JsonParser();
        JsonArray tempJson = tempparser.parse(Gdx.files.internal("Crafting.dat").readString()).getAsJsonArray();
        for (int i = 0; i < tempJson.size(); i++) {
            int Item = tempJson.get(i).getAsJsonObject().get("Crafting").getAsInt();
            int Quantity = tempJson.get(i).getAsJsonObject().get("Quantity").getAsInt();
            JsonArray Requirements = tempJson.get(i).getAsJsonObject().get("Required").getAsJsonArray();
            CraftingRecipes.put(Item, new Craftable(Item, Requirements, Quantity));
        }

        MainCameraFocusPoint = CameraFocusPointEdit;

        gsm.DiscordManager.setPresenceDetails("Level Editor");
        gsm.DiscordManager.setPresenceState("Working so very well...");
        gsm.DiscordManager.getPresence().largeImageText = "";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;

        //Camera setup
        camera = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        camera.setToOrtho(false, gsm.WorldWidth, gsm.WorldHeight);
        GuiCam.setToOrtho(false, gsm.UIWidth, gsm.UIHeight);
        shaker = new ScreenShakeCameraController(camera);

        UI = new UIFSM(GuiCam, gsm);
        UI.inGame = true;
        UI.setState(UI_state.InGameHome);
        UI.setVisable(false);

        //Particles
        Particles = new ParticleHandler();

        UISetup();

    }

    public void update() {

        Particles.Update();

        for (int i = 0; i < Entities.size(); i++) {
            Entities.get(i).update(Gdx.graphics.getDeltaTime(), Collisions);
        }

        cameraUpdate(MainCameraFocusPoint, camera, Entities,0,0, tempshitgiggle.getWidth()*tempshitgiggle.getTileSize(), tempshitgiggle.getHeight()*tempshitgiggle.getTileSize());

        handleInput();

        UIStage.act(Gdx.graphics.getDeltaTime());

        UI.setPlayer(null);
    }

    public void draw(SpriteBatch g, int height, int width, float Time) {

        shaker.update(gsm.DeltaTime);
        g.setProjectionMatrix(shaker.getCombinedMatrix());

        Rectangle drawView = new Rectangle(camera.position.x - camera.viewportWidth/2 - camera.viewportWidth/4, camera.position.y - camera.viewportHeight/2  - camera.viewportHeight/4, camera.viewportWidth + camera.viewportWidth/4, camera.viewportHeight + camera.viewportHeight/4);

        g.setShader(null);
        g.begin();

        //MapRenderer.renderLayer(g, Map, "Ground");
        //MapRenderer.renderLayer(g, Map, "Foreground");
        tempshitgiggle.Draw(camera, g);

        if (gsm.Debug) {
            //MapRenderer.renderLayer(g, Map, "Collision");
            tempshitgiggle.DrawCollision(camera, g);
        }

        //Block of code renders all the entities
        WorldObjectComp entitySort = new WorldObjectComp();
        WorldObjectCompDepth entitySortz = new WorldObjectCompDepth();
        Entities.sort(entitySort);
        Entities.sort(entitySortz);
        for (int i = 0; i < Entities.size(); i++) {
            if(drawView.overlaps(new Rectangle(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, Entities.get(i).getSize().x, Entities.get(i).getSize().y))) {
                Entities.get(i).draw(g, Time);
            }
        }

        //Renders my favorite little debug stuff
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
            gsm.Cursor = GameStateManager.CursorType.Question;

            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);
            gsm.Render.GUIDrawText(g, Common.roundDown(pos.x)-5, Common.roundDown(pos.y)-5, "X: " + ((int)pos.x/16) + " Y: " + ((int)pos.y/16));
        } else {
            gsm.Cursor = GameStateManager.CursorType.Normal;
        }

        //Particles
        Particles.Draw(g);

        //Renders the GUI for entities
        for (int i = 0; i < Entities.size(); i++) {
            if(Entities.get(i) instanceof NPC) {
                NPC Entitemp = (NPC) Entities.get(i);
                if(drawView.overlaps(new Rectangle(Entitemp.getPosition().x, Entitemp.getPosition().y, Entitemp.getSize().x, Entitemp.getSize().y))) {
                    ((NPC) Entities.get(i)).drawGui(g, Time);
                }
            }
        }

        Vector3 pos312 = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
        camera.unproject(pos312);
        if (selected.equals(selection.Ground) && !Erasing) {
            g.draw(tempshitgiggle.Tileset.getTiles()[TileIDSelected], ((int)pos312.x/16)*16, ((int)pos312.y/16)*16);
        } else if (selected.equals(selection.Forground) && !Erasing) {
            g.draw(tempshitgiggle.Tileset.getTiles()[TileIDSelected], ((int)pos312.x/16)*16, ((int)pos312.y/16)*16);
        }

        g.end();

        //DEBUG CODE
        gsm.Render.debugRenderer.setProjectionMatrix(camera.combined);
        gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (gsm.Debug) {

            for (int i = 0; i < Collisions.size(); i++) {

                //The bottom
                gsm.Render.debugRenderer.setColor(Color.YELLOW);
                gsm.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y + Collisions.get(i).getPrism().min.z/2, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());

                //The top of the Cube
                gsm.Render.debugRenderer.setColor(Color.RED);
                gsm.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y + Collisions.get(i).getPrism().getDepth()/2 + Collisions.get(i).getPrism().min.z/2, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());

                gsm.Render.debugRenderer.setColor(Color.ORANGE);
            }

            for (int i = 0; i < Areas.size(); i++) {
                gsm.Render.debugRenderer.setColor(Color.BLUE);
                gsm.Render.debugRenderer.rect(Areas.get(i).Rect.x+1, Areas.get(i).Rect.y+1, Areas.get(i).Rect.width-2, Areas.get(i).Rect.height-2);
            }

        }

        for (int i = 0; i < Entities.size(); i++) {
            //gsm.Render.debugRenderer.box(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y, Entities.get(i).getHitbox().min.z, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight(), Entities.get(i).getHitbox().getDepth());

            if (gsm.Debug || Entities.get(i).isDebugView()) {
                //The bottom
                gsm.Render.debugRenderer.setColor(Color.GREEN);
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().min.z/2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

                //The top of the Cube
                gsm.Render.debugRenderer.setColor(Color.BLUE);
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().getDepth()/2 + Entities.get(i).getHitbox().min.z/2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

            }

        }

        if (Erasing) {
            gsm.Render.debugRenderer.setColor(Color.WHITE);
            gsm.Render.debugRenderer.rect(((int)pos312.x/16)*16+1, ((int)pos312.y/16)*16+1, 15, 15);
        }

        if (SelectedArea != null) {
            Vector3 PosStart = new Vector3(SelectedArea[0].x, SelectedArea[0].y, 0);
            camera.unproject(PosStart);
            gsm.Render.debugRenderer.setColor(Color.ORANGE);
            gsm.Render.debugRenderer.rect(PosStart.x, PosStart.y, -Common.roundUp(SelectedArea[0].x - SelectedArea[1].x) / gsm.Scale, Common.roundUp(SelectedArea[0].y - SelectedArea[1].y) / gsm.Scale);
        }

        gsm.Render.debugRenderer.end();

    }

    public void drawUI(SpriteBatch g, int height, int width, float Time) {
        //Draws things on the screen, and not the world positions
        g.setProjectionMatrix(GuiCam.combined);
        UI.Draw(g);
        if (UI.CursorItem != null) {
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            GuiCam.unproject(pos);
            if (!UI.CursorItem.isStructure()) {
                g.begin();
                g.draw(new Texture(Gdx.files.internal(UI.CursorItem.getTexLocation())), pos.x/2, pos.y/2, 16, 16);
            } else {
                g.flush();
                g.setShader(FillColorShader);
                setFillColorShaderColor(Color.GREEN, 0.6f);
                g.begin();
                g.draw(new Texture(Gdx.files.internal(UI.CursorItem.getTexLocation())), pos.x/2, pos.y/2);
                g.setShader(null);
            }
            g.end();
        }

        UIStage.getViewport().update(gsm.UIWidth, gsm.UIHeight, true);
        UIStage.draw();
        UIStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    private void handleInput() {

        if (Gdx.input.isTouched()) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);

            if (selected.equals(selection.Ground)) {
                if (!Erasing) {
                    tempshitgiggle.setGroundCell(((int) pos.x / 16), ((int) pos.y / 16), TileIDSelected);
                } else {
                    tempshitgiggle.setGroundCell(((int) pos.x / 16), ((int) pos.y / 16), -1);
                }
            } else if (selected.equals(selection.Forground)) {
                if (!Erasing) {
                    tempshitgiggle.setForegroundCell(((int) pos.x / 16), ((int) pos.y / 16), TileIDSelected);
                } else {
                    tempshitgiggle.setForegroundCell(((int) pos.x / 16), ((int) pos.y / 16), -1);
                }
            } else if (selected.equals(selection.None)) {

            }

            if (SelectionDragging) {
                if (SelectedArea == null) {
                    SelectedArea = new Vector2[] {new Vector2(Gdx.input.getX() ,Gdx.input.getY()), new Vector2(0,0)};
                }
                SelectedArea[1].set(Gdx.input.getX() ,Gdx.input.getY());
            } else {
                SelectedArea = null;
            }

        }

        if (Gdx.input.isTouched()) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);

            if (selected.equals(selection.Object)) {
                //if (ObjectSelected != null) {
                //    WorldObject temp = ObjectSelected;
                //    Entities.add(temp);
                //}
            }
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);
            if (Dragging == false) {
                StartDrag = pos;
                Dragging = true;
            }

            CameraFocusPointEdit.setPosition(CameraFocusPointEdit.getPosition().x + Common.roundUp(StartDrag.x - pos.x), CameraFocusPointEdit.getPosition().y + Common.roundUp(StartDrag.y - pos.y), CameraFocusPointEdit.getPosition().z);
        } else {
            Dragging = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            selected = selection.Ground;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            selected = selection.Forground;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            selected = selection.Object;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            selected = selection.None;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) { //KeyHit
            Erasing = !Erasing;
        }


        Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
        camera.unproject(pos);
        updategsmValues(gsm, pos);

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_START) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            if (UI.getState().equals(UI_state.Inventory) && UI.Visible) {
                UI.setVisable(!UI.Visible);
                Gdx.input.setInputProcessor(UI.stage);
            } else if (!UI.Visible) {
                UI.setState(UI_state.InGameHome);
                Gdx.input.setInputProcessor(UI.stage);
            } else {
                UI.setVisable(!UI.Visible);
                Gdx.input.setInputProcessor(UIStage);
            }
            //gsm.ctm.newController("template");
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.T)){
            Vector3 mousepos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(mousepos);
            CameraFocusPointEdit.setPosition((int)mousepos.x, (int)mousepos.y, 0);
        }

    }

    public void reSize(SpriteBatch g, int H, int W) {

        System.out.println("Resized");

        camera = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        camera.setToOrtho(false, gsm.WorldWidth, gsm.WorldHeight);
        GuiCam.setToOrtho(false, gsm.UIWidth, gsm.UIHeight);
        shaker = new ScreenShakeCameraController(camera);

        UISetup();

        /*Vector3 campostemp = camera.position;
        camera.setToOrtho(false, gsm.WorldWidth, gsm.WorldHeight);
        camera.position.set(campostemp);
        GuiCam.setToOrtho(false, gsm.UIWidth, gsm.UIHeight);
        shaker.reSize(camera);

        UI.reSize();

        //shaker.reSize(camera); */
    }

    public void UISetup() {
        UIStage = new Stage(new FitViewport(gsm.UIWidth, gsm.UIHeight)) {
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                super.touchUp(screenX, screenY, pointer, button);
                if (SelectionDragging) {
                    Vector3 PosStart = new Vector3(SelectedArea[0].x, SelectedArea[0].y, 0);
                    Vector3 PosEND = new Vector3(SelectedArea[1].x, SelectedArea[1].y, 0);
                    camera.unproject(PosStart);
                    camera.unproject(PosEND);

                    BoundingBox tempSelection = new BoundingBox(PosStart, PosEND);

                    for (int i = 0; i < Entities.size(); i++) {
                        if (Entities.get(i).getHitbox().intersects(tempSelection)) {
                            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                                SelectedObjects.remove(Entities.get(i));
                                Entities.get(i).setDebugView(false);
                            } else{
                                SelectedObjects.add(Entities.get(i));
                                Entities.get(i).setDebugView(true);
                            }
                        }
                    }
                }

                SelectionDragging = false;
                SelectedArea = null;

                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && selected.equals(selection.Object)) {
                    SelectionDragging = true;
                }

                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                gsm.MouseX = screenX;
                gsm.MouseY = screenY;
                return false;
            }
        };
        Gdx.input.setInputProcessor(UIStage);
        UIStage.getViewport().setCamera(GuiCam);
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));

        InfoTable = new Table(skin);
        InfoTable.setFillParent(true);
        InfoTable.top().left();
        InfoTable.add(new Label(SaveNameText, skin)).pad(15).top().left();
        InfoTable.add(new TkTextButton("BUTTON", skin));

        EditorTable = new Table(skin);
        EditorTable.setFillParent(true);
        EditorTable.bottom().right();
        Table BoxStuff = new Table(skin);
        BoxStuff.setBackground("Window_green");

        TkTextButton Background = new TkTextButton("Background", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (selected.equals(selection.Ground)) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        Background.togglable = false;
        Background.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                selected = selection.Ground;
            }
        });
        TkTextButton Foreground = new TkTextButton("Foreground", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (selected.equals(selection.Forground)) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        Foreground.togglable = true;
        Foreground.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                selected = selection.Forground;
            }
        });
        TkTextButton Objects = new TkTextButton("Objects", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (selected.equals(selection.Object)) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        Objects.togglable = true;
        Objects.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                selected = selection.Object;
            }
        });
        TkTextButton Eraser = new TkTextButton("Eraser", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (Erasing) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        Eraser.togglable = true;
        Eraser.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                Erasing = !Erasing;
            }
        });
        TkTextButton None = new TkTextButton("None", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (selected.equals(selection.None)) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        None.togglable = true;
        None.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                selected = selection.None;
            }
        });
        Table ButtonHolder = new Table();
        ButtonHolder.add(Background);
        ButtonHolder.add(Foreground);
        ButtonHolder.add(Objects);
        ButtonHolder.add(Eraser);
        ButtonHolder.add(None);
        BoxStuff.add(ButtonHolder).row();
        EditorTable.add(BoxStuff);

        Table TilesList = new Table(skin);
        Table TilesFGList = new Table(skin);

        ScrollPane RecipeScroll = new ScrollPane(TilesList, skin) {
            private boolean FG = false;
            @Override
            public void act(float delta) {
                super.act(delta);
                //Check the type of tileset, and change from background or foreground tiles
                if (selected.equals(selection.Ground) && !FG) {
                    this.setActor(TilesList);
                    FG = true;
                } else if (selected.equals(selection.Forground) && FG) {
                    this.setActor(TilesFGList);
                    FG = false;
                }
            }
        };
        RecipeScroll.setupOverscroll(5, 50f, 100f);
        RecipeScroll.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                UIStage.setScrollFocus(RecipeScroll);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                UIStage.setScrollFocus(null);
            }
        });

        for (int i = 1; i < tempshitgiggle.Tileset.getTiles().length+1; i++) {

            int tempi = i-1;
            ImageButton tempimage = new ImageButton(new TextureRegionDrawable(tempshitgiggle.Tileset.getTiles()[i-1])) {
                int MYID = tempi;
                @Override
                public void act(float delta) {
                    super.act(delta);
                    if (TileIDSelected == MYID){
                        this.setDebug(true);
                        this.getImage().setColor(Color.ORANGE);
                    } else { this.setDebug(true); this.getImage().setColor(Color.WHITE);}
                }

            };
            tempimage.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    TileIDSelected = tempi;
                }
            });
            TilesList.add(tempimage);
            if (i % 24 == 0) {
                TilesList.row();
            }
        }

        for (int i = 1; i < tempshitgiggle.Tileset.getTiles().length+1; i++) {

            int tempi = i-1;
            ImageButton tempimage = new ImageButton(new TextureRegionDrawable(tempshitgiggle.Tileset.getTiles()[i-1])) {
                int MYID = tempi;
                @Override
                public void act(float delta) {
                    super.act(delta);
                    if (TileIDSelected == MYID){
                        this.setDebug(true);
                        this.getImage().setColor(Color.ORANGE);
                    } else { this.setDebug(true); this.getImage().setColor(Color.WHITE);}
                }
            };
            tempimage.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    TileIDSelected = tempi;
                }
            });
            TilesFGList.add(tempimage);
            if (i % 24 == 0) {
                TilesFGList.row();
            }
        }

        BoxStuff.add(RecipeScroll).height(100).padTop(5);

        UIStage.addActor(InfoTable);
        UIStage.addActor(EditorTable);
    }

    public void cameraUpdate(WorldObject mainFocus, OrthographicCamera cam, List<WorldObject> Entities, int MinX, int MinY, int MaxX, int MaxY) {

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

        if (FocalPoint.x - cam.viewportWidth/2 <= MinX) {
            FocalPoint.x = MinX + cam.viewportWidth/2;
        } else if (FocalPoint.x + cam.viewportWidth/2 >= MaxX) {
            FocalPoint.x = MaxX - cam.viewportWidth/2;
        }

        if (FocalPoint.y - cam.viewportHeight/2 <= MinY) {
            FocalPoint.y = MinY + cam.viewportHeight/2;
        } else if (FocalPoint.y + cam.viewportHeight/2 >= MaxY) {
            FocalPoint.y = MaxY - cam.viewportHeight/2;
        }

        cam.position.set((int) (FocalPoint.x/totalFocusPoints),(int) (FocalPoint.y/totalFocusPoints), 0);

        cam.update();
    }

    @Override
    public void dispose() {
        Collisions.clear();
        Areas.clear();
        Entities.clear();
    }

    @Override
    public void Shutdown() {

    }


}
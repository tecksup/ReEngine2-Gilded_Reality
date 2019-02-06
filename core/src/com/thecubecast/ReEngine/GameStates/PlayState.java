// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.Data.TkMap.TkMap;
import com.thecubecast.ReEngine.Graphics.Scene2D.UIFSM;
import com.thecubecast.ReEngine.Graphics.Scene2D.UI_state;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;
import com.thecubecast.ReEngine.worldObjects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thecubecast.ReEngine.Data.Common.updategsmValues;
import static com.thecubecast.ReEngine.Graphics.Draw.FillColorShader;
import static com.thecubecast.ReEngine.Graphics.Draw.setFillColorShaderColor;

public class PlayState extends DialogStateExtention {

    //GUI
    UIFSM UI;

    public static HashMap<Integer, Craftable> CraftingRecipes;

    //Camera
    OrthographicCamera GuiCam;
    public static OrthographicCamera camera;
    ScreenShakeCameraController shaker;
    WorldObject MainCameraFocusPoint;

    //Particles
    public static ParticleHandler Particles;

    //GameObjects
    public static Player player;
    public static List<Cube> Collisions = new ArrayList<>();
    public List<Area> Areas = new ArrayList<>();
    public static List<WorldObject> Entities = new ArrayList<>();

    //Map Variables
    TkMap tempshitgiggle;

    //AI
    //FlatTiledGraph MapGraph;

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        tempshitgiggle = new TkMap("Saves/CubeEditor/Level.cube");
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

        player = new Player(13*16,1*16, 0);

        MainCameraFocusPoint = player;

        Entities.add(player);

        //Setup Dialog Instance
        MenuInit(gsm.UIWidth, gsm.UIHeight);

        gsm.DiscordManager.setPresenceDetails("topdown Demo - Level 1");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
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

        //AddDialog("test", "{COLOR=GREEN}{WAVE}THIS IS FLAWLESSLY ADDED, HOW CONVENIENT");

        /*Collisions.add(new Cube(64,56,0,32,64,16));
        Collisions.add(new Cube(96,56,0,4,16,14));
        Collisions.add(new Cube(100,56,0,4,16,12));
        Collisions.add(new Cube(104,56,0,4,16,10));
        Collisions.add(new Cube(108,56,0,4,16, 8));
        Collisions.add(new Cube(112,56,0,4,16,6));
        Collisions.add(new Cube(116,56,0,4,16,4));
        Collisions.add(new Cube(120,56,0,4,16,2));
        */


    }

    public void update() {

        Particles.Update();

        for (int i = 0; i < Entities.size(); i++) {
            Entities.get(i).update(Gdx.graphics.getDeltaTime(), Collisions);

            if(Entities.get(i) instanceof WorldItem) {

                WorldItem Entitemp = (WorldItem) Entities.get(i);

                if (Entitemp.JustDroppedDelay <= 0) {

                    Vector3 tempCenter = new Vector3(player.getPosition().x + player.getSize().x / 2 + 4, player.getPosition().y + player.getSize().y / 2, player.getPosition().z + player.getSize().z / 2);
                    Vector3 CBS = new Vector3(48, 48, 32); //CollectionBoxSize

                    if (Entitemp.ifColliding(new Rectangle(tempCenter.x - CBS.x / 2, tempCenter.y - CBS.y / 2, CBS.x, CBS.y))) {
                        Entitemp.setPosition(new Vector3(tempCenter).sub(Entitemp.getPosition()).clamp(0, 2).add(Entitemp.getPosition()));
                    }

                    if (Entitemp.getHitbox().intersects(player.getHitbox())) {
                        //Add the item to inventory
                        player.AddToInventory(Entitemp.item);
                        Entities.remove(i);
                    }
                }
            }

            //This is for if the object is interactable
            else if(Entities.get(i) instanceof Interactable) {
                Interactable Entitemp = (Interactable) Entities.get(i);
                Entitemp.Trigger(player,shaker,this,MainCameraFocusPoint,Particles,Entities);
                Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
                camera.unproject(pos);
                if(Entitemp.getImageHitbox().contains(new Vector3(pos.x, pos.y, player.getPosition().z))) {
                    ((Interactable) Entities.get(i)).Highlight = true;
                    ((Interactable) Entities.get(i)).HighlightColor = Color.YELLOW;
                    if (Gdx.input.isTouched() && !UI.isVisible()) {
                        //Trigger the action, mine it, open it, trigger the event code
                        ((Interactable) Entities.get(i)).HighlightColor = Color.RED;
                        if (Entities.get(i) instanceof Storage) {
                            Storage temp = (Storage) Entities.get(i);
                            UI.StorageOpen = (Storage) Entities.get(i);
                            UI.setState(UI_state.InventoryAndStorage);
                            UI.setVisable(true);
                        } else if (Entities.get(i) instanceof Interactable){
                            Interactable temp = (Interactable) Entities.get(i);
                            temp.Activated();
                        }
                    }
                } else {
                    ((Interactable) Entities.get(i)).Highlight = false;
                }
            }

        }

        cameraUpdate(MainCameraFocusPoint, camera, Entities,0,0, tempshitgiggle.getWidth()*tempshitgiggle.getTileSize(), tempshitgiggle.getHeight()*tempshitgiggle.getTileSize());

        handleInput();

        UI.setPlayer(player);
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
            if(Entities.get(i).getHitbox().intersects(player.getIntereactBox())){
                if(Entities.get(i) instanceof NPC) {
                    NPC Entitemp = (NPC) Entities.get(i);
                    if(drawView.overlaps(new Rectangle(Entitemp.getPosition().x, Entitemp.getPosition().y, Entitemp.getSize().x, Entitemp.getSize().y))) {
                        //Entities.get(i).draw(g, Time);
                        Entitemp.drawHighlight(g, Time);
                    }
                } else {
                    if(drawView.overlaps(new Rectangle(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, Entities.get(i).getSize().x, Entities.get(i).getSize().y))) {
                        Entities.get(i).draw(g, Time);
                    }
                }
            } else {
                if(drawView.overlaps(new Rectangle(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, Entities.get(i).getSize().x, Entities.get(i).getSize().y))) {
                    Entities.get(i).draw(g, Time);
                }
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

            for (int i = 0; i < Entities.size(); i++) {
                //gsm.Render.debugRenderer.box(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y, Entities.get(i).getHitbox().min.z, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight(), Entities.get(i).getHitbox().getDepth());

                //The bottom
                gsm.Render.debugRenderer.setColor(Color.GREEN);
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().min.z/2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

                //The top of the Cube
                gsm.Render.debugRenderer.setColor(Color.BLUE);
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().getDepth()/2 + Entities.get(i).getHitbox().min.z/2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

            }

            //The bottom of the PLAYER
            gsm.Render.debugRenderer.setColor(Color.YELLOW);
            gsm.Render.debugRenderer.rect(player.getHitbox().min.x, player.getHitbox().min.y + player.getHitbox().min.z/2, player.getHitbox().getWidth(), player.getHitbox().getHeight());
            //The top of the Cube
            gsm.Render.debugRenderer.setColor(Color.RED);
            gsm.Render.debugRenderer.rect(player.getHitbox().min.x, player.getHitbox().min.y + player.getHitbox().getDepth()/2 + player.getHitbox().min.z/2, player.getHitbox().getWidth(), player.getHitbox().getHeight());

            gsm.Render.debugRenderer.setColor(Color.PURPLE);
            gsm.Render.debugRenderer.box(player.getIntereactBox().min.x, player.getIntereactBox().min.y, player.getIntereactBox().min.z, player.getIntereactBox().getWidth(), player.getIntereactBox().getHeight(), player.getIntereactBox().getDepth());

            for (int i = 0; i < Areas.size(); i++) {
                gsm.Render.debugRenderer.setColor(Color.BLUE);
                gsm.Render.debugRenderer.rect(Areas.get(i).Rect.x+1, Areas.get(i).Rect.y+1, Areas.get(i).Rect.width-2, Areas.get(i).Rect.height-2);
            }

            //Item Collection
            gsm.Render.debugRenderer.setColor(Color.LIGHT_GRAY);
            Vector3 tempCenter = new Vector3(player.getPosition().x + player.getSize().x/2 + 4, player.getPosition().y + player.getSize().y/2, player.getPosition().z + player.getSize().z/2);
            Vector3 CBS = new Vector3(48, 48, 32); //CollectionBoxSize
            gsm.Render.debugRenderer.rect(tempCenter.x - CBS.x/2 , tempCenter.y - CBS.y/2, CBS.x , CBS.y);
            
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);
            gsm.Render.debugRenderer.setColor(Color.WHITE);
            gsm.Render.debugRenderer.rect(((int)pos.x/16)*16+1, ((int)pos.y/16)*16+1, 15, 15);
        }

        gsm.Render.debugRenderer.end();

    }

    public void drawUI(SpriteBatch g, int height, int width, float Time) {
        //Draws things on the screen, and not the world positions
        g.setProjectionMatrix(GuiCam.combined);
        g.begin();
        //GUI must draw last
        MenuDraw(g, Gdx.graphics.getDeltaTime());
        g.end();
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
    }

    private void handleInput() {

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isTouched()) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(pos);
            WorldObject Crop = new WorldObject() {
                Texture Crop = new Texture(Gdx.files.internal("Sprites/Map/crops.png"));
                @Override
                public void init(int Width, int Height) {

                }

                @Override
                public void update(float delta, List<Cube> Colls) {

                }

                @Override
                public void draw(SpriteBatch batch, float Time) {
                    batch.draw(Crop, getPosition().x, getPosition().y + getPosition().z/2);
                }
            };

            Crop.setPosition(((int)pos.x/16)*16, ((int)pos.y/16)*16, 0);
            Crop.setSize(new Vector3(16,16,4));


            Entities.add(Crop);
        }

        Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
        camera.unproject(pos);
        updategsmValues(gsm, pos);

        Player.Direction[] temp = new Player.Direction[4];
        boolean moving = false;
        Vector3 speedPercent = new Vector3(1, 1, 0);

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
            if (UI.getState().equals(UI_state.Inventory) && UI.Visible) {
                UI.setVisable(!UI.Visible);
            } else if (!UI.Visible) {
                UI.setState(UI_state.InGameHome);
            } else {
                UI.setVisable(!UI.Visible);
            }
            //gsm.ctm.newController("template");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)){
            if (!UI.isVisible()) {
                UI.setState(UI_state.Inventory);
            } else if (UI.getState().equals(UI_state.Inventory) || UI.getState().equals(UI_state.InventoryAndStorage) || UI.getState().equals(UI_state.CraftingNew)) {
                UI.setVisable(!UI.isVisible());
            }
            //gsm.ctm.newController("template");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)){
            if (UI.getState().equals(UI_state.CraftingNew)) {
                UI.setVisable(!UI.isVisible());
            }
            else {
                UI.setState(UI_state.CraftingNew);
            }
            //gsm.ctm.newController("template");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)){
            shaker.addDamage(0.4f);
            AddDialog("Test", "{WAVE}{COLOR=GREEN}Hello {ENDWAVE},{WAIT} world!"
                    + "{COLOR=ORANGE}{SLOWER} Did{SHAKE} you{ENDSHAKE} know orange is my favorite color?");
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.T)){
            Vector3 mousepos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            camera.unproject(mousepos);
            player.setPosition((int)mousepos.x, (int)mousepos.y, 0);
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

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_Y) || Gdx.input.isKeyJustPressed(Input.Keys.R)){
            if (DialogOpen) {
                DialogNext();
            } else {
                for (int i = 0; i < Entities.size(); i++) {
                    if(Entities.get(i).getHitbox().intersects(player.getIntereactBox())){
                        if(Entities.get(i) instanceof NPC) {
                            NPC Entitemp = (NPC) Entities.get(i);
                            Entitemp.interact();
                        }

                        if(Entities.get(i) instanceof Trigger) {
                            Trigger Ent = (Trigger) Entities.get(i);
                            //Ent.Interact(player,shaker,entity,MainCameraFocusPoint,Particles,Entities);
                        }
                    }
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A)){
            player.setVelocityZ(player.getVelocity().z + 10);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)){
            gsm.setState(GameStateManager.State.MENU);
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_X) || Gdx.input.isKeyJustPressed(Input.Keys.C) ){ // ATTACK
            if(player.AttackTime < .1f) {

                //Vector3 addVeloc = new Vector3(player.getPosition().x - player.getAttackBox().min.x, player.getPosition().y - player.getAttackBox().min.y, 0);
                //player.setVelocity(new Vector3(player.getVelocity().x + (addVeloc.x*1.4f * -1), player.getVelocity().y + (addVeloc.y*1.4f * -1), player.getVelocity().z + (addVeloc.z*1.4f * -1)));

                Particles.AddParticleEffect("sparkle", player.getIntereactBox().getCenterX(), player.getIntereactBox().getCenterY());
                for (int i = 0; i < Entities.size(); i++) {
                    if(player.getAttackBox().intersects(Entities.get(i).getHitbox())){
                        if(Entities.get(i) instanceof NPC) {
                            NPC Entitemp = (NPC) Entities.get(i);

                            float HitVelocity = 40;

                            Vector3 hitDirection = new Vector3(player.VecDirction().x*HitVelocity, player.VecDirction().y*HitVelocity,0);
                            Entitemp.damage(10, hitDirection);
                            shaker.addDamage(0.35f);
                        }
                    }
                }

                player.AttackTime += 0.75f;
            } else {
                moving = false;
            }
        }

        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_L3) || Gdx.input.isKeyJustPressed(Input.Keys.X)){ // THE HEALING BUTTON RIGHT NOW
            Common.print("Healed");
            for (int i = 0; i < Entities.size(); i++) {
                if(player.getAttackBox().intersects(Entities.get(i).getHitbox())){
                    if(Entities.get(i) instanceof NPC) {
                        NPC Entitemp = (NPC) Entities.get(i);
                        Entitemp.heal(10);
                        Particles.AddParticleEffect("HealthArea", Entitemp.getPosition().x + Entitemp.getSize().x/2, Entitemp.getPosition().y + Entitemp.getSize().y/2);
                    }
                } //else
                //Particles.AddParticleEffect("Health", player.getIntereactBox().x + player.getIntereactBox().width/2, player.getIntereactBox().y + player.getIntereactBox().height/2);
            }
        }

        if(player.AttackTime > .1f) {
            moving = false;
        }

        if (moving) {
            if (DialogOpen) {
                //Dont move
            } else {
                player.MovePlayerVelocity(finalDirect,(int) (1));
            }
        }
    }

    public void reSize(SpriteBatch g, int H, int W) {

        /*Vector3 campostemp = camera.position;
        camera.setToOrtho(false, gsm.WorldWidth, gsm.WorldHeight);
        camera.position.set(campostemp);
        GuiCam.setToOrtho(false, gsm.UIWidth, gsm.UIHeight);
        shaker.reSize(camera);

        UI.reSize();

        //shaker.reSize(camera); */
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

    public float HighestZAtPos(int x, int y) {

        float highestZ = 0;

        for (int i = 0; i < Collisions.size(); i++) {
            if (Collisions.get(i).getPrism().max.z >= highestZ) {
                highestZ = Collisions.get(i).getPrism().max.z;
            }
        }

        return highestZ;
    }

}
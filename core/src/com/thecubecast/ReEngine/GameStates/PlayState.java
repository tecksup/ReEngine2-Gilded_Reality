// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.Data.OGMO.*;
import com.thecubecast.ReEngine.Graphics.Scene2D.UI_state;
import com.thecubecast.ReEngine.Graphics.Scene2D.UIFSM;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledGraph;
import com.thecubecast.ReEngine.worldObjects.*;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.Data.Common.updategsmValues;

public class PlayState extends DialogStateExtention {

    //GUI
    UIFSM UI;

    //Camera
    OrthographicCamera GuiCam;
    ScreenShakeCameraController shaker;
    WorldObject MainCameraFocusPoint;

    //Particles
    ParticleHandler Particles;

    //GameObjects
    public static Player player;
    private List<Cube> Collisions = new ArrayList<>();
    public List<Area> Areas = new ArrayList<>();
    private List<WorldObject> Entities = new ArrayList<>();

    //Map Variables
    OelMap Map;
    OelMapRenderer MapRenderer;

    //AI
    FlatTiledGraph MapGraph;

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        player = new Player(13*16,1*16, 0);

        MainCameraFocusPoint = player;

        Entities.add(player);

        Map = new OelMap("Saves/OGMO/test.oel");
        MapRenderer = new OelMapRenderer("Saves/OGMO/test.oep");

        for (int i = 0; i < Map.getLayers().size(); i++) {
            OelLayer layer = Map.getLayers().get(i);
            if(layer instanceof OelEntitiesLayer) {
                OelEntitiesLayer EntLayer = (OelEntitiesLayer) layer;
                EntLayer.loadEntities(Map, player, Entities, Areas);
            }
        }
        
        //Setup Dialog Instance
        MenuInit(gsm.UIWidth, gsm.UIHeight);
        
        gsm.DiscordManager.setPresenceDetails("topdown Demo - Level 1");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;;

        //Camera setup
        camera = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        camera.setToOrtho(false, gsm.WorldWidth, gsm.WorldHeight);
        GuiCam.setToOrtho(false, gsm.UIWidth, gsm.UIHeight);
        shaker = new ScreenShakeCameraController(camera);

        UI = new UIFSM(gsm.UIWidth, gsm.UIHeight, GuiCam, gsm);
        UI.inGame = true;
        UI.setState(UI_state.InGameHome);
        UI.setVisable(false);

        //Particles
        Particles = new ParticleHandler();

        for (int i = 0; i < Map.getLayers().size(); i++) {
            if (Map.getLayers().get(i).getName().equals("Collision")) {
                OelGridLayer temp = (OelGridLayer) Map.getLayers().get(i);
                for (int x = 0; x < Map.getWidth()/16; x++) {
                    for (int y = 0; y < Map.getHeight()/16; y++) {
                        if (temp.getCell(x, y) == 1) {
                            Collisions.add(new Cube(x * 16, y * 16, 0, 16, 16, 16 ));
                        }
                    }
                }
            }
        }

        MapGraph = new FlatTiledGraph(Map);
        MapGraph.init(Map);

        //AddDialog("test", "{COLOR=GREEN}{WAVE}THIS IS FLAWLESSLY ADDED, HOW CONVENIENT");

        Collisions.add(new Cube(64,56,0,32,64,16));
        Collisions.add(new Cube(96,56,0,4,16,14));
        Collisions.add(new Cube(100,56,0,4,16,12));
        Collisions.add(new Cube(104,56,0,4,16,10));
        Collisions.add(new Cube(108,56,0,4,16, 8));
        Collisions.add(new Cube(112,56,0,4,16,6));
        Collisions.add(new Cube(116,56,0,4,16,4));
        Collisions.add(new Cube(120,56,0,4,16,2));



    }

    public void update() {


        //Drops the item in hand when you press Q
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (UI.CursorItem != null) {
                WorldItem temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, UI.CursorItem);
                Entities.add(temp);
                UI.CursorItem = null;
            }
        }

        Particles.Update();

        //This is for triggers
        for (int i = 0; i < Entities.size(); i++) {
            Entities.get(i).update(Gdx.graphics.getDeltaTime(), Collisions);

            if (Entities.get(i) instanceof Trigger) {
                Trigger temp = (Trigger) Entities.get(i);
                temp.Trigger(player,shaker,this,MainCameraFocusPoint,Particles,Entities);
            }
        }

        //This finds out if you have picked up an item
        for (int i = 0; i < Entities.size(); i++) {
            if(Entities.get(i) instanceof WorldItem) {
                WorldItem Entitemp = (WorldItem) Entities.get(i);
                if(Entitemp.getHitbox().intersects(player.getHitbox())) {
                    //Add the item to inventory
                    for (int j = 0; j < player.Inventory.length; j++) {
                        if(player.Inventory[j] == null) {
                            player.Inventory[j] = Entitemp.item;
                            Entities.remove(i);
                            break;
                        }
                    }
                }
            }
        }

        cameraUpdate(MainCameraFocusPoint, camera, Entities,0,0, Map.getWidth(), Map.getHeight());
        
        handleInput();

        UI.setPlayer(player);
    }

    public void draw(SpriteBatch g, int height, int width, float Time) {

        shaker.update(gsm.DeltaTime);
        g.setProjectionMatrix(shaker.getCombinedMatrix());

        Rectangle drawView = new Rectangle(camera.position.x - camera.viewportWidth/2 - camera.viewportWidth/4, camera.position.y - camera.viewportHeight/2  - camera.viewportHeight/4, camera.viewportWidth + camera.viewportWidth/4, camera.viewportHeight + camera.viewportHeight/4);

        MapRenderer.setView(camera);

        g.begin();

        MapRenderer.renderLayer(g, Map, "Ground");
        MapRenderer.renderLayer(g, Map, "Foreground");

        if (gsm.Debug) {
            MapRenderer.renderLayer(g, Map, "Collision");

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
            //gsm.Render.debugRenderer.setColor(Color.GREEN);
            for (int i = 0; i < Entities.size(); i++) {
                //gsm.Render.debugRenderer.box(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y, Entities.get(i).getHitbox().min.z, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight(), Entities.get(i).getHitbox().getDepth());

                //The bottom
                gsm.Render.debugRenderer.setColor(Color.GREEN);
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().min.z/2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

                //The top of the Cube
                gsm.Render.debugRenderer.setColor(Color.BLUE);
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().getDepth()/2 + Entities.get(i).getHitbox().min.z/2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

            }

            for (int i = 0; i < Collisions.size(); i++) {

                //The bottom
                gsm.Render.debugRenderer.setColor(Color.YELLOW);
                gsm.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y + Collisions.get(i).getPrism().min.z/2, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());

                //The top of the Cube
                gsm.Render.debugRenderer.setColor(Color.RED);
                gsm.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y + Collisions.get(i).getPrism().getDepth()/2 + Collisions.get(i).getPrism().min.z/2, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());

                gsm.Render.debugRenderer.setColor(Color.ORANGE);
                //gsm.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());
                //gsm.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());
                //gsm.Render.debugRenderer.box(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y, Collisions.get(i).getPrism().min.z, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight(), Collisions.get(i).getPrism().getDepth());
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
        UI.Draw(g);
        if (UI.CursorItem != null) {
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
            GuiCam.unproject(pos);
            g.draw(new Texture(Gdx.files.internal(UI.CursorItem.getTexLocation())), pos.x, pos.y);

        }
        g.end();
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
            Common.print("Escape!!");
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
            if (UI.getState().equals(UI_state.Inventory)) {
                UI.setVisable(!UI.isVisible());
            }
            else {
                UI.setState(UI_state.Inventory);
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
        float posX = camera.position.x;
        float posY = camera.position.y;
        float posZ = camera.position.z;
        camera.setToOrtho(false, W, H);
        camera.position.set(posX, posY, posZ);

        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, W, H);
        //shaker.reSize(camera);
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
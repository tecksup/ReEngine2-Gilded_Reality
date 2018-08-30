package com.thecubecast.ReEngine.GameStates.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.Data.OGMO.*;
import com.thecubecast.ReEngine.Graphics.Scene2D.Dialog;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;
import com.thecubecast.ReEngine.worldObjects.*;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledGraph;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledNode;
import com.thecubecast.ReEngine.worldObjects.EntityPrefabs.Hank;

import java.util.ArrayList;
import java.util.List;

public enum Level_States implements State<LevelsFSM>, Scene {

    CarScene() {

        //SceneCam
        OrthographicCamera cam;

        Texture Car;
        boolean finishedScene = false;

        @Override
        public void enter(LevelsFSM entity) {
            entity.gsm.DiscordManager.setPresenceState("Story: Introduction");

            //Initialize the camera
            cam = new OrthographicCamera();
            cam.setToOrtho(false, entity.gsm.Width, entity.gsm.Height);

            Car = new Texture(Gdx.files.internal("Sprites/car.png"));

            //sets up the Dialog for the scene
            entity.AddDialog("Hank", "Hey try clicking this dialog box to move on! ", 30, new Texture(Gdx.files.internal("Sprites/Gunter.png")));
            entity.AddDialog("test2","Yeah, you could also press (R)", 30);
            Dialog temp = new Dialog("Hank", new Texture(Gdx.files.internal("Sprites/Gunter.png")), "But who would want to do that!") {
                @Override
                public void exit() {
                    finishedScene = true;
                }
            };
            temp.setCooldown(30);
            entity.AddDialog(temp);

        }

        @Override
        public void update(LevelsFSM entity) {
            if (finishedScene){
                entity.stateMachine.changeState(World);
            }
        }

        public void draw(LevelsFSM entity, SpriteBatch g, int height, int width, float Time) {

            cam.setToOrtho(false, width, height);
            g.setProjectionMatrix(cam.combined);
            g.begin();

            entity.MenuDraw(g, Gdx.graphics.getDeltaTime());
            g.draw(Car, width/2-Car.getWidth()/2,height/2-Car.getHeight()/2);

            g.end();
        }

        public void HandleInput(LevelsFSM entity) {
            if (entity.gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.R)){
                entity.DialogNext();
            }
        }

        @Override
        public void reSize() {

        }

        @Override
        public void exit(LevelsFSM entity) { //Dispose of everything

        }

        @Override
        public boolean onMessage(LevelsFSM entity, Telegram telegram) {
            return false;
        }
    },

    World() {

        //Camera
        OrthographicCamera Worldcam;
        OrthographicCamera GuiCam;
        ScreenShakeCameraController shaker;
        WorldObject MainCameraFocusPoint;

        //Particles
        ParticleHandler Particles;

        //GameObjects
        Player player;
        private List<Cube> Collisions = new ArrayList<>();
        public List<Area> Areas = new ArrayList<>();
        private List<WorldObject> Entities = new ArrayList<>();

        //Map Variables
        OelMap Map;
        OelMapRenderer MapRenderer;

        //AI
        FlatTiledGraph MapGraph;

        @Override
        public void enter(LevelsFSM entity) {

            player = new Player(13*16,1*16, 0, new Vector3(16, 16, 16));

            MainCameraFocusPoint = player;

            Entities.add(player);

            Map = new OelMap("Saves/OGMO/Camp.oel");
            MapRenderer = new OelMapRenderer("Saves/OGMO/test.oep");

            for (int i = 0; i < Map.getLayers().size(); i++) {
                OelLayer layer = Map.getLayers().get(i);
                if(layer instanceof OelEntitiesLayer) {
                    OelEntitiesLayer EntLayer = (OelEntitiesLayer) layer;
                    EntLayer.loadEntities(Map, player, Entities, Areas);
                }
            }

            //Discord Presence
            entity.gsm.DiscordManager.setPresenceState("Lakeside: Exploring");

            //Camera setup
            Worldcam = new OrthographicCamera();
            GuiCam = new OrthographicCamera();
            Worldcam.setToOrtho(false, entity.gsm.Width, entity.gsm.Height);
            GuiCam.setToOrtho(false, entity.gsm.Width, entity.gsm.Height);
            shaker = new ScreenShakeCameraController(Worldcam);

            //Particles
            Particles = new ParticleHandler();

            for (int i = 0; i < Map.getLayers().size(); i++) {
                if (Map.getLayers().get(i).getName().equals("Collision")) {
                    OelGridLayer temp = (OelGridLayer) Map.getLayers().get(i);
                    for (int x = 0; x < Map.getWidth()/16; x++) {
                        for (int y = 0; y < Map.getHeight()/16; y++) {
                            if (temp.getCell(x, y) == 1) {
                                Collisions.add(new Cube(x * 16, y * 16, 0, 16, 16, 0 ));
                            }
                        }
                    }
                }
            }

            MapGraph = new FlatTiledGraph(Map);
            MapGraph.init(Map);

            Hank tempStudent = new Hank( 120 * 16, 5 * 16,0) {
                @Override
                public void interact() {

                }
            };

            Entities.add(tempStudent);

        }

        @Override
        public void update(LevelsFSM entity) {

            Particles.Update();

            player.update(entity.gsm.DeltaTime, Collisions);

            for (int i = 0; i < Entities.size(); i++) {
                Entities.get(i).update(Gdx.graphics.getDeltaTime(), Collisions);

                if (Entities.get(i) instanceof Trigger) {
                    Trigger temp = (Trigger) Entities.get(i);
                    //temp.Trigger(player,shaker,entity,MainCameraFocusPoint,Particles,Entities);
                }
            }

            entity.cameraUpdate(MainCameraFocusPoint, Worldcam, Entities,0,0, Map.getWidth(), Map.getHeight());
        }

        public void draw(LevelsFSM entity, SpriteBatch g, int height, int width, float Time) {

            shaker.update(entity.gsm.DeltaTime);
            g.setProjectionMatrix(shaker.getCombinedMatrix());

            Rectangle drawView = new Rectangle(Worldcam.position.x - Worldcam.viewportWidth/2 - Worldcam.viewportWidth/4, Worldcam.position.y - Worldcam.viewportHeight/2  - Worldcam.viewportHeight/4, Worldcam.viewportWidth + Worldcam.viewportWidth/4, Worldcam.viewportHeight + Worldcam.viewportHeight/4);

            MapRenderer.setView(Worldcam);

            g.begin();

            MapRenderer.renderLayer(g, Map, "Ground");
            MapRenderer.renderLayer(g, Map, "Foreground");

            if (entity.gsm.Debug) {
                MapRenderer.renderLayer(g, Map, "Collision");

            }

            //Block of code renders all the entities
            WorldObjectComp entitySort = new WorldObjectComp();
            Entities.sort(entitySort);
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
                entity.gsm.Cursor = GameStateManager.CursorType.Question;

                Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
                Worldcam.unproject(pos);
                entity.gsm.Render.GUIDrawText(g, Common.roundDown(pos.x)-5, Common.roundDown(pos.y)-5, "X: " + ((int)pos.x/16) + " Y: " + ((int)pos.y/16));
            } else {
                entity.gsm.Cursor = GameStateManager.CursorType.Normal;
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


            g.setProjectionMatrix(GuiCam.combined);
            g.begin();
            //GUI must draw last
            entity.MenuDraw(g, Gdx.graphics.getDeltaTime());
            g.end();


            entity.gsm.Render.debugRenderer.setProjectionMatrix(Worldcam.combined);
            entity.gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);

            if (entity.gsm.Debug) {
                entity.gsm.Render.debugRenderer.setColor(Color.GREEN);
                for (int i = 0; i < Entities.size(); i++) {
                    entity.gsm.Render.debugRenderer.box(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y, Entities.get(i).getHitbox().min.z, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight(), Entities.get(i).getHitbox().getDepth());
                }
                entity.gsm.Render.debugRenderer.setColor(Color.ORANGE);
                entity.gsm.Render.debugRenderer.box(player.getHitbox().min.x, player.getHitbox().min.y, player.getHitbox().min.z, player.getHitbox().getWidth(), player.getHitbox().getHeight(), player.getHitbox().getDepth());
                entity.gsm.Render.debugRenderer.setColor(Color.YELLOW);
                entity.gsm.Render.debugRenderer.box(player.getIntereactBox().min.x, player.getIntereactBox().min.y, player.getIntereactBox().min.z, player.getIntereactBox().getWidth(), player.getIntereactBox().getHeight(), player.getIntereactBox().getDepth());

                entity.gsm.Render.debugRenderer.setColor(Color.FIREBRICK);
                for (int i = 0; i < Entities.size(); i++) {
                    if(Entities.get(i) instanceof Student) {
                        Student temp = (Student) Entities.get(i);
                        int nodeCount = temp.getPath().getCount();
                        for (int j = 0; j < nodeCount; j++) {
                            FlatTiledNode node = temp.getPath().nodes.get(j);
                            entity.gsm.Render.debugRenderer.rect(node.x * 16 + 4, node.y * 16 + 4, 4, 4);
                        }
                    }
                }

                entity.gsm.Render.debugRenderer.setColor(Color.FOREST);
                for (int i = 0; i < Entities.size(); i++) {
                    if(Entities.get(i) instanceof Student) {
                        Student temp = (Student) Entities.get(i);
                        entity.gsm.Render.debugRenderer.rect(temp.getDestination().x+2, temp.getDestination().y+2, 12, 12);
                    }
                }

                for (int i = 0; i < Areas.size(); i++) {
                    entity.gsm.Render.debugRenderer.setColor(Color.BLUE);
                    entity.gsm.Render.debugRenderer.rect(Areas.get(i).Rect.x+1, Areas.get(i).Rect.y+1, Areas.get(i).Rect.width-2, Areas.get(i).Rect.height-2);
                }

            }

            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
                Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
                Worldcam.unproject(pos);
                entity.gsm.Render.debugRenderer.setColor(Color.WHITE);
                entity.gsm.Render.debugRenderer.rect(((int)pos.x/16)*16+1, ((int)pos.y/16)*16+1, 15, 15);
            }

            entity.gsm.Render.debugRenderer.end();

        }

        public void HandleInput(LevelsFSM entity) {
            Player.Direction[] temp = new Player.Direction[4];
            boolean moving = false;
            Vector3 speedPercent = new Vector3(1, 1, 0);

            if (entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f || Gdx.input.isKeyPressed(Input.Keys.D)) {
                temp[3] = Player.Direction.East;
                moving = true;
                if (entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f)
                    speedPercent.x = entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X);
            } else if (entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) < -0.2f || Gdx.input.isKeyPressed(Input.Keys.A)) {
                temp[2] = Player.Direction.West;
                moving = true;
                if (entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > -0.2f)
                    speedPercent.x = entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X);
            }

            if (entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyPressed(Input.Keys.S)) {
                temp[1] = Player.Direction.South;
                moving = true;
                if (entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > -0.2f)
                    speedPercent.y = entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y);
            } else if (entity.gsm.ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyPressed(Input.Keys.W)) {
                temp[0] = Player.Direction.North;
                moving = true;
                if (entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_X) > 0.2f)
                    speedPercent.y = entity.gsm.ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y);
            }

            if (entity.gsm.ctm.isButtonJustDown(1, controlerManager.buttons.BUTTON_START)){
                Common.print("Player 2 joined the game!!");
            }

            if (entity.gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_START) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
                Common.print("Escape!!");
                //entity.gsm.ctm.newController("template");
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)){
                Common.print("Reloaded Map!!");
                Entities.clear();
                player = new Player(13*16,1*16, 0, new Vector3(16, 16, 16));

                MainCameraFocusPoint = player;

                Entities.add(player);

                Map = new OelMap("Saves/OGMO/Camp.oel");
                MapRenderer = new OelMapRenderer("Saves/OGMO/test.oep");

                for (int i = 0; i < Map.getLayers().size(); i++) {
                    OelLayer layer = Map.getLayers().get(i);
                    if(layer instanceof OelEntitiesLayer) {
                        OelEntitiesLayer EntLayer = (OelEntitiesLayer) layer;
                        EntLayer.loadEntities(Map, player, Entities, Areas);
                    }
                }

                Collisions.clear();

                for (int i = 0; i < Map.getLayers().size(); i++) {
                    if (Map.getLayers().get(i).getName().equals("Collision")) {
                        OelGridLayer temp2 = (OelGridLayer) Map.getLayers().get(i);
                        for (int x = 0; x < Map.getWidth()/16; x++) {
                            for (int y = 0; y < Map.getHeight()/16; y++) {
                                if (temp2.getCell(x, y) == 1) {
                                    Collisions.add(new Cube(x * 16, y * 16, 0, 16, 16, 0 ));
                                }
                            }
                        }
                    }
                }

                MapGraph = new FlatTiledGraph(Map);
                MapGraph.init(Map);

                //entity.gsm.ctm.newController("template");
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.B)){
                shaker.addDamage(0.4f);
                entity.AddDialog("Test", "{WAVE}{COLOR=GREEN}Hello {ENDWAVE},{WAIT} world!"
                        + "{COLOR=ORANGE}{SLOWER} Did{SHAKE} you{ENDSHAKE} know orange is my favorite color?");
            }

            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.T)){
                Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
                Worldcam.unproject(pos);
                player.setPosition((int)pos.x, (int)pos.y, 0);
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

            if (entity.gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.R)){
                if (entity.DialogOpen) {
                    entity.DialogNext();
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

            if (entity.gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_X) || Gdx.input.isKeyJustPressed(Input.Keys.C) ){ // ATTACK
                if(player.AttackTime < .1f) {

                    Vector3 addVeloc = new Vector3(player.getPosition().x - player.getAttackBox().min.x, player.getPosition().y - player.getAttackBox().min.y, 0);
                    player.setVelocity(new Vector3(player.getVelocity().x + (addVeloc.x*1.4f * -1), player.getVelocity().y + (addVeloc.y*1.4f * -1), player.getVelocity().z + (addVeloc.z*1.4f * -1)));

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

            if (entity.gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_L3) || Gdx.input.isKeyJustPressed(Input.Keys.X)){ // THE HEALING BUTTON RIGHT NOW
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
                if (entity.DialogOpen) {
                    //Dont move
                } else
                    //player.setPlayerDirection(finalDirect);
                    player.MovePlayerVelocity(finalDirect,(int) (10));
            }

        }

        @Override
        public void reSize() {

        }

        @Override
        public void exit(LevelsFSM entity) { //Dispose of everything

        }

        @Override
        public boolean onMessage(LevelsFSM entity, Telegram telegram) {
            return false;
        }
    }

}

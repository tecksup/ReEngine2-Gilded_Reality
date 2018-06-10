package com.thecubecast.ReEngine.GameStates.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.Graphics.BitwiseTiles;
import com.thecubecast.ReEngine.Graphics.Scene2D.Dialog;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;
import com.thecubecast.ReEngine.worldObjects.*;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledGraph;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledNode;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.worldObjects.WorldObject.polyoverlap;

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
            entity.AddDialog("Hank", "Hey try clicking this dialog box to move on! ", 90, new Texture(Gdx.files.internal("Sprites/Gunter.png")));
            entity.AddDialog("test2","Yeah, you could also press (R)", 90);
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
        ScreenShakeCameraController shaker;

        //Particles
        ParticleHandeler Particles;

        //GameObjects
        Player player;
        private List<collision> Collisions = new ArrayList<>();
        public List<Area> Areas = new ArrayList<>();
        private List<WorldObject> Entities = new ArrayList<>();

        //Map Variables
        TiledMap tiledMap;
        TiledMapRenderer tiledMapRenderer;
        BitwiseTiles tiledBits;

        //AI
        FlatTiledGraph MapGraph;

        @Override
        public void enter(LevelsFSM entity) {
            //Discord Presence
            entity.gsm.DiscordManager.setPresenceState("Story: Introduction");

            //Camera setup
            Worldcam = new OrthographicCamera();
            Worldcam.setToOrtho(false, entity.gsm.Width, entity.gsm.Height);
            shaker = new ScreenShakeCameraController(Worldcam);

            //Particles
            Particles = new ParticleHandeler();

            //SETUP TILEDMAP
            tiledMap = new TmxMapLoader().load("Saves/BITWISE/School/test.tmx");
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            tiledBits = new BitwiseTiles(tiledMap);

            //Test OGMO level
            Map ogmo = new Map();

            MapGraph = new FlatTiledGraph(tiledMap);
            MapGraph.init(tiledMap);

            player = new Player(13*16,1*16, new Vector3(16, 16, 16));
            Entities.add(player);
        }

        @Override
        public void update(LevelsFSM entity) {

            Particles.Update();

            for (int i = 0; i < Entities.size(); i++) {
                Entities.get(i).update(Gdx.graphics.getDeltaTime(), Collisions);
            }

            player.update(entity.gsm.DeltaTime, Collisions);

            entity.cameraUpdate(player, Worldcam, Entities);
        }

        public void draw(LevelsFSM entity, SpriteBatch g, int height, int width, float Time) {

            shaker.update(entity.gsm.DeltaTime);
            g.setProjectionMatrix(shaker.getCombinedMatrix());

            Rectangle drawView = new Rectangle(Worldcam.position.x - Worldcam.viewportWidth/2 - Worldcam.viewportWidth/4, Worldcam.position.y - Worldcam.viewportHeight/2  - Worldcam.viewportHeight/4, Worldcam.viewportWidth + Worldcam.viewportWidth/4, Worldcam.viewportHeight + Worldcam.viewportHeight/4);

            tiledMapRenderer.setView(Worldcam);
            tiledMapRenderer.render();

            g.begin();

            WorldObjectComp entitySort = new WorldObjectComp();
            Entities.sort(entitySort);
            for (int i = 0; i < Entities.size(); i++) {
                if(Entities.get(i).ifColliding(player.getIntereactBox())){
                    if(Entities.get(i) instanceof NPC) {
                        NPC Entitemp = (NPC) Entities.get(i);
                        if(drawView.overlaps(new Rectangle(Entitemp.getPosition().x, Entitemp.getPosition().y, Entitemp.getHitbox().width, Entitemp.getHitbox().height))) {
                            Entitemp.drawHighlight(g, Time);
                        }
                    } else {
                        if(drawView.overlaps(new Rectangle(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, Entities.get(i).getHitbox().width, Entities.get(i).getHitbox().height))) {
                            Entities.get(i).draw(g, Time);
                        }
                    }
                } else {
                    if(drawView.overlaps(new Rectangle(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, Entities.get(i).getHitbox().width, Entities.get(i).getHitbox().height))) {
                        Entities.get(i).draw(g, Time);
                    }
                }
            }

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

            //GUI must draw last
            entity.MenuDraw(g, Gdx.graphics.getDeltaTime());

            g.end();

            entity.gsm.Render.debugRenderer.setProjectionMatrix(Worldcam.combined);
            entity.gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);

            if (entity.gsm.Debug) {
                entity.gsm.Render.debugRenderer.setColor(Color.GREEN);
                for (int i = 0; i < Entities.size(); i++) {
                    entity.gsm.Render.debugRenderer.polygon(Entities.get(i).getHitboxPoly().getVertices());
                }
                entity.gsm.Render.debugRenderer.setColor(Color.ORANGE);
                entity.gsm.Render.debugRenderer.polygon(player.getAttackBox().getVertices());
                entity.gsm.Render.debugRenderer.setColor(Color.YELLOW);
                entity.gsm.Render.debugRenderer.rect(player.getIntereactBox().x, player.getIntereactBox().y, player.getIntereactBox().width, player.getIntereactBox().height);

                if (false) //ONLY SET TO TRUE IF YOU NEED TO VIEW AI GRAPH
                {
                    entity.gsm.Render.debugRenderer.setColor(Color.RED);
                    Collisions.forEach(number -> entity.gsm.Render.debugRenderer.rect(number.getRect().x, number.getRect().y, (number.getRect().width), (number.getRect().height)));

                    for (int y = 0; y < tiledBits.bitTileObjectLayers.get(0).realTile.size(); y++) {
                        for (int x = 0; x < tiledBits.bitTileObjectLayers.get(0).realTile.size(); x++) {
                            switch (MapGraph.getNode(x, y).type) {
                                case FlatTiledNode.GROUND:
                                    entity.gsm.Render.debugRenderer.setColor(Color.GREEN);
                                    //entity.gsm.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                                    break;
                                case FlatTiledNode.COLLIDABLE:
                                    entity.gsm.Render.debugRenderer.setColor(Color.SALMON);
                                    entity.gsm.Render.debugRenderer.rect(x * 16 + 1, y * 16 + 1, 16-2, 16-2);
                                    break;
                                default:
                                    //entity.gsm.Render.debugRenderer.setColor(Color.WHITE);
                                    //entity.gsm.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                                    break;
                            }
                        }
                    }
                }

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
                entity.gsm.Render.debugRenderer.rect(((int)pos.x/16)*16, ((int)pos.y/16)*16, 16, 16);
            }

            entity.gsm.Render.debugRenderer.end();

        }

        public void HandleInput(LevelsFSM entity) {
            Player.Direction[] temp = new Player.Direction[4];
            boolean moving = false;
            Vector2 speedPercent = new Vector2(1, 1);

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
                Common.print("Reloaded Bitwise Images!!");
                tiledBits.reLoadImages();
                //entity.gsm.ctm.newController("template");
            }

            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.T)){
                Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
                Worldcam.unproject(pos);
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

            if (entity.gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.R)){
                if (entity.DialogOpen) {

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

            if (entity.gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_X) || Gdx.input.isKeyJustPressed(Input.Keys.C) ){ // ATTACK
                if(player.AttackTime < .1f) {

                    Particles.AddParticleEffect("sparkle", player.getIntereactBox().x + player.getIntereactBox().width/2, player.getIntereactBox().y + player.getIntereactBox().height/2);
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

            if (entity.gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_L3) || Gdx.input.isKeyJustPressed(Input.Keys.X)){ // THE HEALING BUTTON RIGHT NOW
                Common.print("Healed");
                for (int i = 0; i < Entities.size(); i++) {
                    if(polyoverlap(player.getAttackBox(), Entities.get(i).getHitbox())){
                        if(Entities.get(i) instanceof NPC) {
                            NPC Entitemp = (NPC) Entities.get(i);
                            Entitemp.heal(10);
                            Particles.AddParticleEffect("HealthArea", Entitemp.getPosition().x + Entitemp.getSize().x/2, Entitemp.getPosition().y + Entitemp.getSize().y/2);
                        }
                    } else
                        Particles.AddParticleEffect("Health", player.getIntereactBox().x + player.getIntereactBox().width/2, player.getIntereactBox().y + player.getIntereactBox().height/2);
                }
            }

            if (moving) {
                if (entity.DialogOpen) {
                    //Dont move
                } else
                    //player.setPlayerDirection(finalDirect);
                    player.MovePlayerVelocity(finalDirect,(int) (10), entity.gsm.DeltaTime);
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

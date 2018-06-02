package com.thecubecast.ReEngine.GameStates.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thecubecast.ReEngine.Graphics.Scene2D.Dialog;

public enum Level_States implements State<LevelsFSM>, Scene {

    CarScene() {

        Texture Car;
        boolean finishedScene = false;

        @Override
        public void enter(LevelsFSM entity) {
            entity.gsm.DiscordManager.setPresenceState("Story: Introduction");

            Car = new Texture(Gdx.files.internal("Sprites/car.png"));
        }

        @Override
        public void update(LevelsFSM entity) {
            if (finishedScene){
                entity.stateMachine.changeState(World);
            }
        }

        public void draw(LevelsFSM entity, SpriteBatch g, int height, int width, float Time) {
            entity.MenuDraw(g, Gdx.graphics.getDeltaTime());
            g.draw(Car, width/2-Car.getWidth()/2,height/2-Car.getHeight()/2);

        }

        public void HandleInput(LevelsFSM entity) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { //KeyHit
                entity.AddDialog("test", "Yup, the dialog works just fine", 30, new Texture(Gdx.files.internal("Sprites/Gunter.png")));
                entity.AddDialog("test2", "Yea, nothing wrong with it");
                Dialog temp = new Dialog("test", new Texture(Gdx.files.internal("Sprites/Gunter.png")), "Just what we wanted to see!") {
                    @Override
                    public void exit() {
                        finishedScene = true;
                    }
                };
                temp.setCooldown(30);
                entity.AddDialog(temp);
            }
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

        Texture Splash;

        @Override
        public void enter(LevelsFSM entity) {
            entity.gsm.DiscordManager.setPresenceState("Story: Introduction");

            Splash = new Texture(Gdx.files.internal("Sprites/proto/proto_0.png"));
        }

        @Override
        public void update(LevelsFSM entity) {

        }

        public void draw(LevelsFSM entity, SpriteBatch g, int height, int width, float Time) {
            entity.MenuDraw(g, Gdx.graphics.getDeltaTime());
            g.draw(Splash, width/2-Splash.getWidth()/2,height/2-Splash.getHeight()/2);

        }

        public void HandleInput(LevelsFSM entity) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { //KeyHit
                entity.AddDialog("test", "Yup, the dialog works just fine", 30, new Texture(Gdx.files.internal("Sprites/Gunter.png")));
                entity.AddDialog("test2", "Yea, nothing wrong with it");
                Dialog temp = new Dialog("test", new Texture(Gdx.files.internal("Sprites/Gunter.png")), "Just what we wanted to see!") {
                    @Override
                    public void exit() {

                    }
                };
                temp.setCooldown(30);
                entity.AddDialog(temp);
            }
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

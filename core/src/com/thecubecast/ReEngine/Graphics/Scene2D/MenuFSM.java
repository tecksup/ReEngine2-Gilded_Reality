package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;

import java.net.URI;

public class MenuFSM implements Telegraph {

    public boolean inGame = false;

    protected StateMachine<MenuFSM, MainMenu_State> stateMachine;

    protected Skin skin;
    protected Stage stage;

    protected float width;
    protected float height;

    protected GameStateManager gsm;

    public MenuFSM(float width, float height, OrthographicCamera cam, GameStateManager gsm) {

        this.gsm = gsm;

        this.width = width;
        this.height = height;

        stage = new Stage(new StretchViewport(width, height));
        Gdx.input.setInputProcessor(stage);

        stage.getViewport().setCamera(cam);

        setupSkin();

        stateMachine = new DefaultStateMachine<MenuFSM, MainMenu_State>(this, MainMenu_State.Home);
        stateMachine.getCurrentState().enter(this);
    }

    public MenuFSM(float width, float height, OrthographicCamera cam, GameStateManager gsm, boolean inGame) {

        this.gsm = gsm;

        this.inGame = inGame;

        this.width = width;
        this.height = height;

        stage = new Stage(new StretchViewport(width, height));
        Gdx.input.setInputProcessor(stage);

        stage.getViewport().setCamera(cam);

        setupSkin();

        stateMachine = new DefaultStateMachine<MenuFSM, MainMenu_State>(this, MainMenu_State.Home);
        stateMachine.getCurrentState().enter(this);
    }
    
    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
    }

    public void Draw(SpriteBatch bbg) {
        stateMachine.update();
        stage.getRoot().draw(bbg, 1);
    }



    public void reSize(float width, float height,  OrthographicCamera cam) {
        this.width = width;
        this.height = height;

        stage = new Stage(new StretchViewport(width, height));
        Gdx.input.setInputProcessor(stage);

        stage.getViewport().setCamera(cam);

        stateMachine.changeState(stateMachine.getCurrentState());
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }
}

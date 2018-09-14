package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.GameStateManager;

public class UIFSM implements Telegraph {

    public boolean inGame = false;
    public boolean Visible = true;

    protected StateMachine<UIFSM, UI_state> stateMachine;

    protected Skin skin;
    protected Stage stage;

    protected float width;
    protected float height;

    protected GameStateManager gsm;

    public UIFSM(float width, float height, OrthographicCamera cam, GameStateManager gsm) {


        this.gsm = gsm;

        this.width = width;
        this.height = height;

        stage = new Stage(new StretchViewport(width, height));
        Gdx.input.setInputProcessor(stage);

        stage.getViewport().setCamera(cam);

        setupSkin();

        stateMachine = new DefaultStateMachine<UIFSM, UI_state>(this, UI_state.Home);
        stateMachine.getCurrentState().enter(this);
    }

    public UIFSM(float width, float height, OrthographicCamera cam, GameStateManager gsm, boolean inGame) {

        this.gsm = gsm;

        this.inGame = inGame;

        this.width = width;
        this.height = height;

        stage = new Stage(new StretchViewport(width, height));
        Gdx.input.setInputProcessor(stage);

        stage.getViewport().setCamera(cam);

        setupSkin();

        stateMachine = new DefaultStateMachine<UIFSM, UI_state>(this, UI_state.Home);
        stateMachine.getCurrentState().enter(this);
    }

    public void setState(UI_state State) {
        stateMachine.changeState(State);
        setVisable(true);
    }

    public UI_state getState() {
        return stateMachine.getCurrentState();
    }

    public boolean isVisible() {
        return Visible;
    }

    public void setVisable(boolean visable) {
        Visible = visable;
    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
    }

    public void Draw(SpriteBatch bbg) {
        stateMachine.update();
        stage.draw();
        //stage.getRoot().draw(bbg, 1);
    }



    public void reSize() {
        this.width = gsm.Width;
        this.height = gsm.Height;

        stage = new Stage(new StretchViewport(width,height));

        Gdx.input.setInputProcessor(stage);

        //stage.getViewport().setCamera(cam);

        setupSkin();

        stateMachine.getCurrentState().enter(this);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }
}

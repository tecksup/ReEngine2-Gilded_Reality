package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.Item;
import com.thecubecast.ReEngine.worldObjects.Player;
import com.thecubecast.ReEngine.worldObjects.Storage;
import com.thecubecast.ReEngine.worldObjects.WorldObject;

import javax.xml.soap.Text;
import java.util.List;

public class UIFSM implements Telegraph {

    Player player;
    public Storage StorageOpen;
    public static Item CursorItem;
    public boolean ClickedOutsideInventory = true;

    //-1 if nothing selected, positive numbers including 0 are for actual crafting ids
    public static int CraftingIDSelected = -1;

    public boolean inGame = false;
    public boolean Visible = true;

    protected StateMachine<UIFSM, UI_state> stateMachine;

    protected Skin skin;
    public Stage stage;

    protected GameStateManager gsm;

    public UIFSM(OrthographicCamera cam, GameStateManager gsm) {


        this.gsm = gsm;

        stage = new Stage(new FitViewport(gsm.UIWidth, gsm.UIHeight));

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

        if (CursorItem != null) {
            if (CursorItem.getQuantity() == 0) {
                CursorItem = null;
            }
        }

        stateMachine.update();

        stage.getViewport().update(gsm.UIWidth, gsm.UIHeight, true);
        stage.draw();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void reSize() {
        stage = new Stage(new FitViewport(gsm.UIWidth, gsm.UIHeight));

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

package Editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.Item;
import com.thecubecast.ReEngine.worldObjects.Player;

public class UIFSM implements Telegraph {

    protected StateMachine<UIFSM, UI_state> stateMachine;

    protected Skin skin;
    protected Stage stage;

    public UIFSM() {

        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        Gdx.input.setInputProcessor(stage);

        setupSkin();

        stateMachine = new DefaultStateMachine<>(this, UI_state.Home);
        stateMachine.getCurrentState().enter(this);
    }

    public void setState(UI_state State) {
        stateMachine.changeState(State);
    }

    public UI_state getState() {
        return stateMachine.getCurrentState();
    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
    }

    public void Draw() {

        stateMachine.update();

        stage.draw();
    }

    public void reSize() {
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

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

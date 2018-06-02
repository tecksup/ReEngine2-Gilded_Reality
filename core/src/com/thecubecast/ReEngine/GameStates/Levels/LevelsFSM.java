package com.thecubecast.ReEngine.GameStates.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.controlerManager;
import com.thecubecast.ReEngine.Graphics.Scene2D.Dialog;
import com.thecubecast.ReEngine.Graphics.Scene2D.TkLabel;
import com.thecubecast.ReEngine.worldObjects.NPC;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.Data.GameStateManager.AudioM;

public class LevelsFSM implements Telegraph {

    private Skin skin;
    private Stage Guistage;
    private Table table;

    Boolean DialogOpen = false;
    int DialogTics = 0;
    private List<Dialog> DialogCache = new ArrayList<>();
    TkLabel dialogBoxTitle;
    Image dialogBoxFace;
    TkLabel dialogBoxText;

    protected StateMachine<LevelsFSM, Level_States> stateMachine;
    protected GameStateManager gsm;

    public LevelsFSM(OrthographicCamera cam, GameStateManager gsm) {

        MenuInit(gsm.Width, gsm.Height);

        this.gsm = gsm;

        stateMachine = new DefaultStateMachine<LevelsFSM, Level_States>(this, Level_States.CarScene);
        stateMachine.getCurrentState().enter(this);
    }

    public void Draw(SpriteBatch g, int height, int width, float Time) {
        stateMachine.getCurrentState().draw(this, g,height,width,Time);
    }

    public void Update() {
        stateMachine.update();
    }

    public void HandleInput() {
        if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.R)){
            if (DialogOpen) {
                if(DialogTics > DialogCache.get(0).getCooldown()) {
                    if (DialogCache.size() > 0) {
                        DialogCache.get(0).exit();
                        DialogCache.remove(0);
                        UpdateDialogBox();
                        if(DialogCache.size() == 0) {
                            DialogOpen = false;
                        }
                    } else {
                        DialogOpen = false;
                    }
                } else {
                    dialogBoxText.endScroll();
                }
                AudioM.playS("gain-stone.wav");
            }
        }

        stateMachine.getCurrentState().HandleInput(this);
    }

    public void reSize(OrthographicCamera cam) {
        stateMachine.changeState(stateMachine.getCurrentState());
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
        skin.getFont("Mecha").getData().markupEnabled = true;
        skin.getFont("Pixel").getData().markupEnabled = true;
    }

    public void MenuInit(int width, int height) {

        setupSkin();

        Guistage = new Stage(new StretchViewport(width, height));
        Gdx.input.setInputProcessor(Guistage);

        table = new Table();
        table.setFillParent(true);
        Guistage.addActor(table);

        Table dialogBox = new Table(skin);
        dialogBox.setBackground("Table_dialog");
        Table dialogBoxTitleT = new Table();
        Table dialogBoxTextT = new Table();

        dialogBox.add(dialogBoxTitleT).left().expandX().padLeft(3).padTop(-2).row();
        dialogBox.add(dialogBoxTextT).expandX().left().padLeft(3);

        dialogBoxTitle = new TkLabel("", skin);
        dialogBoxTitle.setAlignment(Align.left);
        dialogBoxFace = new Image(new Texture(Gdx.files.internal("Sprites/face.png")));
        dialogBoxFace.setSize(20,20);
        dialogBoxText= new TkLabel("", skin);
        dialogBoxText.setAlignment(Align.left);
        dialogBoxText.setScrolling(true);

        dialogBoxTitleT.add(dialogBoxTitle).left().fillX();
        dialogBoxTextT.add(dialogBoxFace).expandX().left().padRight(5);
        dialogBoxTextT.add(dialogBoxText).expandX().center();

        table.add(dialogBox).bottom().fillX().expand();

    }

    public void AddDialog(Dialog object) {
        DialogCache.add(object);

        DialogOpen = true;
        UpdateDialogBox();
    }

    public void AddDialog(String Speaker, String Conversation) {
        Dialog temp = new Dialog(Speaker, Conversation) {
            public void exit() {}
        };

        DialogCache.add(temp);
        DialogOpen = true;
        UpdateDialogBox();
    }

    public void AddDialog(String Speaker, String Conversation, int Cooldown) {
        Dialog temp = new Dialog(Speaker, Conversation) {
            public void exit() {}
        };

        temp.setCooldown(Cooldown);

        DialogCache.add(temp);
        DialogOpen = true;
        UpdateDialogBox();
    }

    public void AddDialog(String Speaker, String Conversation, int Cooldown, Texture texture) {
        Dialog temp = new Dialog(Speaker, texture, Conversation) {
            public void exit() {}
        };

        temp.setCooldown(Cooldown);

        DialogCache.add(temp);
        DialogOpen = true;
        UpdateDialogBox();
    }

    public void UpdateDialogBox() {
        DialogTics = 0;
        if(DialogCache.size() > 0) {
            dialogBoxTitle.setText(DialogCache.get(0).getSpeaker());
            dialogBoxText.setText(DialogCache.get(0).getText());
            dialogBoxFace.setDrawable(new TextureRegionDrawable(new TextureRegion(DialogCache.get(0).getSpeakerImage())));
        }
        Guistage.act();
    }

    public void MenuDraw(float Delta) {
        if(DialogTics < 1000)
            DialogTics++;
        Guistage.act(Delta);

        //UpdateDialogBox();
        table.setVisible(DialogOpen);

        Guistage.draw();
    }

    public void MenuDraw(SpriteBatch batch, float Delta) {
        if(DialogTics < 1000)
            DialogTics++;
        Guistage.act(Delta);

        //UpdateDialogBox();
        table.setVisible(DialogOpen);

        Guistage.getRoot().draw(batch, 1);
    }

}

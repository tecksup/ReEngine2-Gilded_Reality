package com.thecubecast.ReEngine.GameStates.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.thecubecast.ReEngine.worldObjects.WorldObject;

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
    Image dialogBoxIcon;
    Image dialogBoxFace;
    TkLabel dialogBoxText;

    protected StateMachine<LevelsFSM, Level_States> stateMachine;
    protected GameStateManager gsm;

    public LevelsFSM(GameStateManager gsm) {

        MenuInit(gsm.Width, gsm.Height);

        this.gsm = gsm;

        stateMachine = new DefaultStateMachine<LevelsFSM, Level_States>(this, Level_States.World);
        stateMachine.getCurrentState().enter(this);
    }

    public void Draw(SpriteBatch g, int height, int width, float Time) {
        stateMachine.getCurrentState().draw(this, g,height,width,Time);

    }

    public void Update() {
        stateMachine.update();
    }

    public void HandleInput() {
        stateMachine.getCurrentState().HandleInput(this);

    }

    public void reSize() {
        stateMachine.getCurrentState().reSize();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
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

        dialogBoxTextT.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                DialogNext();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
                //dialogBoxIcon.setDrawable(skin, "A_icon_alt");
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                //dialogBoxIcon.setDrawable(skin, "A_icon");
            }
        });

        dialogBoxTitle = new TkLabel("", skin);
        dialogBoxTitle.setAlignment(Align.left);
        dialogBoxFace = new Image(new Texture(Gdx.files.internal("Sprites/face.png")));
        //dialogBoxIcon = new Image(skin, "A_icon");
        dialogBoxFace.setSize(20,20);
        dialogBoxText= new TkLabel("", skin);
        dialogBoxText.setAlignment(Align.left);
        dialogBoxText.setScrolling(true);
        dialogBoxText.setWrap(true);

        //Guistage.addActor(dialogBoxIcon);
        //dialogBoxIcon.setPosition(width-3 - dialogBoxIcon.getWidth(), 3);

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

    public void DialogNext() {
        if (DialogOpen) {
            if(DialogTics > DialogCache.get(0).getCooldown()) {
                if (DialogCache.size() > 0) {
                    DialogCache.remove(0).exit();
                    //REPLACE THIS LINE WITH DIALOG CLOSE SOUND
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
        }
    }

    public void MenuDraw(float Delta) {
        if(DialogTics < 1000)
            DialogTics++;
        Guistage.act(Delta);

        //UpdateDialogBox();
        table.setVisible(DialogOpen);
        //dialogBoxIcon.setVisible(DialogOpen);

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

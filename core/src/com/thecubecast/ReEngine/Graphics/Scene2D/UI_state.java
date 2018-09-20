package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.Item;
import com.thecubecast.ReEngine.Data.controlerManager;
import com.thecubecast.ReEngine.GameStates.PlayState;
import com.thecubecast.ReEngine.worldObjects.WorldItem;

import java.net.URI;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.thecubecast.ReEngine.Data.Common.GetMonitorSizeH;
import static com.thecubecast.ReEngine.Data.Common.GetMonitorSizeW;
import static com.thecubecast.ReEngine.Data.GameStateManager.AudioM;
import static com.thecubecast.ReEngine.Data.GameStateManager.ctm;
import static com.thecubecast.ReEngine.GameStates.PlayState.player;
import static com.thecubecast.ReEngine.Graphics.Scene2D.UIFSM.CursorItem;

public enum UI_state implements State<UIFSM> {

   Home() {

       private Table table;

       @Override
       public void enter(UIFSM entity) {

           table = new Table();
           table.setFillParent(true);
           entity.stage.addActor(table);

           final TkTextButton StoryState = new TkTextButton("Story State", entity.skin);
           table.add(StoryState).pad(2);
           table.row();

           final TkTextButton PlayState = new TkTextButton("Play State", entity.skin);
           table.add(PlayState).pad(2);
           table.row();

           final TkTextButton Discord = new TkTextButton("Discord", entity.skin);
           table.add(Discord).pad(2);
           table.row();

           final TkTextButton Options = new TkTextButton("Options", entity.skin);
           table.add(Options).pad(2);
           table.row();

           final TkTextButton button3 = new TkTextButton("Quit", entity.skin);
           table.add(button3).pad(2);
           table.row();

           StoryState.addListener(new ClickListener(){
               @Override
               public void clicked(InputEvent event, float x, float y){
                   //gsm.Audio.stopMusic("8-bit-Digger");
                   //GetLogin("", "");
                   Gdx.app.getPreferences("properties").putString("Username", "");
                   Gdx.app.getPreferences("properties").flush();
                   StoryState.setText("Loading");
               }
           });

           PlayState.addListener(new ClickListener(){
               @Override
               public void clicked(InputEvent event, float x, float y){
                   //gsm.Audio.stopMusic("8-bit-Digger");
                   //GetLogin("", "");
                   Gdx.app.getPreferences("properties").putString("Username", "");
                   Gdx.app.getPreferences("properties").flush();
                   entity.gsm.setState(GameStateManager.State.PLAY);
                   PlayState.setText("Loading");
               }
           });



           Discord.addListener(new ClickListener(){
               @Override
               public void clicked(InputEvent event, float x, float y){
                   try {
                       java.awt.Desktop.getDesktop().browse(new URI("https://discord.gg/7wfpsbf"));
                       Common.print("Opened Discord Link!");
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
           });

           Options.addListener(new ClickListener(){
               @Override
               public void clicked(InputEvent event, float x, float y){
                   entity.stateMachine.changeState(UI_state.Options);
               }
           });

           button3.addListener(new ClickListener(){
               @Override
               public void clicked(InputEvent event, float x, float y){
                   //gsm.Audio.stopMusic("8-bit-Digger");
                   //GetLogin("", "");

                   //Lwjgl3Window window = ((Lwjgl3Graphics)Gdx.graphics).getWindow();
                   //window.iconifyWindow(); // iconify the window

                   Common.ProperShutdown();
               }
           });
       }

      @Override
        public void update(UIFSM entity) {
          table.setVisible(entity.Visible);
          ControllerCheck(table);
          entity.stage.act(Gdx.graphics.getDeltaTime());
        }

       @Override
       public void exit(UIFSM entity) {
           entity.stage.clear();
       }

       @Override
       public boolean onMessage(UIFSM entity, Telegram telegram) {
           return false;
       }
   },

    InGameHome() {


        private Table table;

        @Override
        public void enter(UIFSM entity) {

            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            final TkTextButton Continue = new TkTextButton("Return to Game", entity.skin);
            table.add(Continue).pad(2);
            table.row();

            final TkTextButton Options = new TkTextButton("Options", entity.skin);
            table.add(Options).pad(2);
            table.row();

            final TkTextButton MainMenu = new TkTextButton("Main Menu", entity.skin);
            table.add(MainMenu).pad(2);
            table.row();

            Continue.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.setVisable(false);
                }
            });

            Options.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.stateMachine.changeState(UI_state.Options);
                }
            });

            MainMenu.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    //Return to main menu
                    entity.gsm.setState(GameStateManager.State.MENU);
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },
    Options() {

        private Table table;

        @Override
        public void enter(UIFSM entity) {
            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            final TkTextButton Audio = new TkTextButton("Audio", entity.skin);
            table.add(Audio).pad(2);
            table.row();

            final TkTextButton Graphics = new TkTextButton("Graphics", entity.skin);
            table.add(Graphics).pad(2);
            table.row();

            final TkTextButton Controls = new TkTextButton("Controls", entity.skin);
            table.add(Controls).pad(2);
            table.row();

            final TkTextButton back = new TkTextButton("Back", entity.skin);
            table.add(back).pad(2);
            table.row();

            Audio.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.stateMachine.changeState(UI_state.Audio);
                }
            });

            Graphics.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.stateMachine.changeState(UI_state.Graphics);
                }
            });

            Controls.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.stateMachine.changeState(UI_state.Controls);
                }
            });

            back.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    if(entity.inGame) {
                        entity.stateMachine.changeState(UI_state.InGameHome);
                    } else {
                        entity.stateMachine.changeState(UI_state.Home);
                    }
                }
            });
        }

       @Override
        public void update(UIFSM entity) {
           table.setVisible(entity.Visible);
           ControllerCheck(table);
           entity.stage.act(Gdx.graphics.getDeltaTime());
       }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },
    Audio() {

        private Table table;

        @Override
        public void enter(UIFSM entity) {
            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            final Label Master = new Label("Master Volume", entity.skin);
            final Slider MasterVolume = new Slider(0, 1, 0.01f, false, entity.skin);
            MasterVolume.setValue(AudioM.MasterVolume);
            table.add(Master);
            table.row();
            table.add(MasterVolume).padBottom(12);
            table.row();

            final Label Music = new Label("Music Volume", entity.skin);
            final Slider MusicVolume = new Slider(0, 1, 0.01f, false, entity.skin);
            MusicVolume.setValue(AudioM.MusicVolume);
            table.add(Music);
            table.row();
            table.add(MusicVolume).padBottom(12);
            table.row();

            final Label Sound = new Label("Sound Volume", entity.skin);
            final Slider SoundVolume = new Slider(0, 1, 0.01f, false, entity.skin);
            SoundVolume.setValue(AudioM.SoundVolume);
            table.add(Sound);
            table.row();
            table.add(SoundVolume).padBottom(12);
            table.row();

            final TkTextButton back = new TkTextButton("Back", entity.skin);
            table.add(back).pad(2);
            table.row();

            MasterVolume.addListener(new ChangeListener() {
                @Override
                public void changed (ChangeEvent event, Actor actor) {
                    AudioM.MasterVolume = MasterVolume.getValue();
                }
            });

            MusicVolume.addListener(new ChangeListener() {
                @Override
                public void changed (ChangeEvent event, Actor actor) {
                    AudioM.MusicVolume = MusicVolume.getValue();
                }
            });

            SoundVolume.addListener(new ChangeListener() {
                @Override
                public void changed (ChangeEvent event, Actor actor) {
                    AudioM.SoundVolume = SoundVolume.getValue();
                }
            });

            back.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.stateMachine.changeState(entity.stateMachine.getPreviousState());
                }
            });
        }

       @Override
        public void update(UIFSM entity) {
           table.setVisible(entity.Visible);
           ControllerCheck(table);
           entity.stage.act(Gdx.graphics.getDeltaTime());
       }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    Graphics() {

        private Table table;
        SelectBox ResolutionOptions;
        CheckBox FullScreen;

        @Override
        public void enter(UIFSM entity) {
            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            if (Gdx.app.getPreferences("properties").getString("FullScreen").equals("")) {
                Gdx.app.getPreferences("properties").putString("Resolution", "1280X720");
                Gdx.app.getPreferences("properties").flush();
            }

            FullScreen = new CheckBox("FullScreen", entity.skin);
            FullScreen.setChecked(Gdx.graphics.isFullscreen());
            FullScreen.getLabel().setColor(Color.BLACK);
            FullScreen.setChecked(Gdx.app.getPreferences("properties").getBoolean("FullScreen"));
            table.add(FullScreen).pad(2).row();

            FullScreen.addListener(new ChangeListener(){
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.getPreferences("properties").putBoolean("FullScreen", FullScreen.isChecked());
                    Gdx.app.getPreferences("properties").flush();

                    if (FullScreen.isChecked()) {
                        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    }
                    else {
                        String[] temp = Gdx.app.getPreferences("properties").getString("Resolution").split("X");
                        Gdx.graphics.setWindowedMode(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
                    }

                    String[] temp = Gdx.app.getPreferences("properties").getString("Resolution").split("X");
                    entity.reSize();
                }
            });

            ResolutionOptions = new SelectBox(entity.skin) {
                @Override
                protected void onShow (Actor selectBoxList, boolean below) {
                    //selectBoxList.getColor().a = 0;
                    //selectBoxList.addAction(fadeIn(0.3f, Interpolation.fade));
                }
                @Override
                protected void onHide (Actor selectBoxList) {
                    //selectBoxList.getColor().a = 1;
                    selectBoxList.addAction(removeActor());
                }
            };
            ResolutionOptions.setItems(new String[] {"1280X720", "1366X768", "1440X900", "1600X900", "1920X1080"});
            ResolutionOptions.setSelected(Gdx.app.getPreferences("properties").getString("Resolution"));
            table.add(ResolutionOptions).pad(2).row();

            ResolutionOptions.addListener(new ChangeListener(){
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.getPreferences("properties").putString("Resolution", ResolutionOptions.getSelected().toString());
                    Gdx.app.getPreferences("properties").flush();

                    String[] temp = ResolutionOptions.getSelected().toString().split("X");
                    FullScreen.setChecked(false);
                    Gdx.graphics.setWindowedMode(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
                    entity.reSize();

                    Lwjgl3Window window = ((Lwjgl3Graphics)Gdx.graphics).getWindow();
                    window.setPosition(GetMonitorSizeW()/2 - Gdx.graphics.getWidth()/2, GetMonitorSizeH()/2 - Gdx.graphics.getHeight()/2);
                }

            });

            final TkTextButton back = new TkTextButton("Back", entity.skin);
            table.add(back).pad(2);
            table.row();

            back.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.stateMachine.changeState(entity.stateMachine.getPreviousState());
                }
            });
        }

       @Override
        public void update(UIFSM entity) {
           table.setVisible(entity.Visible);
           ControllerCheck(table);
           entity.stage.act(Gdx.graphics.getDeltaTime());
       }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    Controls() {

        private Table table;

        @Override
        public void enter(UIFSM entity) {
            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            final TkTextButton back = new TkTextButton("Back", entity.skin);
            table.add(back).pad(2);
            table.row();

            back.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.stateMachine.changeState(entity.stateMachine.getPreviousState());
                }
            });
        }
        
        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

   //PLAY STATE STUFF

    Inventory() {

        Skin BackupSkin;

        Item[] InventoryArray = new Item[30];

        private Table Screen;
        private Table InventoryWindow;
        private Table InventoryTable;

        ClickListener StageListener;

        @Override
        public void enter(UIFSM entity) {

            InventoryArray = entity.player.Inventory;

            StageListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //drop out of inventory
                    if (entity.ClickedOutsideInventory) {
                        //Drop item in CursorItem
                        if (entity.CursorItem != null) {
                            WorldItem temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                            PlayState.Entities.add(temp);
                            entity.CursorItem = null;
                        }
                    }

                    entity.ClickedOutsideInventory = true;
                }
            };

            BackupSkin = entity.skin;
            Screen = new Table(entity.skin);
            Screen.setFillParent(true);
            entity.stage.addActor(Screen);

            InventoryWindow = new Table(entity.skin);
            InventoryWindow.setBackground("Table_dialog");
            InventoryWindow.pad(2);

            entity.stage.addListener(StageListener);

            InventoryWindow.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    //Place
                    entity.ClickedOutsideInventory = false;
                    /*if (entity.CursorItem != null) {
                        WorldItem temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                        PlayState.Entities.add(temp);
                        entity.CursorItem = null;
                    }*/
                }
            });

            InventoryTable = new Table(entity.skin);

            for (int i = 1; i < InventoryArray.length+1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi-1);

                ItemBox.add(temp).size(28);

                InventoryTable.add(ItemBox).size(32);
                if (i % 6 == 0)
                    InventoryTable.row();
            }

            InventoryWindow.add(InventoryTable).row();

            Screen.add(InventoryWindow);

            //__________________________________________________________

            final TkTextButton Close = new TkTextButton("Close", entity.skin);
            InventoryWindow.add(Close);
            InventoryWindow.row();

            Close.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.Visible = false;
                }
            });

        }

        @Override
        public void update(UIFSM entity) {
            Screen.setVisible(entity.Visible);
            ControllerCheck(Screen);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {

            for (int j = 0; j < player.Inventory.length; j++) {
                if(player.Inventory[j] == null) {
                    player.Inventory[j] = CursorItem;
                    CursorItem = null;
                    break;
                }
            }

            entity.stage.clear();
            entity.stage.removeListener(StageListener);
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    };

    public void ControllerCheck(Table table) {
        if(ctm.controllers.size() > 0) {
            for(int i = 0; i < table.getCells().size; i++) {
                if(table.getCells().get(i).getActor() instanceof TkTextButton) {
                    int nextSelection = i;
                    if(((TkTextButton) table.getCells().get(i).getActor()).Selected) {
                        //Gdx.app.log("menu", "i is " + i);
                        if (ctm.getAxis(0, controlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                            ((TkTextButton) table.getCells().get(i).getActor()).Selected = false;
                            nextSelection += 1;

                        } else if (ctm.getAxis(0,controlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                            ((TkTextButton) table.getCells().get(i).getActor()).Selected = false;
                            nextSelection -= 1;
                        }

                        if (nextSelection < 0)
                            nextSelection = table.getCells().size-1;
                        if (nextSelection >= table.getCells().size)
                            nextSelection = 0;

                        if(table.getCells().get(nextSelection).getActor() instanceof TkTextButton) {
                            ((TkTextButton) table.getCells().get(nextSelection).getActor()).Selected = true;
                        }

                        if(ctm.isButtonJustDown(0,controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
                            Gdx.app.debug("", "");
                            Array<EventListener> listeners = table.getCells().get(i).getActor().getListeners();
                            for(int b=0;b<listeners.size;b++)
                            {
                                if(listeners.get(b) instanceof ClickListener){
                                    ((ClickListener)listeners.get(b)).clicked(null, 0, 0);
                                }
                            }
                        }

                        break;
                    }
                    else if(i == table.getCells().size-1) {
                        if(table.getCells().get(0).getActor() instanceof TkTextButton)
                            ((TkTextButton) table.getCells().get(0).getActor()).Selected = true;
                        else
                            ((TkTextButton) table.getCells().get(i).getActor()).Selected = true;
                    }
                }
            }

        }
    }


}

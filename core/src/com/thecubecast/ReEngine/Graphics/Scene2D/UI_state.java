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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.thecubecast.ReEngine.Data.*;
import com.thecubecast.ReEngine.GameStates.PlayState;
import com.thecubecast.ReEngine.worldObjects.Storage;
import com.thecubecast.ReEngine.worldObjects.WorldItem;
import com.thecubecast.ReEngine.worldObjects.WorldObject;

import java.net.URI;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.thecubecast.ReEngine.Data.Common.GetMonitorSizeH;
import static com.thecubecast.ReEngine.Data.Common.GetMonitorSizeW;
import static com.thecubecast.ReEngine.Data.GameStateManager.AudioM;
import static com.thecubecast.ReEngine.Data.GameStateManager.ItemPresets;
import static com.thecubecast.ReEngine.Data.GameStateManager.ctm;
import static com.thecubecast.ReEngine.GameStates.PlayState.CraftingRecipes;
import static com.thecubecast.ReEngine.GameStates.PlayState.player;
import static com.thecubecast.ReEngine.Graphics.Draw.OutlineShader;
import static com.thecubecast.ReEngine.Graphics.Draw.setOutlineShaderColor;
import static com.thecubecast.ReEngine.Graphics.Scene2D.UIFSM.CraftingIDSelected;
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

        private Table Screen;
        private Table InventoryWindow;

        private Table InventoryTable;
        private Table EquipmentTable;

        ClickListener StageListener;

        @Override
        public void enter(UIFSM entity) {

            StageListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //drop out of inventory
                    if (entity.ClickedOutsideInventory) {
                        //Drop item in CursorItem
                        if (entity.CursorItem != null) {

                            if (CursorItem.isStructure()) {

                                //GET CORRECT POSITION ON WORLD
                                Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
                                PlayState.camera.unproject(pos);

                                Storage tempObj = new Storage((int) pos.x, (int) pos.y, (int) pos.z, new Vector3(11, 8, 8), WorldObject.type.Static, true) {
                                    Texture Image = new Texture(Gdx.files.internal("Sprites/Map/Objects_04.png"));

                                    @Override
                                    public void init(int Width, int Height) {

                                    }

                                    @Override
                                    public void update(float delta, List<Cube> Colls) {

                                    }

                                    @Override
                                    public void draw(SpriteBatch batch, float Time) {
                                        if (Highlight) {
                                            batch.flush();
                                            batch.setShader(OutlineShader);
                                            setOutlineShaderColor(this.HighlightColor, 0.8f);
                                            batch.draw(Image, getPosition().x, getPosition().y);
                                            batch.setShader(null);
                                        } else {
                                            batch.draw(Image, getPosition().x, getPosition().y);
                                        }
                                    }

                                    @Override
                                    public BoundingBox getImageHitbox() {
                                        BoundingBox temp = new BoundingBox(this.getPosition(), new Vector3(Image.getWidth(), Image.getHeight(), 0).add(this.getPosition()));
                                        return temp;
                                    }
                                };

                                tempObj.setHitboxOffset(new Vector3(3, 0, 0));

                                PlayState.Entities.add(tempObj);
                                if (entity.CursorItem.getQuantity() > 1)
                                    entity.CursorItem.setQuantity(entity.CursorItem.getQuantity()-1);
                                else
                                    entity.CursorItem = null;
                                Vector3 tempVec = tempObj.getPosition();
                                Vector3 tempVecOffset = tempObj.getHitboxOffset();
                                Vector3 tempVecSize = tempObj.getSize();
                                PlayState.Collisions.add(new Cube((int) tempVec.x + (int) tempVecOffset.x, (int) tempVec.y + (int) tempVecOffset.y, (int) tempVec.z + (int) tempVecOffset.z, (int) tempVecSize.x, (int) tempVecSize.y, (int) tempVecSize.z));


                            } else {

                                WorldItem temp = new WorldItem(0, 0, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                switch (player.playerDirection) {

                                    case South:
                                        temp = new WorldItem((int) player.getIntereactBox().min.x, (int) player.getIntereactBox().min.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                        break;
                                    case SouthEast:
                                        temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                        break;
                                    case East:
                                        temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                        break;
                                    case NorthEast:
                                        temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                        break;
                                    case North:
                                        temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                        break;
                                    case NorthWest:
                                        temp = new WorldItem((int) player.getIntereactBox().min.x, (int) player.getIntereactBox().min.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                        break;
                                    case West:
                                        temp = new WorldItem((int) player.getIntereactBox().min.x, (int) player.getIntereactBox().min.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                        break;
                                    case SouthWest:
                                        temp = new WorldItem((int) player.getIntereactBox().min.x, (int) player.getIntereactBox().min.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                        break;
                                }
                                PlayState.Entities.add(temp);
                                entity.CursorItem = null;
                            }
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
            InventoryWindow.setBackground("Window_grey_back");
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

            for (int i = 1; i < player.Inventory.length+1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi-1);

                ItemBox.add(temp).size(32);

                InventoryTable.add(ItemBox).size(36).pad(0.5f);
                if (i % 6 == 0)
                    InventoryTable.row();
            }

            EquipmentTable = new Table(entity.skin);

            for (int i = 1; i < player.Equipment.length+1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi-1, true);

                ItemBox.add(temp).size(32);

                EquipmentTable.add(ItemBox).size(36).pad(0.5f).row();
            }

            InventoryWindow.add(InventoryTable);
            InventoryWindow.add(EquipmentTable).row();

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
    },

    InventoryAndStorage() {

        Skin BackupSkin;

        private Table Screen;
        private Table InventoryWindow;

        private Table InventoryTable;
        private Table EquipmentTable;

        private Table StorageInventoryWindow;

        private Table StorageInventoryTable;

        ClickListener StageListener;

        @Override
        public void enter(UIFSM entity) {

            StageListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //drop out of inventory
                    if (entity.ClickedOutsideInventory) {
                        //Drop item in CursorItem
                        if (entity.CursorItem != null) {
                            WorldItem temp = new WorldItem(0, 0, (int) player.getIntereactBox().max.z, entity.CursorItem);
                            switch (player.playerDirection) {

                                case South:
                                    temp = new WorldItem((int) player.getIntereactBox().min.x, (int) player.getIntereactBox().min.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                    break;
                                case SouthEast:
                                    temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                    break;
                                case East:
                                    temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                    break;
                                case NorthEast:
                                    temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                    break;
                                case North:
                                    temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                    break;
                                case NorthWest:
                                    temp = new WorldItem((int) player.getIntereactBox().min.x, (int) player.getIntereactBox().min.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                    break;
                                case West:
                                    temp = new WorldItem((int) player.getIntereactBox().min.x, (int) player.getIntereactBox().min.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                    break;
                                case SouthWest:
                                    temp = new WorldItem((int) player.getIntereactBox().min.x, (int) player.getIntereactBox().min.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                                    break;
                            }
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
            InventoryWindow.setBackground("Window_grey_back");
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

            for (int i = 1; i < player.Inventory.length+1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi-1);

                ItemBox.add(temp).size(32);

                InventoryTable.add(ItemBox).size(36).pad(0.5f);
                if (i % 6 == 0)
                    InventoryTable.row();
            }

            EquipmentTable = new Table(entity.skin);

            for (int i = 1; i < player.Equipment.length+1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi-1, true);

                ItemBox.add(temp).size(32);

                EquipmentTable.add(ItemBox).size(36).pad(0.5f).row();
            }

            InventoryWindow.add(InventoryTable);
            InventoryWindow.add(EquipmentTable).row();

            Screen.add(InventoryWindow);

            StorageInventoryWindow = new Table(entity.skin);
            StorageInventoryWindow.setBackground("Window_grey_back");
            StorageInventoryWindow.pad(2);

            StorageInventoryWindow.addListener(new ClickListener(){
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

            StorageInventoryTable = new Table(entity.skin);

            for (int i = 1; i < entity.StorageOpen.Inventory.length+1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi-1, entity.StorageOpen);

                ItemBox.add(temp).size(32);

                StorageInventoryTable.add(ItemBox).size(36).pad(0.5f);
                if (i % 6 == 0)
                    StorageInventoryTable.row();
            }

            StorageInventoryWindow.add(StorageInventoryTable);
            Screen.add(StorageInventoryWindow);

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
    },

    CraftingNew() {

        Skin BackupSkin;

        private Table Screen;
        private Table UIWindow;
        private Table CraftingWindow;

        private Table RecipeList;
        private Table CraftingDescription;

        @Override
        public void enter(UIFSM entity) {

            BackupSkin = entity.skin;
            Screen = new Table(entity.skin);
            Screen.setFillParent(true);
            entity.stage.addActor(Screen);

            UIWindow = new Table(entity.skin);
            UIWindow.setBackground("Window_grey_back");

            CraftingWindow = new Table(entity.skin);
            UIWindow.add(CraftingWindow).fill();

            //--------------------

            CraftingDescription = new Table(entity.skin);
            CraftingDescription.setBackground("Window_red");

            Table CraftingDescTop = new Table(entity.skin);
            TypingLabel ItemName = new TypingLabel("", entity.skin);
            ItemName.setWidth(CraftingDescTop.getWidth());
            ItemName.setName("ItemTitle");
            if (CraftingIDSelected != -1) {
                ItemName.restart(ItemPresets.get(CraftingRecipes.get(CraftingIDSelected).getCraftableID()).getName());
                ItemName.skipToTheEnd();
            } else {
                ItemName.restart("Select an Icon from the left!\nCheck the requirements\nThen Click Craft!");
            }
            CraftingDescTop.add(ItemName).pad(5).row();
            TkItemIcon CrafintItemIcon = new TkItemIcon(entity.skin, CraftingIDSelected);
            CrafintItemIcon.setName("CraftingIcon");
            CraftingDescTop.add(CrafintItemIcon).size(48).center().row();
            CraftingDescTop.row();

            CraftingDescription.add(CraftingDescTop).row();
            Table CraftingDescBottom = new Table(entity.skin);
            CraftingDescBottom.setBackground("Window_grey_TopOutline");
            CraftingDescription.add(CraftingDescBottom).padTop(5).expand().row();

            TypingLabel RescourLable = new TypingLabel("Ingredients", entity.skin);
            RescourLable.skipToTheEnd();
            CraftingDescBottom.add(RescourLable).row();

            TkIngrediantsTable IngrediatesTable = new TkIngrediantsTable(entity.skin);

            CraftingDescBottom.add(IngrediatesTable).padTop(2).center().row();
            CraftingDescBottom.row();

            TypingLabel ItemDescription = new TypingLabel("", entity.skin);
            ItemDescription.setWrap(true);
            if (CraftingIDSelected != -1) {
                ItemDescription.restart(ItemPresets.get(CraftingRecipes.get(CraftingIDSelected).getCraftableID()).getDescription());
                ItemDescription.skipToTheEnd();
            }
            ScrollPane DescPane = new ScrollPane(ItemDescription, entity.skin);
            DescPane.setScrollingDisabled(true, false);
            DescPane.addListener(new ClickListener(){
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    entity.stage.setScrollFocus(DescPane);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    entity.stage.setScrollFocus(null);
                }
            });
            CraftingDescBottom.add(DescPane).width(130).expandY().pad(5).row();

            //--------------------
            RecipeList = new Table(entity.skin);
            ScrollPane RecipeScroll = new ScrollPane(RecipeList, entity.skin);
            RecipeScroll.setupOverscroll(5, 50f, 100f);
            RecipeScroll.addListener(new ClickListener(){
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    entity.stage.setScrollFocus(RecipeScroll);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    entity.stage.setScrollFocus(null);
                }
            });

            CraftingWindow.add(RecipeScroll).width(150).height(200).padRight(1);

            for (int i = 0; i < CraftingRecipes.size(); i++) {
                int tempi = i;

                Table Container = new Table(entity.skin);
                TkItemIcon ItemIcon = new TkItemIcon(entity.skin, tempi);
                Container.add(ItemIcon).size(32);
                ItemIcon.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        CraftingIDSelected = CraftingRecipes.get(tempi).getCraftableID();
                        TkItemIcon temp = CraftingDescTop.findActor("CraftingIcon");
                        temp.reload(CraftingIDSelected);
                        ItemDescription.restart(ItemPresets.get(CraftingRecipes.get(CraftingIDSelected).getCraftableID()).getDescription());
                        ItemDescription.skipToTheEnd();
                        ItemName.restart(ItemPresets.get(CraftingRecipes.get(CraftingIDSelected).getCraftableID()).getName());
                        ItemName.skipToTheEnd();
                    }

                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        super.enter(event, x, y, pointer, fromActor);
                        Container.setBackground("Window_grey");
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        super.exit(event, x, y, pointer, toActor);
                        Container.setBackground((Drawable) null);
                    }
                });

                RecipeList.add(Container).size(36);

                RecipeList.row();
            }


            //--------------------------------------

            CraftingWindow.add(CraftingDescription).top().width(150).height(200).padLeft(1);

            TextButton Craft = new TextButton("Craft", entity.skin) {
                @Override
                public void act(float delta) {
                    super.act(delta);
                    //Check if crafting is aloud
                    //Disable button if you can't craft it
                    boolean canCraft = true;
                    if (CraftingIDSelected == -1) {
                        this.setDisabled(true);
                        return;
                    }
                    for (int i = 0; i < CraftingRecipes.get(CraftingIDSelected).RequiredResources().length; i++) {
                        if (CraftingRecipes.get(CraftingIDSelected).RequiredResources()[i][1] >  player.getItemQuant(CraftingRecipes.get(CraftingIDSelected).RequiredResources()[i][0])) {
                            canCraft = false;
                        }
                    }
                    this.setDisabled(!canCraft);
                }
            };
            CraftingDescription.add(Craft);
            Craft.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    if (CraftingIDSelected == -1)
                            return;

                    boolean canCraft = true;
                    for (int i = 0; i < CraftingRecipes.get(CraftingIDSelected).RequiredResources().length; i++) {
                        if (CraftingRecipes.get(CraftingIDSelected).RequiredResources()[i][1] >  player.getItemQuant(CraftingRecipes.get(CraftingIDSelected).RequiredResources()[i][0])) {
                            canCraft = false;
                        }
                    }

                    if (!canCraft) {
                        return;
                    }

                    //Deduct the items from inventory
                    for (int i = 0; i < CraftingRecipes.get(CraftingIDSelected).RequiredResources().length; i++) {
                        player.DeductFromInventory(CraftingRecipes.get(CraftingIDSelected).RequiredResources()[i][0], CraftingRecipes.get(CraftingIDSelected).RequiredResources()[i][1]);
                    }

                    //Craft it and add it to inventory
                    player.AddToInventory(ItemPresets.get(CraftingRecipes.get(CraftingIDSelected).getCraftableID()).setQuantity(CraftingRecipes.get(CraftingIDSelected).getQuantity()));

                    IngrediatesTable.reload();
                }
            });

            //--------------------------------------
            Screen.add(UIWindow);

            UIWindow.row();
            final TkTextButton Close = new TkTextButton("Close", entity.skin);
            UIWindow.add(Close).padTop(2);

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
            entity.stage.clear();
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

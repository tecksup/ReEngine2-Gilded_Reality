package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.controlerManager;

import java.net.URI;

import static com.thecubecast.ReEngine.Data.GameStateManager.AudioM;
import static com.thecubecast.ReEngine.Data.GameStateManager.ctm;

public enum MainMenu_State implements State<MenuFSM> {

   Home() {

       private Table table;
       
       @Override
       public void enter(MenuFSM entity) {

           table = new Table();
           table.setFillParent(true);
           entity.stage.addActor(table);

           final TkTextButton Story = new TkTextButton("Play", entity.skin);
           table.add(Story).pad(2);
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

           Story.addListener(new ClickListener(){
               @Override
               public void clicked(InputEvent event, float x, float y){
                   entity.gsm.setState(GameStateManager.State.PLAY);
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
                   entity.stateMachine.changeState(MainMenu_State.Options);
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
        public void update(MenuFSM entity) { 
            ControllerCheck(table);

            entity.stage.act(Gdx.graphics.getDeltaTime()); 
        }

       @Override
       public void exit(MenuFSM entity) {
           entity.stage.clear();
       }

       @Override
       public boolean onMessage(MenuFSM entity, Telegram telegram) {
           return false;
       }
   },

    InGameHome() {

        private Table table;

        @Override
        public void enter(MenuFSM entity) {

        }

        @Override
       public void update(MenuFSM entity) {
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
                              nextSelection = table.getCells().size;
                          if (nextSelection >= table.getCells().size)
                              nextSelection = 0;

                          if(table.getCells().get(nextSelection).getActor() instanceof TkTextButton) {
                              ((TkTextButton) table.getCells().get(nextSelection).getActor()).Selected = true;
                          }

                          if(ctm.isButtonJustDown(0,controlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
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
                          ((TkTextButton) table.getCells().get(i).getActor()).Selected = true;
                      }
                   }
               }

           }

           entity.stage.act(Gdx.graphics.getDeltaTime());
       }

        @Override
        public void exit(MenuFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(MenuFSM entity, Telegram telegram) {
            return false;
        }
    },
    Options() {

        private Table table;

        @Override
        public void enter(MenuFSM entity) {
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
                    entity.stateMachine.changeState(MainMenu_State.Audio);
                }
            });

            Graphics.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.stateMachine.changeState(MainMenu_State.Graphics);
                }
            });

            Controls.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    entity.stateMachine.changeState(MainMenu_State.Controls);
                }
            });

            back.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    if(entity.inGame) {
                        entity.stateMachine.changeState(MainMenu_State.InGameHome);
                    } else {
                        entity.stateMachine.changeState(MainMenu_State.Home);
                    }
                }
            });
        }

       @Override
        public void update(MenuFSM entity) { 
            ControllerCheck(table);

            entity.stage.act(Gdx.graphics.getDeltaTime()); 
        }

        @Override
        public void exit(MenuFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(MenuFSM entity, Telegram telegram) {
            return false;
        }
    },
    Audio() {

        private Table table;

        @Override
        public void enter(MenuFSM entity) {
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
        public void update(MenuFSM entity) { 
            ControllerCheck(table);

            entity.stage.act(Gdx.graphics.getDeltaTime()); 
        }

        @Override
        public void exit(MenuFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(MenuFSM entity, Telegram telegram) {
            return false;
        }
    },
    Graphics() {

        private Table table;

        @Override
        public void enter(MenuFSM entity) {
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
        public void update(MenuFSM entity) { 
            ControllerCheck(table);

            entity.stage.act(Gdx.graphics.getDeltaTime()); 
        }

        @Override
        public void exit(MenuFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(MenuFSM entity, Telegram telegram) {
            return false;
        }
    },
    Controls() {

        private Table table;

        @Override
        public void enter(MenuFSM entity) {
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
        public void update(MenuFSM entity) { 
            ControllerCheck(table);

            entity.stage.act(Gdx.graphics.getDeltaTime()); 
        }

        @Override
        public void exit(MenuFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(MenuFSM entity, Telegram telegram) {
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

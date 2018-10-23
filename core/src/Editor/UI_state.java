package Editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thecubecast.ReEngine.Data.Common;

import java.io.File;
import java.net.URI;

public enum UI_state implements State<UIFSM> {

   Home() {

       @Override
       public void enter(UIFSM entity) {

           Table FileBar = new Table(entity.skin);
           entity.stage.addActor(FileBar);
           FileBar.top().left().row();
           FileBar.setFillParent(true);
           FileBar.setHeight(16);
           Table Else = new Table(entity.skin);
           Else.setFillParent(true);
           Else.padTop(16);
           entity.stage.addActor(Else);

           final TextButton StoryState = new TextButton("File", entity.skin);
           FileBar.add(StoryState);

           final TextButton PlayState = new TextButton("Settings", entity.skin);
           FileBar.add(PlayState);

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
                   PlayState.setText("Loading");
               }
           });

       }

      @Override
        public void update(UIFSM entity) {
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
   }
}

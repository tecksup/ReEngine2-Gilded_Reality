// GameState that shows logo.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;

public class LoadingState extends GameState {
	
	int tics = 0;
	
	private String Load;

	private Skin skin;
	private Stage stage;
	private Table table;
	ProgressBar progress;
	
	public void setLoad(String LoadIt) {
		Load = LoadIt;
	}
	
	public LoadingState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void init() {
		//Common.print("Loading " + Load);
		MenuInit();
		gsm.Render.Load();

	}

	public void setupSkin() {
		skin = new Skin(Gdx.files.internal("Skins/flat-earth/skin/flat-earth-ui.json"));
	}

	public void MenuInit() {

		setupSkin();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		table.bottom().left().padLeft(150f).padBottom(65f);
		stage.addActor(table);


		progress = new ProgressBar(0f, 1f, 0.01f, false, skin);
		table.add(progress);
		table.row();
	}



	public void update() {
		tics++;
		gsm.Render.manager.update();
		progress.setValue(gsm.Render.manager.getProgress());
		if (Load.equals("STARTUP")) {
			if(gsm.Render.manager.getProgress() == 1) {
				gsm.Render.LoadVariables();
				gsm.setState(GameStateManager.INTRO);
			}
		}
		handleInput();
	}
	
	public void draw(SpriteBatch g, int width, int height, float Time) {
		g.begin();

		stage.act(Gdx.graphics.getDeltaTime());

		//gsm.Render.DrawAnimatedTile(g, gsm.Render.LoadingAnimation, 50, 50, 2.0f, 2.0f, Time);
		gsm.Render.DrawAnimatedTile(g, gsm.Render.LoadingAnimation, 50, 50, 46, 46, Time);
		stage.getRoot().draw(g, 1);
		g.end();
	}
	
	public void handleInput() {

		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) { //KeyHit
			//gsm.setState(GameStateManager.INTRO);
		}

	}
	
	public void reSize(SpriteBatch g,int wi, int he) {}
}
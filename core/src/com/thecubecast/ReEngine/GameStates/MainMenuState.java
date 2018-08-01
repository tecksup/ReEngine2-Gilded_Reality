// GameState that shows Main Menu.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.controlerManager;
import com.thecubecast.ReEngine.Graphics.Scene2D.MenuFSM;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import static com.thecubecast.ReEngine.Data.GameStateManager.AudioM;


public class MainMenuState extends GameState {
	
	OrthographicCamera cameraGui;
	MenuFSM Menus;

	int BGMusicID;

	Texture Background;

	public MainMenuState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void init() {

		Background = new Texture(Gdx.files.internal("Images/image_04.png"));

		gsm.DiscordManager.setPresenceState("In Menus");
		
		cameraGui = new OrthographicCamera();

		Menus = new MenuFSM(gsm.Width, gsm.Height, cameraGui, gsm);

		//BGMusicID = AudioM.playMusic("forgetting.mp3", true);
	}
	
	public void update() {
		handleInput();
		
		cameraGui.update();
	}
	
	public void draw(SpriteBatch bbg, int height, int width, float Time) {
		cameraGui.setToOrtho(false, width, height);
		bbg.setProjectionMatrix(cameraGui.combined);
		bbg.begin();

		bbg.draw(Background, 0, 0, width, height);

		Menus.Draw(bbg);
		
		bbg.end();
	}
	
	public void handleInput() {
		
		Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
		cameraGui.unproject(pos);
		
		gsm.MouseX = (int) pos.x;
		gsm.MouseY = (int) pos.y;
		gsm.MouseClick[1] = (int) pos.x;
		gsm.MouseClick[2] = (int) pos.y;
		gsm.MouseDrag[1] = (int) pos.x;
		gsm.MouseDrag[2] = (int) pos.y;

	}
	
	public void reSize(SpriteBatch g, int H, int W) {
		//stage.getViewport().update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), true);
		cameraGui.setToOrtho(false);
		//Menus.reSize(H, W, cameraGui);
	}

	@Override
	public void Shutdown() {

	}

	@Override
	public void dispose() {
		//AudioM.stopMusic(BGMusicID);
	}
	
}
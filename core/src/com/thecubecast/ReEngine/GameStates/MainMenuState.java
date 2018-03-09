// GameState that shows Main Menu.

package com.thecubecast.ReEngine.GameStates;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.controlerManager;
import com.thecubecast.ReEngine.Graphics.Scene2D.TkTextButton;


public class MainMenuState extends GameState {
	
	OrthographicCamera cameraGui;

	private Skin skin;
	private Stage stage;
	private Table table;
	
	//The Menu states
	private int OldState;
	private int currentState;

	public MainMenuState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void init() {

		gsm.DiscordManager.setPresenceState("In Menus");

		MenuInit();
		
		cameraGui = new OrthographicCamera();
		
		//gsm.Audio.playMusic("8-bit-Digger", true);
		
		stage.getViewport().setCamera(cameraGui);
	}
	
	public void update() {
		handleInput();
		
		//if (AudioMenu.get(4).GetBool() && gsm.Audio.isPlaying("8-bit-Digger")) {
		//	gsm.Audio.pauseMusic("8-bit-Digger");
		//	Common.print("Audio Paused");
		//} else if (gsm.Audio.isPlaying("8-bit-Digger") != true){
		//	gsm.Audio.playMusic("8-bit-Digger", true);
		//}
		
		cameraGui.update();
	}
	
	public void draw(SpriteBatch bbg, int height, int width, float Time) {
		cameraGui.setToOrtho(false, width, height);
		bbg.setProjectionMatrix(cameraGui.combined);
		bbg.begin();
		
		//gsm.Render.DrawBackground(bbg, width, height);
		bbg.draw(gsm.Render.Images[03], 0, 0, width, height);
		
		MenuDraw(bbg);
		
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
		
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			//JukeBox.stop("MenuNavigate");
			//Click.play((SoundVolume * MasterVolume),1,0);
			//Check what button the user is on, runs its function
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			//JukeBox.stop("MenuNavigate");
			
			//Moves the Chosen button RIGHT
		}

		if (gsm.ctm.isButtonJustDown(0, controlerManager.buttons.BUTTON_START)) {
			gsm.setState(GameStateManager.State.Dialog);
		}

	}
	
	public void reSize(SpriteBatch g, int H, int W) {
		//stage.getViewport().update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), true);
		cameraGui.setToOrtho(false);
		MenuInit();
	}
	
	//BELOW IS MENU CODE
	//SHOULD BE SOMEWHAT PORTABLE
	
	public void changeState(int state) {
		OldState = currentState;
		currentState = state;
	}
	
	public void Back() {
		int state = OldState;
		OldState = currentState;
		currentState = state;
	}

	public void setupSkin() {
		 skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
	}
	
	public void MenuInit() {

		setupSkin();
        stage = new Stage(new StretchViewport(gsm.Width, gsm.Height));
		Gdx.input.setInputProcessor(stage);
		
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		final TkTextButton button1 = new TkTextButton("Start", skin);
		table.add(button1).pad(2);
		table.row();

		final TkTextButton button4 = new TkTextButton("Dialog", skin);
		table.add(button4).pad(2);
		table.row();

		final TkTextButton Discord = new TkTextButton("Discord", skin);
		table.add(Discord).pad(2);
		table.row();

		final TkTextButton button3 = new TkTextButton("Quit", skin);
		table.add(button3).pad(2);
		table.row();

		button1.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){
            	//gsm.Audio.stopMusic("8-bit-Digger");
				//GetLogin("", "");
				Gdx.app.getPreferences("properties").putString("Username", "");
				Gdx.app.getPreferences("properties").flush();
            	gsm.setState(GameStateManager.State.Blank);
                button1.setText("Loading");
            }
        });

		button4.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				//gsm.Audio.stopMusic("8-bit-Digger");
				//GetLogin("", "");
				Gdx.app.getPreferences("properties").putString("Username", "");
				Gdx.app.getPreferences("properties").flush();
				gsm.setState(GameStateManager.State.Dialog);
				button1.setText("Loading");
			}
		});


		Discord.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				try {
					java.awt.Desktop.getDesktop().browse(new URI ("https://discord.gg/xaktmEZ"));
					Common.print("Opened Discord Link!");
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	
	public void MenuDraw(SpriteBatch bbg) {
		stage.act(Gdx.graphics.getDeltaTime());
		stage.getRoot().draw(bbg, 1);
	}

	//Ends the Gui Shit
	
	
	public String GetLogin(String email, String Password) throws Exception {
		String url = "https://dev.thecubecast.com/game/login_game.php";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.addRequestProperty("email", email);
		con.addRequestProperty("password", Password);

		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(("email="+email).getBytes());
		os.write(("password="+Password).getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		return(response.toString());
	}
	
	
}
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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Input.Keys;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;


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
		cameraGui.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		
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
	
	public void draw(SpriteBatch bbg, int width, int height, float Time) {
		bbg.begin();
		bbg.setProjectionMatrix(cameraGui.combined);
		
		//gsm.Render.DrawBackground(bbg, width, height);
		bbg.draw(gsm.Render.Images[03], 0, 0, width, height);
		
		MenuDraw(bbg, width, height, Time);
		
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
		
	}
	
	public void reSize(SpriteBatch g, int H, int W) {
		//stage.getViewport().update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), true);
		cameraGui.setToOrtho(false);
		stage = new Stage(new FitViewport(W, H)); Gdx.input.setInputProcessor(stage);
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
		 skin = new Skin(Gdx.files.internal("Skins/flat-earth/skin/flat-earth-ui.json"));
	}
	
	public void MenuInit() {

		setupSkin();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		
		final Label label1 = new Label("Username", skin);
		label1.setColor(0, 0, 0, 1);
		final TextField text1 = new TextField("", skin);
		text1.setText(Gdx.app.getPreferences("properties").getString("Username"));
		table.add(label1);
		table.add(text1).padLeft(12);
		table.row();
		final Label label3 = new Label("Password", skin);
		label3.setColor(0, 0, 0, 1);
		final TextField text3 = new TextField("", skin);
		text3.setName("Password");
		text3.setPasswordMode(true);
		text3.setPasswordCharacter("*".toCharArray()[0]);
		table.add(label3);
		table.add(text3).padLeft(12);
		table.row();
		final Label label2 = new Label("IP", skin);
		label2.setColor(0, 0, 0, 1);
		final TextField text2 = new TextField("", skin);
		table.add(label2);
		table.add(text2).padLeft(12);
		table.row();
		
		
		final TextButton button1 = new TextButton("Start", skin);
		table.add(button1).pad(12);
		table.row();

		final TextButton Discord = new TextButton("Discord", skin);
		table.add(Discord).pad(12);
		table.row();

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

		final TextButton button3 = new TextButton("Quit", skin);
		table.add(button3);
		table.row();

		button1.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){
            	//gsm.Audio.stopMusic("8-bit-Digger");
				//GetLogin("", "");
            	gsm.Username = text1.getText();
				Gdx.app.getPreferences("properties").putString("Username", text1.getText());
				Gdx.app.getPreferences("properties").flush();
            	gsm.IpAdress = text2.getText();
            	gsm.setState(GameStateManager.State.PLAY);
                button1.setText("Loading");
            }
        });

		button3.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				//gsm.Audio.stopMusic("8-bit-Digger");
				//GetLogin("", "");

				Lwjgl3Window window = ((Lwjgl3Graphics)Gdx.graphics).getWindow();
				//window.iconifyWindow(); // iconify the window

				//Common.ProperShutdown();
			}
		});

		text1.addListener(new InputListener() {
			public boolean keyUp(InputEvent event, int keycode) {
	                Common.print("typed started at" + keycode);
	                if (keycode == 66) {// Enter
	                	gsm.Username = text1.getText();
						Gdx.app.getPreferences("properties").putString("Username", text1.getText());
						Gdx.app.getPreferences("properties").flush();
	                	gsm.IpAdress = text2.getText();
						gsm.setState(GameStateManager.State.PLAY);
	                    button1.setText("Loading");
	                }
	                //if (keycode == 66) // Tab
	                	//Do nothing as of right now
					return false;
	        }
		});
	}
	
	public void MenuDraw(SpriteBatch bbg, int width, int height, float Time) {
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
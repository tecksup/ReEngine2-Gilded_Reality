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

		Background = new Texture(Gdx.files.internal("Images/image_03.png"));

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
		//Menus.reSize(H, W, cameraGui);
	}
	
	
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

	@Override
	public void Shutdown() {

	}

	@Override
	public void dispose() {
		//AudioM.stopMusic(BGMusicID);
	}
	
}
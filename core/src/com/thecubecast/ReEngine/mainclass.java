package com.thecubecast.ReEngine;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Matrix4;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import sun.applet.Main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Stream;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_NEAREST;
import static com.thecubecast.ReEngine.Data.Common.GetMonitorSizeH;
import static com.thecubecast.ReEngine.Data.Common.GetMonitorSizeW;

public class mainclass extends ApplicationAdapter implements InputProcessor{
	
	//The Drawing Variable
	private int W;
	private int H;

	private SpriteBatch batch;
	
	//Mouse Position in the window
	private int MouseX;
	private int MouseY;
	//The [0] is 1 for dragging, and 0 for not. the rest is x y
	private int[] MouseDrag = new int[] {0, 0, 0};
	//The [0] is 1 for clicked, and 0 for not. the rest is x y
	private int[] MouseClick = new int[] {0, 0, 0};
	
	// game state manager
	public GameStateManager gsm;

	// A variable for tracking elapsed time for the animation
	private float stateTime;
	
	@Override
	public void create () { // INIT FUNCTION

		String[] temp = Gdx.app.getPreferences("properties").getString("Resolution").split("X");
		Gdx.graphics.setWindowedMode(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));

		Lwjgl3Window window = ((Lwjgl3Graphics)Gdx.graphics).getWindow();
		window.setPosition(GetMonitorSizeW()/2 - Gdx.graphics.getWidth()/2, GetMonitorSizeH()/2 - Gdx.graphics.getHeight()/2);

		if (Gdx.app.getPreferences("properties").getBoolean("FullScreen")) {
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}

		Cursor customCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor0.png")), 0, 0);
		Gdx.graphics.setCursor(customCursor);
		
		stateTime = 0f;
		
		Gdx.input.setInputProcessor(this);

		//Just setting up the variables
		W = Gdx.graphics.getWidth();
		H = Gdx.graphics.getHeight();

		gsm = new GameStateManager(W, H);

		//This is essentially the graphics object we draw too
		batch = new SpriteBatch();
	}
	
	@Override
	public void render () { // UPDATE Runs every frame. 60FPS

		//Gdx.gl.glClearColor( 1, 1, 1, 1 );
		Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
		
		//Code goes here
		
		UpdateInput();
		Update(); //UPDATE
	
		
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

		Draw(batch); //DRAW
		batch.begin();
		gsm.Render.GUIDrawText(batch, 0,H , Gdx.graphics.getFramesPerSecond() + "", Color.YELLOW);
		batch.end();

		if(MouseClick[0] == 1) {
			MouseClick[0] = 0;
		}
	}
	
	public void UpdateInput(){

		if (Gdx.input.isKeyJustPressed(Keys.GRAVE)) { //KeyHit
			Common.ProperShutdown(gsm);
		}

        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Keys.D)) { //KeyHit
            gsm.Debug = !gsm.Debug;
        }

		/*if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Keys.P)) { //KeyHit
			Path path = Paths.get("FpsLog.debug");
			ArrayList<String> lines = new ArrayList<String>();
			for(int i = 0; i < gsm.fpsLog.length; i++) {
				lines.add(gsm.fpsLog[i]+",");
			}
			try {
				Files.deleteIfExists(path);
				Files.write(path, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
			} catch (IOException e) {e.printStackTrace();}
		}*/
	}
	
	public void Update() {
		
		//Figure out how to do this before you start exporting things to external files
		gsm.update(MouseX, MouseY, MouseDrag, MouseClick);
	}
	
	public void Draw(SpriteBatch bbg) {
		//Figure out how to do this before you start exporting things to external files
		gsm.draw(bbg, W, H, stateTime);
	}
	
	@Override
	public void resize(int width, int height) {
		W = width;
		H = height;
		//MasterFBO = new FrameBuffer(Pixmap.Format.RGBA8888, MasterFBOOW, MasterFBOOH, false);
		Common.print("Ran Resize!");
		Common.print("" + width + " and H: " + height);
		gsm.reSize(batch, H, W);
	}
	
	
	@Override
	public void dispose () { //SHUTDOWN FUNCTION
		batch.dispose();
		gsm.dispose();
	}

	public boolean keyDown(int keycode) {return false;}
	public boolean keyUp(int keycode) {return false;}
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}


	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		MouseDrag[0] = 0;
		Common.print("Clicked!");
		int[] MouseClicked = new int[] {1, MouseX, MouseY};
		MouseClick = MouseClicked;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Common.print("Dragging");
			int[] MouseDraged = new int[] {1, screenX, screenY};
			MouseDrag = MouseDraged;
			MouseX = screenX;
			MouseY = screenY;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		MouseX = screenX;
		MouseY = screenY;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
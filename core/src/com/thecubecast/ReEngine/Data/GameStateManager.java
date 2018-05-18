// The GameStateManager does exactly what its
// name says. It contains a list of GameStates.
// It decides which GameState to update() and
// draw() and handles switching between different
// GameStates.

package com.thecubecast.ReEngine.Data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.thecubecast.ReEngine.GameStates.*;
import com.thecubecast.ReEngine.Graphics.Draw;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import java.awt.*;
import java.util.ArrayList;

import static com.thecubecast.ReEngine.Data.GameStateManager.State.Blank;
import static com.thecubecast.ReEngine.Data.GameStateManager.State.SHADER;

public class GameStateManager {
	public boolean Debug = false;
	public float[] fpsLog = new float[999999];
	public int fpsIndex = 0;

    public enum State {
        INTRO, MENU, PLAY, LOADING, OPTIONS, TEST, SHADER, MULTIPLAYER, PLATFORMER, Blank, Dialog
    }

    public State newcurrentState;
    private State newpreviousState;

    private GameState gameState;

	public float CurrentTime;
	public float DeltaTime;
	
	//Public render function object
	public Draw Render;
	public int ticks = 0;

	//Public Audio handler
	public static SoundManager AudioM;

	public static controlerManager ctm;

	public Discord DiscordManager;

	//MousePos
	public int MouseX;
	public int MouseY;
	public int[] MouseDrag;
	public int[] MouseClick;
	public int OldCursor = 0;
	public int Cursor = 0;
	
	//screen
	public int Width;
	public int Height;
	public int Scale = 4;
	
	public GameStateManager() {

        ctm = new controlerManager();

		DiscordManager = new Discord("405784101245943810");

		Render = new Draw();
		AudioM = new SoundManager();

		AudioM.init();
		Render.Init();

		//setState(SHADER);
		LoadState("STARTUP"); //THIS IS THE STATE WERE WE START WHEN THE GAME IS RUN
		
	}
	
	public void LoadState(String LoadIt) {
		newpreviousState = newcurrentState;
		unloadState();
		newcurrentState = State.LOADING;
		//Set up the loading state 
		gameState = new LoadingState(this);
		((LoadingState) gameState).setLoad("STARTUP");
		gameState.init();
	}
	
	public void setState(State i) {
		newpreviousState = newcurrentState;
		unloadState();
        newcurrentState = i;
        switch (newcurrentState) {
            case INTRO:
                Common.print("Loaded state Intro");
                gameState = new IntroState(this);
                gameState.init();
                break;
            case MENU:
                Common.print("Loaded state MENU");
                gameState = new MainMenuState(this);
                gameState.init();
                break;
            case PLAY:
                Common.print("Loaded state PLAY");
                gameState = new PlayState(this);
                gameState.init();
                break;
			case LOADING:
				break;
			case OPTIONS:
				break;
			case TEST:
				gameState = new tempState(this);
				gameState.init();
                break;
            case SHADER:
                Common.print("Loaded state ShaderTest");
                gameState = new ShaderPipelineTestState(this);
                gameState.init();
                break;
            case MULTIPLAYER:
                Common.print("Loaded state MultiplayerTestState");
                gameState = new MultiplayerTestState(this);
                gameState.init();
                break;
			case PLATFORMER:
				Common.print("Loaded state PlatformerTestState");
				gameState = new PlatformerTestState(this);
				gameState.init();
				break;
			case Blank:
				Common.print("Loaded state blank");
				gameState = new blankTestState(this);
				gameState.init();
				break;
            case Dialog:
                Common.print("Loaded state Dialog");
                gameState = new DialogState(this);
                gameState.init();
                break;
        }
		
	}

	/**
	 * unloads the current state
	 * calls dispose on the current gamestate first
	 **/
	public void unloadState() {
		//Common.print("Unloaded state " + i);
		if(gameState != null)
			gameState.dispose();
		gameState = null;
	}
	
	public void update(int MousX, int MousY, int[] Draging, int[] MousCl) {
		ticks++;
		if (Cursor != OldCursor) {
			OldCursor = Cursor;
			com.badlogic.gdx.graphics.Cursor customCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor" + Cursor + ".png")), 0, 0);
    		Gdx.graphics.setCursor(customCursor);
		}
		MouseX = MousX;
		MouseY = MousY;
		MouseDrag = Draging;
		MouseClick = MousCl;
		if(gameState != null) {
			gameState.update();
		}
		//MouseClick[0] = 0;

		AudioM.update();

		DiscordManager.UpdatePresence();
		ctm.update();
	}
	
	public void draw(SpriteBatch bbg, int W, int H, float Time) {
		Width = W;
		Height = H;
		CurrentTime = Time;
		DeltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1f / 60f);
		if(gameState != null) {
			//Notice how the height and width are swapped, woops
			gameState.draw(bbg, H, W, Time);
		}

        //Gdx.graphics.setVSync(!Debug);

		if(Debug) {
            //Common.print("Render Calls: " + bbg.totalRenderCalls);
            //bbg.totalRenderCalls = 0;
        }

        /*
		fpsLog[fpsIndex] = Gdx.graphics.getFramesPerSecond();
		fpsIndex++;
		System.out.println(fpsLog[fpsIndex-1]);
		*/
	}
	
	public void reSize(SpriteBatch bbg, int H, int W) {
		if(gameState != null) {
			gameState.reSize(bbg, H, W);
		}
		Matrix4 matrix = new Matrix4();
		matrix.setToOrtho2D(0, 0, W, H);
		bbg.setProjectionMatrix(matrix);
		Height = H;
		Width = W;
	}

	public void dispose() {
		DiscordManager.dispose();
		gameState.dispose();
	}

	public void Shutdown() {
        gameState.dispose();
	    gameState.Shutdown();
    }
}

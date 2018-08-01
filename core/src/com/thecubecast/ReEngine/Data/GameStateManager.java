// The GameStateManager does exactly what its
// name says. It contains a list of GameStates.
// It decides which GameState to update() and
// draw() and handles switching between different
// GameStates.

package com.thecubecast.ReEngine.Data;

import com.thecubecast.ReEngine.GameStates.*;
import com.thecubecast.ReEngine.Graphics.Draw;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public class GameStateManager {
	public static boolean Debug = false;

    public enum State {
        INTRO, MENU, PLAY, LOADING
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

	//The cursor image
	public enum CursorType {
		Normal, Old, Question
	}

	public CursorType OldCursor = CursorType.Normal;
	public CursorType Cursor = CursorType.Normal;
	
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
			int CursorID = 0;
			switch (Cursor) {
				case Normal:
					CursorID = 0;
					break;
				case Old:
					CursorID = 1;
					break;
				case Question:
					CursorID = 2;
					break;
			}
			com.badlogic.gdx.graphics.Cursor customCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor" + CursorID + ".png")), 0, 0);
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

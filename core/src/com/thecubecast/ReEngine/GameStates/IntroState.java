// GameState that shows logo.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.ControlerManager;
import sun.security.ssl.Debug;

public class IntroState extends GameState {

    OrthographicCamera camera;

    private int alpha;
    private int ticks = 0;

    static final int WORLD_WIDTH = 100;
    static final int WORLD_HEIGHT = 100;

    Texture Splash;

    private final int FADE_IN = 20;
    private final int LENGTH = 40;
    private final int FADE_OUT = 20;

    public IntroState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        Splash = new Texture(Gdx.files.internal("Images/image_00.png"));

        camera = new OrthographicCamera();

        //JukeBox.load("/Music/bgmusic.wav", "LogoSound");
        //JukeBox.play("LogoSound");


    }

    public void update() {
        handleInput();
        ticks++;
        if (ticks < FADE_IN) {
            alpha = (int) (255 - 255 * (1.0 * ticks / FADE_IN));
            if (alpha < 0) alpha = 0;
        }
        if (ticks > FADE_IN + LENGTH) {
            alpha = (int) (255 * (1.0 * ticks - FADE_IN - LENGTH) / FADE_OUT);
            if (alpha > 255) alpha = 255;
        }
        if (ticks > FADE_IN + LENGTH + FADE_OUT) {
            //JukeBox.stop("LogoSound");
            if (Gdx.input.isKeyPressed(Keys.D))
                gsm.Debug = true;
            if (gsm.ctm.isButtonDown(0, ControlerManager.buttons.BUTTON_START) && gsm.ctm.isButtonDown(0, ControlerManager.buttons.BUTTON_BACK))
                gsm.Debug = true;
            if (gsm.Debug)
                Debug.println("Developer", "Debug set to true");
            gsm.setState(GameStateManager.State.MENU);
        }
    }

    public void draw(SpriteBatch g, int height, int width, float Time) {
        camera.setToOrtho(false, width, height);
        g.setProjectionMatrix(camera.combined);
        g.begin();
        Gdx.gl.glClearColor(255f, 255f, 255f, 1);

        g.draw(Splash, width / 2 - ((Splash.getWidth() * 0.5f) / 2), height / 2 - ((Splash.getHeight() * 0.5f) / 2), Splash.getWidth() * 0.5f, Splash.getHeight() * 0.5f);
        g.end();
    }

    public void drawUI(SpriteBatch g, int height, int width, float Time) {
        //Draws things on the screen, and not the world positions
        g.setProjectionMatrix(camera.combined);
        g.begin();
        //GUI must draw last
        g.end();
    }

    public void handleInput() {

        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) { //KeyHit
            //JukeBox.stop("LogoSound");
            Splash = null;
            gsm.setState(GameStateManager.State.MENU);
        }

    }

    @Override
    public void Shutdown() {

    }

}
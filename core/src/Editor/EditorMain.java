package Editor;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.SoundManager;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.thecubecast.ReEngine.Data.Common.GetMonitorSizeH;
import static com.thecubecast.ReEngine.Data.Common.GetMonitorSizeW;

public class EditorMain extends ApplicationAdapter implements InputProcessor {

    public static SoundManager AudioM;

    SpriteBatch batch;

    OrthographicCamera GuiCam;

    UIFSM UI;

    @Override
    public void create () { // INIT FUNCTION

        AudioM = new SoundManager();
        AudioM.init();

        Cursor customCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor0.png")), 0, 0);
        Gdx.graphics.setCursor(customCursor);

        GuiCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();

        UI = new UIFSM();

        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void render () { // UPDATE Runs every frame. 60FPS

        //Gdx.gl.glClearColor( 1, 1, 1, 1 );
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        UpdateInput();
        Update(); //UPDATE


        batch.setProjectionMatrix(GuiCam.combined);
        batch.begin();
        batch.draw(new Texture(Gdx.files.internal("Sprites/face.png")), 50, 50, 64 ,64);
        batch.end();

        UI.Draw();
    }

    public void UpdateInput(){

        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) { //KeyHit
            this.dispose();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.D)) { //KeyHit
            //Debug == true;
        }
    }

    public void Update() {
        if (Gdx.input.isTouched()) {
            GuiCam.translate(-Gdx.input.getDeltaX(),Gdx.input.getDeltaY());
        }

        GuiCam.update();
    }

    public void Draw(SpriteBatch bbg) {

    }

    @Override
    public void resize(int width, int height) {
        UI.reSize();
        Vector3 tempPos = GuiCam.position;
        GuiCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        GuiCam.position.set(tempPos);
    }


    @Override
    public void dispose () { //SHUTDOWN FUNCTION

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

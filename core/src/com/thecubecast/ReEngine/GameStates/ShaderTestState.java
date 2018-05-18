// GameState that tests new mechanics.

package com.thecubecast.ReEngine.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.controlerManager;
import com.thecubecast.ReEngine.worldObjects.Player;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.thecubecast.ReEngine.mainclass.MasterFBO;

public class ShaderTestState extends GameState {

    public class PipelineTexture {
        Texture Diffuse;
        Texture Normal;

    }

    private PipelineTexture[] Layer = new PipelineTexture[5];
    private List<PipelineTexture[]> RenderPipeline = new ArrayList<>();

    OrthographicCamera camera;

    Texture rock, rockNormals;

    private FrameBuffer fbo;
    private FrameBuffer fboNormals;

    ShaderProgram shader;
    ShaderProgram FILL;

    //our constants...
    public static final float DEFAULT_LIGHT_Z = 0.2f;
    public static final float AMBIENT_INTENSITY = 0.75f;
    public static final float LIGHT_INTENSITY = 4f;
    public static final Vector3 LIGHT_POS = new Vector3(0f,0f,DEFAULT_LIGHT_Z);
    //Light RGB and intensity (alpha)
    public static final Vector3 LIGHT_COLOR = new Vector3(1f, 1f, 0.7f);
    //Ambient RGB and intensity (alpha)
    public static final Vector3 AMBIENT_COLOR = new Vector3(0.6f, 0.6f, 1f);
    //Attenuation coefficients for light falloff
    public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 15f);


    public ShaderTestState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()/gsm.Scale, Gdx.graphics.getHeight()/4, false);
        fboNormals = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()/gsm.Scale, Gdx.graphics.getHeight()/4,false);

        //SETUP CAMERA SPRITEBATCH AND MENU
        camera = new OrthographicCamera();
        camera.position.set(0, 0, camera.position.z);

        rock = new Texture(Gdx.files.internal("rock.png"));
        rockNormals = new Texture(Gdx.files.internal("rock_n.png"));

        setupShaders();

        //handle mouse wheel
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean scrolled(int delta) {
                //LibGDX mouse wheel is inverted compared to lwjgl-basics
                LIGHT_POS.z = Math.max(0f, LIGHT_POS.z - (delta * 0.005f));
                System.out.println("New light Z: "+LIGHT_POS.z);
                return true;
            }
        });

    }

    public void update() {
        handleInput();

        camera.update();

    }

    public void draw(SpriteBatch g, int height, int width, float Time) {
        Gdx.gl.glClearColor(0/255f, 0/255f, 45/255f, 1);
        RenderCam();

        camera.setToOrtho(false, width, height);
        g.setProjectionMatrix(camera.combined);

        shader.begin();
        shader.setUniformf("Resolution", width/gsm.Scale, height/gsm.Scale);
        shader.end();

        //update light position, normalized to screen resolution
        float light_x = Gdx.input.getX() / (float)gsm.Width;
        float light_y = Gdx.input.getY() / (float)gsm.Height;

        LIGHT_POS.x = light_x;
        LIGHT_POS.y = 4 - light_y;

        //send a Vector4f to GLSL
        shader.begin();
        shader.setUniformf("LightPos", LIGHT_POS);
        shader.end();

        //THIS IS THE DIFFUSION ------------------------------------------------------------------------------
        fbo.bind();
        fbo.begin();
        g.begin();
        g.setShader(null);

        for(int x = 0; x < 20; x++) {
            for(int y = 0; y < 12; y++) {
                g.draw(rock, x*16, y*16);
            }
        }

        g.end();
        fbo.end();
        //THIS IS THE DIFFUSION ------------------------------------------------------------------------------

        //THIS IS THE NORMALS ------------------------------------------------------------------------------
        fboNormals.bind();
        fboNormals.begin();
        g.begin();
        g.setShader(null);

        for(int x = 0; x < 15; x++) {
            for(int y = 0; y < 12; y++) {
                g.draw(rockNormals, x*16, y*16);
            }
        }
        g.end();
        fboNormals.end();

            //This fills in the blank space
            fboNormals.begin();
            g.begin();
            g.setShader(FILL);
            g.draw(fboNormals.getColorBufferTexture(),0,fboNormals.getHeight(), fboNormals.getWidth(), -fboNormals.getHeight());
            g.end();
            fboNormals.end();
            g.setShader(null);
        //THIS IS THE NORMALS ------------------------------------------------------------------------------

        MasterFBO.bind();
        MasterFBO.begin();

        g.begin();
        g.setShader(shader);
        //bind normal map to texture unit 1
        fboNormals.getColorBufferTexture().bind(1);
        fbo.getColorBufferTexture().bind(0);
        g.draw(fbo.getColorBufferTexture(),0, height, width, -height);

        //Draw any GUI or menus after this line

        g.setShader(null);
        g.end();


    }


    private void handleInput() {

    }

    public void RenderCam() {
        camera.update();
    }


    public void reSize(SpriteBatch g, int H, int W) {
        float posX = camera.position.x;
        float posY = camera.position.y;
        float posZ = camera.position.z;
        camera.setToOrtho(false);
        camera.position.set(posX, posY, posZ);

        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, W, H);
    }

    public void setupShaders() {
        FILL = new ShaderProgram(Gdx.files.internal("Shaders/Normals_Fill/vertex.glsl").readString(), Gdx.files.internal("Shaders/Normals_Fill/fragment.glsl").readString());

        shader = new ShaderProgram(Gdx.files.internal("Shaders/Shadow/vertex.glsl").readString(), Gdx.files.internal("Shaders/Shadow/fragment.glsl").readString());

        //setup default uniforms
        shader.begin();

        //our normal map
        shader.setUniformi("u_normals", 1); //GL_TEXTURE1

        //light/ambient colors
        //LibGDX doesn't have Vector4 class at the moment, so we pass them individually...
        shader.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
        shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
        shader.setUniformf("Falloff", FALLOFF);

        //LibGDX likes us to end the shader program
        shader.end();
    }

    @Override
    public void Shutdown() {

    }

}
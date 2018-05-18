package com.thecubecast.ReEngine.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class RePipeline {

    /*
    Needs everything the normal batch has
    except we draw everything at the end
    all textures must be a PipelineTexture
     */

    public enum drawingTypes {
        none,diffuse,normal,lighting,export
    }

    public drawingTypes CurrentlyDrawing;

    public static class RePipeTextureRegionList extends RePipeTextureDrawable {
        private PipelineTextureRegion PipeTexturere;

        @Override
        public void DrawDiffuse(SpriteBatch batch) {
            batch.draw(PipeTexturere.getDiffuse(), x, y, OriginX, OriginY, width, height, ScaleX, ScaleY, rotation);
        }

        @Override
        public void DrawNormal(SpriteBatch batch) {
            batch.draw(PipeTexturere.getNormal(), x, y, OriginX, OriginY, width, height, ScaleX, ScaleY, rotation);
        }
    }

    public class Light {
        private Vector3 Position;
        private float Intensity;
        private Vector3 Color;
        private Vector3 Falloff;

        public Light (float x, float y, Vector3 Color) {
            this.Position = new Vector3(x,y, 0.2f);
            this.Intensity = 4f;
            this.Color = Color;
            this.Falloff = new Vector3(.4f, 3f, 15f);;
        }

        public Light (float x, float y, float z, Vector3 Color) {
            this.Position = new Vector3(x,y, z);
            this.Intensity = 4f;
            this.Color = Color;
            this.Falloff = new Vector3(.4f, 3f, 15f);;
        }

        public Light (float x, float y, float Intensity, Vector3 Color, Vector3 Falloff) {
            this.Position = new Vector3(x,y, 0.2f);
            this.Intensity = Intensity;
            this.Color = Color;
            this.Falloff = Falloff;
        }

        public Light (float x, float y, float z, float Intensity, Vector3 Color, Vector3 Falloff) {
            this.Position = new Vector3(x,y,z);
            this.Intensity = Intensity;
            this.Color = Color;
            this.Falloff = Falloff;
        }

    }

    public List<RePipeLayer> Layers = new ArrayList<>();
    private List<Light> LightList = new ArrayList<>();

    private int Height, Width, Scale = 4;

    private SpriteBatch batch;
    private SpriteBatch MyBatch;

    //The FBOs that are responsible for parts of the render process
    private FrameBuffer fbo; //the Diffuse map
    private FrameBuffer fboNormals; //the Normal map
    private FrameBuffer fboLighting; // The lighting map
    private FrameBuffer Export; //The texture that gets exported!

    private Texture fboT;
    private Texture fboNormalsT;
    private Texture fboLightingT;
    private Texture ExportT;

    private boolean drawing = false;

    //------------------------------------------------------------------------------------------
    private ShaderProgram shader;

    //our constants...
    public static final float DEFAULT_LIGHT_Z = 0.2f;
    public static final float AMBIENT_INTENSITY = 0.80f;
    public static final float LIGHT_INTENSITY = 2f;
    public static final Vector3 LIGHT_POS = new Vector3(0f,0f,DEFAULT_LIGHT_Z);
    //Light RGB and intensity (alpha)
    public static final Vector3 LIGHT_COLOR = new Vector3(1f, 1f, 0.7f);
    //Ambient RGB and intensity (alpha)
    public static final Vector3 AMBIENT_COLOR = new Vector3(0.6f, 0.6f, 1f);
    //Attenuation coefficients for light falloff
    public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 15f);
    //------------------------------------------------------------------------------------------

    private ShaderProgram FILL;

    public RePipeline() {
        Width = Gdx.graphics.getWidth()/Scale;
        Height = Gdx.graphics.getHeight()/Scale;

        MyBatch = new SpriteBatch();

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Width, Height, false);
        fboNormals = new FrameBuffer(Pixmap.Format.RGBA8888, Width, Height,false);
        fboLighting = new FrameBuffer(Pixmap.Format.RGBA8888, Width, Height,false);


        Export = new FrameBuffer(Pixmap.Format.RGBA8888, Width, Height,false);

        FILL = new ShaderProgram(Gdx.files.internal("Shaders/Normals_Fill/vertex.glsl").readString(), Gdx.files.internal("Shaders/Normals_Fill/fragment.glsl").readString());

        shader = new ShaderProgram(Gdx.files.internal("Shaders/lights/vertex.glsl").readString(), Gdx.files.internal("Shaders/lights/fragment_toon.glsl").readString());

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
        CurrentlyDrawing = drawingTypes.none;
    }

    public void begin(SpriteBatch batch) {
        if (drawing) throw new IllegalStateException("RePipeline.end must be called before begin.");

        this.batch = batch;

        addLayer(1);

        drawing = true;
    }

    public int addLayer(float z) {
        RePipeLayer temp = new RePipeLayer();
        temp.zLayer = z;
        Layers.add(temp);
        return Layers.size()-1;
    }

    public void draw(PipelineTexture texture, float x, float y, int layer) {
        RePipeTextureDrawable temp = new RePipeTextureDrawable() {};
        temp.PipeTexture = texture;
        temp.x = x;
        temp.y = y;
        temp.OriginX = 0;
        temp.OriginY = 0;
        temp.width = texture.getDiffuse().getWidth();
        temp.height =  texture.getDiffuse().getHeight();
        temp.ScaleX = 1;
        temp.ScaleY = 1;
        temp.rotation = 0;
        Layers.get(layer).SpriteList.add(temp);
    }

    public void draw(PipelineTexture texture, float x, float y, int layer, int width, int height) {
        RePipeTextureDrawable temp = new RePipeTextureDrawable(){};
        temp.PipeTexture = texture;
        temp.x = x;
        temp.y = y;
        temp.OriginX = 0;
        temp.OriginY = 0;
        temp.width = width;
        temp.height =  height;
        temp.ScaleX = 1;
        temp.ScaleY = 1;
        temp.rotation = 0;
        Layers.get(layer).SpriteList.add(temp);
    }

    public void draw(PipelineTextureRegion texture, float x, float y, int layer) {
        RePipeTextureRegionList temp = new RePipeTextureRegionList();
        temp.PipeTexturere = texture;
        temp.x = x;
        temp.y = y;
        temp.OriginX = 0;
        temp.OriginY = 0;
        temp.width = texture.getDiffuse().getRegionWidth();
        temp.height =  texture.getDiffuse().getRegionHeight();
        temp.ScaleX = 1;
        temp.ScaleY = 1;
        temp.rotation = 0;
        Layers.get(layer).SpriteList.add(temp);
    }

    public void draw(PipelineTextureRegion texture, float x, float y, int layer, int width, int height) {
        RePipeTextureRegionList temp = new RePipeTextureRegionList();
        temp.PipeTexturere = texture;
        temp.x = x;
        temp.y = y;
        temp.OriginX = 0;
        temp.OriginY = 0;
        temp.width = width;
        temp.height =  height;
        temp.ScaleX = 1;
        temp.ScaleY = 1;
        temp.rotation = 0;
        Layers.get(layer).SpriteList.add(temp);
    }

    public void draw(PipelineTexture texture, float x, float y) {
        RePipeTextureDrawable temp = new RePipeTextureDrawable() {};
        temp.PipeTexture = texture;
        temp.x = x;
        temp.y = y;
        temp.OriginX = 0;
        temp.OriginY = 0;
        temp.width = texture.getDiffuse().getWidth();
        temp.height =  texture.getDiffuse().getHeight();
        temp.ScaleX = 1;
        temp.ScaleY = 1;
        temp.rotation = 0;
        Layers.get(0).SpriteList.add(temp);
    }

    public void draw(PipelineTexture texture, float x, float y, int width, int height) {
        RePipeTextureDrawable temp = new RePipeTextureDrawable(){};
        temp.PipeTexture = texture;
        temp.x = x;
        temp.y = y;
        temp.OriginX = 0;
        temp.OriginY = 0;
        temp.width = width;
        temp.height =  height;
        temp.ScaleX = 1;
        temp.ScaleY = 1;
        temp.rotation = 0;
        Layers.get(0).SpriteList.add(temp);
    }

    public void draw(PipelineTextureRegion texture, float x, float y) {
        RePipeTextureRegionList temp = new RePipeTextureRegionList();
        temp.PipeTexturere = texture;
        temp.x = x;
        temp.y = y;
        temp.OriginX = 0;
        temp.OriginY = 0;
        temp.width = texture.getDiffuse().getRegionWidth();
        temp.height =  texture.getDiffuse().getRegionHeight();
        temp.ScaleX = 1;
        temp.ScaleY = 1;
        temp.rotation = 0;
        Layers.get(0).SpriteList.add(temp);
    }

    public void draw(PipelineTextureRegion texture, float x, float y, int width, int height) {
        RePipeTextureRegionList temp = new RePipeTextureRegionList();
        temp.PipeTexturere = texture;
        temp.x = x;
        temp.y = y;
        temp.OriginX = 0;
        temp.OriginY = 0;
        temp.width = width;
        temp.height =  height;
        temp.ScaleX = 1;
        temp.ScaleY = 1;
        temp.rotation = 0;
        Layers.get(0).SpriteList.add(temp);
    }

    /**
     * Responsible for ending the collection of drawing
     **/
    public Texture end() {

        if (!drawing) throw new IllegalStateException("RePipeline.begin must be called before end.");
        drawing = false;

        RePipeLayerSort sort2 = new RePipeLayerSort();
        Layers.sort(sort2);
        PipelineTextureSort temp = new PipelineTextureSort();
        for(int i = 0; i < Layers.size(); i++) {
            Layers.get(i).SpriteList.sort(temp);
        }

        shader.begin();
        shader.setUniformf("Resolution", Width/Scale, Height/Scale);
        shader.end();

        //update light position, normalized to screen resolution
        float light_x = Gdx.input.getX() / (float)Width;
        float light_y = Gdx.input.getY() / (float)Height;

        LIGHT_POS.x = light_x;
        LIGHT_POS.y = 4 - light_y;

        //send a Vector4f to GLSL
        shader.begin();
        shader.setUniformf("LightPos", LIGHT_POS);
        shader.end();

        //THIS IS THE DIFFUSION ------------------------------------------------------------------------------
        fbo.bind();
        fbo.begin();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawTiledMapDiffuse();

        CurrentlyDrawing = drawingTypes.diffuse;
        batch.begin();
        batch.setShader(null);

        //This loops through the objects in the draw list
        for(int i = 0; i < Layers.size(); i++) {
            for (int x = 0; x < Layers.get(i).SpriteList.size(); x++) {
                Layers.get(i).SpriteList.get(x).DrawDiffuse(batch);
                //batch.draw(SpriteList.get(x).getDrawable().getDiffuse(), SpriteList.get(x).x, SpriteList.get(x).y, SpriteList.get(x).OriginX, SpriteList.get(x).OriginY, SpriteList.get(x).width, SpriteList.get(x).height, SpriteList.get(x).ScaleX, SpriteList.get(x).ScaleY, SpriteList.get(x).rotation);
            }
        }

        batch.end();
        fbo.end();
        CurrentlyDrawing = drawingTypes.none;
        //THIS IS THE DIFFUSION ------------------------------------------------------------------------------
        fboT = fbo.getColorBufferTexture();


        //THIS IS THE NORMALS ------------------------------------------------------------------------------
        fboNormals.bind();
        fboNormals.begin();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawTiledMapNormals();

        CurrentlyDrawing = drawingTypes.normal;
        batch.begin();
        batch.setShader(null);

        for(int i = 0; i < Layers.size(); i++) {
            for (int x = 0; x < Layers.get(i).SpriteList.size(); x++) {
                Layers.get(i).SpriteList.get(x).DrawNormal(batch);
                //batch.draw(SpriteList.get(x).getDrawable().getNormal(), SpriteList.get(x).x, SpriteList.get(x).y, SpriteList.get(x).OriginX, SpriteList.get(x).OriginY, SpriteList.get(x).width, SpriteList.get(x).height, SpriteList.get(x).ScaleX, SpriteList.get(x).ScaleY, SpriteList.get(x).rotation);
            }
        }

        batch.end();
        fboNormals.end();
        CurrentlyDrawing = drawingTypes.none;

        //This fills in the blank space
        /*fboNormals.begin();
        batch.begin();
        batch.setShader(FILL);
        batch.draw(fboNormals.getColorBufferTexture(),0,fboNormals.getHeight(), fboNormals.getWidth(), -fboNormals.getHeight());
        batch.end();
        fboNormals.end();
        batch.setShader(null);*/
        //THIS IS THE NORMALS ------------------------------------------------------------------------------
        fboNormalsT = fboNormals.getColorBufferTexture();


        //THIS IS THE LIGHTING------------------------------------------------------------------------------
        fboLighting.bind();
        fboLighting.begin();
        CurrentlyDrawing = drawingTypes.lighting;
            MyBatch.begin();
            MyBatch.setShader(shader);

            //bind normal map to texture unit 1
            fboNormals.getColorBufferTexture().bind(1);
            fbo.getColorBufferTexture().bind(0);
            MyBatch.draw(fbo.getColorBufferTexture(),0, fbo.getHeight()*Scale, fbo.getWidth()*Scale, -fbo.getHeight()*Scale);

            MyBatch.setShader(null);
            MyBatch.end();
        fboLighting.end();
        CurrentlyDrawing = drawingTypes.none;
        //THIS IS THE LIGHTING------------------------------------------------------------------------------
        fboLightingT = fboLighting.getColorBufferTexture();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Export.bind();
        Export.begin();
        CurrentlyDrawing = drawingTypes.export;
            batch.begin();

            //batch.draw(fbo.getColorBufferTexture(),0, Height, Width, -Height);
            batch.draw(fboLighting.getColorBufferTexture(),0, Height*Scale, Width*Scale, -Height*Scale);

            batch.setShader(null);
            batch.end();
        Export.end();
        CurrentlyDrawing = drawingTypes.none;

        //Empty the SpriteList for the next frame
        Layers.clear();

        return Export.getColorBufferTexture();
        
    }

    public Texture applyShadertoTexture(Texture texture, ShaderProgram shader) {
        FrameBuffer ExportTex = new FrameBuffer(Pixmap.Format.RGBA8888, texture.getWidth(), texture.getHeight(), false);

        ExportTex.bind();
        ExportTex.begin();
        MyBatch.begin();
        MyBatch.setShader(shader);
        MyBatch.draw(texture,0,0);
        MyBatch.setShader(null);
        MyBatch.end();
        ExportTex.end();

        return ExportTex.getColorBufferTexture();
    }

    public Texture applyShadertoTexture(TextureRegion texture, ShaderProgram shader) {
        FrameBuffer ExportTex = new FrameBuffer(Pixmap.Format.RGBA8888, texture.getRegionWidth(), texture.getRegionHeight(), false);

        ExportTex.bind();
        ExportTex.begin();
        MyBatch.begin();
        MyBatch.setShader(shader);
        MyBatch.draw(texture,0,0);
        MyBatch.setShader(null);
        MyBatch.end();
        ExportTex.end();

        return ExportTex.getColorBufferTexture();
    }

    public Texture getFboT() {
        return fboT;
    }

    public Texture getFboNormalsT() {
        return fboNormalsT;
    }

    public Texture getFboLightingT() {
        return fboLightingT;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void drawTiledMapDiffuse() {

    }

    public void drawTiledMapNormals() {

    }

}

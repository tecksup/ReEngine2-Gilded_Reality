package com.thecubecast.ReEngine.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PipelineTextureRegion {
    private TextureRegion Diffuse;
    private TextureRegion Normal;

    public PipelineTextureRegion (TextureRegion Diffuse) {
        this.Diffuse = Diffuse;

        Texture normalmap = new Texture(Gdx.files.internal("Sprites/Empty_Normal.png"));
        this.Normal = new TextureRegion(normalmap);
    }

    public PipelineTextureRegion (TextureRegion Diffuse, TextureRegion Normal) {
        this.Diffuse = Diffuse;
        this.Normal = Normal;
    }

    /**
     * Only use this if you plan on setting the textures through setDiffuse
     **/
    public PipelineTextureRegion () {
    }

    public PipelineTextureRegion (String Diffuse, int col, int row) {
        Texture Diffusemap = new Texture(Gdx.files.internal(Diffuse));
        this.Diffuse = new TextureRegion(Diffusemap);

        Texture normalmap = new Texture(Gdx.files.internal("Sprites/Empty_Normal.png"));
        this.Normal = new TextureRegion(normalmap);
    }

    public PipelineTextureRegion (String Diffuse, String Normal, int col, int row) {
        Texture Diffusemap = new Texture(Gdx.files.internal(Diffuse));
        this.Diffuse = new TextureRegion(Diffusemap);

        Texture normalmap = new Texture(Gdx.files.internal("Sprites/Empty_Normal.png"));
        this.Normal = new TextureRegion(normalmap);
    }

    public TextureRegion getDiffuse() {
        return Diffuse;
    }

    public void setDiffuse(TextureRegion diffuse) {
        Diffuse = diffuse;
    }

    public TextureRegion getNormal() {
        return Normal;
    }

    public void setNormal(TextureRegion normal) {
        Normal = normal;
    }
}

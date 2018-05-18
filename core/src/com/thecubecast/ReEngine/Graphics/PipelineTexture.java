package com.thecubecast.ReEngine.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class PipelineTexture {
    private Texture Diffuse;
    private Texture Normal;

    public PipelineTexture (Texture Diffuse) {
        this.Diffuse = Diffuse;
        Normal = new Texture(Gdx.files.internal("Sprites/Empty_Normal.png"));
    }

    public PipelineTexture (Texture Diffuse, Texture Normal) {
        this.Diffuse = Diffuse;
        this.Normal = Normal;
    }

    /**
     * Only use this if you plan on setting the textures through setDiffuse
    **/
    public PipelineTexture () {
    }

    public PipelineTexture (String Diffuse) {
        this.Diffuse = new Texture(Gdx.files.internal(Diffuse));
        this.Normal = new Texture(Gdx.files.internal("Sprites/Empty_Normal.png"));
    }

    public PipelineTexture (String Diffuse, String Normal) {
        this.Diffuse = new Texture(Gdx.files.internal(Diffuse));
        this.Normal = new Texture(Gdx.files.internal(Normal));
    }

    public Texture getDiffuse() {
        return Diffuse;
    }

    public void setDiffuse(Texture diffuse) {
        Diffuse = diffuse;
    }

    public Texture getNormal() {
        return Normal;
    }

    public void setNormal(Texture normal) {
        Normal = normal;
    }
}

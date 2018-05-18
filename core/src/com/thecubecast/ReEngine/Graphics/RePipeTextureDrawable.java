package com.thecubecast.ReEngine.Graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RePipeTextureDrawable {
    public PipelineTexture PipeTexture;
    public float x = 0;
    public float y = 0;
    int OriginX;
    int OriginY;
    float width;
    float height;
    int ScaleX;
    int ScaleY;
    float rotation;

    public void DrawDiffuse(SpriteBatch batch) {
        //PipelineTextureRegion temp = new PipelineTextureRegion(new TextureRegion(PipeTexture.getDiffuse()), new TextureRegion(PipeTexture.getNormal()));
        batch.draw(PipeTexture.getDiffuse(), x, y, width, height);
    }

    public void DrawNormal(SpriteBatch batch) {
        //PipelineTextureRegion temp = new PipelineTextureRegion(new TextureRegion(PipeTexture.getDiffuse()), new TextureRegion(PipeTexture.getNormal()));
        batch.draw(PipeTexture.getNormal(), x, y, width, height);
    }
}

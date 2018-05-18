package com.thecubecast.ReEngine.Graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RePipeTextureRegionDrawable extends RePipeTextureDrawable {
    public PipelineTextureRegion PipeTexture;

    public void DrawDiffuse(SpriteBatch batch) {
        //PipelineTextureRegion temp = new PipelineTextureRegion(new TextureRegion(PipeTexture.getDiffuse()), new TextureRegion(PipeTexture.getNormal()));
        batch.draw(PipeTexture.getDiffuse(), x, y, width, height);
    }

    public void DrawNormal(SpriteBatch batch) {
        //PipelineTextureRegion temp = new PipelineTextureRegion(new TextureRegion(PipeTexture.getDiffuse()), new TextureRegion(PipeTexture.getNormal()));
        batch.draw(PipeTexture.getNormal(), x, y, width, height);
    }
}

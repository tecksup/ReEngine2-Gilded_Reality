package com.thecubecast.ReEngine.worldObjects.EntityPrefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.collision;
import com.thecubecast.ReEngine.Graphics.PipelineTextureRegion;
import com.thecubecast.ReEngine.Graphics.RePipeTextureRegionDrawable;
import com.thecubecast.ReEngine.Graphics.RePipeline;
import com.thecubecast.ReEngine.Graphics.Scene2D.Dialog;
import com.thecubecast.ReEngine.Graphics.Scene2D.TkLabel;
import com.thecubecast.ReEngine.worldObjects.NPC;

import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.OutlineShader;
import static com.thecubecast.ReEngine.Graphics.Draw.loadAnim;
import static com.thecubecast.ReEngine.Graphics.Draw.setOutlineShaderColor;

public class Hank extends NPC {

    Texture sprite;
    Texture Exclamation = new Texture(Gdx.files.internal("Sprites/Yellow_Marker.png"));

    private Animation<TextureRegion> idle;
    TkLabel NameLabel;
    Group stage;
    ProgressBar HealthBar;

    public Hank(int x, int y) {
        super("[YELLOW]H[GREEN]a[BLUE]n[RED]k", x, y, new Vector3(32, 32, 4), .1f, 100);

        FocusStrength = 0.35f;

        idle = new Animation<TextureRegion>(0.1f, loadAnim(sprite, "Sprites/8direct/south.png", 4, 1));
        Skin skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
        skin.getFont("Mecha").getData().markupEnabled = true;
        skin.getFont("Pixel").getData().markupEnabled = true;

        stage = new Group();

        NameLabel = new TkLabel(getName(),skin);
        HealthBar = new ProgressBar(0f, 10f, 0.1f, false, skin, "Health_npc");
        HealthBar.setValue(getHealth()/10);
        HealthBar.setWidth(40);
        stage.addActor(NameLabel);
        stage.addActor(HealthBar);
    }

    @Override
    public void draw(RePipeline batch, float Time) {

        TextureRegion currentFrame = idle.getKeyFrame(Time, true);

        batch.draw(new PipelineTextureRegion(currentFrame), getPosition().x-6, getPosition().y-4);

    }

    @Override
    public void drawHighlight(RePipeline batch, float Time) {
        TextureRegion currentFrame = idle.getKeyFrame(Time, true);

        setOutlineShaderColor(Color.YELLOW, 0.8f);

        RePipeTextureRegionDrawable temp = new RePipeTextureRegionDrawable() {
            @Override
            public void DrawDiffuse(SpriteBatch batch) {
                batch.setShader(OutlineShader);
                batch.draw(currentFrame, x, y);
                batch.setShader(null);
            }

            @Override
            public void DrawNormal(SpriteBatch batch) {

            }
        };
        temp.x = getPosition().x - 6;
        temp.y = getPosition().y - 4;

        batch.Layers.get(0).SpriteList.add(temp);

    }

    @Override
    public void drawGui(SpriteBatch batch, float Time) {
        stage.draw(batch, 1);
        batch.draw(Exclamation, (int) getPosition().x + 6, (int) getPosition().y+63 + (float) (Math.sin(Time)*2));
    }

    @Override
    public void interact() {

    }

    @Override
    public void update(float delta, List<collision> Colls) {
        if (Colls == null) {
            return;
        }
        for(int i = 0; i < Colls.size(); i++) {
            if (Colls.get(i).getHash() == this.hashCode()) {
                //Rectangle hankbox = new Rectangle();
                //Colls.get(i).setRect(hankbox);
            }
        }
        super.update(delta, Colls);
        stage.act(Gdx.graphics.getDeltaTime());
        NameLabel.setText(getName());
        NameLabel.setPosition((int) getPosition().x + 3, (int) getPosition().y+50);
        HealthBar.setValue(getHealth()/10);
        HealthBar.setPosition((int) getPosition().x + 15 - (HealthBar.getWidth()/2), (int) getPosition().y+44);
    }
}

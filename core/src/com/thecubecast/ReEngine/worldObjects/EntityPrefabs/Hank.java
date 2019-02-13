package com.thecubecast.ReEngine.worldObjects.EntityPrefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.thecubecast.ReEngine.Data.Cube;
import com.thecubecast.ReEngine.Graphics.Scene2D.TkLabel;
import com.thecubecast.ReEngine.worldObjects.NPC;

import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.loadAnim;

public class Hank extends NPC {

    Texture sprite;
    Texture Exclamation = new Texture(Gdx.files.internal("Sprites/Yellow_Marker.png"));

    private Animation<TextureRegion> idle;
    TkLabel NameLabel;
    Group stage;
    ProgressBar HealthBar;

    public Hank(int x, int y, int z) {
        super("[YELLOW]H[GREEN]a[BLUE]n[RED]k", x, y, z, new Vector3(32, 32, 4), .1f, 100);

        FocusStrength = 0.15f;

        idle = new Animation<TextureRegion>(0.1f, loadAnim(sprite, "Sprites/8direct/south.png", 4, 1));
        Skin skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
        skin.getFont("Mecha").getData().markupEnabled = true;
        skin.getFont("Pixel").getData().markupEnabled = true;

        stage = new Group();

        NameLabel = new TkLabel(getName(), skin);
        HealthBar = new ProgressBar(0f, 10f, 0.1f, false, skin, "Health_npc");
        HealthBar.setValue(getHealth() / 10);
        HealthBar.setWidth(40);
        stage.addActor(NameLabel);
        stage.addActor(HealthBar);
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

        TextureRegion currentFrame = idle.getKeyFrame(Time, true);

        if (System.nanoTime() / 1000000 - getLastDamagedTime() < 1000) {

            batch.draw(new TextureRegion(currentFrame), getPosition().x - 6, getPosition().y - 4);

        } else {
            batch.draw(new TextureRegion(currentFrame), getPosition().x - 6, getPosition().y - 4);
        }

    }

    @Override
    public void drawHighlight(SpriteBatch batch, float Time) {
        TextureRegion currentFrame = idle.getKeyFrame(Time, true);

        //setOutlineShaderColor(Color.YELLOW, 0.8f);

        //batch.setShader(OutlineShader);
        //batch.draw(currentFrame, getPosition().x-6, getPosition().y-4);
        //batch.setShader(null);

    }

    @Override
    public void drawGui(SpriteBatch batch, float Time) {
        stage.draw(batch, 1);
        batch.draw(Exclamation, (int) getPosition().x + 6, (int) getPosition().y + 63 + (float) (Math.sin(Time) * 2));
    }

    @Override
    public void interact() {

    }

    @Override
    public void update(float delta, List<Cube> Colls) {
        if (Colls == null) {
            return;
        }
        for (int i = 0; i < Colls.size(); i++) {
            if (Colls.get(i).getHash() == this.hashCode()) {
                //Rectangle hankbox = new Rectangle();
                //Colls.get(i).setRect(hankbox);
            }
        }
        super.update(delta, Colls);
        stage.act(Gdx.graphics.getDeltaTime());
        NameLabel.setText(getName());
        NameLabel.setPosition((int) getPosition().x + 3, (int) getPosition().y + 50);
        HealthBar.setValue(getHealth() / 10);
        HealthBar.setPosition((int) getPosition().x + 15 - (HealthBar.getWidth() / 2), (int) getPosition().y + 44);
    }
}

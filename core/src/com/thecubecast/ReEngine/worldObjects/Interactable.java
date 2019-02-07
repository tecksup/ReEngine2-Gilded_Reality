package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.ReEngine.Data.Cube;
import com.thecubecast.ReEngine.Data.Item;
import com.thecubecast.ReEngine.GameStates.GameState;
import com.thecubecast.ReEngine.GameStates.PlayState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.thecubecast.ReEngine.Data.GameStateManager.ItemPresets;
import static com.thecubecast.ReEngine.GameStates.PlayState.Entities;
import static com.thecubecast.ReEngine.GameStates.PlayState.player;
import static com.thecubecast.ReEngine.Graphics.Draw.OutlineShader;
import static com.thecubecast.ReEngine.Graphics.Draw.setOutlineShaderColor;

public class Interactable extends Trigger{

    public Texture Image;

    public String ID = "None";
    public String Name = "";
    public String Description = "";

    public List<Item> Drops = new ArrayList<>();

    public boolean Collision;

    public boolean Highlight = false;
    public Color HighlightColor = Color.YELLOW;

    public String TexLocation = "";

    public Interactable(int x, int y, int z, Vector3 size, type State, boolean collision) {
        super(x,y,z,size,State,collision,"", TriggerType.None);
        this.Collision = collision;
    }

    public Interactable(int x, int y, int z, Vector3 size, type State, boolean collision, String RawEvents, TriggerType TType) {
        super(x,y,z,size,State,collision,RawEvents, TType);
        this.Collision = collision;
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, List<Cube> Colls) {

    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        if (Highlight) {
            batch.flush();
            batch.setShader(OutlineShader);
            setOutlineShaderColor(this.HighlightColor, 0.8f);
            batch.draw(Image, getPosition().x, getPosition().y);
            batch.setShader(null);
        } else {
            batch.draw(Image, getPosition().x, getPosition().y);
        }
    }

    /**
     * This will give you the area of the sprite, instead of the objects hitbox
     * @return the hitbox of the Sprite
     */
    public BoundingBox getImageHitbox() {

        if (Image != null) {
            BoundingBox temp = new BoundingBox(this.getPosition(), new Vector3(Image.getWidth(), Image.getHeight(), 0).add(this.getPosition()));
            return temp;
        }  else {
            return null;
        }
    }

    public void Activated() {

    }

    public String getTexLocation() {
        return TexLocation;
    }

    public void setTexLocation(String texLocation) {
        TexLocation = texLocation;
        Image = new Texture(Gdx.files.internal(TexLocation));
    }
}

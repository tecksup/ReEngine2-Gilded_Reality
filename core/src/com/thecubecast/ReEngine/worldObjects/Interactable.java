package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.ReEngine.Data.Cube;

import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.OutlineShader;
import static com.thecubecast.ReEngine.Graphics.Draw.setOutlineShaderColor;

public class Interactable extends WorldObject {

    public boolean Highlight = false;
    public Color HighlightColor = Color.YELLOW;

    public Interactable(int x, int y, int z, Vector3 size, type State, boolean collision) {
        super(x,y,z,size,State,collision);
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, List<Cube> Colls) {

    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

    }

    /**
     * This will give you the area of the sprite, instead of the objects hitbox
     * @return the hitbox of the Sprite
     */
    public BoundingBox getImageHitbox() {
        return null;
    }

    public void Activated() {

    }
}

package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.Cube;
import com.thecubecast.ReEngine.Data.Item;

import java.util.List;

public class WorldItem extends WorldObject {

    public Item item;
    private Texture ItemImage;

    public WorldItem(int x, int y, int z, Item item) {
        super(x,y,z, new Vector3(4,4,4));
        this.item = item;
        ItemImage = new Texture(Gdx.files.internal(item.getTexLocation()));
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, List<Cube> Colls) {

    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        batch.draw(ItemImage, getPosition().x, getPosition().y + getPosition().z/2, getSize().x, getSize().y);
    }
}

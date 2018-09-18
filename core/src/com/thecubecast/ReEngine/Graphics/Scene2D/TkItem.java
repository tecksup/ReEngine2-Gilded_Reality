package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.thecubecast.ReEngine.Data.Item;

import static com.thecubecast.ReEngine.Data.GameStateManager.AudioM;

public class TkItem extends Stack {

    private Item item;

    Table LabelTable;

    Image Icons;
    Label Quant;

    public TkItem(Skin skin, Item item) {

        super();

        this.item = item;

        LabelTable = new Table();

        if (item == null) {
            Icons = new Image();
            Quant = new Label("", skin);
        } else {
            Texture Icon = new Texture(Gdx.files.internal(item.getTexLocation()));
            Icons = new Image(Icon);
            Quant = new Label(item.getQuantity() + "", skin);
        }

        this.add(Icons);
        LabelTable.add(Quant);
        LabelTable.bottom().right();
        this.add(LabelTable);

    }

    public void setItem(Item item) {
        this.item = item;
        if (item == null) {
            Icons.setDrawable(null);
            Quant.setText("");
        } else {
            Texture Icon = new Texture(Gdx.files.internal(item.getTexLocation()));
            Icons.setDrawable(new TextureRegionDrawable(new TextureRegion(Icon)));
            Quant.setText(item.getQuantity() + "");
        }
    }

    public Item getItem() {
        return item;
    }
}

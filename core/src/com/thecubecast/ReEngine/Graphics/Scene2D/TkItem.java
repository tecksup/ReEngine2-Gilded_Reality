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
import static com.thecubecast.ReEngine.GameStates.PlayState.player;
import static com.thecubecast.ReEngine.Graphics.Scene2D.UIFSM.CursorItem;

public class TkItem extends Stack {

    private int id;
    Item BackupItem;

    Table LabelTable;

    Image Icons;
    Label Quant;

    public TkItem(Skin skin, int ItemArrayPos) {

        super();

        this.id = ItemArrayPos;
        BackupItem = player.Inventory[id];

        LabelTable = new Table();

        if (player.Inventory[id] == null) {
            Icons = new Image();
            Quant = new Label("", skin);
        } else {
            Texture Icon = new Texture(Gdx.files.internal(player.Inventory[id].getTexLocation()));
            Icons = new Image(Icon);
            Quant = new Label(player.Inventory[id].getQuantity() + "", skin);
        }

        this.add(Icons);
        LabelTable.add(Quant);
        LabelTable.bottom().right();
        this.add(LabelTable);

        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                //Play a click sound
                AudioM.play("Click");

                if (CursorItem == null) { //Pickup
                    CursorItem = player.Inventory[id];
                    player.Inventory[id] = null;
                    Reload();

                } else {
                    if (getItem() != null) {
                        //Swap
                        Item tempItem = CursorItem;
                        CursorItem = getItem();
                        player.Inventory[id] = tempItem;
                        Reload();

                    } else {
                        //Place
                        player.Inventory[id] = CursorItem;
                        CursorItem = null;
                        Reload();

                    }
                }

            }
        });

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!Item.compare(BackupItem,player.Inventory[id])) {
            BackupItem = player.Inventory[id];
            Reload();
        }
    }

    public void Reload() {
        if (player.Inventory[id] == null) {
            Icons.setDrawable(null);
            Quant.setText("");
        } else {
            Texture Icon = new Texture(Gdx.files.internal(player.Inventory[id].getTexLocation()));
            Icons.setDrawable(new TextureRegionDrawable(new TextureRegion(Icon)));
            Quant.setText(player.Inventory[id].getQuantity() + "");
        }
    }

    public Item getItem() {
        return player.Inventory[id];
    }
}

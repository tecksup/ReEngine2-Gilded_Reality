package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thecubecast.ReEngine.Data.Item;

import static com.thecubecast.ReEngine.Data.GameStateManager.ItemPresets;
import static com.thecubecast.ReEngine.GameStates.PlayState.player;
import static com.thecubecast.ReEngine.Graphics.Scene2D.UIFSM.CursorItem;

public class TkItemIcon extends Stack {

    boolean isHovering = false;

    boolean isEquipment = false;

    Table LabelTable;

    Image Icons;
    Label Quant;

    public TkItemIcon(Skin skin, int itemID) {

        super();

        LabelTable = new Table();

        Texture Icon = new Texture(Gdx.files.internal(ItemPresets.get(itemID).getTexLocation()));
        Icons = new Image(Icon);

        this.add(Icons);

    }

    public TkItemIcon(Skin skin, int itemID, int Quantity) {

        super();

        LabelTable = new Table();

        Texture Icon = new Texture(Gdx.files.internal(ItemPresets.get(itemID).getTexLocation()));
        Icons = new Image(Icon);
        Quant = new Label(Quantity + "", skin);

        this.add(Icons);
        LabelTable.add(Quant);
        LabelTable.bottom().right();
        this.add(LabelTable);

    }

}

package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.thecubecast.ReEngine.Data.Equipment;
import com.thecubecast.ReEngine.Data.Item;

import static com.thecubecast.ReEngine.GameStates.PlayState.player;
import static com.thecubecast.ReEngine.Graphics.Scene2D.UIFSM.CursorItem;

public class TkItem extends Stack {

    boolean isHovering = false;

    boolean isEquipment = false;

    private int id;
    Item BackupItem;

    Table LabelTable;

    Image Icons;
    TypingLabel Quant;

    public TkItem(Skin skin, int ItemArrayPos) {

        super();

        this.id = ItemArrayPos;
        BackupItem = player.Inventory[id];

        LabelTable = new Table();

        if (player.Inventory[id] == null) {
            Icons = new Image();
            Quant = new TypingLabel("", skin);
            Quant.skipToTheEnd();
        } else {
            Texture Icon = new Texture(Gdx.files.internal(player.Inventory[id].getTexLocation()));
            Icons = new Image(Icon);
            if (player.Inventory[id].getQuantity() > 99)
                Quant = new TypingLabel("99+", skin);
            else
                Quant = new TypingLabel(player.Inventory[id].getQuantity() + "", skin);
            Quant.skipToTheEnd();
        }

        this.add(Icons);
        LabelTable.add(Quant);
        LabelTable.bottom().right();
        this.add(LabelTable);

        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                //Play a click sound
                //AudioM.play("Click");

                if (CursorItem == null) { //Pickup
                    CursorItem = player.Inventory[id];
                    player.Inventory[id] = null;
                    Reload();

                } else {
                    if (getItem() != null) {
                        if (CursorItem.getName().equals(player.Inventory[id].getName())) { //Stack
                            player.Inventory[id].setQuantity(player.Inventory[id].getQuantity() + CursorItem.getQuantity());
                            CursorItem = null;
                            Reload();
                        } else { //SWAP
                            Item tempItem = CursorItem;
                            CursorItem = getItem();
                            player.Inventory[id] = tempItem;
                            Reload();
                        }

                    } else {
                        //Place
                        player.Inventory[id] = CursorItem;
                        CursorItem = null;
                        Reload();

                    }
                }
            }
        });

        this.addListener(new ClickListener(Input.Buttons.RIGHT){
            @Override
            public void clicked(InputEvent event, float x, float y){
                //Play a click sound
                //AudioM.play("Click");

                if (CursorItem == null) { //Pickup half
                    if (player.Inventory[id].getQuantity() % 2 > 0) {
                        CursorItem = new Item(player.Inventory[id]);
                        CursorItem.setQuantity(CursorItem.getQuantity()/2+1);
                        player.Inventory[id].setQuantity(player.Inventory[id].getQuantity()/2);
                    } else {
                        CursorItem = new Item(player.Inventory[id]);
                        CursorItem.setQuantity(CursorItem.getQuantity()/2);
                        player.Inventory[id].setQuantity(player.Inventory[id].getQuantity()/2);
                    }
                    Reload();

                } else {
                    if (getItem() != null) {
                    } else {
                        //Place half
                        if (CursorItem.getQuantity() % 2 > 0) {
                            Item temp = new Item(CursorItem);
                            temp.setQuantity(CursorItem.getQuantity()/2+1);
                            CursorItem.setQuantity(CursorItem.getQuantity()/2);
                            player.Inventory[id] = temp;
                        } else {
                            Item temp = new Item(CursorItem);
                            temp.setQuantity(CursorItem.getQuantity()/2);
                            CursorItem.setQuantity(CursorItem.getQuantity()/2);
                            player.Inventory[id] = temp;
                        }

                        Reload();

                    }
                }

            }
        });

    }

    public TkItem(Skin skin, int ItemArrayPos, boolean DUD) {
        super();

        this.isEquipment = true;

        this.id = ItemArrayPos;
        BackupItem = player.Equipment[id];

        LabelTable = new Table();

        if (player.Equipment[id] == null) {
            Icons = new Image();
            Quant = new TypingLabel("", skin);
            Quant.skipToTheEnd();
        } else {
            Texture Icon = new Texture(Gdx.files.internal(player.Equipment[id].getTexLocation()));
            Icons = new Image(Icon);
            if (player.Inventory[id].getQuantity() > 99)
                Quant = new TypingLabel("99+", skin);
            else
                Quant = new TypingLabel(player.Inventory[id].getQuantity() + "", skin);
            Quant.skipToTheEnd();
        }

        this.add(Icons);
        LabelTable.add(Quant);
        LabelTable.bottom().right();
        this.add(LabelTable);

        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                //Play a click sound
                //AudioM.play("Click");

                if (CursorItem == null) { //Pickup
                    CursorItem = player.Equipment[id];
                    player.Equipment[id] = null;
                    Reload();

                } else {
                    if (getItem() != null) {
                        if (CursorItem.getName().equals(player.Equipment[id].getName())) { //Stack
                            player.Equipment[id].setQuantity(player.Equipment[id].getQuantity() + CursorItem.getQuantity());
                            CursorItem = null;
                            Reload();
                        } else { //SWAP
                            Item tempItem = CursorItem;
                            CursorItem = getItem();
                            player.Equipment[id] = tempItem;
                            Reload();
                        }

                    } else {
                        //Place
                        player.Equipment[id] = CursorItem;
                        CursorItem = null;
                        Reload();

                    }
                }
            }
        });

        this.addListener(new ClickListener(Input.Buttons.RIGHT){
            @Override
            public void clicked(InputEvent event, float x, float y){
                //Play a click sound
                //AudioM.play("Click");

                if (CursorItem == null) { //Pickup half
                    if (player.Equipment[id].getQuantity() % 2 > 0) {
                        CursorItem = new Item(player.Equipment[id]);
                        CursorItem.setQuantity(CursorItem.getQuantity()/2+1);
                        player.Equipment[id].setQuantity(player.Equipment[id].getQuantity()/2);
                    } else {
                        CursorItem = new Item(player.Equipment[id]);
                        CursorItem.setQuantity(CursorItem.getQuantity()/2);
                        player.Equipment[id].setQuantity(player.Equipment[id].getQuantity()/2);
                    }
                    Reload();

                } else {
                    if (getItem() != null) {
                    } else {
                        //Place half
                        if (player.Equipment[id].getQuantity() % 2 > 0) {
                            Item temp = new Item(CursorItem);
                            temp.setQuantity(CursorItem.getQuantity()/2+1);
                            CursorItem.setQuantity(CursorItem.getQuantity()/2);
                            player.Equipment[id] = temp;
                        } else {
                            Item temp = new Item(CursorItem);
                            temp.setQuantity(CursorItem.getQuantity()/2);
                            CursorItem.setQuantity(CursorItem.getQuantity()/2);
                            player.Equipment[id] = temp;
                        }

                        Reload();

                    }
                }

            }
        });

    }


    @Override
    public void act(float delta) {
        super.act(delta);
        if (isEquipment) {
            if (!Item.compare(BackupItem,player.Equipment[id])) {
                BackupItem = player.Equipment[id];
                Reload();
            }
        } else {
            if (!Item.compare(BackupItem, player.Inventory[id])) {
                BackupItem = player.Inventory[id];
                Reload();
            }
        }
    }

    public void Reload() {
        if (isEquipment) {
            if (player.Equipment[id] == null) {
                Icons.setDrawable(null);
                Quant.setText("");
            } else {
                Texture Icon = new Texture(Gdx.files.internal(player.Equipment[id].getTexLocation()));
                Icons.setDrawable(new TextureRegionDrawable(new TextureRegion(Icon)));
                Quant.setText(player.Equipment[id].getQuantity() + "");
            }
        } else {
            if (player.Inventory[id] == null) {
                Icons.setDrawable(null);
                Quant.setText("");
            } else {
                Texture Icon = new Texture(Gdx.files.internal(player.Inventory[id].getTexLocation()));
                Icons.setDrawable(new TextureRegionDrawable(new TextureRegion(Icon)));
                Quant.setText(player.Inventory[id].getQuantity() + "");
            }
        }
    }

    public Item getItem() {
        if (isEquipment) {
            return player.Equipment[id];
        } else {
            return player.Inventory[id];
        }
    }
}

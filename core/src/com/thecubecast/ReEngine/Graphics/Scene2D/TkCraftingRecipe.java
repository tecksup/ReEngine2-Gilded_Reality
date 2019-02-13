package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.thecubecast.ReEngine.Data.Item;

import static com.thecubecast.ReEngine.Data.GameStateManager.ItemPresets;
import static com.thecubecast.ReEngine.GameStates.PlayState.CraftingRecipes;
import static com.thecubecast.ReEngine.GameStates.PlayState.player;

public class TkCraftingRecipe extends Table {

    boolean CanCraft = false;

    public TkCraftingRecipe(Skin skin, int tempi) {
        for (int j = 0; j < CraftingRecipes.get(tempi).RequiredResources().length; j++) {
            int tempj = j;
            Table ItemB = new Table(skin) {
                @Override
                public void act(float delta) {
                    super.act(delta);
                    int ItemIDSearch = CraftingRecipes.get(tempi).RequiredResources()[tempj][0];
                    int TotalQuantity = 0;
                    for (int i = 0; i < player.Inventory.length; i++) {
                        if (player.Inventory[i] != null) {
                            if (player.Inventory[i].getID() == ItemIDSearch) {
                                //Found matching item
                                TotalQuantity += player.Inventory[i].getQuantity();
                            }
                        }
                    }

                    if (TotalQuantity >= CraftingRecipes.get(tempi).RequiredResources()[tempj][1]) {
                        //Allow the crafting
                        this.setBackground("Window_green");
                        CanCraft = true;
                    } else {
                        this.setBackground("Window_red");
                        CanCraft = false;
                    }
                }
            };

            TkItemIcon Requirments = new TkItemIcon(skin, CraftingRecipes.get(tempi).RequiredResources()[j][0], CraftingRecipes.get(tempi).RequiredResources()[j][1]);
            ItemB.add(Requirments).size(28);
            this.add(ItemB).size(32);
            if (j == 0)
                this.add(new Label(" + ", skin));
            if (j % 2 == 1 && j + 1 < CraftingRecipes.get(tempi).RequiredResources().length)
                this.add(new Label(" + ", skin));
        }

        this.add(new Label(" = ", skin));

        Table ItemBox = new Table(skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (CanCraft) {
                    this.setBackground("Window_green");
                } else {
                    this.setBackground("Window_red");
                }
            }
        };
        ItemBox.setBackground("Window_grey");

        TkItemIcon Craftable = new TkItemIcon(skin, CraftingRecipes.get(tempi).getCraftableID(), CraftingRecipes.get(tempi).getQuantity());
        ItemBox.add(Craftable).size(28);

        this.add(ItemBox).size(32);

        Craftable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Craft the item, put in first available slot
                //Update label for information to user

                if (CanCraft) {

                    for (int j = 0; j < CraftingRecipes.get(tempi).RequiredResources().length; j++) {
                        int tempj = j;

                        int TotalQuantity = CraftingRecipes.get(tempi).RequiredResources()[j][1];
                        int whilecount = 0;
                        while (TotalQuantity > 0 && whilecount < player.Inventory.length) {
                            if (player.Inventory[whilecount] != null) {
                                if (player.Inventory[whilecount].getID() == CraftingRecipes.get(tempi).RequiredResources()[tempj][0]) {
                                    //Found matching item
                                    if (player.Inventory[whilecount].getQuantity() > TotalQuantity) {
                                        player.Inventory[whilecount].setQuantity(player.Inventory[whilecount].getQuantity() - TotalQuantity);
                                        break;
                                    } else if (player.Inventory[whilecount].getQuantity() == TotalQuantity) {
                                        player.Inventory[whilecount] = null;
                                        break;
                                    } else {
                                        TotalQuantity -= player.Inventory[whilecount].getQuantity();
                                        player.Inventory[whilecount] = null;
                                    }
                                }
                            }
                            whilecount++;
                        }
                    }

                    //Finds first empty spot
                    for (int j = 0; j < player.Inventory.length; j++) {
                        if (player.Inventory[j] == null) {
                            Item tempItem = new Item(ItemPresets.get(CraftingRecipes.get(tempi).getCraftableID()));
                            tempItem.setQuantity(CraftingRecipes.get(tempi).getQuantity());
                            player.Inventory[j] = tempItem;
                            break;
                        }
                    }

                }
            }
        });
    }

}

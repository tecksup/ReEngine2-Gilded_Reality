package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import static com.thecubecast.ReEngine.GameStates.PlayState.CraftingRecipes;
import static com.thecubecast.ReEngine.GameStates.PlayState.player;
import static com.thecubecast.ReEngine.Graphics.Scene2D.UIFSM.CraftingIDSelected;

public class TkIngrediantsTable extends Table {

    UIFSM entity;

    public TkIngrediantsTable(Skin Skin) {
        super(Skin);
        this.entity = entity;
    }

    private int LocalCraftingId = -1;
    @Override
    public void act(float delta) {
        if (CraftingIDSelected != LocalCraftingId) {
            reload();
        }
        super.act(delta);
    }

    public void reload() {
        this.clear();
        LocalCraftingId = CraftingIDSelected;

        if (CraftingIDSelected >= 0) {
            //Adds the
            for (int i = 0; i < CraftingRecipes.get(CraftingIDSelected).RequiredResources().length; i++) {

                //Find out how much of the required resource we have
                int RequiredQuant = CraftingRecipes.get(CraftingIDSelected).RequiredResources()[i][1];
                int ItemId = CraftingRecipes.get(CraftingIDSelected).RequiredResources()[i][0];
                int StoredResource = player.getItemQuant(ItemId);

                Table ItemQuant = new Table();

                TkItemIcon Icon = new TkItemIcon(this.getSkin(), ItemId);
                TypingLabel Quant;

                if (StoredResource < RequiredQuant) {
                    if (StoredResource > 99)
                        Quant = new TypingLabel("{COLOR=red}99+/" + RequiredQuant, this.getSkin());
                    else
                        Quant = new TypingLabel("{COLOR=red}"+ StoredResource + "/" + RequiredQuant, this.getSkin());
                } else {
                    if (StoredResource > 99)
                        Quant = new TypingLabel("{COLOR=green}99+/" + RequiredQuant, this.getSkin());
                    else
                        Quant = new TypingLabel("{COLOR=green}"+ StoredResource + "/" + RequiredQuant, this.getSkin());
                }

                Quant.skipToTheEnd();

                ItemQuant.add(Icon).size(16).row();
                ItemQuant.add(Quant);

                this.add(ItemQuant).padLeft(2).padRight(2);
            }
        }
    }
}

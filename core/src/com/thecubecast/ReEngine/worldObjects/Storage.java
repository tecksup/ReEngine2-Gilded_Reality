package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.Item;

public class Storage extends Interactable {

    public Item[] Inventory = new Item[30];

    public Storage(int x, int y, int z, Vector3 size, type State, boolean collision) {
        super(x, y, z, size, State, collision);
        ID = "Chest";
    }

    @Override
    public void Activated() {

    }

    public int getItemQuant(int ItemId) {
        int StoredResource = 0;

        for (int j = 0; j < Inventory.length; j++) {
            if (Inventory[j] != null) {
                if (Inventory[j].getID() == ItemId) {
                    //Found matching item
                    StoredResource += Inventory[j].getQuantity();
                }
            }
        }

        return StoredResource;
    }

    public boolean AddToInventory(Item item) {

        boolean found = false;

        //Finds first Matching spot
        for (int j = 0; j < Inventory.length; j++) {
            if (Inventory[j] != null) {
                if(Inventory[j].getID() == item.getID()) {
                    Inventory[j].setQuantity(Inventory[j].getQuantity() + item.getQuantity());
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            for (int j = 0; j < Inventory.length; j++) {
                if (Inventory[j] == null) {
                    Item tempItem = new Item(item);
                    Inventory[j] = tempItem;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean DeductFromInventory(int ItemID, int Quant) {
        boolean Success = false;

        if(getItemQuant(ItemID) >= Quant) {

            int ResourceRemaining = Quant;

            for (int j = 0; j < Inventory.length; j++) {
                if (Inventory[j] != null) {
                    if (Inventory[j].getID() == ItemID) { //Found matching item
                        if (Inventory[j].getQuantity() < ResourceRemaining) { //if that item Quant is less then needed
                            ResourceRemaining -= Inventory[j].getQuantity();
                            Inventory[j] = null;
                        } else if (Inventory[j].getQuantity() == ResourceRemaining) {
                            ResourceRemaining = 0;
                            Inventory[j] = null;
                            break;
                        } else {
                            Inventory[j].setQuantity(Inventory[j].getQuantity() - ResourceRemaining);
                            break;
                        }
                    }
                }
            }

            if (ResourceRemaining == 0) {
                Success = true;
            }

        } else {
            return false;
        }

        return Success;
    }
}

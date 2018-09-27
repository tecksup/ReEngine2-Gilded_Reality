package com.thecubecast.ReEngine.Data;

import static com.thecubecast.ReEngine.Data.GameStateManager.ItemPresets;

public class Item {
    private String Name = "";
    private int ID = 0;
    private int Quantity = 0;
    private String TexLocation = "";

    public Item(int ID, int Quantity) {
        this(ItemPresets.get(ID));
        this.Quantity = Quantity;
    }

    public Item(Item item) {
        this.Name = item.getName();
        this.ID = item.getID();
        this.Quantity = item.getQuantity();
        this.TexLocation = item.getTexLocation();
    }

    public Item(String Name, int ID, String SpriteLocation) {
        this.Name = Name;
        this.ID = ID;
        this.TexLocation = SpriteLocation;
    }

    public String getName() {
        return Name;
    }

    public int getID() {
        return ID;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getTexLocation() {
        return TexLocation;
    }

    public static boolean compare(Item item1, Item item2) {
        return (item1 == null ? item2 == null : item1.equals(item2));
    }
}

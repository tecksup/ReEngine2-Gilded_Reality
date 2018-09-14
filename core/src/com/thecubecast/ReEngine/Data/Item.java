package com.thecubecast.ReEngine.Data;

public class Item {
    private String Name = "";
    private int ID = 0;
    private int Quantity = 0;
    private String TexLocation = "";

    public Item(String Name, int ID) {
        this.Name = Name;
        this.ID = ID;
    }

    public Item(String Name, int ID, String SpriteLocation) {
        this.Name = Name;
        this.ID = ID;
        this.TexLocation = SpriteLocation;
    }

    public Item(String Name, int ID, int Quantity) {
        this.Name = Name;
        this.ID = ID;
        this.Quantity = Quantity;
    }

    public Item(String Name, int ID, int Quantity, String SpriteLocation) {
        this.Name = Name;
        this.ID = ID;
        this.Quantity = Quantity;
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

    public String getTexLocation() {
        return TexLocation;
    }
}

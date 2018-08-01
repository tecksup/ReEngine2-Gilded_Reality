package com.thecubecast.ReEngine.Data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Floor {
    public enum Types {
        Wood(1, new String[] {"Wood"}, new int[] {50}),
        ReWood(2, new String[] {"Wood", "Stone"}, new int[] {60, 30}),
        Concrete(3, new String[] {"Wood", "Stone"}, new int[] {10, 80}),
        ReConcrete(4, new String[] {"Stone", "Iron"}, new int[] {90,40}),
        Steel(5, new String[] {"Iron"}, new int[] {100}),
        Carbon(6, new String[] {"Wood", "Stone", "Iron"}, new int[] {100, 100, 100});

        int order;
        List<Factory.Cost> Requirements = new ArrayList<>();

        Types(int i, String[] ResourceType, int[] BaseCost) {
            this.order = i;
            for (int j = 0; j < ResourceType.length; j++) {
                Requirements.add(new Factory.Cost(ResourceType[j], BaseCost[j]));
            }
        }

        public int getValue() {
            return order;
        }

        public List<Factory.Cost> getRequirements() {
            return Requirements;
        }

        public Types Upgrade() {
            for(Types cnt :Types.values())
            {
                if(cnt.order == this.order+1)
                {
                    return cnt;
                }
            }
            return this;
        }
    }

    private Types BuildingType;

    private int FloorsAbove = 0;

    Texture Image;

    //UPGRADES
    Boolean Elevator = false;

    public Floor(Types Material) {
        this.BuildingType = Material;
        Image = new Texture(Gdx.files.internal("Sprites/Floor_" + BuildingType.name() + ".png"));
    }

    public Types getBuildingType() {
        return BuildingType;
    }

    public int getCapacity() {
        switch (BuildingType) {
            case Wood:
                return 5;
            case ReWood:
                return 8;
            case Concrete:
                return 10;
            case ReConcrete:
                return 14;
            case Steel:
                return 16;
            case Carbon:
                return 20;
        }
        return -1;
    }

    public void Draw(SpriteBatch batch, int x, int y) {
        batch.draw(Image, x, y);
    }

    public int getFloorsAbove() {
        return FloorsAbove;
    }

    public void setFloorsAbove(int floorsAbove) {
        FloorsAbove = floorsAbove;
    }

    public void setBuildingType(Types Material) {
        this.BuildingType = Material;
        Image = new Texture(Gdx.files.internal("Sprites/Floor_" + BuildingType.name() + ".png"));
    }
}

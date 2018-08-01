package com.thecubecast.ReEngine.Data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.thecubecast.ReEngine.GameStates.PlayState;

import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.xml.soap.Text;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Factory {

    public static class Cost {

        String Resource;
        int Cost;

        public Cost(String Type, int Cost) {
            this.Resource = Type;
            this.Cost = Cost;
        }

        public String getResource() {
            return Resource;
        }

        public void setResource(String resource) {
            Resource = resource;
        }

        public int getCost() {
            return Cost;
        }

        public void setCost(int cost) {
            Cost = cost;
        }
    }

    public enum Types {
        Wood(0, new String[] {"Wood"}, new int[] {10}),
        Stone(1, new String[] {"Wood"}, new int[] {30}),
        Iron(2, new String[] {"Wood", "Stone"}, new int[] {80, 50});

        int order;
        List<Cost> Requirements = new ArrayList<>();

        Types(int i, String[] ResourceType, int[] BaseCost) {
            this.order = i;
            for (int j = 0; j < ResourceType.length; j++) {
                Requirements.add(new Cost(ResourceType[j], BaseCost[j]));
            }
        }

        public int getOrder() {
            return order;
        }

        public List<Cost> getRequirements() {
            return Requirements;
        }
    }

    private Types FactoryType;
    private int Effeciency = 1;
    private int ProductionRate = 5;
    private float TimeToProduce = 5; //In seconds

    private long LastTimeProduced = System.nanoTime();

    Texture Image;

    private int xPosition;

    public Factory(Types Product, int X) {
        this.FactoryType = Product;
        Image = new Texture(Gdx.files.internal("Sprites/Factory_" + FactoryType.name() + ".png"));
        this.xPosition = X;
    }

    public void Draw(SpriteBatch batch, int x, int y) {
        batch.draw(Image, x, y);
    }

    public void Update(int[] Resources) {

        int Resource = -1;

        for (int i = 0; i < Types.values().length; i++) {
            if (Types.values()[i].equals(FactoryType)) {
                Resource = i;
                break;
            }
        }

        if (((LastTimeProduced - System.nanoTime())/1000000000.0)*-1 >= TimeToProduce) {
            Resources[Resource] += ProductionRate*Effeciency;
            if (Resources[Resource] > 999) {
                Resources[Resource] = 999;
            }
            LastTimeProduced = System.nanoTime();
        }

    }

    /**
     *
     * @return normalized float showing how long until the next resource being produced
     */
    public float PercentTillResource() {
        return (float) (TimeToProduce / ((LastTimeProduced - System.nanoTime())/1000000000.0)*-1);
    }

    public Types getFactoryType() {
        return FactoryType;
    }

    public void setFactoryType(Types factoryType) {
        FactoryType = factoryType;
    }

    public int getEffeciency() {
        return Effeciency;
    }

    public void setEffeciency(int effeciency) {
        Effeciency = effeciency;
    }

    public int getProductionRate() {
        return ProductionRate;
    }

    public void setProductionRate(int productionRate) {
        ProductionRate = productionRate;
    }

    public int getxPosition() {
        return xPosition;
    }
}

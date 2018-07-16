package com.thecubecast.ReEngine.Data.OGMO;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.XmlReader;
import com.thecubecast.ReEngine.Data.Area;
import com.thecubecast.ReEngine.Data.collision;
import com.thecubecast.ReEngine.Graphics.RePipeline;
import com.thecubecast.ReEngine.worldObjects.Player;
import com.thecubecast.ReEngine.worldObjects.Trigger;
import com.thecubecast.ReEngine.worldObjects.WorldObject;

import java.awt.*;
import java.util.List;

/**
    Not used, Entities are just loaded straight into world
 */
public class OelEntitiesLayer extends OelLayer {

    XmlReader.Element Layer;

    //Just for calculation of coords
    private int width;
    private int height;


    public OelEntitiesLayer(String Name, XmlReader.Element Text) {
        super(-1, -1, Name);
        Layer = Text;
    }

    public void loadEntities(OelMap map, Player player, List<WorldObject> Entities, List<Area> Areas) {
        //if the object layer is Triggers
        if (Layer.getName().equals("Triggers")) {
            for (int i = 0; i < Layer.getChildCount(); i++) {
                if (Layer.getChild(i).getName().equals("Trigger")) {
                    XmlReader.Element temp = Layer.getChild(i);

                    int x = Integer.parseInt(temp.getAttribute("x"));
                    int y = Integer.parseInt(temp.getAttribute("y"));
                    Vector3 size = new Vector3(Integer.parseInt(temp.getAttribute("width")), Integer.parseInt(temp.getAttribute("height")), 0);
                    String Events = temp.getAttribute("Event");

                    y = (int) (map.getHeight() - y - size.y) + 16;
                    String temptemp = temp.getAttribute("TriggerType");

                    Trigger TriggerObject = null;

                    if (temptemp.equals("OnEntry")) {
                        TriggerObject = new Trigger(x, y, size, Events, Trigger.TriggerType.OnEntry);
                    } else if (temptemp.equals("OnTrigger")) {
                        TriggerObject = new Trigger(x, y, size, Events, Trigger.TriggerType.OnTrigger);
                    } else if (temptemp.equals("OnExit")) {
                        TriggerObject = new Trigger(x, y, size, Events, Trigger.TriggerType.OnExit);
                    }

                    Entities.add(TriggerObject);

                }

            }
        }

        //The Objects layer
        if (Layer.getName().equals("Objects")) {
            for (int i = 0; i < Layer.getChildCount(); i++) {
                if (Layer.getChild(i).getName().equals("Player")) {
                    player.setPosition(Integer.parseInt(Layer.getChild(i).getAttribute("x")),height - Integer.parseInt(Layer.getChild(i).getAttribute("y")));
                }

            }
        }

        //The Areas layer
        if (Layer.getName().equals("Areas")) {
            for (int i = 0; i < Layer.getChildCount(); i++) {

            }
        }
    }

    public void getLayerAttribute(String AttributeName) {

    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

package com.thecubecast.ReEngine.Data;

import com.thecubecast.ReEngine.worldObjects.HiddenArea;
import com.thecubecast.ReEngine.worldObjects.NPC;
import com.thecubecast.ReEngine.worldObjects.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class EntityBuffer {
    private List<WorldObject> WObjects = new ArrayList<>();
    private List<HiddenArea> HAreas = new ArrayList<>();
    private List<NPC> NPCs = new ArrayList<>();

    private List<WorldObject> EntityBufferList = new ArrayList<>();

    public List<WorldObject> EntityBufferList() {
        if(EntityBufferList != null) {
            return EntityBufferList;
        } else {
            List<WorldObject> temp = new ArrayList<>();
            for (int i = 0; i < WObjects.size(); i++) {
                temp.add(WObjects.get(i));
            }

            for (int i = 0; i < HAreas.size(); i++) {
                temp.add(HAreas.get(i));
            }

            for (int i = 0; i < NPCs.size(); i++) {
                temp.add(NPCs.get(i));
            }

            return temp;
        }
    }

    public void reset() {
        EntityBufferList = null;
    }

    public List<WorldObject> getWObjects() {
        return WObjects;
    }

    public List<HiddenArea> getHAreas() {
        return HAreas;
    }

    public List<NPC> getNPCs() {
        return NPCs;
    }
}

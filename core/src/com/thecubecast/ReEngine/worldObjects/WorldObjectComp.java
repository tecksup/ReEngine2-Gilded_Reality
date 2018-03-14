package com.thecubecast.ReEngine.worldObjects;

import java.util.Comparator;

public class WorldObjectComp implements Comparator<WorldObject> {
    @Override
    public int compare(WorldObject o1, WorldObject o2) {
        // entities ordered based on y-position
        return (Float.compare(o2.getHitbox().y, o1.getHitbox().y));
    }
}

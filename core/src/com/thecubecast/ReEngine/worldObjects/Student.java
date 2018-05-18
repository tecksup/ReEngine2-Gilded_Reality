package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.collision;
import com.thecubecast.ReEngine.Graphics.RePipeline;
import com.thecubecast.ReEngine.worldObjects.AI.*;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledGraph;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledNode;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.TiledManhattanDistance;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.TiledSmoothableGraphPath;

import java.util.List;

import static com.badlogic.gdx.utils.TimeUtils.nanoTime;

public class Student extends NPC {

    private Vector2 Destination;
    private Smart AI;

    public Student(String name, int x, int y, Vector3 size, float knockbackResistance, float health, intractability interact, FlatTiledGraph worldMap) {
        super(name, x, y, size, knockbackResistance, health, interact);

        AI = new Smart(this, worldMap);

    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, List<collision> Colls) {
        super.update(delta, Colls);
        AI.setDestination(Destination);
        AI.update();

    }

    @Override
    public void draw(RePipeline batch, float Time) {

    }

    @Override
    public void interact() {
        if(!AI.getStateMachine().getCurrentState().equals(Student_State.WALKING_TO_DESTINATION))
            AI.getStateMachine().changeState(Student_State.WALKING_TO_DESTINATION);

        if (AI.getPath().nodes.size > 1) {
            setPosition(AI.getPath().get(1).x*16, AI.getPath().get(1).y*16);
            AI.updatePath(true);
        }

    }

    public Vector2 getDestination() {
        return Destination;
    }

    public void setDestination(Vector2 destination) {
        Destination = destination;
        AI.setDestination(Destination);
        AI.update();
    }

    public TiledSmoothableGraphPath<FlatTiledNode> getPath() {
        return AI.getPath();
    }

    public Smart getAI() {
        return AI;
    }
}

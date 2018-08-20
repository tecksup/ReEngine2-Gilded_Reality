package com.thecubecast.ReEngine.worldObjects.EntityPrefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.collision;
import com.thecubecast.ReEngine.Graphics.PipelineTextureRegion;
import com.thecubecast.ReEngine.Graphics.RePipeTextureRegionDrawable;
import com.thecubecast.ReEngine.Graphics.RePipeline;
import com.thecubecast.ReEngine.worldObjects.AI.Pathfinding.FlatTiledGraph;
import com.thecubecast.ReEngine.worldObjects.AI.Student_State;
import com.thecubecast.ReEngine.worldObjects.NPC;
import com.thecubecast.ReEngine.worldObjects.Student;

import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.loadAnim;

public class Male_Student extends Student {

    int tics = 0;
    Texture sprite;
    private Animation<TextureRegion> idle;

    public Male_Student(String name, int x, int y, FlatTiledGraph mapGraph) {
        super(name, x, y, new Vector3(16, 16, 4), 1, 100, NPC.intractability.Talk, mapGraph);

        idle = new Animation<TextureRegion>(0.1f, loadAnim(sprite, "Sprites/8direct/east.png", 4, 1));
    }

    @Override
    public void update(float delta, List<collision> Colls) {
        super.update(delta, Colls);
        if(!getAI().getStateMachine().getCurrentState().equals(Student_State.WALKING_TO_DESTINATION))
            getAI().getStateMachine().changeState(Student_State.WALKING_TO_DESTINATION);
        tics++;

        if (getPath().nodes.size > 1) {
            if (tics > 30) {
                //setVelocityX(getPath().get(1).x * 16 - getPosition().x);
                setPosition(getPath().get(1).x * 16, getPath().get(1).y * 16);
                getAI().updatePath(true);
                tics = 0;
            }

        }
    }

    public void draw(RePipeline batch, float Time) {
        TextureRegion currentFrame = idle.getKeyFrame(Time, true);

        RePipeTextureRegionDrawable temp = new RePipeTextureRegionDrawable() {
            @Override
            public void DrawDiffuse(SpriteBatch batch) {
                batch.draw(currentFrame, x, y, 16, 16);
            }

            @Override
            public void DrawNormal(SpriteBatch batch) {

            }
        };
        temp.x = getPosition().x;
        temp.y = getPosition().y;

        batch.Layers.get(0).SpriteList.add(temp);
    }

}

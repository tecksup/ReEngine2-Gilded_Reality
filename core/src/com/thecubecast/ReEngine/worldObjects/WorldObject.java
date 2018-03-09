package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.collision;

import java.util.ArrayList;
import java.util.List;

public abstract class WorldObject {
    private Vector2 position;
    private Vector2 velocity;

    private Vector3 Size; //X is the width, Y is the height, and Z is the Collision box height

    private type State;

    public enum type {
        Static, Dynamic
    }

    private boolean Collidable = false;

    public WorldObject () {
        this.position = new Vector2(0,0);
        this.velocity = new Vector2(0,0);

        this.Size = new Vector3(16, 16, 4);

        this.State = type.Static;
    }

    public WorldObject (int x, int y, Vector3 size) {
        this.position = new Vector2(x,y);
        this.velocity = new Vector2(0,0);

        this.Size = size;

        this.State = type.Static;
    }

    public WorldObject (int x, int y, Vector3 size, type State) {
        this.position = new Vector2(x,y);
        this.velocity = new Vector2(0,0);

        this.Size = size;

        this.State = State;
    }

    public WorldObject (int x, int y, Vector3 size, type State, boolean collision) {
        this.position = new Vector2(x,y);
        this.velocity = new Vector2(0,0);

        this.Size = size;

        this.State = State;

        this.Collidable = collision;
    }

    public Rectangle getHitbox() {
        Rectangle RectPla = new Rectangle(getPosition().x, getPosition().y, getSize().x, getSize().y);
        return RectPla;
    }

    public boolean checkCollision(float xOffset, float yOffset, List<collision> Colls) {
        Rectangle RectPla = new Rectangle(getHitbox().x + xOffset, getHitbox().y + yOffset, getHitbox().width, getHitbox().height);
        for(int i = 0; i < Colls.size(); i++) {
            if (RectPla.overlaps(Colls.get(i).getRect())) {
                return true; // Dont move
            }
        }
        return false;
    }

    public boolean ifColliding (Rectangle coll) {
        if (getHitbox().overlaps(coll)) {
            return true; // Dont move
        } else {
            return false;
        }
    }

    public abstract void init(int Width, int Height);
    public abstract void update(float delta, List<collision> Colls);
    public abstract void draw(SpriteBatch batch, float Time);

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPosition(float x, float y) {
        this.position = new Vector2(x, y);
    }
    public void setPositionX(float x) {
        this.position.x = x;
    }
    public void setPositionY(float y) {
        this.position.y = y;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }
    public void setVelocity(float x, float y) {
        this.velocity = new Vector2(x, y);
    }
    public void setVelocityX(float x) {
        this.velocity.x = x;
    }
    public void setVelocityY(float y) {
        this.velocity.y = y;
    }

    public type getState() {
        return State;
    }

    public void setState(type state) {
        State = state;
    }

    public Vector3 getSize() {
        return Size;
    }

    public void setSize(Vector3 size) {
        Size = size;
    }

    public boolean isCollidable() {
        return Collidable;
    }

    public void setCollidable(boolean collidable) {
        Collidable = collidable;
    }
}

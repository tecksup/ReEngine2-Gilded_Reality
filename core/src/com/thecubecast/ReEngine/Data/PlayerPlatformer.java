package com.thecubecast.ReEngine.Data;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.thecubecast.ReEngine.GameStates.PlayState;

import java.util.ArrayList;
import java.util.List;

public class PlayerPlatformer {

    private List<Rectangle> Collisions = new ArrayList<>();

    public Vector2 Coords = new Vector2(0, 1);
    public Vector2 Velocity = new Vector2(0, 0);
    private Vector2 Gravity;
    private int Size;
    public float mSpeed = 4;
    public float jumpSpeed = 70;
    Rectangle RectPla;
    public float angle = 0;

    public enum Direction {
        left, right, up, down
    }

    public PlayerPlatformer(int size, Vector2 gravity) {
        Gravity = gravity;
        Size = size;
        //RectPla = new Rectangle(Player.Coords.x, Player.Coords.y, size, size);
        //RectPla.setCenter(Player.Coords.x + size/2, Player.Coords.y + size/2);
    }

    public void update(float delta, List<Rectangle> Colls) {
        Collisions = Colls;

        Velocity.x += Gravity.x * delta;
        Velocity.y += Gravity.y * delta;

        Vector2 pos = new Vector2(Velocity.x*delta, Velocity.y*delta);

        if (ifColliding(new Vector2(0, -0.01f))) { // provides Friction when you are on the ground
            Velocity.x += Velocity.x*-1 * 0.04f;
        }

        if (pos.x < 0) { //Moving left
            if (checkCollision(Coords.x - (pos.x*-1), Coords.y)) {
                Velocity.x = 0;
            } else {
                Coords.x -= (Velocity.x*delta*-1);
            }
        } else if (pos.x > 0) { // Moving right
            if (checkCollision(Coords.x + pos.x, Coords.y)) {
                Velocity.x = 0;
            } else {
                Coords.x += (Velocity.x*delta);
            }
        }

        if (pos.y < 0) { // Moving down
            if (checkCollision(Coords.x, Coords.y - (pos.y*-1))) {
                Velocity.y = 0;
            } else {
                Coords.y -= (Velocity.y*delta*-1);
            }
        } else if (pos.y > 0) {
            if (checkCollision(Coords.x, Coords.y + pos.y)) {
                Velocity.y = 0;
            } else {
                Coords.y += Velocity.y*delta;
            }
        }

    }

    public void MovePlayerVelocity(Direction direction, float delta) {
        float speed = 5;
        if (direction == Direction.left && Velocity.x > 0) { // if you want to go left, but are moving right
            Velocity.x += Velocity.x*-1 * 0.2f;
            //Velocity.x -= 3;
        }
        if (direction == Direction.right && Velocity.x < 0) { // if you want to go right, but are moving left
            Velocity.x += Velocity.x*-1 * 0.2f;
            //Velocity.x += 3;
        }

        Vector2 tempVelocity = new Vector2(Velocity.x + (mSpeed * speed) * delta, Velocity.y + (jumpSpeed * speed) * delta);

        if (Math.abs(tempVelocity.x) < 7) {
            if(direction == Direction.right)
                Velocity.x += (mSpeed * speed) * delta;
            if(direction == Direction.left)
                Velocity.x += (-mSpeed * speed) * delta;
        }

        if (Math.abs(tempVelocity.y) < 10) {
            if(direction == Direction.up)
                Velocity.y += (jumpSpeed * speed) * delta;
        }


        //Velocity.x += (moveDir.x * speed) * delta;
        //Velocity.y += (moveDir.y * speed) * delta;

    }

    private boolean checkCollision(float posx, float posy) {
        RectPla = new Rectangle(posx, posy, 0.9f, 0.9f);
        RectPla.setCenter(RectPla.x + RectPla.getWidth()/2, RectPla.y + RectPla.getHeight()/2);
        for(int i = 0; i < Collisions.size(); i++) {
            if (RectPla.overlaps(Collisions.get(i))) {
                return true; // Dont move
            }
        }
        return false;
    }

    public boolean ifColliding (Vector2 direction) {
        RectPla = new Rectangle(Coords.x + direction.x, Coords.y + direction.y, 0.9f, 0.9f);
        RectPla.setCenter(RectPla.x + RectPla.getWidth()/2, RectPla.y + RectPla.getHeight()/2);
        for(int i = 0; i < Collisions.size(); i++) {
            if (RectPla.overlaps(Collisions.get(i))) {
                return true; // Dont move
            }
        }
        return false;
    }

}

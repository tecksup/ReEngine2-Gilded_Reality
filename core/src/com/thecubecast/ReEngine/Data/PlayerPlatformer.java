package com.thecubecast.ReEngine.Data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PlayerPlatformer {

    private List<Rectangle> Collisions = new ArrayList<>();

    public Vector2 Coords = new Vector2(2, 1f);
    public Vector2 Velocity = new Vector2(0, 0);
    private Vector2 Gravity;
    private int Size;
    public float mSpeed = 4;
    public float jumpSpeed = 70;

    float width = 0.9f;
    float height = 1.9f;
    Rectangle RectPla;
    public float angle = 0;

    int numberJumps = 2;

    public AnimationState AnimState = AnimationState.Standing;


    Animation<TextureRegion> StandingAnimation;
    Texture Standing;

    Animation<TextureRegion> WalkingAnimation;
    Texture Walking;

    Animation<TextureRegion> RunningAnimation;
    Texture Running;

    Animation<TextureRegion> FallingAnimation;
    Texture Falling;

    public enum Direction {
        left, right, up, down
    }

    public boolean Facing = false; //true is left

    public enum AnimationState {
        Standing, Walking, Running, Jumping, Falling
    }

    public PlayerPlatformer(int size, Vector2 gravity, GameStateManager gsm) {
        Gravity = gravity;
        Size = size;
        //RectPla = new Rectangle(Player.Coords.x, Player.Coords.y, size, size);
        //RectPla.setCenter(Player.Coords.x + size/2, Player.Coords.y + size/2);

        StandingAnimation = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(Standing, "Sprites/Player/Standing.png", 4, 1));
        WalkingAnimation = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(Walking, "Sprites/Player/Walking.png", 4, 1));
        RunningAnimation = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(Running, "Sprites/Player/Running.png", 4, 1));
        FallingAnimation = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(Falling, "Sprites/Player/Falling.png", 4, 1));

    }

    public void update(float delta, List<Rectangle> Colls) {
        Collisions = Colls;

        Velocity.x += Gravity.x * delta;
        Velocity.y += Gravity.y * delta;

        Vector2 pos = new Vector2(Velocity.x * delta, Velocity.y * delta);

        if (ifColliding(new Vector2(0, -0.01f))) { // provides Friction when you are on the ground
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D)) { //KeyHit

            } else {
                Velocity.x += Velocity.x * -1 * 0.1f;
            }
        }

        if (pos.x < 0) { //Moving left
            if (checkCollision(Coords.x - (pos.x * -1), Coords.y)) {
                Velocity.x = 0;
            } else {
                Coords.x -= (Velocity.x * delta * -1);
            }
        } else if (pos.x > 0) { // Moving right
            if (checkCollision(Coords.x + pos.x, Coords.y)) {
                Velocity.x = 0;
            } else {
                Coords.x += (Velocity.x * delta);
            }
        }

        if (pos.y < 0) { // Moving down
            if (checkCollision(Coords.x, Coords.y - (pos.y * -1))) { // Landed
                numberJumps = 2;
                Velocity.y = 0;
            } else {
                Coords.y -= (Velocity.y * delta * -1);
            }
        } else if (pos.y > 0) {
            if (checkCollision(Coords.x, Coords.y + pos.y)) {
                Velocity.y = 0;
            } else {
                Coords.y += Velocity.y * delta;
            }
        }

        //Calculate the AnimState
        if (Math.abs(Velocity.x) > 0) {
            AnimState = AnimationState.Walking;
        } else {
            AnimState = AnimationState.Standing;
        }

    }

    public void MovePlayerVelocity(Direction direction, float delta) {
        float speed = 5;
        if (direction == Direction.left && Velocity.x > 0) { // if you want to go left, but are moving right
            Velocity.x += Velocity.x * -1 * 0.5f;
            //Velocity.x -= 3;
        }
        if (direction == Direction.right && Velocity.x < 0) { // if you want to go right, but are moving left
            Velocity.x += Velocity.x * -1 * 0.5f;
            //Velocity.x += 3;
        }

        Vector2 tempVelocity = new Vector2(Velocity.x + (mSpeed * speed) * delta, Velocity.y + (jumpSpeed * speed) * delta);

        if (Math.abs(tempVelocity.x) < 7) {
            if (direction == Direction.right)
                Velocity.x += (mSpeed * speed) * delta;
            if (direction == Direction.left)
                Velocity.x += (-mSpeed * speed) * delta;
        }

        if (Math.abs(tempVelocity.y) < 10) {
            if (direction == Direction.up) {
                if (numberJumps > 0) {
                    Velocity.y += (jumpSpeed * speed) * delta;
                    numberJumps--;
                }
            }
        }


        //Velocity.x += (moveDir.x * speed) * delta;
        //Velocity.y += (moveDir.y * speed) * delta;


        if (direction == Direction.left) {
            Facing = true;
        } else if (direction == Direction.right) {
            Facing = false;
        }
    }

    private boolean checkCollision(float posx, float posy) {
        RectPla = new Rectangle(posx, posy, width, height);
        RectPla.setCenter(RectPla.x + RectPla.getWidth() / 2, RectPla.y + RectPla.getHeight() / 2);
        for (int i = 0; i < Collisions.size(); i++) {
            if (RectPla.overlaps(Collisions.get(i))) {
                return true; // Dont move
            }
        }
        return false;
    }

    public boolean ifColliding(Vector2 direction) {
        RectPla = new Rectangle(Coords.x + direction.x, Coords.y + direction.y, width, height);
        RectPla.setCenter(RectPla.x + RectPla.getWidth() / 2, RectPla.y + RectPla.getHeight() / 2);
        for (int i = 0; i < Collisions.size(); i++) {
            if (RectPla.overlaps(Collisions.get(i))) {
                return true; // Dont move
            }
        }
        return false;
    }

    public void draw(SpriteBatch g, float time) {
        TextureRegion currentFrame;
        switch (AnimState) {
            case Standing:
                currentFrame = StandingAnimation.getKeyFrame(time, true);
                g.draw(currentFrame, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size * 2));
                break;

            case Falling:
                //Do nothing
                //g.draw(currentFrame, Facing ? Coords.x*(Size) + (Size) : Coords.x*Size, Coords.y*Size, Facing ? -(Size) : (Size), (Size*2));
                break;

            case Walking:
                currentFrame = WalkingAnimation.getKeyFrame(time * Math.abs(Velocity.x), true);
                g.draw(currentFrame, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size * 2));
                break;

            case Running:
                currentFrame = RunningAnimation.getKeyFrame(time, true);
                g.draw(currentFrame, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size * 2));
                break;
        }
    }

}

package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.Common;
import com.thecubecast.ReEngine.Data.GameStateManager;
import com.thecubecast.ReEngine.Data.collision;
import sun.security.ssl.Debug;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.ReEngine.Graphics.Draw.loadAnim;

public class Player extends WorldObject {

    public float AttackTime;

    public float mSpeed = 4*16;
    public float jumpSpeed = 70;

    public Direction playerDirection = Direction.South;
    public AnimationState AnimState = AnimationState.Standing;

    private Animation<TextureRegion> penguinAnimation0;
    private Animation<TextureRegion> penguinAnimation1;
    private Animation<TextureRegion> penguinAnimation2;
    private Animation<TextureRegion> penguinAnimation3;
    private Animation<TextureRegion> penguinAnimation4;
    private Animation<TextureRegion> penguinAnimation5;
    private Animation<TextureRegion> penguinAnimation6;
    private Animation<TextureRegion> penguinAnimation7;
    Texture penguin0, penguin1, penguin2, penguin3, penguin4, penguin5, penguin6, penguin7;

    public enum Direction {
        South, SouthEast, East, NorthEast, North, NorthWest, West, SouthWest
    }

    public boolean Facing = false; //true is left
    public enum AnimationState {
        Standing, Walking, Running, Jumping, Falling
    }

    public Player(int x, int y, Vector3 size) {
        super(x, y, size,type.Dynamic);

        penguinAnimation0 = new Animation<TextureRegion>(0.1f, loadAnim(penguin0, "Sprites/8direct/south.png", 4, 1));
        penguinAnimation1 = new Animation<TextureRegion>(0.1f, loadAnim(penguin1, "Sprites/8direct/southEast.png", 4, 1));
        penguinAnimation2 = new Animation<TextureRegion>(0.1f, loadAnim(penguin2, "Sprites/8direct/east.png", 4, 1));
        penguinAnimation3 = new Animation<TextureRegion>(0.1f, loadAnim(penguin3, "Sprites/8direct/northEast.png", 4, 1));
        penguinAnimation4 = new Animation<TextureRegion>(0.1f, loadAnim(penguin4, "Sprites/8direct/north.png", 4, 1));
        penguinAnimation5 = new Animation<TextureRegion>(0.1f, loadAnim(penguin5, "Sprites/8direct/northWest.png", 4, 1));
        penguinAnimation6 = new Animation<TextureRegion>(0.1f, loadAnim(penguin6, "Sprites/8direct/west.png", 4, 1));
        penguinAnimation7 = new Animation<TextureRegion>(0.1f, loadAnim(penguin7, "Sprites/8direct/southWest.png", 4, 1));

    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, List<collision> Colls) {

        //Debug.println("Player", "" + delta);
        if (AttackTime - delta > 0 )
            AttackTime -= delta;

       // Debug.println("Player", "" + AttackTime);

        if (getState().equals(type.Dynamic)) {
            super.setVelocityX((getVelocity().x + getVelocity().x*-1 * 0.1f));
            super.setVelocityY((getVelocity().y + getVelocity().y*-1 * 0.1f));

            Vector2 pos = new Vector2(getVelocity().x*delta, getVelocity().y*delta);

            if (pos.x < 0) { //Moving left
                if (checkCollision(-1, 0, Colls)) {
                    super.setVelocityX(0);
                } else {
                    super.setPositionX((getPosition().x - getVelocity().x*delta*-1));
                }
            } else if (pos.x > 0) { // Moving right
                if (checkCollision(+1, 0, Colls)) {
                    super.setVelocityX(0);
                } else {
                    super.setPositionX((getPosition().x + getVelocity().x*delta));
                }
            }

            if (pos.y < 0) { // Moving down
                if (checkCollision(0, -1, Colls)) {
                    super.setVelocityY(0);
                } else {
                    super.setPositionY((getPosition().y - getVelocity().y*delta*-1));
                }
            } else if (pos.y > 0) {
                if (checkCollision(0, +1, Colls)) {
                    super.setVelocityY(0);
                } else {
                    super.setPositionY((getPosition().y + getVelocity().y*delta));
                }
            }
        }

        //Calculate the AnimState
        if(Math.abs(getVelocity().x) > 0) {
            AnimState = AnimationState.Walking;
        } else {
            AnimState = AnimationState.Standing;
        }

    }

    @Override
    public boolean checkCollision(float xOffset, float yOffset, List<collision> Colls) {
        Rectangle RectPla = new Rectangle(getHitbox().x+2 + xOffset, getHitbox().y + yOffset, getHitbox().width-4, getHitbox().height-10);
        for(int i = 0; i < Colls.size(); i++) {
            if (RectPla.overlaps(Colls.get(i).getRect())) {
                return true; // Dont move
            }
        }
        return false;
    }

    public Polygon getAttackBox() {
        Polygon attack = new Polygon(new float[] {
                getIntereactBox().x -1, getIntereactBox().y -1,
                getIntereactBox().x -1, getIntereactBox().y + getIntereactBox().height -1,
                getIntereactBox().x + (getIntereactBox().width) -1, getIntereactBox().y + getIntereactBox().height -1,
                getIntereactBox().x + (getIntereactBox().width) -1 , getIntereactBox().y -1
        });

        return attack;
    }

    public Rectangle getIntereactBox() {
        Rectangle RectPla = getHitbox();
        switch (playerDirection) {
            case South:
                RectPla = new Rectangle(getHitbox().x, getHitbox().y-(1 * getHitbox().height), getHitbox().getWidth(), getHitbox().getHeight());
                break;
            case SouthEast:
                RectPla = new Rectangle(getHitbox().x+(1 * getHitbox().width), getHitbox().y-(1 * getHitbox().height), getHitbox().getWidth(), getHitbox().getHeight());
                break;
            case East:
                RectPla = new Rectangle(getHitbox().x+(1 * getHitbox().width), getHitbox().y, getHitbox().getWidth(), getHitbox().getHeight());
                break;
            case NorthEast:
                RectPla = new Rectangle(getHitbox().x+(1 * getHitbox().width), getHitbox().y+(1 * getHitbox().height), getHitbox().getWidth(), getHitbox().getHeight());
                break;
            case North:
                RectPla = new Rectangle(getHitbox().x, getHitbox().y+(1 * getHitbox().height), getHitbox().getWidth(), getHitbox().getHeight());
                break;
            case NorthWest:
                RectPla = new Rectangle(getHitbox().x-(1 * getHitbox().width), getHitbox().y+(1 * getHitbox().height), getHitbox().getWidth(), getHitbox().getHeight());
                break;
            case West:
                RectPla = new Rectangle(getHitbox().x-(1 * getHitbox().width), getHitbox().y, getHitbox().getWidth(), getHitbox().getHeight());
                break;
            case SouthWest:
                RectPla = new Rectangle(getHitbox().x-(1 * getHitbox().width), getHitbox().y-(1 * getHitbox().height), getHitbox().getWidth(), getHitbox().getHeight());
                break;
        }
        return RectPla;
    }

    @Override
    public void draw(SpriteBatch batch, float time) {
        if (playerDirection == Direction.South) {
            TextureRegion tempFrame0 = penguinAnimation0.getKeyFrame(time, true);
            batch.draw(tempFrame0, Facing ? getPosition().x + (getHitbox().getWidth()) : getPosition().x, getPosition().y, Facing ? -(getHitbox().getHeight()) : (getHitbox().getHeight()), (getHitbox().getHeight()));
        } else if (playerDirection == Direction.SouthEast) {
            TextureRegion tempFrame1 = penguinAnimation1.getKeyFrame(time, true);
            batch.draw(tempFrame1, Facing ? getPosition().x + (getHitbox().getWidth()) : getPosition().x, getPosition().y, Facing ? -(getHitbox().getHeight()) : (getHitbox().getHeight()), (getHitbox().getHeight()));
        } else if (playerDirection == Direction.East) {
            TextureRegion tempFrame2 = penguinAnimation2.getKeyFrame(time, true);
            batch.draw(tempFrame2, Facing ? getPosition().x + (getHitbox().getWidth()) : getPosition().x, getPosition().y, Facing ? -(getHitbox().getHeight()) : (getHitbox().getHeight()), (getHitbox().getHeight()));
        } else if (playerDirection == Direction.NorthEast) {
            TextureRegion tempFrame3 = penguinAnimation3.getKeyFrame(time, true);
            batch.draw(tempFrame3, Facing ? getPosition().x + (getHitbox().getWidth()) : getPosition().x, getPosition().y, Facing ? -(getHitbox().getHeight()) : (getHitbox().getHeight()), (getHitbox().getHeight()));
        } else if (playerDirection == Direction.North) {
            TextureRegion tempFrame4 = penguinAnimation4.getKeyFrame(time, true);
            batch.draw(tempFrame4, Facing ? getPosition().x + (getHitbox().getWidth()) : getPosition().x, getPosition().y, Facing ? -(getHitbox().getHeight()) : (getHitbox().getHeight()), (getHitbox().getHeight()));
        } else if (playerDirection == Direction.NorthWest) {
            TextureRegion tempFrame5 = penguinAnimation5.getKeyFrame(time, true);
            batch.draw(tempFrame5, Facing ? getPosition().x + (getHitbox().getWidth()) : getPosition().x, getPosition().y, Facing ? -(getHitbox().getHeight()) : (getHitbox().getHeight()), (getHitbox().getHeight()));
        } else if (playerDirection == Direction.West) {
            TextureRegion tempFrame6 = penguinAnimation6.getKeyFrame(time, true);
            batch.draw(tempFrame6, Facing ? getPosition().x + (getHitbox().getWidth()) : getPosition().x, getPosition().y, Facing ? -(getHitbox().getHeight()) : (getHitbox().getHeight()), (getHitbox().getHeight()));
        } else if (playerDirection == Direction.SouthWest) {
            TextureRegion tempFrame7 = penguinAnimation7.getKeyFrame(time, true);
            batch.draw(tempFrame7, Facing ? getPosition().x + (getHitbox().getWidth()) : getPosition().x, getPosition().y, Facing ? -(getHitbox().getHeight()) : (getHitbox().getHeight()), (getHitbox().getHeight()));

        }

        switch (AnimState) {
            case Standing:

                break;

            case Falling:
                //Do nothing
                //batch.draw(tempFrame, Facing ? getPosition().x*(Size) + (Size) : getPosition().x*Size, getPosition().y*Size, Facing ? -(Size) : (Size), (Size*2));
                break;

            case Walking:

                break;

            case Running:

                break;
        }
    }

    public void MovePlayerVelocity(Direction direction,float speed, float delta) {
		/*
	    if (direction == Direction.West && Velocity.x > 0) { // if you want to go left, but are moving right
			Velocity.x += Velocity.x*-1 * 0.5f;
			//Velocity.x -= 3;
		}
		if (direction == Direction.East && Velocity.x < 0) { // if you want to go right, but are moving left
			Velocity.x += Velocity.x*-1 * 0.5f;
			//Velocity.x += 3;
		}
        */

        Vector2 tempVelocity = new Vector2(getVelocity().x + (mSpeed * speed) * delta, getVelocity().y + (jumpSpeed * speed) * delta);
        if (Math.abs(tempVelocity.x) < 7 ||  Math.abs(tempVelocity.y) < 7) {

        }

        switch (direction) {
            case South:
                super.setVelocityY((getVelocity().y + (-mSpeed * speed) * delta));
                //super.setVelocity(getVelocity().x, getVelocity().y - 1);
                break;
            case SouthEast:
                super.setVelocityX((getVelocity().x + (mSpeed * speed) * delta));
                super.setVelocityY((getVelocity().y + (-mSpeed * speed) * delta));
                break;
            case East:
                super.setVelocityX((getVelocity().x + (mSpeed * speed) * delta));
                break;
            case NorthEast:
                super.setVelocityX((getVelocity().x + (mSpeed * speed) * delta));
                super.setVelocityY((getVelocity().y + (mSpeed * speed) * delta));
                break;
            case North:
                super.setVelocityY((getVelocity().y + (mSpeed * speed) * delta));
                break;
            case NorthWest:
                super.setVelocityX((getVelocity().x + (-mSpeed * speed) * delta));
                super.setVelocityY((getVelocity().y + (mSpeed * speed) * delta));
                break;
            case West:
                super.setVelocityX((getVelocity().x + (-mSpeed * speed) * delta));
                break;
            case SouthWest:
                super.setVelocityX((getVelocity().x + (-mSpeed * speed) * delta));
                super.setVelocityY((getVelocity().y + (-mSpeed * speed) * delta));
                break;
        }
        //Debug.println("WorldObject", "Velocity " + getVelocity());
        playerDirection = direction;

    }

    public Vector2 VecDirction() {
        switch (playerDirection) {
            case South:
                return new Vector2(0, -1);
            case SouthEast:
                return new Vector2(1, -1);
            case East:
                return new Vector2(1, 0);
            case NorthEast:
                return new Vector2(1, 1);
            case North:
                return new Vector2(0, 1);
            case NorthWest:
                return new Vector2(-1, 1);
            case West:
                return new Vector2(-1, 0);
            case SouthWest:
                return new Vector2(-1, -1);
        }
        return null;
    }

    public Direction getPlayerDirection() {
        return playerDirection;
    }

    public void setPlayerDirection(Direction playerDirection) {
        this.playerDirection = playerDirection;
    }
}

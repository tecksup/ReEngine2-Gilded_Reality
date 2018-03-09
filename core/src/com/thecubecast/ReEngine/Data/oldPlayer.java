package com.thecubecast.ReEngine.Data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class oldPlayer {

	private List<Rectangle> Collisions = new ArrayList<>();

	public Vector2 Coords = new Vector2(2, 1f);
	public Vector2 Velocity = new Vector2(0, 0);
	private int Size;
	public float mSpeed = 4;
	public float jumpSpeed = 70;

	float width = 1;
	float height = 1;
	Rectangle RectPla;
	public float angle = 0;

	public Direction playerDirection = Direction.South;
    public AnimationState AnimState = AnimationState.Standing;

	Animation<TextureRegion> StandingAnimation;
	Texture Standing;

	Animation<TextureRegion> WalkingAnimation;
	Texture Walking;

	Animation<TextureRegion> RunningAnimation;
	Texture Running;

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

	public oldPlayer(int size, GameStateManager gsm) {

        penguinAnimation0 = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(penguin0, "Sprites/8direct/south.png", 4, 1));
        penguinAnimation1 = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(penguin1, "Sprites/8direct/southEast.png", 4, 1));
        penguinAnimation2 = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(penguin2, "Sprites/8direct/east.png", 4, 1));
        penguinAnimation3 = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(penguin3, "Sprites/8direct/northEast.png", 4, 1));
        penguinAnimation4 = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(penguin4, "Sprites/8direct/north.png", 4, 1));
        penguinAnimation5 = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(penguin5, "Sprites/8direct/northWest.png", 4, 1));
        penguinAnimation6 = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(penguin6, "Sprites/8direct/west.png", 4, 1));
        penguinAnimation7 = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(penguin7, "Sprites/8direct/southWest.png", 4, 1));


		Size = size;
		//RectPla = new Rectangle(Player.Coords.x, Player.Coords.y, size, size);
		//RectPla.setCenter(Player.Coords.x + size/2, Player.Coords.y + size/2);

		StandingAnimation = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(Standing, "Sprites/Player/Standing.png", 4, 1));
		WalkingAnimation = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(Walking, "Sprites/Player/Walking.png", 4, 1));
		RunningAnimation = new Animation<TextureRegion>(0.1f, gsm.Render.loadAnim(Running, "Sprites/Player/Running.png", 4, 1));

	}

	public void update(float delta, List<Rectangle> Colls) {
		Collisions = Colls;

		Vector2 pos = new Vector2(Velocity.x*delta, Velocity.y*delta);

        Velocity.x += Velocity.x*-1 * 0.1f;
        Velocity.y += Velocity.y*-1 * 0.1f;

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
			if (checkCollision(Coords.x, Coords.y - (pos.y*-1))) { // Landed
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

		//Calculate the AnimState
		if(Math.abs(Velocity.x) > 0) {
			AnimState = AnimationState.Walking;
		} else {
			AnimState = AnimationState.Standing;
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

		Vector2 tempVelocity = new Vector2(Velocity.x + (mSpeed * speed) * delta, Velocity.y + (jumpSpeed * speed) * delta);
		if (Math.abs(tempVelocity.x) < 7 ||  Math.abs(tempVelocity.y) < 7) {

		}

        switch (direction) {
            case South:
                //Facing = false;
                Velocity.y += (-mSpeed * speed) * delta;
                break;
            case SouthEast:
                Velocity.x += (mSpeed * speed) * delta;
                Velocity.y += (-mSpeed * speed) * delta;
                break;
            case East:
                //Facing = false;
                Velocity.x += (mSpeed * speed) * delta;
                break;
            case NorthEast:
                Velocity.x += (mSpeed * speed) * delta;
                Velocity.y += (mSpeed * speed) * delta;
                break;
            case North:
                Velocity.y += (mSpeed * speed) * delta;
                break;
            case NorthWest:
                Velocity.x += (-mSpeed * speed) * delta;
                Velocity.y += (mSpeed * speed) * delta;
                break;
            case West:
                //Facing = true;
                Velocity.x += (-mSpeed * speed) * delta;
                break;
            case SouthWest:
                Velocity.x += (-mSpeed * speed) * delta;
                Velocity.y += (-mSpeed * speed) * delta;
                break;
        }

        playerDirection = direction;

	}

	private boolean checkCollision(float posx, float posy) {
		RectPla = new Rectangle(posx, posy, width, height);
		RectPla.setCenter(RectPla.x + RectPla.getWidth()/2, RectPla.y + RectPla.getHeight()/2);
		for(int i = 0; i < Collisions.size(); i++) {
			if (RectPla.overlaps(Collisions.get(i))) {
				return true; // Dont move
			}
		}
		return false;
	}

	public boolean ifColliding (Vector2 direction) {
		RectPla = new Rectangle(Coords.x + direction.x, Coords.y + direction.y, width, height);
		RectPla.setCenter(RectPla.x + RectPla.getWidth()/2, RectPla.y + RectPla.getHeight()/2);
		for(int i = 0; i < Collisions.size(); i++) {
			if (RectPla.overlaps(Collisions.get(i))) {
				return true; // Dont move
			}
		}
		return false;
	}

	public Rectangle getHitbox() {
		RectPla = new Rectangle(Coords.x, Coords.y, width, height);
		RectPla.setCenter(RectPla.x + RectPla.getWidth()/2, RectPla.y + RectPla.getHeight()/2);
		return RectPla;
	}

	public Rectangle getIntereactBox() {
		switch (playerDirection) {
			case South:
				RectPla = new Rectangle(Coords.x, Coords.y-height, width, height);
				break;
			case SouthEast:
				RectPla = new Rectangle(Coords.x, Coords.y, width, height);
				break;
			case East:
				RectPla = new Rectangle(Coords.x, Coords.y, width, height);
				break;
			case NorthEast:
				RectPla = new Rectangle(Coords.x, Coords.y, width, height);
				break;
			case North:
				RectPla = new Rectangle(Coords.x, Coords.y, width, height);
				break;
			case NorthWest:
				RectPla = new Rectangle(Coords.x, Coords.y, width, height);
				break;
			case West:
				RectPla = new Rectangle(Coords.x, Coords.y, width, height);
				break;
			case SouthWest:
				RectPla = new Rectangle(Coords.x, Coords.y, width, height);
				break;
		}
		return RectPla;
	}

	public void draw(SpriteBatch g, float time) {
        if (playerDirection == Direction.South) {
            TextureRegion tempFrame0 = penguinAnimation0.getKeyFrame(time, true);
            g.draw(tempFrame0, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size));
        } else if (playerDirection == Direction.SouthEast) {
            TextureRegion tempFrame1 = penguinAnimation1.getKeyFrame(time, true);
            g.draw(tempFrame1, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size));
        } else if (playerDirection == Direction.East) {
            TextureRegion tempFrame2 = penguinAnimation2.getKeyFrame(time, true);
            g.draw(tempFrame2, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size));
        } else if (playerDirection == Direction.NorthEast) {
            TextureRegion tempFrame3 = penguinAnimation3.getKeyFrame(time, true);
            g.draw(tempFrame3, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size));
        } else if (playerDirection == Direction.North) {
            TextureRegion tempFrame4 = penguinAnimation4.getKeyFrame(time, true);
            g.draw(tempFrame4, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size));
        } else if (playerDirection == Direction.NorthWest) {
            TextureRegion tempFrame5 = penguinAnimation5.getKeyFrame(time, true);
            g.draw(tempFrame5, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size));
        } else if (playerDirection == Direction.West) {
            TextureRegion tempFrame6 = penguinAnimation6.getKeyFrame(time, true);
            g.draw(tempFrame6, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size));
        } else if (playerDirection == Direction.SouthWest) {
            TextureRegion tempFrame7 = penguinAnimation7.getKeyFrame(time, true);
            g.draw(tempFrame7, Facing ? Coords.x * (Size) + (Size) : Coords.x * Size, Coords.y * Size, Facing ? -(Size) : (Size), (Size));

        }

		switch (AnimState) {
			case Standing:

				break;

			case Falling:
				//Do nothing
				//g.draw(tempFrame, Facing ? Coords.x*(Size) + (Size) : Coords.x*Size, Coords.y*Size, Facing ? -(Size) : (Size), (Size*2));
				break;

			case Walking:

				break;

			case Running:

				break;
		}
	}

}
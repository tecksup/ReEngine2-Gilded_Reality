package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.collision;

import java.util.List;

import static com.thecubecast.ReEngine.mainclass.FBOH;
import static com.thecubecast.ReEngine.mainclass.FBOW;

public abstract class NPC extends WorldObject {

    private float knockbackResistance;

    private float health;
    private boolean invulnerable = false;
    private String name;

    private intractability interact;
    private entityState EState = entityState.alive;

    public NPC(String name, int x, int y, Vector3 size, float knockbackResistance, float health) {
        super(x, y, size,type.Dynamic);
        this.knockbackResistance = knockbackResistance;
        this.health = health;
        this.name = name;
        this.interact = intractability.Silent;
        init(FBOW, FBOH);
    }

    public NPC(String name, int x, int y, Vector3 size, float knockbackResistance, float health, boolean invincible) {
        super(x, y, size,type.Dynamic);
        this.knockbackResistance = knockbackResistance;
        this.health = health;
        this.name = name;
        this.interact = intractability.Silent;
        this.invulnerable = invincible;
        init(FBOW, FBOH);
    }

    public NPC(String name, int x, int y, Vector3 size, float knockbackResistance, float health, intractability interact) {
        super(x, y, size,type.Dynamic);
        this.knockbackResistance = knockbackResistance;
        this.health = health;
        this.name = name;
        this.interact = interact;
        init(FBOW, FBOH);
    }

    public NPC(String name, int x, int y, Vector3 size, float knockbackResistance, float health, intractability interact, boolean invincible) {
        super(x, y, size,type.Dynamic);
        this.knockbackResistance = knockbackResistance;
        ;
        this.health = health;
        this.name = name;
        this.interact = interact;
        this.invulnerable = invincible;
        init(FBOW, FBOH);
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, List<collision> Colls) {

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
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

    }

    public void drawHighlight(SpriteBatch batch, float Time) {

    }

    public abstract void interact();

    public void heal(int heal) {
        health += heal;
    }

    public void damage(int damage) {
        if(!invulnerable) {
            health -= damage;
        }

        if (health < 0) {
            Die();
        }
    }

    public void damage(int damage, Vector2 knockback) {
        if(!invulnerable) {
            health -= damage;
            knockback.x -= knockback.x * knockbackResistance;
            knockback.y -= knockback.y * knockbackResistance;
            knockback.x += getVelocity().x;
            knockback.y += getVelocity().y;
            super.setVelocity(knockback);
        }

        if (health < 0) {
            Die();
        }
    }

    public void Die() {
        //Remove this NPC, or at least set its state to dead

    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public intractability getInteract() {
        return interact;
    }

    public void setInteract(intractability interact) {
        this.interact = interact;
    }

    public entityState getEState() {
        return EState;
    }

    public void setEState(entityState EState) {
        this.EState = EState;
    }

    private enum intractability {
        Talk, Silent,
    }

    private enum entityState {
        alive, dead
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public void setKnockbackResistance(float knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
    }
}

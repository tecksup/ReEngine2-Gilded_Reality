package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.collision;

import java.awt.*;
import java.util.List;

public abstract class HiddenArea extends WorldObject {

    private boolean Discovered = false;
    private boolean NeverDiscovered = false;

    private float fadeInSpeed;
    private float fadeOutSpeed;
    private float opacity = 1f;

    public HiddenArea(int x, int y, Vector3 size, type State, boolean collision) {
        super(x,y,size,State,collision);
    }

    @Override
    public abstract void init(int Width, int Height);

    @Override
    public abstract void update(float delta, List<collision> Colls);

    @Override
    public abstract void draw(SpriteBatch batch, float Time);

    public boolean isDiscovered() {
        return Discovered;
    }

    public void reveal() {
        Discovered = true;
    }

    public void hide() {
        Discovered = false;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    /**
        Fades out the room
        AKA: it makes the room vanish
     **/
    public void fadeOut() {
        opacity -= fadeOutSpeed;

        if(opacity < 0) {
            opacity = 0;
        }
    }

    /**
     Fades in the room
     AKA: it makes it darker
     **/
    public void fadeIn() {
        opacity += fadeInSpeed;

        if(opacity > 1) {
            opacity = 1;
        }
    }

    public float getFadeInSpeed() {
        return fadeInSpeed;
    }

    public float getFadeOutSpeed() {
        return fadeOutSpeed;
    }

    public void setFadeSpeed(float fadeOut, float fadeIn) {
        if(fadeOut >= 0 && fadeOut <= 1)
            this.fadeOutSpeed = fadeOut;
        if(fadeIn >= 0 && fadeIn <= 1)
            this.fadeInSpeed = fadeIn;
    }

    public boolean isNeverDiscovered() {
        return NeverDiscovered;
    }

    public void setNeverDiscovered(boolean firstTime) {
        NeverDiscovered = firstTime;
    }
}

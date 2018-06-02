package com.thecubecast.ReEngine.Data;

import com.badlogic.gdx.math.Rectangle;

public class collision {
    Rectangle rect;
    int Hash;

    public collision(Rectangle rect, int Hash) {
        this.rect = rect;
        this.Hash = Hash;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    public int getHash() {
        return Hash;
    }

    public void setHash(int hash) {
        Hash = hash;
    }
}

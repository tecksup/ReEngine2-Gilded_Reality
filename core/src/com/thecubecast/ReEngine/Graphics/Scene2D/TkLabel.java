package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TkLabel extends Label{

    public TkLabel(CharSequence text, Skin skin) {
        super(text, skin);
    }

    public TkLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public TkLabel(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}

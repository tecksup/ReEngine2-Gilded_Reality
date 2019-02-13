package com.thecubecast.ReEngine.Graphics.Scene2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public abstract class Dialog {

    private String Speaker;
    private String Text;
    private Texture SpeakerImage;
    private int cooldown = 60;

    public Dialog(String Speaker, String Text) {
        this.Speaker = Speaker;
        this.Text = Text;
        this.SpeakerImage = new Texture(Gdx.files.internal("Sprites/face.png"));
    }

    public Dialog(String Speaker, Texture face, String Text) {
        this.Speaker = Speaker;
        this.Text = Text;
        this.SpeakerImage = face;
    }

    public abstract void exit();

    public String getSpeaker() {
        return Speaker;
    }

    public void setSpeaker(String speaker) {
        Speaker = speaker;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public Texture getSpeakerImage() {
        return SpeakerImage;
    }

    public void setSpeakerImage(Texture speakerImage) {
        SpeakerImage = speakerImage;
    }
}

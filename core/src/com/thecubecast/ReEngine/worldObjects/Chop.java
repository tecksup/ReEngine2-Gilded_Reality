package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.Cube;
import com.thecubecast.ReEngine.GameStates.PlayState;

import java.util.List;
import java.util.Random;

import static com.thecubecast.ReEngine.Data.GameStateManager.ItemPresets;
import static com.thecubecast.ReEngine.GameStates.PlayState.Entities;
import static com.thecubecast.ReEngine.GameStates.PlayState.player;

public class Chop extends Interactable {

    public int xoffset = 0;
    int tics = 0;
    boolean Hit = false;
    boolean IGNOREME = false;

    public int[] drops = new int[2];

    int TotalHits = 0;

    public Chop(int x, int y, int z, Vector3 size, type State, boolean collision) {
        super(x, y, z, size, State, collision);
    }

    @Override
    public void update(float delta, List<Cube> Colls) {
        super.update(delta, Colls);
        if (Hit) {
            if (tics <= 6) {
                if (IGNOREME) {
                    xoffset = -1;
                } else {
                    xoffset = 1;
                }
                IGNOREME = !IGNOREME;
            } else {
                xoffset = 0;
                Hit = false;
            }
        }
        tics++;

        if (TotalHits >= 5) {
            Random rand = new Random();
            //rand.nextInt((3 - 1) + 1) + 1;
            for (int i = 0; i < rand.nextInt((3 - 2) + 2) + 2; i++) {
                WorldItem temp = new WorldItem((int) this.getPosition().x + (int) this.getSize().x/2 + (rand.nextInt(((int) this.getSize().x/2 - (int) this.getSize().x/2) + (int) this.getSize().x/2) + (int) this.getSize().x/2), (int) this.getPosition().y + (int) this.getSize().y/2 + (rand.nextInt(((int) this.getSize().y/2 - (int) this.getSize().y/2) + (int) this.getSize().y/2) + (int) this.getSize().y/2), (int) player.getIntereactBox().max.z, ItemPresets.get(5));
                temp.item.setQuantity(5);
                PlayState.Entities.add(temp);
            }
            Colls.remove(CollisionHashID);
            Entities.remove(this);
        }

    }

    @Override
    public void Activated() {
        if (!Hit && tics > 30) {
            Hit = true;
            tics = 0;
            PlayState.Particles.AddParticleEffect("Leaf", this.getPosition().x + this.getSize().x/2, this.getPosition().y + this.getSize().y).scaleEffect(3);
            PlayState.Particles.AddParticleEffect("Leaf", this.getPosition().x + this.getSize().x, this.getPosition().y + this.getSize().y).scaleEffect(3);
            PlayState.Particles.AddParticleEffect("Leaf", this.getPosition().x + this.getSize().x, this.getPosition().y + this.getSize().y*2).scaleEffect(3);
            PlayState.Particles.AddParticleEffect("Leaf", this.getPosition().x + this.getSize().x + this.getSize().x/2, this.getPosition().y + this.getSize().y).scaleEffect(3);
            TotalHits++;
        }
    }
}

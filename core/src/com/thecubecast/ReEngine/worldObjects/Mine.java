package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.Cube;
import com.thecubecast.ReEngine.GameStates.PlayState;

import java.util.List;
import java.util.Random;

import static com.thecubecast.ReEngine.Data.GameStateManager.ItemPresets;
import static com.thecubecast.ReEngine.GameStates.PlayState.Entities;
import static com.thecubecast.ReEngine.GameStates.PlayState.player;

public class Mine extends Interactable {

    public int xoffset = 0;
    int tics = 0;
    boolean Hit = false;
    boolean IGNOREME = false;

    int TotalHits = 0;

    public Mine(int x, int y, int z, Vector3 size, type State, boolean collision) {
        super(x, y, z, size, State, collision);
        ID = "Mine";
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        batch.draw(Image, getPosition().x + xoffset, getPosition().y);
    }

    @Override
    public void update(float delta, List<Cube> Colls) {
        super.update(delta, Colls);
        if (Hit) {
            if (tics <= 2) {
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
                for (int j = 0; j < Drops.size(); j++) {
                    WorldItem temp = new WorldItem((int) this.getPosition().x + (int) this.getSize().x/2 + (rand.nextInt(((int) this.getSize().x/2 - (int) this.getSize().x/2) + (int) this.getSize().x/2) + (int) this.getSize().x/2), (int) this.getPosition().y + (int) this.getSize().y/2 + (rand.nextInt(((int) this.getSize().y/2 - (int) this.getSize().y/2) + (int) this.getSize().y/2) + (int) this.getSize().y/2), (int) player.getIntereactBox().max.z, ItemPresets.get(Drops.get(j).getID()));
                    temp.item.setQuantity(Drops.get(j).getQuantity());
                    PlayState.Entities.add(temp);
                }
            }
            Colls.remove(CollisionHashID);
            Entities.remove(this);
        }

    }

    @Override
    public void Activated() {
        if (!Hit && tics > 45) {
            Hit = true;
            tics = 0;
            System.out.println("Mined");
            /*PlayState.Particles.AddParticleEffect("Leaf", this.getPosition().x + this.getSize().x/2, this.getPosition().y + this.getSize().y).scaleEffect(3);
            PlayState.Particles.AddParticleEffect("Leaf", this.getPosition().x + this.getSize().x, this.getPosition().y + this.getSize().y).scaleEffect(3);
            PlayState.Particles.AddParticleEffect("Leaf", this.getPosition().x + this.getSize().x, this.getPosition().y + this.getSize().y*2).scaleEffect(3);
            PlayState.Particles.AddParticleEffect("Leaf", this.getPosition().x + this.getSize().x + this.getSize().x/2, this.getPosition().y + this.getSize().y).scaleEffect(3);
            */
            TotalHits++;
        }
    }
}

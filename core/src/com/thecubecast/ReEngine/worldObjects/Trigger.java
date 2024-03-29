package com.thecubecast.ReEngine.worldObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.ReEngine.Data.Cube;
import com.thecubecast.ReEngine.Data.ParticleHandler;
import com.thecubecast.ReEngine.GameStates.DialogStateExtention;
import com.thecubecast.ReEngine.Graphics.ScreenShakeCameraController;

import java.util.List;

public class Trigger extends WorldObject {

    String RawCommands;
    String[][] Commands;

    public enum TriggerType {
        OnEntry,
        OnTrigger,
        OnExit,
        OnInteract,
        OnClick,
        None
    }

    TriggerType ActivationType;

    boolean TriggerActive = false;
    boolean TriggerRun = false;

    public boolean JustRan = false;

    /**
     * Creates a blank WorldObject
     *
     * @param x    the x pos
     * @param y    the y pos
     * @param size the size of the hitbox, x and y, ignore z
     **/
    public Trigger(int x, int y, int z, Vector3 size, String RawEvents, TriggerType TType) {
        super(x, y, z, size);
        RawCommands = RawEvents;

        ActivationType = TType;

        //Parse and then run the script
        String[] Lines = RawCommands.split("&#xD;&#xA;");
        for (int i = 0; i < Lines.length; i++) {
            //Replaces all the XML markup chars with real ones, or cuts them for easier parsing later
            Lines[i] = Lines[i].replace("&quot;", "");
            Lines[i] = Lines[i].replace(" ", "");
        }

        //Max of 10 Args for each command
        Commands = new String[Lines.length][10];

        //Populates the Commands array
        for (int i = 0; i < Lines.length; i++) {
            String CommandName = Lines[i].split("\\(")[0];

            if (CommandName.equals("") || CommandName.equals("Null") || CommandName.equals("null")) {
                Commands[i] = new String[]{CommandName};
                continue;
            }

            //System.out.println(CommandName);

            String params = "";

            //Double checks if the Command has parameters
            if (Lines[i].indexOf("(") != -1 && Lines[i].indexOf(")") != -1) {

                params = Lines[i].substring(
                        Lines[i].indexOf("(") + 1,
                        Lines[i].indexOf(")"));

                params = params.replace(", ", ",");

            }

            //System.out.println(params);


            String[] paramsSplit = params.split(",");

            String[] temp2 = new String[paramsSplit.length + 1];
            for (int j = 0; j < temp2.length; j++) {
                if (j <= 0) {
                    temp2[0] = CommandName;
                } else {
                    //IF PARAMS IS EMPTY THEN DONT FILL IT WITH AN EMPTY SCORE
                    temp2[j] = paramsSplit[j - 1];
                }
            }

            Commands[i] = temp2;
        }

    }

    public Trigger(int x, int y, int z, Vector3 size, type State, boolean collision, String RawEvents, TriggerType TType) {
        super(x, y, z, size, State, collision);
        RawCommands = RawEvents;

        ActivationType = TType;

        //Parse and then run the script
        String[] Lines = RawCommands.split(";");
        for (int i = 0; i < Lines.length; i++) {
            //Replaces all the XML markup chars with real ones, or cuts them for easier parsing later
            //Lines[i] = Lines[i].replace("&quot;", "");
            //Lines[i] = Lines[i].replace(" ", "");
        }

        //Max of 10 Args for each command
        Commands = new String[Lines.length][10];

        //Populates the Commands array
        for (int i = 0; i < Lines.length; i++) {
            String CommandName = Lines[i].split("\\(")[0];

            if (CommandName.equals("") || CommandName.equals("Null") || CommandName.equals("null")) {
                Commands[i] = new String[]{CommandName};
                continue;
            }

            //System.out.println(CommandName);

            String params = "";

            //Double checks if the Command has parameters
            if (Lines[i].indexOf("(") != -1 && Lines[i].indexOf(")") != -1) {

                params = Lines[i].substring(
                        Lines[i].indexOf("(") + 1,
                        Lines[i].indexOf(")"));

                params = params.replace(", ", ",");

            }

            //System.out.println(params);


            String[] paramsSplit = params.split(",");

            String[] temp2 = new String[paramsSplit.length + 1];
            for (int j = 0; j < temp2.length; j++) {
                if (j <= 0) {
                    temp2[0] = CommandName;
                } else {
                    //IF PARAMS IS EMPTY THEN DONT FILL IT WITH AN EMPTY SCORE
                    temp2[j] = paramsSplit[j - 1];
                }
            }

            Commands[i] = temp2;
        }

    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, List<Cube> Colls) {
        if(!Gdx.input.isTouched()) {
            JustRan = false;
        }

    }

    public void Trigger(WorldObject player, ScreenShakeCameraController shaker, DialogStateExtention dialog, WorldObject MainCameraFocusPoint, ParticleHandler Particles, List<WorldObject> Entities) {

        if (ActivationType.equals(TriggerType.None)) {
            return;
        }

        if (player.getHitbox().intersects(this.getHitbox())) {
            TriggerActive = true;
        } else {
            TriggerActive = false;
        }

        if (TriggerActive != TriggerRun && !TriggerRun) { //OnEntry
            if (ActivationType == TriggerType.OnEntry) {
                RunCommands(player, shaker, dialog, MainCameraFocusPoint, Particles, Entities);
            }
            TriggerRun = TriggerActive;
        } else if (TriggerActive != TriggerRun && TriggerRun) { //OnExit
            if (ActivationType == TriggerType.OnExit) {
                RunCommands(player, shaker, dialog, MainCameraFocusPoint, Particles, Entities);
            }
            TriggerRun = TriggerActive;
        }

        if (ActivationType == TriggerType.OnTrigger) {
            if (TriggerActive) {
                RunCommands(player, shaker, dialog, MainCameraFocusPoint, Particles, Entities);
            }
        }

    }

    public void Interact(WorldObject player, ScreenShakeCameraController shaker, DialogStateExtention dialog, WorldObject MainCameraFocusPoint, ParticleHandler Particles, List<WorldObject> Entities) {
        if (ActivationType == TriggerType.OnInteract) {
            RunCommands(player, shaker, dialog, MainCameraFocusPoint, Particles, Entities);
        }
    }

    public void RunCommands(WorldObject player, ScreenShakeCameraController shaker, DialogStateExtention dialog, WorldObject MainCameraFocusPoint, ParticleHandler Particles, List<WorldObject> Entities) {
        for (int i = 0; i < Commands.length; i++) {
            //System.out.println("Command " + Commands[i][0]);
            if (Commands[i][0].equals("shaker.addDamage")) { //The screen shake
                try {
                    shaker.addDamage((float) Integer.parseInt(Commands[i][1]) / 10);
                } catch (Exception e) {
                    System.out.println("Exception " + e);
                }
            } else if (Commands[i][0].equals("shaker.setDamage")) { //The screen shake
                try {
                    shaker.setDamage((float) Integer.parseInt(Commands[i][1]) / 10);
                } catch (Exception e) {
                    System.out.println("Exception " + e);
                }
            } else if (Commands[i][0].equals("Particle")) {
                try {
                    Particles.AddParticleEffect(Commands[i][1], Integer.parseInt(Commands[i][2]) * 16, Integer.parseInt(Commands[i][3]) * 16);
                } catch (Exception e) {
                    System.out.println("Exception " + e);
                }
            } else if (Commands[i][0].equals("Dialog")) {
                try {
                    if (Commands[i].length == 3) {
                        dialog.AddDialog(Commands[i][1], Commands[i][2]);
                    } else if (Commands[i].length == 4) {
                        dialog.AddDialog(Commands[i][1], Commands[i][2], Integer.parseInt(Commands[i][3]));
                    } else if (Commands[i].length == 5) { //Has the image name, in the folder sprites
                        dialog.AddDialog(Commands[i][1], Commands[i][2], Integer.parseInt(Commands[i][3]), new Texture(Gdx.files.internal(Commands[i][4])));
                    }
                } catch (Exception e) {
                    System.out.println("Exception " + e);
                }
            } else if (Commands[i][0].equals("setFocusPoint")) {
                try {
                    WorldObject temp = new WorldObject(Integer.parseInt(Commands[i][1]), Integer.parseInt(Commands[i][2]), 0, new Vector3()) {
                        @Override
                        public void init(int Width, int Height) {

                        }

                        @Override
                        public void update(float delta, List<Cube> Colls) {

                        }

                        @Override
                        public void draw(SpriteBatch batch, float Time) {

                        }
                    };
                    temp.FocusStrength = Integer.parseInt(Commands[i][3]);
                    Entities.add(temp);
                } catch (Exception e) {
                    System.out.println("Exception " + e);
                }
            } else if (Commands[i][0].equals("Player")) {
                try {

                } catch (Exception e) {
                    System.out.println("Exception " + e);
                }
            } else if (Commands[i][0].equals("")) {
                try {

                } catch (Exception e) {
                    System.out.println("Exception " + e);
                }
            } else {
                System.out.println("Command " + Commands[i][0] + " not implemented or does not exist. ");
            }
            for (int j = 1; j < Commands[i].length; j++) {
                //System.out.println("Parameters " + Commands[i][j]);
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

    }

    public TriggerType getActivationType() {
        return ActivationType;
    }

    public void setActivationType(TriggerType activationType) {
        ActivationType = activationType;
    }

    public String getRawCommands() {
        return RawCommands;
    }

    public void setRawCommands(String rawCommands) {
        RawCommands = rawCommands;
    }
}

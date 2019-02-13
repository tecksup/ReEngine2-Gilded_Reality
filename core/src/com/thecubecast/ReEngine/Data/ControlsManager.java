package com.thecubecast.ReEngine.Data;

import com.badlogic.gdx.Input;

public class ControlsManager {

    enum InputType {
        Button {
            Input.Buttons SavedButton;
        },
        Controller {
            ControlerManager.buttons savedBut;
            ControlerManager.axisies savedAxis;
            ControlerManager.POVs savedPov;
        },
        Keyboard {
            Input.Keys SavedKey;
        };
    }

    enum ControlInputs {
        Forward,
        Backward,
        Left,
        Right,
        Inventory,
        Crafting;

        InputType StoredInput;

        ControlInputs() {

        }

    }

    public ControlsManager() {

    }

    public boolean isJustPressed() {
        return false;
    }

}

package com.thecubecast.ReEngine.worldObjects.AI;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.thecubecast.ReEngine.Data.Common;

public enum Student_State implements State<Student_Overworld> {

    IDLE() {
        @Override
        public void enter(Student_Overworld Student) { // INIT

        }

        @Override
        public void update(Student_Overworld Student) {
            //Check for changes, then update state

        }

        @Override
        public void exit(Student_Overworld Student) {

        }

        @Override
        public boolean onMessage(Student_Overworld Student, Telegram telegram) {
            return false;
        }
    },

    WANDER() {
        @Override
        public void enter(Student_Overworld Student) { // INIT

        }

        @Override
        public void update(Student_Overworld Student) {
            //Check for changes, then update state
            //Student.WorldObject.setPosition(Student.WorldObject.getPosition().x + 1, Student.WorldObject.getPosition().y);

        }

        @Override
        public void exit(Student_Overworld Student) {

        }

        @Override
        public boolean onMessage(Student_Overworld Student, Telegram telegram) {
            return false;
        }
    },

    WALKING_TO_DESTINATION() {
        @Override
        public void enter(Student_Overworld Student) { // INIT
            Student.updatePath(true);
        }

        @Override
        public void update(Student_Overworld Student) {
            //Check for changes, then update state

        }

        @Override
        public void exit(Student_Overworld Student) { // Leave this state

        }

        @Override
        public boolean onMessage(Student_Overworld Student, Telegram telegram) {
            return false;
        }
    }

}

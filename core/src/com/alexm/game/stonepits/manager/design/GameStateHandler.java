package com.alexm.game.stonepits.manager.design;

/**
 * Base default Game State Handler who does nothing.
 * Need to inherit and override method for handling actions
 * Implements <b>State Behavioral Design Pattern</b>
 */
public class GameStateHandler {

    public GameStateHandler() {
    }

    /**
     * Processing of TouchUP event as input for Scene actions
   	 * @param screenX coordinate
   	 * @param screenY coordinate
     * @param pointer index of multi touch events, not used here
     * @param button code
     * @return whether input was finally processed
   	 */
    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        //do nothing
        return false;
    }

    /**
     * Processing of Mouse Move event as input for Scene rendering
     * @param screenX coordinate
     * @param screenY coordinate
     * @return whether input was finally processed
     */
    public boolean mouseMoved(int screenX, int screenY){
        //do nothing
        return false;
    }
}

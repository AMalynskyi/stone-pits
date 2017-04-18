package com.alexm.game.stonepits.manager.design;

import com.alexm.game.stonepits.manager.GameSceneManagerStrategy;

/**
 * Handling of action of playing game start after click as input during message with player turn text
 *
 * Implements <b>State Behavioral Design Pattern</b>
 */
public class StartedStateHandler extends GameStateHandler{

    private GameSceneManagerStrategy gameManager;

    public StartedStateHandler(GameSceneManagerStrategy gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        gameManager.letsGO();

        return true;
    }
}

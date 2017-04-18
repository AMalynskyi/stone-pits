package com.alexm.game.stonepits.manager.design;

import com.alexm.game.stonepits.manager.GameSceneManagerStrategy;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

/**
 * Handling of actions during game play rendering:
 * - capture and drop stones by clicking middle and left mouse
 * - draw flying stones after cursor
 *
 * Implements <b>State Behavioral Design Pattern</b>
 */
public class PlayStateHandler extends GameStateHandler {

    private GameSceneManagerStrategy gameManager;
    Vector3 unprojectVec = new Vector3();

    public PlayStateHandler(GameSceneManagerStrategy gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //game play
        //To get correct world position of touch point or mouse cursor it is necessary to
        // unproject the raw screen position coordinates with camera that operates in world space.
        unprojectVec.set(screenX, screenY, 0);
        gameManager.getCameraManager().getCamera().unproject(unprojectVec);

        float x = unprojectVec.x;
        float y = unprojectVec.y;

        //action for dropping stone
        if (button == Input.Buttons.MIDDLE) {
            gameManager.processMiddleButton(x, y);
        } else if (button == Input.Buttons.LEFT) { //capture stones from pit
            gameManager.processLeftButton(x, y);
        }

        return true;
    }

    /**
     * When stones are flying after cursor need to changes their position every rendering mouse move
     * @param screenX cursor coordinate
     * @param screenY cursor coordinate
     * @return whether input was finally processed
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        unprojectVec.set(screenX, screenY, 0);
        gameManager.getCameraManager().getCamera().unproject(unprojectVec);

        float x = unprojectVec.x;
        float y = unprojectVec.y;

        gameManager.flyStones(x, y);

        return true;
    }
}

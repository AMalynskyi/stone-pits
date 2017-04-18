package com.alexm.game.stonepits.manager.design;

import com.alexm.game.stonepits.StonePits;
import com.alexm.game.stonepits.manager.MenuSceneManagerStrategy;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.runtime.component.Transform;

/**
 * Handling of Menu state actions:
 * - clicking on quit or play
 * - switching sounds on and off
 *
 * Implements <b>State Behavioral Design Pattern</b>
 */
public class MenuStateHandler extends GameStateHandler {

    MenuSceneManagerStrategy menuManager;

    public MenuStateHandler(MenuSceneManagerStrategy menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 unprojectVec = new Vector3();
        unprojectVec.set(screenX, screenY, 0);
        menuManager.getCameraManager().getCamera().unproject(unprojectVec);

        float x = unprojectVec.x;
        float y = unprojectVec.y;

        if (menuManager.getPlaySprite().contains(x, y)) {    //start game
            menuManager.getGame().getSceneFactory().loadScene(StonePits.GameState.START);
        }

        if (menuManager.getQuitSprite().contains(x, y)) {    //exit, make sense for desktop
            Gdx.app.exit();
        }

        if (menuManager.getSoundOnBounds().contains(x, y) || menuManager.getSoundOffBounds().contains(x, y)) { //switch off\on sound
            menuManager.getSoundManager().resetSound();
            swapSpritesPosition(menuManager.getSoundOnEntity(), menuManager.getSoundOffEntity());
        }
        return true;
    }

	/**
     * If you have a several sprites for the same action
     * Hide one (out of screen) and show another
     * @param entity1 swap with other
     * @param entity2 swap with other
     */
    public void swapSpritesPosition (Entity entity1, Entity entity2) {
          Transform transform1 = menuManager.getTransformCm().get(entity1);
          Transform transform2 = menuManager.getTransformCm().get(entity2);

          float xPos = transform1.getX(), yPos = transform1.getY();
          transform1.setPosition(transform2.getX(), transform2.getY());
          transform2.setPosition(xPos, yPos);
    }
}

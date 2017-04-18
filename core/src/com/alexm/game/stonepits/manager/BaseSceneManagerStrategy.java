package com.alexm.game.stonepits.manager;

import com.alexm.game.stonepits.StonePits;
import com.alexm.game.stonepits.entity.component.Bounds;
import com.alexm.game.stonepits.manager.design.GameStateHandler;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisGroup;
import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.system.CameraManager;
import com.kotcrab.vis.runtime.system.VisIDManager;
import com.kotcrab.vis.runtime.util.AfterSceneInit;

/**
 * Parent abstract manager strategy for scene processing.
 * Implements <b>Strategy Behavioral Design Pattern</b>
 */
public abstract class BaseSceneManagerStrategy extends Manager implements InputProcessor, AfterSceneInit {

    /*Injections for component mappers*/
    protected ComponentMapper<Bounds> boundsCm;
   	protected ComponentMapper<Transform> transformCm;
   	protected ComponentMapper<VisSprite> spriteCm;
    protected ComponentMapper<VisGroup> groupCm;

    /*Manager injections*/
    protected VisIDManager idManager;
   	protected CameraManager cameraManager;

    /*Application game objects*/
    protected SoundManager soundManager;
    protected StonePits game;

    /**
     *  Coordinates vector used to convert frame coordinates into game world coordinates
     *  E.g. 1 game world unit can include 10 frame pixels
     */
    protected Vector3 unprojectVec = new Vector3();

    /**
     * Create instance passing game to pull threads
     * @param game application
     */
    public BaseSceneManagerStrategy(StonePits game) {
   		this.game = game;
   		this.soundManager = game.getSoundManager();
   	}

    /**
     * @param id entity id
     * @return Return Bounds for entity by Id
     */
    protected Bounds getSpriteBounds (String id) {
   		Entity entity = idManager.get(id);
   		return boundsCm.get(entity);
   	}

    /**
     * Assign current manager as input processor for game
     */
    @Override
    public void afterSceneInit() {
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Processing of TouchUP event as input for Scene actions
   	 * @param screenX coordinate
   	 * @param screenY coordinate
     * @param pointer index of multi touch events, not used here
     * @param button code
     * @return whether input was finally processed
   	 */
     @Override
     public boolean touchUp(int screenX, int screenY, int pointer, int button) {

         GameStateHandler handler = game.getStateHandlers().get(game.getGameState());

         return handler.touchUp(screenX, screenY, pointer, button);
     }

    /**
     * React on mouse movement input during rendering
     * @param screenX mouse coordinate
     * @param screenY mouse coordinate
     * @return whether input was finally processed
     */
     @Override
     public boolean mouseMoved(int screenX, int screenY) {
         GameStateHandler handler = game.getStateHandlers().get(game.getGameState());

         return handler.mouseMoved(screenX, screenY);
     }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public ComponentMapper<Transform> getTransformCm() {
        return transformCm;
    }

    public StonePits getGame() {
        return game;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }
}

package com.alexm.game.stonepits.manager;

import com.alexm.game.stonepits.StonePits;
import com.alexm.game.stonepits.entity.component.Bounds;
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
import com.kotcrab.vis.runtime.system.VisGroupManager;
import com.kotcrab.vis.runtime.system.VisIDManager;
import com.kotcrab.vis.runtime.util.AfterSceneInit;

/**
 *
 * Base managed for scene processing
 *
 */
public abstract class BaseSceneManager extends Manager implements InputProcessor, AfterSceneInit {

    /**Injections*/
    protected ComponentMapper<Bounds> boundsCm;
   	protected ComponentMapper<Transform> transformCm;
   	protected ComponentMapper<VisSprite> spriteCm;
    protected ComponentMapper<VisGroup> groupCm;

    protected VisIDManager idManager;

   	protected SoundManager soundManager;
    protected StonePits game;

   	protected CameraManager cameraManager;
    protected Vector3 unprojectVec = new Vector3();

    /**
     * Create instance passing game to pull threads
     * @param game application
     */
    public BaseSceneManager (StonePits game) {
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

    @Override
    public void afterSceneInit() {
        Gdx.input.setInputProcessor(this);
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
}

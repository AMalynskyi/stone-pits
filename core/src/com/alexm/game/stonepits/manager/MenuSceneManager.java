package com.alexm.game.stonepits.manager;

import com.alexm.game.stonepits.StonePits;
import com.alexm.game.stonepits.entity.component.Bounds;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.runtime.component.Transform;

/**
 * Manager for menu scene processing
 *
 */
public class MenuSceneManager extends BaseSceneManager{

    private Bounds playSprite;
   	private Bounds quitSprite;

   	private Entity soundOnEntity;
   	private Entity soundOffEntity;

   	private Bounds soundOnBounds;
   	private Bounds soundOffBounds;

    /**
     * Create instance passing game to pull threads
     *
     * @param game application
     */
    public MenuSceneManager(StonePits game) {
        super(game);
    }

    @Override
    public void afterSceneInit() {
        super.afterSceneInit();

        soundManager.playMenuTheme();

      	playSprite = getSpriteBounds("play");
      	quitSprite = getSpriteBounds("quit");

      	soundOnEntity = idManager.get("soundon");
      	soundOffEntity = idManager.get("soundoff");

      	soundOnBounds = boundsCm.get(soundOnEntity);
      	soundOffBounds = boundsCm.get(soundOffEntity);

    }

    /**
     * If you have several sprites for same action
     * Hide one (out of screen) and show another
     * @param entity1 swap with other
     * @param entity2 swap with other
     */
    public void swapSpritesPosition (Entity entity1, Entity entity2) {
   		Transform transform1 = transformCm.get(entity1);
   		Transform transform2 = transformCm.get(entity2);

   		float xPos = transform1.getX(), yPos = transform1.getY();
   		transform1.setPosition(transform2.getX(), transform2.getY());
   		transform2.setPosition(xPos, yPos);
   	}


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        unprojectVec.set(screenX, screenY, 0);
        cameraManager.getCamera().unproject(unprojectVec);

      	float x = unprojectVec.x;
      	float y = unprojectVec.y;

      	if (playSprite.contains(x, y)) {
      		game.loadGameScene();
      	}

      	if (quitSprite.contains(x, y)) {
      		Gdx.app.exit();
      	}

      	if (soundOnBounds.contains(x, y) || soundOffBounds.contains(x, y)) {
      		soundManager.resetSound();
      		swapSpritesPosition(soundOnEntity, soundOffEntity);
      	}

      	return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
}

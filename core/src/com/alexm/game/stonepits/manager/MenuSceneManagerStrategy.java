package com.alexm.game.stonepits.manager;

import com.alexm.game.stonepits.StonePits;
import com.alexm.game.stonepits.entity.component.Bounds;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.runtime.component.Transform;

/**
 * Manager for menu scene processing
 */
public class MenuSceneManagerStrategy extends BaseSceneManagerStrategy {

	/*Menu actions entities*/

    private Bounds playSprite;
   	private Bounds quitSprite;

	private Bounds soundOnBounds;
	private Bounds soundOffBounds;

   	private Entity soundOnEntity;
   	private Entity soundOffEntity;

    /**
     * Create instance passing game to pull threads
     *
     * @param game application
     */
    public MenuSceneManagerStrategy(StonePits game) {
        super(game);
	    game.getStateHandlers().put(StonePits.GameState.MENU, new MenuStateHandler());
    }

	/**
	 * Retrieve references to game actions entities
	 */
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
	 * Handling of Menu state actions:
	 * - clicking on quit or play
	 * - switching sounds on&off
	 *
	 * Implements <b>State Behavioral Design Pattern</b>
	 */
	public class MenuStateHandler extends GameStateHandler {

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			unprojectVec.set(screenX, screenY, 0);
 		    cameraManager.getCamera().unproject(unprojectVec);

		    float x = unprojectVec.x;
		    float y = unprojectVec.y;

		    if (playSprite.contains(x, y)) {    //start game
		        game.getSceneFactory().loadScene(StonePits.GameState.START);
		    }

		    if (quitSprite.contains(x, y)) {    //exit, make sense for desktop
		        Gdx.app.exit();
		    }

		    if (soundOnBounds.contains(x, y) || soundOffBounds.contains(x, y)) { //switch off\on sound
		        soundManager.resetSound();
		        swapSpritesPosition(soundOnEntity, soundOffEntity);
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
		      Transform transform1 = transformCm.get(entity1);
		      Transform transform2 = transformCm.get(entity2);

		      float xPos = transform1.getX(), yPos = transform1.getY();
		      transform1.setPosition(transform2.getX(), transform2.getY());
		      transform2.setPosition(xPos, yPos);
		}
	}
}

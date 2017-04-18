package com.alexm.game.stonepits.manager;

import com.alexm.game.stonepits.StonePits;
import com.alexm.game.stonepits.entity.component.Bounds;
import com.alexm.game.stonepits.manager.design.MenuStateHandler;
import com.artemis.Entity;

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
	    game.getStateHandlers().put(StonePits.GameState.MENU, new MenuStateHandler(this));
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

	public Bounds getPlaySprite() {
		return playSprite;
	}

	public Bounds getQuitSprite() {
		return quitSprite;
	}

	public Bounds getSoundOnBounds() {
		return soundOnBounds;
	}

	public Bounds getSoundOffBounds() {
		return soundOffBounds;
	}

	public Entity getSoundOnEntity() {
		return soundOnEntity;
	}

	public Entity getSoundOffEntity() {
		return soundOffEntity;
	}
}

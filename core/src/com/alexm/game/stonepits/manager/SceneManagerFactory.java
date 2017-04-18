package com.alexm.game.stonepits.manager;

import com.alexm.game.stonepits.StonePits;
import com.alexm.game.stonepits.entity.system.FulfillMainScene;
import com.alexm.game.stonepits.entity.system.PitsSystem;
import com.alexm.game.stonepits.entity.system.SpriteBoundsSystem;
import com.artemis.BaseSystem;
import com.artemis.managers.PlayerManager;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.scene.SceneConfig;
import com.kotcrab.vis.runtime.scene.SceneFeature;
import com.kotcrab.vis.runtime.scene.SceneLoader;
import com.kotcrab.vis.runtime.scene.SystemProvider;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;

/**
 * Factory for game scene managed strategies creation and scene loading.
 * Implements <b>Factory Class Design Pattern</b>
 */
public class SceneManagerFactory {

    private StonePits game;

    private String scenePath;

    public SceneManagerFactory(StonePits game) {
        this.game = game;
    }

    public void loadScene(StonePits.GameState state){
        switch (state){
            case START:
                unloadPreviousScene();
                loadGameScene(state);
	            return;
            case MENU:
                unloadPreviousScene();
                loadMenuScene(state);
	            return;
            default:
                throw new IllegalStateException("Undefined state: [" + state + "] for loading scene");
        }
    }

    /**
   	 * Properly unload previous scene to release all resources
   	 */
   	private void unloadPreviousScene () {
   		if (scenePath != null) {
   			game.getAssetManager().unload(scenePath);
   			scenePath = null;
   			game.setScene(null);
   		}
   	}

    /**
   	 * Load menu scene
   	 */
   	private void loadMenuScene (StonePits.GameState state) {
   		unloadPreviousScene();

   		//parameter to pass required  configurations and systems with defined execution priority
   		SceneLoader.SceneParameter parameter = new SceneLoader.SceneParameter();
   		parameter.config.addSystem(SpriteBoundsSystem.class, SceneConfig.Priority.HIGHEST);
   		parameter.config.addSystem(new SystemProvider() {
   			public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
   				return new MenuSceneManagerStrategy(game);
   			}
   		});
   		parameter.config.enable(SceneFeature.GROUP_ID_MANAGER);

   		scenePath = game.getMenuScenePath();
   		game.setScene(game.getAssetManager().loadSceneNow(scenePath, parameter));

   		game.setGameState(state);
   	}

    /**
   	 * Load scene of game play
   	 */
   	private void loadGameScene (StonePits.GameState state) {
   		unloadPreviousScene();

   		//parameter to pass required  configurations and systems with defined execution priority
   		SceneLoader.SceneParameter parameter = new SceneLoader.SceneParameter();
   		parameter.config.addSystem(SpriteBoundsSystem.class, SceneConfig.Priority.HIGHEST);
   		parameter.config.addSystem(PlayerManager.class, SceneConfig.Priority.HIGHEST);
   		parameter.config.addSystem(PitsSystem.class, SceneConfig.Priority.HIGH);
   		parameter.config.addSystem(new SystemProvider() {
   			public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {

   				return new FulfillMainScene(game);
   			}
   		});
   		parameter.config.addSystem(new SystemProvider() {
   			public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
   				return new GameSceneManagerStrategy(game);
   			}
   		}, SceneConfig.Priority.LOW);

   		parameter.config.enable(SceneFeature.GROUP_ID_MANAGER);

   		scenePath = game.getGameScenePath();
   		game.setScene(game.getAssetManager().loadSceneNow(scenePath, parameter));

        game.setGameState(state);
   	}



}

package com.alexm.game.stonepits;


import com.alexm.game.stonepits.entity.system.FulfillMainScene;
import com.alexm.game.stonepits.entity.system.PitsSystem;
import com.alexm.game.stonepits.entity.system.SpriteBoundsSystem;
import com.alexm.game.stonepits.manager.GameSceneManager;
import com.alexm.game.stonepits.manager.MenuSceneManager;
import com.alexm.game.stonepits.manager.SoundManager;
import com.artemis.BaseSystem;
import com.artemis.managers.PlayerManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.Logger;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.scene.*;
import com.kotcrab.vis.runtime.system.VisGroupManager;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;


public class StonePits extends ApplicationAdapter {
	private SpriteBatch batch;
	private VisAssetManager manager;
	private SoundManager soundManager;
	private VisGroupManager groupManager;

	private String scenePath;
	private Scene scene;

	public enum GameState {ERROR, PREPARE, MENU, RUNNING, MESSAGE, GAMEOVER, DISPOSE}

	private GameState gameState;

	public StonePits() {
		gamePreparing();
	}

	@Override
	public void create () {

		try {
			GLProfiler.enable();
			GLProfiler.listener = GLErrorListener.THROWING_LISTENER;

			if(batch == null)
                batch = new SpriteBatch();

			manager = new VisAssetManager(batch);
			manager.getLogger().setLevel(Logger.ERROR);

			soundManager = new SoundManager(manager);

			loadMenuScene();
		} catch (RuntimeException e) {
			Gdx.app.error("INIT", "GAME INIT ERROR", e);
			gameError();
			throw e;
		}
	}

	private void unloadPreviousScene () {
		if (scenePath != null) {
			manager.unload(scenePath);
			scenePath = null;
			scene = null;
		}
	}

	public void loadMenuScene () {
		unloadPreviousScene();

		SceneLoader.SceneParameter parameter = new SceneLoader.SceneParameter();
		parameter.config.addSystem(SpriteBoundsSystem.class, SceneConfig.Priority.HIGHEST);
		parameter.config.addSystem(new SystemProvider() {
			public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
				return new MenuSceneManager(StonePits.this);
			}
		});
		parameter.config.enable(SceneFeature.GROUP_ID_MANAGER);

		scenePath = getMenuScenePath();
		scene = manager.loadSceneNow(scenePath, parameter);
		gameMenu();
	}

	public void loadGameScene () {
		unloadPreviousScene();

		SceneLoader.SceneParameter parameter = new SceneLoader.SceneParameter();
		parameter.config.addSystem(SpriteBoundsSystem.class, SceneConfig.Priority.HIGHEST);
		parameter.config.addSystem(PlayerManager.class, SceneConfig.Priority.HIGHEST);
		parameter.config.addSystem(PitsSystem.class, SceneConfig.Priority.HIGH);
		parameter.config.addSystem(new SystemProvider() {
			public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {

				return new FulfillMainScene(StonePits.this);
			}
		});
		parameter.config.addSystem(new SystemProvider() {
			public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
				return new GameSceneManager(StonePits.this);
			}
		}, SceneConfig.Priority.LOW);

		parameter.config.enable(SceneFeature.GROUP_ID_MANAGER);

		scenePath = getGameScenePath();
		scene = manager.loadSceneNow(scenePath, parameter);
	}

	@Override
	public void render () {
		try {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			scene.render();
		} catch (RuntimeException e) {
			Gdx.app.error("RENDER", "GAME RENDER ERROR", e);
			gameError();
			throw e;
		}
	}

	@Override
	public void resize (int width, int height) {
		try {
			scene.resize(width, height);
		} catch (RuntimeException e) {
			Gdx.app.error("RESIZE", "GAME RESIZE ERROR", e);
			gameError();
			throw e;
		}
	}

	@Override
	public void dispose () {
		try {
			batch.dispose();
			manager.dispose();
			gameDispose();
		} catch (RuntimeException e) {
			Gdx.app.error("DISPOSE", "GAME DISPOSE ERROR", e);
			gameError();
			throw e;
		}
	}

	public VisAssetManager getAssetManager() {
		return manager;
	}

	public void gamePreparing(){
		gameState = GameState.PREPARE;
	}

	public void gameDispose(){
		gameState = GameState.DISPOSE;
	}

	public void gameMenu(){
			gameState = GameState.MENU;
		}

	public void gameRunning(){
		gameState = GameState.RUNNING;
	}

	public void gameOver(){
		gameState = GameState.GAMEOVER;
	}

	public void gameMessage(){
		gameState = GameState.MESSAGE;
	}

	public void gameError(){
			gameState = GameState.ERROR;
		}

	public boolean isGameMenu(){
				return gameState == GameState.MENU;
			}

	public boolean isGameMessage(){
				return gameState == GameState.MESSAGE;
			}

	public boolean isGameDisposed(){
			return gameState == GameState.DISPOSE;
		}

	public boolean isGameRunning(){
		return gameState == GameState.RUNNING;
	}

	public boolean isGamePreparing(){
		return gameState == GameState.PREPARE;
	}

	public boolean isGameError(){
			return gameState == GameState.ERROR;
		}

	public GameState getGameState(){
		return gameState;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}

	public String getMenuScenePath() {
		return "scene/Menu.scene";
	}

	public String getGameScenePath() {
		return "scene/MainFrame.scene";
	}

	public Scene getScene() {
		return scene;
	}
}
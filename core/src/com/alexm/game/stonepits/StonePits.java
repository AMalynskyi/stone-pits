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
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.Logger;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.data.LayerData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.scene.*;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;


public class StonePits extends ApplicationAdapter {
	private SpriteBatch batch;
	private VisAssetManager manager;
	private SoundManager soundManager;

	private String scenePath;
	private Scene scene;

	public enum GameState {PREPARE, RUNNING, MESSAGE, GAMEOVER}

	private GameState gameState;

	FPSLogger logger = new FPSLogger();

	LayerData stoneLayer;

	@Override
	public void create () {

		GLProfiler.enable();
		GLProfiler.listener = GLErrorListener.THROWING_LISTENER;

		batch = new SpriteBatch();

		manager = new VisAssetManager(batch);
		manager.getLogger().setLevel(Logger.ERROR);

		soundManager = new SoundManager(manager);

		loadMenuScene();
	}

	@Override
	public void render () {
		logger.log();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		scene.render();
	}

	@Override
	public void resize (int width, int height) {
		scene.resize(width, height);
	}

	@Override
	public void dispose () {
		batch.dispose();
		manager.dispose();
	}

	public void gamePreparing(){
		gameState = GameState.PREPARE;
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

	public boolean isGameRunning(){
		return gameState == GameState.RUNNING;
	}

	public GameState getGameState(){
		return gameState;
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

//		soundManager.playMenuTheme();

		SceneLoader.SceneParameter parameter = new SceneLoader.SceneParameter();
		parameter.config.addSystem(SpriteBoundsSystem.class, SceneConfig.Priority.HIGHEST);
		parameter.config.addSystem(new SystemProvider() {
			public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
				return new MenuSceneManager(StonePits.this);
			}
		});
		parameter.config.enable(SceneFeature.GROUP_ID_MANAGER);

		scenePath = "scene/Menu.scene";
		scene = manager.loadSceneNow(scenePath, parameter);
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

		scenePath = "scene/MainFrame.scene";
		scene = manager.loadSceneNow(scenePath, parameter);
		stoneLayer = scene.getLayerDataByName("Motion");
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}



}
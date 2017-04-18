package com.alexm.game.stonepits;


import com.alexm.game.stonepits.manager.design.GameStateHandler;
import com.alexm.game.stonepits.manager.design.SceneManagerFactory;
import com.alexm.game.stonepits.manager.SoundManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.Logger;
import com.kotcrab.vis.runtime.scene.*;

import java.util.HashMap;

/**
 * Game application class for processing all high level game actions
 */
public class StonePits extends ApplicationAdapter {

	/*References for game drivers*/

	private SpriteBatch batch;
	private VisAssetManager manager;
	private SoundManager soundManager;

	/**Current scene*/
	private Scene scene;

	/**Game States enum*/
	public enum GameState {ERROR, PREPARE, MENU, RUNNING, START, GAMEOVER, DISPOSE}

	/**Current state of game*/
	private GameState gameState;

	/**Map for game state handlers*/
	private HashMap<GameState, GameStateHandler> stateHandlers = new HashMap<>();

	private SceneManagerFactory sceneFactory;

	private static StonePits instance;

	private StonePits() {
		gamePreparing();
	}

	/**
	 * Implementation of <b>Singleton Design Pattern</b>
	 * @return game instance
	 */
	public static StonePits getInstance(){
		if(instance == null)
			instance = new StonePits();
		return instance;
	}

	/**
	 * Create application instance with initialization and Menu scene loading
	 */
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

			sceneFactory = new SceneManagerFactory(this);

			sceneFactory.loadScene(GameState.MENU);

		} catch (RuntimeException e) {
			Gdx.app.error("INIT", "GAME INIT ERROR", e);
			gameError();
			throw e;
		}
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

	public HashMap<GameState, GameStateHandler> getStateHandlers() {
		return stateHandlers;
	}

	public SceneManagerFactory getSceneFactory() {
		return sceneFactory;
	}

	public VisAssetManager getAssetManager() {
		return manager;
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

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	/*Game State methods*/

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

	public void gameStart(){
		gameState = GameState.START;
	}

	public void gameError(){
			gameState = GameState.ERROR;
		}

	public boolean isGameMenu(){
				return gameState == GameState.MENU;
			}

	public boolean isGameStarted(){
				return gameState == GameState.START;
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

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
}
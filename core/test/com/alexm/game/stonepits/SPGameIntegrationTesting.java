package com.alexm.game.stonepits;

import com.alexm.game.stonepits.manager.SoundManager;
import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.runtime.scene.VisAssetManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration testing suite with special runner for Gdx application starting in parallel thread
 * Marked as IntegrationTestCategory to separate it from regular build executions
 *
 * Tests execution sorted by name asc because of game integration testing depends on current scene
 * and sequence of inputs
 */
@Category(IntegrationTestCategory.class)
@RunWith(SPGameContextTestRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SPGameIntegrationTesting {

    static StonePits game;
    static SoundManager soundMan;
    static VisAssetManager assetMan;

    /**
     * Retrieve game managers from Gdx context
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        game = ((StonePits) Gdx.app.getApplicationListener());
        soundMan = game.getSoundManager();
        assetMan = game.getAssetManager();
    }

    /**
     * Check game for error that might happened in parallel thread before every test
     * @throws Exception
     */
    @Before
    public void checkGameState() throws Exception {
        assertThat(game.isGameError()).isFalse();
    }

    /**
     * Check that sounds&music are loaded into the game
     * @throws Exception if unable to load sounds
     */
    @Test
    public void test1SoundAssetsLoaded() throws Exception{

        assertThat(assetMan.isLoaded(assetMan.getAssetFileName(soundMan.drop))).isTrue();
        assertThat(assetMan.isLoaded(assetMan.getAssetFileName(soundMan.grab))).isTrue();
        assertThat(assetMan.isLoaded(assetMan.getAssetFileName(soundMan.over))).isTrue();
        assertThat(assetMan.isLoaded(assetMan.getAssetFileName(soundMan.win))).isTrue();
        assertThat(assetMan.isLoaded(assetMan.getAssetFileName(soundMan.mainTheme))).isTrue();
        assertThat(assetMan.isLoaded(assetMan.getAssetFileName(soundMan.menuTheme))).isTrue();

    }

    /**
     * Check availability for scene files from system resources
     * Loading of scene is possible to check only inside of OpenGL context Thread
     * But JUnit test is a parallel thread
     * @throws Exception if unable to read files
     */
    @Test
    public void test2ScenesLoading() throws Exception{

        assertThat(Gdx.files.local(game.getMenuScenePath()).read()).isNotNull();

        assertThat(Gdx.files.local(game.getGameScenePath()).read()).isNotNull();

    }

    /**
     * Test game scene loading successfully after simulating click input on a 'play' menu item
     * @throws Exception
     */
    @Test
    public void test3LoadingGameScene() throws Exception {

        //click on play
        Gdx.app.postRunnable(
                () -> Gdx.input.getInputProcessor().touchUp(389, 160, 0, 0));

        SPGameContextTestRunner.waitStage("Wait for Game Play Loading", game, StonePits::isGameMenu);

        assertThat(game.isGameMessage()).isTrue();
    }

}

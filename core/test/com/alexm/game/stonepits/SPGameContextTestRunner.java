package com.alexm.game.stonepits;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.audio.MockAudio;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.function.Predicate;

/**
 *
 * Base class for testing
 * Contain initialisation and cleanup of stab application
 * Execute all tests in Game App context
 */
public class SPGameContextTestRunner extends BlockJUnit4ClassRunner {

    private static Application application;

    private static StonePits game;

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code testClass}
     *
     * @throws org.junit.runners.model.InitializationError if the test class is malformed.
     */
    public SPGameContextTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {

        //wait for game loading to start testing

        waitStage("Wait for Game Preparing", game, StonePits::isGamePreparing);

        return super.classBlock(notifier);
    }

    @Override
    public void run(RunNotifier notifier) {

        notifier.addListener(new GdxSPGameContextListener());

        super.run(notifier);
    }

    public class GdxSPGameContextListener extends RunListener {

        @Override
        public void testSuiteStarted(Description description) throws Exception {
            if(application == null){
                game = new StonePits();

                LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
              		config.title = "test";
              		config.height=200;
              		config.width=540;
              		config.resizable=false;
                    config.forceExit=false;

                application = new LwjglApplication(game, config);
                Gdx.audio = new MockAudio();
            }
        }

        @Override
        public void testSuiteFinished(Description description) throws Exception {
            Gdx.app.exit();

            //wait to dispose game and it's resources (audio, graphics ,etc)
            waitStage("Wait for Game Disposing", game,
                    game -> !game.isGameDisposed());
        }

    }

    /**
     * Thread waiting for parallel game thread become in a proper state for testing
     * @param msg message for test console to see stage wait
     * @param obj object of game app listener to retrieve  state value
     * @param check state condition
     * @param <T> type of game app listener
     */
    public static <T> void waitStage(String msg, T obj, Predicate<T> check){

        System.out.println();
        System.out.print(msg);

        long sleep = 0;
        try {
            while (check.test(obj)){
                System.out.print(".");
                    Thread.sleep(100);
                sleep += 100;
                if(sleep > 10000)
                    break;
            }
            System.out.println();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}


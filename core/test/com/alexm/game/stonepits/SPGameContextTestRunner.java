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
 * Contain initialisation and cleanup of testing application
 * Gdx starts app in a separate thread with graphics OpenGL context available only in that Thread
 * To test graphic related behavior need to attach and wait asynchronously for results
 */
public class SPGameContextTestRunner extends BlockJUnit4ClassRunner {

    private static Application application;

    private static StonePits game;

    public SPGameContextTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {

        //wait for application loading to start testing
        waitStage("Wait for Game Preparing", game, StonePits::isGamePreparing);

        return super.classBlock(notifier);
    }

    @Override
    public void run(RunNotifier notifier) {

        //listener to catch events of starting and finishing testing
        notifier.addListener(new GdxSPGameContextListener());

        super.run(notifier);
    }

    /**
     * Listener to catch events of starting and finishing testing
     */
    public class GdxSPGameContextListener extends RunListener {

        /**
         * When suite started need to start Gdx Application
         * Gdx will run app in a separate thread
         */
        @Override
        public void testSuiteStarted(Description description) throws Exception {
            if(application == null){
                game = StonePits.getInstance();

                LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
              		config.title = "test";
              		config.height=200;
              		config.width=540;
              		config.resizable=false;
                    config.forceExit=false;

                application = new LwjglApplication(game, config);
                Gdx.audio = new MockAudio(); //not to produce noise during testing
            }
        }

        /**
         * When suite testing is finished send exit to application and wait parallel Gdx thread to complete releasing resources
         */
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


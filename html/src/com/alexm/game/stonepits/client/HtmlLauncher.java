package com.alexm.game.stonepits.client;

import com.alexm.game.stonepits.StonePits;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(540, 200);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new StonePits();
        }
}
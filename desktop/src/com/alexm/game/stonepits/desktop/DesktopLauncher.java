package com.alexm.game.stonepits.desktop;

import com.alexm.game.stonepits.StonePits;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Stone Pits";
		config.height=200;
		config.width=540;
		config.resizable=false;
		new LwjglApplication(new StonePits(), config);
	}
}

package com.alexm.game.stonepits.manager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.kotcrab.vis.runtime.component.VisSound;
import com.kotcrab.vis.runtime.scene.VisAssetManager;

import java.util.Iterator;

/**
 * User: Oleksandr Malynskyi
 * Date: 03/18/17
 */
public class SoundManager{

    private boolean enabled = true;
   	private Music mainTheme, menuTheme, music;
   	private Sound grab, drop, win, over;

   	public SoundManager (VisAssetManager manager) {

   		manager.load("music/main.mp3", Music.class);
	    manager.load("music/menu.mp3", Music.class);
        manager.load("sound/grab.wav", Sound.class);
        manager.load("sound/land.wav", Sound.class);
        manager.load("sound/over.mp3", Sound.class);
        manager.load("sound/win.wav", Sound.class);

   		manager.finishLoading();

   		mainTheme = manager.get("music/main.mp3", Music.class);
	    menuTheme = manager.get("music/menu.mp3", Music.class);
   		grab =      manager.get("sound/grab.wav", Sound.class);
        drop =      manager.get("sound/land.wav", Sound.class);
        win =       manager.get("sound/over.mp3", Sound.class);
        over =      manager.get("sound/win.wav", Sound.class);

   	}


	public void playGrab() {
		play(grab);
	}

	public void playDrop() {
		play(drop);
	}

	public void playWin() {
		play(win);
	}

	public void playOver() {
		play(over);
	}

	public void play (VisSound component) {
   		play(component.sound);
   	}

   	public void play (Sound sound) {
   		if (enabled) {
   			sound.play(0.5f);
   		}
   	}

	public void playMainTheme(){
		if(enabled){
			if(music != null)
				music.stop();
			music = mainTheme;
			music.setLooping(true);
			music.setVolume(0.5f);
			music.play();
		}
	}

	public void playMenuTheme(){
		if(enabled){
			if(music != null)
				music.stop();
			music = menuTheme;
			music.setLooping(true);
			music.setVolume(0.5f);
			music.play();
		}
	}

   	public void resetSound() {
   		enabled = !enabled;

   		if (enabled) {
		    music.play();
	    }else {
		    music.stop();
	    }
   	}
}

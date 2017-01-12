package com.marvin.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.marvin.game.Screens.PlayScreen;

public class Mario extends Game {
	//virtual dimensions
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    public static final float PPM = 100;

    //bit flags
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 0x01;
    public static final short MARIO_BIT = 0x02;
    public static final short BRICK_BIT = 0x04;
    public static final short COIN_BIT = 0x08;
    public static final short DESTROYED_BIT = 0x10;
    public static final short OBJECT_BIT = 0x20;
	public static final short ENEMY_BIT = 0x40;
	public static final short ENEMY_HEAD_BIT = 0x80;
	public static final short ITEM_BIT = 0x100; // 0001 0000 0000
	public static final short MARIO_HEAD_BIT = 0x200;
	public static final short FIREBALL_BIT = 0x400;

	public SpriteBatch batch;

	public static AssetManager manager;

	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/mario_music.ogg", Music.class);
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/stomp.wav", Sound.class);
		manager.load("audio/sounds/breakblock.wav", Sound.class);
		manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
		manager.load("audio/sounds/powerup.wav", Sound.class);
		manager.load("audio/sounds/powerdown.wav", Sound.class);
		manager.load("audio/sounds/stomp.wav", Sound.class);
		manager.load("audio/sounds/mariodie.wav", Sound.class);
		manager.finishLoading();
		setScreen(new PlayScreen(this));
	}


	@Override
	public void render () {
	    super.render();
	    manager.update();
	}
	
	@Override
	public void dispose () {
	    super.dispose();
	    manager.dispose();
		batch.dispose();
	}
}

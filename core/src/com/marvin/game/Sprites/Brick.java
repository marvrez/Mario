package com.marvin.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.marvin.game.Mario;
import com.marvin.game.Scenes.Hud;
import com.marvin.game.Screens.PlayScreen;

/**
 * Created by marvinreza on 07.01.2017.
 */
public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(Mario.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Brick","Collision");
        setCategoryFilter(Mario.DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(200);
        Mario.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
    }
}

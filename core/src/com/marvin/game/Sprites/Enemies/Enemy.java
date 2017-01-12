package com.marvin.game.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.marvin.game.Mario;
import com.marvin.game.Screens.PlayScreen;
import com.marvin.game.Sprites.MarioSprite;
import com.marvin.game.Sprites.Other.FireBall;

/**
 * Created by marvinreza on 09.01.2017.
 */
public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x,y);
        defineEnemy();
        velocity = new Vector2(-1, -2);
        b2body.setActive(false);
    }

    protected abstract void defineEnemy();
    public abstract void hitOnHead(MarioSprite mario);
    public abstract void hitOnHead(FireBall fireBall);
    public abstract void onEnemyHit(Enemy enemy);
    public abstract void update(float dt);

    public void reverseVelocity(boolean x, boolean y) {
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}

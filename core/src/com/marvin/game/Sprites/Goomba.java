package com.marvin.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.marvin.game.Mario;
import com.marvin.game.Screens.PlayScreen;

/**
 * Created by marvinreza on 09.01.2017.
 */
public class Goomba extends Enemy {

    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy, destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        walkAnimation = new Animation(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / Mario.PPM, 16 / Mario.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        stateTime += dt;
        if(setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        }
        else if(!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime,true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Mario.PPM);
        fDef.filter.categoryBits = Mario.ENEMY_BIT;
        fDef.filter.maskBits = Mario.GROUND_BIT | Mario.COIN_BIT | Mario.BRICK_BIT
                | Mario.ENEMY_BIT | Mario.OBJECT_BIT | Mario.MARIO_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);

        //create head
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / Mario.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / Mario.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / Mario.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / Mario.PPM);
        head.set(vertice);

        fDef.shape = head;
        fDef.restitution = 0.5f;
        fDef.filter.categoryBits = Mario.ENEMY_HEAD_BIT;
        b2body.createFixture(fDef).setUserData(this);
    }

    public void draw(Batch batch) {
        if(!destroyed || stateTime < 1)
            super.draw(batch);
    }

    @Override
    public void hitOnHead() {
        setToDestroy = true;
        Mario.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

}

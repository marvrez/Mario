package com.marvin.game.Sprites.Other;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.marvin.game.Mario;
import com.marvin.game.Screens.PlayScreen;

/**
 * Created by marvinreza on 12.01.2017.
 */
public class FireBall extends Sprite {

    private PlayScreen screen;
    private World world;
    public Array<TextureRegion> frames;
    private Animation fireAnimation;
    float stateTime;
    boolean destroyed;
    boolean setToDestroy;
    boolean fireRight;

    Body b2body;
    public FireBall(PlayScreen screen, float x, float y, boolean fireRight){
        frames = new Array<TextureRegion>();
        this.fireRight = fireRight;
        this.screen = screen;
        this.world = screen.getWorld();
        for(int i = 0; i < 4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i * 8, 0, 8, 8));
        }
        fireAnimation = new Animation(0.2f, frames);
        setRegion((TextureRegion)fireAnimation.getKeyFrame(0));
        setBounds(x, y, 6 / Mario.PPM, 6 / Mario.PPM);
        defineFireBall();
    }

    public void defineFireBall(){
        BodyDef bDef = new BodyDef();
        bDef.position.set(fireRight ? getX() + 12 / Mario.PPM : getX() - 12 /Mario.PPM, getY());
        bDef.type = BodyDef.BodyType.DynamicBody;
        if(!world.isLocked())
            b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3 / Mario.PPM);
        fDef.filter.categoryBits = Mario.FIREBALL_BIT;
        fDef.filter.maskBits = Mario.GROUND_BIT |
                Mario.COIN_BIT |
                Mario.BRICK_BIT |
                Mario.ENEMY_BIT |
                Mario.ENEMY_HEAD_BIT |
                Mario.OBJECT_BIT;

        fDef.shape = shape;
        fDef.restitution = 1;
        fDef.friction = 0;
        b2body.createFixture(fDef).setUserData(this);
        b2body.setLinearVelocity(new Vector2(fireRight ? 2 : -2, 2.5f));
    }

    public void update(float dt){
        stateTime += dt;
        setRegion((TextureRegion)fireAnimation.getKeyFrame(stateTime, true));
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        if((stateTime > 3 || setToDestroy) && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }
        if(b2body.getLinearVelocity().y > 2f)
            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 2f);
        if((fireRight && b2body.getLinearVelocity().x < 0) || (!fireRight && b2body.getLinearVelocity().x > 0))
            setToDestroy();
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }


}

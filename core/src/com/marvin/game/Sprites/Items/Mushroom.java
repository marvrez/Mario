package com.marvin.game.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.marvin.game.Mario;
import com.marvin.game.Screens.PlayScreen;
import com.marvin.game.Sprites.MarioSprite;

/**
 * Created by marvinreza on 09.01.2017.
 */
public class Mushroom extends Item {
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0,  16, 16);
        velocity = new Vector2(0.7f,0);
    }

    @Override
    public void defineItem() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Mario.PPM);
        fDef.filter.categoryBits = Mario.ITEM_BIT;
        fDef.filter.maskBits = Mario.MARIO_BIT | Mario.OBJECT_BIT | Mario.GROUND_BIT | Mario.COIN_BIT | Mario.BRICK_BIT;


        fDef.shape = shape;
        body.createFixture(fDef).setUserData(this);
    }

    @Override
    public void use(MarioSprite mario) {
        destroy();
        mario.grow();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}

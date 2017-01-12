package com.marvin.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.marvin.game.Mario;
import com.marvin.game.Sprites.Enemies.Enemy;
import com.marvin.game.Sprites.TileObjects.InteractiveTileObject;
import com.marvin.game.Sprites.Items.Item;
import com.marvin.game.Sprites.MarioSprite;

/**
 * Created by marvinreza on 08.01.2017.
 */
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case Mario.MARIO_HEAD_BIT | Mario.BRICK_BIT:
            case Mario.MARIO_HEAD_BIT | Mario.COIN_BIT:
                if(fixA.getFilterData().categoryBits == Mario.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((MarioSprite)fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((MarioSprite)fixB.getUserData());
                break;
            case Mario.ENEMY_HEAD_BIT | Mario.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == Mario.ENEMY_HEAD_BIT)
                    ((Enemy)(fixA.getUserData())).hitOnHead( (MarioSprite)fixB.getUserData());
                else
                    ((Enemy)(fixB.getUserData())).hitOnHead((MarioSprite)fixA.getUserData());
                break;
            case Mario.ENEMY_BIT | Mario.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Mario.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Mario.MARIO_BIT | Mario.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == Mario.MARIO_BIT)
                    ((MarioSprite) fixA.getUserData()).hit( (Enemy) fixB.getUserData());
                else
                    ((MarioSprite) fixB.getUserData()).hit( (Enemy) fixA.getUserData());
                break;
            case Mario.ENEMY_BIT | Mario.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Mario.ITEM_BIT | Mario.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Mario.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Mario.ITEM_BIT | Mario.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == Mario.ITEM_BIT)
                    ((Item)fixA.getUserData()).use( (MarioSprite)fixB.getUserData() );
                else
                    ((Item)fixB.getUserData()).use( (MarioSprite)fixA.getUserData() );
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

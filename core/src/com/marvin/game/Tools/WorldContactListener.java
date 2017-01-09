package com.marvin.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.marvin.game.Mario;
import com.marvin.game.Sprites.Enemy;
import com.marvin.game.Sprites.InteractiveTileObject;

/**
 * Created by marvinreza on 08.01.2017.
 */
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        if(fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if(object.getUserData() instanceof InteractiveTileObject) {
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }

        switch (cDef) {
            case Mario.ENEMY_HEAD_BIT | Mario.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == Mario.ENEMY_HEAD_BIT)
                    ((Enemy)(fixA.getUserData())).hitOnHead();
                else
                    ((Enemy)(fixB.getUserData())).hitOnHead();
                break;
            case Mario.ENEMY_BIT | Mario.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Mario.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Mario.MARIO_BIT | Mario.ENEMY_BIT:
                Gdx.app.log("Mario","Died");
            case Mario.ENEMY_BIT | Mario.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            /*case Mario.ITEM_BIT | Mario.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == Mario.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);*/
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

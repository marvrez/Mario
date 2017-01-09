package com.marvin.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.marvin.game.Mario;
import com.marvin.game.Screens.PlayScreen;
import com.marvin.game.Sprites.Brick;
import com.marvin.game.Sprites.Coin;

/**
 * Created by marvinreza on 07.01.2017.
 */
public class B2WorldCreator {
    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        BodyDef bDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fDef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set((rect.getX() + rect.getWidth() / 2) / Mario.PPM, (rect.getY() + rect.getHeight() / 2) / Mario.PPM);

            body = world.createBody(bDef);

            shape.setAsBox((rect.getWidth() / 2) / Mario.PPM, (rect.getHeight() / 2) / Mario.PPM);
            fDef.shape = shape;
            body.createFixture(fDef);
        }

        //create pipe bodies/fixtures
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set((rect.getX() + rect.getWidth() / 2) / Mario.PPM, (rect.getY() + rect.getHeight() / 2) / Mario.PPM);

            body = world.createBody(bDef);

            shape.setAsBox((rect.getWidth() / 2) / Mario.PPM, (rect.getHeight() / 2) / Mario.PPM);
            fDef.shape = shape;
            fDef.filter.categoryBits = Mario.OBJECT_BIT;
            body.createFixture(fDef);
        }

        //create bricks bodies/fixtures
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Brick(screen, rect);
        }
        //create coins bodies/fixtures
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Coin(screen, rect);
        }
    }
}
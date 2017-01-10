package com.marvin.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.marvin.game.Mario;
import com.marvin.game.Screens.PlayScreen;

/**
 * Created by marvinreza on 07.01.2017.
 */
public class MarioSprite extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING , GROWING, DEAD};
    public State curState, prevState;

    private Animation marioRun;
    private Animation marioJump;

    private float stateTimer;
    private boolean runningRight;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;

    public MarioSprite(PlayScreen screen) {
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = screen.getWorld();
        curState = State.STANDING;
        prevState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),i *16,0,16,16));
        marioRun = new Animation(0.1f, frames);

        frames.clear();

        for(int i = 4; i < 6; i++)
            frames.add(new TextureRegion(getTexture(),i*16,0,16,16));
        marioJump = new Animation(0.1f,frames);

        frames.clear();

        defineMario();
        marioStand = new TextureRegion(getTexture(), 1, 11, 16, 16 );
        setBounds(0,0, 16 / Mario.PPM, 16 / Mario.PPM);
        setRegion(marioStand);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt) {
        curState = getState();

        TextureRegion region;
        switch (curState) {
            case JUMPING:
                region = (TextureRegion) marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
                break;
        }

        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true,false);
            runningRight = false;
        }
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true,false);
            runningRight = true;
        }

        stateTimer = curState == prevState ? stateTimer + dt : 0;
        prevState = curState;
        return region;
    }

    public State getState() {
        if ((b2body.getLinearVelocity().y > 0 && curState == State.JUMPING) || (b2body.getLinearVelocity().y < 0  && prevState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void defineMario() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(32 / Mario.PPM, 32 / Mario.PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Mario.PPM);
        fDef.filter.categoryBits = Mario.MARIO_BIT;
        fDef.filter.maskBits = Mario.GROUND_BIT | Mario.COIN_BIT | Mario.BRICK_BIT | Mario. ENEMY_BIT |
                Mario.OBJECT_BIT | Mario.ENEMY_HEAD_BIT | Mario.ITEM_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Mario.PPM, 7 / Mario.PPM), new Vector2(2 / Mario.PPM, 7 / Mario.PPM));
        fDef.shape = head;
        fDef.isSensor = true;

        b2body.createFixture(fDef).setUserData("head");
    }
}

package com.marvin.game.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.marvin.game.Mario;
import com.marvin.game.Screens.PlayScreen;
import com.marvin.game.Sprites.Enemies.Enemy;
import com.marvin.game.Sprites.Enemies.Turtle;
import com.marvin.game.Sprites.Other.FireBall;

/**
 * Created by marvinreza on 07.01.2017.
 */
public class MarioSprite extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING , GROWING, DEAD};
    public State curState, prevState;

    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation bigMarioRun;
    private Animation growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig, runGrowAnimation;
    private boolean timeToDefineBigMario, timeToRedefineMario;
    private boolean marioIsDead;
    private PlayScreen screen;

    private Array<FireBall> fireballs;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;

    public MarioSprite(PlayScreen screen) {
        //default values
        this.world = screen.getWorld();
        this.screen = screen;
        curState = State.STANDING;
        prevState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),i *16,0,16,16));
        marioRun = new Animation(0.1f, frames);

        frames.clear();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),i *16,0,16,32));
        bigMarioRun= new Animation(0.1f, frames);

        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);

        frames.clear();

        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16 );
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"),
                0, 0, 16, 32);

        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"),
                80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"),
                80, 0, 16, 32);

        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        //define mario in box2d
        defineMario();

        setBounds(0,0, 16 / Mario.PPM, 16 / Mario.PPM);
        setRegion(marioStand);

        fireballs = new Array<FireBall>();
    }

    public void update(float dt) {
        if(marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / Mario.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        //Update sprite with correct frame depending on action
        setRegion(getFrame(dt));
        if (timeToDefineBigMario)
            defineBigMario();
        if(timeToRedefineMario)
            redefineMario();

        for(FireBall ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }

    }

    public TextureRegion getFrame(float dt) {
        curState = getState();

        TextureRegion region;
        switch (curState) {
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? (TextureRegion) bigMarioRun.getKeyFrame(stateTimer, true) : (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
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

    public boolean isBig(){
        return marioIsBig;
    }

    public void hit(Enemy enemy) {
        if(enemy instanceof Turtle && ((Turtle)enemy).getCurState() == Turtle.State.STANDING_SHELL) {
            ((Turtle)enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }
        else {

            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                Mario.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                Mario.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                Mario.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = Mario.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }
    }


    public State getState() {
        if (marioIsDead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if ((b2body.getLinearVelocity().y > 0 && curState == State.JUMPING) || (b2body.getLinearVelocity().y < 0  && prevState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public boolean isDead() {
        return marioIsDead;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void grow() {
        if (!isBig()) {
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        }
        Mario.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void redefineMario() {
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bDef = new BodyDef();
        bDef.position.set(position);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Mario.PPM);
        fDef.filter.categoryBits = Mario.MARIO_BIT;
        fDef.filter.maskBits = Mario.GROUND_BIT | Mario.COIN_BIT | Mario.BRICK_BIT | Mario. ENEMY_BIT |
                Mario.OBJECT_BIT | Mario.ENEMY_HEAD_BIT | Mario.ITEM_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Mario.PPM, 6 / Mario.PPM), new Vector2(2 / Mario.PPM, 6 / Mario.PPM));
        fDef.filter.categoryBits = Mario.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;

        b2body.createFixture(fDef).setUserData(this);
        timeToRedefineMario = false;
    }

    public void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bDef = new BodyDef();
        bDef.position.set(currentPosition.add(0,10 / Mario.PPM));
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Mario.PPM);
        fDef.filter.categoryBits = Mario.MARIO_BIT;
        fDef.filter.maskBits = Mario.GROUND_BIT | Mario.COIN_BIT | Mario.BRICK_BIT | Mario. ENEMY_BIT |
                Mario.OBJECT_BIT | Mario.ENEMY_HEAD_BIT | Mario.ITEM_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / Mario.PPM));
        b2body.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Mario.PPM, 6 / Mario.PPM), new Vector2(2 / Mario.PPM, 6 / Mario.PPM));
        fDef.filter.categoryBits = Mario.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;

        b2body.createFixture(fDef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void fire(){
        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false));
    }

    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
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
        b2body.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Mario.PPM, 6 / Mario.PPM), new Vector2(2 / Mario.PPM, 6 / Mario.PPM));
        fDef.filter.categoryBits = Mario.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;

        b2body.createFixture(fDef).setUserData(this);
    }
}

package com.marvin.game.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.marvin.game.Mario;
import com.marvin.game.Screens.PlayScreen;
import com.marvin.game.Sprites.MarioSprite;

import javax.xml.soap.Text;

/**
 * Created by marvinreza on 12.01.2017.
 */
public class Turtle extends Enemy {
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;

    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL}
    public State curState, prevState;
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy, destroyed;
    private TextureRegion shell;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
        walkAnimation = new Animation(0.2f, frames);
        curState = prevState = State.WALKING;

        setBounds(getX(), getY(),  16 / Mario.PPM, 24 / Mario.PPM);
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
        fDef.restitution = 1.5f;
        fDef.filter.categoryBits = Mario.ENEMY_HEAD_BIT;
        b2body.createFixture(fDef).setUserData(this);
    }

    @Override
    public void hitOnHead(MarioSprite mario) {
        if(curState != State.STANDING_SHELL) {
            curState = State.STANDING_SHELL;
            velocity.x = 0;
        }
        else {
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }

    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch (curState) {
            case STANDING_SHELL:
                case MOVING_SHELL:

                region = shell;
                break;
            case WALKING:
            default:
                region = (TextureRegion) walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if(velocity.x > 0 && region.isFlipX() == false) {
            region.flip(true,false);
        }
        if(velocity.x < 0 && region.isFlipX() == true) {
            region.flip(true,false);
        }

        stateTime = curState == prevState ? stateTime + dt : 0;
        prevState = curState;
        return region;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (curState == State.STANDING_SHELL && stateTime > 5) {
            curState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 8 / Mario.PPM);
        b2body.setLinearVelocity(velocity);
    }

    public void kick(int speed) {
        velocity.x = speed;
        curState = State.MOVING_SHELL;
    }

    public State getCurState() {
        return curState;
    }
}

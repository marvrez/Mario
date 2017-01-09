package com.marvin.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.marvin.game.Mario;
import com.marvin.game.Scenes.Hud;
import com.marvin.game.Sprites.Enemy;
import com.marvin.game.Sprites.Goomba;
import com.marvin.game.Sprites.MarioSprite;
import com.marvin.game.Tools.B2WorldCreator;
import com.marvin.game.Tools.WorldContactListener;
import com.sun.prism.image.ViewPort;

/**
 * Created by marvinreza on 07.01.2017.
 */
public class PlayScreen implements Screen{

    private Mario game;
    private TextureAtlas atlas;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    //sprites
    private MarioSprite player;

    private Music music;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    public PlayScreen(Mario game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(Mario.V_WIDTH / Mario.PPM, Mario.V_HEIGHT / Mario.PPM, gameCam);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Mario.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new MarioSprite(this);

        world.setContactListener(new WorldContactListener());

        music = Mario.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();
    }

    @Override
    public void show() {

    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public void handleInput(float dt) {

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP))
           player.b2body.applyLinearImpulse(new Vector2(0,4f), player.b2body.getWorldCenter(), true);

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
            player.b2body.applyLinearImpulse(new Vector2(0.1f,0), player.b2body.getWorldCenter(), true);

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
            player.b2body.applyLinearImpulse(new Vector2(-0.1f,0), player.b2body.getWorldCenter(), true);


    }

    public void update(float dt) {
       handleInput(dt);

       world.step(1/60f , 6 , 2);

       player.update(dt);
       for (Enemy enemy : creator.getGoombas()) {
           enemy.update(dt);
           if(enemy.getX() < player.getX() + 224 / Mario.PPM)
               enemy.b2body.setActive(true);
       }

       hud.update(dt);

       gameCam.position.x = player.b2body.getPosition().x;

       gameCam.update();
       renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render(); //renders game map

        b2dr.render(world, gameCam.combined);

        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        player.draw(game.batch);
        for (Enemy enemy : creator.getGoombas())
            enemy.draw(game.batch);

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}

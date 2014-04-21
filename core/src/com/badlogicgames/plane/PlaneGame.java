package com.badlogicgames.plane;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class PlaneGame extends ApplicationAdapter {
	private static final float PLANE_JUMP_IMPULSE = 350;
	private static final float GRAVITY = -20;
	private static final float PLANE_VELOCITY_X = 200;
	private static final float PLANE_START_Y = 240;
	private static final float PLANE_START_X = 50;
	SpriteBatch batch;
	OrthographicCamera camera;
	Texture background;
	Texture ground;
	float groundOffsetX = 0;
	TextureRegion ceiling;
	Animation plane;
	
	Vector2 planePosition = new Vector2();
	Vector2 planeVelocity = new Vector2();
	float planeStateTime = 0;
	Vector2 gravity = new Vector2();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		background = new Texture("background.png");	
		ground = new Texture("ground.png");
		ceiling = new TextureRegion(ground);
		ceiling.flip(false, true);
		
		Texture frame1 = new Texture("plane1.png");
		frame1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Texture frame2 = new Texture("plane2.png");
		Texture frame3 = new Texture("plane3.png");
		
		plane = new Animation(0.05f, new TextureRegion(frame1), new TextureRegion(frame2), new TextureRegion(frame3), new TextureRegion(frame2));
		plane.setPlayMode(PlayMode.LOOP);
		planePosition.set(PLANE_START_X, PLANE_START_Y);
		planeVelocity.set(PLANE_VELOCITY_X, 0);
		gravity.set(0, GRAVITY);
	}
	
	private void updateWorld() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		planeStateTime += deltaTime;
		
		if(Gdx.input.justTouched()) {
			planeVelocity.set(PLANE_VELOCITY_X, PLANE_JUMP_IMPULSE);
		}
		
		planeVelocity.add(gravity);
		planePosition.mulAdd(planeVelocity, deltaTime);
		
		camera.position.x = planePosition.x + 350;		
		if(camera.position.x - groundOffsetX > ground.getWidth() + 400) {
			groundOffsetX += ground.getWidth();
		}
	}
	
	private void drawWorld() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(background, camera.position.x - background.getWidth() / 2, 0);
		batch.draw(ground, groundOffsetX, 0);
		batch.draw(ground, groundOffsetX + ground.getWidth(), 0);
		batch.draw(ceiling, groundOffsetX, 480 - ceiling.getRegionHeight());
		batch.draw(ceiling, groundOffsetX + ceiling.getRegionWidth(), 480 - ceiling.getRegionHeight());
		batch.draw(plane.getKeyFrame(planeStateTime), planePosition.x, planePosition.y);
		batch.end();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		updateWorld();
		drawWorld();			
	}
}

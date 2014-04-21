package com.badlogicgames.plane;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class PlaneGame extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	Texture background;
	Texture ground;
	TextureRegion ceiling;
	Animation plane;
	float stateTime = 0;
	
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
		Texture frame2 = new Texture("plane2.png");
		Texture frame3 = new Texture("plane3.png");
		
		plane = new Animation(0.05f, new TextureRegion(frame1), new TextureRegion(frame2), new TextureRegion(frame3), new TextureRegion(frame2));
		plane.setPlayMode(PlayMode.LOOP);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(background, 0, 0);
		batch.draw(ground, 0, 0);
		batch.draw(ceiling, 0, 480 - ceiling.getRegionHeight());
		batch.draw(plane.getKeyFrame(stateTime), 50, 240);
		batch.end();
		
		stateTime += Gdx.graphics.getDeltaTime();
	}
}

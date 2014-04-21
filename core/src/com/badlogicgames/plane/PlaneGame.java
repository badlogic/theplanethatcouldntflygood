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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PlaneGame extends ApplicationAdapter {
	private static final float PLANE_JUMP_IMPULSE = 350;
	private static final float GRAVITY = -20;
	private static final float PLANE_VELOCITY_X = 200;
	private static final float PLANE_START_Y = 240;
	private static final float PLANE_START_X = 50;
	ShapeRenderer shapeRenderer;
	SpriteBatch batch;
	OrthographicCamera camera;
	Texture background;
	TextureRegion ground;
	float groundOffsetX = 0;
	TextureRegion ceiling;
	TextureRegion rock;
	TextureRegion rockDown;
	Animation plane;
	
	Vector2 planePosition = new Vector2();
	Vector2 planeVelocity = new Vector2();
	float planeStateTime = 0;
	Vector2 gravity = new Vector2();
	Array<Rock> rocks = new Array<Rock>();
	
	GameState gameState = GameState.Running;
	Rectangle rect1 = new Rectangle();
	Rectangle rect2 = new Rectangle();
	
	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		background = new Texture("background.png");	
		ground = new TextureRegion(new Texture("ground.png"));
		ceiling = new TextureRegion(ground);
		ceiling.flip(true, true);
		
		rock = new TextureRegion(new Texture("rock.png"));
		rockDown = new TextureRegion(rock);
		rockDown.flip(false, true);
		
		Texture frame1 = new Texture("plane1.png");
		frame1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Texture frame2 = new Texture("plane2.png");
		Texture frame3 = new Texture("plane3.png");
		
		plane = new Animation(0.05f, new TextureRegion(frame1), new TextureRegion(frame2), new TextureRegion(frame3), new TextureRegion(frame2));
		plane.setPlayMode(PlayMode.LOOP);
		planePosition.set(PLANE_START_X, PLANE_START_Y);
		planeVelocity.set(PLANE_VELOCITY_X, 0);
		gravity.set(0, GRAVITY);
		
		for(int i = 0; i < 5; i++) {
			boolean isDown = MathUtils.randomBoolean();
			rocks.add(new Rock(700 + i * 200, isDown?480-rock.getRegionHeight(): 0, isDown? rockDown: rock));
		}
	}
	
	private void updateWorld() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		planeStateTime += deltaTime;
		
		if(Gdx.input.justTouched() && gameState != GameState.GameOver) {
			planeVelocity.set(PLANE_VELOCITY_X, PLANE_JUMP_IMPULSE);
		}
		
		planeVelocity.add(gravity);
		planePosition.mulAdd(planeVelocity, deltaTime);
		
		camera.position.x = planePosition.x + 350;		
		if(camera.position.x - groundOffsetX > ground.getRegionWidth() + 400) {
			groundOffsetX += ground.getRegionWidth();
		}
				
		rect1.set(planePosition.x, planePosition.y, plane.getKeyFrames()[0].getRegionWidth(), plane.getKeyFrames()[0].getRegionHeight());
		for(Rock r: rocks) {
			if(camera.position.x - r.position.x > 400 + r.image.getRegionWidth()) {
				boolean isDown = MathUtils.randomBoolean();
				r.position.x += 5 * 200;
				r.position.y = isDown?480-rock.getRegionHeight(): 0;
				r.image = isDown? rockDown: rock;
			}
			rect2.set(r.position.x + (r.image.getRegionWidth() - 30) / 2, r.position.y, 30, r.image.getRegionHeight());
			if(rect1.overlaps(rect2)) {
				gameState = GameState.GameOver;
				planeVelocity.x = 0;
			}
		}
		
		if(planePosition.y < ground.getRegionHeight() - 20 || 
			planePosition.y + plane.getKeyFrames()[0].getRegionHeight() > 480 - ground.getRegionHeight() + 20) {
			gameState = GameState.GameOver;
			planeVelocity.x = 0;
		}		
	}
	
	private void drawWorld() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(background, camera.position.x - background.getWidth() / 2, 0);
		for(Rock rock: rocks) {
			batch.draw(rock.image, rock.position.x, rock.position.y);
		}
		batch.draw(ground, groundOffsetX, 0);
		batch.draw(ground, groundOffsetX + ground.getRegionWidth(), 0);
		batch.draw(ceiling, groundOffsetX, 480 - ceiling.getRegionHeight());
		batch.draw(ceiling, groundOffsetX + ceiling.getRegionWidth(), 480 - ceiling.getRegionHeight());
		batch.draw(plane.getKeyFrame(planeStateTime), planePosition.x, planePosition.y);
		batch.end();
		
		debugDraw();
	}
	
	private void debugDraw() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		rect1.set(planePosition.x, planePosition.y, plane.getKeyFrames()[0].getRegionWidth(), plane.getKeyFrames()[0].getRegionHeight());
		shapeRenderer.rect(rect1.x, rect1.y, rect1.width, rect1.height);
		for(Rock r: rocks) {
			rect2.set(r.position.x + (r.image.getRegionWidth() - 30) / 2, r.position.y, 30, r.image.getRegionHeight());
			shapeRenderer.rect(rect2.x, rect2.y, rect2.width, rect2.height);
		}		
		shapeRenderer.end();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		updateWorld();
		drawWorld();			
	}
	
	static class Rock {
		Vector2 position = new Vector2();
		TextureRegion image;
		
		public Rock(float x, float y, TextureRegion image) {
			this.position.x = x;
			this.position.y = y;
			this.image = image;
		}
	}
	
	static enum GameState {
		Start, Running, GameOver
	}
}

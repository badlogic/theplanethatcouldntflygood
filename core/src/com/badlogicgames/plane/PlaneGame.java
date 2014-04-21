package com.badlogicgames.plane;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
	OrthographicCamera uiCamera;
	Texture background;
	TextureRegion ground;
	float groundOffsetX = 0;
	TextureRegion ceiling;
	TextureRegion rock;
	TextureRegion rockDown;
	Animation plane;
	TextureRegion ready;
	TextureRegion gameOver;
	BitmapFont font;
	
	Vector2 planePosition = new Vector2();
	Vector2 planeVelocity = new Vector2();
	float planeStateTime = 0;
	Vector2 gravity = new Vector2();
	Array<Rock> rocks = new Array<Rock>();
	
	GameState gameState = GameState.Start;
	int score = 0;
	Rectangle rect1 = new Rectangle();
	Rectangle rect2 = new Rectangle();
	
	Music music;
	Sound explode;
	
	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.update();
		
		font = new BitmapFont(Gdx.files.internal("arial.fnt"));
		
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
		
		ready = new TextureRegion(new Texture("ready.png"));
		gameOver = new TextureRegion(new Texture("gameover.png"));
		
		plane = new Animation(0.05f, new TextureRegion(frame1), new TextureRegion(frame2), new TextureRegion(frame3), new TextureRegion(frame2));
		plane.setPlayMode(PlayMode.LOOP);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		music.setLooping(true);
		music.play();
		
		explode = Gdx.audio.newSound(Gdx.files.internal("explode.wav"));
		
		resetWorld();
	}
	
	private void resetWorld() {
		score = 0;
		groundOffsetX = 0;
		planePosition.set(PLANE_START_X, PLANE_START_Y);
		planeVelocity.set(0, 0);
		gravity.set(0, GRAVITY);
		camera.position.x = 400;
		
		rocks.clear();
		for(int i = 0; i < 5; i++) {
			boolean isDown = MathUtils.randomBoolean();
			rocks.add(new Rock(700 + i * 200, isDown?480-rock.getRegionHeight(): 0, isDown? rockDown: rock));
		}
	}
	
	private void updateWorld() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		planeStateTime += deltaTime;
		
		if(Gdx.input.justTouched()) {
			if(gameState == GameState.Start) {
				gameState = GameState.Running;
			}
			if(gameState == GameState.Running) {
				planeVelocity.set(PLANE_VELOCITY_X, PLANE_JUMP_IMPULSE);
			}
			if(gameState == GameState.GameOver) {
				gameState = GameState.Start;
				resetWorld();
			}
		}
			
		if(gameState != GameState.Start) planeVelocity.add(gravity);
		
		planePosition.mulAdd(planeVelocity, deltaTime);
		
		camera.position.x = planePosition.x + 350;		
		if(camera.position.x - groundOffsetX > ground.getRegionWidth() + 400) {
			groundOffsetX += ground.getRegionWidth();
		}
				
		rect1.set(planePosition.x + 20, planePosition.y, plane.getKeyFrames()[0].getRegionWidth() - 20, plane.getKeyFrames()[0].getRegionHeight());
		for(Rock r: rocks) {
			if(camera.position.x - r.position.x > 400 + r.image.getRegionWidth()) {
				boolean isDown = MathUtils.randomBoolean();
				r.position.x += 5 * 200;
				r.position.y = isDown?480-rock.getRegionHeight(): 0;
				r.image = isDown? rockDown: rock;
				r.counted = false;
			}
			rect2.set(r.position.x + (r.image.getRegionWidth() - 30) / 2 + 20, r.position.y, 20, r.image.getRegionHeight() - 10);
			if(rect1.overlaps(rect2)) {
				if(gameState != GameState.GameOver) explode.play();
				gameState = GameState.GameOver;
				planeVelocity.x = 0;				
			}
			if(r.position.x < planePosition.x && !r.counted) {
				score++;
				r.counted = true;
			}
		}
		
		if(planePosition.y < ground.getRegionHeight() - 20 || 
			planePosition.y + plane.getKeyFrames()[0].getRegionHeight() > 480 - ground.getRegionHeight() + 20) {
			if(gameState != GameState.GameOver) explode.play();
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
		
		batch.setProjectionMatrix(uiCamera.combined);
		batch.begin();		
		if(gameState == GameState.Start) {
			batch.draw(ready, Gdx.graphics.getWidth() / 2 - ready.getRegionWidth() / 2, Gdx.graphics.getHeight() / 2 - ready.getRegionHeight() / 2);
		}
		if(gameState == GameState.GameOver) {
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getRegionWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getRegionHeight() / 2);
		}
		if(gameState == GameState.GameOver || gameState == GameState.Running) {
			font.draw(batch, "" + score, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 60);
		}
		batch.end();
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
		boolean counted;
		
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

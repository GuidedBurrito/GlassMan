//Application Name: Glass Man
//By: Jonathan Hodder and Alex
//Purpose of Application: Allows user to play a platformer game where they dodge shapes. When the user
//collides with an object the game ends
//Purpose of Activity: Create a game for the user to play.  Spawns a player character that allows the user
//to jump.  User must dodge spawning objects.  If they are hit by the objects user has lost the game and is 
//sent to the gameOver activity
//Version 1: Attempted to load up parralex background
//Version 2: background is now working having problem with atlas imports
//Version 3: Imports are fixed and character sprite have been loaded.  Sprite is now moving
//Version 4: Enemy initial spawn is working.  Do not have enemy deletion or collision detection working
//Version 5: Added timer.  Score text view does not seem to be showing up but there are no errors showing up
//Version 6: Attempting to get the enemies to spawn after initial spawning needs some work
package ca.project2.glassman;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;

public class Game extends BackgroundHelper {
	//declare constants
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	//declare objects
	private Camera mCamera;
	private LinkedList<Sprite> ObjectsToBeAdded;
	private Texture mTexture;
	private Font mFont;
	private TextureRegion mObjectTextureRegion;
	private Texture mAutoParallaxBackgroundTexture;
	private TiledTextureRegion mPlayerTextureRegion;
	private Scene scene;
	private AnimatedSprite player;
	private TextureRegion mParallaxLayerBack; 
	public static int CurrentScore = 0;
	private ChangeableText score;//the score
	private int count = 0; //counts the seconds
	private Texture mFontTexture;
	private int InitialTime = 30000*20;
	private CountDownTimer timer;
	private LinkedList<Sprite> ObjectIterator;

	@Override
	//Load engine to application
	public Engine onLoadEngine() {
		//create new camera
		this.mCamera = new Camera (0,0,CAMERA_WIDTH, CAMERA_HEIGHT);
		//add new engine to the application
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}//end of onLoadEngine method

	@Override
	//load resources to application
	public void onLoadResources() {
		//create textures
		this.mTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(mFontTexture, Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD), 40, true, Color.WHITE);
		this.mAutoParallaxBackgroundTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);
		this.mParallaxLayerBack = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/BackgroundResized.png", 0, 188);
		this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/GlassPlayer.png", 0, 0, 3, 4);
		this.mObjectTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/CratePictureResized.png", 128, 0);
		
		
		
		//add textures to and engine
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		this.mEngine.getTextureManager().loadTexture(this.mAutoParallaxBackgroundTexture);

		this.mEngine.getTextureManager().loadTexture(mFontTexture);
		this.mEngine.getFontManager().loadFont(mFont);
	}//end of onLoadResources method

	@Override
	public Scene onLoadScene() {  
		this.mEngine.registerUpdateHandler(new FPSLogger());

		scene = new Scene(1);
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-20.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack)));
		scene.setBackground(autoParallaxBackground);

		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
				switch(pSceneTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					jump();
					break;
				case TouchEvent.ACTION_UP:
					jump();
					break;
				}
				return true;
			}
		});

		final int playerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2;
		final int playerY = CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight() - 5;

		/* Create two sprits and add it to the scene. */
		player = new AnimatedSprite(playerX - 200, playerY - 40, this.mPlayerTextureRegion);
		player.setScaleCenterY(this.mPlayerTextureRegion.getTileHeight());
		player.setScale(2);
		player.animate(new long[]{200, 200, 200}, 3, 5, true);    

		scene.attachChild(player);

		score = new ChangeableText(0, 0, mFont, String.valueOf(0), "XXXXXX".length());
		// repositioning the score later so we can use the score.getWidth()
		score.setPosition(mCamera.getWidth()/2 - score.getWidth() - 5, 5);
		scene.attachChild(score);
		
		ObjectIterator = new LinkedList<Sprite>();
		ObjectsToBeAdded = new LinkedList<Sprite>();
		
		createSpriteSpawnTimeHandler();
		scene.registerUpdateHandler(detect);

		timer = new CountDownTimer(InitialTime, 1000) { // creates a new timer
			@Override
			public void onTick(long millisUntilFinished) { // on every tick...

				CurrentScore = CurrentScore + 5;
				score.setText(String.valueOf(CurrentScore));
				scene.attachChild(score);
				count = count + 1;
			} 

			public void onFinish() {// when the timer reaches 0
			}
		};

		return scene;
	}//end of onLoadScene method

	@Override
	public void onLoadComplete() {
		timer.start(); // start the timer
	}//end of onLoadComplete method

	IUpdateHandler detect = new IUpdateHandler() {
		public void onUpdate(float pSecondsElapsed) {
			
			Iterator<Sprite> Object = ObjectIterator.iterator();
			Sprite _Object;
			
			while (Object.hasNext()) {
				_Object = Object.next();

				// if target passed the left edge of the screen, then remove it
				// and call a fail
				if (_Object.getX() <= -_Object.getWidth()) {
					scene.detachChild(_Object);
					continue;
				}

					// if the targets collides with a projectile, remove the
					// projectile and set the hit flag to true
					if (_Object.collidesWith(player)) {
						scene.detachChild(player);
						Intent myIntent = new Intent(Game.this, GameOver.class);
						Game.this.startActivity(myIntent);
						break;
					}
			}

			if (CurrentScore > 500 && count == 3){
				count = 0;
				//spawn enemy
				addObject();
			}
			else if (CurrentScore > 100 && count == 4){
				count = 0;
				//spawn enemy
				addObject();
			}
			else if (count == 5){
				count = 0;
				//spawn enemy
				addObject();
			}	
			
			ObjectIterator.addAll(ObjectsToBeAdded);
			ObjectsToBeAdded.clear();
		}//end onUpdate method

		@Override
		public void reset() {
			// TODO Auto-generated method stub

		}
	};
	
	public void addObject() {
		int x = ((CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2) - 200;
		int y = (CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight() - 5 + mObjectTextureRegion.getWidth()) - 40;

		Sprite Object = new Sprite(x+800, y - 40, mObjectTextureRegion);
		scene.attachChild(Object);

		int actualDuration = 4;

		MoveXModifier mod = new MoveXModifier(actualDuration, Object.getX(), -Object.getWidth());
		Object.registerEntityModifier(mod);
		ObjectsToBeAdded.add(Object);

	}
	
	private void createSpriteSpawnTimeHandler() {
		TimerHandler spriteTimerHandler;
		float mEffectSpawnDelay = 1f;

		spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {

						addObject();
					}
				});

		getEngine().registerUpdateHandler(spriteTimerHandler);
	}

	public void jump()
	{      
		//player jumps
		scene.detachChild(player);
		final int playerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2;
		final int playerY = CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight() - 5;
		// Create a sprite to hand in
		player = new AnimatedSprite(playerX - 200, playerY - 100, this.mPlayerTextureRegion);
		player.setScaleCenterY(this.mPlayerTextureRegion.getTileHeight());
		player.setScale(2);
		player.animate(new long[]{200, 200, 200}, 3, 5, true);
		scene.attachChild(player);
		CountDownTimer timer = new CountDownTimer(500, 1000) {
			//what happens when the timer ticks down
			public void onTick(long howMuchTimeLeft) {
			}
			public void onFinish() {
				land();
			}//end of the onFinish method
		};//end of the new CountDownTimer
		timer.start();		
	}

	public void land(){
		//player jumps
		scene.detachChild(player);
		final int playerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2;
		final int playerY = CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight() - 5;
		// Create a sprite to hand in
		player = new AnimatedSprite(playerX - 200, playerY - 40, this.mPlayerTextureRegion);
		player.setScaleCenterY(this.mPlayerTextureRegion.getTileHeight());
		player.setScale(2);
		player.animate(new long[]{200, 200, 200}, 3, 5, true);
		scene.attachChild(player);
	}
}//end of Game class

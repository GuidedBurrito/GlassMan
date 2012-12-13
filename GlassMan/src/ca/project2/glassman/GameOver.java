//Application Name: Glass Man
//By: Jonathan Hodder and Alex
//Purpose of Application: Allows user to play a platformer game where they dodge shapes. When the user
//collides with an object the game ends
//Purpose of Activity: When the game ends the game over screen appear and clicking on the game over allows 
//the user to return to the main menu.  
//Version 1: There are no problems with the game over activity itself but because the game activity is not 
//working some of the functionality can not be tested as easily.
package ca.project2.glassman;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class GameOver extends BaseGameActivity{

	//declare constants
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	//declare objects
	private Camera mCamera;
	private Scene scene;

	private Texture mTexture;

	private Texture mAutoParallaxBackgroundTexture;
	private TextureRegion mGameOverTextTextureRegion;
	private Sprite GameOverText;
	private TextureRegion mParallaxLayerBack;   

	//load up the new engine to application
	public Engine onLoadEngine() {
		//create new camera
		this.mCamera = new Camera (0,0,CAMERA_WIDTH, CAMERA_HEIGHT);
		//add new engine to application
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}//end of onLoadEngine method

	//load resources into the application
	public void onLoadResources() {
		//create a new texture
		this.mTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//add textures and the background texture
		this.mAutoParallaxBackgroundTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);
		this.mParallaxLayerBack = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/BackgroundResized.png", 0, 188);
		//add the game over text
		this.mGameOverTextTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/Game_Over.png", 3, 4);
		//add textures to the and engine
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		this.mEngine.getTextureManager().loadTexture(this.mAutoParallaxBackgroundTexture);
	}//end of onLoadResources method
	
	//load scene to application
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		//create new scene
		scene = new Scene(1);
		//create a new parallax background
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		//add a entity to the auto parallax background
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack)));
		//add background to the scene
		scene.setBackground(autoParallaxBackground);

		//declare the dimensions of the player
		final int playerX = (CAMERA_WIDTH - this.mGameOverTextTextureRegion.getWidth()) / 2;
		final int playerY = CAMERA_HEIGHT - this.mGameOverTextTextureRegion.getHeight() - 5;

		//add the game over sprite
		GameOverText = new Sprite(playerX, playerY - 40, this.mGameOverTextTextureRegion);
		GameOverText.setScaleCenterY(this.mGameOverTextTextureRegion.getHeight());
		GameOverText.setScale(2);

		//add the game over text to the scene
		scene.attachChild(GameOverText);

		//return the scene
		return scene;
	}//end of onLoadScene method

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
	}//end of onLoadComplete method
}//end of GameOver class

//Application Name: Glass Man
//By: Jonathan Hodder and Alex
//Purpose of Application: Allows user to play a platformer game where they dodge shapes. When the user
//collides with an object the game ends
//Purpose of Activity: Display a splash screen to the user and then goes to the main menu
//Version 1: Problems with image size
//Version 2: Image size fixed and splash screen working.  
//Version 3: Splash screen after 3 seconds moves on to main menu screen
package ca.project2.glassman;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import android.content.Intent;
import android.os.Handler;


public class SplashScreen extends BaseGameActivity {

	//declare camera dimension cosntants
	private static final int CAMERA_WIDTH = 512;
	private static final int CAMERA_HEIGHT = 360;

	//declare objects
	private Camera mCamera;
	private Texture mTexture;
	private TextureRegion mSplashTextureRegion;
	private Handler mHandler;

	@Override
	//What happens when the engine loads
	public Engine onLoadEngine() {
		//create a new handler
		mHandler = new Handler();
		//create a new camera with set constant camera dimensions
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		//return the new engine and engine options
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				this.mCamera));
	}//end of onLoadEngine method
	//Loads the resources into the app
	@Override
	public void onLoadResources() {
		//create a new texture
		this.mTexture = new Texture(512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//create the splash screen backgroud
		this.mSplashTextureRegion = TextureRegionFactory.createFromAsset(
				this.mTexture, this, "gfx/SplashScreenResized.png", 0, 0);
		//load the textures into the engine
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
	}//end of onLoadResources method

	@Override
	//Loads the objects created in the scene onto the app
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);

		//Center the splash on the camera.
		final int centerX = (CAMERA_WIDTH - this.mSplashTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mSplashTextureRegion.getHeight()) / 2;

		//Create the sprite and add it to the scene.
		final Sprite splash = new Sprite(centerX, centerY, this.mSplashTextureRegion);
		scene.getLastChild().attachChild(splash);
		//return the scene and the objects added to the scene
		return scene;
	}//end of the onLoadScene method

	@Override
	//actions taken after the app has loaded completely
	public void onLoadComplete() {
		mHandler.postDelayed(mLaunchTask, 3000);
	}//end of onLoadComplete method

	//create a new runnable
	private Runnable mLaunchTask = new Runnable() {
		public void run() {
			//go the main menu activity
			Intent myIntent = new Intent(SplashScreen.this,
					MainMenu.class);
			SplashScreen.this.startActivity(myIntent);
		}//end of run method
	};//end of Runnable 
}//end of splashscreen class


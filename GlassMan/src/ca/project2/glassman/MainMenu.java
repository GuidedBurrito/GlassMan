//Application Name: Glass Man
//By: Jonathan Hodder and Alex
//Purpose of Application: Allows user to play a platformer game where they dodge shapes. When the user
//collides with an object the game ends
//Purpose of Activity: Main menu of the game.  Allows user to start a new game.  Also allows user to look
//at options on how to play the game and to exit the game.
//Version 1: Added main menu background to screen.
//Version 2: Got buttons on to the main menu
//Version 3: Added functionality to the quit and play buttons
//Version 4: Added popup display for the about game.  Describes what the game is about.
package ca.project2.glassman;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.ScaleAtModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.KeyEvent;


public class MainMenu extends BaseGameActivity implements IOnMenuItemClickListener {
	//declare constants
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	
	protected static final int MENU_ABOUT = 0;
	protected static final int MENU_PLAY = 100;
	protected static final int MENU_HELP = MENU_PLAY + 1;
	protected static final int MENU_QUIT = MENU_HELP + 1;

	//declare objects
	protected Camera mCamera;

	protected Scene mMainScene;
	protected Handler mHandler;

	private Texture mMenuBackTexture;
	private TextureRegion mMenuBackTextureRegion;

	protected MenuScene mStaticMenuScene, mPopUpMenuScene;

	private Texture mPopUpTexture;
	private Texture mFontTexture;
	private Font mFont;
	protected TextureRegion mPopUpAboutTextureRegion;
	protected TextureRegion mPopUpQuitTextureRegion;
	protected TextureRegion mMenuPlayTextureRegion;
	protected TextureRegion mMenuQuitTextureRegion;
	protected TextureRegion mMenuHelpTextureRegion;
	private boolean popupDisplayed;
	
	@Override
	//load up the engine to the application
	public Engine onLoadEngine() {
		//create new handler
		mHandler = new Handler();
		//create a new camera
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		//add new engine to application
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}//end of onLoadEngine method

	@Override
	//load resources to application
	public void onLoadResources() {
		//create a new font texture
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		FontFactory.setAssetBasePath("font/");
		//set font texture properties and add to the and engine
		this.mFont = FontFactory.createFromAsset(this.mFontTexture, this, "ARIBLK.TTF", 32, true, Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);
		//create new menu texture, add new menu texture to and engine
		this.mMenuBackTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuBackTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuBackTexture, this, "gfx/BackgroundResized.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuBackTexture);
		//create new popup texture, add buttons and add them to the and engine
		this.mPopUpTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mPopUpAboutTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, this, "gfx/Help.png", 0, 0);
		this.mPopUpQuitTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, this, "gfx/Play.png", 0, 50);
		this.mEngine.getTextureManager().loadTexture(this.mPopUpTexture);
		//set popup display to false
		popupDisplayed = false;
		}//end of onLoadResources method

	@Override
	//load scene to the application
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		//create a static menu and popup menu
		this.createStaticMenuScene();
		this.createPopUpMenuScene();

		/* Center the background on the camera. */
		final int centerX = (CAMERA_WIDTH - this.mMenuBackTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mMenuBackTextureRegion.getHeight()) / 2;

		this.mMainScene = new Scene(1);
		/* Add the background and static menu */
		final Sprite menuBack = new Sprite(centerX, centerY, this.mMenuBackTextureRegion);
		mMainScene.getLastChild().attachChild(menuBack);
		mMainScene.setChildScene(mStaticMenuScene);

		//return the main scene
		return this.mMainScene;
	}//end of onLoadScene method

	@Override
	public void onLoadComplete() {
	}//end of onLoadComplete method

	public void onResumeGame() {
		super.onResume();
		mMainScene.registerEntityModifier(new ScaleAtModifier(0.5f, 0.0f, 1.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
		mStaticMenuScene.registerEntityModifier(new ScaleAtModifier(0.5f, 0.0f, 1.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
	}//end of onResumeGame method

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(popupDisplayed) {
				/* Remove the menu and reset it. */
				this.mPopUpMenuScene.back();
				mMainScene.setChildScene(mStaticMenuScene);
				popupDisplayed = false;
			} else {
				/* Attach the menu. */
				this.mMainScene.setChildScene(this.mPopUpMenuScene, false, true, true);
				popupDisplayed = true;
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}//end onKeyDown

	@Override
	//what happens when a menu item is clicked
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_QUIT:
				/* End Activity. */
				this.finish();
				return true;
			case MENU_PLAY:
				//start game activity
				mMainScene.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0.0f));
				mStaticMenuScene.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0.0f));
				mHandler.postDelayed(mLaunchLevel1Task,1000);
				return true;
			case MENU_HELP:
				//display game options
				AlertDialog.Builder builder = new AlertDialog.Builder(this); // creates new dialogbox		 
				 builder.setCancelable(false); // user cant cancel the textbox must select yes or no
				 builder.setTitle("Help"); // sets dialogbox title
				 builder.setMessage("Welcome to GlassMan! \n \n " +
				 					"In GlassMan you must avoid running into obstacles while you run through the street. \n \n" +
				 					"To avoid obstacles, jump over them by tapping the screen.\n \n Watch out for coins!" +
				 					" Collecting them will boost your score! \n \n How long can you survive?"); // sets dialogbox message
				 
				 builder.setInverseBackgroundForced(true); // inveses the background
				 builder.setPositiveButton("GO!", new DialogInterface.OnClickListener() { // creates the yes button for the dialogbox
				   
					 @Override
				   public void onClick(DialogInterface dialog, int which) {
					      dialog.dismiss(); // closes dialogbox
				   }
				 });
				 AlertDialog alert = builder.create(); 
				 alert.show(); // shows dialogbox
				return true;
			default:
				return false;
		}
	}//end of onMenuItemClicked method

	protected void createStaticMenuScene() {
		//create a new static menu scene
		this.mStaticMenuScene = new MenuScene(this.mCamera);
		//add menu items
		final IMenuItem playMenuItem = new ColorMenuItemDecorator( new TextMenuItem(MENU_PLAY, mFont, "Play Game"), 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		playMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mStaticMenuScene.addMenuItem(playMenuItem);

		final IMenuItem helpMenuItem = new ColorMenuItemDecorator( new TextMenuItem(MENU_HELP, mFont, "Help"), 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		helpMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mStaticMenuScene.addMenuItem(helpMenuItem);
		
		final IMenuItem quitMenuItem = new ColorMenuItemDecorator( new TextMenuItem(MENU_QUIT, mFont, "Quit"), 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mStaticMenuScene.addMenuItem(quitMenuItem);
		this.mStaticMenuScene.buildAnimations();
		
		this.mStaticMenuScene.setBackgroundEnabled(false);
		//create item click listener
		this.mStaticMenuScene.setOnMenuItemClickListener(this);
	}//end of createStaticMenu method

	protected void createPopUpMenuScene() {
		//create new popup menu scene
		this.mPopUpMenuScene = new MenuScene(this.mCamera);

		//add sprite menu items
		final SpriteMenuItem aboutMenuItem = new SpriteMenuItem(MENU_ABOUT, this.mPopUpAboutTextureRegion);
		aboutMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPopUpMenuScene.addMenuItem(aboutMenuItem);

		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, this.mPopUpQuitTextureRegion);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPopUpMenuScene.addMenuItem(quitMenuItem);
		this.mPopUpMenuScene.setMenuAnimator(new SlideMenuAnimator());

		this.mPopUpMenuScene.buildAnimations();

		this.mPopUpMenuScene.setBackgroundEnabled(false);

		//create click listener
		this.mPopUpMenuScene.setOnMenuItemClickListener(this);
	}//end of createPopUpMenuScene

    private Runnable mLaunchLevel1Task = new Runnable() {
        public void run() {
        	//start game activity
    		Intent myIntent = new Intent(MainMenu.this, Game.class);
    		MainMenu.this.startActivity(myIntent);
        }//end run method
     };//end of runnable
}//end of MainMenu class

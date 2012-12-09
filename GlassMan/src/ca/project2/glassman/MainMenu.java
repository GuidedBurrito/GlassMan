package ca.project2.glassman;

import java.io.IOException;

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
import android.widget.Toast;

public class MainMenu extends BaseGameActivity implements IOnMenuItemClickListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	
	protected static final int MENU_ABOUT = 0;
	protected static final int MENU_PLAY = 100;
	protected static final int MENU_HELP = MENU_PLAY + 1;
	protected static final int MENU_QUIT = MENU_HELP + 1;

	

	// ===========================================================
	// Fields
	// ===========================================================

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

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		mHandler = new Handler();
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		/* Load Font/Textures. */
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		FontFactory.setAssetBasePath("font/");
		this.mFont = FontFactory.createFromAsset(this.mFontTexture, this, "ARIBLK.TTF", 32, true, Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);

		this.mMenuBackTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuBackTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuBackTexture, this, "gfx/BackgroundResized.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuBackTexture);
		
		this.mPopUpTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mPopUpAboutTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, this, "gfx/Help.png", 0, 0);
		this.mPopUpQuitTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, this, "gfx/Play.png", 0, 50);
		this.mEngine.getTextureManager().loadTexture(this.mPopUpTexture);
		popupDisplayed = false;
		}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

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

		return this.mMainScene;
	}

	@Override
	public void onLoadComplete() {
	}

	public void onResumeGame() {
		super.onResume();
		mMainScene.registerEntityModifier(new ScaleAtModifier(0.5f, 0.0f, 1.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
		mStaticMenuScene.registerEntityModifier(new ScaleAtModifier(0.5f, 0.0f, 1.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
	}

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
	}

	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_QUIT:
				/* End Activity. */
				this.finish();
				return true;
			case MENU_PLAY:
				mMainScene.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0.0f));
				mStaticMenuScene.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0.0f));
				mHandler.postDelayed(mLaunchLevel1Task,1000);
				return true;
			case MENU_HELP:
				AlertDialog.Builder builder = new AlertDialog.Builder(this); // creates new dialogbox		 
				 builder.setCancelable(false); // user cant cancel the textbox must select yes or no
				 builder.setTitle("Help"); // sets dialogbox title
				 builder.setMessage("It works"); // sets dialogbox message
				 
				 builder.setInverseBackgroundForced(true); // inveses the background
				 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // creates the yes button for the dialogbox
				   
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
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	protected void createStaticMenuScene() {
		this.mStaticMenuScene = new MenuScene(this.mCamera);

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

		this.mStaticMenuScene.setOnMenuItemClickListener(this);
	}

	protected void createPopUpMenuScene() {
		this.mPopUpMenuScene = new MenuScene(this.mCamera);

		final SpriteMenuItem aboutMenuItem = new SpriteMenuItem(MENU_ABOUT, this.mPopUpAboutTextureRegion);
		aboutMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPopUpMenuScene.addMenuItem(aboutMenuItem);

		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, this.mPopUpQuitTextureRegion);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPopUpMenuScene.addMenuItem(quitMenuItem);
		this.mPopUpMenuScene.setMenuAnimator(new SlideMenuAnimator());

		this.mPopUpMenuScene.buildAnimations();

		this.mPopUpMenuScene.setBackgroundEnabled(false);

		this.mPopUpMenuScene.setOnMenuItemClickListener(this);
	}

    private Runnable mLaunchLevel1Task = new Runnable() {
        public void run() {
    		Intent myIntent = new Intent(MainMenu.this, Game.class);
    		MainMenu.this.startActivity(myIntent);
        }
     };

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

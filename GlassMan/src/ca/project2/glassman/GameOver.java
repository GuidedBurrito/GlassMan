package ca.project2.glassman;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class GameOver extends BaseGameActivity{
	

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;


	private Camera mCamera;
    private Scene scene;

    private Texture mTexture;

    private Texture mAutoParallaxBackgroundTexture;
    private TextureRegion mGameOverTextTextureRegion;
    private Sprite GameOverText;
    private TextureRegion mParallaxLayerBack;   
   
    public Engine onLoadEngine() {
            this.mCamera = new Camera (0,0,CAMERA_WIDTH, CAMERA_HEIGHT);
            return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
    }
   
    public void onLoadResources() {
            this.mTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

            this.mAutoParallaxBackgroundTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);
            this.mParallaxLayerBack = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/BackgroundResized.png", 0, 188);
            this.mGameOverTextTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/Game_Over.png", 3, 4);
            this.mEngine.getTextureManager().loadTexture(this.mTexture);
            this.mEngine.getTextureManager().loadTexture(this.mAutoParallaxBackgroundTexture);
           
    }
    public Scene onLoadScene() {
            this.mEngine.registerUpdateHandler(new FPSLogger());
           
            MoveXModifier left = new MoveXModifier(5f, CAMERA_WIDTH - 15, 0);
            
            scene = new Scene(1);
            final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
            autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-20.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack)));
            scene.setBackground(autoParallaxBackground);
            
            final int playerX = (CAMERA_WIDTH - this.mGameOverTextTextureRegion.getWidth()) / 2;
    		final int playerY = CAMERA_HEIGHT - this.mGameOverTextTextureRegion.getHeight() - 5;

    		/* Create two sprits and add it to the scene. */
    		GameOverText = new Sprite(playerX - 200, playerY - 40, this.mGameOverTextTextureRegion);
    	    GameOverText.setScaleCenterY(this.mGameOverTextTextureRegion.getHeight());
    		GameOverText.setScale(2);
    		 
            
    		scene.attachChild(GameOverText);
    		
    		
            return scene;
            
            
    }

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}

}

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



import android.content.Intent;
import android.widget.Toast;

public class Game extends BackgroundHelper {


	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;


	private Camera mCamera;
    private Scene scene;

    private Texture mTexture;
   
    private TextureRegion mBarrelTextureRegion;
    private TextureRegion mCrateTextureRegion;
    private Texture mAutoParallaxBackgroundTexture;
    private TiledTextureRegion mPlayerTextureRegion;
    private Sprite Crate;
    private Sprite Barrel;
    private AnimatedSprite player;
    private TextureRegion mParallaxLayerBack;   
   
    @Override
    public Engine onLoadEngine() {
            this.mCamera = new Camera (0,0,CAMERA_WIDTH, CAMERA_HEIGHT);
            return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
    }
   
    @Override
    public void onLoadResources() {
            this.mTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

            this.mAutoParallaxBackgroundTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);
            this.mParallaxLayerBack = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/BackgroundResized.png", 0, 188);
            this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/GlassPlayer.png", 0, 0, 3, 4);
            this.mBarrelTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/BarrelPictureResized.png", 128, 0);
            this.mCrateTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/CratePictureResized.png", 128, 0);
            this.mEngine.getTextureManager().loadTexture(this.mTexture);
            this.mEngine.getTextureManager().loadTexture(this.mAutoParallaxBackgroundTexture);
           
    }
    @Override
    public Scene onLoadScene() {
            this.mEngine.registerUpdateHandler(new FPSLogger());
           
            MoveXModifier left = new MoveXModifier(5f, CAMERA_WIDTH - 15, 0);
            
            scene = new Scene(1);
            final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
            autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-20.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack)));
            scene.setBackground(autoParallaxBackground);
            
            final int playerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2;
    		final int playerY = CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight() - 5;

    		/* Create two sprits and add it to the scene. */
    	    player = new AnimatedSprite(playerX - 200, playerY - 40, this.mPlayerTextureRegion);
    		player.setScaleCenterY(this.mPlayerTextureRegion.getTileHeight());
    		player.setScale(2);
    		player.animate(new long[]{200, 200, 200}, 3, 5, true);    
            
    		scene.attachChild(player);
            
    		Crate = new Sprite(CAMERA_WIDTH, playerY - 50, mCrateTextureRegion);
    		Crate.registerEntityModifier(left);
    		scene.attachChild(Crate);
    		
    		 
    		Barrel = new Sprite(CAMERA_WIDTH, playerY - 50, mBarrelTextureRegion);
    		Barrel.registerEntityModifier(left);
    		scene.attachChild(Barrel);
    		
    		
            return scene;
            
            
    }
    @Override
    public void onLoadComplete() {
            // TODO Auto-generated method stub
           
           
    }
    public void onUpdate(float pSecondsElapsed) {
    	
    	
    	if (Crate.getX() > 0){
			//removeSprite(Crate);
    		Toast.makeText(Game.this, "get x works" + "", Toast.LENGTH_SHORT).show();
    	}
    	if (Crate.collidesWith(player)) {
    		Intent myIntent = new Intent(Game.this, GameOver.class);
    		Game.this.startActivity(myIntent);
    	}
    }
	
    	public void removeSprite(final Sprite _sprite) {
    		runOnUpdateThread(new Runnable() {

    			@Override
    			public void run() {
    				scene.detachChild(_sprite);
    			}
    		});
    	}
}

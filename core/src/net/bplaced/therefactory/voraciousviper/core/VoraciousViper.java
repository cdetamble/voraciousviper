package net.bplaced.therefactory.voraciousviper.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.voraciousviper.core.constants.Config;
import net.bplaced.therefactory.voraciousviper.core.constants.PrefsKeys;
import net.bplaced.therefactory.voraciousviper.core.misc.IAndroidInterface;
import net.bplaced.therefactory.voraciousviper.core.misc.SettingsManager;
import net.bplaced.therefactory.voraciousviper.core.misc.Utils;
import net.bplaced.therefactory.voraciousviper.core.screens.GameScreen;
import net.bplaced.therefactory.voraciousviper.core.screens.LogoScreen;
import net.bplaced.therefactory.voraciousviper.core.screens.TitleScreen;

import java.util.Locale;

public class VoraciousViper extends Game {

	private static VoraciousViper instance;
	private final IAndroidInterface androidInterface;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private FitViewport viewportGame;
	private FitViewport viewportOther;

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

	private BitmapFont amigaFont;
	private BitmapFont vcrOsdFont;

    private TitleScreen titleScreen;
    private GameScreen gameScreen;

    private Sprite spriteFade;
    private I18NBundle bundle;

    private Music music;
    private String[] musicFiles; // list of music files located in "assets/audio/music"
    private int currentMusicFile = -1; // pointer to the music file that is currently being played

	public VoraciousViper(IAndroidInterface androidInterface) {
		this.androidInterface = androidInterface;
	}
	
    @Override
    public void create() {
    	instance = this;
        textureAtlas = new TextureAtlas("textures.pack");
        assetManager = new AssetManager();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        amigaFont = Utils.initializeFont("fonts/amiga4ever pro2.ttf", 8);
        vcrOsdFont = Utils.initializeFont("fonts/VCR_OSD_MONO_1.001.ttf", 16);
		
        // set up camera view
        camera = new OrthographicCamera();
        viewportGame = new FitViewport(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, camera);
        viewportOther = new FitViewport(Config.WINDOW_WIDTH, Config.WINDOW_WIDTH/16*9, camera);
        camera.position.set(viewportOther.getWorldWidth()/2, viewportOther.getWorldHeight()/2, 0);
        camera.update();

        spriteFade = textureAtlas.createSprite("fade");
		spriteFade = textureAtlas.createSprite("fade");
		spriteFade.setBounds(0, 0, viewportGame.getWorldWidth(), viewportGame.getWorldHeight());
        
        SettingsManager.getInstance().loadSettings();
        bundle = getBundle(SettingsManager.getInstance().get().getString("language", Locale.ENGLISH.toString()));
        
        // add sounds to queue for asynchronous loading while the user is in main menu
        assetManager.load("audio/sounds/win.ogg", Sound.class);
        assetManager.load("audio/sounds/lose.ogg", Sound.class);
        assetManager.load("audio/sounds/collect.ogg", Sound.class);
        assetManager.load("audio/sounds/applause.ogg", Sound.class);
        assetManager.load("audio/sounds/one_blow_from_party_horn.ogg", Sound.class);
        
       setScreen(new LogoScreen(batch, shapeRenderer, assetManager));
    }

	public String getVersionName() {
		if (androidInterface != null && androidInterface.getVersionName() != null)
			return androidInterface.getVersionName();
		return Config.GAME_VERSION_NAME;
	}
	
	public int getVersionCode() {
		if (androidInterface != null)
			return androidInterface.getVersionCode();
		return Config.GAME_VERSION_CODE;
	}

	private I18NBundle getBundle(String locale) {
		String pathToBundle = "i18n/" + locale;
		if (!assetManager.isLoaded(pathToBundle)) {
			assetManager.load(pathToBundle, I18NBundle.class);
	        assetManager.finishLoading();
		}
        return assetManager.get(pathToBundle, I18NBundle.class);
	}

	public static VoraciousViper getInstance() {
		return instance;
	}

    public void startGame() {
		if (gameScreen == null) {
			gameScreen = new GameScreen(batch, shapeRenderer, viewportGame, camera, assetManager, textureAtlas, vcrOsdFont);
		}
		gameScreen.getLevel().reset();
        setScreen(gameScreen);
	}
    
    public void showTitleScreen(){
        if (titleScreen == null){
            titleScreen = new TitleScreen(shapeRenderer, viewportOther, camera, assetManager, textureAtlas, amigaFont);
        }
        setScreen(titleScreen);
    }

	public Sprite getFadeSprite() {
		return spriteFade;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
	}

	public void playSound(String path) {
		if (!SettingsManager.getInstance().get().getBoolean(PrefsKeys.Sound)) {
			return;
		}
		if (!assetManager.isLoaded(path)) {
			assetManager.load(path, Sound.class);
			assetManager.finishLoading();
		}
		assetManager.get(path, Sound.class).play();
	}

	public I18NBundle getBundle() {
		return bundle;
	}

	public I18NBundle setLocale(String locale) {
		bundle = getBundle(locale);
		return getBundle();
	}

	public BitmapFont getAmigaFont() {
		return amigaFont;
	}

	public void toast(String message, boolean longDuration) {
		if (androidInterface == null) {
			Gdx.app.log(getClass().getName(), message);
			return;
		}
		androidInterface.toast(message, longDuration);
	}

	/**
	 * Either continues the music playback of a previously paused music file or proceeds to the next music file depending on the proceedToNext flag.
	 * If there was no previous music file, a new music file is chosen randomly.
	 * @param proceedToNext If set to true, a new music file is chosen randomly. If set to false, the previously paused file is being resumed.
	 */
	public void playMusicFile(boolean proceedToNext) {

        // remember all available music files
        if (musicFiles == null) {
            FileHandle dirHandle = Gdx.files.internal("audio/music");
            FileHandle[] fileList = dirHandle.list();
            musicFiles = new String[fileList.length];
            for (int i = 0; i < musicFiles.length; i++) {
                musicFiles[i] = fileList[i].path();
                Gdx.app.log(getClass().getName(), "Found '" + fileList[i].path() + "'");
            }
        }
        
        if (musicFiles.length == 0) {
            Gdx.app.error(getClass().getName(), "Could not find any music files!");
        }
        else {
            // select a music file to play
            if (currentMusicFile == -1) {
                // if there is no previous music file, choose a new one randomly
                currentMusicFile = (short) Utils.randomWithin(0, musicFiles.length - 1);
                music = loadMusicAsset(musicFiles[currentMusicFile]);
            } else if (proceedToNext) {
                // switch to the next music file randomly
                int previousMusicFile = currentMusicFile;
                do {
                    currentMusicFile = (short) Utils.randomWithin(0, musicFiles.length - 1);
                } while (previousMusicFile == currentMusicFile);
                assetManager.unload(musicFiles[previousMusicFile]); // free the resources of the previous music file
                if (music != null) {
                    music.dispose();
                    music = null;
                }
                music = loadMusicAsset(musicFiles[currentMusicFile]);
            } else {
                // resume previously paused music file
            }
    
            // play the selected music file
            music.play();
            music.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    playMusicFile(true);
                }
            });
}
	}

    public void pauseMusic() {
        if (music != null) {
            music.pause();
        }
    }

    private Music loadMusicAsset(String path) {
        assetManager.load(path, Music.class);
        assetManager.finishLoading();
        Gdx.app.log(getClass().getName(), "Loaded '" + path + "'");
        return assetManager.get(path);
    }

	public void showScoreTable() {
		showTitleScreen();
		titleScreen.showScoreTable();
	}

}

package net.bplaced.therefactory.voraciousviper.core.constants;

/**
 * Created by Christian on 17.09.2016.
 */
public class Config {

    /**
     * if set to true the following changes apply to the game:
     *  - logos are played in a loop until the screen is touched
     *  - game can be paused at will
     *  - level can be changed at will
     * Important: set to false in release versions
     */
    public static final boolean DEBUG_MODE = false;

    // if set to true the google play store badge will be displayed in the main menu
    public static final boolean SHOW_BADGE = false;

    // app
	public static final String GAME_TITLE = "VoVi: Voracious Viper";
    public static final String GAME_VERSION_NAME = "1.0.1";
    public static final int GAME_VERSION_CODE = 5;

    // game play
    public static final int NUM_FRAMES_FOR_TICK = 8;
    public static final int NUM_LIVES_INITIAL = 5;
    
    // game screen
    public static final int WINDOW_WIDTH = 624;
    public static final int WINDOW_HEIGHT = 342;

    // level dimension
    public static final int TILE_WIDTH = 24;
    public static final int TILE_HEIGHT = 18;
    public static final int NUM_TILES_ROW = 26;
    public static final int NUM_TILES_COLUMN = 18;

    // animation
    public static final float FADING_SPEED = .05f;
	public static final int NUM_FRAMES_DURING_LEVEL_TRANSITION = NUM_FRAMES_FOR_TICK / 4;
	
	// other
	public static final int MAX_LENGTH_PLAYERNAME = 20;
	public static final int LINE_HEIGHT_HIGHSCORES = 11;

}

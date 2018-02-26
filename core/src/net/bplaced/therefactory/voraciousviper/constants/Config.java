/*
 * Copyright (C) 2016  Christian DeTamble
 *
 * This file is part of Voracious Viper.
 *
 * Voracious Viper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Voracious Viper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Voracious Viper.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bplaced.therefactory.voraciousviper.constants;

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
    public static final String GAME_VERSION_NAME = "1.0.4";
    public static final int GAME_VERSION_CODE = 7;

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

	// uris
    public static final String URL_TO_SOUNDTRACK = "https://goo.gl/imaHWj";
    public static final String URL_TO_PLAY_STORE = "https://goo.gl/g0przi";

}

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

package net.bplaced.therefactory.voraciousviper.core.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import net.bplaced.therefactory.voraciousviper.core.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.core.constants.Config;
import net.bplaced.therefactory.voraciousviper.core.misc.SettingsManager;
import net.bplaced.therefactory.voraciousviper.core.model.Tile.TileType;
import net.bplaced.therefactory.voraciousviper.core.model.Viper.SpriteColor;
import net.bplaced.therefactory.voraciousviper.core.net.HttpServer;
import net.bplaced.therefactory.voraciousviper.core.screens.GameScreen;

import static net.bplaced.therefactory.voraciousviper.core.screens.GameScreen.GameState.GameIsBeginning;
import static net.bplaced.therefactory.voraciousviper.core.screens.GameScreen.GameState.LevelFinished;
import static net.bplaced.therefactory.voraciousviper.core.screens.GameScreen.GameState.LevelTransition;
import static net.bplaced.therefactory.voraciousviper.core.screens.GameScreen.GameState.ShowContinueDialog;
import static net.bplaced.therefactory.voraciousviper.core.screens.GameScreen.GameState.ShowGameOverDialog;
import static net.bplaced.therefactory.voraciousviper.core.screens.GameScreen.GameState.ViperCrashed;

public class Level {

    // temporary variables
	private int numTicks;
	private int indexCurrentColumn;
	
	private final Tile[][] tiles; // holds all level tiles including the viper
    private final Tile[][][] tilesDefault; // for resetting the level to its default state
	private final Array<TileAnimated> tilesAnimated; // for fast looping over animated tiles excluding the viper
	private int indexCurrentLevel, indexCurrentLevelContinuous;
    private final Viper viper;
	private final GameScreen gameScreen;
    private boolean animateInThisTick;
    private int numConsumableTiles;
    private int numConsumedTiles;
	private boolean hadFirstLevelTransition;

    private enum LevelCommands {
        Point, Line, Rectangular, Grid
    }

    private Level(GameScreen gameScreen, TextureAtlas textureAtlas, short numLevels) {
    	this.gameScreen = gameScreen;
        tiles = new Tile[Config.NUM_TILES_ROW][Config.NUM_TILES_COLUMN];
        tilesDefault = new Tile[numLevels][Config.NUM_TILES_ROW][Config.NUM_TILES_COLUMN];
        tilesAnimated = new Array<TileAnimated>();
        viper = new Viper(this, textureAtlas);
        indexCurrentLevel = 0;
        indexCurrentLevelContinuous = 0;
        numConsumedTiles = 0;
        numConsumableTiles = 0;
    }

    public static Level fromFile(GameScreen gameScreen, FileHandle file, TextureAtlas textureAtlas) {
        String levelAsString = file.readString();
        
        // count number of levels
        short numLevels = 0;
    	String[] splittedByNewline = levelAsString.split("\n");
    	for (String line : splittedByNewline) {
        	line = line.trim();
            if (line.length() == 0) { // skip empty lines
                continue;
            }
            if (line.startsWith("-")) {
            	numLevels++;
            }
    	}
    	Gdx.app.log(Level.class.getName(), "Loading " + numLevels + " levels from '" + file + "'");
    	
        Level level = new Level(gameScreen, textureAtlas, numLevels);
        
        // prepare temporary variables needed for parsing
        int x, y;
        Tile tile;
        int currLevel = -1;
        
        // parse level file
        for (String line : splittedByNewline) {
        	line = line.trim();
            if (line.length() == 0) { // skip empty lines
                continue;
            }
            if (line.startsWith("-")) {
            	Gdx.app.log(Level.class.getName(), line);    
                currLevel++;
                continue;
            }
            line = line.replaceAll(" +", " "); // replace multiple whitespaces with a single whitespace
            Gdx.app.log(Level.class.getName(), line);            
            GridPoint2 startPoint, endPoint;
			switch (LevelCommands.valueOf(line.substring(0, line.indexOf(" ")))) {
				/**
				 * Example: Line (0,0) (0,5) Consumable Skull-1 [Skull-2]
				 * Constraints:
				 *     - first point must have smaller x value
				 */
                case Line:
                    GridPoint2 p1 = getPoint(line.substring(line.indexOf("("), line.indexOf(")")));
                    GridPoint2 p2 = getPoint(line.substring(line.lastIndexOf("("), line.lastIndexOf(")")));
                    x = Math.min(p1.x, p2.x); 
                    y = p1.y <= p2.y ? Math.min(p1.y, p2.y) : Math.max(p1.y, p2.y);
                    tile = parseTile(line.substring(line.lastIndexOf(")") + 1).trim(), textureAtlas);
                    do {
                    	level.setDefaultTileForLevel(currLevel, tile, x, y);
                    	x = Math.min(x + 1, Math.max(p1.x, p2.x));
                    	y = (p1.y <= p2.y ? Math.min(y + 1, Math.max(p1.y, p2.y))
                    			: Math.max(y - 1, Math.min(p1.y, p2.y)));
                    } while (x < Math.max(p1.x, p2.x) || (p1.y <= p2.y ? y < Math.max(p1.y, p2.y) : y > Math.min(p1.y, p2.y)));
                    
                    level.setDefaultTileForLevel(currLevel, tile, x, y);
                    break;
                    
                /**
                 * Example: Point (7,12) Consumable Skull-1 [Skull-2]
                 */
                case Point:
                    GridPoint2 point = getPoint(line.substring(line.indexOf("("), line.indexOf(")")));
                    tile = parseTile(line.substring(line.lastIndexOf(")") + 1).trim(), textureAtlas);
                    level.setDefaultTileForLevel(currLevel, tile, point.x, point.y);
                    break;
                   
                /**
                 * Example: Rectangular (0,0) (25,16) Consumable Skull-1 [Skull-2]
                 */
                case Rectangular:
                    startPoint = getPoint(line.substring(line.indexOf("("), line.indexOf(")")));
                    endPoint = getPoint(line.substring(line.lastIndexOf("("), line.lastIndexOf(")")));
                    tile = parseTile(line.substring(line.lastIndexOf(")") + 1).trim(), textureAtlas);
                    for (x = startPoint.x; x <= endPoint.x; x++) {
                        level.setDefaultTileForLevel(currLevel, tile, x, startPoint.y);
                        level.setDefaultTileForLevel(currLevel, tile, x, endPoint.y);
                    }
                    for (y = startPoint.y + 1; y < endPoint.y; y++) {
                        level.setDefaultTileForLevel(currLevel, tile, startPoint.x, y);
                        level.setDefaultTileForLevel(currLevel, tile, endPoint.x, y);
                    }
                    break;
                    
                /**
                 * Example: Grid ( 6, 5) (10,13) 1,1 Consumable Coke [Skull-2]
                 */
                case Grid:
                	startPoint = getPoint(line.substring(line.indexOf("("), line.indexOf(")")));
                    endPoint = getPoint(line.substring(line.lastIndexOf("("), line.lastIndexOf(")")));
                    String lineWithputPoints = line.substring(line.lastIndexOf(")") + 1).trim();
                    GridPoint2 space = getPoint(lineWithputPoints.substring(0, lineWithputPoints.indexOf(" ")));
                    tile = parseTile(lineWithputPoints.substring(lineWithputPoints.indexOf(" ") + 1).trim(), textureAtlas);
                    for (x = startPoint.x; x <= endPoint.x; x = x + 1 + space.x) {
                    	for (y = startPoint.y; y <= endPoint.y; y = y + 1 + space.y) {
                    		level.setDefaultTileForLevel(currLevel, tile, x, y);
                    	}
                    }
                    break;
            }
        }
        return level;
    }

    private void setDefaultTileForLevel(int levelNumber, Tile tile, int x, int y) {
        tilesDefault[levelNumber][x][y] = tile;
    }

    private static Tile parseTile(String line, TextureAtlas textureAtlas) {
    	String[] words = line.split(" ");
    	TileType type = Tile.TileType.valueOf(words[0]);
    	Tile tile;
        if (words.length == 3) {
        	tile = new TileAnimated(textureAtlas.createSprite(words[1]), textureAtlas.createSprite(words[2]), type);
        } else if (words.length == 2) {
        	tile = new Tile(textureAtlas.createSprite(words[1]), type);
        } else {
        	tile = new Tile();
        }
    	if (tile.getSprite() == null) {
    		Gdx.app.log(Level.class.getName(), "Could not find '" + type + "' sprite in TextureAtlas!");
    	}
    	return tile;
	}

    Tile getTileAt(GridPoint2 gridPoint2) {
        return tiles[gridPoint2.x][gridPoint2.y];
    }

    void incrementNumConsumedTiles() {
        numConsumedTiles++;
    }

    public Viper getViper() {
        return viper;
    }
    
	public int getIndexCurrentLevel() {
		return indexCurrentLevel;
	}

	void setTile(Tile tile, GridPoint2 position) {
		tiles[position.x][position.y] = tile;
	}

	private void removeTile(int x, int y) {
		tiles[x][y] = null;
	}

    public void removeViper() {
        for (GridPoint2 position : viper.getPositions()) {
            removeTile(position.x, position.y);
        }
    }
	
	private void clearTiles() {
		for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
            	removeTile(x, y);
            }
		}
	}
	
	void removeTile(GridPoint2 position) {
		tiles[position.x][position.y] = null;
	}

    private static GridPoint2 getPoint(String word) {
        String[] point = word.replace("(", "").replace(")", "").split(",");
        return new GridPoint2(Integer.parseInt(point[0].trim()), Integer.parseInt(point[1].trim()));
    }

    public void update() {
        viper.update();
    }

    /**
     * Encapsulates main game logic.
     */
    public void tick(GameScreen.GameState gameState) {
        switch (gameState) {
        
            case GameIsRunning:
            	viper.tick(true, animateInThisTick);
                if (viper.hasCrashed()) {
                	VoraciousViper.getInstance().playSound("audio/sounds/lose.ogg");
                    gameScreen.setState(viper.getNumLives() > 2 && numConsumedTiles > 0 ? ShowContinueDialog : ViperCrashed);
                    viper.decrementLives();
                } else {
                    if (numConsumedTiles == numConsumableTiles) {
                    	VoraciousViper.getInstance().playSound("audio/sounds/win.ogg");
                        viper.setColor(Viper.SpriteColor.Blue);
                        viper.tick(false, false);
                        gameScreen.setState(LevelFinished);
                    } else {
                        animateTiles();
                        animateInThisTick = !animateInThisTick;
                    }
                }
                break;
                
			case GameIsBeginning:
				viper.tick(false, animateInThisTick);
				animateTiles();
		        animateInThisTick = !animateInThisTick;
				break;
				
			case LevelTransition:
				renderColumnOfLevel();
				if (indexCurrentColumn >= Config.NUM_TILES_ROW) { // animation is done
					setHadFirstLevelTransition(true);
					if (viper.hasCrashed()) {
						viper.setHasCrashed(false);
					}
					viper.restart(true);
					indexCurrentColumn = 0;
                    numConsumedTiles = 0;
					gameScreen.setState(GameIsBeginning);
					gameScreen.setNumFramesPerTick(Config.NUM_FRAMES_FOR_TICK);
				}
				break;
			case ShowPauseMenuDialog:
				break;
			case ShowGameOverDialog:
				break;
            case LevelFinished:
			case ViperCrashed:
                if (!gameScreen.getState().equals(ShowContinueDialog)) {
                	if (numTicks == 2) { // wait some ticks before showing level loading animation
                        numTicks = 0;
                        if (viper.hasCrashed()) {
                            if (viper.getNumLives() == 0) { // lives will be decremented in GameIsBeginning state
                                viper.setColor(SpriteColor.Green);
                                viper.tick(false, false);
                                gameScreen.setState(ShowGameOverDialog);

                                // check and upload if highscore has been improved
                                if (viper.getScore() > 0) {
                                    int bestScore = SettingsManager.getInstance().getScore();
                                    gameScreen.setHasImprovedHighscore(viper.getScore() > bestScore);
                                    if (gameScreen.hasImprovedHighscore()) {
                                        VoraciousViper.getInstance().playSound("audio/sounds/applause.ogg");
                                        SettingsManager.getInstance().setScore(viper.getScore());
                                        SettingsManager.getInstance().setNumSteps(viper.getNumSteps());
                                        SettingsManager.getInstance().setLevel(indexCurrentLevelContinuous + 1); // index starts with 0
                                        HttpServer.submitHighscore(SettingsManager.getInstance().getPlayerId(),
                                                SettingsManager.getInstance().getPlayerName(),
                                                viper.getNumSteps(),
                                                SettingsManager.getInstance().getLevel(),
                                                viper.getScore(),
                                                VoraciousViper.getInstance().getVersionCode(),
                                                true);
                                    }
                                }
                                if (!gameScreen.hasImprovedHighscore()) {
                                    VoraciousViper.getInstance().playSound("audio/sounds/one_blow_from_party_horn.ogg");
                                }
                                break;
                            }
                        } else {  // proceed to next level
                            incrementCurrentLevel();
                        }
                        tilesAnimated.clear();
                        numConsumableTiles = 0;
                        gameScreen.setState(LevelTransition);
                    }
                    numTicks++;
                } else {
                    numTicks = 0;
                }
				break;
			default:
				break;
        }
    }

    private void renderColumnOfLevel() {
    	for (int y = 0; indexCurrentColumn < tiles.length && y < Config.NUM_TILES_COLUMN; y++) {
			tiles[indexCurrentColumn][y] = tilesDefault[indexCurrentLevel][indexCurrentColumn][y];
			if (tiles[indexCurrentColumn][y] instanceof TileAnimated) {
        		tilesAnimated.add((TileAnimated)tiles[indexCurrentColumn][y]);
        	}
            if (tiles[indexCurrentColumn][y] != null
            		&& (tiles[indexCurrentColumn][y].getType() == TileType.Consumable
            			|| tiles[indexCurrentColumn][y].getType() == TileType.Key)) {
                numConsumableTiles++;
            }
		}
    	indexCurrentColumn++;
	}

	private void animateTiles() {
        for (TileAnimated tileAnimated : tilesAnimated) {
        	tileAnimated.animate(animateInThisTick);
        }
	}

	public void render(SpriteBatch batch) {
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                if (tiles[x][y] != null) {
                    tiles[x][y].render(batch, x, y);
                }
            }
        }
    }
	
	private void setHadFirstLevelTransition(boolean hadFirstLevelTransition) {
		this.hadFirstLevelTransition = hadFirstLevelTransition;
	}
	
	public boolean hadFirstLevelTransition() {
		return hadFirstLevelTransition;
	}

	public void decrementCurrentLevel() {
		indexCurrentLevel = Math.max(0, indexCurrentLevel - 1);
	}

	public void incrementCurrentLevel() {
		indexCurrentLevel = (indexCurrentLevel + 1) % (tilesDefault.length);
		indexCurrentLevelContinuous++;
        viper.incrementLives();
	}

	public void reset() {
		gameScreen.setHasImprovedHighscore(false);
		clearTiles();
		hadFirstLevelTransition = false;
		indexCurrentLevel = 0;
		indexCurrentLevelContinuous = 0;
		numConsumableTiles = 0;
		viper.reset();
		gameScreen.setState(LevelTransition);
	}

	public void finishLevelTransition() {
		gameScreen.setNumFramesPerTick(1);
		for (int i = indexCurrentColumn; i < Config.NUM_TILES_ROW; i++) {
			renderColumnOfLevel();
		}
		gameScreen.setNumFramesPerTick(Config.NUM_FRAMES_FOR_TICK);
	}

	public void restartLevelTransition() {
		indexCurrentColumn = 0;
	}
	
}

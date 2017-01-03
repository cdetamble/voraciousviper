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

package net.bplaced.therefactory.voraciousviper.core.input.gamescreen;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.bplaced.therefactory.voraciousviper.core.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.core.constants.Config;
import net.bplaced.therefactory.voraciousviper.core.input.IRenderableInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.input.buttons.RetroTextButton;
import net.bplaced.therefactory.voraciousviper.core.screens.GameScreen;
import net.bplaced.therefactory.voraciousviper.core.screens.GameScreen.GameState;

/**
 * Encapsulates logic that is common to all types of input control.
 */
public abstract class AbstractGameScreenInputProcessor extends InputAdapter implements IRenderableInputProcessor {

    final GameScreen gameScreen;
	Viewport viewport;
	
	private RetroTextButton pauseButton;
	final Stage stage;
	int numTouches = 0;
	final Color buttonEdgeColor = new Color(.4f,.4f,.4f,.5f);
	final Color buttonTransparentColor = new Color(.2f, .2f, .2f, 1);
	
	public enum CompassDirection {
		Up, Right, Down, Left
	}
	
    AbstractGameScreenInputProcessor(final GameScreen gameScreen, Viewport viewport,
                                     TextureAtlas textureAtlas, ShapeRenderer shapeRenderer) {
        this.gameScreen = gameScreen;
        this.viewport = viewport;
        this.stage = gameScreen.getStage();
		gameScreen.setPreviousGameState(gameScreen.getState());

        Skin skin = new Skin();
        skin.addRegions(textureAtlas);
    	
        pauseButton = new RetroTextButton(shapeRenderer, Config.TILE_WIDTH*2, Config.TILE_HEIGHT*2, VoraciousViper.getInstance().getAmigaFont(), "X", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                pause();
            }
        });
        pauseButton.setPosition(viewport.getWorldWidth()-pauseButton.getWidth(), viewport.getWorldHeight()-pauseButton.getHeight());
        pauseButton.setThickness(3);
        
        stage.addActor(pauseButton);
    }
    
    private void pause() {
    	if (!pauseButton.isVisible()) return;
        if (gameScreen.getState() == GameState.ShowPauseMenuDialog) {
			gameScreen.setState(gameScreen.getPreviousGameState());
		} else {
			gameScreen.setPreviousGameState(gameScreen.getState());
			gameScreen.setState(GameState.ShowPauseMenuDialog);
		}
    }
	
    @Override
    public boolean keyDown(int keycode) {
        boolean returnValue = super.keyDown(keycode);
        if (keycode == Keys.BACK) {
            if (!gameScreen.getState().equals(GameState.ShowPauseMenuDialog)) {
            	pause();
                return true;
            }
        }
        return returnValue;
    }

    @Override
    public boolean keyTyped(char character) {
        super.keyTyped(character);
        if (!Config.DEBUG_MODE)
        	return false;
        if (character == '1') { // toggle paused state
            gameScreen.setState(gameScreen.getState() == GameScreen.GameState.GameIsBeginning ? GameScreen.GameState.GameIsRunning  : GameScreen.GameState.GameIsBeginning );
        }
        else if (character == '2') { // load previous level
        	gameScreen.getLevel().decrementCurrentLevel();
        	gameScreen.setState(GameState.LevelTransition);
        }
        else if (character == '3') { // load next level
        	gameScreen.getLevel().incrementCurrentLevel();
            gameScreen.setState(GameState.LevelTransition);
        }
        else if (character == 'w') { 
        	gameScreen.getViper().moveUp();
        }
        else if (character == 's') { 
        	gameScreen.getViper().moveDown();
        }
        else if (character == 'a') { 
        	gameScreen.getViper().moveLeft();
        }
        else if (character == 'd') { 
        	gameScreen.getViper().moveRight();
        }
        return false;
    }
    
    @Override
    public void render(ShapeRenderer shapeRenderer) {
    	// do nothing
    }
    
    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
    	if (gameScreen.getLevel().hadFirstLevelTransition()) {
    		stage.act();
    		pauseButton.setVisible(!gameScreen.getState().equals(GameState.ShowGameOverDialog));
    		if (pauseButton.isVisible()) {
    			pauseButton.draw(batch, 1f);
    		}
    	}
	}
    
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean ret = super.touchDown(screenX, screenY, pointer, button);
		numTouches++;
        if (gameScreen.getState() == GameState.GameIsBeginning) {
            gameScreen.setState(GameState.GameIsRunning);
        }
        if (gameScreen.getState() == GameState.LevelTransition) { // skip level transition by a single tap
        	gameScreen.getLevel().finishLevelTransition();
        }
        return ret;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		boolean ret = super.touchUp(screenX, screenY, pointer, button);
		numTouches--;
		return ret;
	}
}

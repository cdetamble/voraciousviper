package net.bplaced.therefactory.voraciousviper.core.input.gamescreen;

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
 * 
 * Created by Christian on 18.09.2016.
 */
public abstract class AbstractGameScreenInputProcessor extends InputAdapter implements IRenderableInputProcessor {

    final GameScreen gameScreen;
	Viewport viewport;
	
	private RetroTextButton abortButton;
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
    	
        abortButton = new RetroTextButton(shapeRenderer, Config.TILE_WIDTH*2, Config.TILE_HEIGHT*2, VoraciousViper.getInstance().getAmigaFont(), "X", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!abortButton.isVisible()) return;
                if (gameScreen.getState() == GameState.ShowPauseMenuDialog) {
	    			gameScreen.setState(gameScreen.getPreviousGameState());
	    		} else {
	    			gameScreen.setPreviousGameState(gameScreen.getState());
	    			gameScreen.setState(GameState.ShowPauseMenuDialog);
	    		}
            }
        });
        abortButton.setPosition(viewport.getWorldWidth()-abortButton.getWidth(), viewport.getWorldHeight()-abortButton.getHeight());
        abortButton.setThickness(3);
        
        stage.addActor(abortButton);
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
    		abortButton.setVisible(!gameScreen.getState().equals(GameState.ShowGameOverDialog));
    		if (abortButton.isVisible()) {
    			abortButton.draw(batch, 1f);
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

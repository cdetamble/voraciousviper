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

package net.bplaced.therefactory.voraciousviper.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.voraciousviper.core.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.core.constants.Config;
import net.bplaced.therefactory.voraciousviper.core.constants.I18NKeys;
import net.bplaced.therefactory.voraciousviper.core.constants.PrefsKeys;
import net.bplaced.therefactory.voraciousviper.core.input.IRenderableInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.input.buttons.RetroBundleTextButton;
import net.bplaced.therefactory.voraciousviper.core.input.gamescreen.JoystickInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.input.gamescreen.RelativeInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.input.gamescreen.SwipeInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.input.gamescreen.TouchAreasInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.input.gamescreen.TouchPadInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.misc.SettingsManager;
import net.bplaced.therefactory.voraciousviper.core.misc.Utils;
import net.bplaced.therefactory.voraciousviper.core.model.Level;
import net.bplaced.therefactory.voraciousviper.core.model.Viper;
import net.bplaced.therefactory.voraciousviper.core.ui.Hud;
import net.bplaced.therefactory.voraciousviper.core.ui.Particles;

public class GameScreen extends ScreenAdapter {

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private final Hud hud;
    private final Stage stage;
    private final Level level;
    private final Particles particles;
    private GameState currentGameState;
    private GameState previousGameState;

	private BitmapFont font;
	private final AssetManager assetManager;
    private final TextureAtlas textureAtlas;

	private final RetroBundleTextButton[] buttonsPauseDialog;
	private final RetroBundleTextButton[] buttonsGameOverDialog;
	private final RetroBundleTextButton[] buttonsContinueDialog;
	private final Rectangle rectangleMenuDialog;

	private final InputMultiplexer inputMultiplexer;
    private IRenderableInputProcessor inputProcessor;

    private boolean hasImprovedHighscore;
	private String currentControls;
    private int numFrames;
    private int numFramesPerTick = Config.NUM_FRAMES_FOR_TICK;

    public enum GameState {
        GameIsBeginning, /* viper is animated but does not move and game starts after the next touch */
        GameIsRunning,
        ShowGameOverDialog,
        ShowPauseMenuDialog,
        ShowContinueDialog,
        LevelTransition,
        ViperCrashed,
        LevelFinished
    }

    public GameScreen(SpriteBatch batch, ShapeRenderer shapeRenderer, FitViewport viewport, OrthographicCamera camera,
    		AssetManager assetManager, TextureAtlas textureAtlas, BitmapFont font) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.font = font;
        this.textureAtlas = textureAtlas;
        this.assetManager = assetManager;
        hud = new Hud(this, font, textureAtlas);
        level = Level.fromFile(this, Gdx.files.internal("levels/original.pack"), textureAtlas);
        hud.setLevel(level);
        this.viewport = viewport;
        this.camera = camera;
        currentGameState = GameState.LevelTransition;
        particles = new Particles(textureAtlas);

        rectangleMenuDialog = new Rectangle();
        rectangleMenuDialog.setSize(260, 160);
        rectangleMenuDialog.setPosition(viewport.getWorldWidth()/2-rectangleMenuDialog.getWidth()/2, 100);
        
        stage = new Stage();
		stage.setViewport(viewport);
		Skin skin = new Skin();
		skin.addRegions(textureAtlas);

		int buttonWidth = 120;
		int buttonHeight = 70;
		
		// pause menu dialog
		buttonsPauseDialog = new RetroBundleTextButton[] {
				new RetroBundleTextButton(shapeRenderer, buttonWidth, buttonHeight, VoraciousViper.getInstance().getAmigaFont(), I18NKeys.MainMenu, new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						VoraciousViper.getInstance().showTitleScreen();
					}
				}),
				new RetroBundleTextButton(shapeRenderer, buttonWidth, buttonHeight, VoraciousViper.getInstance().getAmigaFont(), I18NKeys.Restart, new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						level.reset();
					}
				}),
				new RetroBundleTextButton(shapeRenderer, buttonWidth, buttonHeight, VoraciousViper.getInstance().getAmigaFont(), I18NKeys.Cancel, new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						setState(previousGameState);
					}
				})
		};
		positionButtons(buttonsPauseDialog);

		// game over dialog
		buttonsGameOverDialog = new RetroBundleTextButton[] {
				new RetroBundleTextButton(shapeRenderer, buttonWidth, buttonHeight, VoraciousViper.getInstance().getAmigaFont(), I18NKeys.MainMenu, new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						VoraciousViper.getInstance().showTitleScreen();
					}
				}),
				new RetroBundleTextButton(shapeRenderer, buttonWidth, buttonHeight, VoraciousViper.getInstance().getAmigaFont(), I18NKeys.ScoreTable, new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						VoraciousViper.getInstance().showScoreTable();
					}
				}),
				new RetroBundleTextButton(shapeRenderer, buttonWidth, buttonHeight, VoraciousViper.getInstance().getAmigaFont(), I18NKeys.PlayAgain, new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						level.reset();
					}
				})
		};
		positionButtons(buttonsGameOverDialog);

		// continue dialog
		buttonsContinueDialog = new RetroBundleTextButton[] {
				new RetroBundleTextButton(shapeRenderer, buttonWidth, buttonHeight, VoraciousViper.getInstance().getAmigaFont(), I18NKeys.ResetText, new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						setState(GameState.ViperCrashed);
					}
				}),
				new RetroBundleTextButton(shapeRenderer, buttonWidth, buttonHeight, VoraciousViper.getInstance().getAmigaFont(), I18NKeys.Continue, new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
                        getLevel().removeViper();
                        getViper().decrementLives();
                        getViper().setHasCrashed(false);
                        getViper().restart(false);
                        setState(GameState.GameIsBeginning);
					}
				})
		};
		positionButtons(buttonsContinueDialog);

		inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(0, stage);
        updateGameScreenControls();
    }

	private void positionButtons(RetroBundleTextButton[] buttons) {
    	float x = rectangleMenuDialog.getX() + 20;
		for (RetroBundleTextButton button : buttons) {
			button.setPosition(x, rectangleMenuDialog.getY() + 20);
			button.setThickness(3);
			stage.addActor(button);
			x += button.getWidth() + 10;
		}
	}

	private void update() {
    	assetManager.update(); // load and unload assets from the manager's queue
        numFrames++;
        inputProcessor.update();
        level.update();

        if (numFrames >= numFramesPerTick) {
            numFrames = 0;
            level.tick(currentGameState);
        }

        if (currentGameState.equals(GameState.ShowPauseMenuDialog)) {
        	stage.act();
        }

        updateDialog(GameState.ShowPauseMenuDialog, buttonsPauseDialog);
        updateDialog(GameState.ShowGameOverDialog, buttonsGameOverDialog);
        updateDialog(GameState.ShowContinueDialog, buttonsContinueDialog);
    }

    private void updateDialog(GameState state, RetroBundleTextButton[] buttons) {
        int width = 30;
        for (RetroBundleTextButton button : buttons) {
            button.setVisible(currentGameState.equals(state));
            width += button.getWidth() + 10;
        }
        if (currentGameState.equals(state)) {
            rectangleMenuDialog.setWidth(width);
            rectangleMenuDialog.setPosition(viewport.getWorldWidth()/2-rectangleMenuDialog.getWidth()/2, 100);
            positionButtons(buttons);
        }
    }

    @Override
    public void render(float delta) {
    	super.render(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        
        update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        if (level.hadFirstLevelTransition()) {
        	inputProcessor.render(shapeRenderer);
        }
        shapeRenderer.end();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        level.render(batch);
        hud.render(batch);

        // fade background if dialog is shown
        if (currentGameState.equals(GameState.ShowPauseMenuDialog) || currentGameState.equals(GameState.ShowGameOverDialog) || currentGameState.equals(GameState.ShowContinueDialog)) {
        	VoraciousViper.getInstance().getFadeSprite().setAlpha(.5f);
        	VoraciousViper.getInstance().getFadeSprite().draw(batch);
        }

        // fireworks effect if highscore has been improved
        if (hasImprovedHighscore) {
            particles.renderFireworks(batch, delta);
        }

        inputProcessor.render(batch, font);
        batch.end();
        
        // render dialog box
        if (currentGameState.equals(GameState.ShowPauseMenuDialog) || currentGameState.equals(GameState.ShowGameOverDialog) || currentGameState.equals(GameState.ShowContinueDialog)) {
            shapeRenderer.begin(ShapeType.Filled);
            
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(rectangleMenuDialog.x, rectangleMenuDialog.y, rectangleMenuDialog.width, rectangleMenuDialog.height);
            
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(rectangleMenuDialog.x, rectangleMenuDialog.y + rectangleMenuDialog.height - 25, rectangleMenuDialog.width, 25);
            shapeRenderer.end();
            
            batch.begin();
            font = VoraciousViper.getInstance().getAmigaFont();
            
            font.setColor(Color.WHITE);
            font.draw(batch, Config.GAME_TITLE,
            		rectangleMenuDialog.x + rectangleMenuDialog.width/2 - Utils.getFontWidth(Config.GAME_TITLE, font)/2,
            		rectangleMenuDialog.y + rectangleMenuDialog.height - 25/2 + Utils.getFontHeight(Config.GAME_TITLE, font)/2);
            
            font.setColor(Color.BLACK);

            String phrase;
            if (currentGameState.equals(GameState.ShowPauseMenuDialog)) phrase = VoraciousViper.getInstance().getBundle().get(I18NKeys.GivinUpAlready);
            else if (currentGameState.equals(GameState.ShowContinueDialog)) phrase = VoraciousViper.getInstance().getBundle().get(I18NKeys.ContinueText);
            else if (hasImprovedHighscore) phrase = VoraciousViper.getInstance().getBundle().get(I18NKeys.HighscoreImprovedText);
            else phrase = VoraciousViper.getInstance().getBundle().get(I18NKeys.GameOverText);
            font.draw(batch, phrase,
            		rectangleMenuDialog.x + rectangleMenuDialog.width/2 - Utils.getFontWidth(phrase, font)/2,
            		rectangleMenuDialog.y + rectangleMenuDialog.height - 45);
  
            batch.end();
        	stage.draw();
        }
    }

    @Override
    public void show() {
    	super.show();
		if (currentGameState.equals(GameState.LevelTransition)) { // restart because the level transition may be unfinished (player left game while level was transitioning)
			level.restartLevelTransition();
		}
    	hud.show();
        camera.position.set(Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT / 2, 0);
        camera.update();
        if (!currentControls.equals(SettingsManager.getInstance().getControls())) { // control setting has been changed
        	updateGameScreenControls();
        }
        Gdx.input.setInputProcessor(inputMultiplexer);
        Gdx.input.setCatchBackKey(true);
    }
    
    private void updateGameScreenControls() {
    	if (inputProcessor != null)
    		inputProcessor.dispose();
    	inputMultiplexer.removeProcessor(inputProcessor);
        currentControls = SettingsManager.getInstance().getControls();
        
        if (currentControls.equals(PrefsKeys.ControlsRelative)) {
            inputProcessor = new RelativeInputProcessor(this, viewport, textureAtlas, shapeRenderer, font);
        }
        else if (currentControls.equals(PrefsKeys.ControlsSwipe)) {
        	inputProcessor = new SwipeInputProcessor(this, viewport, textureAtlas, shapeRenderer, font);
        }
        else if (currentControls.equals(PrefsKeys.ControlsTouchPad)) {
        	inputProcessor = new TouchPadInputProcessor(this, viewport, textureAtlas, shapeRenderer, font);
        }
        else if (currentControls.equals(PrefsKeys.ControlsJoystick)) {
        	inputProcessor = new JoystickInputProcessor(this, viewport, textureAtlas, shapeRenderer, font);
        }
        else  {
        	inputProcessor = new TouchAreasInputProcessor(this, viewport, textureAtlas, shapeRenderer, font);
        }
        
        if (inputMultiplexer.getProcessors().contains(stage, true)) {
        	inputMultiplexer.addProcessor(1, inputProcessor);
        } else {
        	inputMultiplexer.addProcessor(inputProcessor);
        }
    }
    
    @Override
    public void pause() {
    	super.pause();
    	if (currentGameState == GameState.GameIsRunning)
    		setState(GameState.GameIsBeginning);
    }

    @Override
    public void resize(int width, int height) {
    	super.resize(width, height);
        viewport.update(width, height);
    }

    public void setState(GameState gameState) {
        this.currentGameState = gameState;
        if (!level.hadFirstLevelTransition() && gameState == GameState.LevelTransition) {
        	inputMultiplexer.getProcessors().removeValue(stage, true);
        } else if (!inputMultiplexer.getProcessors().contains(stage, false)) {
        	inputMultiplexer.getProcessors().insert(0, stage);
        }
        if (gameState.equals(GameState.LevelTransition)) {
			setNumFramesPerTick(Config.NUM_FRAMES_DURING_LEVEL_TRANSITION);
        }
    }

    public GameState getState() {
        return currentGameState;
    }

	public Viper getViper() {
		return level.getViper();
	}
	
	public void setNumFramesPerTick(int numFramesPerTick) {
		this.numFramesPerTick = numFramesPerTick;
	}

	public Level getLevel() {
		return level;
	}
	
	public Stage getStage() {
		return stage;
	}

	public void setHasImprovedHighscore(boolean hasImprovedHighscore) {
		this.hasImprovedHighscore = hasImprovedHighscore;
	}

	public boolean hasImprovedHighscore() {
		return hasImprovedHighscore;
	}

	public void setPreviousGameState(GameState state) {
		this.previousGameState = state;
	}

	public GameState getPreviousGameState() {
		return previousGameState;
	}

    @Override
    public void dispose() {
        super.dispose();
        if (particles != null)
            particles.dispose();
    }
}

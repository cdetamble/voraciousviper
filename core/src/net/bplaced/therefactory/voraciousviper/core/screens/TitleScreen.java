package net.bplaced.therefactory.voraciousviper.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.bplaced.therefactory.voraciousviper.core.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.core.constants.Config;
import net.bplaced.therefactory.voraciousviper.core.constants.I18NKeys;
import net.bplaced.therefactory.voraciousviper.core.constants.PrefsKeys;
import net.bplaced.therefactory.voraciousviper.core.input.KeyboardInputListener;
import net.bplaced.therefactory.voraciousviper.core.input.ScoreTableInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.input.buttons.AbstractRetroButton;
import net.bplaced.therefactory.voraciousviper.core.input.buttons.RetroBundleTextButton;
import net.bplaced.therefactory.voraciousviper.core.input.buttons.RetroImageButton;
import net.bplaced.therefactory.voraciousviper.core.input.buttons.RetroImageToggleButton;
import net.bplaced.therefactory.voraciousviper.core.input.buttons.RetroTextButton;
import net.bplaced.therefactory.voraciousviper.core.input.buttons.RetroTextButton.TextAlign;
import net.bplaced.therefactory.voraciousviper.core.misc.Scaler;
import net.bplaced.therefactory.voraciousviper.core.misc.ScoreEntry;
import net.bplaced.therefactory.voraciousviper.core.misc.SettingsManager;
import net.bplaced.therefactory.voraciousviper.core.misc.Utils;
import net.bplaced.therefactory.voraciousviper.core.net.HttpServer;

import java.util.Locale;

public class TitleScreen extends ScreenAdapter {

    private TitleScreenState state;
	private enum TitleScreenState {
		ShowMainMenu, ShowSettings, ShowAbout, ShowBadge, ShowScoreTable
	}

	private final Stage stage;
	private FitViewport viewport;
	private final OrthographicCamera camera;
	private final ShapeRenderer shapeRenderer;

	private final RetroTextButton buttonSwitchBetweenAboutAndSettings;
	private final RetroTextButton buttonClose;
	private final AbstractRetroButton[][] buttonsSettings;
	private final RetroBundleTextButton[] buttonsMainMenu;

	private final Rectangle rectangleTitle;
	private final Rectangle rectangleTitleScoreTable;
	
	private static final Color colorViperBlue = new Color(0, 0, 170f/255f, 1); // blue
	private static final Color colorViperPurple = new Color(170f/255f, 0, 1, 1); // purple
	private final Color colorScaler;

	private final Sprite spriteInfo;
	private final Sprite[] spritesViperHeads;
	private final Sprite spriteSettings;
	private final Sprite spriteTitle;
    private Sprite spriteBadge;
	private Sprite spriteThere;
	private Sprite spriteFactory;
	
	// score table
	private ScoreEntry[] scoreEntries;
	private final StringBuilder stringBuilder;
	private CharSequence customHighscoreText = "";
	private final ScoreTableInputProcessor inputHandler;
	private final InputMultiplexer inputMultiplexer;
	private final RetroTextButton scrollbarHandle;
	private boolean fetching;
	private boolean updating;
	private float scrollbarCurrentY;
	private float scrollbarMaximumY;
	
    // other
    private final Preferences prefs;
    private final AssetManager assetManager;
	private final Scaler scaler;
	private final BitmapFont font;
	private final KeyboardInputListener keyboardInputListener;
	private final TitleScreen instance;

    // temporary variables
	private float xOffset;
	private final float xOffsetMax;
	private final int paddingFromViewport;
	private int vSpaceBetweenButtons;
	private int indexCurrentHeadSprite;

	public TitleScreen(ShapeRenderer shapeRenderer, final FitViewport viewport,
                       OrthographicCamera camera, AssetManager assetManager, TextureAtlas textureAtlas, BitmapFont font) {
		this.instance = this;
		this.shapeRenderer = shapeRenderer;
		this.camera = camera;
		this.assetManager = assetManager;
		this.font = font;
		this.viewport = viewport;
		this.prefs = SettingsManager.getInstance().get();
		this.keyboardInputListener = new KeyboardInputListener(this);
		this.state = TitleScreenState.ShowMainMenu;
		this.stringBuilder = new StringBuilder();
		
		if (SettingsManager.getInstance().isMusicEnabled())
			VoraciousViper.getInstance().playMusicFile(true);

		paddingFromViewport = 10;
		int hSpaceBetweenButtons = 10;
		vSpaceBetweenButtons = 20;

		camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
		camera.update();

        if (Config.SHOW_BADGE) { spriteBadge = new Sprite(new Texture("google-play-badge.png")); }
		spriteTitle = new Sprite(new Texture("title.png"));
		rectangleTitle = new Rectangle(viewport.getWorldWidth() / 2 - spriteTitle.getWidth() / 2+xOffset, 200, spriteTitle.getWidth(), spriteTitle.getHeight());
		rectangleTitleScoreTable = new Rectangle(8, viewport.getWorldHeight() - 40, rectangleTitle.width/2, rectangleTitle.height/2);
		spriteTitle.setBounds(rectangleTitle.x, rectangleTitle.y, rectangleTitle.width, rectangleTitle.height);
		
		spriteInfo = textureAtlas.createSprite("Info");
		spriteSettings = textureAtlas.createSprite("settings");
		spriteThere = new Sprite(new Texture("there.png"));
		spriteFactory = new Sprite(new Texture("factory.png"));
		
		indexCurrentHeadSprite = 0;
		spritesViperHeads = new Sprite[6];
		spritesViperHeads[0] = textureAtlas.createSprite("Head.X.1.Green");
		spritesViperHeads[1] = textureAtlas.createSprite("Head.X.2.Green");
		spritesViperHeads[2] = textureAtlas.createSprite("Head.X.1.Purple");
		spritesViperHeads[3] = textureAtlas.createSprite("Head.X.2.Purple");
		spritesViperHeads[4] = textureAtlas.createSprite("Head.X.1.Blue");
		spritesViperHeads[5] = textureAtlas.createSprite("Head.X.2.Blue");
		
		stage = new Stage();
		stage.setViewport(this.viewport);
        Skin skin = new Skin();
		skin.addRegions(textureAtlas);

		// button to close settings
		buttonClose = new RetroTextButton(shapeRenderer, 52, 52, font, "X", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				setState(TitleScreenState.ShowMainMenu);
			}
		});
		buttonClose.setPosition(viewport.getWorldWidth()-buttonClose.getWidth()-vSpaceBetweenButtons,
				viewport.getWorldHeight()-buttonClose.getHeight()-vSpaceBetweenButtons);
		buttonClose.setThickness(3);
		stage.addActor(buttonClose);

		// button to show about screen
		buttonSwitchBetweenAboutAndSettings = new RetroTextButton(shapeRenderer, 52, 52, font, "?", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				if (state.equals(TitleScreenState.ShowSettings)) { // toggle about screen
					setState(TitleScreenState.ShowAbout);
				} else {
					setState(TitleScreenState.ShowSettings);					
				}
			}
		});
		buttonSwitchBetweenAboutAndSettings.setPosition(buttonClose.getX() - buttonSwitchBetweenAboutAndSettings.getWidth() - hSpaceBetweenButtons, buttonClose.getY());
		stage.addActor(buttonSwitchBetweenAboutAndSettings);

		// initialize buttons for main menu
		int buttonSize = 130;
		buttonsMainMenu = new RetroBundleTextButton[] { new RetroBundleTextButton(shapeRenderer, buttonSize, 100, font,
				I18NKeys.Singleplayer, new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				VoraciousViper.getInstance().startGame();
			}
		}), new RetroBundleTextButton(shapeRenderer, buttonSize, 100, font, I18NKeys.ScoreTable,
				new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				showScoreTable();
			}
		}),
			new RetroBundleTextButton(shapeRenderer, buttonSize, 100, font, I18NKeys.Settings, new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					setState(TitleScreenState.ShowSettings);
				}
			}),
			new RetroBundleTextButton(shapeRenderer, buttonSize, 100, font,
					 I18NKeys.About, new ClickListener() {
				 @Override
				 public void touchUp(InputEvent event, float x, float y, int
				 pointer, int button) {
					 super.touchUp(event, x, y, pointer, button);
                     if (Config.SHOW_BADGE) {
                         setState(TitleScreenState.ShowBadge);
                     } else {
                         setState(TitleScreenState.ShowAbout);
                     }
				 }
			 })
		};
		for (RetroTextButton textButton : buttonsMainMenu) {
			textButton.setPosition(paddingFromViewport, paddingFromViewport);
			textButton.setAlignment(TextAlign.CenterBottom);
			stage.addActor(textButton);
		}

		buttonSize = 52;

		// initialize buttons for settings menu
		buttonsSettings = new AbstractRetroButton[][] {
			new RetroImageButton[] {
					new RetroImageButton(shapeRenderer, buttonSize, buttonSize, skin.getDrawable("flag_usa"),
							PrefsKeys.Language, Locale.ENGLISH.toString()),
					new RetroImageButton(shapeRenderer, buttonSize, buttonSize, skin.getDrawable("flag_germany"),
							PrefsKeys.Language, Locale.GERMAN.toString()) },
			new RetroImageToggleButton[] {
					new RetroImageToggleButton(shapeRenderer, buttonSize, buttonSize, skin.getDrawable("music.on"),
							skin.getDrawable("music.off"), PrefsKeys.Music, true),
					new RetroImageToggleButton(shapeRenderer, buttonSize, buttonSize, skin.getDrawable("sound.on"),
							skin.getDrawable("sound.off"), PrefsKeys.Sound, true) },
			new RetroTextButton[] { new RetroTextButton(shapeRenderer, buttonSize, buttonSize, font,
					I18NKeys.Playername, new ClickListener() {
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					super.touchUp(event, x, y, pointer, button);
					Gdx.input.getTextInput(keyboardInputListener, VoraciousViper.getInstance().getBundle().get(I18NKeys.PleaseEnterYourPlayername),
							SettingsManager.getInstance().get().getString(PrefsKeys.PlayerName), "");
				}
			})},
			new RetroImageButton[] { new RetroImageButton(shapeRenderer, buttonSize, buttonSize,
					skin.getDrawable("controls_touchareas"), PrefsKeys.Controls, PrefsKeys.ControlsTouchAreas),
					new RetroImageButton(shapeRenderer, buttonSize, buttonSize,
							skin.getDrawable("controls_touchpad"), PrefsKeys.Controls, PrefsKeys.ControlsTouchPad),
					new RetroImageButton(shapeRenderer, buttonSize, buttonSize,
							skin.getDrawable("controls_relative"), PrefsKeys.Controls, PrefsKeys.ControlsRelative),
//					new RetroImageButton(shapeRenderer, buttonSize, buttonSize, skin.getDrawable("controls_swipe"),
//							PrefsKeys.Controls, PrefsKeys.ControlsSwipe),
//					new RetroImageButton(shapeRenderer, buttonSize, buttonSize, skin.getDrawable("controls_joystick"),
//							PrefsKeys.Controls, PrefsKeys.ControlsJoystick)
			},
		};

		// listeners to change locale in settings manager
		ClickListener clickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				VoraciousViper.getInstance().setLocale(prefs.getString(PrefsKeys.Language));
				updatePlayernameText();
			}
		};
		buttonsSettings[0][0].addListener(clickListener);
		buttonsSettings[0][1].addListener(clickListener);
		((RetroTextButton) buttonsSettings[2][0]).setAutoSize(true);
        buttonsSettings[1][0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (SettingsManager.getInstance().isMusicEnabled()) {
                    VoraciousViper.getInstance().playMusicFile(true);
                } else {
                    VoraciousViper.getInstance().pauseMusic();
                }
            }
        });

		// equally distribute settings buttons based on the defined buttonSize
		for (int i = 0; i < buttonsSettings.length; i++) {
			for (int j = 0; j < buttonsSettings[i].length; j++) {
				buttonsSettings[i][j].setPosition(vSpaceBetweenButtons + j * (buttonSize + hSpaceBetweenButtons),
						vSpaceBetweenButtons * 2 + (buttonsSettings.length - 1 - i) * (vSpaceBetweenButtons + buttonSize));
				int pointer = 0;
				if (i == 0 || i == 3) {
					AbstractRetroButton[] adjacentButtons = new AbstractRetroButton[buttonsSettings[i].length - 1];
					for (int k = 0; k < buttonsSettings[i].length; k++) {
						if (k != j) {
							adjacentButtons[pointer++] = buttonsSettings[i][k];
						}
					}
					((RetroImageButton) buttonsSettings[i][j]).setAdjacentButtons(adjacentButtons);
				}
				stage.addActor(buttonsSettings[i][j]);
			}
		}
		scaler = new Scaler(0, 1, .003f, true);
		colorScaler = new Color();
		xOffsetMax = 80;
		
		scrollbarMaximumY = viewport.getWorldHeight() - buttonClose.getHeight() + 1;
		scrollbarCurrentY = scrollbarMaximumY;
		
		inputMultiplexer = new InputMultiplexer();
		inputHandler = new ScoreTableInputProcessor(this, scrollbarMaximumY, 0);  
		inputMultiplexer.addProcessor(inputHandler);
		inputMultiplexer.addProcessor(stage);
		scrollbarHandle = new RetroTextButton(shapeRenderer, 52, 25, font, "", new ClickListener() {

		});
		scrollbarHandle.setX(viewport.getWorldWidth() - scrollbarHandle.getWidth());
		scrollbarHandle.setThickness(3);
		stage.addActor(scrollbarHandle);
	}

	public void setUpdating(boolean updating) {
		this.updating = updating;
	}

	public void showScoreTable() {
		customHighscoreText = "";
		fetching = true;
		if (scoreEntries == null) {
			scoreEntries = SettingsManager.getInstance().getScoreEntries();
		}
		Utils.async(new Runnable() {
			@Override
			public void run() {
				HttpServer.fetchHighscores(instance,
						SettingsManager.getInstance().getPlayerId(),
						SettingsManager.getInstance().getPlayerName(),
						SettingsManager.getInstance().getNumSteps(),
						SettingsManager.getInstance().getLevel(),
						SettingsManager.getInstance().getScore(),
						VoraciousViper.getInstance().getVersionCode(), false);
			}
		});
		setState(TitleScreenState.ShowScoreTable);
	}

	private void setState(TitleScreenState state) {
		this.state = state;
        if (state.equals(TitleScreenState.ShowSettings)) {
            synchronizeCheckedStatesOfSettingsButtons();
        }
	}
	
	private void update(float delta) {
		inputHandler.update();
		assetManager.update();
		
		// offset sprites to the right in settings screen
		if (state.equals(TitleScreenState.ShowSettings)) {
			if (xOffset < xOffsetMax) { // increase xOffset
				xOffset = Math.min(xOffsetMax, xOffset + (int)((xOffsetMax - xOffset)/8));
			}
		} else {
			if (xOffset > 0) {  // decrease xOffset
				xOffset = Math.max(0, xOffset - (int)(xOffset/8));
			}
		}
        float xOffsetQuotient = xOffset / xOffsetMax;

		// color transition for viper's eyes
		scaler.update();
		stage.act(delta);
		
		float red = Math.abs((scaler.get() * colorViperBlue.r) + ((1 - scaler.get()) * colorViperPurple.r));
		float green = Math.abs((scaler.get() * colorViperBlue.g) + ((1 - scaler.get()) * colorViperPurple.g));
		float blue = Math.abs((scaler.get() * colorViperBlue.b) + ((1 - scaler.get()) * colorViperPurple.b));

		colorScaler.set(red, green, blue, 1f);
		
		buttonClose.setVisible(!state.equals(TitleScreenState.ShowMainMenu));
		buttonSwitchBetweenAboutAndSettings.setVisible(!state.equals(TitleScreenState.ShowMainMenu) && !state.equals(TitleScreenState.ShowScoreTable));

		// buttons in main menu
		int hSpace = 15;
		float x = (viewport.getWorldWidth() - buttonsMainMenu.length * buttonsMainMenu[0].getWidth()
				- ((buttonsMainMenu.length - 1) * hSpace)) / 2;
        for (RetroBundleTextButton aButtonsMainMenu : buttonsMainMenu) {
            aButtonsMainMenu.setVisible(state.equals(TitleScreenState.ShowMainMenu));
            aButtonsMainMenu.setPosition(x + xOffsetQuotient * 100, 70);
            aButtonsMainMenu.setAlpha(1 - xOffsetQuotient);
            x += aButtonsMainMenu.getWidth() + hSpace;
        }

		// buttons in settings menu
		x = paddingFromViewport;
		for (AbstractRetroButton[] buttons : buttonsSettings) {
			for (AbstractRetroButton button : buttons) {
				button.setX(x + (xOffsetQuotient * 100) - 90);
				button.setVisible(state.equals(TitleScreenState.ShowSettings));
				button.setAlpha(xOffsetQuotient);
				x += button.getWidth() + hSpace;
			}
			x = paddingFromViewport;
		}
		
		// animate shrinking of title sprite when going into / out from the score table
		float time = 15;
		if (state.equals(TitleScreenState.ShowScoreTable)) {
			spriteTitle.setBounds(
					Math.max(rectangleTitleScoreTable.x, spriteTitle.getX() - Math.abs(rectangleTitle.x - rectangleTitleScoreTable.x)/time),
					Math.min(rectangleTitleScoreTable.y, spriteTitle.getY() + Math.abs(rectangleTitleScoreTable.y - rectangleTitle.y)/time),
					Math.max(rectangleTitleScoreTable.width, spriteTitle.getWidth() - Math.abs(rectangleTitleScoreTable.width - rectangleTitle.width)/time),
					Math.max(rectangleTitleScoreTable.height, spriteTitle.getHeight() - Math.abs(rectangleTitleScoreTable.height - rectangleTitle.height)/time)
			);
			for (Sprite spriteHead : spritesViperHeads) {
				spriteHead.setPosition(spriteTitle.getX() + spriteTitle.getWidth() * .376f, spriteTitle.getY() + spriteTitle.getHeight()*.77f);
				spriteHead.setSize(Math.max(24/2, spriteHead.getWidth() - 12/time), Math.max(18/2, spriteHead.getHeight() - 9/time));
			}
			buttonClose.setPosition(viewport.getWorldWidth()-buttonClose.getWidth(),
					viewport.getWorldHeight()-buttonClose.getHeight());
		} else {
			spriteTitle.setBounds(
					Math.min(rectangleTitle.x + xOffset, spriteTitle.getX() + xOffset + Math.abs(rectangleTitle.x - rectangleTitleScoreTable.x)/time),
					Math.max(rectangleTitle.y, spriteTitle.getY() - Math.abs(rectangleTitleScoreTable.y - rectangleTitle.y)/time),
					Math.min(rectangleTitle.width, spriteTitle.getWidth() + Math.abs(rectangleTitleScoreTable.width - rectangleTitle.width)/time),
					Math.min(rectangleTitle.height, spriteTitle.getHeight() + Math.abs(rectangleTitleScoreTable.height - rectangleTitle.height)/time)
			);
			for (Sprite spriteHead : spritesViperHeads) {
				spriteHead.setPosition(spriteTitle.getX() + spriteTitle.getWidth() * .376f, spriteTitle.getY() + spriteTitle.getHeight()*.77f);
				spriteHead.setSize(Math.min(24, spriteHead.getWidth() + 12/time), Math.min(18, spriteHead.getHeight() + 9/time));
			}
			buttonClose.setPosition(viewport.getWorldWidth()-buttonClose.getWidth()-vSpaceBetweenButtons,
					viewport.getWorldHeight()-buttonClose.getHeight()-vSpaceBetweenButtons);
		}
		scrollbarHandle.setY(Math.min(scrollbarMaximumY-buttonClose.getHeight()/2, scrollbarCurrentY)); // limit top line
		scrollbarHandle.setVisible(state.equals(TitleScreenState.ShowScoreTable) && !fetching && scoreEntries != null);
	
		 // animate the viper head that represents the dot on the 'i' of Vorac_i_ous in the title image
		if (indexCurrentHeadSprite % 2 == 0 && System.currentTimeMillis() % 1000 < 500) {
			indexCurrentHeadSprite++;
		} else if (indexCurrentHeadSprite % 2 == 1 && System.currentTimeMillis() % 1000 > 500) {
			indexCurrentHeadSprite--;
		}
		if (Gdx.input.justTouched()) {
			Vector2 touchCoordinates = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
			if (Utils.within(touchCoordinates, spritesViperHeads[0])) {
				VoraciousViper.getInstance().playSound("audio/sounds/lose.ogg");
				indexCurrentHeadSprite = (indexCurrentHeadSprite + ((indexCurrentHeadSprite % 2 == 0) ? 2 : 1)) % (spritesViperHeads.length);
			}
		}
	}

    @Override
	public void render(float delta) {
		super.render(delta);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		update(delta);
		
		// draw gradient background
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight(), Color.BLACK, Color.BLACK, colorScaler, colorScaler);
		shapeRenderer.end();
		
		stage.getBatch().setProjectionMatrix(camera.combined);
		stage.getBatch().begin();

		if (state.equals(TitleScreenState.ShowSettings)) { // button descriptions
			font.setColor(Color.WHITE);
			font.draw(stage.getBatch(), VoraciousViper.getInstance().getBundle().get(I18NKeys.ControlsBy) + " "
					+ VoraciousViper.getInstance().getBundle().get(prefs.getString(PrefsKeys.Controls)),
					buttonsSettings[0][0].getX(), 25);
		}
        else if (state.equals(TitleScreenState.ShowAbout)) { // about text
			font.setColor(1, 1, 1, 1 - xOffset/xOffsetMax);
			float xOffsetAboutText = paddingFromViewport + xOffset + (xOffset/xOffsetMax) * 50;
			font.draw(stage.getBatch(), VoraciousViper.getInstance().getBundle().format(I18NKeys.AboutText, VoraciousViper.getInstance().getVersionName()),
					xOffsetAboutText, 180);
			
			spriteThere.setAlpha(1 - xOffset/xOffsetMax);
			spriteThere.setScale(.4f);
			spriteThere.setPosition(xOffsetAboutText+81, 105);
			spriteThere.draw(stage.getBatch());
			
			spriteFactory.setAlpha(1 - xOffset/xOffsetMax);
			spriteFactory.setScale(.4f);
			spriteFactory.setPosition(xOffsetAboutText+125, spriteThere.getY());
			spriteFactory.draw(stage.getBatch());
		}
		
		if (!state.equals(TitleScreenState.ShowScoreTable)) {
		font.setColor(Color.GRAY);
			if (state.equals(TitleScreenState.ShowMainMenu)) {
				font.draw(stage.getBatch(), VoraciousViper.getInstance().getBundle().get(I18NKeys.CopyrightText), 150, 40);
			}
			font.draw(stage.getBatch(),
					VoraciousViper.getInstance().getBundle().get(I18NKeys.Version) +
							" " + VoraciousViper.getInstance().getVersionName(),
							spriteTitle.getX() + spriteTitle.getWidth() - 98,
							spriteTitle.getY() + 5);
		}

        if (Config.SHOW_BADGE) {
        	spriteThere.setScale(.5f, .5f);
        	spriteThere.setPosition(335, 105);
        	spriteThere.draw(stage.getBatch());
        	
        	spriteFactory.setScale(.5f, .5f);
        	spriteFactory.setPosition(spriteThere.getX() + 65, spriteThere.getY());
        	spriteFactory.draw(stage.getBatch());
        	
        	spriteBadge.setScale(.5f, .5f);
        	spriteBadge.setPosition(-100, 40);
            spriteBadge.draw(stage.getBatch());
        } else {
            stage.getBatch().end();
            stage.draw();
            stage.getBatch().begin();
    		if (state.equals(TitleScreenState.ShowMainMenu)) {
    			renderMainMenuButtons(); // draw image captions on main menu buttons
    		}
    		else if (state.equals(TitleScreenState.ShowScoreTable)) {
    			renderScoreTable();
    		}
        }
        
		spriteTitle.draw(stage.getBatch());
		spritesViperHeads[indexCurrentHeadSprite].draw(stage.getBatch()); // dot on the 'i' of Vorac_i_ous in the title image
		
		if (updating) {
			VoraciousViper.getInstance().getFadeSprite().setBounds(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
			VoraciousViper.getInstance().getFadeSprite().setAlpha(.8f);
			VoraciousViper.getInstance().getFadeSprite().draw(stage.getBatch());
			font.setColor(Color.WHITE);
			String updating = VoraciousViper.getInstance().getBundle().get(I18NKeys.Updating) + "...";
			font.draw(stage.getBatch(), updating,
					viewport.getWorldWidth()/2-Utils.getFontWidth(updating, font)/2,
					viewport.getWorldHeight()/2-Utils.getFontHeight(updating, font)/2);
		}
		
		stage.getBatch().end();
	}

	private void renderMainMenuButtons() {
		stage.getBatch().draw(spritesViperHeads[1],
				buttonsMainMenu[0].getX() + buttonsMainMenu[0].getWidth() / 2 - 12,
				buttonsMainMenu[0].getY() + buttonsMainMenu[0].getHeight() / 2
				- (buttonsMainMenu[0].isPressed() ? 2 : 0));

		int diff = 7;
		stage.getBatch().draw(spritesViperHeads[5],
				buttonsMainMenu[1].getX() + buttonsMainMenu[1].getWidth() / 2 - 12
				+ diff * 2,
				buttonsMainMenu[1].getY() + buttonsMainMenu[0].getHeight() / 2 + diff
				- (buttonsMainMenu[1].isPressed() ? 2 : 0));
		
		stage.getBatch().draw(spritesViperHeads[3],
				buttonsMainMenu[1].getX() + buttonsMainMenu[1].getWidth() / 2 - 12,
				buttonsMainMenu[1].getY() + buttonsMainMenu[0].getHeight() / 2
				- (buttonsMainMenu[1].isPressed() ? 2 : 0));
		
		stage.getBatch().draw(spritesViperHeads[1],
				buttonsMainMenu[1].getX() + buttonsMainMenu[1].getWidth() / 2 - 12
				- diff * 2,
				buttonsMainMenu[1].getY() + buttonsMainMenu[0].getHeight() / 2 - diff
				- (buttonsMainMenu[1].isPressed() ? 2 : 0));

		stage.getBatch().draw(spriteSettings,
				buttonsMainMenu[2].getX() + buttonsMainMenu[2].getWidth() / 2 - 17,
				buttonsMainMenu[2].getY() + 42
				- (buttonsMainMenu[2].isPressed() ? 2 : 0));
		
		stage.getBatch().draw(spriteInfo,
				buttonsMainMenu[3].getX() + buttonsMainMenu[3].getWidth() / 2 - spriteSettings.getWidth() / 2,
				buttonsMainMenu[3].getY() + buttonsMainMenu[3].getHeight() / 2 - spriteSettings.getHeight() / 4
				- (buttonsMainMenu[3].isPressed() ? 2 : 0));
	}

	private void renderScoreTable() {
		float xOffsetScoreTable = (spriteTitle.getX() - rectangleTitleScoreTable.x);
		
		font.setColor(Color.YELLOW);
		font.draw(stage.getBatch(), VoraciousViper.getInstance().getBundle().get(I18NKeys.ViviTopScoreTable),
				xOffsetScoreTable + 240, viewport.getWorldHeight()-20);

		font.setColor(Color.GRAY);
		font.draw(stage.getBatch(), VoraciousViper.getInstance().getBundle().get(I18NKeys.Name), xOffsetScoreTable+45, viewport.getWorldHeight()-40);
		font.draw(stage.getBatch(), VoraciousViper.getInstance().getBundle().get(I18NKeys.Date), xOffsetScoreTable+240, viewport.getWorldHeight()-40);
		font.draw(stage.getBatch(), VoraciousViper.getInstance().getBundle().get(I18NKeys.Steps), xOffsetScoreTable+390, viewport.getWorldHeight()-40);
		font.draw(stage.getBatch(), VoraciousViper.getInstance().getBundle().get(I18NKeys.Level), xOffsetScoreTable+460, viewport.getWorldHeight()-40);
		font.draw(stage.getBatch(), VoraciousViper.getInstance().getBundle().get(I18NKeys.Score), xOffsetScoreTable+510, viewport.getWorldHeight()-40);
		
		font.setColor(Color.WHITE);
		if (scoreEntries != null) {
			for (int i = 0; i < scoreEntries.length; i++) {
				if (scoreEntries[i] == null) {
					continue;
				}
				font.setColor(scoreEntries[i].getId().equals(SettingsManager.getInstance().getPlayerId()) ?
						new Color(0, 1, 0, fetching ? .5f : 1) : new Color(1, 1, 1, fetching ? .5f : 1));
				float y = (scrollbarMaximumY - i * Config.LINE_HEIGHT_HIGHSCORES + inputHandler.getDeltaY());
				if (y <= scrollbarMaximumY) { // lines disappear when having y above topY

					// name
					stringBuilder.append(Utils.padLeft(i + 1, 3))
							.append(". ")
							.append(scoreEntries[i].getName());
					font.draw(stage.getBatch(), stringBuilder, xOffsetScoreTable+10, y);
					stringBuilder.setLength(0);

					// date
					font.draw(stage.getBatch(), scoreEntries[i].getDate(), xOffsetScoreTable+240, y);

					// numSteps
					font.draw(stage.getBatch(), scoreEntries[i].getNumSteps() + "", xOffsetScoreTable+390, y);
					
					// level
					font.draw(stage.getBatch(), stringBuilder.append(scoreEntries[i].getLevel()), xOffsetScoreTable+460, y);
					stringBuilder.setLength(0);

					// score
					font.draw(stage.getBatch(), stringBuilder.append(scoreEntries[i].getScore()), xOffsetScoreTable+510, y);

					stringBuilder.setLength(0);
				}
			}
		} else {
			font.draw(stage.getBatch(), customHighscoreText, xOffsetScoreTable+10, scrollbarMaximumY);
		}
		if (fetching) {
			font.setColor(Color.WHITE);
			String fetching = VoraciousViper.getInstance().getBundle().get(I18NKeys.Fetching) + "...";
			font.draw(stage.getBatch(), fetching, 
					viewport.getWorldWidth()/2-Utils.getFontWidth(fetching, font)/2+xOffsetScoreTable,
					viewport.getWorldHeight()/2-Utils.getFontHeight(fetching, font)/2);
		}
	}

	@Override
	public void show() {
		super.show();
		camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
		camera.update();
		xOffset = 0;
		Gdx.input.setInputProcessor(inputMultiplexer);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	private void synchronizeCheckedStatesOfSettingsButtons() {
		Preferences prefs = SettingsManager.getInstance().get();
		buttonsSettings[0][0].setChecked(prefs.getString(PrefsKeys.Language).equals(Locale.ENGLISH.toString()));
		buttonsSettings[0][1].setChecked(prefs.getString(PrefsKeys.Language).equals(Locale.GERMAN.toString()));

		buttonsSettings[1][0].setChecked(prefs.getBoolean(PrefsKeys.Music));
		((RetroImageToggleButton) buttonsSettings[1][0]).setToggled(prefs.getBoolean(PrefsKeys.Music));
		buttonsSettings[1][1].setChecked(prefs.getBoolean(PrefsKeys.Sound));
		((RetroImageToggleButton) buttonsSettings[1][1]).setToggled(prefs.getBoolean(PrefsKeys.Sound));

		buttonsSettings[3][0].setChecked(prefs.getString(PrefsKeys.Controls).equals(PrefsKeys.ControlsTouchAreas));
		buttonsSettings[3][1].setChecked(prefs.getString(PrefsKeys.Controls).equals(PrefsKeys.ControlsTouchPad));
		buttonsSettings[3][2].setChecked(prefs.getString(PrefsKeys.Controls).equals(PrefsKeys.ControlsRelative));

		for (RetroTextButton button : buttonsMainMenu) {
			button.autoSize();
		}
		((RetroTextButton) buttonsSettings[2][0]).autoSize();
		updatePlayernameText();
	}

	public void keyboardInputCanceled() {
		buttonsSettings[3][0].setChecked(false);
	}

	public void updatePlayernameText() {
		String playername = SettingsManager.getInstance().get().getString(PrefsKeys.PlayerName);
		((RetroTextButton) buttonsSettings[2][0]).setText(VoraciousViper.getInstance().getBundle().get(I18NKeys.Playername) + ": " +
				((playername.length() == 0) ? "<" + VoraciousViper.getInstance().getBundle().get(I18NKeys.Unknown) + ">" : playername));
	}

	public Viewport getViewport() {
		return viewport;
	}

	public ScoreEntry[] getScoreEntries() {
		return scoreEntries;
	}

	public void setScrollbarPositionY(float scrollbarPositionY) {
		if (scrollbarHandle != null && scrollbarHandle.isVisible())
			this.scrollbarCurrentY = scrollbarPositionY;
	}

	public RetroTextButton getCloseButton() {
		return buttonClose;
	}

	public void setFetching(boolean fetching) {
		this.fetching = fetching;
	}

	public void setScoreEntries(ScoreEntry[] scoreEntries) {
		this.scoreEntries = scoreEntries;
		scrollbarCurrentY = scrollbarMaximumY;
		inputHandler.resetDeltaY();
	}

	public int getNumScoreEntries() {
		if (scoreEntries == null)
			return 0;
		return scoreEntries.length;
	}
	
	public void setCustomHighscoreText(String customHighscoreText) {
		this.customHighscoreText = customHighscoreText;
	}

}

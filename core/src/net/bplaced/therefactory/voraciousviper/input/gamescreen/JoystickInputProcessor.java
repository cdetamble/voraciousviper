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

package net.bplaced.therefactory.voraciousviper.input.gamescreen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.voraciousviper.input.IRenderableInputProcessor;
import net.bplaced.therefactory.voraciousviper.misc.Utils;
import net.bplaced.therefactory.voraciousviper.screens.GameScreen;
import net.bplaced.therefactory.voraciousviper.screens.GameScreen.GameState;

public class JoystickInputProcessor extends AbstractGameScreenInputProcessor implements IRenderableInputProcessor {

	private final Touchpad touchpad;
	private final float circleX;
	private final float circleY;

	public JoystickInputProcessor(final GameScreen gameScreen, FitViewport viewport, TextureAtlas textureAtlas,
			ShapeRenderer shapeRenderer, BitmapFont font) {
		super(gameScreen, viewport, textureAtlas, shapeRenderer);
		
		//Create a touchpad skin	
		Skin touchpadSkin = new Skin();
		//Set background image
		//Set knob image
		touchpadSkin.add("touchKnob", new Texture("touchKnob.png"));
		//Create TouchPad Style
		TouchpadStyle touchpadStyle = new TouchpadStyle();
		//Create Drawable's from TouchPad skin
		Drawable touchKnob = touchpadSkin.getDrawable("touchKnob");
		//Apply the Drawables to the TouchPad Style
		//touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		//Create new TouchPad with the created style
		touchpad = new Touchpad(10, touchpadStyle);
		//setBounds(x,y,width,height)
		touchpad.setBounds(viewport.getWorldWidth() - 15 - 200, 15, 200, 200);
		circleX = touchpad.getX() + touchpad.getWidth()/2;
		circleY = touchpad.getY() + touchpad.getHeight()/2;
		touchpad.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
		        if (gameScreen.getState() == GameState.GameIsBeginning) {
		            gameScreen.setState(GameState.GameIsRunning);
		        }
			}
		});
		stage.addActor(touchpad);
	}

	@Override
	public void update() {
		if (Utils.within(touchpad.getKnobPercentX(), -.5f, .5f) && touchpad.getKnobPercentY() > 0) {			
			gameScreen.getViper().moveUp();
		}
		else if (Utils.within(touchpad.getKnobPercentX(), -.5f, .5f) && touchpad.getKnobPercentY() < 0) {			
			gameScreen.getViper().moveDown();
		}
		else if (Utils.within(touchpad.getKnobPercentY(), -.5f, .5f) && touchpad.getKnobPercentX() < 0) {			
			gameScreen.getViper().moveLeft();
		}
		else if (Utils.within(touchpad.getKnobPercentY(), -.5f, .5f) && touchpad.getKnobPercentX() > 0) {			
			gameScreen.getViper().moveRight();
		}
	}
	
	@Override
	public void render(SpriteBatch batch, BitmapFont font) {
		super.render(batch, font);
    	if (gameScreen.getState().equals(GameState.GameIsRunning) || gameScreen.getState().equals(GameState.GameIsBeginning)) {
			touchpad.draw(batch, 1);
			touchpad.setVisible(true);
    	} else {
    		touchpad.setVisible(false);
    	}
	}

	@Override
	public void render(ShapeRenderer shapeRenderer) {
		super.render(shapeRenderer);
		shapeRenderer.set(ShapeType.Filled);
		shapeRenderer.setColor(buttonTransparentColor);
		shapeRenderer.circle(circleX, circleY, 100);
	}

	@Override
	public void dispose() {
		stage.getActors().removeValue(touchpad, true);
	}

}

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

import static net.bplaced.therefactory.voraciousviper.core.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Down;
import static net.bplaced.therefactory.voraciousviper.core.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Left;
import static net.bplaced.therefactory.voraciousviper.core.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Right;
import static net.bplaced.therefactory.voraciousviper.core.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Up;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.voraciousviper.core.constants.Config;
import net.bplaced.therefactory.voraciousviper.core.input.IRenderableInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.misc.Utils;
import net.bplaced.therefactory.voraciousviper.core.model.Viper;
import net.bplaced.therefactory.voraciousviper.core.screens.GameScreen;
import net.bplaced.therefactory.voraciousviper.core.screens.GameScreen.GameState;

public class TouchPadInputProcessor extends AbstractGameScreenInputProcessor implements IRenderableInputProcessor {

	private final Rectangle[][] rectanglesTouchAreas;
	private final Viper viper;
	private final Vector2 touchCoordinates;
	private int indexOfTouchedButton;
	private final short size = 70;
	private short yOffset = 60;
	
	public TouchPadInputProcessor(GameScreen gameScreen, FitViewport viewport, TextureAtlas textureAtlas,
			ShapeRenderer shapeRenderer, BitmapFont font) {
		super(gameScreen, viewport, textureAtlas, shapeRenderer);
		this.rectanglesTouchAreas = new Rectangle[2][4];
		this.viewport = viewport;
		this.touchCoordinates = new Vector2();
		this.indexOfTouchedButton = -1;
		this.viper = gameScreen.getViper();
		
		float xOffset = Config.TILE_WIDTH * 1.5f;
		rectanglesTouchAreas[0][Up.ordinal()] = new Rectangle(xOffset+size, yOffset+size*2, size, size);
		rectanglesTouchAreas[0][Down.ordinal()] = new Rectangle(xOffset+size, yOffset, size, size);
		rectanglesTouchAreas[0][Left.ordinal()] = new Rectangle(xOffset, yOffset+size, size, size);
		rectanglesTouchAreas[0][Right.ordinal()] = new Rectangle(xOffset+size*2, yOffset+size, size, size);
		
		xOffset = viewport.getWorldWidth() - size*3 - Config.TILE_WIDTH * 1.5f;
		rectanglesTouchAreas[1][Up.ordinal()] = new Rectangle(xOffset+size, yOffset+size*2, size, size);
		rectanglesTouchAreas[1][Down.ordinal()] = new Rectangle(xOffset+size, yOffset, size, size);
		rectanglesTouchAreas[1][Left.ordinal()] = new Rectangle(xOffset, yOffset+size, size, size);
		rectanglesTouchAreas[1][Right.ordinal()] = new Rectangle(xOffset+size*2, yOffset+size, size, size);
	}
	
	@Override
	public void render(ShapeRenderer shapeRenderer) {
		super.render(shapeRenderer);
		shapeRenderer.set(ShapeType.Filled);
		
		// 4 touch pads
		for (int i = 0; i < rectanglesTouchAreas[0].length; i++) {
			if (indexOfTouchedButton == i) {
				shapeRenderer.setColor(buttonEdgeColor);
			} else {
				shapeRenderer.setColor(.2f, .2f, .2f, 1);
			}
			for (Rectangle[] rectangles : rectanglesTouchAreas) {
				shapeRenderer.rect(rectangles[i].x, rectangles[i].y, rectangles[i].width, rectangles[i].height);
			}
		}
		
		// circle in the middle
		for (Rectangle[] rectangles : rectanglesTouchAreas) {
			shapeRenderer.setColor(buttonTransparentColor);
			shapeRenderer.rect(rectangles[Left.ordinal()].getX()+size, yOffset+size, size, size);
			shapeRenderer.setColor(.3f, .3f, .3f, 1f);
			shapeRenderer.circle(rectangles[Left.ordinal()].getX()+size*1.5f, yOffset+size+size/2, size*.55f);
		}
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		boolean returnValue = super.touchDragged(screenX, screenY, pointer);
		return handleTouchAt(screenX, screenY) || returnValue;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean returnValue = super.touchDown(screenX, screenY, pointer, button);
		return handleTouchAt(screenX, screenY) || returnValue;
	}
	
	private boolean handleTouchAt(int screenX, int screenY) {
		if (gameScreen.getState().equals(GameState.ShowContinueDialog)
				|| gameScreen.getState().equals(GameState.ShowGameOverDialog)
				|| gameScreen.getState().equals(GameState.ShowPauseMenuDialog)) {
			return false;
		}
		touchCoordinates.set(screenX, screenY);
		touchCoordinates.set(viewport.unproject(touchCoordinates));
		for (int i = 0; i < rectanglesTouchAreas[0].length; i++) {
			if (Utils.within(touchCoordinates, rectanglesTouchAreas[0][i]) || Utils.within(touchCoordinates, rectanglesTouchAreas[1][i])) {
				indexOfTouchedButton = i;
				viper.move(indexOfTouchedButton);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		boolean returnValue = super.touchUp(screenX, screenY, pointer, button);
		indexOfTouchedButton = -1;
		return returnValue;
	}

	@Override
	public void update() {
		// do nothing
	}

	@Override
	public void dispose() {
		// do nothing
	}

}

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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.bplaced.therefactory.voraciousviper.constants.Config;
import net.bplaced.therefactory.voraciousviper.input.IRenderableInputProcessor;
import net.bplaced.therefactory.voraciousviper.misc.Utils;
import net.bplaced.therefactory.voraciousviper.model.Viper;
import net.bplaced.therefactory.voraciousviper.screens.GameScreen;
import net.bplaced.therefactory.voraciousviper.screens.GameScreen.GameState;

import static net.bplaced.therefactory.voraciousviper.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Down;
import static net.bplaced.therefactory.voraciousviper.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Left;
import static net.bplaced.therefactory.voraciousviper.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Right;
import static net.bplaced.therefactory.voraciousviper.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Up;

public class TouchAreasInputProcessor extends AbstractGameScreenInputProcessor implements IRenderableInputProcessor {

	private final Viper viper;
	private int indexOfTouchedButton = -1;
	private final Rectangle[] rectangles;
	private final GameScreen gameScreen;
	private final Vector2 touchCoordinates;

	public TouchAreasInputProcessor(GameScreen gameScreen, Viewport viewport, TextureAtlas textureAtlas, ShapeRenderer shapeRenderer, BitmapFont font) {
		super(gameScreen, viewport, textureAtlas, shapeRenderer);
		this.gameScreen = gameScreen;
		this.viper = gameScreen.getLevel().getViper();
		rectangles = new Rectangle[4];
        touchCoordinates = new Vector2();
		float widthPercentageOfVerticalButtons = .6f;

		// left button
		rectangles[Left.ordinal()] = new Rectangle(
				0,
				0,
				Config.WINDOW_WIDTH * (1 - widthPercentageOfVerticalButtons) / 2,
				Config.WINDOW_HEIGHT- Config.TILE_HEIGHT*2);

		// up button
		rectangles[Up.ordinal()] = new Rectangle(
				rectangles[Left.ordinal()].getWidth(),
				viewport.getWorldHeight()/2,
				Config.WINDOW_WIDTH * widthPercentageOfVerticalButtons,
				(viewport.getWorldHeight())/2- Config.TILE_HEIGHT*2);

		// down button
		rectangles[Down.ordinal()] = new Rectangle(
				rectangles[Left.ordinal()].getWidth(),
				0,
				Config.WINDOW_WIDTH * widthPercentageOfVerticalButtons,
				viewport.getWorldHeight()/2);

		// right button
		rectangles[Right.ordinal()] = new Rectangle(
				rectangles[Left.ordinal()].getWidth() + rectangles[Up.ordinal()].getWidth(),
				0,
				Config.WINDOW_WIDTH * (1 - widthPercentageOfVerticalButtons)/2,
				Config.WINDOW_HEIGHT- Config.TILE_HEIGHT*2);
	}

	@Override
	public void update() {
		// do nothing
	}

	@Override
	public void render(SpriteBatch batch, BitmapFont font) {
		super.render(batch, font);
		// do nothing
	}

	@Override
	public void render(ShapeRenderer shapeRenderer) {
		super.render(shapeRenderer);

		if (gameScreen.getState().equals(GameState.GameIsRunning) || gameScreen.getState().equals(GameState.GameIsBeginning)) {
			if (indexOfTouchedButton > -1) {
				shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(buttonEdgeColor);
				shapeRenderer.rect(
						rectangles[indexOfTouchedButton].getX(),
						rectangles[indexOfTouchedButton].getY(),
						rectangles[indexOfTouchedButton].getWidth(),
						rectangles[indexOfTouchedButton].getHeight());
			}

			shapeRenderer.setColor(buttonEdgeColor);
			shapeRenderer.set(ShapeRenderer.ShapeType.Line);
			shapeRenderer.rect(rectangles[Left.ordinal()].getX(), rectangles[Left.ordinal()].getY(), rectangles[Left.ordinal()].getWidth(), rectangles[Left.ordinal()].getHeight());
			shapeRenderer.rect(rectangles[Right.ordinal()].getX(), rectangles[Right.ordinal()].getY(), rectangles[Right.ordinal()].getWidth(), rectangles[Right.ordinal()].getHeight());
			shapeRenderer.line(rectangles[Up.ordinal()].getX(), rectangles[Up.ordinal()].getY(), rectangles[Right.ordinal()].getX(), rectangles[Up.ordinal()].getY());

			shapeRenderer.polygon(new float[] {
					60, Config.WINDOW_HEIGHT/2 - 10,
					45, Config.WINDOW_HEIGHT/2,
					60, Config.WINDOW_HEIGHT/2 + 10
					});
			shapeRenderer.polygon(new float[] {
					565, Config.WINDOW_HEIGHT/2 - 10,
					580, Config.WINDOW_HEIGHT/2,
					565, Config.WINDOW_HEIGHT/2 + 10
					});
			shapeRenderer.polygon(new float[] {
					Config.WINDOW_WIDTH/2, Config.WINDOW_HEIGHT - 60,
					Config.WINDOW_WIDTH/2 - 10, Config.WINDOW_HEIGHT - 75,
					Config.WINDOW_WIDTH/2 + 10, Config.WINDOW_HEIGHT - 75
					});
			shapeRenderer.polygon(new float[] {
					Config.WINDOW_WIDTH/2, 40,
					Config.WINDOW_WIDTH/2 - 10, 55,
					Config.WINDOW_WIDTH/2 + 10, 55
					});
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		super.touchDragged(screenX, screenY, pointer);
		handleTouchAt(screenX, screenY);
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		super.touchDown(screenX, screenY, pointer, button);
		handleTouchAt(screenX, screenY);
		return true;
	}

	private void handleTouchAt(int screenX, int screenY) {
		if (numTouches > 1) return;
		touchCoordinates.set(viewport.unproject(new Vector2(screenX, screenY)));
		for (int i = 0; i < rectangles.length; i++) {
			if (Utils.within(touchCoordinates, rectangles[i])) {
				indexOfTouchedButton = i;
				if (i == Up.ordinal()) {
					viper.moveUp();
				} else if (i == Right.ordinal()) {
					viper.moveRight();
				} else if (i == Down.ordinal()) {
					viper.moveDown();
				} else if (i == Left.ordinal()) {
					viper.moveLeft();
				}
			}
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
		indexOfTouchedButton = -1;
		return true;
	}

	@Override
	public void dispose() {
		// do nothing
	}

}

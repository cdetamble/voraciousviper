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

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.bplaced.therefactory.voraciousviper.input.IRenderableInputProcessor;
import net.bplaced.therefactory.voraciousviper.model.Viper;
import net.bplaced.therefactory.voraciousviper.screens.GameScreen;

public class SwipeInputProcessor extends AbstractGameScreenInputProcessor implements IRenderableInputProcessor {

	private final Viper viper;
	private final Vector2 vector2TouchStart;
	private final Vector2 vector2TouchEnd;

	public SwipeInputProcessor(GameScreen gameScreen, Viewport viewport, TextureAtlas textureAtlas,
			ShapeRenderer shapeRenderer, BitmapFont font) {
		super(gameScreen, viewport, textureAtlas, shapeRenderer);
		this.viper = gameScreen.getViper();
		vector2TouchStart = new Vector2();
		vector2TouchEnd = new Vector2();
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		super.touchDragged(screenX, screenY, pointer);
		Vector2 vector2NewCoordinates = viewport.unproject(new Vector2(screenX, screenY));
		vector2TouchEnd.set(vector2NewCoordinates);
		moveViper();
		float tolerance = 10;
		if (Math.abs(vector2NewCoordinates.x - vector2TouchStart.x) > tolerance
				&& Math.abs(vector2NewCoordinates.y - vector2TouchStart.y) > tolerance) {
			vector2TouchStart.set(vector2TouchEnd);
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		super.touchDown(screenX, screenY, pointer, button);
		vector2TouchStart.set(viewport.unproject(new Vector2(screenX, screenY)));
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
		vector2TouchEnd.set(viewport.unproject(new Vector2(screenX, screenY)));
		moveViper();
		return true;
	}
	
	private void moveViper() {
		float deltaX = vector2TouchStart.x - vector2TouchEnd.x;
		float deltaY = vector2TouchStart.y - vector2TouchEnd.y;
		if (Math.abs(deltaX) > Math.abs(deltaY)) {
			if (deltaX < 0) {
				viper.moveRight();
			} else {
				viper.moveLeft();
			}
		} else {
			if (deltaY < 0) {
				viper.moveUp();
			} else {
				viper.moveDown();
			}
		}
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

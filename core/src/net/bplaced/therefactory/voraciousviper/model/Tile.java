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

package net.bplaced.therefactory.voraciousviper.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;

import net.bplaced.therefactory.voraciousviper.constants.Config;

public class Tile {

	private TileType type;
	Sprite sprite;
	private GridPoint2 position;

	enum TileType {
		Floor, Consumable, Blocking, Door, Key
	}

	Tile() {
		
	}
	
	Tile(Sprite sprite, TileType type) {
		this.sprite = sprite;
		this.type = type;
		position = new GridPoint2();
	}

	Tile(Sprite sprite) {
		this(sprite, TileType.Floor);
	}

	void setType(TileType type) {
		this.type = type;
	}

	TileType getType() {
		return type;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	void setPosition(int x, int y) {
		position.set(x, y);
	}

	public GridPoint2 getPosition() {
		return position;
	}

	public void render(SpriteBatch batch, int x, int y) {
		batch.draw(sprite, x * Config.TILE_WIDTH, y * Config.TILE_HEIGHT);
	}

}

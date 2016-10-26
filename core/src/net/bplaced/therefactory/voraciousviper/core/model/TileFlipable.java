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

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.bplaced.therefactory.voraciousviper.core.constants.Config;

class TileFlipable extends Tile {

	private boolean flipX;
	private boolean flipY;

	TileFlipable(Sprite sprite) {
		super(sprite);
		setType(TileType.Blocking);
	}
	
	@Override
	public void render(SpriteBatch batch, int x, int y) {
    	if (flipX) {
    		batch.draw(getSprite(),
    				getPosition().x * Config.TILE_WIDTH + getSprite().getWidth(),
    				getPosition().y * Config.TILE_HEIGHT, -getSprite().getWidth(),
    				getSprite().getHeight());
    	}
    	else if (flipY) {
        	batch.draw(getSprite(),
        			getPosition().x * Config.TILE_WIDTH,
        			getPosition().y * Config.TILE_HEIGHT + getSprite().getHeight(),
        			getSprite().getWidth(), -getSprite().getHeight());
    	}
    	else if (getSprite() != null) {
    		batch.draw(getSprite(), getPosition().x * Config.TILE_WIDTH, getPosition().y * Config.TILE_HEIGHT);
    	}
	}

	void setFlipX(boolean flipX) {
		this.flipX = flipX;
	}

	void setFlipY(boolean flipY) {
		this.flipY = flipY;
	}

}

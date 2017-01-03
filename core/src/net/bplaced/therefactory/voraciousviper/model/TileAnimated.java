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

class TileAnimated extends Tile {

    private Sprite spriteAnimated;
    private final Sprite spriteDefault;
    
	TileAnimated(Sprite sprite, Sprite spriteAnimated, TileType tileType) {
        super(sprite, tileType);
        this.setSpriteAnimated(spriteAnimated);
        this.spriteDefault = new Sprite(sprite);
    }

	private void setSpriteAnimated(Sprite spriteAnimated) {
		this.spriteAnimated = spriteAnimated;
	}

	void animate(boolean animationTick) {
		if (animationTick) {
			sprite = spriteAnimated;
		} else {
			sprite = spriteDefault;
		}
	}
    
}

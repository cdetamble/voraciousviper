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

package net.bplaced.therefactory.voraciousviper.misc;

public class Scaler {

	private boolean scaleDown;
	private float scaler;
	private float step;
	private float min;
	private float max;

	public Scaler(float min, float max, float step, boolean scaleDown) {
		this.scaleDown = scaleDown;
		this.min = min;
		this.max = max;
		this.step = step;
		if (scaleDown) {
			this.scaler = max;
		} else {
			this.scaler = min;
		}
	}
	
	public void update() {
		if (scaleDown) {
			scaler -= step;
			if (scaler <= min) {
				scaler = min;
				scaleDown = false;
			}
		} else {
			scaler += step;
			if (scaler >= max) {
				scaler = max;
				scaleDown = true;
			}
		}
	}
	
	public float get() {
		return scaler;
	}

}

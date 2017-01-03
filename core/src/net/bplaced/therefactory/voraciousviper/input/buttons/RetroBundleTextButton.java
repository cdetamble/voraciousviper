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

package net.bplaced.therefactory.voraciousviper.input.buttons;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.bplaced.therefactory.voraciousviper.VoraciousViper;

public class RetroBundleTextButton extends RetroTextButton {

	private final String bundleKey;

	public RetroBundleTextButton(ShapeRenderer shapeRenderer, int width, int height, BitmapFont font, String bundleKey,
			ClickListener clickListener) {
		super(shapeRenderer, width, height, font, bundleKey, clickListener);
		this.bundleKey = bundleKey;
    	layout.setText(font, getText());
	}

	@Override
	protected String getText() {
		return VoraciousViper.getInstance().getBundle().get(bundleKey);
	}

}

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

package net.bplaced.therefactory.voraciousviper.core.input.buttons;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class RetroImageToggleButton extends RetroImageButton {

	private final Drawable drawable;
	private final Drawable drawableToggled;
	private boolean toggled;

	public RetroImageToggleButton(ShapeRenderer shapeRenderer, int width, int height, final Drawable drawable,
			final Drawable drawableToggled, String prefKey, boolean prefValue) {
		super(shapeRenderer, width, height, drawable, prefKey);
		this.drawable = drawable;
		this.drawableToggled = drawableToggled;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				setChecked(!toggled);
			}
		});
	}
	
	@Override
	public void setChecked(boolean isChecked) {
		setDrawable(isChecked ? drawable : drawableToggled);
		toggled = !toggled;
		super.setChecked(false);
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}

}

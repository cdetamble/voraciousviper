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

package net.bplaced.therefactory.voraciousviper.core.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utils {

	private static final Random random = new Random();
	private static final GlyphLayout layout = new GlyphLayout();
	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public static int randomWithin(int min, int max) {
		return random.nextInt(max) + min;
	}
	
	public static boolean within(float v, float min, float max) {
        return (v >= min && v <= max);
	}

	public static boolean within(Vector2 vector2, Rectangle rectangle) {
        return within(vector2.x, rectangle.getX(), rectangle.getX() + rectangle.getWidth())
                && within(vector2.y, rectangle.getY(), rectangle.getY() + rectangle.getHeight());
    }

	public static boolean within(Vector2 vector2, Button button) {
		return Utils.within(vector2.x, button.getX(), button.getX() + button.getWidth())
        		&& (Utils.within(vector2.y, button.getY(), button.getY() + button.getHeight()));
	}
	
	public static boolean within(int x, int y, Sprite sprite) {
		return within(x, sprite.getX(), sprite.getX() + sprite.getWidth())
				&& within(y, sprite.getY(), sprite.getY() + sprite.getHeight());
	}

	public static boolean within(Vector2 touchCoordinates, Sprite sprite) {
		return within(touchCoordinates.x, sprite.getX(), sprite.getX() + sprite.getWidth())
				&& within(touchCoordinates.y, sprite.getY(), sprite.getY() + sprite.getHeight());
	}

	public static String padLeft(int s, int n) {
        return String.format("%1$" + n + "s", s);
    }

	static String generatePlayerId() {
		return System.nanoTime() + "0" + Utils.randomWithin(1000, 9999);
	}

	public static BitmapFont initializeFont(String path, int size) {
        BitmapFont font;
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.mono = true;
		parameter.borderStraight = true;
        font = generator.generateFont(parameter);
        generator.dispose();
        return font;
	}

	public static float getFontWidth(String string, BitmapFont font) {
		layout.setText(font, string);
		return layout.width;
	}

	public static String secondsToTimeString(long numSeconds) {
		if (numSeconds == 0)
			return "-";
		return format.format(new Date(numSeconds * 1000L));
	}

	public static float getFontHeight(String string, BitmapFont font) {
		layout.setText(font, string);
		return layout.height;
	}

	public static void async(Runnable runnable) {
		new Thread(runnable).start();
	}
	
}

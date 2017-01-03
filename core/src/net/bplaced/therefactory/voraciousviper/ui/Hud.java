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

package net.bplaced.therefactory.voraciousviper.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.bplaced.therefactory.voraciousviper.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.constants.Config;
import net.bplaced.therefactory.voraciousviper.constants.I18NKeys;
import net.bplaced.therefactory.voraciousviper.misc.Utils;
import net.bplaced.therefactory.voraciousviper.model.Level;
import net.bplaced.therefactory.voraciousviper.screens.GameScreen;

public class Hud extends Stage {

	private final BitmapFont font;
	private Level level;
	private final StringBuilder stringBuilder;
	private final GameScreen gameScreen;
	
	private final int hudPositionY;
	private float livesXOffset;
	
	private final Sprite spriteTitle;
	private final Sprite spriteKey;
	
	public Hud(GameScreen gameScreen, BitmapFont font, TextureAtlas textureAtlas) {
		this.gameScreen = gameScreen;
		this.font = font;
		this.stringBuilder = new StringBuilder();

		spriteTitle = new Sprite(textureAtlas.createSprite("Title.Green"));
		spriteTitle.setPosition(5, Config.WINDOW_HEIGHT - spriteTitle.getHeight() - 2);
		
		spriteKey = textureAtlas.createSprite("Key.1");
		spriteKey.setPosition(spriteTitle.getX() + spriteTitle.getWidth() + 7, Config.WINDOW_HEIGHT - spriteTitle.getHeight() - 4);
		
		hudPositionY = Config.WINDOW_HEIGHT - 4;
	}
	
	public void render(SpriteBatch batch) {
		if (!gameScreen.getLevel().hadFirstLevelTransition()) { return; }
		spriteTitle.draw(batch);
		
		// change color of title if viper has collected the key
		if (gameScreen.getViper().hasKey()) {
			spriteKey.draw(batch);
		}
		
		// render number of lives
		for (int i = 0; i < level.getViper().getNumLives(); i++) {
			batch.draw(level.getViper().getHeadX2(),
					livesXOffset + i * level.getViper().getHeadX2().getWidth(),
					Config.WINDOW_HEIGHT - level.getViper().getHeadX2().getHeight());
		}
		
		font.setColor(Color.GRAY);

		int scoreX = 230;
		font.draw(batch, VoraciousViper.getInstance().getBundle().get(I18NKeys.Level), scoreX + 25, hudPositionY);
		font.draw(batch, VoraciousViper.getInstance().getBundle().get(I18NKeys.Score), scoreX + 140, hudPositionY);
		font.draw(batch, VoraciousViper.getInstance().getBundle().get(I18NKeys.Steps), scoreX + 260, hudPositionY);
		
		font.setColor(Color.YELLOW);
		font.draw(batch, stringBuilder.append(Utils.padLeft(level.getViper().getNumSteps(), 4)), scoreX +215, hudPositionY);
		stringBuilder.setLength(0);
		
		font.draw(batch, stringBuilder.append(Utils.padLeft(level.getViper().getScore(), 4)), scoreX +95, hudPositionY);
		stringBuilder.setLength(0);
		
		font.draw(batch, stringBuilder.append(Utils.padLeft(level.getIndexCurrentLevel() + 1, 2)), scoreX, hudPositionY);
		stringBuilder.setLength(0);
	}

	public void setLevel(Level level) {
		this.level = level;
        livesXOffset = spriteTitle.getWidth() + level.getViper().getHeadX2().getWidth() + 20;
	}
	
	public void show() {
		
	}

}

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

package net.bplaced.therefactory.voraciousviper.core.input;

import com.badlogic.gdx.Input.TextInputListener;

import net.bplaced.therefactory.voraciousviper.core.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.core.constants.Config;
import net.bplaced.therefactory.voraciousviper.core.constants.I18NKeys;
import net.bplaced.therefactory.voraciousviper.core.misc.SettingsManager;
import net.bplaced.therefactory.voraciousviper.core.net.HttpServer;
import net.bplaced.therefactory.voraciousviper.core.screens.TitleScreen;

public class KeyboardInputListener implements TextInputListener {

	private final TitleScreen titleScreen;

	public KeyboardInputListener(TitleScreen titleScreen) {
		this.titleScreen = titleScreen;
	}
	
    @Override
    public void input(String text) {
        if (text.length() <= Config.MAX_LENGTH_PLAYERNAME) {
    		HttpServer.changeName(titleScreen, SettingsManager.getInstance().getPlayerId(),
    				text, VoraciousViper.getInstance().getVersionCode(), false);
        } else {
			VoraciousViper.getInstance().toast(VoraciousViper.getInstance().getBundle().format(I18NKeys.NameMustBeShorterThan, Config.MAX_LENGTH_PLAYERNAME + 1), true);
        }
    }

    @Override
    public void canceled() {
    	titleScreen.keyboardInputCanceled();
    }

}

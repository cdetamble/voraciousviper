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

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import net.bplaced.therefactory.voraciousviper.core.constants.PrefsKeys;

public class SettingsManager {

    private static final String ObjectSeparator = "\n";
	private final Preferences preferences;
    private static SettingsManager instance;
    
	// prevent external instantiation
    private SettingsManager() {
        this.preferences = Gdx.app.getPreferences(PrefsKeys.PREFERENCES_FILE_ID);
    }
    
    public static SettingsManager getInstance() {
    	if (instance == null)
    		instance = new SettingsManager();
    	return instance;
    }
    
    public Preferences get() {
    	return preferences;
    }
    
    public void loadSettings() {
    	if (!preferences.contains(PrefsKeys.PlayerId)) preferences.putString(PrefsKeys.PlayerId, Utils.generatePlayerId());
    	if (!preferences.contains(PrefsKeys.Language)) preferences.putString(PrefsKeys.Language, getSystemLanguage(Locale.ENGLISH.toString()));
    	if (!preferences.contains(PrefsKeys.Music)) preferences.putBoolean(PrefsKeys.Music, true);
    	if (!preferences.contains(PrefsKeys.Sound)) preferences.putBoolean(PrefsKeys.Sound, true);
    	if (!preferences.contains(PrefsKeys.Controls)) preferences.putString(PrefsKeys.Controls, PrefsKeys.ControlsTouchAreas);
    	preferences.flush();
    }

	private String getSystemLanguage(String fallbackLocale) {
		String systemLanguage = java.util.Locale.getDefault().getLanguage();
		if (systemLanguage.equals(Locale.ENGLISH.toString())) {
			return Locale.ENGLISH.toString();
		}
		else if (systemLanguage.equals(Locale.GERMAN.toString())) {
			return Locale.GERMAN.toString();
		}
		return fallbackLocale;
	}

	public void setPlayerRank(int playerRank) {
		preferences.putInteger(PrefsKeys.PlayerRank, playerRank).flush();
	}

	public String getPlayerId() {
		return preferences.getString(PrefsKeys.PlayerId);
	}

	public String getPlayerName() {
		return preferences.getString(PrefsKeys.PlayerName);
	}

	public int getNumSteps() {
		return preferences.getInteger(PrefsKeys.NumSteps, 0);
	}

	public int getLevel() {
		return preferences.getInteger(PrefsKeys.Level);
	}

	public int getScore() {
		return preferences.getInteger(PrefsKeys.Score, Integer.MIN_VALUE);
	}

	public void setScore(int bestScore) {
		preferences.putInteger(PrefsKeys.Score, bestScore).flush();
	}

	public void setNumSteps(int bestNumSteps) {
		preferences.putInteger(PrefsKeys.NumSteps, bestNumSteps).flush();
	}

	public String getControls() {
		return preferences.getString(PrefsKeys.Controls);
	}

	public void setScoreEntries(ScoreEntry[] scoreEntries) {
		if (scoreEntries == null || scoreEntries.length == 0)
			return;
		StringBuilder sb = new StringBuilder();
		for (ScoreEntry scoreEntry : scoreEntries) {
			if (scoreEntry != null)
				sb.append(scoreEntry.toString()).append(ObjectSeparator);
		}
		sb.deleteCharAt(sb.length() - 1);
		if (sb.toString().length() > 0)
			preferences.putString(PrefsKeys.ScoreEntries, sb.toString()).flush();
	}
	
	public ScoreEntry[] getScoreEntries() {
		if (preferences.contains(PrefsKeys.ScoreEntries)
				&& preferences.getString(PrefsKeys.ScoreEntries).length() > 0) {
			String[] objects = preferences.getString(PrefsKeys.ScoreEntries).split(ObjectSeparator);
			ScoreEntry[] scoreEntries = new ScoreEntry[objects.length - 1];
			for (int i = 0; i < scoreEntries.length; i++) {
				scoreEntries[i] = ScoreEntry.fromString(objects[i]);
			}
			return scoreEntries;
		}
		return null;
	}

	public boolean isMusicEnabled() {
		return preferences.getBoolean(PrefsKeys.Music);
	}

}

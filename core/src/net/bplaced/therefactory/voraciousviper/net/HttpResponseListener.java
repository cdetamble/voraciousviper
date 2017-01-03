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

package net.bplaced.therefactory.voraciousviper.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import net.bplaced.therefactory.voraciousviper.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.constants.I18NKeys;
import net.bplaced.therefactory.voraciousviper.constants.PrefsKeys;
import net.bplaced.therefactory.voraciousviper.misc.ScoreEntry;
import net.bplaced.therefactory.voraciousviper.misc.SettingsManager;
import net.bplaced.therefactory.voraciousviper.misc.Utils;
import net.bplaced.therefactory.voraciousviper.net.HttpServer.Action;
import net.bplaced.therefactory.voraciousviper.screens.TitleScreen;

class HttpResponseListener implements Net.HttpResponseListener {

	private Action method;
	private String userId;
	private String userName;
	private TitleScreen titleScreen;
	private boolean silent;

	@Override
	public void handleHttpResponse(HttpResponse httpResponse) {

		// log header contents
//		for (Entry<String, List<String>> e : httpResponse.getHeaders().entrySet()) {
//			Gdx.app.log(getClass().getName(), e.getKey());
//			for (String s : e.getValue()) {
//				Gdx.app.log(getClass().getName(), s);
//			}
//		}
		String result = httpResponse.getResultAsString().trim();
		Gdx.app.log(getClass().getName(), result);
		
		switch (method) {
			case FetchHighscores:
				JsonValue root = new JsonReader().parse(result);
				ScoreEntry[] scoreEntries = new ScoreEntry[root.size];
				int pointer = 0;
				if (result.startsWith("DEBUG")) { // check for database maintenance mode
					VoraciousViper.getInstance().toast("Server Message: " + result.replace("DEBUG", ""), true);
				} else {
					for (int i = 0; i < root.size; i++) {
						String user_name = root.get(i).getString("user_name");
						ScoreEntry scoreEntry = new ScoreEntry();

						try {
							scoreEntry.setPlayerName(user_name != null && user_name.trim().length() > 0 ? user_name : VoraciousViper.getInstance().getBundle().get(I18NKeys.Unknown));
							scoreEntry.setPlayerId(root.get(i).has("user_id") ? root.get(i).getString("user_id") : "");
	                    	scoreEntry.setScore(root.get(i).has("score") ? root.get(i).getInt("score") : 0);
							scoreEntry.setLevel(root.get(i).has("level") ? root.get(i).getInt("level") : 0);
							scoreEntry.setNumSteps(root.get(i).has("num_steps") ? root.get(i).getInt("num_steps") : 0);
							scoreEntry.setDate(root.get(i).has("time") ? Utils.secondsToTimeString(Integer.parseInt(root.get(i).getString("time"))) : "");
							scoreEntries[pointer++] = scoreEntry;
						} catch (IllegalStateException ex) {
							ex.printStackTrace();
						}

						if (root.get(i).getString("user_id").equals(userId)) {
							SettingsManager.getInstance().setPlayerRank(i);
						}
					}
				}
				
				SettingsManager.getInstance().setScoreEntries(scoreEntries);
				titleScreen.setScoreEntries(scoreEntries);
				titleScreen.setFetching(false);
				break;
			case ChangeName:
	            SettingsManager.getInstance().get().putString(PrefsKeys.PlayerName, userName).flush();
	            titleScreen.updatePlayernameText();
	            titleScreen.setUpdating(false);
				break;
			default:
				break;
		}
	}

	@Override
	public void failed(Throwable t) {
		Gdx.app.log(getClass().getName(), "Request failed");
		requestFailed();
		t.printStackTrace();
	}

	private void requestFailed() {
		String customText = VoraciousViper.getInstance().getBundle().get(I18NKeys.RequestFailed);
		if (!silent)
			VoraciousViper.getInstance().toast(customText, true);
		switch (method) {
			case FetchHighscores:
				titleScreen.setFetching(false);
				titleScreen.setCustomHighscoreText("(" + customText + ")");
				break;
			case ChangeName:
				titleScreen.setUpdating(false);
				break;
			case SubmitHighscore:
				break;
			default:
				break;
		}
	}

	@Override
	public void cancelled() {
		Gdx.app.log(getClass().getName(), "Request cancelled");
		requestFailed();
	}

	void setMethod(HttpServer.Action method) {
		this.method = method;
	}
	
	void setUserId(String userId) {
		this.userId = userId;
	}
	
	void setUserName(String userName) {
		this.userName = userName;
	}

	void setTitleScreen(TitleScreen titleScreen) {
		this.titleScreen = titleScreen;
	}

	void setSilent(boolean silent) {
		this.silent = silent;
	}
}

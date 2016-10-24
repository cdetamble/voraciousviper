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

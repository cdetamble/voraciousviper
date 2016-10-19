package net.bplaced.therefactory.core.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.bplaced.therefactory.voraciousviper.core.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.core.constants.Config;

class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        float scale = 2f;
        config.width = (int)(Config.WINDOW_WIDTH * scale);
        config.height = (int)(Config.WINDOW_HEIGHT * scale);
        config.vSyncEnabled = true;
        config.resizable = true;
		new LwjglApplication(new VoraciousViper(null), config);
	}
}

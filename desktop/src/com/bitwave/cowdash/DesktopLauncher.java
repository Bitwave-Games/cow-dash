package com.bitwave.cowdash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bitwave.cowdash.CowDash;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setForegroundFPS(60);
		config.useVsync(true);
		config.setTitle("Cow Dash");
		new Lwjgl3Application(new CowDash(), config);
	}
}

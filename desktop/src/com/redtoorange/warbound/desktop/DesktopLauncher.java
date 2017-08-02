package com.redtoorange.warbound.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.redtoorange.warbound.MainGame;
import com.redtoorange.warbound.utilities.Constants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = Constants.TITLE;

		config.width = Constants.DESKTOP_WIDTH;
		config.height = Constants.DESKTOP_HEIGHT;

		config.resizable = false;

		if( Constants.BORDERLESS )
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

		new LwjglApplication(new MainGame(), config);
	}
}

package com.redtoorange.warbound.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.redtoorange.warbound.MainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Test Window";
		config.width = 1422;
		config.height = 800;

		new LwjglApplication(new MainGame(), config);
	}
}

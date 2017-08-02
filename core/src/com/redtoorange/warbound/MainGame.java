package com.redtoorange.warbound;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.redtoorange.warbound.screens.PlayScreen;

public class MainGame extends Game {

	@Override
	public void create() {
		setScreen( new PlayScreen() );
	}

	@Override
	public void render() {
		clearScreen();
		super.render();
	}

	public void clearScreen () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

}

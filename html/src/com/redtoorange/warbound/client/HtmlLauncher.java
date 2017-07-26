package com.redtoorange.warbound.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.redtoorange.warbound.MainGame;
import com.redtoorange.warbound.utilities.Constants;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration( Constants.WIDTH, Constants.HEIGHT );
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new MainGame();
        }
}
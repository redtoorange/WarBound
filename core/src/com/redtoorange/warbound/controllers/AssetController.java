package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * AssetController.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class AssetController {
    private AssetManager manager;

    public AssetController(){
        manager = new AssetManager(  );

        manager.load( "tiles/grass_1.png", Texture.class );
        manager.load( "tiles/grass_2.png", Texture.class );
    }
}

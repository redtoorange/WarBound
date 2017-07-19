package com.redtoorange.warbound;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.controllers.BuildingController;

/**
 * BuildingFactory.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class BuildingFactory {
    public static Building BuildFarm( BuildingController controller){
        return new Building( "Farm", new TextureRegion( new Texture( "buildings/farm.png" ) ), 2, 2,  controller);
    }
}

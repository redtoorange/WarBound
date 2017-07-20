package com.redtoorange.warbound.buildings;

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
    /**
     * Construct a default farm.
     * @param controller    Building controller for the game
     * @return  Constructed building ready to be placed.
     */
    public static Building BuildBarracks( BuildingController controller){
        return new Barracks( "Barracks",
                new TextureRegion( new Texture( "wc2_buildings/barracks.png" ) ),
                4, 4,  controller);
    }

    public static Building BuildFarm( BuildingController controller){
        return new Farm( "Farm",
                new TextureRegion( new Texture( "wc2_buildings/farm.png" ) ),
                2, 2,  controller);
    }
}

package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.controllers.BuildingController;
import com.redtoorange.warbound.utilities.Resource;

/**
 * Barracks.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class Farm extends Building {
    public static final String TAG = Farm.class.getSimpleName();

    protected static TextureRegion[] regions = {
            new TextureRegion( new Texture( "wc2_buildings/small_building_started.png" ) ),
            new TextureRegion( new Texture( "wc2_buildings/farm_building.png" ) ),
            new TextureRegion( new Texture( "wc2_buildings/farm.png" ) ),
    };

    protected int foodToAdd = 10;

    public Farm( String name, int width, int height, BuildingController controller ) {
        super( name, regions, width, height, controller );
        TYPE = BuildingType.FARM;
    }

    @Override
    protected void completeBuildingConstruction() {
        super.completeBuildingConstruction();

        owner.getResourceController().changeResource( Resource.FOOD_STORED, foodToAdd );
    }
}

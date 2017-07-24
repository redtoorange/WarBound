package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.Resource;

/**
 * Barracks.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class Farm extends Building {
    private int productionAmount = 10;

    private static TextureRegion[] regions = {
            new TextureRegion( new Texture( "wc2_buildings/small_building_started.png" ) ),
            new TextureRegion( new Texture( "wc2_buildings/farm_building.png" ) ),
            new TextureRegion( new Texture( "wc2_buildings/farm.png" ) ),
    };

    public Farm( String name, int width, int height, BuildingController controller ) {
        super( name, regions, width, height, controller );
    }

    @Override
    protected void finishConstruction() {
        super.finishConstruction();
        owner.getResourceController().changeResource( Resource.FOOD_STORED, productionAmount );
    }

    @Override
    public BuildingType getType() {
        return BuildingType.FARM;
    }
}

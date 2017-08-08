package com.redtoorange.warbound.buildings;

import com.redtoorange.warbound.controllers.BuildingController;
import com.redtoorange.warbound.map.MapTile;

/**
 * BuildingFactory.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class BuildingFactory {
    /** Instantly spawn a Barracks at the given location. */
    private static Building InstanceBarracks( BuildingController controller, MapTile tile ) {
        return new Barracks( "Barracks", 4, 4, controller, tile );
    }

    /** Setup a foundation to begin building a Barracks. */
    private static Building BuildBarracks( BuildingController controller ) {
        return new Barracks( "Barracks", 4, 4, controller );
    }

    /** Instantly spawn a Farm at the given location. */
    private static Building InstanceFarm( BuildingController controller, MapTile tile ) {
        return new Farm( "Farm", 2, 2, controller, tile );
    }

    /** Setup a foundation to begin building a Farm. */
    private static Building BuildFarm( BuildingController controller ) {
        return new Farm( "Farm", 2, 2, controller );
    }

    /** Instantly spawn a Goldmine at the given location. */
    private static Building InstanceGoldmine( BuildingController controller, MapTile tile  ){
        return new GoldMine( "Gold Mine", 4, 4, controller, tile );
    }

    /** Create a building of the given type and assign it to the given controller. */
    public static Building CreateBuildingConstruction( BuildingType buildingType, BuildingController controller){
        Building buildingInstance = null;

        switch ( buildingType ) {
            case BARRACKS:
                buildingInstance = BuildingFactory.BuildBarracks( controller );
                break;

            case FARM:
                buildingInstance = BuildingFactory.BuildFarm( controller );
                break;

            case NONE:
                break;
        }

        return buildingInstance;
    }

    /** Create a building of the given type and assign it to the given controller. */
    public static Building CreateBuildingInstance( BuildingType buildingType, BuildingController controller, MapTile tile){
        Building buildingInstance = null;

        switch ( buildingType ) {
            case GOLDMINE:
                buildingInstance = BuildingFactory.InstanceGoldmine( controller, tile );
                break;

            case BARRACKS:
                buildingInstance = BuildingFactory.InstanceBarracks( controller, tile );
                break;

            case FARM:
                buildingInstance = BuildingFactory.InstanceFarm( controller, tile );
                break;
        }

        return buildingInstance;
    }

}

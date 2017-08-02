package com.redtoorange.warbound.buildings;

import com.redtoorange.warbound.controllers.BuildingController;

/**
 * BuildingFactory.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class BuildingFactory {
    private static Building BuildBarracks( BuildingController controller ) {
        return new Barracks( "Barracks", 4, 4, controller );
    }

    private static Building BuildFarm( BuildingController controller ) {
        return new Farm( "Farm", 2, 2, controller );
    }

    /** Create a building of the given type and assign it to the given controller. */
    public static Building CreateBuildingInstance( BuildingType buildingType, BuildingController controller){
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

}

package com.redtoorange.warbound.buildings;

/**
 * BuildingType.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/24/2017
 */
public enum BuildingType {
    //          Gold    Wood    Oil     Food    Time
    NONE( 0, 0, 0, 0, 0.0f ),
    FARM( 100, 100, 0, 0, 3.5f ),
    BARRACKS( 500, 250, 0, 0, 7.0f ),
    GOLDMINE( 0, 0, 0, 0, 0.0f );

    public int goldCost, woodCost, oilCost, foodCost;
    public float productionTime;
    BuildingType( int goldCost, int woodCost, int oilCost, int foodCost, float productionTime ) {
        this.goldCost = goldCost;
        this.woodCost = woodCost;
        this.oilCost = oilCost;
        this.foodCost = foodCost;
        this.productionTime = productionTime;
    }

    public static BuildingType parseString( String type ) {
        BuildingType buildingType = NONE;

        switch ( type ) {
            case "farm":
            case "Farm":
            case "FARM":
                buildingType = FARM;
                break;

            case "barracks":
            case "Barracks":
            case "BARRACKS":
                buildingType = BARRACKS;
                break;

            case "goldmine":
            case "Goldmine":
            case "GOLDMINE":
                buildingType = GOLDMINE;
                break;
        }

        return buildingType;
    }
}

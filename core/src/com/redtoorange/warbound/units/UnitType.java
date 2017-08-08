package com.redtoorange.warbound.units;

/**
 * UnitType.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/22/2017
 */
public enum UnitType {
    //          Gold    Wood    Oil     Food    Time
    PEON( 100, 0, 0, 1, 1.0f ),
    FOOTMAN( 250, 0, 0, 1, 3.0f ),
    WORKER( 100, 0, 0, 1, 1.75f ),
    GRUNT( 400, 0, 0, 1, 4.0f );

    public int goldCost, woodCost, oilCost, foodCost;
    public float productionTime;
    UnitType( int goldCost, int woodCost, int oilCost, int foodCost, float productionTime ) {
        this.goldCost = goldCost;
        this.woodCost = woodCost;
        this.oilCost = oilCost;
        this.foodCost = foodCost;
        this.productionTime = productionTime;
    }

    public static UnitType parseString( String type ) {
        UnitType unitType = PEON;

        switch ( type ) {
            case "Peon":
            case "peon":
            case "PEON":
                unitType = PEON;
                break;

            case "Footman":
            case "footman":
            case "FOOTMAN":
                unitType = FOOTMAN;
                break;

            case "Worker":
            case "worker":
            case "WORKER":
                unitType = WORKER;
                break;

            case "Grunt":
            case "grunt":
            case "GRUNT":
                unitType = GRUNT;
                break;
        }

        return unitType;
    }
}

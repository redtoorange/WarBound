package com.redtoorange.warbound.buildings;

/**
 * BuildingType.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/24/2017
 */
public enum BuildingType {
    FARM(100, 0, 0, 1, 5.0f),
    BARRACKS(250, 0, 0, 1, 3.0f);

    BuildingType(int goldCost, int woodCost, int oilCost, int foodCost, float productionTime){
        this.goldCost = goldCost;
        this.woodCost = woodCost;
        this.oilCost = oilCost;
        this.foodCost = foodCost;
        this.productionTime = productionTime;
    }

    public int goldCost, woodCost, oilCost, foodCost;
    public float productionTime;
}

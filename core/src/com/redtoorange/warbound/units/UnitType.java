package com.redtoorange.warbound.units;

/**
 * UnitType.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/22/2017
 */
public enum UnitType {
    PEON(100, 0, 0, 1), FOOTMAN(250, 0, 0, 1), WORKER(100, 0, 0, 1), GRUNT(400, 0, 0, 1);

    UnitType(int goldCost, int woodCost, int oilCost, int foodCost){
        this.goldCost = goldCost;
        this.woodCost = woodCost;
        this.oilCost = oilCost;
        this.foodCost = foodCost;
    }

    public int goldCost, woodCost, oilCost, foodCost;
}

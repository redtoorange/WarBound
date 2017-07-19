package com.redtoorange.warbound.controllers;

import com.redtoorange.warbound.Resource;

/**
 * PlayerController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class PlayerController {
    private int gold;
    private int wood;
    private int oil;
    private int usedFood;
    private int availableFood;

    public PlayerController( int gold, int wood, int oil, int usedFood, int availableFood ) {
        this.gold = gold;
        this.wood = wood;
        this.oil = oil;
        this.usedFood = usedFood;
        this.availableFood = availableFood;
    }

    public int getUsedFood() {
        return usedFood;
    }

    public int getAvailableFood() {
        return availableFood;
    }

    public int getResource( Resource type ){
        switch( type ){
            case GOLD:
                return gold;

            case WOOD:
                return wood;

            case OIL:
                return oil;
            default:
                return Integer.MIN_VALUE;
        }
    }

    public void changeResource( Resource type, int amount ){
        switch( type ){
            case GOLD:
                gold += amount;
                break;

            case WOOD:
                wood += amount;
                break;

            case OIL:
                oil += amount;
                break;
        }
    }
}

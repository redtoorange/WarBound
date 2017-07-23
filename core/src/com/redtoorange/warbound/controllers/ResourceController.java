package com.redtoorange.warbound.controllers;

import com.redtoorange.warbound.Resource;

/**
 * ResourceController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class ResourceController {
    private PlayerController owner;

    private int gold;
    private int wood;
    private int oil;
    private int usedFood;
    private int availableFood;

    public ResourceController( PlayerController owner, int gold, int wood, int oil, int usedFood, int availableFood ) {
        this.owner = owner;

        this.gold = gold;
        this.wood = wood;
        this.oil = oil;
        this.usedFood = usedFood;
        this.availableFood = availableFood;
    }

    public int getResource( Resource type ){
        switch( type ){
            case GOLD:
                return gold;

            case WOOD:
                return wood;

            case OIL:
                return oil;

            case FOOD_USED:
                return usedFood;

            case FOOD_STORED:
                return availableFood;

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

            case FOOD_USED:
                usedFood += amount;
                break;

            case FOOD_STORED:
                availableFood += amount;
                break;
        }
    }
}

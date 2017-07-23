package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.Resource;
import com.redtoorange.warbound.controllers.ResourceController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.Peon;
import com.redtoorange.warbound.units.UnitFactory;
import com.redtoorange.warbound.units.UnitType;

/**
 * Barracks.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class Barracks extends Building {
    public static final String TAG = Barracks.class.getSimpleName();

    private boolean producing = false;
    private float progress = 0.0f;
    private float totalTime = 0.0f;

    public Barracks( String name, TextureRegion texture, int width, int height, BuildingController controller ) {
        super( name, texture, width, height, controller );

        canBeEntered = true;
    }

    public void queueUnit( UnitType unitType ){
        if( !producing && !isUnderConstruction()){
            if( checkResources( unitType )){
                producing = true;

                progress = totalTime = unitType.productionTime;

                ResourceController rc = owner.getResourceController();

                rc.changeResource( Resource.GOLD, -unitType.goldCost);
                rc.changeResource( Resource.WOOD, -unitType.woodCost);
                rc.changeResource( Resource.OIL, -unitType.oilCost);
                rc.changeResource( Resource.FOOD_USED, unitType.foodCost);
            }
        }
    }

    public void cancelQueued(){
        producing = false;
    }

    @Override
    public void update( float deltaTime ) {
        super.update( deltaTime );

        if( producing){
            progress -= deltaTime;
            System.out.println( "\t" + ((1 - (progress/totalTime)) * 100) + "%" );

            if( progress <= 0.0f){
                produceUnit();
                producing = false;
            }
        }
    }

    private boolean checkResources(UnitType unit){
        ResourceController rc = owner.getResourceController();

        return (
                rc.getResource( Resource.GOLD ) >= unit.goldCost &&
                rc.getResource( Resource.WOOD ) >= unit.woodCost &&
                rc.getResource( Resource.OIL ) >= unit.oilCost &&
                rc.getResource( Resource.FOOD_STORED ) - rc.getResource( Resource.FOOD_USED )>= unit.foodCost
                );
    }

    private void produceUnit(){
        MapTile tile = getSpotOnPerimeter();

        if( tile != null){
            Peon p = (Peon) UnitFactory.BuildFootman( owner.getUnitController(), currentTiles[0][0].getEmptyOutsideArea( width, height, -1, -1 ) );
            owner.getUnitController().addUnit( p );
        }
        else{
            System.out.println( "No Empty Tiles!" );
        }
    }
}

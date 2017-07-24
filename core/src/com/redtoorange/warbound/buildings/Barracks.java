package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private static TextureRegion[] regions = {
            new TextureRegion( new Texture( "wc2_buildings/large_building_started.png" ) ),
            new TextureRegion( new Texture( "wc2_buildings/barracks_building.png" ) ),
            new TextureRegion( new Texture( "wc2_buildings/barracks.png" ) ),
    };

    private boolean producing = false;
    private float progress = 0.0f;
    private float totalTime = 0.0f;

    public Barracks( String name, int width, int height, BuildingController controller ) {
        super( name, regions, width, height, controller );

        canBeEntered = true;
    }

    public void queueUnit( UnitType unitType ){
        if( !producing && !inReadyForConstruction()){
            if( checkResources( unitType )){
                producing = true;
                progress = totalTime = unitType.productionTime;

                owner.getResourceController().chargeAmount(
                        unitType.goldCost, unitType.woodCost,
                        unitType.oilCost, unitType.foodCost
                );
            }
        }
    }

    public void cancelQueued(){
        producing = false;
    }

    @Override
    public void update( float deltaTime ) {
        super.update( deltaTime );

        switch ( buildingState ){
            case COMPLETE:
                if( producing){
                    progress -= deltaTime;
                    System.out.println( "\t" + ((1 - (progress/totalTime)) * 100) + "%" );

                    if( progress <= 0.0f){
                        produceUnit();
                        producing = false;
                    }
                }
                break;
        }

    }

    private boolean checkResources(UnitType unit){
        return owner.getResourceController().canAfford(
                unit.goldCost, unit.woodCost,
                unit.oilCost, unit.foodCost
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

    @Override
    public BuildingType getType() {
        return BuildingType.BARRACKS;
    }
}

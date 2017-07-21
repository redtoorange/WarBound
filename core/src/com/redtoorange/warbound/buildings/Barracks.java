package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.controllers.BuildingController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.Peon;
import com.redtoorange.warbound.units.UnitFactory;

/**
 * Barracks.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class Barracks extends Building {
    private float coolDown = .10f;
    private float currentTick = coolDown;
    private int productionAmount = 10;

    private MapTile rallyPoint;

    public Barracks( String name, TextureRegion texture, int width, int height, BuildingController controller ) {
        super( name, texture, width, height, controller );
    }

    @Override
    public void update( float deltaTime ) {
        super.update( deltaTime );

        if( Gdx.input.isKeyJustPressed( Input.Keys.P ))
            produceUnit();
//        if( state == State.PRODUCING ){
//            currentTick -= deltaTime;
//            if(  currentTick <= 0.0f ){
//                currentTick += coolDown;
//                produceUnit();
//            }
//        }
    }



    private void produceUnit(){
        MapTile tile = currentTiles[0][0].getEmptyOutsideArea( width, width, -1, -1 );

        if( tile != null){
            Peon p = (Peon) UnitFactory.BuildFootman( owner.getUnitController(), currentTiles[0][0].getEmptyOutsideArea( width, height, -1, -1 ) );
            owner.getUnitController().addUnit( p );
        }
        else{
            System.out.println( "No Empty Tiles!" );
        }

    }
}

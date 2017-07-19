package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.Building;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;

/**
 * BuildingController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class BuildingController {
    private Array<Building> buildings;
    private Building currentBuilding;
    private boolean placingBuilding;
    private MapController mapController;
    private CameraController cameraController;

    public BuildingController( MapController mapController, CameraController cameraController ){
        buildings = new Array< Building >(  );
        this.mapController = mapController;
        this.cameraController = cameraController;
    }

    public void beginPlacing( Building b){
        currentBuilding = b;
        placingBuilding = true;
    }

    public void cancelPlacing(){
        currentBuilding.cancel();
        currentBuilding = null;
        placingBuilding = false;
    }

    public void addBuilding( Building b){
        if( !buildings.contains( b, true ))
            buildings.add( b );
    }

    public void removeBuilding( Building b){
        buildings.removeValue( b, true );
    }

    public void update( float deltaTime ){
        for( Building b : buildings )
            b.update( deltaTime );

        //We are placing a building
        if( placingBuilding && currentBuilding != null){
            //Find the hovered tile
            MapTile currentTile = mapController.getTileByWorldPos(
                    cameraController.getMouseWorldPosition()
            );

            //Hand the tile to the building for positioning
            if( currentTile != null)
                currentBuilding.setPosition( currentTile );
        }
    }

    public void draw( SpriteBatch batch ) {
        for ( Building b : buildings )
            b.draw( batch );

        if( placingBuilding && currentBuilding != null)
            currentBuilding.draw( batch );
    }

    public boolean placeBuilding(){
        boolean placed = currentBuilding.placeBuilding();

        if( placed ){
            buildings.add( currentBuilding );
            currentBuilding = null;
            placingBuilding = false;
        }

        return placed;
    }

    public MapController getMapController(){
        return mapController;
    }
}

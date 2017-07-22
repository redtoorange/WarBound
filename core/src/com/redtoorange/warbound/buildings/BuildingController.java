package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.ControlState;
import com.redtoorange.warbound.controllers.CameraController;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.ui.ControlButtonState;
import com.redtoorange.warbound.units.UnitType;

/**
 * BuildingController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class BuildingController {
    private PlayerController owner;

    private Array<Building> buildings;
    private Building currentBuilding;
    private boolean placingBuilding;
    private MapController mapController;
    private CameraController cameraController;
    private ShapeRenderer shapeRenderer;


    public BuildingController( PlayerController owner, MapController mapController, CameraController cameraController ){
        this.owner = owner;
        buildings = new Array< Building >(  );

        shapeRenderer = new ShapeRenderer(  );
        this.mapController = mapController;
        this.cameraController = cameraController;
    }

    public void beginPlacing( Building b){
        owner.setControlState( ControlState.PLACING_BUILDING );
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

        if( !placingBuilding && currentBuilding != null ){
            if( Gdx.input.isKeyJustPressed( Input.Keys.P ))
                if( currentBuilding instanceof Barracks)
                    ((Barracks)currentBuilding).queueUnit( UnitType.PEON );
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

    /**
     *
     * @param start
     * @param end
     * @return
     */
    public boolean selectBuilding( Vector2 start, Vector2 end){
        float x = Math.min( start.x, end.x );
        float y = Math.min( start.y, end.y );

        float width = Math.max( start.x, end.x ) - x;
        float height = Math.max( start.y, end.y ) - y;

        Rectangle selectionRect = new Rectangle( x, y, width, height);


        for( Building b : buildings){
            if( selectionRect.overlaps( b.getBoundingBox() ) )
                currentBuilding = b;
        }

        boolean anythingSelected = currentBuilding != null;

        if( anythingSelected && currentBuilding instanceof Barracks){
            owner.getUiController().changeControlState( ControlButtonState.ButtonLayout.BARRACKS );
        }

        return anythingSelected;
    }

    /**
     *
     */
    public void deselectBuilding(){
        currentBuilding = null;
        owner.getUiController().changeControlState( ControlButtonState.ButtonLayout.DEFAULT );

    }

    /**
     *
     * @param cameraController
     * @param selectionBoxColor
     */
    public void renderSelected( CameraController cameraController, Color selectionBoxColor) {
        Gdx.gl.glLineWidth( 3f );
        shapeRenderer.setProjectionMatrix( cameraController.combineMatrix() );
        shapeRenderer.begin( ShapeRenderer.ShapeType.Line );
        shapeRenderer.setColor( selectionBoxColor );

        Rectangle box = currentBuilding.getBoundingBox();
        shapeRenderer.rect(box.x, box.y, box.width, box.height );

        shapeRenderer.end();
    }

    public PlayerController getOwner() {
        return owner;
    }

    public Building getCurrentBuilding() {
        return currentBuilding;
    }
}

package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.Constants;
import com.redtoorange.warbound.ControlState;
import com.redtoorange.warbound.controllers.CameraController;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.ui.ControlButtonState;

/**
 * BuildingController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class BuildingController {
    private PlayerController owner;
    private ShapeRenderer shapeRenderer;
    private Array<Building> buildings;

    private boolean placingBuilding;
    private Building currentBuilding;

    boolean initialized = false;
    private MapController mapController;
    private CameraController cameraController;

    public BuildingController( PlayerController owner ){
        this.owner = owner;
        buildings = new Array< Building >(  );
        shapeRenderer = new ShapeRenderer(  );
    }

    public void beginPlacing( Building b){
        owner.setControlState( ControlState.PLACING_BUILDING );
        currentBuilding = b;
        placingBuilding = true;
    }

    public void cancelPlacing(){
        currentBuilding.cancelPlacement();
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

    private void initialize(){
        initialized = true;

        mapController = owner.getMapController();
        cameraController = owner.getCameraController();
    }

    public void update( float deltaTime ){
        if( !initialized )
            initialize();

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

        if( !placingBuilding && currentBuilding != null && !currentBuilding.isComplete() ){
            if( Gdx.input.isKeyJustPressed( Input.Keys.N )) {
                currentBuilding.cancelConstruction();
                removeBuilding( currentBuilding );
                currentBuilding = null;
                System.out.println( "** Building Cancelled **" );
            }
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
            addBuilding( currentBuilding );
            currentBuilding = null;
            placingBuilding = false;
        }

        return placed;
    }


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

        if( anythingSelected ){
            if( !currentBuilding.isComplete() ){
                System.out.println( "Building under construction, should set menu to reflect that." );
            }
            else if( currentBuilding instanceof Barracks ){
                owner.getUiController().changeControlState( ControlButtonState.ButtonLayout.BARRACKS );
            }

        }

        return anythingSelected;
    }


    public void deselectBuilding(){
        currentBuilding = null;
        owner.getUiController().changeControlState( ControlButtonState.ButtonLayout.DEFAULT );
    }


    public void renderSelected( ) {
        if( currentBuilding != null ){
            Gdx.gl.glLineWidth( 3f );
            shapeRenderer.setProjectionMatrix( cameraController.combinedMatrix() );
            shapeRenderer.begin( ShapeRenderer.ShapeType.Line );
            shapeRenderer.setColor( Constants.SELECTION_COLOR  );

            Rectangle box = currentBuilding.getBoundingBox();
            shapeRenderer.rect(box.x, box.y, box.width, box.height );

            shapeRenderer.end();
        }
    }

    public PlayerController getOwner() {
        return owner;
    }

    public Building getCurrentBuilding() {
        return currentBuilding;
    }
}

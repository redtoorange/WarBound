package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.utilities.Constants;
import com.redtoorange.warbound.utilities.ControlState;
import com.redtoorange.warbound.controllers.CameraController;
import com.redtoorange.warbound.controllers.Controller;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.ui.ButtonLayout;
import com.redtoorange.warbound.units.Peon;
import com.redtoorange.warbound.units.UnitController;

/**
 * BuildingController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class BuildingController extends Controller{
    boolean initialized = false;
    private ShapeRenderer shapeRenderer;
    private Array< Building > buildings;
    private boolean placingBuilding;
    private Building currentBuilding;
    private MapController mapController;
    private CameraController cameraController;

    public BuildingController( PlayerController owner ) {
        super(owner);
        buildings = new Array< Building >();
        shapeRenderer = new ShapeRenderer();
    }

    public void attemptToBeginPlacing( BuildingType buildingType ) {
        if ( placingBuilding )
            cancelPlacing();

        if ( checkResources( buildingType ) )
            beginPlacing( buildingType );
    }

    private void beginPlacing( BuildingType buildingType ) {
        //charge resource
        owner.setControlState( ControlState.PLACING_BUILDING );
        currentBuilding = instanceBuilding( buildingType );
        placingBuilding = ( currentBuilding != null );
    }

    private Building instanceBuilding( BuildingType buildingType ) {
        owner.getResourceController().chargeAmount(
                buildingType.goldCost, buildingType.woodCost,
                buildingType.oilCost, buildingType.foodCost
        );

        return BuildingFactory.CreateBuildingInstance( buildingType, this );
    }

    private boolean checkResources( BuildingType b ) {
        return owner.getResourceController().canAfford(
                b.goldCost, b.woodCost,
                b.oilCost, b.foodCost
        );
    }

    public void cancelPlacing() {

        refundCurrentBuilding();

        currentBuilding.cancelPlacement();
        currentBuilding = null;
        placingBuilding = false;
    }

    private void refundCurrentBuilding() {
        BuildingType type = currentBuilding.getType();
        owner.getResourceController().chargeAmount(
                -type.goldCost, -type.woodCost,
                -type.oilCost, -type.foodCost
        );
    }

    public void addBuilding( Building b ) {
        if ( !buildings.contains( b, true ) )
            buildings.add( b );
    }

    public void removeBuilding( Building b ) {
        buildings.removeValue( b, true );
    }

    private void initialize() {
        initialized = true;

        mapController = owner.getMapController();
        cameraController = owner.getCameraController();
    }

    public void update( float deltaTime ) {
        if ( !initialized )
            initialize();

        for ( Building b : buildings )
            b.update( deltaTime );

        //We are placing a building
        if ( placingBuilding && currentBuilding != null ) {
            //Find the hovered tile
            MapTile currentTile = mapController.getTileByWorldPos(
                    cameraController.getMouseWorldPosition()
            );

            //Hand the tile to the building for positioning
            if ( currentTile != null )
                currentBuilding.setPosition( currentTile );
        }

        if ( !placingBuilding && currentBuilding != null && !currentBuilding.isComplete() ) {
            if ( Gdx.input.isKeyJustPressed( Input.Keys.N ) ) {
                cancelCurrentConstruction();
            }
        }
    }

    public void cancelCurrentConstruction() {
        if ( currentBuilding != null ) {

            refundCurrentBuilding();

            currentBuilding.cancelConstruction();
            removeBuilding( currentBuilding );
            currentBuilding = null;
            System.out.println( "** Building Cancelled **" );
        }
    }

    public void draw( SpriteBatch batch ) {
        for ( Building b : buildings )
            b.draw( batch );

        if ( placingBuilding && currentBuilding != null )
            currentBuilding.draw( batch );
    }

    public boolean placeBuilding() {
        boolean placed = currentBuilding.placeBuilding();

        if ( placed ) {
            UnitController uc = owner.getUnitController();
            Peon p = uc.getFirstSelectedPeon();
            MapTile t = currentBuilding.getCentralTile();

            if ( p != null && t != null ) {
                p.giveMoveOrder( t );
            }

            addBuilding( currentBuilding );
            currentBuilding = null;
            placingBuilding = false;
        }

        return placed;
    }

    public boolean selectBuilding( Vector2 start, Vector2 end ) {
        float x = Math.min( start.x, end.x );
        float y = Math.min( start.y, end.y );

        float width = Math.max( start.x, end.x ) - x;
        float height = Math.max( start.y, end.y ) - y;

        Rectangle selectionRect = new Rectangle( x, y, width, height );


        for ( Building b : buildings )
            if ( selectionRect.overlaps( b.getBoundingBox() ) )
                currentBuilding = b;

        boolean anythingSelected = currentBuilding != null;

        if ( anythingSelected )
            updateUI();

        return anythingSelected;
    }

    public void updateUI() {
        if ( currentBuilding != null ) {
            if ( !currentBuilding.isComplete() ) {
                owner.getUiController().changeControlState( ButtonLayout.CONSTRUCTION );
            } else {
                switch ( currentBuilding.getType() ) {
                    case BARRACKS:
                        owner.getUiController().changeControlState( ButtonLayout.BARRACKS );
                        break;

                    case FARM:
                    case NONE:
                        owner.getUiController().changeControlState( ButtonLayout.DEFAULT );
                        break;
                }
            }
        } else {
            owner.getUiController().changeControlState( ButtonLayout.DEFAULT );
        }
    }

    public void deselectBuilding() {
        currentBuilding = null;
        updateUI();
    }

    public void renderSelected() {
        if ( currentBuilding != null ) {
            Gdx.gl.glLineWidth( 3f );
            shapeRenderer.setProjectionMatrix( cameraController.combinedMatrix() );
            shapeRenderer.begin( ShapeRenderer.ShapeType.Line );
            shapeRenderer.setColor( Constants.SELECTION_COLOR );

            Rectangle box = currentBuilding.getBoundingBox();
            shapeRenderer.rect( box.x, box.y, box.width, box.height );

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

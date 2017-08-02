package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.ui.UIController;
import com.redtoorange.warbound.units.Unit;
import com.redtoorange.warbound.units.UnitController;
import com.redtoorange.warbound.units.UnitFactory;
import com.redtoorange.warbound.utilities.Constants;
import com.redtoorange.warbound.utilities.ControlState;

/**
 * PlayerController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class PlayerController implements ClickListener {
    private SpriteBatch batch;
    private InputMultiplexer inputMultiplexer;

    //Controllers
    private BuildingController buildingController;
    private UnitController unitController;
    private UIController uiController;
    private CameraController cameraController;
    private MouseClickController clickController;
    private ResourceController resourceController;
    private SelectionController selectionController;

    /** Loaded in by the game engine. */
    private MapController mapController;

    private ControlState controlState = ControlState.IDLE;


    public PlayerController( MapController mapController ) {
        this.mapController = mapController;

        batch = new SpriteBatch();

        initControllers();
    }

    private void initControllers() {
        selectionController = new SelectionController( this );
        cameraController = new CameraController( this, 25, 25 );
        buildingController = new BuildingController( this );
        unitController = new UnitController( this );
        resourceController = new ResourceController( this, 1000, 500, 0, 4, 10 );

        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor( inputMultiplexer );
        uiController = new UIController( this, inputMultiplexer );
        clickController = new MouseClickController( this );
        inputMultiplexer.addProcessor( clickController );

        if( mapController.unitSpawns.size > 0){
            for( MapTile tile : mapController.unitSpawns.keys()){
                System.out.println( "Tile: " + tile );
                System.out.println( "Type: " + mapController.unitSpawns.get( tile ) );

                Unit u = UnitFactory.BuildUnit( mapController.unitSpawns.get( tile ), unitController, tile );
                if( u != null){
                    unitController.addUnit( u );
                }
            }
        }
    }

    public void update( float deltaTime ) {
        uiController.update( deltaTime );
        cameraController.handleInput( deltaTime );

        unitController.update( deltaTime );
        buildingController.update( deltaTime );
    }

    public void draw() {
        cameraController.update();

        batch.setProjectionMatrix( cameraController.combinedMatrix() );
        batch.begin();

        mapController.draw( batch );
        unitController.draw( batch );
        buildingController.draw( batch );

        batch.end();

        if ( controlState == ControlState.SELECTING )
            selectionController.renderSelectionBox();

        if ( placingABuilding() )
            unitController.renderSelected();

        if ( controlState == ControlState.BUILDING_SELECTED )
            buildingController.renderSelected( );

        uiController.draw();

        if ( Constants.DEBUGGING ) {
            unitController.debugDraw();
        }
    }

    /** @return Is the player placing a building while units are selected */
    private boolean placingABuilding() {
        return controlState == ControlState.UNITS_SELECTED || controlState == ControlState.PLACING_BUILDING && unitController.hasUnitsSelected();
    }

    /** Resize the CameraController and the UIController. */
    public void resize( int width, int height ) {
        cameraController.resize( width, height );
        uiController.resize( width, height );
    }

    @Override
    public boolean touchDown( int screenX, int screenY, int pointer, int button ) {
        if ( button == Input.Buttons.LEFT ) {
            switch ( controlState ) {
                case UNITS_SELECTED:
                case BUILDING_SELECTED:
                case IDLE:

                    selectionController.setStartTouch( cameraController.getMouseWorldPosition() );
                    selectionController.setEndTouch( cameraController.getMouseWorldPosition() );

                    controlState = ControlState.SELECTING;
                    break;

                case PLACING_BUILDING:
                    if ( buildingController.placeBuilding() ) {
                        if ( unitController.hasUnitsSelected() )
                            controlState = ControlState.UNITS_SELECTED;
                        else
                            controlState = ControlState.IDLE;
                    }
                    break;
            }
        }

        if ( button == Input.Buttons.RIGHT ) {
            switch ( controlState ) {
                case IDLE:
                    break;

                case UNITS_SELECTED:
                    MapTile goal = mapController.getTileByWorldPos( cameraController.getMouseWorldPosition() );
                    if ( goal != null )
                        unitController.giveMoveOrder( goal );
                    break;

                case PLACING_BUILDING:
                    buildingController.cancelPlacing();
                    if ( unitController.hasUnitsSelected() )
                        controlState = ControlState.UNITS_SELECTED;
                    else
                        controlState = ControlState.IDLE;
                    break;

                default:
                    setControlState( ControlState.IDLE );
            }
        }

        return false;
    }

    @Override
    public boolean touchUp( int screenX, int screenY, int pointer, int button ) {
        if ( button == Input.Buttons.LEFT ) {
            switch ( controlState ) {
                case IDLE:
                    break;

                case SELECTING:
                    unitController.deselectUnits();
                    buildingController.deselectBuilding();

                    selectionController.setEndTouch( cameraController.getMouseWorldPosition() );

                    if ( unitController.selectUnits( selectionController.getStartTouch(), selectionController.getEndTouch() ) ) {
                        controlState = ControlState.UNITS_SELECTED;
                    } else if ( buildingController.selectBuilding( selectionController.getStartTouch(), selectionController.getEndTouch() ) ) {
                        controlState = ControlState.BUILDING_SELECTED;
                    } else {
                        controlState = ControlState.IDLE;
                    }

                    break;
            }
        }

        return false;
    }

    @Override
    public boolean touchDragged( int screenX, int screenY, int pointer ) {
        if ( controlState == ControlState.SELECTING )
            selectionController.setEndTouch( cameraController.getMouseWorldPosition() );

        return false;
    }

    //-------------------------------------------------------------------------------
    //      Getters
    //-------------------------------------------------------------------------------
    public BuildingController getBuildingController() {
        return buildingController;
    }

    public UnitController getUnitController() {
        return unitController;
    }

    public UIController getUiController() {
        return uiController;
    }

    public CameraController getCameraController() {
        return cameraController;
    }

    public MouseClickController getClickController() {
        return clickController;
    }

    public ResourceController getResourceController() {
        return resourceController;
    }

    public MapController getMapController() {
        return mapController;
    }

    public ControlState getControlState() {
        return controlState;
    }

    public void setControlState( ControlState controlState ) {
        switch ( this.controlState ) {
            case UNITS_SELECTED:
                if ( controlState != ControlState.PLACING_BUILDING ) {
                    unitController.deselectUnits();
                }
                break;

            case BUILDING_SELECTED:
                buildingController.deselectBuilding();
                break;

            case PLACING_BUILDING:
                buildingController.cancelPlacing();
                break;
        }

        this.controlState = controlState;
    }

}

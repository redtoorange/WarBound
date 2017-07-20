package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.redtoorange.warbound.Constants;
import com.redtoorange.warbound.ControlState;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.UnitFactory;

/**
 * PlayerController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class PlayerController implements ClickListener {
    private SpriteBatch batch;

    private InputMultiplexer inputMultiplexer;

    private BuildingController buildingController;
    private UnitController unitController;
    private UIController uiController;
    private CameraController cameraController;
    private MouseClickController clickController;
    private ResourceController resourceController;
    private SelectionController selectionController;

    private MapController mapController;

    private ControlState controlState = ControlState.IDLE;


    public PlayerController( MapController mapController) {
        this.mapController = mapController;

        batch = new SpriteBatch(  );

        initControllers();
    }

    private void initControllers( ){
        inputMultiplexer = new InputMultiplexer(  );
        Gdx.input.setInputProcessor( inputMultiplexer );

        selectionController = new SelectionController( this );
        uiController = new UIController( this, inputMultiplexer );
        cameraController = new CameraController(25, 25);

        clickController = new MouseClickController();
        inputMultiplexer.addProcessor( clickController );
        clickController.addListener( this );

        resourceController = new ResourceController( 0, 0, 0, 4, 10 );

        unitController = new UnitController( this );
        unitController.addUnit( com.redtoorange.warbound.units.UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  25, 25 ) ) );
        unitController.addUnit( com.redtoorange.warbound.units.UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  25, 20 )  ) );
        unitController.addUnit( com.redtoorange.warbound.units.UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  20, 25 )  ) );
        unitController.addUnit( UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  20, 20 )  ) );

        buildingController = new BuildingController( this, mapController, cameraController );
    }



    public void update( float deltaTime ){
        uiController.update( deltaTime );
        cameraController.handleInput( deltaTime );

        unitController.update( deltaTime );
        buildingController.update( deltaTime );
    }

    public void draw(){
        cameraController.update();

        batch.setProjectionMatrix( cameraController.combineMatrix() );
        batch.begin();

        mapController.draw( batch );
        unitController.draw( batch );
        buildingController.draw( batch );

        batch.end();

        if( controlState == ControlState.SELECTING )
            selectionController.renderSelectionBox();
        if( controlState == ControlState.UNITS_SELECTED )
            unitController.renderSelected( cameraController, Constants.SELECTION_COLOR);
        if( controlState == ControlState.BUILDING_SELECTED )
            buildingController.renderSelected( cameraController, Constants.SELECTION_COLOR);

        uiController.draw();
    }



    public void resize( int width, int height ) {
        cameraController.resize( width, height );
        uiController.resize( width, height);
    }

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

    @Override
    public boolean touchDown( int screenX, int screenY, int pointer, int button ) {
        if( button == Input.Buttons.LEFT ){
            switch( controlState ){
                case IDLE:
                    selectionController.setStartTouch( cameraController.getMouseWorldPosition() );
                    selectionController.setEndTouch( cameraController.getMouseWorldPosition() );

                    controlState = ControlState.SELECTING;
                    break;

                case SELECTING:
                    break;

                case UNITS_SELECTED:
                    unitController.deselectUnits();

                    selectionController.setStartTouch( cameraController.getMouseWorldPosition() );
                    selectionController.setEndTouch( cameraController.getMouseWorldPosition() );

                    controlState = ControlState.SELECTING;
                    break;

                case BUILDING_SELECTED:
                    buildingController.deselectBuilding();

                    selectionController.setStartTouch( cameraController.getMouseWorldPosition() );
                    selectionController.setEndTouch( cameraController.getMouseWorldPosition() );

                    controlState = ControlState.SELECTING;
                    break;

                case PLACING_BUILDING:
                    if( buildingController.placeBuilding() )
                        controlState = ControlState.IDLE;
                    break;

                default:
                    break;
            }
        }

        if( button == Input.Buttons.RIGHT ){
            switch( controlState ){
                case IDLE:
                    break;

                case UNITS_SELECTED:
                    MapTile goal = mapController.getTileByWorldPos( cameraController.getMouseWorldPosition() );
                    if( goal != null)
                        unitController.giveMoveOrder( goal );

                    break;

                default:
                    setControlState( ControlState.IDLE );
            }
        }

        return false;
    }

    @Override
    public boolean touchUp( int screenX, int screenY, int pointer, int button ) {
        if( button == Input.Buttons.LEFT ){
            switch( controlState ){
                case IDLE:
                    break;

                case SELECTING:
                    selectionController.setEndTouch( cameraController.getMouseWorldPosition() );

                    if( unitController.selectUnits( selectionController.getStartTouch(), selectionController.getEndTouch() ) )
                        controlState = ControlState.UNITS_SELECTED;

                    else if( buildingController.selectBuilding( selectionController.getStartTouch(), selectionController.getEndTouch() ) )
                        controlState = ControlState.BUILDING_SELECTED;

                    else
                        controlState = ControlState.IDLE;

                    break;

                case UNITS_SELECTED:
                    break;

                case BUILDING_SELECTED:
                    break;

                case PLACING_BUILDING:
                    break;

                default:
                    break;
            }
        }

        return false;
    }

    @Override
    public boolean touchDragged( int screenX, int screenY, int pointer ) {
        if( controlState == ControlState.SELECTING )
            selectionController.setEndTouch( cameraController.getMouseWorldPosition() );

        return false;
    }

    public ControlState getControlState() {
        return controlState;
    }

    public void setControlState( ControlState controlState ) {

        switch(this.controlState){
            case UNITS_SELECTED:
                unitController.deselectUnits();
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

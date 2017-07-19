package com.redtoorange.warbound.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.redtoorange.warbound.*;
import com.redtoorange.warbound.buildings.BuildingFactory;
import com.redtoorange.warbound.controllers.*;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.UnitFactory;

/**
 * PlayScreen.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class PlayScreen extends ScreenAdapter implements ClickListener{
    private CameraController cameraController;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private MapController mapController;
    private MouseClickController clickController;
    private UnitController unitController;
    private BuildingController buildingController;
    private UIController uiController;
    private PlayerController playerController;

    private ControlState controlState = ControlState.IDLE;

    private Vector2 startTouch;
    private Vector2 endTouch;

    private int mapWidth = 50;
    private int mapHeight = 50;

    private float LINE_WIDTH = 0.1f;
    private Color selectionBoxColor = new Color( 0, 1, 0, 0.75f);
    private Color gridLineColor = new Color( 0, 0, 0, 0.5f);


    public PlayScreen() {
        super();

        playerController = new PlayerController( 100, 100, 0, 4, 10 );
        uiController = new UIController( playerController );
        cameraController = new CameraController(25, 25);

        batch = new SpriteBatch(  );
        shapeRenderer = new ShapeRenderer(  );

        mapController = new MapController( 0, 0, mapWidth, mapHeight );

        clickController = new MouseClickController();
        Gdx.input.setInputProcessor( clickController );
        clickController.addListener( this );

        unitController = new UnitController( playerController );
        unitController.addUnit( com.redtoorange.warbound.units.UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  25, 25 ) ) );
        unitController.addUnit( com.redtoorange.warbound.units.UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  25, 20 )  ) );
        unitController.addUnit( com.redtoorange.warbound.units.UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  20, 25 )  ) );
        unitController.addUnit( UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  20, 20 )  ) );

        buildingController = new BuildingController( playerController, mapController, cameraController );
    }

    /**
     *
     * @param delta
     */
    @Override
    public void render( float delta ) {
        super.render( delta );

        update( delta );
        draw();

        if( Gdx.input.isKeyJustPressed( Input.Keys.ESCAPE ) ) {
            switch ( controlState ){
                case PLACING_BUILDING:
                    buildingController.cancelPlacing();
                    controlState = ControlState.IDLE;
                    break;
                default:
                    Gdx.app.exit();
            }
        }
        if( Gdx.input.isKeyJustPressed( Input.Keys.F )) {
            buildingController.beginPlacing( BuildingFactory.BuildFarm( buildingController ) );
            controlState = ControlState.PLACING_BUILDING;
        }
    }

    /**
     *
     * @param deltaTime
     */
    private void update( float deltaTime ){
        mapController.update( deltaTime );
        uiController.update( deltaTime );

        cameraController.handleInput( deltaTime );
        unitController.update( deltaTime );
        buildingController.update( deltaTime );
    }

    /**
     *
     */
    private void draw( ){
        cameraController.update();

        batch.setProjectionMatrix( cameraController.combineMatrix() );
        batch.begin();

        mapController.draw( batch );
        unitController.draw( batch );
        buildingController.draw( batch );

        batch.end();

//        drawDebugGrid();

        if( controlState == ControlState.SELECTING )
            renderSelectionBox();
        if( controlState == ControlState.UNITS_SELECTED )
            unitController.renderSelected( cameraController, selectionBoxColor);
        if( controlState == ControlState.BUILDING_SELECTED )
            buildingController.renderSelected( cameraController, selectionBoxColor);

        uiController.draw();
    }



    /**
     *
     * @param width
     * @param height
     */
    @Override
    public void resize( int width, int height ) {
        super.resize( width, height );
        cameraController.resize( width, height );
    }

    /**
     *
     */
    @Override
    public void show() {
        super.show();
    }


    @Override
    public boolean touchDown( int screenX, int screenY, int pointer, int button ) {
        if( button == Input.Buttons.LEFT ){
            switch( controlState ){
                case IDLE:
                    startTouch = cameraController.getMouseWorldPosition();
                    endTouch = cameraController.getMouseWorldPosition();
                    controlState = ControlState.SELECTING;
                    break;

                case SELECTING:
                    break;

                case UNITS_SELECTED:
                    unitController.deselectUnits();

                    startTouch = cameraController.getMouseWorldPosition();
                    endTouch = cameraController.getMouseWorldPosition();
                    controlState = ControlState.SELECTING;
                    break;

                case BUILDING_SELECTED:
                    buildingController.deselectBuilding();

                    startTouch = cameraController.getMouseWorldPosition();
                    endTouch = cameraController.getMouseWorldPosition();
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

                case SELECTING:
                    controlState = ControlState.IDLE;
                    break;

                case UNITS_SELECTED:
                    MapTile goal = mapController.getTileByWorldPos( cameraController.getMouseWorldPosition() );
                    if( goal != null)
                        unitController.giveMoveOrder( goal );

                    unitController.deselectUnits();
                    controlState = ControlState.IDLE;
                    break;

                case BUILDING_SELECTED:
                    buildingController.deselectBuilding();
                    controlState = ControlState.IDLE;
                    break;

                case PLACING_BUILDING:
                    buildingController.cancelPlacing();
                    controlState = ControlState.IDLE;
                    break;

                default:
                    break;
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
                    endTouch = cameraController.getMouseWorldPosition();

                    if( unitController.selectUnits( startTouch, endTouch ) )
                        controlState = ControlState.UNITS_SELECTED;
                    else if( buildingController.selectBuilding( startTouch, endTouch ) )
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
        if( controlState == ControlState.SELECTING ) {
            endTouch = cameraController.getMouseWorldPosition();
        }

        return false;
    }



    /**
     *
     */
    private void renderSelectionBox() {
        shapeRenderer.setProjectionMatrix(cameraController.combineMatrix());
        shapeRenderer.begin( ShapeRenderer.ShapeType.Filled );
        shapeRenderer.setColor( selectionBoxColor );

        //Render the four corners
        shapeRenderer.rectLine(
                startTouch.x, startTouch.y,
                endTouch.x, startTouch.y , LINE_WIDTH );
        shapeRenderer.rectLine(
                endTouch.x, startTouch.y,
                endTouch.x, endTouch.y, LINE_WIDTH );
        shapeRenderer.rectLine(
                startTouch.x, startTouch.y,
                startTouch.x, endTouch.y, LINE_WIDTH );
        shapeRenderer.rectLine(
                startTouch.x, endTouch.y,
                endTouch.x, endTouch.y, LINE_WIDTH );


        //Render the lines between the corners
        float avg = LINE_WIDTH/2f;
        shapeRenderer.rect(
                startTouch.x - avg, startTouch.y - avg,
                LINE_WIDTH, LINE_WIDTH );
        shapeRenderer.rect(
                endTouch.x - avg, endTouch.y - avg,
                LINE_WIDTH, LINE_WIDTH );
        shapeRenderer.rect(
                endTouch.x - avg, startTouch.y - avg,
                LINE_WIDTH, LINE_WIDTH );
        shapeRenderer.rect(
                startTouch.x - avg, endTouch.y - avg,
                LINE_WIDTH, LINE_WIDTH );

        shapeRenderer.end();
    }

    /**
     * Draw black debug lines along the world grid squares.
     */
    private void drawDebugGrid() {
        shapeRenderer.setProjectionMatrix( cameraController.combineMatrix() );
        shapeRenderer.begin( ShapeRenderer.ShapeType.Line );
        shapeRenderer.setColor( gridLineColor );

        for( int x = 0; x < mapWidth; x++){
            for(int y = 0; y < mapHeight; y++){
                shapeRenderer.rect( x, y, 1, 1 );
            }
        }

        shapeRenderer.end();
    }
}

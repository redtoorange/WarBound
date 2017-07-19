package com.redtoorange.warbound.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.*;
import com.redtoorange.warbound.controllers.*;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;

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

    private ControlState controlState = ControlState.IDLE;

    private Unit p;

    public PlayScreen() {
        super();

        cameraController = new CameraController(25, 25);

        batch = new SpriteBatch(  );
        shapeRenderer = new ShapeRenderer(  );

        mapController = new MapController( 0, 0, 50, 50 );

        clickController = new MouseClickController();
        Gdx.input.setInputProcessor( clickController );
        clickController.addListener( this );

        unitController = new UnitController();
        unitController.addUnit( UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  25, 25 ) ) );
        unitController.addUnit( UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  25, 20 )  ) );
        unitController.addUnit( UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  20, 25 )  ) );
        unitController.addUnit( UnitFactory.BuildFootman( unitController, mapController.getTileByWorldPos(  20, 20 )  ) );

        buildingController = new BuildingController( mapController, cameraController );
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

        cameraController.handleInput( deltaTime );
        unitController.update( deltaTime );
        buildingController.update( deltaTime );
    }

    /**
     *
     */
    private void draw( ){
        cameraController.update();

        drawDebugGrid();

        batch.setProjectionMatrix( cameraController.combineMatrix() );
        batch.begin();

        mapController.draw( batch );
        unitController.draw( batch );

        buildingController.draw( batch );

        batch.end();

        if( selecting )
            renderSelectionBox();
    }

    /**
     * Draw black debug lines along the world grid squares.
     */
    private void drawDebugGrid() {
        shapeRenderer.setProjectionMatrix( cameraController.combineMatrix() );
        shapeRenderer.begin( ShapeRenderer.ShapeType.Line );
        shapeRenderer.setColor( Color.BLACK );

        for( int x = 0; x < Constants.WIDTH_UNITS; x++){
            for(int y = 0; y < Constants.HEIGHT_UNITS; y++){
                shapeRenderer.rect( x, y, 1, 1 );
            }
        }

        shapeRenderer.end();
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

    Vector2 startTouch;
    Vector2 endTouch;
    boolean selecting = false;
    Array<Unit> units;

    private void deselectUnits(){
        if( units != null){
            for(Unit u : units){
                u.select( false );
            }
            units = null;
        }
    }

    @Override
    public boolean touchDown( int screenX, int screenY, int pointer, int button ) {
        if( button == Input.Buttons.LEFT ){
            switch( controlState ){
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
                case PLACING_BUILDING:
                    buildingController.cancelPlacing();
                    controlState = ControlState.IDLE;
                    break;

                default:
                    break;
            }
        }


        if( button == Input.Buttons.LEFT && (units == null || units.size == 0 )){
            deselectUnits();

            selecting = true;
            startTouch = cameraController.getMouseWorldPosition();
            endTouch = cameraController.getMouseWorldPosition();
        }
        else if( button == Input.Buttons.LEFT && units != null && units.size > 0 ){
            MapTile goal = mapController.getTileByWorldPos( cameraController.getMouseWorldPosition() );
            if( goal != null)
                for ( Unit u : units )
                    u.giveOrder( new MoveOrder( goal ) );

            deselectUnits();
        }

        return false;
    }

    @Override
    public boolean touchUp( int screenX, int screenY, int pointer, int button ) {
        if( selecting ) {

            endTouch = cameraController.getMouseWorldPosition();

            units = unitController.selectUnits( startTouch, endTouch );
            for ( Unit u : units ) {
                u.select( true );
            }

            selecting = false;
        }
        return false;
    }

    @Override
    public boolean touchDragged( int screenX, int screenY, int pointer ) {
        if( selecting ) {
            endTouch = cameraController.getMouseWorldPosition();
        }

        return false;
    }

    private float LINE_WIDTH = 0.1f;

    private void renderSelectionBox() {
        shapeRenderer.setProjectionMatrix(cameraController.combineMatrix());
        shapeRenderer.begin( ShapeRenderer.ShapeType.Filled );
        shapeRenderer.setColor(Color.CHARTREUSE );

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
}

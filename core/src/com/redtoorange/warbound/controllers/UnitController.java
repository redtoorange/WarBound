package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.ai.MoveOrder;
import com.redtoorange.warbound.units.Unit;
import com.redtoorange.warbound.map.MapTile;

/**
 * UnitController.java - Handle AI for all units.
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public class UnitController {
    public static String TAG = UnitController.class.getSimpleName();

    private PlayerController owner;

    private Array<Unit> units;
    private Array<Unit> selectedUnits;
    private ShapeRenderer shapeRenderer;

    public UnitController( PlayerController owner ){
        this.owner = owner;

        units = new Array< Unit >(  );
        selectedUnits = new Array< Unit >(  );

        shapeRenderer = new ShapeRenderer(  );
    }


    public void addUnit( Unit u){
        if( !units.contains( u, true ))
            units.add( u );
    }

    public void removeUnit( Unit u){
        units.removeValue( u, true );
    }

    public void update( float deltaTime ){
        for( Unit u : units )
            u.update( deltaTime );
    }

    public void draw( SpriteBatch batch ){
        for(Unit u : units)
            u.draw( batch );
    }

    /**
     *
     * @param start
     * @param end
     * @return
     */
    public boolean selectUnits( Vector2 start, Vector2 end){
        float x = Math.min( start.x, end.x );
        float y = Math.min( start.y, end.y );

        float width = Math.max( start.x, end.x ) - x;
        float height = Math.max( start.y, end.y ) - y;

        Rectangle selectionRect = new Rectangle( x, y, width, height);

        for( Unit u : units){
            if( selectionRect.overlaps( u.getBoundingBox() ) )
                selectedUnits.add( u );
        }

        return (selectedUnits.size > 0);
    }


    /**
     *
     */
    public void deselectUnits(){
        for(Unit u : selectedUnits)
            u.select( false );

        selectedUnits.clear();
    }

    /**
     *
     * @param goal
     */
    public void giveMoveOrder( MapTile goal){
        for ( Unit u : selectedUnits )
            u.giveOrder( new MoveOrder( goal ) );
    }

    /**
     *
     * @param cameraController
     * @param selectionBoxColor
     */
    public void renderSelected( CameraController cameraController, Color selectionBoxColor) {
        Gdx.gl.glLineWidth( 2f );
        shapeRenderer.setProjectionMatrix( cameraController.combineMatrix() );
        shapeRenderer.begin( ShapeRenderer.ShapeType.Line );
        shapeRenderer.setColor( selectionBoxColor );


        for( Unit u : selectedUnits){
            Rectangle box = u.getBoundingBox();

            shapeRenderer.rect(box.x, box.y, box.width, box.height );
        }

        shapeRenderer.end();
    }

    public PlayerController getOwner() {
        return owner;
    }
}

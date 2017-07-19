package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.Unit;

/**
 * UnitController.java - Handle AI for all units.
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public class UnitController {
    private Array<Unit> units;

    public UnitController(){
        units = new Array< Unit >(  );
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

    public Array<Unit> selectUnits( Vector2 start, Vector2 end){
        float x = Math.min( start.x, end.x );
        float y = Math.min( start.y, end.y );
        float width = Math.max( start.x, end.x ) - x;
        float height = Math.max( start.y, end.y ) - y;

        Rectangle selectionRect = new Rectangle( x, y, width, height);

        System.out.println( "Pos: " + selectionRect.getX() + ", " + selectionRect.getY());
        System.out.println( "Size: " + selectionRect.getWidth() + ", " + selectionRect.getHeight() + "\n");



        Array<Unit> selectedUnits = new Array< Unit >(  );

        for( Unit u : units){
            if( selectionRect.contains( u.getBoundingBox() ) )
                selectedUnits.add( u );
        }

        return selectedUnits;
    }
}

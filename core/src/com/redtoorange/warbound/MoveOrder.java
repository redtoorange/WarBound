package com.redtoorange.warbound;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.ai.AStarSearch;
import com.redtoorange.warbound.map.MapTile;

/**
 * MoveOrder.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public class MoveOrder extends UnitOrder {
    private MapTile targetTile;
    private MapTile nextTile;
    private Unit unit;
    private Vector2 deltaVelocity = new Vector2( 0, 0 );

    private Array<MapTile> path;

    public MoveOrder( MapTile targetTile ){
        this.targetTile = targetTile;
        completed = false;
    }

    @Override
    public void executed( float deltaTime ) {
        if( unit != null && !completed){
            if( arrivedAtNext() ) {
                unit.move( nextTile.getWorldPosition() );
                unit.setCurrentTile( nextTile );
                calculateNextTile();
            }

            if( nextTile != null && !completed ){
                moveTowardNext( deltaTime );
            }
        }
    }

    @Override
    public void cancelled() {
        System.out.println( "Order cancelled" );
        if( nextTile != null)
            nextTile.select( false );

        if( path != null && path.size > 0)
            for(MapTile t : path)
                t.select( false );

        if( unit != null )
            unit.move( unit.getCurrentTile().getWorldPosition() );
    }

    @Override
    public void recieved( Unit unit ) {
        this.unit = unit;
        calculateNewPath();
    }

    private void calculateNewPath(){
        if( targetTile.blocked() )
            targetTile = targetTile.getEmptyNeighbor();

        path = new AStarSearch( targetTile.getController(), unit.getCurrentTile(), targetTile ).path;

        for(MapTile t : path){
            t.select( true );
        }

        calculateNextTile();
    }

    private void calculateNextTile(){
        if( path != null && path.size >  0) {
            nextTile = path.pop();

            if( nextTile.blocked() )
                calculateNewPath();
            else
                deltaVelocity.set( nextTile.getWorldPosition().sub( unit.getPosition() ) );
        }
        else{
            completed();
        }
    }

    private void moveTowardNext( float deltaTime ){
        Vector2 amount = new Vector2( deltaVelocity );
        amount.scl( deltaTime * unit.getSpeed() );
        unit.translate( amount );
    }

    private boolean arrivedAtNext(){
        float dist = Math.abs( nextTile.getWorldPosition().dst( unit.getPosition() ) );
        boolean atNext = dist < 0.05f;

        if( atNext )
            nextTile.select( false );

        return atNext;
    }

    @Override
    public void completed() {
        System.out.println( "Completed move order!" );
        deltaVelocity.set( 0, 0 );

        path = null;
        nextTile = null;
        completed = true;
    }
}

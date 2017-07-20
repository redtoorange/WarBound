package com.redtoorange.warbound.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.Unit;

/**
 * MoveOrder.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
//TODO: Have the unit finish the move to the next tile, then cancel the order.
public class MoveOrder extends UnitOrder {
    enum MoveState{
        COMPLETED, MOVING, ARRIVED, CANCELLED, BLOCKED, NEW, RECIEVED
    }

    private MoveState state = MoveState.NEW;
    private MapTile destination;
    private MapTile nextTile;
    private Unit unit;
    private Vector2 deltaVelocity = new Vector2( 0, 0 );

    private Array<MapTile> path;


    public MoveOrder( MapTile destination ){
        this.destination = destination;
        completed = false;
    }

    @Override
    public void executed( float deltaTime ) {
        switch( state ){
            case NEW:
                return;
            case RECIEVED:
                calculateNewPath();
                break;
            case MOVING:
                moveTowardNext( deltaTime );
                break;
            case CANCELLED:
                moveTowardNext( deltaTime );
                break;
            case ARRIVED:
                unit.move( nextTile.getWorldPosition() );
                unit.setCurrentTile( nextTile );
                calculateNextTile();
                break;
        }
    }

    private void moveTowardNext( float deltaTime ){
        Vector2 amount = new Vector2( deltaVelocity );
        amount.scl( deltaTime * unit.getSpeed() );
        unit.translate( amount );

        if( hasArrivedAtNext() ) {
            if ( state == MoveState.MOVING )
                state = MoveState.ARRIVED;
            else if( state == MoveState.CANCELLED) {
                unit.move( nextTile.getWorldPosition() );
                unit.setCurrentTile( nextTile );
                completed();
            }
        }
    }


    private void updateFacing(){
        Vector2 cur = unit.getCurrentTile().getWorldPosition();
        Vector2 nex = nextTile.getWorldPosition();

        cur.sub( nex ).nor();

        if( cur.x < 0 ){
            //moving right
            if( cur.y < 0 ){
                //moving up
                unitFacing = Facing.NORTH_EAST;
            }
            else if( cur.y > 0){
                //moving down
                unitFacing = Facing.SOUTH_EAST;
            }
            else{
                //flat line
                unitFacing = Facing.EAST;
            }
        }
        else if( cur.x > 0){
            //moving left
            if( cur.y < 0 ){
                //moving up
                unitFacing = Facing.NORTH_WEST;
            }
            else if( cur.y > 0){
                //moving down
                unitFacing = Facing.SOUTH_WEST;
            }
            else{
                //flat line
                unitFacing = Facing.WEST;
            }
        }
        else{
            if( cur.y < 0 ){
                //moving up
                unitFacing = Facing.NORTH;
            }
            else if( cur.y > 0){
                //moving down
                unitFacing = Facing.SOUTH;
            }
        }
    }

    //TODO: Fix this, there is a jumping back when the order is cancelled.
    @Override
    public void cancelled() {
        state = MoveState.CANCELLED;
    }

    @Override
    public void received( Unit unit ) {
        this.unit = unit;
        state = MoveState.RECIEVED;
    }

    private void calculateNextTile(){
        if( path != null && path.size >  0) {
            nextTile = path.pop();

            if( nextTile.blocked() )
                calculateNewPath();
            else {
                nextTile.setOccupier( unit );
                deltaVelocity.set( nextTile.getWorldPosition().sub( unit.getPosition() ) ).nor();
            }

            state = MoveState.MOVING;
            updateFacing();
        }
        else{
            completed();
        }
    }

    private void calculateNewPath(){
        if( destination.blocked() )
            destination = destination.getEmptyNeighbor();

        path = new AStarSearch( destination.getController(), unit.getCurrentTile(), destination ).path;

        calculateNextTile();
    }


    private boolean hasArrivedAtNext(){
        float dist = 0.0f;

        if( nextTile != null )
            Math.abs( nextTile.getWorldPosition().dst( unit.getPosition() ) );

        return (dist < 0.05f);
    }

    @Override
    public void completed() {
        state = MoveState.COMPLETED;

        deltaVelocity.set( 0, 0 );

        path = null;
        nextTile = null;
        completed = true;
    }
}

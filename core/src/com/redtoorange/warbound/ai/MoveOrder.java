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
public class MoveOrder extends UnitOrder {
    enum MoveState{
        COMPLETED, MOVING, ARRIVED, CANCELLED, BLOCKED, NEW, RECIEVED
    }

    private MoveState state = MoveState.NEW;

    private MapTile targetTile;
    private MapTile nextTile;

    private com.redtoorange.warbound.units.Unit unit;
    private Vector2 deltaVelocity = new Vector2( 0, 0 );

    private Array<MapTile> path;

    public MoveOrder( MapTile targetTile ){
        this.targetTile = targetTile;
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

        if( arrivedAtNext() )
            state = MoveState.ARRIVED;
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


    @Override
    public void cancelled() {
        if( nextTile != null)
            nextTile.setOccupier( null );

        if( unit != null )
            unit.move( unit.getCurrentTile().getWorldPosition() );
    }

    @Override
    public void recieved( Unit unit ) {
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
        if( targetTile.blocked() )
            targetTile = targetTile.getEmptyNeighbor();

        path = new AStarSearch( targetTile.getController(), unit.getCurrentTile(), targetTile ).path;

        calculateNextTile();
    }


    private boolean arrivedAtNext(){
        float dist = Math.abs( nextTile.getWorldPosition().dst( unit.getPosition() ) );

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

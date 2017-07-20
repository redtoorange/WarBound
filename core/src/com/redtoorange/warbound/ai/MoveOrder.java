package com.redtoorange.warbound.ai;

import com.badlogic.gdx.Gdx;
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
    public static final String TAG = MoveOrder.class.getSimpleName();
    private static final float ARRIVAL_THRESHOLD = 0.05f;

    enum MoveState{
        COMPLETED, MOVING, ARRIVED, CANCELLING, BLOCKED, NEW, RECIEVED
    }

    private MoveState state;

    private Unit unit;
    private Vector2 deltaVelocity = new Vector2( 0, 0 );


    private Array<MapTile> path;
    private MapTile destination;
    private MapTile candidate;
    private MapTile nextTile;


    public MoveOrder( MapTile destination ){
        this.destination = destination;
        this.state = MoveState.NEW;

    }

    @Override
    public void executeOrder( float deltaTime ) {
        switch( state ){
            case NEW:
                return;
            case RECIEVED:
                calculateNextTile();
                break;

            case MOVING: case CANCELLING:
                moveTowardNext( deltaTime );
                break;

            case ARRIVED:
                calculateNextTile();
                break;
        }
    }

    private void moveTowardNext( float deltaTime ){
        Vector2 amount = new Vector2( deltaVelocity );
        amount.scl( deltaTime * unit.getSpeed() );
        unit.translate( amount );

        if( hasArrivedAtNext() ) {

            if( nextTile != null ) {
                unit.move( nextTile.getWorldPosition() );
                unit.setCurrentTile( nextTile );
            }

            switch ( state ){
                case MOVING:
                    state = MoveState.ARRIVED;
                    break;

                case CANCELLING:
                    completeOrder();
                    break;
            }
        }
    }

    private void updateFacing(){
        if( deltaVelocity.x > 0 ){
            //moving right
            if( deltaVelocity.y > 0 ){
                //moving up
                unitFacing = Facing.NORTH_EAST;
            }
            else if( deltaVelocity.y < 0){
                //moving down
                unitFacing = Facing.SOUTH_EAST;
            }
            else{
                //flat line
                unitFacing = Facing.EAST;
            }
        }
        else if( deltaVelocity.x < 0){
            //moving left
            if( deltaVelocity.y > 0 ){
                //moving up
                unitFacing = Facing.NORTH_WEST;
            }
            else if( deltaVelocity.y < 0){
                //moving down
                unitFacing = Facing.SOUTH_WEST;
            }
            else{
                //flat line
                unitFacing = Facing.WEST;
            }
        }
        else{
            if( deltaVelocity.y > 0 ){
                //moving up
                unitFacing = Facing.NORTH;
            }
            else if( deltaVelocity.y < 0){
                //moving down
                unitFacing = Facing.SOUTH;
            }
        }
    }

    @Override
    public void cancelOrder() {
        if( state != MoveState.COMPLETED)
            state = MoveState.CANCELLING;
    }

    @Override
    public void receiveOrder( Unit unit ) {
        this.unit = unit;
        state = MoveState.RECIEVED;
    }

    private void calculateNextTile(){
        //Create a new path
        calculateNewPath();

        //if the path's size is greater than 0
        if( path != null && path.size >  0) {
            nextTile = path.pop();
            nextTile.setOccupier( unit );

            deltaVelocity.set( nextTile.getWorldPosition().sub( unit.getPosition() ) ).nor();
            state = MoveState.MOVING;
            updateFacing();
        }
        else{
            completeOrder();
        }
    }

    /**
     * Calculate a new path from the unit's current tile to the destination, if the destination
     * is currently blocked, then a new empty tile will be selected.
     */
    private void calculateNewPath(){
        Gdx.app.log(TAG, "Calculating Path");
        if( (candidate != null && unit.getCurrentTile() == candidate && destination.blocked()) ||
                unit.getCurrentTile() == destination) {
            path = null;
        }

        else {
            candidate = destination;

            if ( candidate.blocked() && candidate.getOccupier() != this )
                candidate = destination.getEmptyNeighbor();

            if( unit.getCurrentTile() == candidate )
                path = null;
            else
                path = new AStarSearch( destination.getController(), unit.getCurrentTile(), candidate ).path;
        }

        //Log path data
        if( path != null)
            Gdx.app.log(TAG, "Path size: " + path.size);
        else
            Gdx.app.log(TAG, "Path is null ");
    }


    /**
     * Has the distance from the nextTile and the unit's currentTile been closed to less than the threshold.
     * @return  true if less than threshold.
     */
    private boolean hasArrivedAtNext(){
        if( nextTile == null )
            return true;

        float dist = Math.abs( nextTile.getWorldPosition().dst( unit.getPosition() ) );
        return (dist < ARRIVAL_THRESHOLD);
    }

    @Override
    protected void completeOrder() {
        state = MoveState.COMPLETED;

        deltaVelocity.set( 0, 0 );

        path = null;
        nextTile = null;
    }

    @Override
    public boolean isCompleted() {
        return state == MoveState.COMPLETED;
    }
}

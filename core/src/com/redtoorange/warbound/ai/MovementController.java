package com.redtoorange.warbound.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.Unit;

/**
 * MovementController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/20/2017
 */
public class MovementController {
    public static final String TAG = MovementController.class.getSimpleName();

    public Facing unitFacing = Facing.SOUTH;

    private static final float ARRIVAL_THRESHOLD = 0.05f;

    enum MoveState{
        IDLE, MOVING, ARRIVED, NEW_ORDER
    }

    private MoveState state;

    private Unit owner;
    private Vector2 deltaVelocity = new Vector2( 0, 0 );

    private Array<MapTile> path;
    private MapTile destination;
    private MapTile candidate;
    private MapTile nextTile;

    public MovementController( Unit owner ){
        this.owner = owner;
        this.state = MoveState.IDLE;
    }

    public void execute( float deltaTime ){
        switch( state ){
            case IDLE:
                return;

            case NEW_ORDER:
                calculateNextTile();
                break;

            case MOVING:
                moveTowardNext( deltaTime );
                break;

            case ARRIVED:
                calculateNextTile();
                break;
        }
    }

    public void setDestination( MapTile destination ){
        this.destination = destination;
        state = MoveState.NEW_ORDER;
    }

    private void moveTowardNext( float deltaTime ){
        Vector2 amount = new Vector2( deltaVelocity );
        amount.scl( deltaTime * owner.getSpeed() );
        owner.translate( amount );

        if( hasArrivedAtNext() ) {

            if( nextTile != null ) {
                owner.move( nextTile.getWorldPosition() );
                owner.setCurrentTile( nextTile );
            }

            switch ( state ){
                case MOVING:
                    state = MoveState.ARRIVED;
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

    private void calculateNextTile(){
        //Create a new path
        calculateNewPath();

        //if the path's size is greater than 0
        if( path != null && path.size >  0) {
            nextTile = path.pop();
            nextTile.setOccupier( owner );

            deltaVelocity.set( nextTile.getWorldPosition().sub( owner.getPosition() ) ).nor();
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
        if( (candidate != null && owner.getCurrentTile() == candidate && destination.blocked()) ||
                owner.getCurrentTile() == destination) {
            path = null;
        }

        else {
            candidate = destination;

            if ( candidate.blocked() && candidate.getOccupier() != this )
                candidate = destination.getEmptyNeighbor();

            if( owner.getCurrentTile() == candidate )
                path = null;
            else
                path = new AStarSearch( destination.getController(), owner.getCurrentTile(), candidate ).path;
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

        float dist = Math.abs( nextTile.getWorldPosition().dst( owner.getPosition() ) );
        return (dist < ARRIVAL_THRESHOLD);
    }


    protected void completeOrder() {
        state = MoveState.IDLE;

        deltaVelocity.set( 0, 0 );

        path = null;
        nextTile = null;
    }

    public boolean isIdle(){
        return state == MoveState.IDLE;
    }
}

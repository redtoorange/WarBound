package com.redtoorange.warbound.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
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
    private MapTile nextTile;
    private MapTile prevTile;

    public MovementController( Unit owner ){
        this.owner = owner;
        this.state = MoveState.IDLE;
    }

    public void execute( float deltaTime ){
        switch( state ){
            case IDLE:
                if( owner.getCurrentOrder() == UnitOrder.MOVE )
                    owner.setCurrentOrder( UnitOrder.IDLE );
                return;

            case NEW_ORDER:
                if( nextTile == null)
                    calculateNextTile();
                else
                    moveTowardNext( deltaTime );
                break;

            case MOVING:
                moveTowardNext( deltaTime );
                break;

            case ARRIVED:
                calculateNextTile();
                break;
        }

        if( Gdx.input.isKeyJustPressed( Input.Keys.K )) {
            completeOrder();
        }
    }

    public void setDestination( MapTile destination ){
        this.destination = destination;
        tempDestination = destination;
        state = MoveState.NEW_ORDER;
    }

    private void moveTowardNext( float deltaTime ){
        float x = MathUtils.lerp( prevTile.getWorldPosition().x, nextTile.getWorldPosition().x, deltaTime );
        float y = MathUtils.lerp( prevTile.getWorldPosition().y, nextTile.getWorldPosition().y, deltaTime );

        Vector2 amount = new Vector2( deltaVelocity.x, deltaVelocity.y );
        amount.scl( deltaTime * owner.getSpeed() );
        owner.translate( amount );

        owner.move( new Vector2( x, y ) );

        if( hasArrivedAtNext() ) {

            if( nextTile != null ) {
                owner.move( nextTile.getWorldPosition() );
                nextTile = null;
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
        deltaVelocity.set( 0, 0 );

        //Create a new path
        calculateNewPath();


        //if the path's size is greater than 0
        if( path != null && path.size >  0) {
            nextTile = path.pop();

            deltaVelocity.set( nextTile.getWorldPosition().sub( owner.getCurrentTile().getWorldPosition() ) ).nor();

            prevTile = owner.getCurrentTile();
            owner.setCurrentTile( nextTile );

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
    MapTile tempDestination;

    private void calculateNewPath(){
//        Gdx.app.log(TAG, "Calculating Path");
        if(owner.getCurrentTile() == destination){
//            Gdx.app.log(TAG, "Owner is at destination");
            path = null;
        }
        else if( owner.getCurrentTile() == tempDestination && destination.blocked()){
//            Gdx.app.log(TAG, "Owner is at temp destination, destination was blocked.");
            path = null;
        }
        else {
            if ( destination.blocked() && destination.getOccupier() != owner ) {
//                Gdx.app.log(TAG, "Destination is blocked, finding a new destination.");
                Vector2 direction = owner.getCurrentTile().getWorldPosition().sub( destination.getWorldPosition() ).nor();
                tempDestination = destination.getEmptyNeighbor( direction.x, direction.y, owner);
            }
            else{
                tempDestination = destination;
            }

            if(owner.getCurrentTile() == destination){
//                Gdx.app.log(TAG, "Owner is at destination");
                path = null;
            }
            else if( owner.getCurrentTile() == tempDestination && destination.blocked()){
//                Gdx.app.log(TAG, "Owner is at temp destination, destination was blocked.");
                path = null;
            }
            else if( tempDestination == null) {
                path = null;
//                Gdx.app.log(TAG, "No viable route.");
            }
            else {
//                Gdx.app.log(TAG, "Path Calculated");
                path = new AStarSearch( tempDestination.getController(), owner.getCurrentTile(), tempDestination ).path;
            }
        }

        //Log path data
//        if( Constants.DEBUGGING ){
//            if( path != null)
//                Gdx.app.log(TAG, "Path size: " + path.size);
//            else
//                Gdx.app.log(TAG, "Path is null ");
//        }
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


    private void completeOrder() {
        state = MoveState.IDLE;

        deltaVelocity.set( 0, 0 );

        path = null;
        nextTile = null;
    }


    public boolean isIdle(){
        return state == MoveState.IDLE;
    }

    /**
     * @return  Unit's current path, null if the unit is IDLE without a path.
     */
    public Array< MapTile > getPath() {
        return path;
    }

    public void debuggingInfo(){
        System.out.println( "Path: " + path );
        System.out.println( "\tDestination: " + destination);
        System.out.println( "\tTempDestination: " + tempDestination);
        System.out.println( "\tNext Tile: " + nextTile);
        System.out.println("\tCurrent State: " + state);
        System.out.println("\tDelta Velocity: " + deltaVelocity);

    }
}

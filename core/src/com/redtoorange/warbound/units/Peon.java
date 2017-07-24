package com.redtoorange.warbound.units;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.Constants;
import com.redtoorange.warbound.ai.UnitOrder;
import com.redtoorange.warbound.buildings.Building;
import com.redtoorange.warbound.map.MapTile;

/**
 * Peon.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class Peon extends Unit {
    public static final String TAG = Peon.class.getSimpleName();

    private float animationTime = 0.0f;
    private Animation currentAnimation;

    private Animation[] animations;
    private TextureAtlas textureAtlas;
    private boolean flipped = false;




    private static final int NORTH = 0, N_EAST = 1, EAST = 2, S_EAST = 3, SOUTH = 4;

    public Peon( MapTile startTile, TextureRegion texture, UnitController controller ) {
        super( startTile, texture, controller );

        textureAtlas = new TextureAtlas( "units/peon/peon.pack" );
        animations = new Animation[5];

        animations[NORTH] = new Animation( 1 / 8.0f, textureAtlas.findRegions( "peon_n" ), Animation.PlayMode.LOOP_PINGPONG );
        animations[N_EAST] = new Animation( 1 / 8.0f, textureAtlas.findRegions( "peon_ne" ), Animation.PlayMode.LOOP_PINGPONG );
        animations[EAST] = new Animation( 1 / 8.0f, textureAtlas.findRegions( "peon_e" ), Animation.PlayMode.LOOP_PINGPONG );
        animations[S_EAST] = new Animation( 1 / 8.0f, textureAtlas.findRegions( "peon_se" ), Animation.PlayMode.LOOP_PINGPONG );
        animations[SOUTH] = new Animation( 1 / 8.0f, textureAtlas.findRegions( "peon_s" ), Animation.PlayMode.LOOP_PINGPONG );

        updateSpriteFacing();
        sprite.setRegion( currentAnimation.getKeyFrame( animationTime ) );
    }

    protected void updateSpriteFacing() {
        flipped = false;
//        switch ( currentFacing ) {
        switch ( movementController.unitFacing ) {
                case NORTH:
                    currentAnimation = animations[NORTH];
                    break;

                case SOUTH:
                    currentAnimation = animations[SOUTH];
                    break;

                //Flip Pair
                case EAST:
                    currentAnimation = animations[EAST];
                    break;

                case WEST:
                    flipped = true;
                    currentAnimation = animations[EAST];
                    break;

                //Flip Pair
                case NORTH_EAST:
                    currentAnimation = animations[N_EAST];
                    break;

                case NORTH_WEST:
                    flipped = true;
                    currentAnimation = animations[N_EAST];
                    break;

                //Flip Pair
                case SOUTH_EAST:
                    currentAnimation = animations[S_EAST];
                    break;

                case SOUTH_WEST:
                    flipped = true;
                    currentAnimation = animations[S_EAST];
                    break;
            }
    }

    public void draw( SpriteBatch batch ) {
        if( !insideBuilding ){
            sprite.setRegion( currentAnimation.getKeyFrame( animationTime ) );
            sprite.setFlip( flipped, false );
            super.draw( batch );
        }
    }

    float timer = 1.0f;

    public void update( float deltaTime ) {
        super.update( deltaTime );

        if( currentOrder != UnitOrder.IDLE ) {
            updateSpriteFacing();
            animationTime += deltaTime;
        }
        else
            animationTime = 0.0f;

        switch ( currentOrder ){
            case ENTER_BUILDING:
                movementController.execute( deltaTime );
                if( movementController.isIdle())
                    enterBuilding();

                break;

            case CONSTRUCT_BUILDING:
                if( !targetBuilding.isCurrentlyBeingBuilt())
                    exitBuilding();

                break;
            case DEPOSIT:
                timer -= deltaTime;

                if( timer <= 0.0f )
                    exitBuilding();

                break;
        }
    }

    private void enterBuilding() {
        boolean shouldEnter = false;

        //  The building's construction is halted
        if( targetBuilding.inReadyForConstruction() ){
            System.out.println( "**Constructing building**" );
            currentOrder = UnitOrder.CONSTRUCT_BUILDING;
            targetBuilding.beginConstruction( this );
            shouldEnter = true;
        }

        //  The building is COMPLETE and can be entered
        else if( targetBuilding.canBeEntered()){
            System.out.println( "**Depositing at building**" );
            currentOrder = UnitOrder.DEPOSIT;
            timer = Constants.DROP_OFF_TIME;
            shouldEnter = true;
        }

        if( shouldEnter ){
            controller.deselectUnit( this );
            insideBuilding = true;
            setCurrentTile( null );
        }
        else{
            System.out.println( "**Cannot enter building**" );
            currentOrder = UnitOrder.IDLE;
            targetBuilding = null;
        }

    }

    private void exitBuilding() {
        switch( currentOrder){
            case DEPOSIT:
                System.out.println( "**Deposited materials.**" );
                break;
            case CONSTRUCT_BUILDING:
                System.out.println( "**Finished Building.**" );
                break;
        }

        ejectFromBuilding( targetBuilding.getSpotOnPerimeter() );
    }

    private void moveToBuilding( Building building, MapTile destination ){
        targetBuilding = building;

        currentOrder = UnitOrder.ENTER_BUILDING;
        movementController.setDestination( destination );
    }

    @Override
    public void giveMoveOrder( MapTile destination ) {
        if( destination.isOccupied() && destination.getOccupier() instanceof Building){
            moveToBuilding( (Building)destination.getOccupier(), destination );
        }
        else {
            targetBuilding = null;
            super.giveMoveOrder( destination );
        }
    }
}

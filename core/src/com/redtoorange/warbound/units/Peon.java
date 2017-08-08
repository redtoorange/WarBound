package com.redtoorange.warbound.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.buildings.Building;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.ai.UnitOrder;
import com.redtoorange.warbound.utilities.Constants;

/**
 * Peon.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class Peon extends Unit implements Builder {
    public static final String TAG = Peon.class.getSimpleName();
    private static final int NORTH = 0, N_EAST = 1, EAST = 2, S_EAST = 3, SOUTH = 4;
    float timer = 1.0f;
    private float animationTime = 0.0f;
    private Animation currentAnimation;
    private Animation[] animations;
    private TextureAtlas textureAtlas;
    private boolean flipped = false;

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
        switch ( movementComponent.unitFacing ) {
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
        if ( !insideBuilding ) {
            sprite.setRegion( currentAnimation.getKeyFrame( animationTime ) );
            sprite.setFlip( flipped, false );
            super.draw( batch );
        }
    }

    public void update( float deltaTime ) {
        super.update( deltaTime );

        if ( currentOrder != UnitOrder.IDLE ) {
            updateSpriteFacing();
            animationTime += deltaTime;
        } else
            animationTime = 0.0f;

        switch ( currentOrder ) {
            case ENTER_BUILDING:
                movementComponent.execute( deltaTime );
                if ( movementComponent.isIdle() )
                    enterBuilding();

                break;

            case CONSTRUCT_BUILDING:
                //STUB: Do do anything?
                break;
            case DEPOSIT:
                timer -= deltaTime;

                if ( timer <= 0.0f )
                    exitBuilding();

                break;
        }
    }

    @Override
    public void enterBuilding() {
        boolean shouldEnter = false;

        if ( !targetBuilding.isCancelled() ) {
            //  The building's construction is halted
            if ( targetBuilding.isReadyForConstruction() ) {
                Gdx.app.log( TAG, " Constructing building" );
                currentOrder = UnitOrder.CONSTRUCT_BUILDING;
                targetBuilding.beginBuildingConstruction( this );
                shouldEnter = true;
            }

            //  The building is COMPLETE and can be entered
            else if ( targetBuilding.canBeEntered() ) {
                Gdx.app.log( TAG, " Depositing at building" );
                currentOrder = UnitOrder.DEPOSIT;
                timer = Constants.DROP_OFF_TIME;
                shouldEnter = true;
            }
        }

        if ( shouldEnter ) {
            Gdx.app.log( TAG, " Entering building" );
            controller.deselectUnit( this );
            insideBuilding = true;
            setCurrentTile( null );
        } else {
            Gdx.app.log( TAG, " Cannot enter building" );
            currentOrder = UnitOrder.IDLE;
            targetBuilding = null;
        }
    }

    @Override
    public void exitBuilding() {
        switch ( currentOrder ) {
            case DEPOSIT:
                Gdx.app.log( TAG, " Deposited materials" );
                break;
            case CONSTRUCT_BUILDING:
                Gdx.app.log( TAG, " Finished Building" );
                break;
        }
    }

    @Override
    public void moveToBuilding( Building building, MapTile destination ) {
        targetBuilding = building;

        currentOrder = UnitOrder.ENTER_BUILDING;
        movementComponent.setDestination( destination );
    }

    @Override
    public void ejectFromBuilding( MapTile mapTile ) {
        if ( mapTile == null ) {
            System.out.println( "Peon has had it's current tile set to null by the eject command." );
            return;
        }

        setCurrentTile( mapTile );
        move( mapTile.getWorldPosition() );

        exitBuilding();

        insideBuilding = false;
        targetBuilding = null;
        currentOrder = UnitOrder.IDLE;
    }

    @Override
    public void giveMoveOrder( MapTile destination ) {
        if ( destination.isOccupied() && destination.getOccupier() instanceof Building ) {
            moveToBuilding( ( Building ) destination.getOccupier(), destination );
        } else {
            targetBuilding = null;
            super.giveMoveOrder( destination );
        }
    }
}

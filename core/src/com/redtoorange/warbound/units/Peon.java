package com.redtoorange.warbound.units;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.controllers.UnitController;
import com.redtoorange.warbound.map.MapTile;

/**
 * Peon.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class Peon extends Unit {
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
        switch ( currentFacing ) {
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
        sprite.setRegion( currentAnimation.getKeyFrame( animationTime ) );
        sprite.setFlip( flipped, false );
        super.draw( batch );
    }

    public void update( float deltaTime ) {
        super.update( deltaTime );

        if( currentOrder != null) {
            updateSpriteFacing();
            animationTime += deltaTime;
        }
        else
            animationTime = 0.0f;
    }
}

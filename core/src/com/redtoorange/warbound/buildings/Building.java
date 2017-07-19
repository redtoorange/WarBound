package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redtoorange.warbound.GameObject;
import com.redtoorange.warbound.Resource;
import com.redtoorange.warbound.controllers.BuildingController;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;

/**
 * Building.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class Building implements GameObject {
    public static String TAG = Building.class.getSimpleName();

    public enum State{
        PLACING, PRODUCING
    }

    private BuildingController controller;
    private PlayerController owner;

    private String name;
    private MapTile[][] currentTiles;
    private Sprite sprite;
    private int width;
    private int height;
    private boolean validLocations;

    private State state = State.PLACING;

    public Building( String name, TextureRegion texture, int width, int height, BuildingController controller){
        this.name = name;
        sprite = new Sprite( texture );
        sprite.setAlpha( 0.5f );
        sprite.setSize( width, height );
        currentTiles = new MapTile[width][height];

        this.width = width;
        this.height = height;

        this.controller = controller;
        owner = controller.getOwner();
    }

    @Override
    public void update( float deltaTime ) {
        if( state == State.PRODUCING ){
            owner.changeResource( Resource.GOLD, 1 );
        }
    }

    @Override
    public void draw( SpriteBatch batch ) {
//        validatePosition();
        if( validLocations )
            sprite.draw( batch );
    }

    //Try to see if a building can be placed on the hovered tile
    public void setPosition( MapTile hoveredTile ) {
        unpaintTiles();
        MapController mc = controller.getMapController();
        validLocations = true;

        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                int gridX = ( hoveredTile.getMapX() - (width / 2) ) + x;
                int gridY = ( hoveredTile.getMapY() - (height / 2) ) + y;

                currentTiles[x][y] = mc.getTileByGridPos( gridX, gridY );

                //A single null tile invalidates the position
                if (currentTiles[x][y] == null )
                    validLocations = false;
            }
        }

        //All of the tiles were valid, update the sprite's position
        if ( validLocations ) {
            setPosition( currentTiles[0][0].getWorldPosition() );
            paintTiles();
        }

    }

    public void setPosition( Vector2 pos ){
        sprite.setPosition( pos.x, pos.y );
    }

    //Set all tiles to default color
    private void unpaintTiles(){
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                if( currentTiles[x][y] != null ) {
                    currentTiles[x][y].setColor( Color.WHITE );
                }
            }
        }
    }

    //Set the tiles to green if valid or red if blocked
    private void paintTiles(){
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                if( currentTiles[x][y] != null ) {
                    if( currentTiles[x][y].blocked() )
                        currentTiles[x][y].setColor( Color.RED );
                    else
                        currentTiles[x][y].setColor( Color.GREEN );
                }
            }
        }
    }

    public void cancel(){
        unpaintTiles();
    }

    public boolean placeBuilding(){
        boolean success = validLocations && validatePlacement();

        if( success ){
            unpaintTiles();
            sprite.setAlpha( 1.0f );
            state = State.PRODUCING;

            for ( int x = 0; x < width; x++ ) {
                for ( int y = 0; y < height; y++ ) {
                    currentTiles[x][y].setOccupier( this );
                }
            }
        }

        return success;
    }

    private boolean validatePlacement(){
        boolean valid = true;

        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                if( currentTiles[x][y].blocked() )
                    valid = false;
            }
        }

        return valid;
    }

    public Rectangle getBoundingBox(){
        return sprite.getBoundingRectangle();
    }



}

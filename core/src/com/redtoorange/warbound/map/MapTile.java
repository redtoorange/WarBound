package com.redtoorange.warbound.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.Constants;
import com.redtoorange.warbound.GameObject;

/**
 * MapTile.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class MapTile {
    public static final String TAG = MapTile.class.getSimpleName();


    private final MapController controller;
    private final float worldX;
    private final float worldY;
    private final int mapX;
    private final int mapY;

    private Sprite sprite;
    private Array< MapTile > neighbors;
    private TileType type;
    private GameObject occupier;


    public MapTile( float worldX, float worldY, int mapX, int mapY, MapController controller, TileType type ) {
        this.controller = controller;

        this.worldX = worldX;
        this.worldY = worldY;

        this.mapX = mapX;
        this.mapY = mapY;

        this.type = type;

        sprite = new Sprite( controller.getTexture( type ) );
        sprite.setSize( 1, 1 );
        sprite.setPosition( worldX, worldY );
    }

    public Array< MapTile > getNeighbors() {
        return neighbors;
    }

    public void setNeighbors( Array< MapTile > neighbors ) {
        this.neighbors = neighbors;
    }

    public void drawTile( SpriteBatch batch ) {
        if ( Constants.DEBUGGING ) {
            if ( isOccupied() )
                sprite.setColor( Color.RED );
            else
                sprite.setColor( Color.WHITE );
        }

        sprite.draw( batch );
    }

    public void drawDoodad( SpriteBatch batch ) {
        //STUB
    }

    public void update( float deltaTime ) {
        //STUB
    }

    public boolean contains( float x, float y ) {
        return sprite.getBoundingRectangle().contains( x, y );
    }

    @Override
    public String toString() {
        return "Tile at (" + worldX + ", " + worldY + ").";
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public boolean isOccupied() {
        if ( occupier == null )
            return false;

        return true;
    }

    /**
     * @return True if the tile is occupied or is stone.
     */
    public boolean blocked() {
        return isOccupied() || type == TileType.STONE;
    }

    public Vector2 getWorldPosition() {
        return new Vector2( sprite.getX(), sprite.getY() );
    }

    public Vector2 getWorldPositionCenter() {
        return new Vector2(
                sprite.getX() + ( sprite.getWidth() / 2.0f ),
                sprite.getY() + ( sprite.getHeight() / 2.0f ) );
    }

    public MapController getController() {
        return controller;
    }

    public float getCost() {
        return type.cost;
    }

    public MapTile getEmptyNeighbor() {
        MapTile empty = null;

        for ( int i = 0; i < neighbors.size && empty == null; i++ ) {
            if ( !neighbors.get( i ).blocked() )
                empty = neighbors.get( i );
        }

        if ( empty == null ) {
            for ( int i = 0; i < neighbors.size && empty == null; i++ ) {
                empty = neighbors.get( i ).getEmptyNeighbor();
            }
        }

        return empty;
    }

    /**
     * Pretty ham fisted way of finding a free tile with a weighted preference for direction then distance.
     * @param dirX
     * @param dirY
     * @return
     */
    public MapTile getEmptyNeighbor( float dirX, float dirY, GameObject exclude) {
        MapTile empty = null;
        MapTile nextBestCandidate = null;

        //From Below: DONE
        if( dirX == 0 && dirY < 0){
            nextBestCandidate = empty = controller.getTileByGridPos( mapX, mapY-1 );

            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY+1 );
        }
        //From Above: DONE
        else if( dirX == 0 && dirY > 0){
            nextBestCandidate = empty = controller.getTileByGridPos( mapX, mapY+1 );

            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY-1 );
        }
        //From Right: DONE
        else if( dirX > 0 && dirY == 0){
            nextBestCandidate = empty = controller.getTileByGridPos( mapX+1, mapY );

            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY-1 );
        }
        //From Left: DONE
        else if( dirX < 0 && dirY == 0){
            nextBestCandidate = empty = controller.getTileByGridPos( mapX-1, mapY );

            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY-1 );
        }

        //From Top Right: DONE
        else if( dirX > 0 && dirY > 0){
            nextBestCandidate = empty = controller.getTileByGridPos( mapX+1, mapY+1 );

            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY-1 );
        }
        //From Bottom Left: DONE
        else if( dirX < 0 && dirY < 0){
            nextBestCandidate = empty = controller.getTileByGridPos( mapX-1, mapY-1 );

            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY+1 );
        }
        //From Top Left: DONE
        else if( dirX < 0 && dirY > 0){
            nextBestCandidate = empty = controller.getTileByGridPos( mapX-1, mapY+1 );

            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY-1 );
        }
        //From Bottom Right
        else if( dirX > 0 && dirY < 0){
            nextBestCandidate = empty = controller.getTileByGridPos( mapX+1, mapY-1 );

            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY-1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX+1, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX, mapY+1 );
            if( empty == null || (empty.blocked() && empty.getOccupier() != exclude) )
                empty = controller.getTileByGridPos( mapX-1, mapY+1 );
        }

        if (empty == null || (empty.blocked() && empty.getOccupier() != exclude)) {
            empty = nextBestCandidate.getEmptyNeighbor( dirX, dirY, exclude);

            if (empty == null || (empty.blocked() && empty.getOccupier() != exclude)) {
                for ( int i = 0; i < neighbors.size && ( empty == null || ( empty.blocked() && empty.getOccupier() != exclude ) ); i++ ) {
                    empty = neighbors.get( i ).getEmptyNeighbor( dirX, dirY, exclude );
                }
            }
        }

        return empty;
    }

    public MapTile getEmptyOutsideArea( int width, int height, int offsetX, int offsetY){
        MapTile empty = null;


        for( int y = offsetY; y <= height && empty == null; y++){
            for( int x = offsetX; x <= width && empty == null; x++){
                MapTile t = controller.getTileByGridPos( mapX + x, mapY + y );
                if( t != null && !t.blocked())
                    empty = t;
            }
        }

        if( empty == null){
            empty = getEmptyOutsideArea( ++width, ++height, --offsetX, --offsetY );
        }

        return empty;
    }

    /**
     * Set the current tint of the tile's sprite
     *
     * @param color Color to tint the sprite by.
     */
    public void setColor( Color color ) {
        sprite.setColor( color );
    }

    public GameObject getOccupier() {
        return occupier;
    }

    public void setOccupier( GameObject occupier ) {
        this.occupier = occupier;
    }
}

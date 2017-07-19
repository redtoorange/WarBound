package com.redtoorange.warbound.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redtoorange.warbound.GameObject;

/**
 * MapTile.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class MapTile {
    private Array<MapTile> neighbors;
    private TileType type;

    private GameObject occupier = null;


    private MapController controller;

    private float worldX;
    private float worldY;

    private int mapX;
    private int mapY;

    private Sprite sprite;


    public MapTile( float worldX, float worldY, int mapX, int mapY, MapController controller, TileType type){
        this.controller = controller;

        this.worldX = worldX;
        this.worldY = worldY;

        this.mapX = mapX;
        this.mapY = mapY;

        this.type = type;

        sprite = new Sprite( controller.getTexture(type) );
        sprite.setSize( 1, 1 );
        sprite.setPosition( worldX, worldY );
    }

    public void setNeightbors( Array<MapTile> neighbors ){
        this.neighbors = neighbors;
    }

    public Array<MapTile> getNeightbors(){
        return neighbors;
    }

    public void drawTile( SpriteBatch batch ){
        sprite.draw( batch );
    }

    public void drawDoodad( SpriteBatch batch ){
        //STUB
    }

    public void update( float deltaTime ){
        //STUB
    }

    public boolean contains(float x, float y){
        return sprite.getBoundingRectangle().contains( x, y );
    }

    @Override
    public String toString() {
        return "Tile at (" + worldX + ", " + worldY + ").";
    }

    public void select( boolean selected ){
        if( selected == true ){
            sprite.setColor( Color.BLUE );
        }
        else{
            sprite.setColor( Color.WHITE );
        }
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public boolean isOccupied(){
        if(occupier == null)
            return false;

        return true;
    }

    public boolean blocked(){
        return isOccupied() || type == TileType.STONE;
    }

    public void setOccupier( GameObject occupier ){
        this.occupier = occupier;
    }

    public Vector2 getWorldPosition(){
        return new Vector2( sprite.getX(), sprite.getY() );
    }

    public MapController getController() {
        return controller;
    }


    public float getCost(){
        return type.cost;
    }

    public MapTile getEmptyNeighbor(){
        MapTile empty = null;

        for( int i = 0; i < neighbors.size && empty == null; i++){
            if( !neighbors.get( i ).isOccupied())
                empty = neighbors.get( i );
        }

        if( empty == null ){
            for( int i = 0; i < neighbors.size && empty == null; i++){
                empty = neighbors.get( i ).getEmptyNeighbor();
            }
        }

        return empty;
    }

    /**
     * Set the current tint of the tile's sprite
     * @param color Color to tint the sprite by.
     */
    public void setColor( Color color ){
        sprite.setColor( color );
    }
}

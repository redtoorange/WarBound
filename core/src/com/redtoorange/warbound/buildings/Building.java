package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redtoorange.warbound.GameObject;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.Unit;

/**
 * Building.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public class Building implements GameObject {
    public static final String TAG = Building.class.getSimpleName();

    protected BuildingController controller;
    protected PlayerController owner;

    protected String name;
    protected MapTile[][] currentTiles;
    protected Sprite sprite;
    protected int width;
    protected int height;
    protected boolean validLocations;

    protected BuildingState buildingState = BuildingState.PLACING;
    private Unit builder;

    private float amountConstructed = 0.0f;
    private float constructionTime = 5.0f;

    protected boolean canBeEntered = false;

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
    }

    @Override
    public void draw( SpriteBatch batch ) {
        if( validLocations )
            sprite.draw( batch );
    }

    /**Try to see if a building can be placed on the hovered tile*/
    public void setPosition( MapTile hoveredTile ) {
        unpaintTiles();
        MapController mc = controller.getOwner().getMapController();
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
    protected void unpaintTiles(){
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                if( currentTiles[x][y] != null ) {
                    currentTiles[x][y].setColor( Color.WHITE );
                }
            }
        }
    }

    //Set the tiles to green if valid or red if blocked
    protected void paintTiles(){
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

    public void cancelPlacement(){
        unpaintTiles();
    }

    public void cancelConstruction(){
        System.out.println( "**Cancel this building**" );

        if( builder != null )
            builder.ejectFromBuilding( getSpotOnPerimeter() );

        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                currentTiles[x][y].setOccupier( null );
            }
        }
    }

    /** Commit the building to it's current placement. */
    public boolean placeBuilding(){
        boolean success = validLocations && validatePlacement();

        if( success ){
            unpaintTiles();
            sprite.setAlpha( 1.0f );
            buildingState = BuildingState.CONSTRUCTION_STARTED;

            for ( int x = 0; x < width; x++ ) {
                for ( int y = 0; y < height; y++ ) {
                    currentTiles[x][y].setOccupier( this );
                }
            }
        }

        return success;
    }

    /** Ensure all the tiles the building is on are valid. */
    protected boolean validatePlacement(){
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

    public MapTile getSpotOnPerimeter(){
        return currentTiles[0][0].getEmptyOutsideArea( width, width, -1, -1 );
    }

    public boolean canBeEntered() {
        return buildingState == BuildingState.COMPLETE && canBeEntered;
    }

    public boolean isUnderConstruction(){
        return buildingState == BuildingState.CONSTRUCTION_STARTED;
    }

    public boolean isBeingBuilt(){
        return buildingState == BuildingState.BEING_CONSTRUCTED;
    }

    public BuildingState getBuildingState() {
        return buildingState;
    }

    public void finishConstruction(){
        buildingState = BuildingState.COMPLETE;
    }

    public void setBuildingState( BuildingState buildingState ) {
        this.buildingState = buildingState;
    }

    public void beginConstruction( Unit unit ){
        this.builder = unit;
        buildingState = BuildingState.BEING_CONSTRUCTED;
    }

    public void constructBuilding( float amount ){
        if( isBeingBuilt() ){
            amountConstructed += amount;
            if( amountConstructed >= constructionTime){
                finishConstruction();
            }
        }
    }

    public boolean isComplete(){
        return buildingState == BuildingState.COMPLETE;
    }
}

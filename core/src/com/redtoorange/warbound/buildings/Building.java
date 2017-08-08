package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redtoorange.warbound.GameObject;
import com.redtoorange.warbound.controllers.BuildingController;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.Builder;

/**
 * Building.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public abstract class Building extends GameObject {
    public static final String TAG = Building.class.getSimpleName();
    protected static final int STARTED = 0, PARTIAL = 1, COMPLETE = 2;
    protected BuildingType TYPE = BuildingType.NONE;
    protected TextureRegion[] regions;
    protected BuildingController controller;
    protected PlayerController owner;
    protected String name;
    protected MapTile[][] currentTiles;
    protected Sprite sprite;
    protected int width;
    protected int height;
    protected boolean validLocations;
    protected BuildingState currentState = BuildingState.PLACING;
    protected Builder builder;
    protected float amountConstructed;
    protected float constructionTime;
    protected boolean canBeEntered = false;

    public Building( String name, TextureRegion[] regions, int width, int height, BuildingController controller ) {
        super( controller );

        this.name = name;
        this.regions = regions;

        sprite = new Sprite();

        setRegion( COMPLETE );

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
        switch ( currentState ) {
            case BEING_CONSTRUCTED:
                progressBuildingConstruction( deltaTime );
                break;
        }
    }

    @Override
    public void draw( SpriteBatch batch ) {
        if ( validLocations && sprite.getTexture() != null )
            sprite.draw( batch );
    }

    /** Try to see if a building can be placed on the hovered tile */
    public void setPosition( MapTile hoveredTile ) {
        unpaintTiles();
        MapController mc = controller.getOwner().getMapController();
        validLocations = true;

        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                int gridX = ( hoveredTile.getMapX() - ( width / 2 ) ) + x;
                int gridY = ( hoveredTile.getMapY() - ( height / 2 ) ) + y;

                currentTiles[x][y] = mc.getTileByGridPos( gridX, gridY );

                //A single null tile invalidates the position
                if ( currentTiles[x][y] == null )
                    validLocations = false;
            }
        }

        //All of the tiles were valid, update the sprite's position
        if ( validLocations ) {
            setPosition( currentTiles[0][0].getWorldPosition() );
            paintTiles();
        }
    }

    public void instantBuild( MapTile tile ) {
        setPosition( tile );

        if ( placeBuilding() ) {
            completeBuildingConstruction();
        } else {
            System.out.println( "!!Failed to spawn instant building!!" );
            System.out.println( "\tWorld Pos: " + tile.getWorldPosition() );
            System.out.println( "\tMap Pos: " + tile.getMapX() + "," + tile.getMapY() );
        }
    }

    protected void setPosition( Vector2 pos ) {
        sprite.setPosition( pos.x, pos.y );
    }

    /** Set all tiles to default color */
    protected void unpaintTiles() {
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                if ( currentTiles[x][y] != null ) {
                    currentTiles[x][y].setColor( Color.WHITE );
                }
            }
        }
    }

    /** Set the tiles to green if valid or red if blocked */
    protected void paintTiles() {
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                if ( currentTiles[x][y] != null ) {
                    if ( currentTiles[x][y].blocked() )
                        currentTiles[x][y].setColor( Color.RED );
                    else
                        currentTiles[x][y].setColor( Color.GREEN );
                }
            }
        }
    }

    /** Commit the building to it's current placement. */
    public boolean placeBuilding() {
        boolean success = validLocations && validatePlacement();

        if ( success ) {
            unpaintTiles();

            setRegion( STARTED );
            sprite.setAlpha( 1.0f );
            currentState = BuildingState.CONSTRUCTION_HALTED;

            for ( int x = 0; x < width; x++ ) {
                for ( int y = 0; y < height; y++ ) {
                    currentTiles[x][y].setOccupier( this );
                }
            }

            constructionTime = TYPE.productionTime;
            amountConstructed = 0.0f;
        }

        return success;
    }

    public void cancelPlacement() {
        unpaintTiles();
    }

    /** Ensure all the tiles the building is on are valid. */
    protected boolean validatePlacement() {
        boolean valid = true;

        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                if ( currentTiles[x][y].blocked() )
                    valid = false;
            }
        }

        return valid;
    }

    public Rectangle getBoundingBox() {
        return sprite.getBoundingRectangle();
    }

    public MapTile getCentralTile() {
        return currentTiles[width / 2][height / 2];
    }


    /** Set the builder for the building and begin the construction. */
    public void beginBuildingConstruction( Builder builder ) {
        this.builder = builder;
        currentState = BuildingState.BEING_CONSTRUCTED;
    }

    /** Add to the building's overall progress. */
    public void progressBuildingConstruction( float amount ) {
        if ( isCurrentlyBeingBuilt() ) {
            amountConstructed += amount;
            if ( amountConstructed / constructionTime >= 0.5f )
                sprite.setRegion( regions[PARTIAL] );

            if ( amountConstructed >= constructionTime )
                completeBuildingConstruction();
        }
    }

    /** Update the building's state, eject the builder, update the UI. */
    protected void completeBuildingConstruction() {
        Gdx.app.log( TAG, "Complete!" );

        setRegion( COMPLETE );
        currentState = BuildingState.COMPLETE;

        if ( builder != null ) {
            builder.ejectFromBuilding( getSpotOnPerimeter() );
            builder = null;
        }

        if ( controller.getCurrentBuilding() == this )
            controller.updateUI();
    }


    /** Update the building's state, clear out tiles, eject the builder. */
    public void cancelConstruction() {
        Gdx.app.log( TAG, "Cancelled." );
        currentState = BuildingState.CANCELLED;

        //De-occupy all tiles
        for ( int x = 0; x < width; x++ )
            for ( int y = 0; y < height; y++ )
                currentTiles[x][y].setOccupier( null );

        //Eject the builder on a central tiles
        if ( builder != null ) {
            builder.ejectFromBuilding( getCentralTile() );
            builder = null;
        }
    }

    /** Select an empty spot along the perimeter of the Building. */
    public MapTile getSpotOnPerimeter() {
        return currentTiles[0][0].getEmptyOutsideArea( width, height, -1, -1 );
    }

    public boolean canBeEntered() {
        return currentState == BuildingState.COMPLETE && canBeEntered;
    }

    public boolean isReadyForConstruction() {
        return currentState == BuildingState.CONSTRUCTION_HALTED;
    }

    public boolean isCurrentlyBeingBuilt() {
        return currentState == BuildingState.BEING_CONSTRUCTED;
    }

    public boolean isComplete() {
        return currentState == BuildingState.COMPLETE;
    }

    public boolean isCancelled() {
        return currentState == BuildingState.CANCELLED;
    }

    public BuildingType getType() {
        return TYPE;
    }

    protected void setRegion( int REGION ) {
        if ( REGION <= regions.length - 1 )
            sprite.setRegion( regions[REGION] );
    }
}

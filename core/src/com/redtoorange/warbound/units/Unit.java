package com.redtoorange.warbound.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redtoorange.warbound.GameObject;
import com.redtoorange.warbound.units.ai.MovementComponent;
import com.redtoorange.warbound.utilities.Facing;
import com.redtoorange.warbound.units.ai.UnitOrder;
import com.redtoorange.warbound.buildings.Building;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapTile;

/**
 * Unit.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public abstract class Unit extends GameObject {
    public static String TAG = Unit.class.getSimpleName();

    protected Facing currentFacing = Facing.SOUTH;
    protected UnitOrder currentOrder = UnitOrder.IDLE;

    protected UnitController controller;
    protected PlayerController owner;

    protected float speed = 2.5f;

    protected MovementComponent movementComponent;

    protected MapTile currentTile = null;
    protected Sprite sprite;
    protected boolean selected = false;
    protected boolean insideBuilding = false;
    protected Building targetBuilding;

    public Unit( MapTile startTile, TextureRegion texture, UnitController controller){
        super( controller );

        sprite = new Sprite( texture );
        sprite.setSize( 1, 1 );
        sprite.setPosition( startTile.getWorldPosition().x, startTile.getWorldPosition().y );

        movementComponent = new MovementComponent( this );

        setCurrentTile( startTile );
        this.controller = controller;
        owner = controller.getOwner();
    }

    /** Handles the default IDLE and MOVE orders. */
    public void update( float deltaTime){
        switch ( currentOrder ){
            case IDLE:
                break;
            case MOVE:
                movementComponent.execute( deltaTime );
                if( movementComponent.isIdle())
                    currentOrder = UnitOrder.IDLE;
                break;
        }

    }

    public void draw( SpriteBatch batch){
        sprite.draw( batch );
    }

    public void select( boolean selected ){
        this.selected = selected;
    }

    public Rectangle getBoundingBox(){
        return sprite.getBoundingRectangle();
    }


    /**
     * Order the unit to move to the indicated tile or a close one.
     * @param destination Tile to move to
     */
    public void giveMoveOrder( MapTile destination ){
        currentOrder = UnitOrder.MOVE;
        movementComponent.setDestination( destination );
    }

    public void giveAttackOrder( GameObject target ){
        //STUB
    }

    public void move( Vector2 newPos ){
        sprite.setPosition( newPos.x, newPos.y );
    }

    public void translate( Vector2 amount ){
        sprite.translate( amount.x, amount.y );
    }

    public float getSpeed() {
        return speed;
    }

    public Vector2 getPosition(){
        return new Vector2( sprite.getX(), sprite.getY() );
    }

    public MapTile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile( MapTile newTile ) {
        if( currentTile != null)
            currentTile.setOccupier( null );

        currentTile = newTile;

        if( currentTile != null)
            currentTile.setOccupier( this );
        else
            System.out.println( "Current Tile has been set to NULL" );
    }

    public MovementComponent getMovementComponent() {
        return movementComponent;
    }

    public UnitOrder getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder( UnitOrder currentOrder ) {
        this.currentOrder = currentOrder;
    }

    public boolean isInsideBuilding() {
        return insideBuilding;
    }

    public boolean isSelectable(){
        return !insideBuilding;
    }
}

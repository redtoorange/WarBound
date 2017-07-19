package com.redtoorange.warbound;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redtoorange.warbound.controllers.UnitController;
import com.redtoorange.warbound.map.MapTile;

/**
 * Unit.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class Unit implements GameObject{
    private float speed = 5;
    private UnitOrder currentOrder;
    private UnitController unitController;

    private MapTile currentTile = null;
    private Sprite sprite;
    private boolean selected = false;

    public Unit( MapTile startTile, TextureRegion texture, UnitController controller){
        sprite = new Sprite( texture );
        sprite.setSize( 1, 1 );
        sprite.setPosition( startTile.getWorldPosition().x, startTile.getWorldPosition().y );

        setCurrentTile( startTile );
        this.unitController = controller;
    }

    public void update( float deltaTime){
        if( currentOrder != null ) {
            currentOrder.executed( deltaTime );

            if( currentOrder.isCompleted())
                currentOrder = null;
        }

        if( selected ){
            //Do some cool shit?
        }
    }

    public void draw( SpriteBatch batch){
        sprite.draw( batch );
    }

    public void select( boolean selected ){
        this.selected = selected;

        if( selected )
            sprite.setColor( Color.GREEN );
        else
            sprite.setColor( Color.WHITE );
    }

    public Rectangle getBoundingBox(){
        return sprite.getBoundingRectangle();
    }

    @Override
    public String toString() {
        return "Unit at (" + sprite.getX() + ", " + sprite.getY() + ")\n";
    }

    public void cancelCurrentOrder(){
        if( currentOrder != null ){
            currentOrder.cancelled();
            currentOrder = null;
        }
    }

    public void giveOrder( UnitOrder order ){
        cancelCurrentOrder();

        currentOrder = order;
        currentOrder.recieved( this );
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
        currentTile.setOccupier( this );

    }

}

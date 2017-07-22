package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.redtoorange.warbound.Constants;

/**
 * CameraController.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class CameraController {
    private OrthographicCamera camera;
    private Viewport viewport;

    private float speed = 10f;

    private float scollZone = .05f;

    private int scrollZoneHor;
    private int scrollZoneVert;
    private int currentMouseX;
    private int currentMouseY;

    public CameraController(){
        this( 0, 0);
    }

    public CameraController( float startX, float startY){
        camera = new OrthographicCamera( );
        camera.position.set( startX, startY, 0 );

        viewport = new ExtendViewport( Constants.WIDTH_UNITS, Constants.HEIGHT_UNITS, camera );

        currentMouseX = Gdx.graphics.getWidth() / 2;
        currentMouseY = Gdx.graphics.getHeight() / 2;

        scrollZoneHor = (int)(Gdx.graphics.getWidth() * scollZone);
        scrollZoneVert = (int)(Gdx.graphics.getHeight() * scollZone);

        Gdx.input.setCursorPosition( currentMouseX, currentMouseY );
    }

    public void update(){
        camera.update();
    }

    public void resize( int width, int height ) {
        viewport.update( width, height );
        camera.update();

        scrollZoneHor = (int)(Gdx.graphics.getWidth() * scollZone);
        scrollZoneVert = (int)(Gdx.graphics.getHeight() * scollZone);
    }

    public Matrix4 combineMatrix(){
        return camera.combined;
    }

    public void handleInput( float deltaTime ){
        Vector2 cameraDelta = Vector2.Zero;

        if( Gdx.input.isKeyPressed( Input.Keys.RIGHT ))
            cameraDelta.x = 1;
        if( Gdx.input.isKeyPressed( Input.Keys.LEFT ))
            cameraDelta.x = -1;

        if( Gdx.input.isKeyPressed( Input.Keys.UP ))
            cameraDelta.y = 1;
        if( Gdx.input.isKeyPressed( Input.Keys.DOWN ))
            cameraDelta.y = -1;

        currentMouseX = Gdx.input.getX();
        currentMouseY = Gdx.input.getY();

        //Mouse scroll disabled for now
//        if( currentMouseX < scrollZoneHor)
//            cameraDelta.x = -1;
//        if( currentMouseX > Gdx.graphics.getWidth() - scrollZoneHor)
//            cameraDelta.x = 1;
//
//        if( currentMouseY < scrollZoneVert)
//            cameraDelta.y = 1;
//        if( currentMouseY > Gdx.graphics.getHeight() - scrollZoneVert)
//            cameraDelta.y = -1;

        camera.translate( cameraDelta.scl( deltaTime * speed) );
    }

    public Vector2 getMouseScreenPosition(){
        return new Vector2( currentMouseX, currentMouseY );
    }
    public Vector2 getMouseWorldPosition(){
        return viewport.unproject( getMouseScreenPosition() );
    }
}

package com.redtoorange.warbound.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.redtoorange.warbound.Constants;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapController;

/**
 * PlayScreen.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class PlayScreen extends ScreenAdapter {
    private MapController mapController;
    private PlayerController playerController;

    public PlayScreen() {
        super();

        mapController = new MapController( 0, 0, 50, 50 );
        playerController = new PlayerController( mapController);
    }

    /**
     *
     * @param delta
     */
    @Override
    public void render( float delta ) {
        super.render( delta );

        update( delta );
        draw();
    }

    /**
     *
     * @param deltaTime
     */
    private void update( float deltaTime ){
        if( Gdx.input.isKeyJustPressed( Input.Keys.ESCAPE ) ) {
            Gdx.app.exit();
        }
        if( Gdx.input.isKeyJustPressed( Input.Keys.D ) ) {
            Constants.DEBUGGING = !Constants.DEBUGGING;
        }

        mapController.update( deltaTime );
        playerController.update( deltaTime );
    }

    /**
     *
     */
    private void draw( ){
        playerController.draw();
    }



    /**
     *
     * @param width
     * @param height
     */
    @Override
    public void resize( int width, int height ) {
        super.resize( width, height );
        playerController.resize( width, height );
    }

    /**
     *
     */
    @Override
    public void show() {
        super.show();
    }







}

package com.redtoorange.warbound.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.ui.DebugUI;
import com.redtoorange.warbound.utilities.Constants;

/**
 * PlayScreen.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class PlayScreen extends ScreenAdapter {
    private MapController mapController;
    private PlayerController playerController;
    private DebugUI debugUI;


    public PlayScreen() {
        super();

//        mapController = new MapController( 0, 0, 50, 50 );
        mapController = new MapController( "maps/map_1.tmx", 10, 10 );
        playerController = new PlayerController( mapController);
        debugUI = new DebugUI( playerController, mapController );
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
        if( Gdx.input.isKeyJustPressed( Input.Keys.F2 ) ) {
            Constants.DEBUGGING = !Constants.DEBUGGING;
        }

        mapController.update( deltaTime );
        playerController.update( deltaTime );
        debugUI.update( deltaTime );
    }

    /**
     *
     */
    private void draw( ){
        playerController.draw();
        debugUI.draw();
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
        debugUI.resize( width, height );
    }

    /**
     *
     */
    @Override
    public void show() {
        super.show();
    }
}

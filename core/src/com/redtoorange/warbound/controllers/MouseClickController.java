package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

/**
 * MouseClickController.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public class MouseClickController implements InputProcessor {
    private PlayerController owner;
    private Array<ClickListener> listeners;


    public MouseClickController( PlayerController owner ){
        this.owner = owner;
        listeners = new Array< ClickListener >(  );

        addListener( owner );
    }

    /** Add a listener to be notified of mouse events. */
    public void addListener( ClickListener listener ){
        if( !listeners.contains( listener, true ))
            listeners.add( listener );
    }

    /** Remove a listener from the mouse events notification. */
    public boolean removeListener( ClickListener listener){
        boolean success = listeners.contains( listener, true );

        if( success )
            listeners.removeValue( listener, true );

        return success;
    }


    //******************************************************************
    //********      Implemented Interface Methods           ************
    //******************************************************************
    @Override
    public boolean touchDown( int screenX, int screenY, int pointer, int button ) {
        for( ClickListener l : listeners )
            l.touchDown( screenX, screenY, pointer, button );

        return false;
    }

    @Override
    public boolean touchUp( int screenX, int screenY, int pointer, int button ) {
        for( ClickListener l : listeners )
            l.touchUp( screenX, screenY, pointer, button );

        return false;
    }

    @Override
    public boolean touchDragged( int screenX, int screenY, int pointer ) {
        for( ClickListener l : listeners )
            l.touchDragged( screenX, screenY, pointer );

        return false;
    }
    //******************************************************************



    @Override
    public boolean keyDown( int keycode ) {
        return false;
    }
    @Override
    public boolean keyUp( int keycode ) {
        return false;
    }
    @Override
    public boolean keyTyped( char character ) {
        return false;
    }
    @Override
    public boolean mouseMoved( int screenX, int screenY ) {
        return false;
    }
    @Override
    public boolean scrolled( int amount ) {
        return false;
    }
}

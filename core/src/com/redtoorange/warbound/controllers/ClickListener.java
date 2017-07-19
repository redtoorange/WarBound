package com.redtoorange.warbound.controllers;

/**
 * ClickListener.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/21/2017
 */
public interface ClickListener {
    boolean touchDown( int screenX, int screenY, int pointer, int button );
    boolean touchUp( int screenX, int screenY, int pointer, int button );
    boolean touchDragged( int screenX, int screenY, int pointer );
}

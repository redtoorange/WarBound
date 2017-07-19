package com.redtoorange.warbound;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * GameObject.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public interface GameObject {
    void update( float deltaTime );
    void draw( SpriteBatch batch );
}

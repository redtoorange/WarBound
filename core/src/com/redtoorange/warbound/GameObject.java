package com.redtoorange.warbound;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.redtoorange.warbound.controllers.Controller;
import com.redtoorange.warbound.controllers.PlayerController;

/**
 * GameObject.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/18/2017
 */
public abstract class GameObject {
    protected PlayerController owner;
    protected Controller controller;

    public GameObject( Controller controller ) {
        this.controller = controller;
        this.owner = controller.getOwner();
    }

    public abstract void update( float deltaTime );
    public abstract void draw( SpriteBatch batch );

    public PlayerController getOwner() {
        return owner;
    }

    public Controller getController() {
        return controller;
    }
}

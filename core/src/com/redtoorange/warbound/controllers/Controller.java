package com.redtoorange.warbound.controllers;

/**
 * Controller.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/23/2017
 */
public abstract class Controller {
    protected PlayerController owner;

    public Controller( PlayerController owner ) {
        this.owner = owner;
    }

    public void update( float deltaTime ){}

    public void draw(){}

    public PlayerController getOwner() {
        return owner;
    }
}

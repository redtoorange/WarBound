package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.controllers.BuildingController;

/**
 * Barracks.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class Barracks extends Building {
    private float coolDown = 1.0f;
    private float currentTick = coolDown;
    private int productionAmount = 10;

    public Barracks( String name, TextureRegion texture, int width, int height, BuildingController controller ) {
        super( name, texture, width, height, controller );
    }

    @Override
    public void update( float deltaTime ) {
        super.update( deltaTime );

//        if( state == State.PRODUCING ){
//            currentTick -= deltaTime;
//            if(  currentTick <= 0.0f ){
//                currentTick += coolDown;
//                owner.getResourceController().changeResource( Resource.GOLD, productionAmount );
//            }
//        }
    }
}

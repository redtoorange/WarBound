package com.redtoorange.warbound;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.redtoorange.warbound.controllers.UnitController;
import com.redtoorange.warbound.map.MapTile;

/**
 * UnitFactory.java - Description
 *
 * @author Andrew McGuiness
 * @version 6/22/2017
 */
public class UnitFactory {
    private static TextureAtlas UNITS = new TextureAtlas( "units/player.pack" );

    public static Unit BuildFootman( UnitController controller, MapTile startTile ){
        return new Unit(startTile, UNITS.findRegion( "idle" ), controller);
    }
}

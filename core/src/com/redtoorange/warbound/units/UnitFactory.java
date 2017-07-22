package com.redtoorange.warbound.units;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
        return new Peon(startTile, new TextureRegion( new Texture( "units/worker_m.png" ) ), controller);
    }

    public static Unit BuildUnit( UnitType unitType, UnitController controller, MapTile startTile){
        Unit builtUnit = null;

        switch( unitType ){
            case PEON:
                builtUnit = BuildFootman( controller, startTile );
        }

        return builtUnit;
    }
}



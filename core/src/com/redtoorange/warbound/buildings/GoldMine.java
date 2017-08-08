package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.controllers.BuildingController;
import com.redtoorange.warbound.map.MapTile;

/**
 * GoldMine.java - Description
 *
 * @author Andrew McGuiness
 * @version 8/3/2017
 */
public class GoldMine extends Building {
    public static final String TAG = Barracks.class.getSimpleName();
    private static int EMPTY = 0, FULL = 1;
    private int mineState = EMPTY;

    protected static final TextureRegion[] mineRegions = {
            new TextureRegion( new Texture( "wc2_buildings/gold_mine_empty.png" ) ),
            new TextureRegion( new Texture( "wc2_buildings/gold_mine_full.png" ) )
    };

    public GoldMine( String name, int width, int height, BuildingController controller, MapTile tile ) {
        super( name, mineRegions, width, height, controller );

        TYPE = BuildingType.GOLDMINE;

        instantBuild( tile );
        sprite.setRegion( mineRegions[EMPTY] );
        canBeEntered = true;
    }

    @Override
    public void update( float deltaTime ) {
        sprite.setRegion( mineRegions[mineState] );
    }

    @Override
    public void draw( SpriteBatch batch ) {
        sprite.draw( batch );
    }
}

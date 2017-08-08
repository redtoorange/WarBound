package com.redtoorange.warbound.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redtoorange.warbound.controllers.BuildingController;
import com.redtoorange.warbound.map.MapTile;
import com.redtoorange.warbound.units.Unit;
import com.redtoorange.warbound.units.UnitFactory;
import com.redtoorange.warbound.units.UnitType;

/**
 * Barracks.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class Barracks extends Building {
    public static final String TAG = Barracks.class.getSimpleName();

    protected static final TextureRegion[] barracksRegions = {
            new TextureRegion( new Texture( "wc2_buildings/large_building_started.png" ) ),
            new TextureRegion( new Texture( "wc2_buildings/barracks_building.png" ) ),
            new TextureRegion( new Texture( "wc2_buildings/barracks.png" ) ),
    };

    protected UnitType unitBeingProduced;
    protected boolean producingUnit = false;
    protected float progress = 0.0f;
    protected float totalTime = 0.0f;

    public Barracks( String name, int width, int height, BuildingController controller ) {
        super( name, barracksRegions, width, height, controller );
        TYPE = BuildingType.BARRACKS;
    }

    public Barracks( String name, int width, int height, BuildingController controller, MapTile tile ) {
        super( name, barracksRegions, width, height, controller );
        instantBuild( tile );
        TYPE = BuildingType.BARRACKS;
    }

    @Override
    public void update( float deltaTime ) {
        super.update( deltaTime );

        switch ( currentState ) {
            case COMPLETE:
                if ( producingUnit )
                    handleUnitProduction( deltaTime );

                break;
        }
    }

    /** Subtract deltaTime from the total progress, when it reaches 0, the unit will be produced. */
    private void handleUnitProduction( float deltaTime ) {
        progress -= deltaTime;

        if ( progress <= 0.0f ) {
            produceQueuedUnit();
            producingUnit = false;
        }
    }

    /**
     * Start of the unit production chain.  Attempt to Queue a unit, resources are checked
     * against the UnitType.
     *
     * @param unitType Type of unit to produce.
     */
    public void attemptToQueueUnit( UnitType unitType ) {
        if ( isComplete() && !producingUnit && hasNeededResources( unitType ) )
            queueUnit( unitType );
    }

    /** See if the player controller's resource controller has the required resources. */
    private boolean hasNeededResources( UnitType unitType ) {
        return owner.getResourceController().canAfford(
                unitType.goldCost, unitType.woodCost,
                unitType.oilCost, unitType.foodCost
        );
    }

    /** Charge the player the resources and begin production of the unit. */
    private void queueUnit( UnitType unitType ) {
        unitBeingProduced = unitType;
        producingUnit = true;
        progress = totalTime = unitType.productionTime;

        owner.getResourceController().chargeAmount(
                unitType.goldCost, unitType.woodCost,
                unitType.oilCost, unitType.foodCost
        );
    }

    /** Unit is finished, completed it and put it on an empty tile. */
    private void produceQueuedUnit() {
        MapTile tile = getSpotOnPerimeter();

        if ( tile != null ) {
            Unit createdUnit = UnitFactory.BuildUnit( unitBeingProduced, owner.getUnitController(), tile );

            if ( createdUnit != null )
                owner.getUnitController().addUnit( createdUnit );
        }
    }

    /** Cancel the production and refund the resources. */
    public void cancelQueuedUnit() {
        producingUnit = false;

        owner.getResourceController().chargeAmount(
                -unitBeingProduced.goldCost, -unitBeingProduced.woodCost,
                -unitBeingProduced.oilCost, -unitBeingProduced.foodCost
        );
    }
}

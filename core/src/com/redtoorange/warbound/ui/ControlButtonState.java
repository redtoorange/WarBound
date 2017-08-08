package com.redtoorange.warbound.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.redtoorange.warbound.buildings.Barracks;
import com.redtoorange.warbound.buildings.BuildingType;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.units.UnitType;

/**
 * ControlButtonState.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/22/2017
 */
public class ControlButtonState {
    private static final String ICON_DIR = "ui_icons/";

    private static final Texture[] buttonRegions = {
            new Texture( ICON_DIR + "attack.png" ),
            new Texture( ICON_DIR + "defend.png" ),
            new Texture( ICON_DIR + "move.png" ),

            new Texture( ICON_DIR + "tier1.png" ),
            new Texture( ICON_DIR + "tier2.png" ),
            new Texture( ICON_DIR + "mine.png" ),

            new Texture( ICON_DIR + "repair.png" ),
            new Texture( ICON_DIR + "stop.png" )
    };

    private static final int ATTACK = 0, DEFEND = 1, MOVE = 2,
            TIER_1 = 3, TIER_2 = 4, MINE = 5,
            REPAIR = 6, STOP = 7, ICON_COUNT = 8;

    public static void setLayout( ButtonLayout layout, UIController uiController ) {
        PlayerController owner = uiController.getOwner();
        ControlButton[] buttonGrid = uiController.getButtonGrid();

        resetButtons( buttonGrid );

        switch ( layout ) {
            case PEON:
                setPeonGrid( buttonGrid, owner );
                break;

            case BARRACKS:
                setBarracksGrid( buttonGrid, owner );
                break;

            case DEFAULT:
                setDefaultGrid( buttonGrid, owner );
                break;

            case CONSTRUCTION:
                setConstructionGrid( buttonGrid, owner );
                break;
        }
    }

    private static void resetButtons( ControlButton[] buttonGrid ) {
        for ( int i = 0; i < buttonGrid.length; i++ )
            buttonGrid[i].reset();
    }

    private static void setDefaultGrid( ControlButton[] buttonGrid, final PlayerController owner ) {
        //STUB
    }

    private static void setPeonGrid( ControlButton[] buttonGrid, final PlayerController owner ) {
        buttonGrid[0].setImage( buttonRegions[TIER_1] );
        buttonGrid[0].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().attemptToBeginPlacing( BuildingType.FARM );
            }
        } );

        buttonGrid[1].setImage( buttonRegions[TIER_2] );
        buttonGrid[1].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().attemptToBeginPlacing( BuildingType.BARRACKS );
            }
        } );
    }

    private static void setBarracksGrid( ControlButton[] buttonGrid, final PlayerController owner ) {
        buttonGrid[0].setImage( new Texture( "units/peon/peon_s_0.png" ) );
        buttonGrid[0].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                if ( owner.getBuildingController().getCurrentBuilding() instanceof Barracks ) {
                    Barracks barracks = ( Barracks ) owner.getBuildingController().getCurrentBuilding();
                    if ( barracks != null )
                        barracks.attemptToQueueUnit( UnitType.PEON );
                }
            }
        } );
    }

    private static void setConstructionGrid( ControlButton[] buttonGrid, final PlayerController owner ) {
        buttonGrid[8].setImage( buttonRegions[STOP] );
        buttonGrid[8].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().cancelCurrentConstruction();
            }
        } );
    }
}

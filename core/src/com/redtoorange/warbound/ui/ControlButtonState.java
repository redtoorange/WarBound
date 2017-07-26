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
        buttonGrid[0].setImage( new Texture( "wc2_buildings/farm.png" ) );
        buttonGrid[0].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().attemptToBeginPlacing( BuildingType.FARM );
            }
        } );

        buttonGrid[1].setImage( new Texture( "wc2_buildings/barracks.png" ) );
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
        buttonGrid[8].setImage( new Texture( "badlogic.jpg" ) );
        buttonGrid[8].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().cancelCurrentConstruction();
            }
        } );
    }
}

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
    public enum ButtonLayout{
        DEFAULT, PEON, BARRACKS, CONSTRUCTION
    }

    public static void setLayout( ButtonLayout layout, UIController uiController ){
        PlayerController owner = uiController.getOwner();
        ControlButton[] buttonGrid = uiController.getButtonGrid();

        for( int i = 0; i < buttonGrid.length; i++){
            buttonGrid[i].reset();
        }

        switch ( layout ){
            case PEON:
                setPeonGrid( buttonGrid, owner );
                break;
            case BARRACKS:
                setBarracksGrid( buttonGrid, owner  );
            case DEFAULT:
                setDefaultGrid( buttonGrid, owner );
                break;
            case CONSTRUCTION:
                setConstructionGrid( buttonGrid, owner);
        }
    }

    private static void setDefaultGrid( ControlButton[] buttonGrid, final PlayerController owner ) {

    }

    private static void setPeonGrid( ControlButton[] buttonGrid, final PlayerController owner ) {
        buttonGrid[0].setImage( new Texture( "wc2_buildings/farm.png" ));
        buttonGrid[0].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().beginPlacing( BuildingType.FARM );
            }
        } );

        buttonGrid[1].setImage( new Texture( "wc2_buildings/barracks.png" ) );
        buttonGrid[1].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().beginPlacing( BuildingType.BARRACKS );
            }
        } );
    }

    private static void setBarracksGrid( ControlButton[] buttonGrid, final PlayerController owner ) {
        buttonGrid[0].setImage( new Texture( "units/peon/peon_s_0.png" ));
        buttonGrid[0].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                if( owner.getBuildingController().getCurrentBuilding( ) instanceof Barracks){
                    Barracks barracks = (Barracks )owner.getBuildingController().getCurrentBuilding( );
                    if( barracks != null )
                        barracks.queueUnit( UnitType.PEON );
                }
            }
        } );
    }

    private static void setConstructionGrid( ControlButton[] buttonGrid, final PlayerController owner ) {
        buttonGrid[8].setImage( new Texture( "badlogic.jpg" ));
        buttonGrid[8].setChangeListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().cancelCurrentConstruction();
            }
        } );
    }
}

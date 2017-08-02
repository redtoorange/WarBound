package com.redtoorange.warbound.tools;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.redtoorange.warbound.units.Unit;

/**
 * UnitTable.java - Description
 *
 * @author Andrew McGuiness
 * @version 8/2/2017
 */
public class UnitTable extends DebugTable {
    private Unit unit;

    private VisLabel facingField;
    private VisLabel orderField;

    private VisLabel speedField;
    private VisSlider speedSlider;


    public UnitTable( Unit unit ){
        this.unit = unit;

        init();
    }

    private void init(){


        //        builder.append( "\nCurrent Order: \t" );
        //        builder.append( currentOrder );
        add( "Current Order:" );
        orderField = new VisLabel( unit.getCurrentOrder().toString() );
        add( orderField );
        row();


        //        builder.append( "Current Facing: \t" );
        //        builder.append( currentFacing );
        add( "Current Facing:" );
        facingField = new VisLabel( unit.getCurrentFacing().toString()  );
        add( facingField );
        row();

        //
        //        builder.append( "\nSpeed: " );
        //        builder.append( speed );
        add( "Speed: " );
        speedSlider = new VisSlider( 0.0f, 10.0f, 0.5f, false );
        speedSlider.setValue( unit.getSpeed() );
        speedField = new VisLabel( unit.getSpeed() + "" );

        add( speedSlider );
        add( speedField );

        row();

        for( int i = 0; i < 15; i++){
            add( "Test Label" );
            row();
        }
        //
        //        builder.append( "\nCurrent Tile: \t" );
        //        builder.append( currentTile );
        //
        //        if( currentTile != null){
        //            builder.append( "\nTile Position: " );
        //            builder.append( currentTile.getMapX() + ", " + currentTile.getMapY() );
        //        }
        //
        //        builder.append( "\nWorld Position: " );
        //        builder.append( sprite.getX() + ", " + sprite.getX() );
        //
        //        builder.append( "\nSelected: " );
        //        builder.append( selected );
        //
        //        builder.append( "\nInside a Building: " );
        //        builder.append( insideBuilding );
        //
        //        if( targetBuilding != null) {
        //            builder.append( "\nTarget Building: \t" );
        //            builder.append( targetBuilding );
        //        }
        //
        //        builder.append( "\n" );
        //        return builder.toString();
    }

    @Override
    public void updateUI() {
        orderField.setText( unit.getCurrentOrder().toString() );
        facingField.setText( unit.getCurrentFacing().toString() );


        speedField.setText( speedSlider.getValue() + "" );
        unit.setSpeed( speedSlider.getValue() );
    }
}

package com.redtoorange.warbound.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * ControlButton.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/22/2017
 */
public class ControlButton extends ImageButton {
    protected static ImageButtonStyle STYLE;
    private ChangeListener changeListener;
    private Image image;

    public ControlButton( Skin skin ) {
        super( skin );

        if( STYLE == null )
            STYLE = new ImageButtonStyle( getStyle() );

        setStyle( new ImageButtonStyle( STYLE ) );
    }

    public void setChangeListener( ChangeListener changeListener ){
        if( this.changeListener != null){
            getListeners().removeValue( this.changeListener, true );
        }

        if( changeListener != null){
            this.changeListener = changeListener;
            addListener( this.changeListener );
        }
    }

    public void setImage( Texture texture){
        if( texture != null){
            image = new Image( texture );
            getStyle().imageUp = image.getDrawable();
        }
        else{
            getStyle().imageUp = STYLE.imageUp;
        }

    }

    public void reset(){
        setChangeListener( null );
        setImage( null );
    }
}

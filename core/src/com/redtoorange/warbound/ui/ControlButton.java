package com.redtoorange.warbound.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * ControlButton.java - Specialize button that can contain only a single click
 * listener and an image.
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

        if ( STYLE == null )
            STYLE = new ImageButtonStyle( getStyle() );

        setStyle( new ImageButtonStyle( STYLE ) );
    }

    /** Set the button's current action when clicked. */
    public void setChangeListener( ChangeListener p_changeListener ) {
        if ( changeListener != null )
            getListeners().removeValue( changeListener, true );

        if ( p_changeListener != null ) {
            changeListener = p_changeListener;
            addListener( changeListener );
        }
    }

    /** Set the button's current Image. */
    public void setImage( Texture p_texture ) {
        if ( p_texture != null ) {
            image = new Image( p_texture );
            getStyle().imageUp = image.getDrawable();
        } else
            getStyle().imageUp = STYLE.imageUp;
    }

    /** Reset the button to the default style with no click listener. */
    public void reset() {
        setChangeListener( null );
        setImage( null );
    }
}

package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.redtoorange.warbound.Resource;

/**
 * UIController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class UIController {
    private OrthographicCamera guiCamera;
    private Viewport viewport;
    private Stage guiStage;
    private Table rootTable;
    private Skin guiSkin;

    private Label goldLabel;
    private Label woodLabel;
    private Label oilLabel;
    private Label usedFoodLabel;
    private Label availableFoodLabel;

    private PlayerController playerController;

    public UIController( PlayerController playerController ) {
        this.playerController = playerController;

        guiCamera = new OrthographicCamera( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        viewport = new ScreenViewport( guiCamera );

        VisUI.load();
        guiSkin = VisUI.getSkin();

        guiStage = new Stage( viewport );
        rootTable = new Table( guiSkin );
        guiStage.addActor( rootTable );

        rootTable.setFillParent( true );


        rootTable.setDebug( true );

        rootTable.top();
        Table resources = new Table( guiSkin );
        rootTable.add( resources );

        Pixmap pix = new Pixmap( 1, 1, Pixmap.Format.RGB888 );
        pix.setColor( Color.BLACK );
        pix.fill();

        TextureRegionDrawable tex = new TextureRegionDrawable( new TextureRegion( new Texture( pix ) ) );
        resources.setBackground( tex );
        float cellWidth = 100.0f;
        float spacingWidth = 50.0f;

        //GOLD
        resources.add( "Gold: " );
        goldLabel = new Label( "0", guiSkin );
        resources.add( goldLabel ).width( cellWidth ).center();

        //WOOD
        resources.add().width( spacingWidth );

        resources.add( "Wood: " );
        woodLabel = new Label( "0", guiSkin );
        resources.add( woodLabel ).width( cellWidth ).center();

        //OIL
        resources.add().width( spacingWidth );

        resources.add( "Oil: " );
        oilLabel = new Label( "0", guiSkin );
        resources.add( oilLabel ).width( cellWidth ).center();



        //FOOD
        resources.add().width( spacingWidth );

        resources.add( "Food: " );

        usedFoodLabel = new Label( "0", guiSkin );
        resources.add( usedFoodLabel );

        resources.add( "/" );

        availableFoodLabel = new Label( "0", guiSkin );
        resources.add( availableFoodLabel );
    }

    public void update( float deltaTime ){
        updateResources();
        guiStage.act(deltaTime);
    }

    public void draw( ){
        guiCamera.update();
        guiStage.draw();
    }

    private void updateResources(){
        goldLabel.setText( "" + playerController.getResource( Resource.GOLD ) );
        woodLabel.setText( "" + playerController.getResource( Resource.WOOD ) );
        oilLabel.setText( "" + playerController.getResource( Resource.OIL ) );

        usedFoodLabel.setText( "" + playerController.getUsedFood() );
        availableFoodLabel.setText( "" + playerController.getAvailableFood() );
    }
}

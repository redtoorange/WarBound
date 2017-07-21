package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.redtoorange.warbound.Resource;
import com.redtoorange.warbound.buildings.BuildingFactory;

/**
 * UIController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class UIController {
    private PlayerController owner;
    private ResourceController resourceController;

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

    private Table debuggingTable;
    public Label debuggingLabel;

    public UIController( PlayerController owner, InputMultiplexer multiplexer ) {
        this.owner = owner;

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


        Pixmap pix = new Pixmap( 1, 1, Pixmap.Format.RGB888 );
        pix.setColor( Color.BLACK );
        pix.fill();

        TextureRegionDrawable tex = new TextureRegionDrawable( new TextureRegion( new Texture( pix ) ) );

        initBuildPanel( tex );
        initResourcePanel( tex );

        multiplexer.addProcessor( guiStage );

        debuggingTable = new Table( guiSkin );
        debuggingTable.setDebug( true );
        debuggingTable.setFillParent( true );
        debuggingLabel = new Label( "Debug Info", guiSkin );
        debuggingTable.add( debuggingLabel );
        guiStage.addActor( debuggingTable );
    }

    private void initBuildPanel( TextureRegionDrawable tex ) {
        Table buildings = new Table( guiSkin );

        rootTable.add( buildings ).expandY().left().width( Gdx.graphics.getWidth() * 0.15f ).height( Gdx.graphics.getHeight()  );
        buildings.setBackground( tex );

        Button farmButton = new Button( guiSkin );
        farmButton.add( "Farm" );
        farmButton.addListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().beginPlacing( BuildingFactory.BuildFarm( owner.getBuildingController() ) );
            }
        } );
        buildings.add( farmButton );

        Button barracksButton = new Button( guiSkin );
        barracksButton.add( "Barracks" );
        barracksButton.addListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                owner.getBuildingController().beginPlacing( BuildingFactory.BuildBarracks( owner.getBuildingController() ) );
            }
        } );
        buildings.add( barracksButton );
    }



    private void initResourcePanel( TextureRegionDrawable tex ) {
        Table resources = new Table( guiSkin );
        rootTable.add( resources ).center().top().expandX().width( Gdx.graphics.getWidth() * 0.85f );
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
        if( resourceController == null )
            resourceController = owner.getResourceController();

        goldLabel.setText( "" + resourceController.getResource( Resource.GOLD ) );
        woodLabel.setText( "" + resourceController.getResource( Resource.WOOD ) );
        oilLabel.setText( "" + resourceController.getResource( Resource.OIL ) );

        usedFoodLabel.setText( "" + resourceController.getResource( Resource.FOOD_USED) );
        availableFoodLabel.setText( "" + resourceController.getResource( Resource.FOOD_STORED) );
    }

    public PlayerController getOwner() {
        return owner;
    }

    public void resize( int width, int height ) {
        viewport.update( width, height );
    }
}

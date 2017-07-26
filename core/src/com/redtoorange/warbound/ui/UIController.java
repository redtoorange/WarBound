package com.redtoorange.warbound.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.redtoorange.warbound.utilities.Resource;
import com.redtoorange.warbound.controllers.Controller;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.controllers.ResourceController;

/**
 * UIController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class UIController extends Controller {
    private ResourceController resourceController;

    private OrthographicCamera guiCamera;
    private Viewport viewport;

    private Stage guiStage;
    private Skin guiSkin;

    private Label goldLabel, woodLabel, oilLabel, usedFoodLabel, availableFoodLabel;
    private Table rootTable, controlPanel, buildingPanel;

    private ControlButton[] buttonGrid;

    public UIController( PlayerController owner, InputMultiplexer multiplexer ) {
        super( owner );

        VisUI.load();
        guiSkin = VisUI.getSkin();

        guiCamera = new OrthographicCamera( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        viewport = new ScreenViewport( guiCamera );

        guiStage = new Stage( viewport );

        initRootTable();

        TextureRegionDrawable tex = getBlackPixel();
        initControlPanel( tex );
        initResourcePanel( tex );

        multiplexer.addProcessor( guiStage );
    }

    /** Create a black 1 pixel texture to fill the tables with a black background */
    private TextureRegionDrawable getBlackPixel() {
        Pixmap pix = new Pixmap( 1, 1, Pixmap.Format.RGB888 );
        pix.setColor( Color.BLACK );
        pix.fill();

        return new TextureRegionDrawable( new TextureRegion( new Texture( pix ) ) );
    }

    private void initRootTable() {
        rootTable = new Table( guiSkin );
        guiStage.addActor( rootTable );

        rootTable.setFillParent( true );
        rootTable.setDebug( true );
        rootTable.center();
    }

    private void initControlPanel( TextureRegionDrawable tex ) {
        //Control Panel is three stacked tables, each 33% of the panel height
        controlPanel = new Table( guiSkin );
        controlPanel.background( tex );
        controlPanel.setDebug( true );

        rootTable.add( controlPanel )
                .left()
                .width( Value.percentWidth( 0.15f, rootTable ) )
                .height( Value.percentHeight( 1, rootTable ) );


        Table minimapPanel = new Table( guiSkin );
        minimapPanel.add( "Minimap?" ).center();
        controlPanel.add( minimapPanel )
                .width( Value.percentWidth( 1, controlPanel ) )
                .height( Value.percentHeight( 0.33f, controlPanel ) );

        controlPanel.row();

        Table statPanel = new Table( guiSkin );
        statPanel.add( "Unit stats or selection list" ).center();
        controlPanel.add( statPanel )
                .width( Value.percentWidth( 1, controlPanel ) )
                .height( Value.percentHeight( 0.33f, controlPanel ) );

        controlPanel.row();


        buildingPanel = new Table( guiSkin );
        controlPanel.add( buildingPanel )
                .width( Value.percentWidth( 1, controlPanel ) )
                .height( Value.percentHeight( 0.33f, controlPanel ) );
        createBuildingPanel();
    }

    private void initResourcePanel( TextureRegionDrawable tex ) {
        Table resources = new Table( guiSkin );
        rootTable.add( resources ).center().top().expandX().width( Value.percentWidth( 0.85f, rootTable ) );
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

    private void createBuildingPanel() {
        buildingPanel.clearChildren();

        buttonGrid = new ControlButton[9];

        for ( int i = 0; i < buttonGrid.length; i++ ) {
            if ( i > 0 && i % 3 == 0 )
                buildingPanel.row();

            buttonGrid[i] = createButton( buildingPanel );
        }
    }

    private ControlButton createButton( Table parentTable ) {
        ControlButton button = new ControlButton( guiSkin );

        parentTable.add( button )
                .width( Value.percentWidth( 1.0f / 3.0f, parentTable ) )
                .height( Value.percentHeight( 1.0f / 3.0f, parentTable ) );

        return button;
    }

    public void update( float deltaTime ) {
        updateResourceLabels();
        guiStage.act( deltaTime );
    }

    public void draw() {
        guiCamera.update();
        guiStage.draw();
    }

    public void resize( int width, int height ) {
        guiStage.getViewport().update( width, height, true );
    }

    private void updateResourceLabels() {
        if ( resourceController == null )
            resourceController = owner.getResourceController();

        goldLabel.setText( "" + resourceController.getResource( Resource.GOLD ) );
        woodLabel.setText( "" + resourceController.getResource( Resource.WOOD ) );
        oilLabel.setText( "" + resourceController.getResource( Resource.OIL ) );

        usedFoodLabel.setText( "" + resourceController.getResource( Resource.FOOD_USED ) );
        availableFoodLabel.setText( "" + resourceController.getResource( Resource.FOOD_STORED ) );
    }

    public void changeControlState( ButtonLayout layout ) {
        ControlButtonState.setLayout( layout, this );
    }

    public ControlButton[] getButtonGrid() {
        return buttonGrid;
    }
}

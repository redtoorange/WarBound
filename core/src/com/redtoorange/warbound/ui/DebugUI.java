package com.redtoorange.warbound.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.redtoorange.warbound.controllers.PlayerController;
import com.redtoorange.warbound.map.MapController;
import com.redtoorange.warbound.tools.DebugTable;
import com.redtoorange.warbound.units.Unit;

/**
 * DebugUI.java - Description
 *
 * @author Andrew McGuiness
 * @version 8/2/2017
 */
public class DebugUI {
    private PlayerController playerController;
    private MapController mapController;

    private OrthographicCamera guiCamera;
    private Viewport viewport;

    private Stage guiStage;
    private Skin guiSkin;

    private Table rootTable;

    private boolean show = false;
    private TextureRegionDrawable pixel;

    private boolean unitPaths = false;

    private VisTable debugTable;

    private TextureRegionDrawable getBlackPixel() {
        Pixmap pix = new Pixmap( 1, 1, Pixmap.Format.RGB888 );
        pix.setColor( Color.BLACK );
        pix.fill();

        return new TextureRegionDrawable( new TextureRegion( new Texture( pix ) ) );
    }

    public DebugUI( PlayerController playerController, MapController mapController ) {
        this.playerController = playerController;
        this.mapController = mapController;

        guiSkin = VisUI.getSkin();

        pixel = getBlackPixel();

        guiCamera = new OrthographicCamera( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        viewport = new ScreenViewport( guiCamera );

        guiStage = new Stage( viewport );
        this.playerController.getInputMultiplexer().addProcessor( 0, guiStage );

        initRootTable();
    }

    private void initRootTable() {
        rootTable = new Table( guiSkin );
        guiStage.addActor( rootTable );

        rootTable.setFillParent( true );
        rootTable.setDebug( true );
        rootTable.top().right();
//        rootTable.center();

        Table debugContrainer = new Table( guiSkin );
        debugContrainer.setBackground( pixel );
        rootTable.add( debugContrainer );

        debugContrainer.addCaptureListener( new EventListener() {
            @Override
            public boolean handle( Event event ) {
                //Capture mouse input and shit
                return false;
            }
        } );

        debugContrainer.setDebug( true );
        debugContrainer.add( new VisLabel("Hello Debug!")  ).align( Align.center ).size( 360, 50  );
        debugContrainer.row();
        VisCheckBox checkBox = new VisCheckBox( "Debug paths", unitPaths );
        checkBox.addListener( new ChangeListener() {
            @Override
            public void changed( ChangeEvent event, Actor actor ) {
                unitPaths = ((VisCheckBox)actor).isChecked();
            }
        } );
        debugContrainer.add(  checkBox ).center();

        debugTable = new VisTable(  );
        debugTable.top().left();

        ScrollPane scrollPane = new ScrollPane( debugTable);
        scrollPane.setScrollbarsOnTop( true );
        scrollPane.setScrollBarPositions( false, true );

        debugContrainer.row();
        debugContrainer.add( scrollPane ).height( 360 ).expand().fill();
    }

    private DebugTable currentDebugTable = null;

    public void update( float deltaTime ) {
        if( Gdx.input.isKeyJustPressed( Input.Keys.F1 ))
            show = !show;

        if( playerController.getUnitController().hasUnitsSelected()){
            Unit u = playerController.getUnitController().getFirstSelectedUnit();
            DebugTable debugTable = u.getDebugTable();
            if( currentDebugTable != debugTable ){
                this.debugTable.clear();
                currentDebugTable = debugTable;
                this.debugTable.add( currentDebugTable );
            }

            if( currentDebugTable != null )
                currentDebugTable.updateUI();

        }
        else{
            debugTable.clear();
            currentDebugTable = null;
        }

        if( show )
            guiStage.act( deltaTime );
    }

    public void draw() {
        if( show ) {
            guiCamera.update();
            guiStage.draw();

            if( unitPaths )
                playerController.getUnitController().debugDraw();
        }
    }

    public void resize( int width, int height ) {
        guiStage.getViewport().update( width, height, true );
    }
}

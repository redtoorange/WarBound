package com.redtoorange.warbound.controllers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.redtoorange.warbound.Constants;

/**
 * SelectionController.java - Description
 *
 * @author Andrew McGuiness
 * @version 7/19/2017
 */
public class SelectionController {
    private PlayerController owner;
    private CameraController cameraController;

    private ShapeRenderer shapeRenderer;
    private Vector2 startTouch;
    private Vector2 endTouch;

    private float LINE_WIDTH = 0.1f;


    public SelectionController( PlayerController owner ){
        this.owner = owner;
        shapeRenderer = new ShapeRenderer(  );
    }

    public void renderSelectionBox() {
        if( cameraController == null )
            cameraController = owner.getCameraController();

        shapeRenderer.setProjectionMatrix(cameraController.combinedMatrix());
        shapeRenderer.begin( ShapeRenderer.ShapeType.Filled );
        shapeRenderer.setColor( Constants.SELECTION_COLOR );

        //Render the four corners
        shapeRenderer.rectLine(
                startTouch.x, startTouch.y,
                endTouch.x, startTouch.y , LINE_WIDTH );
        shapeRenderer.rectLine(
                endTouch.x, startTouch.y,
                endTouch.x, endTouch.y, LINE_WIDTH );
        shapeRenderer.rectLine(
                startTouch.x, startTouch.y,
                startTouch.x, endTouch.y, LINE_WIDTH );
        shapeRenderer.rectLine(
                startTouch.x, endTouch.y,
                endTouch.x, endTouch.y, LINE_WIDTH );


        //Render the lines between the corners
        float avg = LINE_WIDTH/2f;
        shapeRenderer.rect(
                startTouch.x - avg, startTouch.y - avg,
                LINE_WIDTH, LINE_WIDTH );
        shapeRenderer.rect(
                endTouch.x - avg, endTouch.y - avg,
                LINE_WIDTH, LINE_WIDTH );
        shapeRenderer.rect(
                endTouch.x - avg, startTouch.y - avg,
                LINE_WIDTH, LINE_WIDTH );
        shapeRenderer.rect(
                startTouch.x - avg, endTouch.y - avg,
                LINE_WIDTH, LINE_WIDTH );

        shapeRenderer.end();
    }

    public Vector2 getStartTouch() {
        return startTouch;
    }

    public void setStartTouch( Vector2 startTouch ) {
        this.startTouch = startTouch;
    }

    public Vector2 getEndTouch() {
        return endTouch;
    }

    public void setEndTouch( Vector2 endTouch ) {
        this.endTouch = endTouch;
    }
}

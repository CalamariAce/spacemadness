package com.spacemadness.model;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.spacemadness.util.Geom;

// simple camera

// maps from world (x,y) coordinates to screen coordinates (x,y) to
// and vice versa
// 1 screen : 10 world
public class Camera extends Point {
	private static final long serialVersionUID = 1L;
	
	public Rectangle viewPort = new Rectangle(500,500);
	
	
	private float zoom = 1/100.0f; // larger numbers are less zoomed out
	
	public Camera(Point p, float zoom) {
		super(p);
		setScale(zoom);
	}
	
	/**
	 * @param scale
	 */
	public void setScale(float scale) {
		zoom = 1.0f/scale;
	}
	
	public float getInverseScale() {
		return zoom;
	}
	
	public float getScale() {
		return 1.0f/zoom;
	}
	
	public void setViewPort(Rectangle screen) {
		viewPort = screen;
	}
	
	public Point worldToScreen(float xPt, float yPt) {
		return new Point(
			Geom.worldToScreen(xPt, viewPort.width, this.x, zoom),
			Geom.worldToScreen(yPt, viewPort.height, this.y, zoom));
	}
	
	public Point2D.Float screenToWorld(int xScr, int yScr) {
		return new Point2D.Float( 
			Geom.screenToWorld(xScr, viewPort.width, this.x, zoom),
			Geom.screenToWorld(yScr, viewPort.height, this.y, zoom)
		);
	}
	
	public void computeTransform(AffineTransform drawTransform, float xPt, float yPt, float r, float theta) {
		Point screenPt = worldToScreen(xPt, yPt);
		drawTransform.setToIdentity();
		drawTransform.translate(screenPt.x, screenPt.y);
		drawTransform.rotate(theta);
		
		float scaleFactor = (float)(zoom);
		drawTransform.scale(scaleFactor, scaleFactor);
	}
	
	/**
	 * Compute the bounds (in world coordinates) of the viewPort.
	 * 
	 * @return
	 */
	public Rectangle2D getWorldRect() {
		Rectangle2D result = new Rectangle2D.Double();
		result.setFrameFromDiagonal(
			screenToWorld(viewPort.x, viewPort.y),
			screenToWorld(viewPort.x + viewPort.width, viewPort.y + viewPort.height)
		);
		return result;
	}
}

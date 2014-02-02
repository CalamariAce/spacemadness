package com.spacemadness.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.spacemadness.view.SelectionCompass;

public class Entity {

	// Used for drawing.
	protected static SelectionCompass s_compass = new SelectionCompass();

	// All in world coords.
	public float theta;
	// Radius of the entity.
	public int r;
	// Position
	public float x, y;
	// Velocity of the entity.
	public float vx, vy;
	// External forces acting on the entity.
	public float fx, fy;

	// A point where the entity is trying to move to (in world coordinates).
	protected Point2D.Float.Float m_heading = null;

	// drawing options
	public boolean selected;
	protected boolean m_showHeading = false;

	protected AffineTransform saveTransform;
	protected AffineTransform drawTransform = new AffineTransform();

	public void computeDrawTransform(Camera camera) {
		camera.computeTransform(drawTransform, x, y, r, theta);
	}

	public void drawSelectionCompass(Graphics2D G, Camera camera) {
		G.setColor(Color.green);
		s_compass.draw(G, camera);
	}

	public void setHeading(Point2D.Float heading) {
		m_heading = heading;
	}

	public Point2D.Float getHeading() {
		return m_heading;
	}

	public void setShowHeading(boolean x) {
		m_showHeading = x;
	}

	private static NumberFormat fmt = new DecimalFormat("#.###");

	/**
	 * 
	 * @param G
	 * @param viewRect
	 *            screen rectangle in
	 * @param scale
	 *            drawing scale
	 */
	public void preDraw(Graphics2D G, Camera camera) {
		saveTransform = G.getTransform();

		Point2D.Float relativeHeading = null;
		if (m_showHeading && (m_heading != null)) {
			relativeHeading = new Point2D.Float.Float(
					m_heading.x - x,
					m_heading.y - y);

			// absolute drawing in the screen coordinate system
			Point headingPt = camera.worldToScreen(m_heading.x, m_heading.y);
			G.setColor(Color.cyan);
			G.drawString(fmt.format(relativeHeading.distance(0, 0)),
					headingPt.x, headingPt.y);
		}

		// absolute drawing in the world system
		Point screenPt = camera.worldToScreen(x, y);

		drawTransform.setToIdentity();
		drawTransform.translate(screenPt.x, screenPt.y);
		drawTransform.scale(camera.getInverseScale(), camera.getInverseScale());
		G.setTransform(drawTransform);

		if (m_showHeading && (m_heading != null)) {
			s_compass.drawHeading(relativeHeading, G, camera);
		}

		// drawing in the object relative coordinate system
		drawTransform.rotate(theta);
		G.setTransform(drawTransform);

		if (selected) {
			drawSelectionCompass(G, camera);
		}
	}

	public void postDraw(Graphics2D G, Camera camera) {
		G.setTransform(saveTransform);
	}

}

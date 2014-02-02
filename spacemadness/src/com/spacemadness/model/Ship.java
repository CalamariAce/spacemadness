package com.spacemadness.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class Ship extends Entity {
	private static final long serialVersionUID = 1L;
	
	private Path2D m_outline;
	
	public Ship() {
		
		// basic fighter, 15 meters long
		int[][] outline = new int[][] {
			{-50,0}, {0, 150}, {50,0}, {0, 10},
		};
		
		setOutline(outline);
	}
	
	/**
	 * @param point click point in world coordinates
	 * @param camera
	 * @return
	 */
	public boolean intersects(Point2D.Float point, Camera camera) {
		float saveX = x, saveY = y;
		
		AffineTransform transform = new AffineTransform();
		transform.setToIdentity();
		transform.rotate(-theta);
		transform.translate(-saveX, -saveY);
		// transform.scale(1.0f/r, 1.0f/r);
		
		Point2D.Float mappedPoint = new Point2D.Float();
		transform.transform(point, mappedPoint);
		System.out.println(point + " -> " + mappedPoint);
		return m_outline.contains(mappedPoint);
	}
	
	/**
	 * Units are in 1/10 of a meter.
	 * 
	 * @param outline
	 */
	private void setOutline(int outline[][]) {
		int cx = 0, cy = 0;
		for (int i = 0; i < outline.length; i++) {
			cx += outline[i][0];
			cy += outline[i][1];
		}
		cx = cx / outline.length;
		cy = cy / outline.length;
		
		m_outline = new Path2D.Float();
		int maxr = 0;
		for (int i = 0; i < outline.length; i++) {
			int dx = outline[i][0] - cx;
			int dy = outline[i][1] - cy;
			
			int r = dx*dx + dy*dy;
			if (r > maxr) maxr = r;
			
			if (i == 0)
				m_outline.moveTo(dx, dy);
			else
				m_outline.lineTo(dx, dy);
		}
		r = (int)Math.sqrt(maxr);
		m_outline.closePath();
	}
	
	public void draw(Graphics2D G, Camera camera) {
		preDraw(G, camera);
		
		G.setColor(Color.orange);
		G.fill(m_outline);
		G.setColor(Color.yellow);
		G.draw(m_outline);
		
		postDraw(G, camera);
	}
	
}

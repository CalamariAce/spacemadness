package com.spacemadness.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.spacemadness.model.Camera;

public class SelectionCompass {
	
	private final static int[][] s_compass;
	private final static int[][] s_compassInner;
	private final static int s_compassRadius = 100;
	
	private final static Ellipse2D m_outerCircle; 
	private final static Ellipse2D m_innnerCircle; 

	private static int m_animationCycle = 0;
	
	static {
		int n = 48;
		
		// pre-compute the positions of the compass ticks
		s_compass = new int[n][2];
		s_compassInner = new int[n][2];
		for (int i = 0; i < n; i++) {
			double t = (2*i*Math.PI)/n;
			s_compass[i][0] = (int)(s_compassRadius * Math.cos(t) * 2.2);
			s_compass[i][1] = (int)(s_compassRadius * Math.sin(t) * 2.2);
			
			if ((i == 0) || (i == (n/4))) {
				s_compassInner[i][0] = -s_compass[i][0];
				s_compassInner[i][1] = -s_compass[i][1];
			}
			else {
				s_compassInner[i][0] = (int)(s_compass[i][0] * 0.9);
				s_compassInner[i][1] = (int)(s_compass[i][1] * 0.9);
			}
		}
		
		m_outerCircle = new Ellipse2D.Float();
		m_innnerCircle = new Ellipse2D.Float();
		
		m_outerCircle.setFrame(-s_compassRadius, -s_compassRadius, s_compassRadius*2, s_compassRadius*2);
		m_innnerCircle.setFrame(-s_compassRadius*2, -s_compassRadius*2, s_compassRadius*4, s_compassRadius*4);
	}
	
	public void drawHeading(Point2D.Float pt, Graphics2D G, Camera camera) {
		if (pt == null) return;
		G.setColor(Color.cyan);
		
		Point2D.Float origin = new Point2D.Float();
		G.draw(new Line2D.Float(new Point2D.Float(), pt));
		float r = (float)pt.distance(origin);
		G.draw(new Ellipse2D.Float( -r, -r, 2*r, 2*r));
		
		AffineTransform tmp = G.getTransform();
		AffineTransform at = new AffineTransform();
		at.setToIdentity();
		G.setTransform(at);
		G.drawString(r + " m", (int)pt.x, (int)pt.y);
		G.setTransform(tmp);
	}
	
	// draws the compass in the coordinate system of the entity
	public void draw(Graphics2D G, Camera camera) {
		G.setColor(Color.green);
		
		G.draw(m_innnerCircle);
		G.draw(m_outerCircle);
		
		for (int i = 0; i < s_compass.length; i++) {
			G.drawLine(s_compass[i][0], s_compass[i][1], s_compassInner[i][0], s_compassInner[i][1]);
		}
	}
}

package com.spacemadness.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Planet extends Entity {

	Ellipse2D.Float m_outline;
	
	public Planet(int r) {
		m_outline = new Ellipse2D.Float(-r, -r, 2*r, 2*r);
		this.r = r;
	}

	public void draw(Graphics2D G, Camera camera) {
		preDraw(G, camera);
		
		G.setColor(Color.DARK_GRAY);
		G.fill(m_outline);
		G.setColor(Color.yellow);
		G.draw(m_outline);
		
		postDraw(G, camera);
	}
}

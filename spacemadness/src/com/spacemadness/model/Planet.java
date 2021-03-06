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

	public static Planet create(float x, float y, int radius) {
		Planet p = new Planet(radius);
		p.x = x;
		p.y = y;
		return p;
	}
	
	@Override
  protected void drawEntity(Graphics2D G, Camera camera) {
		G.setColor(Color.DARK_GRAY);
		G.fill(m_outline);
		G.setColor(Color.yellow);
		G.draw(m_outline);
  }
}

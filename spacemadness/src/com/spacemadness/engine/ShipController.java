package com.spacemadness.engine;

import java.awt.geom.Point2D;

import com.spacemadness.model.Entity;
import com.spacemadness.model.Planet;
import com.spacemadness.model.World;
import com.spacemadness.util.Geom;

/**
 * This moves ships around, it is primarily used by the server
 * 
 * @author Justin
 */
public class ShipController {
	private long m_lastUpdate = 0;
	private long m_frameNumber = 0;
	private World m_world;
	private long m_updateFreq = 10;
	private long m_dt = 1;

	public ShipController(World w) {
		m_world = w;
	}

	public void applyGravity(Entity e) {
		float fx = 0, fy = 0;
		final float G = -11f;
		
		for (Planet p : m_world.getPlanets()) {
			float dx = (e.x - p.x);
			float dy = (e.y - p.y);

			float rsq = dx*dx + dy*dy;
			fx += G * dx / (rsq + 1);
			fy += G * dy / (rsq + 1);
		}
		e.fx = fx;
		e.fy = fy;
	}
	
	public void update(long nowTime) {
		// TODO: probably we should do something else here.
		if ((m_dt = nowTime - m_lastUpdate) < m_updateFreq) {
			return;
		}

		float dThetaMax = 0.01f;
		float vMax = 2.1f;
		double accMax = 0.01f;

		float dt = 1;
		Point2D.Float v = new Point2D.Float();
		for (Entity e : m_world.getShips()) {
			applyGravity(e);

			Point2D.Float heading = e.getHeading();

			// If a ship is trying to move to a heading -- it will
			// rotate to face that point, and apply thrust in that direction.
			if (heading != null) {
				Point2D.Float dr = new Point2D.Float();
				Geom.subtract(heading, new Point2D.Float(e.x, e.y), dr);
				if (dr.distance(0, 0) < 10) {
  				// If we are almost at our destination, stop trying to get there.
					e.setHeading(null);
				}

				// Determine the angle to rotate twords, and rotate.
				float dThetaHeading = Geom.dot(dr, new Point2D.Float(
						(float) Math.cos(e.theta), 
						(float) Math.sin(e.theta)));
				if (dThetaHeading < 0) { // we might over-rotate by a bit.
					e.theta += dThetaMax * dt;
				} else {
					e.theta -= dThetaMax * dt;
				}

				// Apply a force
				float thrust = 1;
				e.fx -= (float) (Math.sin(e.theta) * thrust);
				e.fy += (float) (Math.cos(e.theta) * thrust);
			}
			
			// Acceleration
			e.vx += e.fx * dt;
			e.vy += e.fy * dt;

			// Clamp Velocity
			v.x = e.vx;
			v.y = e.vy;
			Geom.clamp(v, vMax);
			e.vx = v.x;
			e.vy = v.y;

			// Update position
			e.x += e.vx * dt;
			e.y += e.vy * dt;
		}

		m_frameNumber++;
		m_lastUpdate = nowTime;
	}
}

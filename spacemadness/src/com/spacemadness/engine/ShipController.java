package com.spacemadness.engine;

import java.awt.geom.Point2D;

import com.spacemadness.model.Entity;
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

	public void update(long nowTime) {
		if ((m_dt = nowTime - m_lastUpdate) < m_updateFreq) {
			return;
		}

		float dThetaMax = 0.01f;
		float vMax = 5.1f;
		double accMax = 0.01f;

		// Test commit.
		for (Entity e : m_world.getShips()) {
			Point2D.Float heading = e.getHeading();

			if (heading != null) {
				Point2D.Float v = new Point2D.Float();
				Geom.subtract(heading, new Point2D.Float(e.x, e.y), v);
				if (v.distance(0, 0) < 10) {
					e.setHeading(null);
				}

				if (Geom.dot(v, new Point2D.Float((float) Math.cos(e.theta),
				    (float) Math.sin(e.theta))) < 0) {
					e.theta += dThetaMax;
				} else {
					e.theta -= dThetaMax;
				}

				double vCur = e.distance(0, 0) + accMax;
				if (vCur > vMax)
					vCur = vMax;
				e.vx = -(float) (Math.sin(e.theta) * vCur);
				e.vy = (float) (Math.cos(e.theta) * vCur);
			} else {
				double vsqr = e.vx * e.vx + e.vy * e.vy;
				if (vsqr > 0) {

				} else {

				}
			}
			e.x += e.vx;
			e.y += e.vy;
		}

		m_frameNumber++;
		m_lastUpdate = nowTime;
	}
}

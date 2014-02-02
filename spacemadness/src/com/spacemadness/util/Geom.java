package com.spacemadness.util;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import com.spacemadness.model.Entity;

/**
 * Maps between viewport and world coordinates.
 * 
 */
public class Geom {
	
	public final static Point2D.Float ORIGIN = new Point2D.Float(0,0);
	
	/**
	 * @param Vx View center (world coord)
	 * @param w screen coord
	 * @param W max screen coord (h or w)
	 * @param scale drawing scale
	 * 
	 * @return
	 */
	public static final float screenToWorld(int w, int W, int Vx, float scale) {
		return Vx + (w - W / 2) / scale;
	}
	
	/**
	 * result = a - b;
	 * 
	 * @param a
	 * @param b
	 * @param result
	 */
	public static void subtract(final Point2D.Float a, final Point2D.Float b, Point2D.Float result) {
		result.x = a.x - b.x;
		result.y = a.y - b.y;
	}
	
	public static float dot(Point2D.Float a, Point2D.Float b) {
		return a.x * b.x + a.y * b.y;
	}
	
	public static void normalize(Point2D.Float x) {
		double r = x.distance(ORIGIN);
		x.x = (float)(x.x / r);
		x.y = (float)(x.y / r);
	}

	public static void clamp(Point2D.Float x, float len) {
		double r = x.distance(ORIGIN);
		if (r > len) {
			x.x = (float)(x.x * len / r);
			x.y = (float)(x.y * len / r);
		}
	}

	/**
	 * @param x a world coordinate
	 * @param W width of the window
	 * @param Vx the center of the viewport  
	 * @param scale zoom level
	 * @return a world coordinate
	 */
	public static final int worldToScreen(float x, int W, float Vx, float scale) {
		return (int)((x - Vx) * scale) + W/2;
	}
	
	public static final boolean intersects(int x, int r, int a, int b) {
		return true;
	}
	
	public static final boolean intersects(Rectangle r, Entity e) {
		return true;
	}
	
}

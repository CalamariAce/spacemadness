package com.spacemadness.util;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import com.spacemadness.model.Entity;

public class Geom {
	
	public final static Point2D.Float ORIGIN = new Point2D.Float(0,0);
	
	/**
	 * @param vx View center (world coord)
	 * @param w screen coord
	 * @param W max screen coord (h or w)
	 * @param scale drawing scale
	 * 
	 * @return
	 */
	public static final float screenToWorld(int w, int W, int vx, float scale) {
		return vx + (w - W/2) / scale;
	}
	
	/**
	 * result = a - b;
	 * 
	 * @param a
	 * @param b
	 * @param result
	 */
	public static void subtract(Point2D.Float a, Point2D.Float b, Point2D.Float result) {
		result.x = a.x - b.x;
		result.y = a.y - b.y;
	}
	
	public static float dot(Point2D.Float a, Point2D.Float b) {
		return a.x * b.x + a.y * b.y;
	}
	
	public static void normalize(Point2D.Float x) {
		double r = x.distance(ORIGIN);
		x.x = (float)(x.x/r);
		x.y = (float)(x.y/r);
	}
	
	/**
	 * 
	 * @param x
	 * @param W
	 * @param vx
	 * @param scale
	 * @return
	 */
	public static final int worldToScreen(float x, int W, float vx, float scale) {
		return (int)((x - vx)*scale) + W/2;
	}
	
	public static final boolean intersects(int x, int r, int a, int b) {
		return true;
	}
	
	public static final boolean intersects(Rectangle r, Entity e) {
		return true;
	}
	
}

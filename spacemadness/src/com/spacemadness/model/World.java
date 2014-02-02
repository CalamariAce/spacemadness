package com.spacemadness.model;

import java.util.ArrayList;
import java.util.List;

import com.spacemadness.engine.ShipController;

public class World {

	private int m_MaxY, m_MaxX;

	private List<Ship> m_ships = new ArrayList<Ship>();
	private List<Resource> m_resources = new ArrayList<Resource>();

	private ShipController m_controller;

	public World() {
	}

	public void init() {
		m_controller = new ShipController(this);
	}

	public void update(long nowTime) {
		m_controller.update(nowTime);
	}

	public List<Ship> getShips() {
		return m_ships;
	}

	public void setShips(List<Ship> ships) {
		m_ships = ships;
	}

	public List<Resource> getResources() {
		return m_resources;
	}

	public void setResources(List<Resource> resources) {
		m_resources = resources;
	}

	public int getMaxY() {
		return m_MaxY;
	}

	public void setMaxY(int maxY) {
		m_MaxY = maxY;
	}

	public int getMaxX() {
		return m_MaxX;
	}

	public void setMaxX(int maxX) {
		m_MaxX = maxX;
	}
}

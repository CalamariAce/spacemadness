package com.spacemadness.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.spacemadness.engine.Server;
import com.spacemadness.model.Planet;
import com.spacemadness.model.Ship;
import com.spacemadness.model.ShipComponents;
import com.spacemadness.model.World;
import com.spacemadness.util.Flags;

// Simple main method that builds a simple game model and views it.
public class ViewTest {

	public static World createTestWorld() {
		World w = new World();
		w.setMaxX(10000);
		w.setMaxY(10000);

		ShipComponents components = ShipComponents.getInstance();
		
		Ship carrier = components.getPrototype("carrier");
		Ship fighter = new Ship();

		List<Ship> ships = new ArrayList<>();
		ships.add(carrier);
		ships.add(fighter);
		
		Random r = new Random();
		for (Ship s : ships) {
			s.x = 4500 + (2200 - r.nextFloat() * 4400);
			s.y = 4500 + (2200 - r.nextFloat() * 4400);

			// s.vx = 0.5f - r.nextFloat();
			// s.vy = 0.5f - r.nextFloat();
			s.theta = (float) (r.nextFloat() * Math.PI * 2);

			w.getShips().add(s);
		}

		w.getPlanets().add(Planet.create(4500.0f, 4500.0f, 700));
		w.getPlanets().add(Planet.create(5500.0f, 5500.0f, 300));

		w.init();
		return w;
	}
	
	private static List<String> flags;
	public static String getFlag(String flagName) {
		int index = flags.indexOf(flagName);
		if (index > -1) {
			return flags.get(index + 1);
		}
		return null;
	}
	
	public static void main(String args[]) throws Exception {
		Flags.parse(args);

		if (Flags.isSet("--host")) {
			System.out.println("Starting server...");
			Server s = Server.create();
		} else if (Flags.isSet("--server")) {
			String host = Flags.getFlag("--server");
			
		}

		WorldView wv = new WorldView();
		wv.setWorld(createTestWorld());
		wv.setPreferredSize(new Dimension(500, 500));

		JFrame jf = new JFrame("world view");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(wv);
		jf.pack();
		jf.setVisible(true);
	}
}

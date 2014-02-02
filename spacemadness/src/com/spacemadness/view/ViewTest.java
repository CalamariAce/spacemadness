package com.spacemadness.view;

import java.awt.Dimension;
import java.util.Random;

import javax.swing.JFrame;

import com.spacemadness.model.Planet;
import com.spacemadness.model.Ship;
import com.spacemadness.model.World;

public class ViewTest {

	public static World createTestWorld() {
		World w = new World();
		w.setMaxX(10000);
		w.setMaxY(10000);

		Random r = new Random();
		for (int i = 0; i < 2; i++) {
			Ship s = new Ship();
			s.x = 4500 + (2200 - r.nextFloat() * 4400);
			s.y = 4500 + (2200 - r.nextFloat() * 4400);

			// s.vx = 0.5f - r.nextFloat();
			// s.vy = 0.5f - r.nextFloat();
			s.theta = (float) (r.nextFloat() * Math.PI * 2);

			w.getShips().add(s);
		}

		Planet p = new Planet(700);
		p.x = 4500;
		p.y = 4500;
		w.getPlanets().add(p);

		w.init();
		return w;
	}

	public static void main(String args[]) throws Exception {
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

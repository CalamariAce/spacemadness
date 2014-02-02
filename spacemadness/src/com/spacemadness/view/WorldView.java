package com.spacemadness.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.spacemadness.model.Camera;
import com.spacemadness.model.Entity;
import com.spacemadness.model.Planet;
import com.spacemadness.model.Ship;
import com.spacemadness.model.World;

public class WorldView extends JPanel {
	private static final long serialVersionUID = 1L;

	private World m_world;
	private VolatileImage m_offscreen;

	private Camera m_camera = new Camera(new Point(0, 0), 3);

	private Scroller m_scrollThread = new Scroller();
	private RenderThread m_renderThread = new RenderThread();

	private enum Command {
		MOVE
	};

	private Entity m_currentFocusOwner = null;
	private boolean m_overlay = false;
	private Command m_command = null;
	private Point2D.Float m_saveHeading;
	private HeadingIllustrator m_headingIllustrator = new HeadingIllustrator();

	public void setWorld(World w) {
		m_world = w;
		m_camera.x = m_world.getMaxX() / 2;
		m_camera.y = m_world.getMaxY() / 2;
		m_renderThread.start();
	}

	public WorldView() {
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (!doSelectClick(evt))
					doScroll(evt);
			}
		});

		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				float newScale = m_camera.getScale() + e.getWheelRotation() * 0.5f
				    * e.getScrollAmount();
				if (newScale >= 0.00000001) {
					m_camera.setScale(newScale);
				}
				repaint();
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				doKeyPress(e);
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				m_camera.setViewPort(getBounds());
			}
		});

		setFocusable(true);
		requestFocusInWindow();
	}

	private void doScroll(MouseEvent evt) {
		Point mousePoint = evt.getPoint();

		int dx = mousePoint.x - getWidth() / 2;
		int dy = mousePoint.y - getHeight() / 2;

		m_scrollThread.scroll(m_camera, (int) (dx * m_camera.getScale()),
		    (int) (dy * m_camera.getScale()));
	}

	private boolean doSelectClick(MouseEvent evt) {
		Point mousePoint = evt.getPoint();
		Point2D.Float pt = m_camera.screenToWorld(mousePoint.x, mousePoint.y);

		if (m_overlay) {
			applyCurrentCommand(evt);
			return true;
		}

		for (Ship s : m_world.getShips()) {
			if (s.intersects(pt, m_camera)) {
				handleSelection(s);
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets the selection, updating the focus owner Deselects the previous owner
	 * if one exists.
	 * 
	 * @param e
	 */
	private void setSelection(Entity e) {
		// clear the previous selection owner
		if (m_currentFocusOwner != null && m_currentFocusOwner != e)
			m_currentFocusOwner.selected = false;

		// set the new selction owner
		m_currentFocusOwner = e;
	}

	/**
	 * Selects the
	 * 
	 * @param e
	 */
	private void handleSelection(Entity e) {
		if (e != null) {
			e.selected = true;
		}
		setSelection(e);
	}

	private void applyCurrentCommand(MouseEvent evt) {
		removeMouseMotionListener(m_headingIllustrator);
		handleSelection(null);
		m_overlay = false;
	}

	private class HeadingIllustrator extends MouseMotionAdapter {
		public void mouseMoved(MouseEvent evt) {
			if (m_currentFocusOwner != null) {
				Point2D.Float worldPoint = m_camera.screenToWorld(evt.getX(),
				    evt.getY());
				m_currentFocusOwner.setHeading(worldPoint);
			}
		}
	}

	private void doKeyPress(KeyEvent evt) {
		switch (evt.getKeyCode()) {
		case 'm':
		case 'M':
			if (!m_overlay && m_currentFocusOwner != null) {
				m_command = Command.MOVE;

				m_saveHeading = m_currentFocusOwner.getHeading();
				m_currentFocusOwner.setShowHeading(true);

				addMouseMotionListener(m_headingIllustrator);
				m_overlay = true;
			}
			break;
		case KeyEvent.VK_ESCAPE:
			if (m_overlay) {
				removeMouseMotionListener(m_headingIllustrator);
				m_currentFocusOwner.setHeading(m_saveHeading);
				m_currentFocusOwner.setShowHeading(false);
				m_overlay = false;
				return;
			}
			break;
		}
	}

	private class RenderThread extends Thread {
		boolean m_done = false;
		public long m_frames = 0;
		public long m_startTime = 0;

		public RenderThread() {
			super("render thread");
			setDaemon(true);
		}

		public float getFPS(long nowTime) {
			return ((float) m_frames * 1000) / (nowTime - m_startTime);
		}

		public void run() {
			m_startTime = System.currentTimeMillis();
			while (!m_done) {
				gameUpdate();
				renderOffscreen();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Graphics g = getGraphics();
						if (g != null) {
							blitToComponent(g);
							g.dispose();
						}
					}
				});
				m_frames++;

				try {
					Thread.sleep(15);
				} catch (Exception ex) {
				}
			}
		}
	}

	private void gameUpdate() {
		long nowTime = System.currentTimeMillis();
		m_world.update(nowTime); // not quite good seperation, should not really
		                         // be part of the view
		m_scrollThread.update(nowTime);
	}

	private void renderOffscreen() {
		// we need a new ting
		if (m_offscreen == null || m_offscreen.getWidth() != getWidth()
		    || m_offscreen.getHeight() != getHeight()) {
			if (m_offscreen != null)
				m_offscreen.flush();

			m_offscreen = createVolatileImage(getWidth(), getHeight());
			if (m_offscreen == null)
				return;
		}

		do {
			if (m_offscreen.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				// old vImg doesn't work with new GraphicsConfig; re-create it
				m_offscreen = createVolatileImage(getWidth(), getHeight());
			}

			Graphics2D g = m_offscreen.createGraphics();
			paintComponent(g);
			g.dispose();
		} while (m_offscreen.contentsLost());
	}

	// should be called from swmet
	private void blitToComponent(Graphics gScreen) {
		if (m_offscreen == null)
			return;

		do {
			int returnCode = m_offscreen.validate(getGraphicsConfiguration());
			if (returnCode == VolatileImage.IMAGE_RESTORED) {
				// Contents need to be restored
				renderOffscreen(); // restore contents
			} else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
				// old vImg doesn't work with new GraphicsConfig; re-create it
				renderOffscreen();
			}
			gScreen.drawImage(m_offscreen, 0, 0, this);
		} while (m_offscreen.contentsLost());
	}

	private Color getGridColor(boolean major, int logDivisions, int numTicks,
	    int maxTicks) {
		float fadeOut = 1.0f / logDivisions;

		if (major)
			return new Color(fadeOut / 2, fadeOut / 2, fadeOut);
		else
			return new Color(fadeOut * 0.5f, fadeOut * 0.5f, fadeOut * 0.5f);
	}

	public void drawBackground(Graphics2D G, Camera camera) {
		Rectangle viewRect = camera.viewPort;

		G.setColor(Color.black);
		G.fillRect(viewRect.x, viewRect.y, viewRect.width, viewRect.height);

		float gridSpacing = 200;
		int maxTicks = 40;
		int numDivisions = 1;

		Rectangle2D worldRect = m_camera.getWorldRect();

		int x0 = 0, x1 = 0;

		// make sure we don't have too many tics
		while (true) {
			x0 = (int) (worldRect.getX() / gridSpacing);
			x1 = (int) (worldRect.getMaxX() / gridSpacing);
			if (x1 - x0 > maxTicks) {
				gridSpacing = gridSpacing * 2;
				numDivisions++;
			} else
				break;
		}

		Color majorColor = getGridColor(true, numDivisions, x1 - x0, maxTicks);
		Color minorColor = getGridColor(false, numDivisions, x1 - x0, maxTicks);
		boolean major = false;
		boolean smallGrids = (x1 - x0) > 10;
		for (int i = x0 - 1; i <= x1; i++) {
			major = (i % 5 == 0);
			Point p = m_camera.worldToScreen(i * gridSpacing, 0);

			if (major)
				G.setColor(majorColor);
			else
				G.setColor(minorColor);

			G.drawLine((int) p.getX(), viewRect.y, (int) p.getX(), viewRect.y
			    + viewRect.height);

			if (major || !smallGrids)
				G.drawString("" + i * gridSpacing, (int) p.getX(), viewRect.y + 30);
		}

		int y0 = (int) (worldRect.getY() / gridSpacing);
		int y1 = (int) (worldRect.getMaxY() / gridSpacing);
		G.setColor(Color.blue);
		for (int i = y0 - 1; i <= y1; i++) {
			major = (i % 5 == 0);

			Point p = m_camera.worldToScreen(0, i * gridSpacing);

			if (major)
				G.setColor(majorColor);
			else
				G.setColor(minorColor);

			G.drawLine(viewRect.x, (int) p.getY(), viewRect.x + viewRect.width,
			    (int) p.getY());

			if (major || !smallGrids)
				G.drawString("" + i * gridSpacing, viewRect.x, (int) p.getY());
		}
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Graphics2D G = (Graphics2D) graphics;

		drawBackground(G, m_camera);

		for (Planet p : m_world.getPlanets()) {
			p.draw(G, m_camera);
		}
		
		for (Ship s : m_world.getShips()) {
			s.draw(G, m_camera);
		}

		if (m_renderThread != null) {
			G.setColor(Color.cyan);
			G.drawString("" + m_renderThread.getFPS(System.currentTimeMillis()), 5,
			    30);
		}
	}
}

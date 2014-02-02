package com.spacemadness.view;

import java.awt.Point;

import com.spacemadness.model.Camera;

public class Scroller {
	private Camera m_target;
	
	private Point m_scrollTo = null;
	private Point m_scrollFrom = null;
	private long m_startTime;		// when is the last time we were clicked
	
	private boolean m_inScroll;		// are we still scrolling
	
	private long m_maxTime = 1100;	// maximum time for a scroll
	
	public void update(long nowTime) {
		if (m_inScroll) {
			long delta = nowTime - m_startTime;
			if (delta < m_maxTime) {
				double t = ((double)delta)/m_maxTime;
				
				m_target.x = (int)(m_scrollTo.x * t + m_scrollFrom.x * (1.0 - t));
				m_target.y = (int)(m_scrollTo.y * t + m_scrollFrom.y * (1.0 - t));
			}
			else { // times up, go!
				m_target.x = m_scrollTo.x;
				m_target.y = m_scrollTo.y;
				m_inScroll = false;
			}
		}
	}
	
	/**
	 * @param curPoint
	 * @param dWx delta X in world coordinates
	 * @param dWy delta X in world coordinates
	 */
	public void scroll(Camera curPoint, int dWx, int dWy) {
		m_startTime = System.currentTimeMillis();
		m_inScroll = true;
		m_target = curPoint;
		m_scrollFrom = new Point(curPoint);
		m_scrollTo = new Point(m_scrollFrom.x + dWx, m_scrollFrom.y + dWy);
	}
}

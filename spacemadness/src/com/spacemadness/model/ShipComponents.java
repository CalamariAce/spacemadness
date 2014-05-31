package com.spacemadness.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ShipComponents {

	private final JAXBContext jaxbContext;
	private final Marshaller jaxbMarshaller;
	private final Unmarshaller jaxbUnMarshaller;

	private ShipComponents() {
		try {
			jaxbContext = JAXBContext.newInstance(Entity.class);
			jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbUnMarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static ShipComponents defaultInstance;
	public static synchronized ShipComponents getInstance() {
		if (defaultInstance == null) {
			defaultInstance = new ShipComponents();
		}
		return defaultInstance;
	}

	public static class Point {
		int x, y;
		
		protected Point() {
		}

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@XmlAttribute(name="x")
		public void setX(int x) {
			this.x = x;
		}

		public int getX() {
			return x;
		}

		@XmlAttribute(name="y")
		public void setY(int y) {
			this.y = y;
		}
		
		public int getY() {
			return y;
		}
	}
	
	@XmlRootElement
	public static class Component {
		List<Point> outline = new ArrayList<>();

		public List<Point> getOutline() {
			return outline;
		}

		public void setOutline(List<Point> outline) {
			this.outline = outline;
		}
		
		public int[][] outlineAsArray() {
			int array[][] = new int[outline.size()][2];
			
			int i = 0;
			for (Point p : outline) {
				array[i][0] = p.x;
				array[i][1] = p.y;
				i++;
			}
			return array;
		}
	}
	
	@XmlRootElement
	public static class Entity {
		List<Component> component = new ArrayList<>();

		public List<Component> getComponent() {
			return component;
		}

		public void setComponent(List<Component> component) {
			this.component = component;
		}
	}

	public Ship getPrototype(String resourceName) {
		URL resource = ShipComponents.class.getClassLoader().getResource("com/spacemadness/model/ships/" + resourceName + ".xml");
		try {
			Entity entity = (Entity) jaxbUnMarshaller.unmarshal(resource);
			System.out.println(entity);

			Ship s = new Ship(entity.component.get(0).outlineAsArray());
			return s;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String args[]) throws Exception {
		ShipComponents s = new ShipComponents();
		
		s.getPrototype("carrier");
		
		Component c = new Component();
		c.getOutline().add(new Point(0, 0));
		c.getOutline().add(new Point(0, 1));
		c.getOutline().add(new Point(1, 1));
		
		Marshaller marshaller = s.jaxbMarshaller;
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(c, System.out);
	}
}

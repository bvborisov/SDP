package world.state;

import geometry.Vector;

public abstract class Entity {
	private Vector position = new Vector(0,0);
	
	public void setPosition(Vector position) {
		this.position = position;
	}
	public Vector getPosition() { return position; }
	
	abstract String name();

	public static double distance(Entity e1, Entity e2) {
		return Vector.distance(e1.position, e2.position);
	}
	public double distance(Entity e) {
		return distance(this, e);
	}
}

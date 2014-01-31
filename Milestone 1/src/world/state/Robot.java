package world.state;

public class Robot extends Entity {
	
	public double x;
	public double y;
	/** In radians*/
	public double bearing;
	public RobotType type;
	
	public Robot(RobotType type) {
		this.type = type;
	}
	

	public String name() {
		return "Robot (" + type.toString() + ")";
	}
	
	public String position() {
		return String.format("%s: %s bearing %f",
				this.type, this.getPosition(), this.bearing);
	}
	
}

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

public class EdgeFollower {

	static final LightSensor leftLight = new LightSensor(SensorPort.S4);;
	static final LightSensor rightLight = new LightSensor(SensorPort.S1);;
	static final DifferentialPilot pilot = new DifferentialPilot(5.6, 9.7, Motor.B, Motor.A, true);
	static final OdometryPoseProvider opp = new OdometryPoseProvider(pilot);
	
	private static boolean foundEdge = false;
	private static boolean departed = false;
	private static boolean returned = false;
	private static boolean stopped = false;
	
	private static double currentDisplacement;
	private static double minDisplacement;

	public static void main(String[] aArg) throws Exception {
		pilot.addMoveListener(opp);
		
		System.out.println("EdgeFollower");
		System.out.println("PRESS ANY BUTTON");
		Button.waitForAnyPress();
		
		while (!stop()) {
			if (seesOnlyGreen()) {
				pilot.setTravelSpeed(10);
				pilot.forward();
				while (seesOnlyGreen() && !stop()) {
					//Keep moving forward
				}
				pilot.stop();
			}
			else if (seesWhite()) {
				pilot.setTravelSpeed(5);
				pilot.rotateLeft();
				while (seesWhite()) {
					//Keep rotating
				}
				pilot.stop();
			}
		}
		System.out.println("PRESS ANY BUTTON");
		Button.waitForAnyPress();
	}
	
	private static boolean stop() {
		if (stopped) return true;
		updateDisplacements();
		if (hasReturned() && currentDisplacement-minDisplacement > 0.5) {
			System.out.println("STOPPED");
			stopped = true;
			return true;
		}else return false;
	}
	
	private static boolean hasReturned() {
		if (returned) return true;
		if (hasDeparted() && currentDisplacement < 10) {
			System.out.println("RETURNED");
			returned = true;
			return true;
		}else return false;
	}
	
	private static boolean hasDeparted() {
		if (departed) return true;
		if (hasFoundEdge() && currentDisplacement > 20) {
			System.out.println("DEPARTED");
			departed = true;
			return true;
		} else return false;
	}
	
	private static boolean hasFoundEdge() {
		if (foundEdge) return true;
		if (seesWhite()) {
			opp.setPose(new Pose());
			updateDisplacements();
			
			foundEdge = true;
			System.out.println("FOUND LINE");
			return true;
		}else return false;
	}
	
	private static void updateDisplacements() {
		currentDisplacement = Math.sqrt(
				Math.pow(opp.getPose().getX(), 2) + 
				Math.pow(opp.getPose().getY(), 2));
		
		if ((currentDisplacement < minDisplacement) && hasDeparted()) {
			minDisplacement = currentDisplacement;
		}
	}

	protected static boolean seesOnlyWhite() {
		return seesWhite(leftLight) && seesWhite(rightLight);
	}

	protected static boolean seesOnlyGreen() {
		return seesGreen(leftLight) && seesGreen(rightLight);
	}

	protected static boolean seesEdge() {
		return seesWhite(leftLight) != seesWhite(rightLight);
	}

	private static boolean seesWhite() {
		return seesWhite(leftLight) || seesWhite(rightLight);
	}

	private static boolean seesWhite(LightSensor light) {
		return light.readValue() > 40;
	}

	private static boolean seesGreen(LightSensor light) {
		return light.readValue() <= 40;
	}
}

/*
 * I'm a pretty lousy commenter but I tried adding some comments. 
 * I'm sure many comments are redundant and areas that should be commented are not.
 * Feel free to use the comment functionality of github or in some other way 
 * point out and ask about anything that is unclear. 
 */
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

public class EdgeFollower {

	/*
	 * Setting up the robot
	 */
	static final LightSensor leftLight = new LightSensor(SensorPort.S4);;
	static final LightSensor rightLight = new LightSensor(SensorPort.S1);;
	static final DifferentialPilot pilot = new DifferentialPilot(5.6, 9.7, Motor.B, Motor.A, true);
	static final OdometryPoseProvider opp = new OdometryPoseProvider(pilot);
	
	/*
	 * Flags to keep track of what conditions have been satisfied
	 */
	private static boolean foundEdge = false;
	private static boolean departed = false;
	private static boolean returned = false;
	private static boolean stopped = false;
	
	/*
	 * Fields to keep track of displacement
	 */
	private static double currentDisplacement;
	private static double minDisplacement;

	public static void main(String[] aArg) throws Exception {
		//Allows the pose to be updated also during a movement and not just after
		pilot.addMoveListener(opp);
		
		System.out.println("EdgeFollower");
		System.out.println("PRESS ANY BUTTON");
		Button.waitForAnyPress();
		
		while (!stop()) {
			/*
			 * Until the stop condition is satisfied the robot will try to follow the
			 * edge as closely as possible but never be on it. That is to say it will
			 * try to always have both sensors over a green area. 
			 */
			if (seesOnlyGreen()) {
				pilot.setTravelSpeed(10);
				pilot.forward();
				while (seesOnlyGreen() && !stop()) {
					/*
					 * Robot keeps moving forward until it either sees white or
					 * satisfies the stop condition
					 */
				}
				pilot.stop();
			}
			else if (seesSomeWhite()) {
				pilot.setTravelSpeed(5);
				pilot.rotateLeft();
				while (seesSomeWhite()) {
					/*
					 * The robot keeps rotating left until is only sees green
					 */
				}
				pilot.stop();
			}
		}
		System.out.println("PRESS ANY BUTTON");
		Button.waitForAnyPress();
	}
	
	/*
	 * The robot should stop once it is in the return zone and 
	 * the displacement from the origin starts increasing.
	 */
	private static boolean stop() {
		if (stopped) return true;
		updateDisplacements();
		if (hasReturned() && currentDisplacement-minDisplacement > 0.5) {
			System.out.println("STOPPED");
			stopped = true;
			return true;
		}else return false;
	}
	
	/*
	 * The robot is in the return zone if it has left the departure zone and
	 * its displacement from the origin is less than some threshold.
	 */
	private static boolean hasReturned() {
		if (returned) return true;
		if (hasDeparted() && currentDisplacement < 10) {
			System.out.println("RETURNED");
			returned = true;
			return true;
		}else return false;
	}
	
	/*
	 * The robot has left the departure zone if it has found the edge and
	 * its displacement from the origin is more than some threshold.
	 */
	private static boolean hasDeparted() {
		if (departed) return true;
		if (hasFoundEdge() && currentDisplacement > 20) {
			System.out.println("DEPARTED");
			departed = true;
			return true;
		} else return false;
	}
	
	/*
	 * The robot had found the edge if it at any point has encountered the color white.
	 */
	private static boolean hasFoundEdge() {
		if (foundEdge) return true;
		if (seesSomeWhite()) {
			/*
			 * When the robot first finds the edge it sets that position as the 
			 * origin in its positioning system. It also updates displacement values
			 * as these should now be 0;
			 */
			opp.setPose(new Pose());
			updateDisplacements();
			
			foundEdge = true;
			System.out.println("FOUND LINE");
			return true;
		}else return false;
	}
	
	/*
	 * The displacement is calculated as the euclidean distance from the origin to
	 * the current position of the robot.
	 */
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

	private static boolean seesSomeWhite() {
		return seesWhite(leftLight) || seesWhite(rightLight);
	}

	private static boolean seesWhite(LightSensor light) {
		return light.readValue() > 40;
	}

	private static boolean seesGreen(LightSensor light) {
		return light.readValue() <= 40;
	}
}

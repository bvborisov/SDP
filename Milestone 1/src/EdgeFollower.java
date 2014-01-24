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
	
	private static double minDisplacement = -1;

	public static void main(String[] aArg) throws Exception {
		pilot.addMoveListener(opp);
		
		System.out.println("EdgeFollower\n(No Behaviours)");
		Button.waitForAnyPress();
		
		while (!hasReturned()) {
			if (seesEdge() || !foundEdge) {
				pilot.setTravelSpeed(10);
				pilot.forward();
				while ((seesEdge() || !foundEdge) && !hasReturned()) {
					//Keep moving forward
				}
				pilot.stop();
			}
			else if (seesOnlyGreen()) {
				pilot.setTravelSpeed(5);
				pilot.rotateRight();
				while (seesOnlyGreen()) {
					//Keep rotating
				}
				pilot.stop();
			}
			else if (seesOnlyWhite()) {
				pilot.setTravelSpeed(5);
				pilot.rotateLeft();
				while (seesOnlyWhite()) {
					//Keep rotating
				}
				pilot.stop();
			}
		}
		
		System.out.println("ÁDONE!");
		Button.waitForAnyPress();
	}

	protected static boolean hasReturned() {
		if (hasDeparted() && (displacement() < 1)) {
			returned = true;
		}
		return returned;
	}

	private static boolean hasDeparted() {
		if (hasFoundEdge() && (displacement() > 15)) {
			departed = true;
		}

		return departed;
	}

	private static boolean hasFoundEdge() {
		if (!foundEdge && seesWhite()) {
			opp.setPose(new Pose());
			foundEdge = true;
		}
		return foundEdge;
	}

	private static double displacement() {
		double d = Math.sqrt(
				Math.pow(opp.getPose().getX(), 2) + 
				Math.pow(opp.getPose().getY(), 2));
		if ((d < minDisplacement || minDisplacement < 0) && departed) {
			minDisplacement = d;
			System.out.println("MIN " + minDisplacement);// #TODO
		}
		return d;
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

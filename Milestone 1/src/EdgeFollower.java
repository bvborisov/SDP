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

	public static void main(String[] aArg) throws Exception {
		pilot.setRotateSpeed(20);
		pilot.setTravelSpeed(10);
		pilot.addMoveListener(opp);
		
		System.out.println("EdgeFollower\n(No Behaviours)");
		Button.waitForAnyPress();
		
		while (!hasReturned()) {
			if (seesEdge() || !foundEdge) {
				pilot.forward();
				while ((seesEdge() || !foundEdge) && !hasReturned()) {
					//Keeping moving forward
				}
				pilot.stop();
			}
			else if (seesOnlyGreen()) {
				pilot.rotateRight();
				while (seesOnlyGreen()) {
					//Keep rotating
				}
				pilot.stop();
			}
			else if (seesOnlyWhite()) {
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
		// System.out.println(opp.getPose());//#TODO
		System.out.println(" D:" + displacement());// #TODO
		if (hasDeparted() && (displacement() < 5)) {
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
			pilot.stop();
			opp.setPose(new Pose());
			foundEdge = true;
		}
		return foundEdge;
	}

	private static double displacement() {
		return Math.sqrt(
				Math.pow(opp.getPose().getX(), 2) + 
				Math.pow(opp.getPose().getY(), 2));
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

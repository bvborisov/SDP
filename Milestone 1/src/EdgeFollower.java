
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class EdgeFollower {
	
	static final LightSensor leftLight = new LightSensor(SensorPort.S4);;
	static final LightSensor rightLight = new LightSensor(SensorPort.S1);;
	static final DifferentialPilot pilot = new DifferentialPilot(2.2f, 5.0F, Motor.B, Motor.A, true);
	static final OdometryPoseProvider opp = new OdometryPoseProvider(pilot);
	
	static boolean foundEdge = false;
	
	public static void main (String[] aArg)
	throws Exception
	{
		pilot.setRotateSpeed(20);
		pilot.setTravelSpeed(5);
		
		Behavior DriveForward = new Behavior()
		{
			public boolean takeControl() {return seesEdge() || !foundEdge;}
			
			public void suppress() {
				pilot.stop();
			}
			public void action() {
				pilot.forward();
                while(seesEdge()) Thread.yield();
			}					
		};
		
		Behavior OffEdge = new Behavior()
		{
			private boolean suppress = false;
			
			public boolean takeControl() {return !seesEdge();}

			public void suppress() {
				suppress = true;
			}
			
			public void action() {
				if (seesOnlyGreen()) {
					pilot.rotateLeft();
				} else {
					pilot.rotateRight();
				}
				while (!suppress && pilot.isMoving()) Thread.yield();
				pilot.stop();
				suppress = false;
				}
		};

		Behavior[] bArray = {OffEdge, DriveForward};
        LCD.drawString("EdgeFollower ", 0, 1);
        Button.waitForAnyPress();
	    (new Arbitrator(bArray)).start();
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
	
	private static boolean seesWhite(LightSensor light) {
		if (light.readValue() > 40) {
			foundEdge = true;
			return true;
		} else {
			return false;
		}
	}

	private static boolean seesGreen(LightSensor light) {
		return light.readValue() <= 40;
	}
}


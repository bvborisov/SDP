
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.RotateMoveController;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.util.PilotProps;

public class EdgeFollower {
	
	static LightSensor leftLight;
	static LightSensor rightLight;
	
	static boolean foundEdge = false;
	
	public static void main (String[] aArg)
	throws Exception
	{
     	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.96"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "13.0"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
		final DifferentialPilot pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		leftLight = new LightSensor(SensorPort.S4);
		rightLight = new LightSensor(SensorPort.S1);
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


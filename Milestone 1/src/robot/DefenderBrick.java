package robot;

import java.io.InputStream;
import java.lang.Object;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.OmniPilot;

public class DefenderBrick {

	//Setting up the robot
	//static final OmniPilot pilot = new OmniPilot(6.5f, 4.7f, Motor.A, Motor.C, Motor.B, true, true);
	private final static DifferentialPilot pilot = new DifferentialPilot(13.4, 16.5, Motor.B, Motor.A);
	private static double rotateSpeed = 5;
    private static double travelSpeed = 20;
	private static InputStream dis;

    // Commands
 	private final static int NOTHING = 0;
 	private final static int FORWARDS = 1;
 	private final static int BACKWARDS = 2;
 	private final static int STOP = 3;
 //	private final static int KICK = 4;
 	private final static int SPEED = 5;
 	private final static int ROTATELEFT = 6;
 	private final static int ROTATERIGHT = 7;
 	private final static int QUIT = 9;
 	
 	public static void main(String[] args) throws Exception {
 	//Set initial command and initialise options
	int command = NOTHING;
	int option1, option2, option3;
			
	while (true) {
 

		Button.waitForAnyPress();
		pilot.setTravelSpeed(20.0);
		pilot.setRotateSpeed(30);
		pilot.travel(-30.0);
		pilot.stop();
//		Button.waitForAnyPress();
		
		pilot.rotate(90);
//		Button.waitForAnyPress();
		pilot.rotate(-90);
//		Button.waitForAnyPress();
		
		pilot.setTravelSpeed(20.0);
		pilot.travel(30.0);
		pilot.stop();
		Button.waitForAnyPress();
		
	}

		
			
		
				//pilot.backward();
	
//				pilot.stop();
		
				
//			case KICK:
//				
//				break;
	  			
		

	
			
	
	//			pilot.rotate(90);

			

		//		pilot.rotate(-90);


	}
 	}
	
	
 	
 	

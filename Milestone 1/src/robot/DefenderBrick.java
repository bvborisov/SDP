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
 	
 	public static void main(String[] args) throws Exception {
			
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

		


	}
 	}
	
	
 	
 	

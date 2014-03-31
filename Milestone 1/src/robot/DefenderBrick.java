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
	private final static DifferentialPilot pilot = new DifferentialPilot(8.16, 12.2, Motor.B, Motor.A);
 	
 	public static void main(String[] args) throws Exception {
			
	while (true) {
 

		Button.waitForAnyPress();
		pilot.setRotateSpeed(800);
		pilot.setTravelSpeed(20);
		
		pilot.travel(-30);
		Button.waitForAnyPress();
		pilot.setTravelSpeed(25);
		pilot.travel(30);
		Button.waitForAnyPress();
		pilot.setTravelSpeed(30);
		pilot.travel(-30);
		Button.waitForAnyPress();
		pilot.setTravelSpeed(35);
		pilot.travel(30);
		Button.waitForAnyPress();
		pilot.stop();
		
	}

		


	}
 	}
	
	
 	
 	

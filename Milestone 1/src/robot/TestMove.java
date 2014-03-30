package robot;

import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class TestMove {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DifferentialPilot pilot = new DifferentialPilot(13.4, 16.5, Motor.B, Motor.A);
		//pilot.setTravelSpeed(10);
		pilot.travel(10);
	}

}

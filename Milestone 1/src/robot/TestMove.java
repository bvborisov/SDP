package robot;

import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class TestMove {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DifferentialPilot pilot = new DifferentialPilot(8.16, 12.2, Motor.B, Motor.A);
		//pilot.setTravelSpeed(10);
		pilot.travel(10);
		pilot.rotate(90);
	}

}

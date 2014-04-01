package robot;

import lejos.nxt.Button;
import lejos.nxt.Motor;

public class TestK {

	public static void main(String[] args) {

		while (true) {
			
			Button.waitForAnyPress();
			Motor.C.setSpeed(400);
			Motor.C.rotate(-60);
			Button.waitForAnyPress();
			Motor.C.setSpeed(900);
			Motor.C.rotate(80);
			
		}
		
	}
	
}

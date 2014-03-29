package robot;

import lejos.nxt.Button;
import lejos.nxt.Motor;

public class TestK {

	public static void main(String[] args) {
		
		Motor.C.setSpeed(250);
		
		while (true) {
			Button.waitForAnyPress();
			Motor.C.rotate(-90);
			Button.waitForAnyPress();
			Motor.C.rotate(90);
		}
		
		
	}
	
}

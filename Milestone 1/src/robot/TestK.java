package robot;

import lejos.nxt.Button;
import lejos.nxt.Motor;

public class TestK {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Motor.C.setSpeed(20000);
		
		while (true) {
			Button.waitForAnyPress();
			Motor.C.rotate(-90);
			Button.waitForAnyPress();
			Motor.C.rotate(90);
			
		}
	}

}

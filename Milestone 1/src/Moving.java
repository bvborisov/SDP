
import lejos.nxt.ADSensorPort;
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.*;

public class Moving {
    /**
     * @param args
     */
		
	static LightSensor lightSensor = new LightSensor((ADSensorPort) SensorPort.S1);
	static TouchSensor touchSensor = new TouchSensor((ADSensorPort) SensorPort.S2);
    
	public static void main(String[] args) {
        
		while (true) {
		
			int x = lightSensor.readValue();
			System.out.println(x);  
			Motor.A.setSpeed(700);  
			Motor.B.setSpeed(700);
		
			if (x > 40) {

				Motor.A.backward();
				Motor.B.backward();
				
				while (x > 40) {
					x = lightSensor.readValue();
					Motor.A.setSpeed(100);
					Motor.B.setSpeed(100);
					
					Motor.A.forward();
				    Motor.B.backward();
					
				}
				
				Motor.A.stop();
				Motor.B.stop();
//				
//				
//				Motor.A.rotate(90);
//				Motor.B.rotate(-90);
//				while(Motor.A.isMoving() && Motor.B.isMoving()) { 
//						Thread.yield();				
//				}
				
//				Button.waitForAnyPress();
			}
			
			Motor.A.forward();
			Motor.B.forward();
			
//			Button.waitForAnyPress();
			
		}
               
    }
}

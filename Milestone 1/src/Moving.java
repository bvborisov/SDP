
import lejos.nxt.*;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.DifferentialPilot;

public class Moving {
    /**
     * @param args
     */
		
	static LightSensor lightSensor1 = new LightSensor((ADSensorPort) SensorPort.S1);
	static LightSensor lightSensor2 = new LightSensor((ADSensorPort) SensorPort.S4);
	static TouchSensor touchSensor = new TouchSensor((ADSensorPort) SensorPort.S2);
	static DifferentialPilot pilot = new DifferentialPilot(2.2f, 5.125f, Motor.A, Motor.B, false);	
	
	public static void main(String[] args) {
        
		pilot.setTravelSpeed(7);

		
		while (true) {
		    int x = lightSensor1.readValue();
			int y = lightSensor2.readValue();
			int z;
			int distance;
			
//			Motor.A.setSpeed(550);  
//			Motor.B.setSpeed(550);
		
			if (x > 40 || y > 40) {
			    pilot.setRotateSpeed(50);
				while (x > 40 || y > 40) {
					x = lightSensor1.readValue();
					y = lightSensor2.readValue();
//					Motor.A.setSpeed(200);
//					Motor.B.setSpeed(200);
//					
//					Motor.A.forward();
//				    Motor.B.backward();
					pilot.rotateLeft();
					
				}
			}
			pilot.forward();
		}
               
    }
	
	public void returnBack(int distance, int x, int y, int z) {
		while (true) {
			x = lightSensor1.readValue();
			y = lightSensor2.readValue();
			
			if (x > 40 || y > 40) {

				Motor.A.backward();
				Motor.B.backward();
			
				while (x > 40 || y > 40) {
					x = lightSensor1.readValue();
					y = lightSensor2.readValue();
					Motor.A.setSpeed(200);
					Motor.B.setSpeed(200);
					
					Motor.A.forward();
				    Motor.B.backward();
					
				}
				z = Motor.B.getLimitAngle();
				System.out.println(z);
				Motor.A.stop();
				Motor.B.stop();

			}
		}
	}	
}

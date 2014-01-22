
import lejos.nxt.*;
//import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.DifferentialPilot;

public class Moving {
		
	static LightSensor lightSensor1 = new LightSensor((ADSensorPort) SensorPort.S1);
	static LightSensor lightSensor2 = new LightSensor((ADSensorPort) SensorPort.S4);
	static DifferentialPilot pilot = new DifferentialPilot(2.2f, 5.125f, Motor.A, Motor.B, false);	
	
	public static void main(String[] args) {
        System.out.println("trying out egit");
		pilot.setTravelSpeed(7);

		boolean reached = false;
		
		int x = lightSensor1.readValue();
		int y = lightSensor2.readValue();
		float distance = 0;
		
		while (!reached) {
		    x = lightSensor1.readValue();
			y = lightSensor2.readValue();

			if (x > 40 || y > 40) {
			    pilot.setRotateSpeed(40);

			    while (x > 40 || y > 40) {
					x = lightSensor1.readValue();
					y = lightSensor2.readValue();
     				pilot.rotateLeft();	
				}
				reached = true;
			} else { 
				pilot.forward();
			}
		}          
		
		while (distance < 30) {

		    x = lightSensor1.readValue();
			y = lightSensor2.readValue();
			
			if (x > 40 || y > 40) {
			    pilot.setRotateSpeed(40);
			    distance += pilot.getMovementIncrement();
				while (x > 40 || y > 40) {
					x = lightSensor1.readValue();
					y = lightSensor2.readValue();
     				pilot.rotateLeft();	
				}
			} else { 
				pilot.forward();
				distance += pilot.getMovementIncrement();
			}
		
		}
		
    }
	
	
//	public static void returnBack(int x, int y, float z, float distance) {
//		
//		pilot.setTravelSpeed(7);
//		float angleTurned =0;
//		
//		while (angleTurned < 180) {
//		    x = lightSensor1.readValue();
//			y = lightSensor2.readValue();
//			
//			if (x > 40 || y > 40) {
//			    pilot.setRotateSpeed(50);
//				while (x > 40 || y > 40) {
//					x = lightSensor1.readValue();
//					y = lightSensor2.readValue();
//     				pilot.rotateLeft();
//     				angleTurned += pilot.getAngleIncrement();
//				}
//			}
//			pilot.forward();
//		}  
//		
//		pilot.rotate(-z);
//		pilot.travel(20, false);
//	}	
}


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

		boolean reached = false;
		
		while (!reached) {
		    int x = lightSensor1.readValue();
			int y = lightSensor2.readValue();
			float z = 0;
			float distance = 0;
		
			if (x > 40 || y > 40) {
			    pilot.setRotateSpeed(50);
			    distance += pilot.getMovementIncrement();
				while (x > 40 || y > 40) {
					x = lightSensor1.readValue();
					y = lightSensor2.readValue();
     				pilot.rotateLeft();	
     				z += pilot.getAngleIncrement();
				}
			    //z = pilot.getAngleIncrement();
			    System.out.println(z);
			    System.out.println(distance);
				returnBack(x, y, z, distance);
				reached = true;
			} else{ 
				pilot.forward();
				distance += pilot.getMovementIncrement();
			}
		}              
    }
	
	public static void returnBack(int x, int y, float z, float distance) {
		
		pilot.setTravelSpeed(7);
		float angleTurned =0;
		
		while (angleTurned < 180) {
		    x = lightSensor1.readValue();
			y = lightSensor2.readValue();
			
			if (x > 40 || y > 40) {
			    pilot.setRotateSpeed(50);
				while (x > 40 || y > 40) {
					x = lightSensor1.readValue();
					y = lightSensor2.readValue();
     				pilot.rotateLeft();
     				angleTurned += pilot.getAngleIncrement();
				}
			}
			pilot.forward();
		}  
		
		pilot.rotate(-z);
		pilot.travel(20, false);
	}	
}

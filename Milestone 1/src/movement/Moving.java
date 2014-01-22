package movement;

import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;
import lejos.nxt.*;

public class Moving {
	
	static LightSensor lightSensor1 = new LightSensor((ADSensorPort) SensorPort.S4);
	static LightSensor lightSensor2 = new LightSensor((ADSensorPort) SensorPort.S1);
	static DifferentialPilot pilot = new DifferentialPilot(2.2f, 5.0F, Motor.A, Motor.B, true);

//  Code for the robot to use odometry	
//	static OdometryPoseProvider opp;
//	static Pose pose;
//	static Pose temp;
	
	public static void main(String[] args) {
		
		int left = lightSensor1.readValue();
		int right = lightSensor2.readValue();
		float distance = 0; 
		
//		opp = new OdometryPoseProvider(pilot);
//		Checking if it is the first time the loop is run
//		int x = 0;
		
		while(true) {
//			counter
//			x++;
			
			left = lightSensor1.readValue();
			right = lightSensor2.readValue();
			
			boolean onWhite = (left > 40) && (right > 40);
			boolean onGreen = (left < 40) && (right < 40);
			boolean onLine = (left < 40 && right > 40); //When left is white and right is on green
			
			//System.out.println("left: " + left + " right: " + right);
			
//			Gets pose of robot at current postion
//			pose = opp.getPose();		
//			if (temp != pose) {
			
				if (onWhite) { //Both on White
					
//					Gets pose of starting position and assigns it to a temp pose
//					if (x == 1) {
//						pose = opp.getPose();
//						temp = pose;
//					}
					//System.out.println("On White");
					
					while (!onLine) {
						
						left = lightSensor1.readValue();
						right = lightSensor2.readValue();
						onLine = (left < 40 && right > 40);
						
						//System.out.println("left: " + left + " right: " + right);
						pilot.setRotateSpeed(20);
						pilot.rotateRight();
						
					}
					
					//System.out.println("On Line1");
					pilot.setTravelSpeed(5);
					pilot.forward();
			
				} else if (onLine) {
					
					//System.out.println("On Line2");
					pilot.setTravelSpeed(8);
					pilot.forward();
					distance += pilot.getMovementIncrement();
					System.out.println(distance);
					
				} else if (onGreen) {
					
					//System.out.println("On Green");
					pilot.setTravelSpeed(8);
					pilot.forward();
					distance += pilot.getMovementIncrement();
					System.out.println(distance);
					
				}
				
//			Stops robot once initial pose is reached
//			}else {
//				pilot.stop();
//			}
		}
		
	}	
}

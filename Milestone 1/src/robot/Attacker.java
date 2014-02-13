package robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.LCD;
import lejos.nxt.Button;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class Attacker {
	// IO control
	private static InputStream dis;
	private static OutputStream dos;

	// Commands
	private final static int NOTHING = 0;
	private final static int FORWARDS = 1;
	private final static int BACKWARDS = 2;
	private final static int STOP = 3;
	private final static int KICK = 4;
	private final static int SPEED = 5;
	private final static int ROTATELEFT = 6;
	private final static int ROTATERIGHT = 7;
	private final static int MOVING = 8;
	private final static int QUIT = 9;
	
	private final static DifferentialPilot pilot = new DifferentialPilot(5.6, 6.7, Motor.B, Motor.A, false);

	public static void main(String [] args)  throws Exception {
		try {
			//Wait for Bluetooth connection
			LCD.drawString("Waiting for connection...",0,0);
			BTConnection btc = Bluetooth.waitForConnection();

			//Show connected and open data streams
			LCD.clear();
			LCD.drawString("Connected!", 0, 0);
			dis = btc.openInputStream();
			dos = btc.openOutputStream();

			//Send PC ready state code
			byte[] readyState = { 0, 0, 0, 0 };
			dos.write(readyState);
			dos.flush();

			//Set initial command and initialise options
			int command = NOTHING;
			int option1, option2, option3;

			while ((command != QUIT) && !(Button.ESCAPE.isDown())) {
				// Get command from the input stream
				byte[] byteBuffer = new byte[4];
				dis.read(byteBuffer);

				// We receive 4 numbers
				command = byteBuffer[0];
				option1 = byteBuffer[1];
				option2 = byteBuffer[2];
				option3 = byteBuffer[3];

				//Based on the received command call the relevant method
				switch(command) {
					case FORWARDS:
						LCD.clear();
						LCD.drawString("Forwards!", 0, 0);
						forwards();
						break;
					case BACKWARDS:
						LCD.clear();
						LCD.drawString("BACKWARDS!", 0, 0);
						backwards();
						break;				
					case STOP:
						LCD.clear();
						LCD.drawString("STOP!", 0, 0);
						stop();
						break;						
					case KICK:
						LCD.clear();
						LCD.drawString("KICK!", 0, 0);
						kick();
						break;			
					case SPEED:
						LCD.clear();
						LCD.drawString("SPEED SET!", 0, 0);
						setSpeed(option1);
						break;
					case ROTATELEFT:
						LCD.clear();
						LCD.drawString("ROTATE LEFT!", 0, 0);
						rotateLeft(option1);
						break;	
					case ROTATERIGHT:
						LCD.clear();
						LCD.drawString("ROTATE RIGHT!", 0, 0);
						rotateRight();
						break;
					case QUIT:
						LCD.clear();
						LCD.drawString("QUIT!", 0, 0);
						//Add call to FORWARDS method
						break;
					case MOVING:
						moving();
					default:
				}
			}
			//Now close data streams and connection
			dis.close();
			dos.close();
			Thread.sleep(100);
			LCD.clear();
			LCD.drawString("Closing connection", 0, 0);
			btc.close();
		} catch (Exception e) {
			LCD.drawString("Exception:", 0, 2);
			String msg = e.getMessage();
			if (msg != null)
				LCD.drawString(msg, 2, 3);
			else
				LCD.drawString("Error message is null", 2, 3);
		}
	}
	
	public static void forwards() throws IOException {
		pilot.forward();
		byte[] distance = toBytes((int)pilot.getMovementIncrement() );
		dos.write(distance);
		dos.flush();
	}
	
	public static byte[] toBytes(int i)
	{
	  byte[] result = new byte[4];

	  result[0] = (byte) (i >> 24);
	  result[1] = (byte) (i >> 16);
	  result[2] = (byte) (i >> 8);
	  result[3] = (byte) (i /*>> 0*/);

	  return result;
	}
	
	public static void backwards() {
		pilot.backward();
	}
	
	public static void stop() {
		pilot.stop();
	}
	
	public static void kick() {
		Motor.C.setSpeed(900);
		Motor.C.rotate(80);	
		Motor.C.rotate(-80);
	}
	
	public static void setSpeed(int speed) {
		pilot.setTravelSpeed(speed);
	}
	
	public static void rotateLeft(int angle) {
		pilot.setRotateSpeed(10);
		pilot.rotate(angle);
	}
	public static void rotateRight() {
		pilot.setRotateSpeed(10);
		pilot.rotateRight();
	}
	
	public static void moving() throws IOException{
		byte[] isMOving = {0,0,0,1};
		byte[] isNotMoving = {0,0,1,0};
		if (pilot.isMoving()) {
			dos.write(isMOving);
			dos.flush();
		} else {
			dos.write(isNotMoving);
			dos.flush();	
		}
	}
}
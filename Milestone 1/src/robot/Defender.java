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

public class Defender {
	// IO control
	private static InputStream dis;
	private static OutputStream dos;


	// Commands
	private final static int NOTHING = 0;
	private final static int FORWARDS = 1;
	private final static int BACKWARDSC = 2;
	private final static int STOP = 3;
	private final static int GRAB = 4;
	private final static int KICK = 5;
	private final static int SPEED = 6;
	//private final static int ROTATESPEED = 7;
	private final static int ROTATELEFT = 7;
	private final static int ROTATERIGHT = 8;
	private final static int MOVING = 9;
	private final static int QUIT = 10;
	private final static int FORWARDSC = 11;

	//private final static DifferentialPilot pilot = new DifferentialPilot(28.0, 15.0, Motor.B, Motor.A, false);	private final static DifferentialPilot pilot = new DifferentialPilot(13.4, 16.5, Motor.B, Motor.A);
	//private final static DifferentialPilot pilot = new DifferentialPilot(13.4, 16.5, Motor.B, Motor.A);
	private final static DifferentialPilot pilot = new DifferentialPilot(8.16, 12.2, Motor.B, Motor.A);

	public static void main(String [] args)  throws Exception {

			while (true) {
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

				while ((command != QUIT) && !(Button.ENTER.isDown())) {
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
							forwards(option1, option2);
							break;
						case BACKWARDSC:
							LCD.clear();
							LCD.drawString("BACKWARDS!", 0, 0);
							setSpeed(20);
							forwardsC();
							break;					
						case STOP:
							LCD.clear();
							LCD.drawString("STOP!", 0, 0);
							stop();
							break;		
						case GRAB:
							LCD.clear();
							LCD.drawString("GRAB!", 0, 0);
							grab();
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
						/*case ROTATESPEED:
							LCD.clear();
							LCD.drawString("SPEED SET!", 0, 0);
							setRotateSpeed(option1);
							break;
						*/case ROTATELEFT:
							LCD.clear();
							LCD.drawString("ROTATE LEFT!", 0, 0);
							rotateLeft(option1, option2);
							break;	
						case ROTATERIGHT:
							LCD.clear();
							LCD.drawString("ROTATE RIGHT!", 0, 0);
							rotateRight(option1, option2, option3);
							break;
						case MOVING:
							LCD.clear();
							LCD.drawString("MOVING?", 0, 0);
							moving();
							break;
						case FORWARDSC:
							LCD.clear();
							LCD.drawString("FORWARDSC!", 0, 0);
							setSpeed(20);
							backwardsC();
							break;
						case QUIT:
							LCD.clear();
							LCD.drawString("QUIT!", 0, 0);
							//Add call to FORWARDS method
							break;						
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
				}
			    catch (Exception e) {
				LCD.drawString("Exception:", 0, 2);
				String msg = e.getMessage();
				if (msg != null)
					LCD.drawString(msg, 2, 3);
				else
					LCD.drawString("Error message is null", 2, 3);
			}
	 	}
	}
			
	
	public static void forwards(int distance, int distance2) throws Exception {
		pilot.setTravelSpeed(20);
		pilot.travel(-(distance + distance2), false);
		done();
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

	public static void backwards() throws Exception {
		pilot.forward();
		done();
	}
	
	public static void stop() throws Exception {
		pilot.stop();
		done();
	}
	
	public static void grab() throws Exception {
		Motor.C.setSpeed(400);
		Motor.C.rotate(-75);
		done();
	}
	
	public static void kick() throws Exception {
		Motor.C.setSpeed(900);
		Motor.C.rotate(75);
		done();
	}
	
	public static void rest() throws Exception {
		Motor.C.setSpeed(40);
		Motor.C.rotate(-10);
		done();
	}
	
	public static void setSpeed(int speed) throws Exception {
		pilot.setTravelSpeed(speed);
		pilot.setRotateSpeed(speed);
		done();
	}
	

	public static void setRotateSpeed(int speed) throws Exception {
		pilot.setRotateSpeed(speed);
		done();
	}
	
	public static void rotateLeft(double angle1, double angle2) throws Exception {
		pilot.setRotateSpeed(700);
		pilot.rotate(-(angle1 + angle2), false);
		done();
	}
	public static void rotateRight(double angle1, double angle2, double angle3) throws Exception {
		double turned = 0.0;
		while (turned < angle1 + angle2 + angle3) {
			pilot.rotateRight();
			turned += pilot.getAngleIncrement();
		}
		done();
	}
	
	public static void moving() throws Exception {
		byte[] moving = {0, 0, 0, 0};
		if (pilot.isMoving()) {
			moving[3]= (byte)1;
			dos.write(moving);
			dos.flush();
		} else {
			dos.write(moving);
			dos.flush();
		}
		done();
	}
	
	public static void done() throws Exception {
		byte[] done = {12, 0, 0, 0};
	    dos.write(done);
	    dos.flush();
	}
	public static void forwardsC() throws Exception {
		
		//Do done first to let PC know that the robot is ready for another command
		// but keep in mind that the next command must be waited on the pc end
		
		done();
		pilot.forward();
	}
	
	public static void backwardsC() throws Exception {
		
		//Do done first to let PC know that the robot is ready for another command
		// but keep in mind that the next command must be waited on the pc end
		
		done();
		pilot.backward();
	}
}

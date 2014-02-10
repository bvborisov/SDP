package comms;

import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.LCD;
import lejos.nxt.Button;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class Robot {
	// IO control
	private static InputStream dis;
	private static OutputStream dos;

	// Commands
	private final static int NOTHING = 0;
	private final static int FORWARDS = 1;
	private final static int BACKWARDS = 2;
	private final static int STOP = 3;
	private final static int KICK = 4;
	private final static int ROTATE = 6;
	private final static int QUIT = 9;
	
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
						//Add call to FORWARDS method
						break;
					case BACKWARDS:
						LCD.clear();
						LCD.drawString("BACKWARDS!", 0, 0);
						//Add call to FORWARDS method
						break;				
					case STOP:
						LCD.clear();
						LCD.drawString("STOP!", 0, 0);
						//Add call to FORWARDS method
						break;						
					case KICK:
						LCD.clear();
						LCD.drawString("KICK!", 0, 0);
						//Add call to FORWARDS method
						break;					
					case ROTATE:
						LCD.clear();
						LCD.drawString("ROTATE!", 0, 0);
						//Add call to FORWARDS method
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
		} catch (Exception e) {
			LCD.drawString("Exception:", 0, 2);
			String msg = e.getMessage();
			if (msg != null)
				LCD.drawString(msg, 2, 3);
			else
				LCD.drawString("Error message is null", 2, 3);
		}
	}
}

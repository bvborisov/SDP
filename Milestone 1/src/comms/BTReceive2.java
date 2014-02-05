package comms;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class BTReceive2 {

	public static void main(String [] args)  throws Exception {
		String connected = "Connected";
        String waiting = "Waiting...";
        String closing = "Closing...";
        
		while (true) {
			//Show that device is waiting for connection
			LCD.drawString(waiting,0,0);
			LCD.refresh();

			//Listen for bluetooth connection
	        BTConnection btc = Bluetooth.waitForConnection();
	        
	        //Show that device is connected
			LCD.clear();
			LCD.drawString(connected,0,0);
			LCD.refresh();	

			//Open data streams
			DataInputStream dis = btc.openDataInputStream();
			DataOutputStream dos = btc.openDataOutputStream();
			
			for(int i=0;i<50;i++) {
				int n = dis.readInt();
				LCD.drawInt(n,7,0,1);
				LCD.refresh();
				//dos.writeInt(-n);
				//dos.flush();
			}
			
			//Close connections and update streams
			dis.close();
			dos.close();
			//btc.close();
		}
	}
}


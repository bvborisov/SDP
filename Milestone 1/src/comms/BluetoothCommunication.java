package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 * Provides communication between PC and Robot
 * 
 * @author Sarun Gulyanon
 * @author Richard Kenyon
 * @author Nikolay Bogoychev
 */
public class BluetoothCommunication {

	public static final int[] ROBOT_READY = { 0, 0, 0, 0 };
	private InputStream in;
	private OutputStream out;
	private NXTComm nxtComm;
	private NXTInfo nxtInfo;
	private boolean robotReady = false;
	private boolean connected = false;
	private int buffer = 0;

	/**
	 * @param deviceName
	 *            The name of the Bluetooth device
	 * @param deviceMACAddress
	 *            The MAC address of the Bluetooth device
	 */
	public BluetoothCommunication(String deviceName, String deviceMACAddress) {
		nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, deviceName,
				deviceMACAddress);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (connected) {
					closeBluetoothConnection();
				}
			}
		});
	}

	/**
	 * Returns true if the server is connected to the robot, returns false
	 * otherwise
	 * 
	 * @return a boolean indicating whether the server is connected to the robot
	 *         or not
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Receive a byte from the robot
	 * 
	 * @return An integer containing the byte we received from the robot
	 * 
	 * @throws IOException
	 *             when fail to receive a byte from robot
	 */
	public int[] receiveFromRobot() throws IOException {
		byte[] res = new byte[4];
		in.read(res);
		int[] ret = { (int) (res[0]), (int) (res[1]), (int) (res[2]),
				(int) (res[3]) };
		return ret;
	}

	/**
	 * Returns whether the robot is ready to receive data or not. Always check
	 * that the robot is ready before sending any commands.
	 */
	public boolean isRobotReady() {
		return robotReady;
	}

	public void clearBuff() {
		buffer = 0;
	}

	/**
	 * Send 4 byte commands to the robot. Simple version that does not keep
	 * track of the buffer or require confirmation of received package.
	 * 
	 * @param comm
	 *            - int [] with 4 elements, first element is the opcode the rest
	 *            are options that can be passed.
	 * @throws IOException
	 *             when fail to send command to robot
	 */

	public void sendToRobotSimple(int[] comm) throws IOException {
		if (!connected)
			return;
		byte[] command = { (byte) comm[0], (byte) comm[1], (byte) comm[2],
				(byte) comm[3] };

		out.write(command);
		out.flush();
	}

	/**
	 * Send 4 byte commands to the robot. This version keeps track of the buffer
	 * and requires confirmation to be received from a package.
	 * 
	 * @param comm
	 *            - int [] with 4 elements, first element is the opcode the rest
	 *            are options that can be passed.
	 * @throws IOException
	 *             when fail to send command to robot
	 * 
	 * @return Returns the opcode sent (if successful), -1 if we couldn't send
	 *         the command because the buffer was full or -2 if we received
	 *         wrong opcode or didn't receive confirmation or -3 if the
	 *         connection is not open
	 */

	public int sendToRobot(int[] comm) throws IOException {
		if (!connected)
			return -3;
		if (buffer < 2) {
			byte[] command = { (byte) comm[0], (byte) comm[1], (byte) comm[2],
					(byte) comm[3] };

			out.write(command);
			out.flush();
			buffer += 1;
		} else {
			// The buffer is full we can't send a package;
			return -1;
		}

		int[] confirmation;
		try {
			confirmation = receiveFromRobot();
			if (confirmation[1] == comm[0]) {
				buffer -= 1;
				return confirmation[1];
			}
		} catch (IOException e1) {
			System.out.println("Could not receive confirmation");
			buffer -= 1;
			return -2;
		}
		System.out.println("Buffer is full, command not sent!");
		return -2;
	}

	/**
	 * Opens a new Bluetooth connection and connects the input and output
	 * streams to this new Bluetooth connection
	 * 
	 * @throws IOException
	 *             when we fail to open the Bluetooth connection
	 */
	public void openBluetoothConnection() throws IOException {
		try {
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		} catch (NXTCommException e) {
			System.err.println("Could not create connection: " + e.toString());
		}

		System.out.println("Attempting to connect to robot...");

		try {
			nxtComm.open(nxtInfo);
			in = nxtComm.getInputStream();
			out = nxtComm.getOutputStream();

			while (true) {
				int[] res = receiveFromRobot();
				boolean equals = true;
				for (int i = 0; i < 4; i++) { // wait for ready signal
					if (res[i] != ROBOT_READY[i]) {
						equals = false;
						break;
					}
				}
				if (equals) {
					break;
				} else {
					Thread.sleep(10); // Prevent 100% cpuusage
				}
			}

			robotReady = true;
			System.out.println("Connected to robot!");
			connected = true;
		} catch (NXTCommException e) {
			throw new IOException("Failed to connect " + e.toString());
		} catch (InterruptedException e) {
			throw new IOException("Failed to connect " + e.toString());
		}
	}

	/**
	 * Closes the Bluetooth connection and closes the input and output streams
	 */
	public void closeBluetoothConnection() {
		try {
			connected = false;
			in.close();
			out.close();
			nxtComm.close();
		} catch (IOException e) {
			System.err.println("Couldn't close Bluetooth connection: "
					+ e.toString());
		}
	}

	public boolean isMoving() {
		// TODO Auto-generated method stub
		return false;
	}
}

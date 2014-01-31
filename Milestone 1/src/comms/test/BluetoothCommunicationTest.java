package communication.test;

import java.io.IOException;
import java.util.Arrays;
import comms.BluetoothCommunication;

public class BluetoothCommunicationTest {

	public static final int test_no = 1000;

	public static final String NXT_MAC_ADDRESS = "00:16:53:09:70:C6";
	public static final String NXT_NAME = "team trinity";

	private static BluetoothCommunication comms;

	/**
	 * Testing the BluetoothCommunication class by sending number to Brick and
	 * wait for an echo
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Bluetooth Working: " + testBTComm());
	}

	public static boolean testBTComm() throws IOException {
		int correct = 0;
		long start_time = System.currentTimeMillis();
		long end_time = start_time;

		comms = new BluetoothCommunication(NXT_NAME, NXT_MAC_ADDRESS);
		try {
			comms.openBluetoothConnection();
		} catch (IOException e) {
			System.out.println("Error Open Connection");
			end_time = System.currentTimeMillis();
			System.out.println("Time: " + (end_time - start_time) + " ms");
			return false;
		}
		end_time = System.currentTimeMillis();
		System.out.println("Establish Connection Time: "
				+ (end_time - start_time) + " ms");

		if (comms.isRobotReady()) {
			start_time = System.currentTimeMillis();
			int [] tststr = new int [] {66,0,0,66};
			int [] ret_expect = new int [] {66,77,88,99};
			for (int i = 0; i < test_no; i++) {
				comms.sendToRobotSimple(tststr);
				System.out.println("Wait Data from Robot");
				int [] receive_info = new int [4];
				try {
					receive_info = comms.receiveFromRobot();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (!Arrays.equals(receive_info,ret_expect)) {
					System.out.println("ERROR! " + receive_info[0] + " " + receive_info[1] + " " + receive_info[2] + " " + receive_info[3]);
				} else {
					correct++;
				}

				end_time = System.currentTimeMillis();
				System.out.println("Time: " + (end_time - start_time) + " ms");
			}
		}
		System.out.println("Result : " + correct + "/" + test_no);
		comms.closeBluetoothConnection();
		return correct == test_no;
	}
}

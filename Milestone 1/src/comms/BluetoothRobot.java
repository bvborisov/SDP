package communication;

import java.io.IOException;

import world.state.Robot;
import world.state.RobotType;

/**
 * A class to control the robot via bluetooth
 */
public class BluetoothRobot extends Robot implements RobotController {
	/**
	 * A bi-directional stream connected to the robot by bluetooth
	 */
	private BluetoothCommunication comms;

	public BluetoothRobot(RobotType type, BluetoothCommunication comms) {
		super(type);
		this.comms = comms;
	}

	@Override
	public void connect() {
		try {
			comms.openBluetoothConnection();
		} catch (IOException e) {
			System.err.println();
		}
	}

	@Override
	public boolean isConnected() {
		return comms.isConnected();
	}

	@Override
	public boolean isReady() {
		return comms.isRobotReady();
	}

	@Override
	public void disconnect() {
		int[] command = { Commands.QUIT, 0, 0, 0 };
		try {
			comms.sendToRobotSimple(command);
			// Give the command time to send - prevents brick crash
			Thread.sleep(100);
		} catch (IOException e) {
			System.out.println("Could not send command");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted");
			e.printStackTrace();
		}
		comms.closeBluetoothConnection();
		System.out.println("Quit... Please reconnect.");
	}

	public void forcequit() {
		int[] command = { Commands.FORCEQUIT, 0, 0, 0 };
		try {
			comms.sendToRobotSimple(command);
			// Give the command time to send - prevents brick crash
			Thread.sleep(100);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted");
			e.printStackTrace();
		}
		comms.closeBluetoothConnection();
		System.out.println("Force quit... Reset the brick.");
	}

	@Override
	public int stop() {
		int[] command = { Commands.STOP, 0, 0, 0 };
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Stop...");
		return confirmation;
	}

	// @Override
	// public int beep() {
	// int[] command = { Commands.BEEP, 0, 0, 0 };
	// int confirmation = 0;
	// try {
	// confirmation = comms.sendToRobot(command);
	// } catch (IOException e1) {
	// System.out.println("Could not send command");
	// e1.printStackTrace();
	// }
	// System.out.println("Beep...");
	// return confirmation;
	// }

	@Override
	public int kick() {
		int[] command = { Commands.KICK, 0, 0, 0 };
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Kick");
		return confirmation;
	}

	@Override
	public int arc(int left, int right) {
		int[] command = { Commands.ARC, left, right, 0 };
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Arc");
		return confirmation;
	}

	@Override
	public int dribble(int direction) {
		int[] command = { Commands.DRIBBLERON, direction, 0, 0 };
		int confirmation = 0;
		String direct;
		if (direction == 1) {
			direct = "forwards";
		} else {
			direct = "backwards";
		}
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Dribbling in direction " + direct + "!");
		return confirmation;
	}

	@Override
	public int stopdribble() {
		int[] command = { Commands.DRIBBLEROFF, 0, 0, 0 };
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Stop the dribbler.");
		return confirmation;
	}

	@Override
	public int rotate(int input) {
		int dir = 2;
		int confirmation = 0;
		if (input < 0 && input > -180) {
			input = -input;
			dir = 1;
		} else if (input < -180 && input > -360) {
			input += 360;
		}

		int op1 = input % 127;
		int op2 = input / 127;
		int[] command = { Commands.ROTATE, dir, op2, op1 };// Angle is the sum
															// of option1 +
															// option2
		try {
			System.out.println("Str " + dir + " " + op2 + " " + op1);
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Rotate...");
		return confirmation;
	}

	@Override
	public int move(int speedX, int speedY) {
		int[] command = { Commands.ANGLEMOVE, speedX, speedY, 0 };
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
			System.out.println("Confirmation for move " + confirmation);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving");
		return confirmation;
	}

	@Override
	public int rotateMove(int speedX, int speedY, int rotSpeed) {
		int[] command = { Commands.ROTATEMOVE, speedX, speedY, rotSpeed };
		int confirmation = 0;
		try {
			confirmation = comms.sendToRobot(command);
		} catch (IOException e1) {
			System.out.println("Could not send command");
			e1.printStackTrace();
		}
		System.out.println("Moving at speed (" + speedX + ", " + speedY + ") while rotating at an angle: " + rotSpeed);
		return confirmation;
	}

	@Override
	public void clearBuff() {
		comms.clearBuff();
	}
	// TODO Add anglemove method

	/*
	 * public boolean forward(Double speed) { return rc.forward(speed); }
	 */

	/*
	 * public boolean arcForward(Double radius) { return rc.arcForward(radius);
	 * }
	 * 
	 * 
	 * public boolean arcForward(Double radius, Double speed) { return
	 * rc.arcForward(radius, speed); }
	 */
	/*
	 * public boolean isMoving() { return rc.isMoving(); }
	 */

	/*
	 * public boolean isStalled() { return rc.isStalled(); }
	 */

	// TODO - Implement when sensors have been added
	/*
	 * public boolean isTouchPressed() { return rc.isTouchPressed(); }
	 */

	/*
	 * public boolean rotate(Double angle, Double speed) { return
	 * rc.rotate(angle, speed); }
	 */

	/*
	 * public void setDefaultRotateSpeed(Double speed) {
	 * rc.setDefaultRotateSpeed(speed); }
	 */

	/*
	 * public boolean rotate(Double angle) { return rc.rotate(angle); }
	 */
}

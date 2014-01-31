package communication;

/**
 * An interface to provide a common basis for both the real robot, and the
 * simulator's version of the robot
 * 
 * @author Alex Adams (s1046358)
 */
public interface RobotController {
	/**
	 * Attempts to connect to the robot
	 * 
	 * @throws Exception
	 *             if connection failed
	 */
	public void connect() throws Exception;

	/**
	 * Tests if the robot is connected
	 */
	public boolean isConnected();

	/**
	 * Tests if the robot is ready to receive commands
	 */
	public boolean isReady();

	/**
	 * Disconnects from the robot
	 */
	public void disconnect();

	/**
	 * Tells the robot to stop
	 * 
	 * @return A confirmation code for the command
	 */
	public int stop();

	// /**
	// * Tells the robot to beep 3 times
	// *
	// * @return A confirmation code for the command
	// */
	// public int beep();

	/**
	 * Tells the robot to kick
	 * 
	 * @return A confirmation code for the command
	 */
	public int kick();

	/**
	 * Tells the robot to activate it's dribbler
	 * 
	 * @param direction
	 *            Tells which direction the dribbler should move. Use 1 for
	 *            forward (as in dribbling) and 2 for backward (as in kicking).
	 * @return A confirmation code for the command
	 */
	public int dribble(int direction);

	/**
	 * Stops the dribbler so that we don't drain the battery.
	 * 
	 * @return A confirmation code for the command
	 */
	public int stopdribble();

	/**
	 * Tells the robot to move along a vector, relative to the robot
	 * 
	 * @param speedX
	 *            A speed setting for the left/right wheels, from -100 to 100
	 * @param speedY
	 *            A speed setting for the forward/backward wheels, from -100 to
	 *            100
	 * @return A confirmation code for the command
	 */
	public int move(int speedX, int speedY);

	/**
	 * Tells the robot to rotate by an angle
	 * 
	 * @param angleDeg
	 *            The angle to rotate by, in degrees
	 * @return A confirmation code for the command
	 */
	public int rotate(int angleDeg);

	/**
	 * @param left
	 *            left wheel speed out of 100
	 * @param right
	 *            right wheel speed of 100
	 * @return
	 */
	public int arc(int left, int right);

	/**
	 * Tells the robot to rotate while moving along a vector, relative to the
	 * robot<br/>
	 * NOTE: this vector will change as the robot rotates!
	 * 
	 * @param speedX
	 *            A speed setting for the left/right wheels, from -100 to 100
	 * @param speedY
	 *            A speed setting for the forward/backward wheels, from -100 to
	 *            100
	 * @param rotSpeed
	 *            The speed to rotate at, in degrees per second
	 * @return A confirmation code for the command
	 */
	public int rotateMove(int speedX, int speedY, int rotSpeed);

	/**
	 * Clears the command / confirmation buffers for the robot
	 */
	public void clearBuff();

	public void forcequit();
}

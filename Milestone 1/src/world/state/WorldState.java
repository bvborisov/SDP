package world.state;

import geometry.Vector;
import strategy.calculations.AngleCalculator;
import strategy.calculations.DistanceCalculator;
import strategy.calculations.GoalInfo;
import world.state.WorldState;

import vision.Position;

/**
 * A class storing the world state from vision, used by strategy
 * 
 * TODO: clean up duplicated functionality from integrating vision and strategy
 * (will be easier via eclipse refactoring tools)
 */
public class WorldState {
	/** The number of frames used to calculate velocity */
	private static final int NUM_FRAMES = 10;
	private AngleCalculator a = new AngleCalculator(this);
	private long counter;
	private int direction; // 0 = right, 1 = left.
	private int colour; // 0 = yellow, 1 = blue
	private int pitch; // 0 = main, 1 = side room

	private int greenX;
	private int greenY;

	private int currentFrame = 0;
	// Buffers for smoothing positions/angles and calculating velocities
	private int[] blueXBuf = new int[NUM_FRAMES];
	private int[] blueYBuf = new int[NUM_FRAMES];
	private double[] blueOrientBuf = new double[NUM_FRAMES];

	private int[] yellowXBuf = new int[NUM_FRAMES];
	private int[] yellowYBuf = new int[NUM_FRAMES];
	private double[] yellowOrientBuf = new double[NUM_FRAMES];

	private int[] ballXBuf = new int[NUM_FRAMES];
	private int[] ballYBuf = new int[NUM_FRAMES];

	// Smoothed positions/angles
	private int blueX;
	private int blueY;
	private double blueOrient;

	private int yellowX;
	private int yellowY;
	private double yellowOrient;

	private int ballX;
	private int ballY;

	// Velocities
	private double blueVelX;
	private double blueVelY;

	private double yellowVelX;
	private double yellowVelY;

	private double ballVelX;
	private double ballVelY;

	private boolean weAreBlue = true;
	private boolean weAreOnLeft = true;
	private boolean mainPitch = true;
	private boolean blueHasBall = false;
	private boolean yellowHasBall = false;

	private PossessionManager pm = new PossessionManager();

	public final GoalInfo goalInfo;

	public int frame;
	public Robot theirRobot = new Robot(RobotType.Them);
	public Robot ourRobot = new Robot(RobotType.Us);
	public Ball ball = new Ball();
	public Ball prevBall = new Ball();
	public PossessionType hasPossession = PossessionType.Nobody;

	private static final double sqVelocityThreshold = 1.0;
	private static final double distVelFactorScale = 5.0;

	// Coordinates of the target placement of the robot.
	public static int targetX = 100;
	public static int targetY = 100;

	public WorldState(GoalInfo goalInfo) {
		// control properties
		this.direction = 0;
		this.colour = 0;
		this.pitch = 0;
		this.goalInfo = goalInfo;
		this.a = new AngleCalculator(this);
	}

	public void setBlueX(int blueX) {
		this.blueX = blueX;
		this.blueXBuf[currentFrame] = blueX;
	}

	public int getBlueX() {
		return blueX;
	}

	public double getBlueXVelocity() {
		return blueVelX;
	}

	public void setBlueY(int blueY) {
		this.blueY = blueY;
		this.blueYBuf[currentFrame] = blueY;
	}

	public int getBlueY() {
		return blueY;
	}

	public double getBlueYVelocity() {
		return blueVelY;
	}

	public void setBlueOrientation(double blueOrientation) {
		this.blueOrient = blueOrientation;
		this.blueOrientBuf[currentFrame] = blueOrientation;
	}

	public double getBlueOrientation() {
		return blueOrient;
	}

	public void setYellowX(int yellowX) {
		this.yellowX = yellowX;
		this.yellowXBuf[currentFrame] = yellowX;
	}

	public int getYellowX() {
		return yellowX;
	}

	public double getYellowXVelocity() {
		return yellowVelX;
	}

	public void setYellowY(int yellowY) {
		this.yellowY = yellowY;
		this.yellowYBuf[currentFrame] = yellowY;
	}

	public int getYellowY() {
		return yellowY;
	}

	public double getYellowYVelocity() {
		return yellowVelY;
	}

	public void setYellowOrientation(double yellowOrientation) {
		this.yellowOrient = yellowOrientation;
		this.yellowOrientBuf[currentFrame] = yellowOrientation;
	}

	public double getYellowOrientation() {
		return yellowOrient;
	}

	public void setGreenX(int greenX) {
		this.greenX = greenX;
	}

	public int getGreenX() {
		return greenX;
	}

	public void setGreenY(int greenY) {
		this.greenY = greenY;
	}

	public int getGreenY() {
		return greenY;
	}

	public void setBallX(int ballX) {
		this.ballX = ballX;
		this.ballXBuf[currentFrame] = ballX;
	}

	public int getBallX() {
		return ballX;
	}

	public double getBallXVelocity() {
		return ballVelX;
	}

	public void setBallY(int ballY) {
		this.ballY = ballY;
		this.ballYBuf[currentFrame] = ballY;
	}

	public int getBallY() {
		return ballY;
	}

	public double getBallYVelocity() {
		return ballVelY;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public int getColour() {
		return colour;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public int getPitch() {
		return pitch;
	}

	public long getCounter() {
		return this.counter;
	}

	/**
	 * Triggers calculation of smoothed angles and velocities
	 */
	public void update() {
		++counter;

		// Reinitialise all positions/angles/velocities to 0
		blueVelX = 0;
		blueVelY = 0;
		blueOrient = 0;

		yellowVelX = 0;
		yellowVelY = 0;
		yellowOrient = 0;

		ballVelX = 0;
		ballVelY = 0;

		// Smooth angles
		for (int i = 0; i < NUM_FRAMES; ++i) {
			blueOrient += blueOrientBuf[i];

			yellowOrient += yellowOrientBuf[i];
		}
		blueOrient /= NUM_FRAMES;
		yellowOrient /= NUM_FRAMES;

		// Calculate velocities
		int prevFrame;
		// Calculate +ve modulus - % operator doesn't have the desired effect.
		prevFrame = 2 + currentFrame - NUM_FRAMES;
		if (prevFrame < 0)
			prevFrame += NUM_FRAMES;

		blueVelX += blueXBuf[currentFrame] - blueXBuf[prevFrame];
		blueVelY += blueYBuf[currentFrame] - blueYBuf[prevFrame];

		yellowVelX += yellowXBuf[currentFrame] - yellowXBuf[prevFrame];
		yellowVelY += yellowYBuf[currentFrame] - yellowYBuf[prevFrame];

		ballVelX += ballXBuf[currentFrame] - ballXBuf[prevFrame];
		ballVelY += ballYBuf[currentFrame] - ballYBuf[prevFrame];

		blueVelX /= NUM_FRAMES;
		blueVelY /= NUM_FRAMES;

		yellowVelX /= NUM_FRAMES;
		yellowVelY /= NUM_FRAMES;

		ballVelX /= NUM_FRAMES;
		ballVelY /= NUM_FRAMES;

		++currentFrame;
		if (currentFrame >= NUM_FRAMES)
			currentFrame = 0;
	}

	// ///////////////////////
	public Robot getOurRobot() {
		return this.ourRobot;
	}

	public Robot getTheirRobot() {
		return this.theirRobot;
	}

	public Ball getBall() {
		return this.ball;
	}

	// //////////////////////////
	public void setOurRobot() {
		if (areWeBlue()) {
			this.ourRobot.x = blueX;
			this.ourRobot.y = blueY;
			this.ourRobot.setPosition(new Vector(ourRobot.x, ourRobot.y));
			this.ourRobot.bearing = blueOrient;
		} else {
			this.ourRobot.x = yellowX;
			this.ourRobot.y = yellowY;
			this.ourRobot.setPosition(new Vector(ourRobot.x, ourRobot.y));
			this.ourRobot.bearing = yellowOrient;
		}
	}

	public void setTheirRobot() {
		if (areWeBlue()) {
			// If we are blue, the other robot is yellow.
			// Please correct me if I am wrong, the code is obscure...
			this.theirRobot.x = yellowX;
			this.theirRobot.y = yellowY;
			this.theirRobot.setPosition(new Vector(theirRobot.x, theirRobot.y));
			this.theirRobot.bearing = yellowOrient;
		} else {
			this.theirRobot.x = blueX;
			this.theirRobot.y = blueY;
			this.theirRobot.setPosition(new Vector(theirRobot.x, theirRobot.y));
			this.theirRobot.bearing = blueOrient;
		}
	}

	public void setBall() {
		this.ball.x = ballX;
		this.ball.y = ballY;
		this.ball.speedX = ballVelX;
		this.ball.speedY = ballVelY;
	}

	public void setWeAreOnLeft(boolean weAreOnLeft) {
		this.weAreOnLeft = weAreOnLeft;
	}

	public boolean areWeOnLeft() {
		return weAreOnLeft;
	}

	public boolean areWeBlue() {
		return weAreBlue;
	}

	public void setWeAreBlue(boolean areWeBlue) {
		weAreBlue = areWeBlue;
	}

	public boolean isMainPitch() {
		return mainPitch;
	}

	public void setMainPitch(boolean onMainPitch) {
		mainPitch = onMainPitch;
	}

	public Position getOurGoal() {
		if (weAreOnLeft) {
			return goalInfo.getLeftGoalCenter();
		} else {
			return goalInfo.getRightGoalCenter();
		}
	}

	public Position getTheirGoal() {
		if (weAreOnLeft) {
			return goalInfo.getRightGoalCenter();
		} else {
			return goalInfo.getLeftGoalCenter();
		}
	}

	public int getMidLine() {
		return (goalInfo.pitchConst.getLeftBuffer() + (640 - goalInfo.pitchConst.getRightBuffer())) / 2;
	}

	public double distanceBetweenUsAndBall() {
		return DistanceCalculator.Distance(this.ourRobot.x, this.ourRobot.y, this.ball.x, this.ball.y);
	}

	public double distanceBetweenBallAndOurGoalX() {
		return DistanceCalculator.Distance(this.getOurGoal().getX(), 0, this.ball.x, 0);
	}

	public double distanceBetweenBallAndTheirGoalX() {
		return DistanceCalculator.Distance(this.getTheirGoal().getX(), 0, this.ball.x, 0);
	}

	public boolean areWeInOurHalf() {
		int midLine = getMidLine();

		if (weAreOnLeft) {
			return this.ourRobot.x < midLine;
		} else {
			return this.ourRobot.x > midLine;
		}
	}

	public boolean ballIsInGoal() {
		if (this.ball.x < goalInfo.getLeftGoalCenter().getX() || this.ball.x > goalInfo.getRightGoalCenter().getX())
			return true;
		else
			return false;
	}

	public int whoHasTheBall() {
		if (blueHasBall) {
			return 1;
		} else if (yellowHasBall) {
			return 2;
		} else
			return -1;
	}

	public void updatePossesion() {
		hasPossession = pm.setPossession(this);
		if (weAreBlue) {
			if (hasPossession == PossessionType.Us) {
				blueHasBall = true;
				yellowHasBall = false;
			} else if (hasPossession == PossessionType.Them) {
				blueHasBall = false;
				yellowHasBall = true;
			} else {
				blueHasBall = false;
				yellowHasBall = false;
			}
		} else {
			if (hasPossession == PossessionType.Us) {
				blueHasBall = false;
				yellowHasBall = true;
			} else if (hasPossession == PossessionType.Them) {
				blueHasBall = true;
				yellowHasBall = false;
			} else {
				blueHasBall = false;
				yellowHasBall = false;
			}
		}
	}

	public PossessionType getPosession() {
		return hasPossession;
	}

	public boolean weFaceTheirGoal() {

		double gpt;
		double gpb;
		double x;
		if (weAreOnLeft) {
			gpt = goalInfo.getRightGoalTop().getY();
			gpb = goalInfo.getRightGoalBottom().getY();
			x = goalInfo.getRightGoalBottom().getX();
		} else {
			gpt = goalInfo.getLeftGoalTop().getY();
			gpb = goalInfo.getLeftGoalBottom().getY();
			x = goalInfo.getLeftGoalBottom().getX();
		}
		if ((a.AngleTurner(x, gpt) > 0 && a.AngleTurner(x, gpt) < 20) || (a.AngleTurner(x, gpb) < 0 && a.AngleTurner(x, gpb) > (-20))) {
			return true;
		} else
			return false;
	}

	public boolean enemyIsClose() {
		if (distanceToRobot() < 200)
			return true;
		else
			return false;
	}

	public boolean enemyInFront() {
		double ang = a.angleToEnemy();
		if ((ang > (-15)) && (ang < 15))
			return true;
		else
			return false;
	}

	public Position getTheirGoalTop() {
		if (weAreOnLeft)
			return goalInfo.getRightGoalTop();
		else
			return goalInfo.getLeftGoalTop();
	}

	public Position getTheirGoalBot() {
		if (weAreOnLeft)
			return goalInfo.getRightGoalBottom();
		else
			return goalInfo.getLeftGoalBottom();
	}

	public Position getOurGoalTop() {
		if (weAreOnLeft)
			return goalInfo.getLeftGoalTop();
		else
			return goalInfo.getRightGoalTop();
	}

	public Position getOurGoalBot() {
		if (weAreOnLeft)
			return goalInfo.getLeftGoalBottom();
		else
			return goalInfo.getRightGoalBottom();
	}

	public double distanceToBall() {

		return DistanceCalculator.Distance(ourRobot.x, ourRobot.y, this.ball.x, this.ball.y);

	}

	public double distanceToRobot() {

		return DistanceCalculator.Distance(ourRobot.x, ourRobot.y, theirRobot.x, theirRobot.y);

	}

	public double distanceUsToTheirgoal() {
		return DistanceCalculator.Distance(ourRobot.x, ourRobot.y, this.getTheirGoal().getX(), this.getTheirGoal().getY());
	}

	public double distanceThemToTheirgoal() {
		return DistanceCalculator.Distance(theirRobot.x, theirRobot.y, this.getTheirGoal().getX(), this.getTheirGoal().getY());
	}

	public double distanceUsToOurgoal() {
		return DistanceCalculator.Distance(ourRobot.x, ourRobot.y, this.getOurGoal().getX(), this.getOurGoal().getY());
	}

	public double distanceThemToOurgoal() {
		return DistanceCalculator.Distance(theirRobot.x, theirRobot.y, this.getOurGoal().getX(), this.getOurGoal().getY());
	}

	public double angleToEnemy() {
		double pointBearing = a.findPointBearing(ourRobot, this.getTheirRobot().x, this.getTheirRobot().y);
		double angle = a.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}

	public double angleToBall() {
		double pointBearing = a.findPointBearing(ourRobot, this.ball.x, this.ball.y);
		double angle = a.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}

	public double angleToTheirGoal() {
		double pointBearing = a.findPointBearing(ourRobot, this.getTheirGoal().getX(), this.getTheirGoal().getY());
		double angle = a.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}
	public double angleToTheirGoalX() {
		double pointBearing = a.findPointBearing(ourRobot, this.getTheirGoal().getX(), ourRobot.y);
		double angle = a.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}

	public double angleToOurGoal() {
		double pointBearing = a.findPointBearing(ourRobot, this.getOurGoal().getX(), this.getOurGoal().getY());
		double angle = a.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}

	public double angleToTheirGoalTop() {
		double pointBearing = a.findPointBearing(ourRobot, this.getTheirGoalTop().getX(), this.getTheirGoalTop().getY());
		double angle = a.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}

	public double angleToTheirGoalBot() {
		double pointBearing = a.findPointBearing(ourRobot, this.getTheirGoalBot().getX(), this.getTheirGoalBot().getY());
		double angle = a.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}

	public double angleToOurGoalTop() {
		double pointBearing = a.findPointBearing(ourRobot, this.getOurGoalTop().getX(), this.getOurGoalTop().getY());
		double angle = a.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}

	public double angleToOurGoalBot() {
		double pointBearing = a.findPointBearing(ourRobot, this.getOurGoalTop().getX(), this.getOurGoalBot().getY());
		double angle = a.turnAngle(ourRobot.bearing, pointBearing);
		return angle;
	}

	public Position projectedBallPos() {
		// Ignore noise
		if (Math.abs(ball.speedX) < 0.3)
			ball.speedX = 0;
		if (Math.abs(ball.speedY) < 0.3)
			ball.speedY = 0;

		/*
		 * Don't bother projecting where the ball's going to be if it's barely
		 * moving
		 */
		if (ball.speedX * ball.speedX + ball.speedY * ball.speedY > sqVelocityThreshold) {
			// The ball's velocity matters more the further it is from our robot
			double velocityFactor = distanceBetweenUsAndBall() / distVelFactorScale;

			int minX = this.goalInfo.getLeftGoalCenter().getX();
			int maxX = this.goalInfo.getRightGoalCenter().getX();
			int minY = this.goalInfo.getTopLeftCorner().getY();
			int maxY = this.goalInfo.getBotLeftCorner().getY();
			;

			double projX = ball.x + velocityFactor * ball.speedX;
			if (projX < minX)
				projX = minX + 30;
			else if (projX > maxX)
				projX = maxX - 30;

			double projY = ball.y + velocityFactor * ball.speedY;
			if (projY < minY)
				projY = minY + 30;
			else if (projY > maxY)
				projY = maxY - 30;

			return new Position((int) projX, (int) projY);
		} else
			return new Position((int) ball.x, (int) ball.y);
	}

}

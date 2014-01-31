package world.state;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;

import geometry.Vector;
import strategy.calculations.DistanceCalculator;
import strategy.calculations.GoalInfo;
import vision.*;


public class HittingObstacle {
//The purpose of this class is to check whether we are hitting a wall or not

	/**
	 * We define 6 walls
	 * 
	 * south wall
	 * 
	 * South West wall next  to the goal
	 * 
	 * South East wall next  to the goal
	 * 	
	 * 
	 * North Wall
	 * 
	 * North East Wall next  to the goal 
	 * 
	 * North West next  to the goal
	 * 
	 * 
	 */
	PitchConstants pc = new PitchConstants(0);
	
	GoalInfo goalCord = new GoalInfo(pc);
	
	Vector northWestPole = new Vector(goalCord.getLeftGoalTop().getX(),  goalCord.getLeftGoalTop().getY());
	Vector southWestPole = new Vector (goalCord.getLeftGoalBottom().getX(), goalCord.getLeftGoalBottom().getY()) ;
	
	
	Vector northWestCorner =  new Vector(35, 94);
	Vector southWestCorner =  new Vector(35, 392);
	
	
	
	
	Vector northEastPole = new Vector( goalCord.getRightGoalTop().getX(),  goalCord.getRightGoalTop().getY() );
	Vector southEastPole = new Vector (goalCord.getRightGoalBottom().getX(), goalCord.getRightGoalBottom().getY()) ;
	
	
	Vector northEastCorner =  new Vector(598, 92);
	Vector southEastCorner =  new Vector(608, 395);
	
	double pitchWidth = (northEastCorner.getX()- northWestCorner.getX());
	double pitchHeight = (southEastCorner.getY()- northEastCorner.getY());
	
	
public boolean nearSouth(WorldState world){
				
	double YaxisRobot = world.getOurRobot().y;
	
	double angle = Math.toDegrees(world.getOurRobot().bearing);
	double correction = Math.abs(Math.sin(angle)*5);
	
	if( world.getOurRobot().x > 500){
		
		
		YaxisRobot -= 4;
		
		
	}else if ( world.getOurRobot().x > 230){
		YaxisRobot -= 2;
	}
	
	
	if (YaxisRobot>354)
		
		
	
		return true;
	
	else return false;
		
		
		
	}
	
	
public boolean nearNorth(WorldState world){
	
		double YaxisRobot = world.getOurRobot().y;
		
		double angle = Math.toDegrees(world.getOurRobot().bearing);
		double correction = Math.abs(Math.sin(angle)*5);
		
		if((world.getOurRobot().x< 222 &&  world.getOurRobot().x> 35) || (world.getOurRobot().x<596 && world.getOurRobot().x>109 ) ){
			
			
			YaxisRobot -=  10;
			
			
		}
		
		
		if (YaxisRobot<124+correction )
			
			
		
			return true;
		
		else return false;
		
		
		
	}
	
public boolean nearNorthWest(WorldState world){
	Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
		double x = world.getOurRobot().x;
		double y = world.getOurRobot().y;
		double nwp = northWestPole.getY();
		double nwc = northWestCorner.getY(); 
		if (x < 73 && y < nwp && y > nwc){
			
			return true;
		}
			
		
			
		
		else return false;
		
		
		
	}
	

public boolean nearSouthWest(WorldState world){
	Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
	double x = world.getOurRobot().x;
	double y = world.getOurRobot().y;
	double nsp = southWestPole.getY();
	double nsc = southWestCorner.getY(); 
	if (x < 73 && y > nsp && y < nsc){
		
		return true;
	}
		
	
		
	
	else return false;
	
	
	
}



public boolean nearNorthEast(WorldState world){
	Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
	double x = world.getOurRobot().x;
	double y = world.getOurRobot().y;
	double nep = northEastPole.getY();
	double nec = northEastCorner.getY(); 
	if (x > 564 && y < nep && y > nec){
		
		return true;
	}
		
	
		
	
	else return false;
	
	
	
}


public boolean nearSouthEast(WorldState world){
	Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
	double x = world.getOurRobot().x;
	double y = world.getOurRobot().y;
	double sep = southEastPole.getY();
	double sec = southEastCorner.getY(); 
	if (x > 568 && y > sep && y < sec){
		
		return true;
	}
		
	
		
	
	else return false;
	
	
	
}






	
public boolean notHittingWall(WorldState world){
	
	if((nearSouth(world) ||  nearNorth(world) || nearNorthWest(world) || nearSouthWest(world) || 
			nearNorthEast(world) || nearSouthEast(world)) == false )
		return true;
	
	
	else return false;
	
	
	
	
}
public boolean inCorner (WorldState world) {
	if ((nearSouth(world) && (nearSouthEast(world) ||nearSouthWest(world)  )) ||
			(nearNorth(world) && (nearNorthEast(world) ||nearNorthWest(world)))) return true;
	else return false;
}
	
	

	
	
	/*
	 * Then we also have the  opponent 
	 * robot
	 * 
	 * 
	 * so from the center of the robot 
	 * we extend it to incorporate 4 boundaries
	 * 
	 */
	
	
	public boolean nearObstacleEnnemy(WorldState world){
		
		
		Vector positionOurRobot =  new Vector(world.getOurRobot().x, world.getOurRobot().y);
		
		Vector northEast =  new Vector(world.getTheirRobot().x+39, world.getTheirRobot().y-44);
		
		Vector northWest =  new Vector(world.getTheirRobot().x-37, world.getTheirRobot().y-44);
		
		Vector southWest =  new Vector(world.getTheirRobot().x-36, world.getTheirRobot().y+32);
		
		Vector southEast =  new Vector(world.getTheirRobot().x+37, world.getTheirRobot().y+30);
		double d = DistanceCalculator.Distance(world.getOurRobot().x, world.getOurRobot().y,
				world.getTheirRobot().x, world.getTheirRobot().y);
		System.out.println("Distance: " + d);
		
		if( d < 75 )
		{
			
			return true;
			
			
		}
		
		else return false;
		
		
	
		
		
		
		
		
	}

	 public double pointToLineDistance(Vector A, Vector  B, Vector P) {
		    double normalLength = Math.sqrt((B.getX()-A.getX())*(B.getY()-A.getX())+(B.getY()-A.getY())*(B.getY()-A.getY()));
		    return Math.abs((P.getX()-A.getX())*(B.getY()-A.getY())-(P.getY()-A.getY())*(B.getX()-A.getX()))/normalLength;
		  }

	
	
}

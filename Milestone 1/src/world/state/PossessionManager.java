package world.state;

import strategy.calculations.Possession;

public class PossessionManager {
	
	public PossessionType setPossession(WorldState world) {
		PossessionType hasPossession;
		// TODO Auto-generated method stub
		// Code for when:
		// Both teams have possession
		// TODO: Factor this into World state - it is an abstraction above the state of the world
		if (Possession.hasPossession(world, RobotType.Us) && Possession.hasPossession(world, RobotType.Them)) {
			hasPossession = PossessionType.Both;
		} else if (Possession.hasPossession(world, RobotType.Them)) {
			hasPossession = PossessionType.Them;
		} else if (Possession.hasPossession(world,RobotType.Us)) {
			hasPossession = PossessionType.Us;
		} else {
			hasPossession = PossessionType.Nobody;
		}
		return hasPossession;
	}
	
}

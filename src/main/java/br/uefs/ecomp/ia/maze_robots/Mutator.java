package br.uefs.ecomp.ia.maze_robots;

import java.util.Random;

public class Mutator {

	private Random random;
	private int stateMin;
	private int addState;
	private int stateMax;
	private int delState;
	private int changeState;
	private int changeOutput;

	public Mutator setStateMin(int stateMin) {
		this.stateMin = stateMin;
		return this;
	}

	public Mutator setStateMax(int stateMax) {
		this.stateMax = stateMax;
		return this;
	}

	public Mutator setRandom(Random random) {
		this.random = random;
		return this;
	}

	public Mutator setAddState(int addState) {
		this.addState = addState;
		return this;
	}

	public Mutator setDelState(int delState) {
		this.delState = delState;
		return this;
	}

	public Mutator setChangeState(int changeState) {
		this.changeState = changeState;
		return this;
	}

	public Mutator setChangeOutput(int changeOutput) {
		this.changeOutput = changeOutput;
		return this;
	}

	public void mutate(Robot robot) {
		robot.forEach((s, i) -> {
			if (changeOutput >= random.nextInt(100))
				robot.setOutput(s, i, random.nextInt(Robot.OUTPUT_SIZE));
			if (changeState >= random.nextInt(100))
				robot.setState(s, i, random.nextInt(robot.getStateSize()));
		});

		if (delState >= random.nextInt(100) && stateMin < robot.getStateSize())
			robot.delState();
		if (addState >= random.nextInt(100) && robot.getStateSize() < stateMax) {
			robot.addState();
			robot.forEach(robot.getStateSize() - 1, 0, (s, i) -> {
				robot.getValue()[s][i] = new Integer[] { random.nextInt(Robot.OUTPUT_SIZE), random.nextInt(robot.getStateSize()) };
			});
		}
	}
}

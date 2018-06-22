package br.uefs.ecomp.ia.maze_robots;

import java.util.Random;

public class Mutator {

	private Random random;
	private int stateMin;
	private int stateMax;
	private double addState;
	private double delState;
	private double changeState;
	private double changeOutput;

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

	public Mutator setAddState(double mAddStateStart) {
		this.addState = mAddStateStart;
		return this;
	}

	public Mutator setDelState(double delState) {
		this.delState = delState;
		return this;
	}

	public Mutator setChangeState(double changeState) {
		this.changeState = changeState;
		return this;
	}

	public Mutator setChangeOutput(double changeOutput) {
		this.changeOutput = changeOutput;
		return this;
	}

	public boolean testChance(double value) {
		return value >= (random.nextInt(100) + 1);
	}

	public void mutate(Robot robot) {
		robot.forEach((s, i) -> {
			if (testChance(changeOutput))
				robot.setOutput(s, i, random.nextInt(Robot.OUTPUT_SIZE));
			if (testChance(changeState))
				robot.setState(s, i, random.nextInt(robot.getStateSize()));
		});

		if (testChance(delState) && stateMin < robot.getStateSize())
			robot.delState();
		if (testChance(addState) && robot.getStateSize() < stateMax)
			robot.addState();

		robot.forEach((s, i) -> {
			if (robot.getOutput(s, i) == -1)
				robot.setOutput(s, i, random.nextInt(Robot.OUTPUT_SIZE));
			if (robot.getState(s, i) == -1)
				robot.setState(s, i, random.nextInt(robot.getStateSize()));
		});
	}
}

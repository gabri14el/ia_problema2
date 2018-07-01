package br.uefs.ecomp.ia.maze_robots;

import java.util.List;
import java.util.Random;

public class Mutator {

	private Random random;
	private int stateMin;
	private int stateMax;
	private double addState;
	private double delState;
	private double changeState;
	private double changeOutput;
	private List<Robot> robots;

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

	public Mutator setRobots(List<Robot> robots) {
		this.robots = robots;
		return this;
	}

	private boolean testChance(double value) {
		return value >= (random.nextInt(100) + 1);
	}

	public void mutate() {
		robots.forEach((r) -> {
			r.forEach((s, i) -> {
				if (testChance(changeOutput))
					r.setOutput(s, i, random.nextInt(Robot.OUTPUT_SIZE));
				if (testChance(changeState))
					r.setState(s, i, random.nextInt(r.getStateSize()));
			});

			boolean del = testChance(delState) && stateMin < r.getStateSize();
			boolean add = testChance(addState) && r.getStateSize() < stateMax;
			if (del && add)
				r.changeLastState(random);
			else if (del)
				r.delState(random);
			else if (add)
				r.addState(random);
		});
	}
}

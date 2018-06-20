package br.uefs.ecomp.ia.maze_robots;

public class Mutator {

	private long seed;
	private int addState;
	private int delState;
	private int changeState;
	private int changeOutput;
	private Robot robot;

	public Mutator setSeed(long seed) {
		this.seed = seed;
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

	public Mutator setRobot(Robot robot) {
		this.robot = robot;
		return this;
	}

	public void mutate() {
	}
}

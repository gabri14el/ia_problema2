package br.uefs.ecomp.ia.maze_robots;

public class FitnessCalculator {

	private int state;
	private int maxSteps;
	private Maze maze;
	private Robot robot;

	public FitnessCalculator setMaxSteps(int maxSteps) {
		this.maxSteps = maxSteps;
		return this;
	}

	public FitnessCalculator setMaze(Maze maze) {
		this.maze = maze;
		return this;
	}

	public FitnessCalculator setRobot(Robot robot) {
		this.robot = robot;
		return this;
	}

	public FitnessCalculator run() {
		return this;
	}

	public Double getFitness() {
		return 0.0;
	}

	public boolean itsOver() {
		return false;
	}
}

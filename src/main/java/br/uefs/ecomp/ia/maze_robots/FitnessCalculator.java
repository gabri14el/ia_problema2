package br.uefs.ecomp.ia.maze_robots;

public class FitnessCalculator {

	private int state;
	private int maxSteps;

	public FitnessCalculator setMaxSteps(int maxSteps) {
		this.maxSteps = maxSteps;
		return this;
	}

	public FitnessCalculator setMaze(Maze m) {
		return null;
	}

	public FitnessCalculator setRobot(Robot r) {
		return null;
	}

	public FitnessCalculator run() {
		return null;
	}

	public Double getFitness() {
		return null;
	}

	public boolean itsOver() {
		return false;
	}
}

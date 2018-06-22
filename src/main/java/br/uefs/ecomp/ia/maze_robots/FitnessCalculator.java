package br.uefs.ecomp.ia.maze_robots;

import java.util.LinkedList;
import java.util.List;

public class FitnessCalculator {

	private double scoreWallColision;
	private double scoreStep;
	private double scoreCleanStep;
	private double scoreRevisit;
	private double scoreEnd;

	private boolean stopInEnd;
	private int maxSteps;
	private Maze maze;

	private List<Step> steps;

	public FitnessCalculator() {
		steps = new LinkedList<>();
	}

	public List<Step> getSteps() {
		return steps;
	}

	public FitnessCalculator setMaxSteps(int maxSteps) {
		this.maxSteps = maxSteps;
		return this;
	}

	public FitnessCalculator setStopInEnd(boolean stopInEnd) {
		this.stopInEnd = stopInEnd;
		return this;
	}

	public FitnessCalculator setMaze(Maze maze) {
		this.maze = maze;
		return this;
	}

	public FitnessCalculator setScoreWallColision(double scoreWallColision) {
		this.scoreWallColision = scoreWallColision;
		return this;
	}

	public FitnessCalculator setScoreStep(double scoreStep) {
		this.scoreStep = scoreStep;
		return this;
	}

	public FitnessCalculator setScoreCleanStep(double scoreCleanStep) {
		this.scoreCleanStep = scoreCleanStep;
		return this;
	}

	public FitnessCalculator setScoreRevisit(double scoreRevisit) {
		this.scoreRevisit = scoreRevisit;
		return this;
	}

	public FitnessCalculator setScoreEnd(double scoreEnd) {
		this.scoreEnd = scoreEnd;
		return this;
	}

	public boolean calculate(Robot robot) {
		steps.clear();
		Step step = new Step();
		Step nextStep;
		step.ry = maze.getSY();
		step.rx = maze.getSX();
		step.fitness = 0.0;
		step.state = 0;
		boolean end = false;

		int[][] visited = new int[maze.getYLength()][maze.getXLength()];
		for (int x = 0; x < maxSteps && !(stopInEnd && end); x++) {
			step.input = robot.getInput(maze, step.ry, step.rx);
			step.output = robot.getOutput(step.state, step.input);
			steps.add(step);

			nextStep = new Step();
			nextStep.state = robot.getState(step.state, step.input);
			nextStep.ry = step.ry + robot.sumY(step.output);
			nextStep.rx = step.rx + robot.sumX(step.output);

			nextStep.fitness = step.fitness + scoreStep;

			// Houve colisão com paredes ou tentou sair do vetor
			if (maze.isWall(nextStep.ry, nextStep.rx) || nextStep.ry == 0 || nextStep.ry > maze.getYLength() || nextStep.rx == 0 || nextStep.rx > maze.getXLength()) {
				// Volta para a posição anterior
				nextStep.ry -= robot.sumY(step.output);
				nextStep.rx -= robot.sumX(step.output);

				nextStep.fitness += scoreWallColision;
			}
			if ((step.ry != nextStep.ry || step.rx != nextStep.rx) && maze.isEmpty(nextStep.ry, nextStep.rx))
				nextStep.fitness = step.fitness + scoreCleanStep;
			if (maze.isEnd(nextStep.ry, nextStep.rx)) {
				nextStep.fitness += scoreEnd;
				end = true;
			}
			if (visited[nextStep.ry][nextStep.rx] == 0)
				visited[nextStep.ry][nextStep.rx] = 1;
			else
				nextStep.fitness += scoreRevisit;

			step = nextStep;
		}

		robot.setFitness(steps.get(steps.size() - 1).fitness);

		return end;
	}

	public boolean itsOver() {
		return false;
	}

	public class Step {

		public int output;
		public int input;
		public int state;
		public double fitness;
		public int rx;
		public int ry;
	}
}

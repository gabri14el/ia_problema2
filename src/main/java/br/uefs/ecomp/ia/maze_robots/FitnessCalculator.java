package br.uefs.ecomp.ia.maze_robots;

import java.util.LinkedList;
import java.util.List;

public class FitnessCalculator {

	private double scoreWallColision;
	private double scoreStep;
	private double scoreCleanStep;
	private double scoreRevisit;
	private double scoreEnd;

	private Maze maze;
	private Robot robot;

	private List<Step> steps;
	private List<Robot> robots;

	public FitnessCalculator() {
		steps = new LinkedList<>();
	}

	public List<Step> getLastMazeSteps() {
		return steps;
	}

	public FitnessCalculator setMaze(Maze maze) {
		this.maze = maze;
		return this;
	}

	public FitnessCalculator setRobot(Robot robot) {
		this.robot = robot;
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

	public FitnessCalculator setRobots(List<Robot> robots) {
		this.robots = robots;
		return this;
	}

	public void normalize() {
		Double minFitness = robots.stream().mapToDouble(Robot::getFitness).min().getAsDouble();
		robots.forEach((r) -> r.setNormalizedFitness(r.getFitness() - minFitness));

		Double totalFitness = robots.stream().mapToDouble(Robot::getNormalizedFitness).sum();
		robots.forEach((r) -> r.setFitnessProportional((r.getNormalizedFitness() * 100) / totalFitness));
	}

	public double calculate() {
		steps.clear();
		Step step = new Step();
		Step nextStep;
		step.count = 0;
		step.ry = maze.getSY();
		step.rx = maze.getSX();
		step.fitness = 0.0;
		step.state = 0;
		int maxSteps = (int) (maze.getEmptyPositions() * 1.2);

		int[][] visited = new int[maze.getYLength()][maze.getXLength()];
		for (int x = 0; x < maxSteps; x++) {
			step.input = robot.getInput(maze, step.ry, step.rx);
			step.output = robot.getOutput(step.state, step.input);
			steps.add(step);

			nextStep = new Step();
			nextStep.count = step.count + 1;
			nextStep.state = robot.getState(step.state, step.input);
			nextStep.ry = step.ry + robot.sumY(step.output);
			nextStep.rx = step.rx + robot.sumX(step.output);
			nextStep.fitness = step.fitness;

			if (maze.isEnd(nextStep.ry, nextStep.rx))
				nextStep.fitness += scoreEnd;

			if (visited[nextStep.ry][nextStep.rx] == 0)
				visited[nextStep.ry][nextStep.rx] = 1;
			else if (!maze.isEnd(nextStep.ry, nextStep.rx))
				nextStep.fitness += scoreRevisit;

			if (step.ry != nextStep.ry || step.rx != nextStep.rx) {
				nextStep.fitness += scoreStep;
				if (maze.isEmpty(nextStep.ry, nextStep.rx))
					nextStep.fitness += scoreCleanStep;
			}

			// Houve colisão com paredes ou tentou sair do vetor
			if (nextStep.ry == -1 || nextStep.ry == maze.getYLength() || nextStep.rx == -1 || nextStep.rx == maze.getXLength() || maze.isWall(nextStep.ry, nextStep.rx)) {
				// Volta para a posição anterior
				nextStep.ry -= robot.sumY(step.output);
				nextStep.rx -= robot.sumX(step.output);

				nextStep.fitness += scoreWallColision;
			}

			step = nextStep;
		}

		return steps.get(steps.size() - 1).fitness;
	}

	public boolean itsOver() {
		return false;
	}

	public class Step {

		public int count;
		public int output;
		public int input;
		public int state;
		public double fitness;
		public int rx;
		public int ry;

		@Override
		public String toString() {
			return "Step [count=" + count + ", output=" + output + ", input=" + input + ", state=" + state + ", fitness=" + fitness + ", rx=" + rx + ", ry=" + ry + "]";
		}
	}
}

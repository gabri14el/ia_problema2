package br.uefs.ecomp.ia.maze_robots;

public class FitnessCalculator {

	private double scoreWallColision;
	private double scoreStep;
	private double scoreEnd;

	private boolean stopInEnd;
	private int maxSteps;
	private Maze maze;
	private Robot robot;

	private int state;
	private int rx;
	private int ry;
	private double fitness;

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

	public FitnessCalculator setScoreEnd(double scoreEnd) {
		this.scoreEnd = scoreEnd;
		return this;
	}

	public FitnessCalculator run() {
		boolean end = false;
		state = 0;
		rx = maze.getSX();
		ry = maze.getSY();
		fitness = 0.0;

		int state = 0, input, output;
		for (int x = 0; x < maxSteps && !(stopInEnd && end); x++) {
			fitness += scoreStep;
			input = robot.getInput(maze, rx, ry);
			output = robot.getOutput(state, input);
			state = robot.getState(state, input);

			rx += robot.sumX(output);
			ry += robot.sumY(output);

			// Houve colisão com paredes ou tentou sair do vetor
			if (maze.isWall(rx, ry) || rx == 0 || rx > maze.getMaze().length || ry == 0 || ry > maze.getMaze()[0].length) {
				// Volta para a posição anterior
				rx -= robot.sumX(output);
				ry -= robot.sumY(output);

				fitness += scoreWallColision;
			} else if (maze.isEnd(rx, ry)) {
				fitness += scoreEnd;
				end = true;
			}
		}
		return this;
	}

	public Double getFitness() {
		return fitness;
	}

	public boolean itsOver() {
		return false;
	}
}

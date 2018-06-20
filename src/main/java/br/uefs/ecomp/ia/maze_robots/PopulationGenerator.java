package br.uefs.ecomp.ia.maze_robots;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PopulationGenerator {

	private long seed;
	private int size;
	private int inputSize;
	private int stateSize;
	private int outputSize;

	public PopulationGenerator setSeed(long seed) {
		this.seed = seed;
		return this;
	}

	public PopulationGenerator setSize(int size) {
		this.size = size;
		return this;
	}

	public PopulationGenerator setInputSize(int inputSize) {
		this.inputSize = inputSize;
		return this;
	}

	public PopulationGenerator setStateSize(int stateSize) {
		this.stateSize = stateSize;
		return this;
	}

	public PopulationGenerator setOutputSize(int outputSize) {
		this.outputSize = outputSize;
		return this;
	}

	public List<Robot> generate() {
		List<Robot> population = new LinkedList<>();

		Integer[][] stateMachine;
		Robot robot;
		int output;
		int state;
		Random random = new Random(seed);

		for (int count = 0; count < size; count++) {
			stateMachine = new Integer[inputSize][stateSize];

			for (int x = 0; x < inputSize; x++) {
				for (int y = 0; y < stateSize; y++) {
					output = random.nextInt(outputSize + 1);
					state = random.nextInt(stateSize);
					stateMachine[x][y] = (output << stateSize) + state; // transforma 11² em 110², caso COUNT_INITIAL_STATE seja 1;
				}
			}

			robot = new Robot();
			robot.setValue(stateMachine);
			population.add(robot);
		}

		return population;
	}
}

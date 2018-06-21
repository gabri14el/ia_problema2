package br.uefs.ecomp.ia.maze_robots;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PopulationGenerator {

	private Random random;
	private int size;
	private int stateSize;

	public PopulationGenerator setRandom(Random random) {
		this.random = random;
		return this;
	}

	public PopulationGenerator setSize(int size) {
		this.size = size;
		return this;
	}

	public PopulationGenerator setStateSize(int stateSize) {
		this.stateSize = stateSize;
		return this;
	}

	public List<Robot> generate() {
		List<Robot> population = new LinkedList<>();

		for (int count = 0; count < size; count++)
			population.add(new Robot(stateSize));

		population.forEach((r) -> {
			r.forEach((s, i) -> {
				r.setOutput(s, i, random.nextInt(Robot.OUTPUT_SIZE));
				r.setState(s, i, random.nextInt(stateSize));
			});
		});

		return population;
	}
}

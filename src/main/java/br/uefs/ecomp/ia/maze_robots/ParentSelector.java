package br.uefs.ecomp.ia.maze_robots;

import java.util.List;
import java.util.Random;

public class ParentSelector {

	private List<Robot> population;
	private Random random;

	public ParentSelector setPopulation(List<Robot> population) {
		this.population = population;
		return this;
	}

	public ParentSelector setRandom(Random random) {
		this.random = random;
		return this;
	}

	public List<Robot> select() {
		List<Robot> robots = population.subList(0, population.size() / 2);
		return robots;
	}
}

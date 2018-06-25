package br.uefs.ecomp.ia.maze_robots;

import java.util.List;
import java.util.Random;

public class ParentSelector {

	private List<Robot> population;
	private Random random;
	private int goodRate;

	public ParentSelector setPopulation(List<Robot> population) {
		this.population = population;
		return this;
	}

	public ParentSelector setRandom(Random random) {
		this.random = random;
		return this;
	}

	public ParentSelector setGoodRate(int goodRate) {
		this.goodRate = goodRate;
		return this;
	}

	public List<Robot> select() {
		/*int good = (int) ((double) population.size() * (goodRate / 100));
		int x = (population.size() / 2) - good;
		List<Robot> robots = population.subList(0, good);
		robots.addAll(population.subList(population.size() - x - 1, population.size() - 1));*/
		List<Robot> robots = population.subList(0, population.size() / 2);
		return robots;
	}
}

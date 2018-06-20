package br.uefs.ecomp.ia.maze_robots;

import java.util.List;

public class SurvivorSelector {

	private List<Robot> population;

	public SurvivorSelector setPopulation(List<Robot> population) {
		this.population = population;
		return this;
	}

	public List<Robot> select() {
		return population;
	}
}

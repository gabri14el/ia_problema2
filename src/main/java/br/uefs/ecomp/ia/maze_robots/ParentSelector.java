package br.uefs.ecomp.ia.maze_robots;

import java.util.List;

public class ParentSelector {

	private List<Robot> population;

	public ParentSelector setPopulation(List<Robot> population) {
		this.population = population;
		return this;
	}

	public List<Robot> select() {
		return population;
	}
}

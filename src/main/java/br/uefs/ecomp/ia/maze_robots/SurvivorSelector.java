package br.uefs.ecomp.ia.maze_robots;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import br.uefs.ecomp.ia.maze_robots.App.ParentSelection;

public class SurvivorSelector {

	private Random random;
	private int populationSize;
	private List<Robot> parents;
	private List<Robot> children;
	private ParentSelection pSMode;
	private boolean killParents;

	public SurvivorSelector setRandom(Random random) {
		this.random = random;
		return this;
	}

	public SurvivorSelector setParents(List<Robot> parents) {
		this.parents = parents;
		return this;
	}

	public SurvivorSelector setChildren(List<Robot> children) {
		this.children = children;
		return this;
	}

	public SurvivorSelector setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
		return this;
	}

	public SurvivorSelector setPSMode(ParentSelection pSMode) {
		this.pSMode = pSMode;
		return this;
	}

	public SurvivorSelector setKillParents(boolean killParents) {
		this.killParents = killParents;
		return this;
	}

	public List<Robot> select() {
		List<Robot> robots = new LinkedList<>();
		if (!killParents)
			robots.addAll(parents);
		robots.addAll(children);
		robots.sort(App.comparator);

		if (killParents)
			return robots;

		if (pSMode == ParentSelection.TORNEIO) {
			List<Robot> selecteds = new LinkedList<>();
			if (robots.size() % 2 == 1)
				selecteds.add(robots.remove(0));
			Collections.shuffle(robots, random);

			for (int x = 0; x < robots.size(); x += 2) {
				if (robots.get(x).getFitness() > robots.get(x + 1).getFitness())
					selecteds.add(robots.get(x));
				else
					selecteds.add(robots.get(x + 1));
			}

			return selecteds;
		} else
			return robots.subList(0, populationSize);
	}
}

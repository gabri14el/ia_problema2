package br.uefs.ecomp.ia.maze_robots;

import java.util.LinkedList;
import java.util.List;
import br.uefs.ecomp.ia.maze_robots.App.ParentSelection;

public class SurvivorSelector {

	private int populationSize;
	private List<Robot> parents;
	private List<Robot> children;
	private ParentSelection pSMode;
	private boolean killParents;

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

		return (killParents) ? robots : robots.subList(0, populationSize);
	}
}

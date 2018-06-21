package br.uefs.ecomp.ia.maze_robots;

import java.util.LinkedList;
import java.util.List;

public class SurvivorSelector {

	private List<Robot> parents;
	private List<Robot> children;

	public SurvivorSelector setParents(List<Robot> parents) {
		this.parents = parents;
		return this;
	}

	public SurvivorSelector setChildren(List<Robot> children) {
		this.children = children;
		return this;
	}

	public List<Robot> select() {
		List<Robot> robots = new LinkedList<>();
		robots.addAll(parents);
		robots.addAll(children);
		return robots;
	}
}

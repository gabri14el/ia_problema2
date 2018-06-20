package br.uefs.ecomp.ia.maze_robots;

import java.util.LinkedList;
import java.util.List;

public class Recombinator {

	private List<Robot> parents;

	public Recombinator setParents(List<Robot> parents) {
		this.parents = parents;
		return this;
	}

	public List<Robot> recombine() {
		List<Robot> robots = new LinkedList<>();
		parents.forEach((p) -> robots.add(p.clone()));
		return robots;
	}
}

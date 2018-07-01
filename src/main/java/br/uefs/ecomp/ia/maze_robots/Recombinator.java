package br.uefs.ecomp.ia.maze_robots;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import br.uefs.ecomp.ia.maze_robots.App.ParentSelection;

public class Recombinator {

	private int populationSize;
	private List<Robot> parents;
	private ParentSelection pSMode;
	private Random random;

	public Recombinator setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
		return this;
	}

	public Recombinator setParents(List<Robot> parents) {
		this.parents = parents;
		return this;
	}

	public Recombinator setPSMode(ParentSelection pSMode) {
		this.pSMode = pSMode;
		return this;
	}

	public Recombinator setRandom(Random random) {
		this.random = random;
		return this;
	}

	public List<Robot> recombine() {
		List<Robot> robots = new LinkedList<>();

		switch (pSMode) {
			case PROPORCIONAL: {
				for (Robot p : parents) {
					int childrenToGenerate = (int) Math.round(populationSize * (p.getFitnessProportional() / 100));
					for (int x = 0; x < childrenToGenerate; x++)
						robots.add(p.clone());
				}
				break;
			}
			case PROPORCIONAL_RANDOM: {
				double value;
				double v;
				while (robots.size() < populationSize) {
					v = 0.0;
					value = random.nextDouble();
					for (Robot p : parents) {
						v += p.getFitnessProportional();
						if (v >= value) {
							robots.add(p.clone());
							break;
						}
					}
				}
				break;
			}
			case TORNEIO:
			case BI_CLASSISTA:
			case ELITISTA: {
				int childrenToGenerate = populationSize / parents.size();
				parents.forEach((p) -> {
					for (int x = 0; x < childrenToGenerate; x++)
						robots.add(p.clone());
				});
				break;
			}
		}

		while (robots.size() < populationSize)
			robots.add(parents.get(0).clone());
		return robots;
	}
}

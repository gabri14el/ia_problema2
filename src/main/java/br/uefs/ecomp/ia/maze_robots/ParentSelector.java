package br.uefs.ecomp.ia.maze_robots;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import br.uefs.ecomp.ia.maze_robots.App.ParentSelection;

public class ParentSelector {

	private Random random;
	private List<Robot> population;
	private ParentSelection mode;
	private int percentagemToSelect;
	private int mPercentage;
	private int pPercentage;

	public ParentSelector setRandom(Random random) {
		this.random = random;
		return this;
	}

	public ParentSelector setPopulation(List<Robot> population) {
		this.population = population;
		return this;
	}

	public ParentSelector setMode(ParentSelection mode) {
		this.mode = mode;
		return this;
	}

	public ParentSelector setPercentagemToSelect(int percentagemToSelect) {
		this.percentagemToSelect = percentagemToSelect;
		return this;
	}

	public ParentSelector setMPercentage(int mPercentage) {
		this.mPercentage = mPercentage;
		return this;
	}

	public ParentSelector setPPercentage(int pPercentage) {
		this.pPercentage = pPercentage;
		return this;
	}

	public List<Robot> select() {
		List<Robot> robots = new LinkedList<>();

		int sizeToSelect = (int) (population.size() * (percentagemToSelect / 100.0));

		switch (mode) {
			case PROPORCIONAL:
			case PROPORCIONAL_RANDOM:
			case ELITISTA: {
				robots.addAll(population.subList(0, sizeToSelect));
				break;
			}

			case TORNEIO: {
				List<Robot> randomRobots = new LinkedList<>(population);
				if (randomRobots.size() % 2 == 1) {
					robots.add(randomRobots.get(0));
					randomRobots.remove(0);
				}
				Collections.shuffle(randomRobots, random);
				for (int x = 0; x < randomRobots.size(); x += 2) {
					if (randomRobots.get(x).getFitness() > randomRobots.get(x + 1).getFitness())
						robots.add(randomRobots.get(x));
					else
						robots.add(randomRobots.get(x + 1));
				}
				sizeToSelect = robots.size();
				break;
			}

			case BI_CLASSISTA: {
				int m_size = (int) (sizeToSelect * (mPercentage / 100.0));
				int p_size = (int) (sizeToSelect * (pPercentage / 100.0));
				robots.addAll(population.subList(0, m_size));
				robots.addAll(population.subList(p_size, population.size() - 1));
				break;
			}

		}

		if (robots.size() < sizeToSelect) {
			List<Robot> randomRobots = new LinkedList<>(population);
			randomRobots.removeIf((r) -> robots.contains(r));
			randomRobots.sort((r1, r2) -> random.nextInt() - random.nextInt());
			robots.addAll(randomRobots.subList(0, sizeToSelect - robots.size()));
		}

		return robots;
	}
}

package br.uefs.ecomp.ia.maze_robots.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class EvolutionaryAlgorithm<R extends Representation<?>> {

	protected List<R> population;

	protected List<R> parents;
	protected List<R> children;

	protected long generation;

	protected Map<String, Long> times;

	public EvolutionaryAlgorithm() {
		population = null;
		parents = null;
		children = null;
		generation = 0;
		times = new LinkedHashMap<>();
	}

	public void run() {
		createStartPopulation();
		calculateFitness(population);
		logPopulation();
		System.out.println();

		generation = 1;
		long startGenerator;
		while (!checkStopCondition()) {
			startGenerator = System.currentTimeMillis();

			selectParents();
			times.put("Select Parents", getTime(startGenerator));

			recombine();
			times.put("Recombine", getTime(startGenerator));

			mutate();
			times.put("Mutate", getTime(startGenerator));

			calculateFitness(children);
			times.put("Calculate Fitness", getTime(startGenerator));

			selectSurvivors();
			times.put("Select Survivors", getTime(startGenerator));

			logPopulation();
			generation++;
			times.put("Total", getTime(startGenerator));
			System.out.println("----- Times -----");
			times.forEach((k, v) -> System.out.println(k + ": " + v));
			System.out.println("\n");
		}
	}

	protected long getTime(long startGenerator) {
		return (System.currentTimeMillis() - startGenerator);
	}

	protected abstract void createStartPopulation();

	protected abstract void calculateFitness(List<R> r);

	protected abstract void logPopulation();

	protected abstract boolean checkStopCondition();

	protected abstract void selectParents();

	protected abstract void recombine();

	protected abstract void mutate();

	protected abstract void selectSurvivors();
}

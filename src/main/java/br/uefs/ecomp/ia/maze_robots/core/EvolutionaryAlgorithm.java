package br.uefs.ecomp.ia.maze_robots.core;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class EvolutionaryAlgorithm<R extends Representation<?>> {

	protected List<R> population;

	protected List<R> parents;
	protected List<R> children;

	protected long generation;

	protected Map<String, Double> times;

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
		long start = System.currentTimeMillis();
		long startGenerator;
		while (!checkStopCondition()) {
			startGenerator = System.currentTimeMillis();
			selectParents();
			times.put("Select Parents", getTime(startGenerator));

			startGenerator = System.currentTimeMillis();
			recombine();
			times.put("Recombine", getTime(startGenerator));

			startGenerator = System.currentTimeMillis();
			mutate();
			times.put("Mutate", getTime(startGenerator));

			startGenerator = System.currentTimeMillis();
			calculateFitness(children);
			times.put("Calculate Fitness", getTime(startGenerator));

			startGenerator = System.currentTimeMillis();
			selectSurvivors();
			times.put("Select Survivors", getTime(startGenerator));

			startGenerator = System.currentTimeMillis();
			logPopulation();
			generation++;
			times.put("Total G.", getTime(startGenerator));
			logTimes(System.out);
		}
		times.put("Total", getTime(start));
	}

	public void logTimes(PrintStream out) {
		out.println("--------- Times ---------");
		times.forEach((k, v) -> out.format("%17s: %3.3fs\n", k, v));
		out.println("\n");
	}

	protected double getTime(long startGenerator) {
		return ((double) (System.currentTimeMillis() - startGenerator)) / 1000.0;
	}

	protected abstract void createStartPopulation();

	protected abstract void selectParents();

	protected abstract void recombine();

	protected abstract void mutate();

	protected abstract void selectSurvivors();

	protected abstract void calculateFitness(List<R> r);

	protected abstract void logPopulation();

	protected abstract boolean checkStopCondition();
}

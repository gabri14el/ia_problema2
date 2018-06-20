package br.uefs.ecomp.ia.maze_robots.core;

import java.util.List;

public abstract class EvolutionaryAlgorithm<R extends Representation<?>> {

	protected List<R> population;

	protected List<R> parents;
	protected List<R> children;

	protected long generation;

	public EvolutionaryAlgorithm() {
		population = null;
		parents = null;
		children = null;
		generation = 0;
	}

	public void run() {
		createStartPopulation();
		calculateFitness();
		logPopulation();

		generation = 1;
		while (!checkStopCondition()) {
			selectParents();
			recombine();
			mutate();
			calculateFitness();
			selectSurvivors();
			generation++;
			logPopulation();
		}
	}

	protected abstract void createStartPopulation();

	protected abstract void calculateFitness();

	protected abstract void logPopulation();

	protected abstract boolean checkStopCondition();

	protected abstract void selectParents();

	protected abstract void recombine();

	protected abstract void mutate();

	protected abstract void selectSurvivors();
}

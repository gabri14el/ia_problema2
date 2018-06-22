package br.uefs.ecomp.ia.maze_robots;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;

public class PopulationLogger {

	private long startTime;
	private long generation;
	private List<Robot> population;

	public PopulationLogger setStartTime(long startTime) {
		this.startTime = startTime;
		return this;
	}

	public PopulationLogger setGeneration(long generation) {
		this.generation = generation;
		return this;
	}

	public PopulationLogger setPopulation(List<Robot> population) {
		this.population = population;
		return this;
	}

	public void log() {
		Robot best = population.get(0);
		Robot worse = population.get(population.size() - 1);
		int maxStates = population.stream().max(Comparator.comparing(Robot::getStateSize)).get().getStateSize();
		int minStates = population.stream().min(Comparator.comparing(Robot::getStateSize)).get().getStateSize();
		double avgStates = population.stream().mapToInt(Robot::getStateSize).average().getAsDouble();
		long equalBest = population.stream().filter((r) -> r.getFitness().doubleValue() == best.getFitness().doubleValue()).count();
		long equalWorse = population.stream().filter((r) -> r.getFitness().doubleValue() == worse.getFitness().doubleValue()).count();
		double average = population.stream().mapToDouble(Robot::getFitness).average().getAsDouble();
		long aboveAverage = population.stream().filter((r) -> r.getFitness().doubleValue() >= average).count();
		long belowAverage = population.stream().filter((r) -> r.getFitness().doubleValue() < average).count();

		System.out.flush();
		System.out.format("---------------- GENERATION %05d ----------------\n", generation);
		System.out.format("Fitness Max - id: %6d states: %3d    fitness: %5.0f\n", best.getId(), best.getStateSize(), best.getFitness());
		System.out.format("Fitness Min - id: %6d states: %3d    fitness: %5.0f\n", worse.getId(), worse.getStateSize(), worse.getFitness());
		System.out.format("Fitness Average:               %5.0f\n", average);
		System.out.format("Count Above Average:           %5d\n", aboveAverage);
		System.out.format("Count Below Average:           %5d\n", belowAverage);
		System.out.format("Count Equal Best:              %5d\n", equalBest);
		System.out.format("Count Equal Worse:             %5d\n", equalWorse);
		System.out.format("States Average:                %5.0f\n", avgStates);
		System.out.format("States Max:                    %5d\n", maxStates);
		System.out.format("States Min:                    %5d\n", minStates);

		File dir = new File("output" + File.separator + String.format("run_%13d", startTime));
		dir.mkdirs();

		File f = new File(dir, "output.txt");
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8"));) {
			out.format("---------------- GENERATION %05d ----------------\n", generation);
			out.format("Fitness Max - id: %6d states: %3d    fitness: %5.0f\n", best.getId(), best.getStateSize(), best.getFitness());
			out.format("Fitness Min - id: %6d states: %3d    fitness: %5.0f\n", worse.getId(), worse.getStateSize(), worse.getFitness());
			out.format("Fitness Average:               %5.0f\n", average);
			out.format("Count Above Average:           %5d\n", aboveAverage);
			out.format("Count Below Average:           %5d\n", belowAverage);
			out.format("Count Equal Best:              %5d\n", equalBest);
			out.format("Count Equal Worse:             %5d\n", equalWorse);
			out.format("States Max                     %5d\n", maxStates);
			out.format("States Min                     %5d\n", minStates);
			out.format("Best  - %s\n", best.toString());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}

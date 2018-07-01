package br.uefs.ecomp.ia.maze_robots;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
		System.out.format("------ GENERATION %05d ------\n", generation);
		System.out.format("F max - id: %6d s: %3d    f: %5.0f\n", best.getId(), best.getStateSize(), best.getFitness());
		System.out.format("F min - id: %6d s: %3d    f: %5.0f\n", worse.getId(), worse.getStateSize(), worse.getFitness());
		System.out.format("F avg:      %5.0f\n", average);
		System.out.format("C A avg:    %6d\n", aboveAverage);
		System.out.format("C B avb:    %6d\n", belowAverage);
		System.out.format("C E best:   %6d\n", equalBest);
		System.out.format("C E worse:  %6d\n", equalWorse);
		System.out.format("S avg:      %6.0f\n", avgStates);
		System.out.format("S max:      %6d\n", maxStates);
		System.out.format("S min:      %6d\n", minStates);

		File dir = new File("output" + File.separator + String.format("run_%13d", startTime));
		dir.mkdirs();

		File f = new File(dir, "output.txt");
		try (PrintStream out = new PrintStream(new FileOutputStream(f, true), true, "UTF-8")) {
			out.format("------ GENERATION %05d ------\n", generation);
			out.format("F max - id: %6d s: %3d    f: %5.0f\n", best.getId(), best.getStateSize(), best.getFitness());
			out.format("F min - id: %6d s: %3d    f: %5.0f\n", worse.getId(), worse.getStateSize(), worse.getFitness());
			out.format("F avg:      %5.0f\n", average);
			out.format("C A avg:    %6d\n", aboveAverage);
			out.format("C B avb:    %6d\n", belowAverage);
			out.format("C E best:   %6d\n", equalBest);
			out.format("C E worse:  %6d\n", equalWorse);
			out.format("S avg:      %6.0f\n", avgStates);
			out.format("S max:      %6d\n", maxStates);
			out.format("S min:      %6d\n", minStates);
			if (generation >= App.SC_MAX_GENERATION) {
				out.println(worse);
				out.println();
				out.println("BEST " + best);
				App.getInstance().logTimes(out);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}

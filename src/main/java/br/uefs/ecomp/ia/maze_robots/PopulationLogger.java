package br.uefs.ecomp.ia.maze_robots;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
		long equalBest = population.stream().filter((r) -> r.getFitness().doubleValue() == best.getFitness().doubleValue()).count();
		Robot worse = population.get(population.size() - 1);
		long equalWorse = population.stream().filter((r) -> r.getFitness().doubleValue() == worse.getFitness().doubleValue()).count();
		double average = population.stream().mapToDouble(Robot::getFitness).average().getAsDouble();
		long aboveAverage = population.stream().filter((r) -> r.getFitness().doubleValue() >= average).count();
		long belowAverage = population.stream().filter((r) -> r.getFitness().doubleValue() < average).count();

		System.out.format("---------------- GENERATION %05d ----------------\n", generation);
		System.out.format("Best  - id: %6d fitness: %5.0f\n", best.getId(), best.getFitness());
		System.out.format("Worse - id: %6d fitness: %5.0f\n", worse.getId(), worse.getFitness());
		System.out.format("Count Equal Best:        %5d\n", equalBest);
		System.out.format("Count Equal Worse:       %5d\n", equalWorse);
		System.out.format("Fitness Average:         %5.0f\n", average);
		System.out.format("Count Above Average:     %5d\n", aboveAverage);
		System.out.format("Count Below Average:     %5d\n", belowAverage);

		File dir = new File("output" + File.separator + String.format("run_%13d", startTime));
		dir.mkdirs();

		File f = new File(dir, "output.txt");
		File f2 = new File("output", "output.txt");
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8"));
				PrintWriter out2 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f2, true), "UTF-8"));) {
			out.format("---------------- GENERATION %05d ----------------\n", generation);
			out.format("Best  - %s\n", best.toString());
			out.format("Worse - %s\n", worse.toString());
			out.format("Count Equal Best:        %3d\n", equalBest);
			out.format("Count Equal Worse:       %3d\n", equalWorse);
			out.format("Fitness Average:         %5.0f\n", average);
			out.format("Count Above Average:     %3d\n", aboveAverage);
			out.format("Count Below Average:     %3d\n", belowAverage);

			out2.format("Best  - %s\n", best.toString());
			out2.format("Worse - %s\n", worse.toString());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}

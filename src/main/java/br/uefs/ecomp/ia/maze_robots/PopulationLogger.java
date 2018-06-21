package br.uefs.ecomp.ia.maze_robots;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
		File dir = new File("output" + File.separator + "run_" + startTime);
		dir.mkdirs();
		File f;

		f = new File(dir, String.format("definitions.txt"));
		if (!f.exists()) {
			try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
				for (Field field : App.class.getDeclaredFields()) {
					field.setAccessible(true);
					if (Modifier.isStatic(field.getModifiers()))
						out.println(field.getName() + ": " + field.get(null));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		System.out.println(String.format("---------------------------------------------- GENERATION %05d ----------------------------------------------", generation));
		f = new File(dir, String.format("gen_%05d_popu.txt", generation));
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
			population.forEach((r) -> out.println(r));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		f = new File(dir, String.format("gen_%05d_best.txt", generation));
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
			System.out.println(population.get(0));
			out.println(population.get(0));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		f = new File(dir, String.format("gen_%05d_worse.txt", generation));
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
			System.out.println(population.get(population.size() - 1));
			out.println(population.get(population.size() - 1));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("\n\n");
	}
}

package br.uefs.ecomp.ia.maze_robots;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import br.uefs.ecomp.ia.maze_robots.core.EvolutionaryAlgorithm;

public class App extends EvolutionaryAlgorithm<Robot> {

	// Codições de parada
	private static final int SC_MAX_GENERATION = 1; // Número máximo de gerações para parar o algoritmo
	private static final boolean SC_STOP_IN_END = true; // Indica se o algoritmo deve ser finalizado quando um robô chega ao fim;
	private boolean stop_end;

	// Limitações
	public static final int STATE_MAX = 999;
	public static final int STATE_MIN = 1;
	public static final int STATE_INITIAL = 2;

	// População
	private static final int POPULATION_SIZE = 100; // Tamanho da população

	// Mutação
	private static final int MUTATION_CHANGE_OUTPUT = 20; // porcentagem
	private static final int MUTATION_CHANGE_STATE = 20; // porcentagem
	private static final int MUTATION_ADD_STATE = 5; // porcentagem
	private static final int MUTATION_DEL_STATE = 10; // porcentagem

	// Outros Parâmetros
	private static final long START = System.currentTimeMillis();
	private static final long[] RANDOM_SEEDS = new long[] { // Conjunto de sementes para gerar números aleatórios
			135827968109L, 208248857186L, 432099974000L, 863278201449L, 461431272318L, 666015161980L, 586981007620L, 877453781828L, 574598151547L, 218042335334L, 435229484920L, 236406828574L,
			363310412856L, 337560821399L, 918214207238L, 654497046710L, 923238918586L, 388953847145L, 823029413652L, 861453743932L
	};
	private static final long RANDOM_SEED = RANDOM_SEEDS[0]; // Semente para gerar números aleatórios usada atualmente

	private final Comparator<? super Robot> comparator = (r1, r2) -> {
		Double fitness1 = (r1.getFitness() != null) ? r1.getFitness() : Double.MAX_VALUE;
		Double fitness2 = (r2.getFitness() != null) ? r2.getFitness() : Double.MAX_VALUE;
		int r = fitness1.compareTo(fitness2);
		return (r != 0) ? r : Long.compare(r1.getId(), r2.getId());
	};

	@Override
	protected void createStartPopulation() {
		population = new LinkedList<>();

		Integer[][] stateMachine;
		Robot robot;
		int output;
		int state;
		Random random = new Random(RANDOM_SEED);

		for (int count = 0; count < POPULATION_SIZE; count++) {
			stateMachine = new Integer[Robot.COUNT_INPUTS][STATE_INITIAL];

			for (int x = 0; x < Robot.COUNT_INPUTS; x++) {
				for (int y = 0; y < STATE_INITIAL; y++) {
					output = random.nextInt(Robot.COUNT_OUTPUTS + 1);
					state = random.nextInt(STATE_INITIAL);
					stateMachine[x][y] = (output << STATE_INITIAL) + state; // transforma 11² em 110², caso COUNT_INITIAL_STATE seja 1;
				}
			}

			robot = new Robot();
			robot.setValue(stateMachine);
			population.add(robot);
		}
	}

	@Override
	protected void calculateFitness() {
		Double fitness;

		for (Robot r : population) {
			if (r.getFitness() != null)
				continue;

			fitness = 0.0;
			// TODO - Calcular fitness
			// TODO - Se chegar ao fim, stop_end = true

			r.setFitness(fitness);
		}

		population.sort(comparator);
	}

	@Override
	protected void logPopulation() {
		File dir = new File("output" + File.separator + "run_" + START);
		dir.mkdirs();
		File f;

		f = new File(dir, String.format("definitions.txt"));
		if (!f.exists()) {
			try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
				for (Field field : App.class.getDeclaredFields()) {
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
		System.out.println("\n\n");
	}

	@Override
	protected boolean checkStopCondition() {
		return SC_MAX_GENERATION < generation || (SC_STOP_IN_END && stop_end);
	}

	@Override
	protected void selectParents() {
		parents = new LinkedList<>(population);
		parents.sort(comparator);
	}

	@Override
	protected void recombination() {
		children = new LinkedList<>();
		parents.forEach((p) -> children.add(p.clone()));
	}

	@Override
	protected void applyMutation() {
		Random random = new Random(RANDOM_SEED);
		boolean addColumn = MUTATION_ADD_STATE >= random.nextInt(100);
		boolean delColumn = MUTATION_DEL_STATE >= random.nextInt(100);

		for (Robot r : children) {

		}
	}

	@Override
	protected void selectSurvivors() {}

	public static void main(String[] args) {
		new App().run();
	}
}

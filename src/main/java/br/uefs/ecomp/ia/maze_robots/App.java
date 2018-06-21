package br.uefs.ecomp.ia.maze_robots;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import br.uefs.ecomp.ia.maze_robots.core.EvolutionaryAlgorithm;

public class App extends EvolutionaryAlgorithm<Robot> {

	// Codições de parada
	private static final int SC_MAX_GENERATION = 1; // Número máximo de gerações para parar o algoritmo
	private static final boolean SC_STOP_IN_END = true; // Indica se o algoritmo deve ser finalizado quando um robô chega ao fim;
	private static final int SC_MAX_STEPS = 1000; // Máximo de iterações da máquina de estados
	private boolean stop_end;

	// Limitações
	public static final int STATE_MIN = 1;
	public static final int STATE_MAX = 999;
	public static final int STATE_INITIAL = 2;

	// População
	private static final int POPULATION_SIZE = 100; // Tamanho da população

	// Mutação
	private static final int MUTATION_CHANGE_STATE = 20; // porcentagem
	private static final int MUTATION_CHANGE_OUTPUT = 20; // porcentagem
	private static final int MUTATION_ADD_STATE = 5; // porcentagem
	private static final int MUTATION_DEL_STATE = 10; // porcentagem

	// Outros Parâmetros
	private static final long START_TIME = System.currentTimeMillis();
	private static final long[] RANDOM_SEEDS = new long[] { // Conjunto de sementes para gerar números aleatórios
			135827968109L, 208248857186L, 432099974000L, 863278201449L, 461431272318L, 666015161980L, 586981007620L, 877453781828L, 574598151547L, 218042335334L, 435229484920L, 236406828574L,
			363310412856L, 337560821399L, 918214207238L, 654497046710L, 923238918586L, 388953847145L, 823029413652L, 861453743932L
	};
	private static final long RANDOM_SEED = RANDOM_SEEDS[0]; // Semente para gerar números aleatórios usada atualmente
	private Random random = new Random(RANDOM_SEED);

	private final Comparator<? super Robot> comparator = (r1, r2) -> {
		Double fitness1 = (r1.getFitness() != null) ? r1.getFitness() : Double.MIN_VALUE;
		Double fitness2 = (r2.getFitness() != null) ? r2.getFitness() : Double.MIN_VALUE;
		int r = fitness1.compareTo(fitness2);
		return (r != 0) ? r : Long.compare(r1.getId(), r2.getId());
	};

	@Override
	protected void createStartPopulation() {
		PopulationGenerator generator = new PopulationGenerator()
				.setRandom(random)
				.setSize(POPULATION_SIZE)
				.setStateSize(STATE_INITIAL);
		population = generator.generate();
	}

	@Override
	protected void calculateFitness() {
		Double fitness;
		boolean end;
		FitnessCalculator calculator = new FitnessCalculator();
		List<Robot> robots = new LinkedList<>();
		robots.addAll(population);
		if (children != null)
			robots.addAll(children);

		for (Robot r : robots) {
			if (r.getFitness() != null)
				continue;

			fitness = 0.0;
			end = true;
			for (Maze m : Maze.mazes) {
				calculator.setMaxSteps(SC_MAX_STEPS)
						.setMaze(m)
						.setRobot(r)
						.run();
				fitness += calculator.getFitness();
				if (!end)
					end = calculator.itsOver();
			}

			r.setFitness(fitness);
		}

		population.sort(comparator);
	}

	@Override
	protected void logPopulation() {
		PopulationLogger logger = new PopulationLogger()
				.setStartTime(START_TIME)
				.setGeneration(generation)
				.setPopulation(population);
		logger.log();
	}

	@Override
	protected boolean checkStopCondition() {
		return SC_MAX_GENERATION < generation || (SC_STOP_IN_END && stop_end);
	}

	@Override
	protected void selectParents() {
		ParentSelector selector = new ParentSelector()
				.setPopulation(population);
		parents = selector.select();
	}

	@Override
	protected void recombine() {
		Recombinator recombinator = new Recombinator()
				.setParents(parents);
		children = recombinator.recombine();
	}

	@Override
	protected void mutate() {
		Mutator mutator = new Mutator()
				.setRandom(random)
				.setStateMin(STATE_MIN)
				.setStateMax(STATE_MAX)
				.setAddState(MUTATION_ADD_STATE)
				.setDelState(MUTATION_DEL_STATE)
				.setChangeState(MUTATION_CHANGE_STATE)
				.setChangeOutput(MUTATION_CHANGE_OUTPUT);
		children.forEach((r) -> mutator.mutate(r));
	}

	@Override
	protected void selectSurvivors() {
		SurvivorSelector selector = new SurvivorSelector()
				.setParents(parents)
				.setChildren(children);
		population = selector.select();
	}

	public static void main(String[] args) {
		new App().run();
	}
}

package br.uefs.ecomp.ia.maze_robots;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import br.uefs.ecomp.ia.maze_robots.core.EvolutionaryAlgorithm;

public class App extends EvolutionaryAlgorithm<Robot> {

	private static App instance;

	public static App getInstance() {
		if (instance == null)
			instance = new App();
		return instance;
	}

	// Codições de parada
	public static final int SC_MAX_GENERATION = 10000; // Número máximo de gerações para parar o algoritmo
	public static final boolean SC_STOP_IN_END = false; // Indica se o algoritmo deve ser finalizado quando um robô chega ao fim;
	public static final int SC_MAX_STEPS = 100; // Máximo de iterações da máquina de estados
	public boolean stop_end;

	// Limitações
	public static final int STATE_MIN = 1;
	public static final int STATE_MAX = 999;
	public static final int STATE_INITIAL = 10;

	// População
	public static final int POPULATION_SIZE = 1000; // Tamanho da população

	// Fitness
	public static final double FITNESS_WALL_COLISION = -5.0; // Pontuação em caso de colisão com paredes
	public static final double FITNESS_STEP = -1.0; // Pontuação para cada passo dado
	public static final double FITNESS_CLEAN_STEP = 5.0; // Pontuação para cada passo dado em um local disponível
	public static final double FITNESS_REVISIT = -25.0; // Pontuação para cada passo dado em um local disponível
	public static final double FITNESS_END = 50.0; // Pontuação por chegar ao fim (e manter-se lá, caso não interrompa a execução)

	// Mutação
	public static final double M_CHANGE_STATE_START = 100; // porcentagem
	public static final double M_CHANGE_STATE_END = 5; // porcentagem
	public static final double M_CHANGE_OUTPUT_START = 100; // porcentagem
	public static final double M_CHANGE_OUTPUT_END = 5; // porcentagem
	public static final double M_ADD_STATE_START = 100; // porcentagem
	public static final double M_ADD_STATE_END = 0; // porcentagem
	public static final double M_DEL_STATE_START = 0; // porcentagem
	public static final double M_DEL_STATE_END = 30; // porcentagem

	// Outros Parâmetros
	public static final long START_TIME = System.currentTimeMillis();
	public static final long[] RANDOM_SEEDS = new long[] { // Conjunto de sementes para gerar números aleatórios
			135827968109L, 208248857186L, 432099974000L, 863278201449L, 461431272318L, 666015161980L, 586981007620L, 877453781828L, 574598151547L, 218042335334L, 435229484920L, 236406828574L,
			363310412856L, 337560821399L, 918214207238L, 654497046710L, 923238918586L, 388953847145L, 823029413652L, 861453743932L
	};
	public static final long RANDOM_SEED = RANDOM_SEEDS[0]; // Semente para gerar números aleatórios usada atualmente
	private Random random = new Random(RANDOM_SEED);

	private List<Maze> mazes = Arrays.asList(Maze.mazes);

	public static final Comparator<? super Robot> comparator = (r1, r2) -> {
		Double fitness1 = (r1.getFitness() != null) ? r1.getFitness() : Double.MIN_VALUE;
		Double fitness2 = (r2.getFitness() != null) ? r2.getFitness() : Double.MIN_VALUE;
		return fitness2.compareTo(fitness1);
	};

	/**
	 * Retorna um valod decimal que indica a porcentagem de conclusão do processo
	 * 
	 * @return 0.0 ~ 1.0
	 */
	private double getPercentageCompleted() {
		return ((generation * (double) 100) / SC_MAX_GENERATION) / (double) 100;
	}

	private double getTemporalValue(double start, double end) {
		return ((start * (1 - getPercentageCompleted())) + (end * getPercentageCompleted()));
	}

	@Override
	protected void createStartPopulation() {
		PopulationGenerator generator = new PopulationGenerator()
				.setRandom(random)
				.setSize(POPULATION_SIZE)
				.setStateSize(STATE_INITIAL);
		population = generator.generate();
	}

	@Override
	protected void calculateFitness(List<Robot> robots) {
		FitnessCalculator calculator = new FitnessCalculator()
				.setMaxSteps(SC_MAX_STEPS)
				.setStopInEnd(SC_STOP_IN_END)
				.setScoreWallColision(FITNESS_WALL_COLISION)
				.setScoreStep(FITNESS_STEP)
				.setScoreCleanStep(FITNESS_CLEAN_STEP)
				.setScoreRevisit(FITNESS_REVISIT)
				.setScoreEnd(FITNESS_END);

		robots.forEach((r) -> {
			calculator.setRobot(r);
			double fitness = 0.0;
			for (Maze m : mazes) {
				fitness += calculator
						.setMaze(m)
						.calculate();
			}
			r.setFitness(fitness);
		});
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
		population.sort(comparator);
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
				.setAddState(getTemporalValue(M_ADD_STATE_START, M_ADD_STATE_END))
				.setDelState(getTemporalValue(M_DEL_STATE_START, M_DEL_STATE_END))
				.setChangeState(getTemporalValue(M_CHANGE_STATE_START, M_CHANGE_STATE_END))
				.setChangeOutput(getTemporalValue(M_CHANGE_OUTPUT_START, M_CHANGE_OUTPUT_END));
		children.forEach((r) -> mutator.mutate(r));
	}

	@Override
	protected void selectSurvivors() {
		population.sort(comparator);
		SurvivorSelector selector = new SurvivorSelector()
				.setParents(parents)
				.setChildren(children);
		population = selector.select();
		population.sort(comparator);
	}

	public static void main(String[] args) {
		File dir = new File("output" + File.separator + "run_" + START_TIME);
		dir.mkdirs();
		File f = new File(dir, String.format("definitions.txt"));
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

		getInstance().run();
	}
}

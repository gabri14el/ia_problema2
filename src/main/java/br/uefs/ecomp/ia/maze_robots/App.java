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
	public static final int SC_MAX_GENERATION = 1000; // Número máximo de gerações para parar o algoritmo

	// Limitações
	public static final int STATE_MIN = 1;
	public static final int STATE_MAX = 15;
	public static final int STATE_INITIAL = 3;

	// População
	public static final int POPULATION_SIZE = 100; // Tamanho da população

	// Seleção de Pais
	enum ParentSelection {
		PROPORCIONAL, PROPORCIONAL_RANDOM, TORNEIO, BI_CLASSISTA, ELITISTA
	};

	public static final ParentSelection PS_MODE = ParentSelection.ELITISTA;
	public static final int PS_PERCENTAGEM_TO_SELECT = (PS_MODE == ParentSelection.PROPORCIONAL || PS_MODE == ParentSelection.PROPORCIONAL) ? 100 : 50; // porcentagem
	public static final int PS_MPercentage = 30;
	public static final int PS_PPercentage = 20;

	// Seleção de Sobreviventes
	public static final boolean KILL_PARENTS = true;

	// Fitness
	public static final double FITNESS_WALL_COLISION = -25.0; // Pontuação em caso de colisão com paredes
	public static final double FITNESS_STEP = -5.0; // Pontuação para cada passo dado
	public static final double FITNESS_CLEAN_STEP = 10.0; // Pontuação para cada passo dado em um local disponível
	public static final double FITNESS_REVISIT = -100.0; // Pontuação para cada passo dado em um local disponível
	public static final double FITNESS_END = 100.0; // Pontuação por chegar ao fim (e manter-se lá, caso não interrompa a execução)

	// Mutação
	public static final double M_CHANGE_STATE_START = 50; // porcentagem
	public static final double M_CHANGE_STATE_END = 5; // porcentagem
	public static final double M_CHANGE_OUTPUT_START = 50; // porcentagem
	public static final double M_CHANGE_OUTPUT_END = 5; // porcentagem
	public static final double M_ADD_STATE_START = 0; // porcentagem
	public static final double M_ADD_STATE_END = 0; // porcentagem
	public static final double M_DEL_STATE_START = 0; // porcentagem
	public static final double M_DEL_STATE_END = 0; // porcentagem

	// Outros Parâmetros
	public static final long START_TIME = System.currentTimeMillis();
	public static final long[] RANDOM_SEEDS = new long[] { // Conjunto de sementes para gerar números aleatórios
			135827968109L, 208248857186L, 432099974000L, 863278201449L, 461431272318L, 666015161980L, 586981007620L, 877453781828L, 574598151547L, 218042335334L, 435229484920L, 236406828574L,
			363310412856L, 337560821399L, 918214207238L, 654497046710L, 923238918586L, 388953847145L, 823029413652L, 861453743932L
	};
	public static final long RANDOM_SEED = RANDOM_SEEDS[0]; // Semente para gerar números aleatórios usada atualmente
	private Random random = new Random(RANDOM_SEED);

	private static List<Maze> mazes = Arrays.asList(Maze.get(6));

	public static final Comparator<? super Robot> comparator = (r1, r2) -> {
		Double fitness1 = (r1.getFitness() != null) ? r1.getFitness() : Double.MIN_VALUE;
		Double fitness2 = (r2.getFitness() != null) ? r2.getFitness() : Double.MIN_VALUE;
		return fitness2.compareTo(fitness1);
	};

	/**
	 * Retorna um valor decimal que indica a porcentagem de conclusão do processo
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
	protected void selectParents() {
		population.sort(comparator);
		ParentSelector selector = new ParentSelector()
				.setRandom(random)
				.setPopulation(population)
				.setMode(PS_MODE)
				.setPercentagemToSelect(PS_PERCENTAGEM_TO_SELECT)
				.setMPercentage(PS_MPercentage)
				.setPPercentage(PS_PPercentage);
		parents = selector.select();
	}

	@Override
	protected void recombine() {
		parents.sort(comparator);
		if (PS_MODE == ParentSelection.PROPORCIONAL || PS_MODE == ParentSelection.PROPORCIONAL_RANDOM) {
			new FitnessCalculator()
					.setRobots(parents)
					.normalize();
		}

		Recombinator recombinator = new Recombinator()
				.setRandom(random)
				.setPopulationSize(POPULATION_SIZE)
				.setParents(parents)
				.setPSMode(PS_MODE);
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
				.setChangeOutput(getTemporalValue(M_CHANGE_OUTPUT_START, M_CHANGE_OUTPUT_END))
				.setRobots(children);
		mutator.mutate();
	}

	@Override
	protected void selectSurvivors() {
		SurvivorSelector selector = new SurvivorSelector()
				.setPopulationSize(POPULATION_SIZE)
				.setParents(parents)
				.setChildren(children)
				.setPSMode(PS_MODE)
				.setKillParents(KILL_PARENTS);
		population = selector.select();
		population.sort(comparator);
	}

	@Override
	protected void calculateFitness(List<Robot> robots) {
		FitnessCalculator calculator = new FitnessCalculator()
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
		return SC_MAX_GENERATION < generation;
	}

	public static void main(String[] args) {
		File dir = new File("output" + File.separator + "run_" + START_TIME);
		dir.mkdirs();
		File f = new File(dir, String.format("definitions.txt"));
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
			for (Field field : App.class.getDeclaredFields()) {
				field.setAccessible(true);
				if (Modifier.isStatic(field.getModifiers())) {
					Object v = field.get(null);
					if (List.class.isInstance(v))
						out.println(field.getName() + ": " + Arrays.toString(((List<?>) v).toArray()));
					else
						out.println(field.getName() + ": " + v);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		getInstance().run();
	}
}

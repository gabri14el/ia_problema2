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
	public static int MAX_GENERATION = 2000; // Número máximo de gerações para parar o algoritmo

	// Limitações
	public static int STATE_MIN = 1;
	public static int STATE_MAX = 5;

	// População
	public static int POPULATION_SIZE = 200; // Tamanho da população

	// Seleção de Pais
	enum ParentSelection {
		PROPORCIONAL, TORNEIO, BI_CLASSISTA, ELITISTA, PROPORCIONAL_RANDOM
	};

	public static ParentSelection PS_MODE = ParentSelection.PROPORCIONAL;
	public static int PS_PERCENTAGEM_TO_SELECT = (PS_MODE == ParentSelection.PROPORCIONAL || PS_MODE == ParentSelection.PROPORCIONAL) ? 100 : 50; // porcentagem
	public static int PS_MPercentage = 70;
	public static int PS_PPercentage = 30;

	// Seleção de Sobreviventes
	public static boolean PS_KILL_PARENTS = false;

	// Fitness
	public static double FITNESS_WALL_COLISION = -25.0; // Pontuação em caso de colisão com paredes
	public static double FITNESS_STEP = -5.0; // Pontuação para cada passo dado
	public static double FITNESS_CLEAN_STEP = 10.0; // Pontuação para cada passo dado em um local disponível
	public static double FITNESS_REVISIT = -100.0; // Pontuação para cada passo dado em um local disponível
	public static double FITNESS_END = 100.0; // Pontuação por chegar ao fim (e manter-se lá, caso não interrompa a execução)

	// Mutação
	public static double M_CHANGE_STATE_START = 50; // porcentagem
	public static double M_CHANGE_STATE_END = 5; // porcentagem
	public static double M_CHANGE_OUTPUT_START = 50; // porcentagem
	public static double M_CHANGE_OUTPUT_END = 5; // porcentagem
	public static double M_ADD_STATE_START = 15; // porcentagem
	public static double M_ADD_STATE_END = 0; // porcentagem
	public static double M_DEL_STATE_START = 0; // porcentagem
	public static double M_DEL_STATE_END = 30; // porcentagem
	public static double GENERATION_TO_END = 70; // porcentagem

	// Outros Parâmetros
	public static long START_TIME = System.currentTimeMillis();

	// Conjunto de sementes para gerar números aleatórios
	public static long[] RANDOM_SEEDS = new long[] { 135827968109L, 208248857186L, 432099974000L, 863278201449L, 461431272318L, 666015161980L, 586981007620L, 877453781828L };
	public static long RANDOM_SEED = RANDOM_SEEDS[1]; // Semente para gerar números aleatórios usada atualmente
	private Random random = new Random(RANDOM_SEED);

	private static List<Maze> MAZES = Arrays.asList(Maze.getGroup(4));

	public static Comparator<? super Robot> comparator = (r1, r2) -> {
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
		return ((generation * (double) 100) / (MAX_GENERATION * (GENERATION_TO_END / 100))) / 100;
	}

	private double getTemporalValue(double start, double end) {
		return ((start * (1 - getPercentageCompleted())) + (end * getPercentageCompleted()));
	}

	@Override
	protected void createStartPopulation() {
		PopulationGenerator generator = new PopulationGenerator()
				.setRandom(random)
				.setSize(POPULATION_SIZE)
				.setMinStates(STATE_MIN)
				.setMaxStates(STATE_MAX);
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
				.setRandom(random)
				.setParents(parents)
				.setChildren(children)
				.setPSMode(PS_MODE)
				.setKillParents(PS_KILL_PARENTS);
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
			for (Maze m : MAZES) {
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
		return MAX_GENERATION < generation;
	}

	public static void main(String[] args) {
		// Execução 1
		initParams();
		RANDOM_SEED = RANDOM_SEEDS[1];
		MAZES = Arrays.asList(Maze.getGroup(4));
		runAllPS();

		// Execução 2
		initParams();
		RANDOM_SEED = RANDOM_SEEDS[2];
		MAZES = Arrays.asList(Maze.getGroup(3));
		PS_PERCENTAGEM_TO_SELECT = 50;
		PS_MPercentage = 70;
		PS_PPercentage = 30;
		runPS(ParentSelection.BI_CLASSISTA);
		PS_MPercentage = 30;
		PS_PPercentage = 70;
		runPS(ParentSelection.BI_CLASSISTA);
		PS_MPercentage = 30;
		PS_PPercentage = 20;
		runPS(ParentSelection.BI_CLASSISTA);
		PS_MPercentage = 20;
		PS_PPercentage = 10;
		runPS(ParentSelection.BI_CLASSISTA);
		PS_PERCENTAGEM_TO_SELECT = 100;
		PS_MPercentage = 70;
		PS_PPercentage = 30;
		runPS(ParentSelection.BI_CLASSISTA);
		PS_MPercentage = 30;
		PS_PPercentage = 70;
		runPS(ParentSelection.BI_CLASSISTA);

		// Execução 3
		initParams();
		RANDOM_SEED = RANDOM_SEEDS[3];
		PS_PERCENTAGEM_TO_SELECT = 10;
		runPS(ParentSelection.ELITISTA);
		PS_PERCENTAGEM_TO_SELECT = 70;
		runPS(ParentSelection.ELITISTA);

		// Execução 4
		initParams();
		RANDOM_SEED = RANDOM_SEEDS[4];
		MAZES = Arrays.asList(Maze.getGroup(2));
		runAllPS();
		PS_KILL_PARENTS = true;
		runAllPS();

		// Execução 5
		initParams();
		RANDOM_SEED = RANDOM_SEEDS[5];
		MAZES = Arrays.asList(Maze.getGroup(0));
		POPULATION_SIZE = 100;
		runAllPS();
		POPULATION_SIZE = 50;
		runAllPS();

		// Execução 6
		initParams();
		RANDOM_SEED = RANDOM_SEEDS[6];
		MAZES = Arrays.asList(Maze.getGroup(1));
		M_CHANGE_STATE_START = 10;
		M_CHANGE_STATE_END = 1;
		M_CHANGE_OUTPUT_START = 10;
		M_CHANGE_OUTPUT_END = 1;
		runAllPS();
		M_CHANGE_STATE_START = 100;
		M_CHANGE_STATE_END = 30;
		M_CHANGE_OUTPUT_START = 100;
		M_CHANGE_OUTPUT_END = 30;
		runAllPS();

		// Execução 7
		initParams();
		RANDOM_SEED = RANDOM_SEEDS[7];
		MAZES = Arrays.asList(Maze.getGroup(1));
		M_ADD_STATE_START = 5;
		M_ADD_STATE_END = 0;
		M_DEL_STATE_START = 0;
		M_DEL_STATE_END = 5;
		runAllPS();
		M_ADD_STATE_START = 80;
		M_ADD_STATE_END = 20;
		M_DEL_STATE_START = 20;
		M_DEL_STATE_END = 80;
		runAllPS();
	}

	private static void initParams() {
		MAX_GENERATION = 2000;
		POPULATION_SIZE = 200;
		RANDOM_SEED = RANDOM_SEEDS[0];
		MAZES = Arrays.asList(Maze.getGroup(0));

		PS_PERCENTAGEM_TO_SELECT = 50;
		PS_MPercentage = 70;
		PS_PPercentage = 30;
		PS_KILL_PARENTS = false;

		M_CHANGE_STATE_START = 50;
		M_CHANGE_STATE_END = 5;
		M_CHANGE_OUTPUT_START = 50;
		M_CHANGE_OUTPUT_END = 5;
		M_ADD_STATE_START = 15;
		M_ADD_STATE_END = 0;
		M_DEL_STATE_START = 0;
		M_DEL_STATE_END = 30;
	}

	private static void runAllPS() {
		runPS(ParentSelection.PROPORCIONAL);
		runPS(ParentSelection.TORNEIO);
		runPS(ParentSelection.BI_CLASSISTA);
		runPS(ParentSelection.ELITISTA);
	}

	private static void runPS(ParentSelection parentSelection) {
		PS_MODE = parentSelection;
		START_TIME = System.currentTimeMillis();
		App.instance = new App();
		App.logParameters();
		App.getInstance().run();
	}

	private static void logParameters() {
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
	}
}

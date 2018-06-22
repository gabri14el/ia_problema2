package br.uefs.ecomp.ia.maze_robots.view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import br.uefs.ecomp.ia.maze_robots.App;
import br.uefs.ecomp.ia.maze_robots.FitnessCalculator;
import br.uefs.ecomp.ia.maze_robots.Maze;
import br.uefs.ecomp.ia.maze_robots.Robot;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MazeView extends Application {

	private Robot robot;
	private Maze maze;

	public MazeView() {
		maze = Maze.get(5);
		robot = loadRobot();
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	private Node createRobot(String color) {
		Circle c = new Circle(15);
		c.setStyle("-fx-fill: " + color + ";");
		return c;
	}

	private Node createMazePane(String color) {
		Pane pane = new Pane();
		pane.setPrefWidth(30);
		pane.setPrefHeight(30);
		pane.setMinWidth(30);
		pane.setMinHeight(30);
		pane.setMaxWidth(30);
		pane.setMaxHeight(30);
		pane.setStyle("-fx-background-color: " + color + ";");
		return pane;
	}

	private Node createMazeView(FitnessCalculator.Step step) {
		GridPane m = new GridPane();
		for (int y = 0; y < maze.getYLength(); y++) {
			for (int x = 0; x < maze.getXLength(); x++) {

				if (y == step.ry && x == step.rx)
					m.add(createRobot("red"), x, y);
				else if (maze.isWall(y, x))
					m.add(createMazePane("black"), x, y);
				else if (maze.isStart(y, x))
					m.add(createMazePane("blue"), x, y);
				else if (maze.isEnd(y, x))
					m.add(createMazePane("green"), x, y);
				else
					m.add(createMazePane("white"), x, y);
			}
		}
		return m;
	}

	private Pane createStatusPane() {
		Pane pane = new Pane();
		pane.setPrefWidth(30);
		pane.setPrefHeight(30);
		pane.setMinWidth(30);
		pane.setMinHeight(30);
		pane.setMaxWidth(30);
		pane.setMaxHeight(30);
		return pane;
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setOnCloseRequest((we) -> System.exit(0));

		BorderPane contentPane = new BorderPane();
		currentStepProperty().addListener((obs, o, n) -> contentPane.setCenter(createMazeView(n)));

		FitnessCalculator calculator = new FitnessCalculator()
				.setMaxSteps(App.SC_MAX_STEPS)
				.setStopInEnd(App.SC_STOP_IN_END)
				.setScoreWallColision(App.FITNESS_WALL_COLISION)
				.setScoreStep(App.FITNESS_STEP)
				.setScoreEnd(App.FITNESS_END)
				.setMaze(maze)
				.setRobot(robot);
		robot.setFitness(calculator.calculate());
		List<FitnessCalculator.Step> steps = calculator.getLastMazeSteps();

		Button firstButton = new Button("First");
		firstButton.setOnAction((ae) -> {
			if (!steps.isEmpty())
				setStep(steps.get(0));
		});
		Button nextButton = new Button("Next");
		nextButton.setOnAction((ae) -> {
			if (steps.indexOf(getStep()) < (steps.size() - 1))
				setStep(steps.get(steps.indexOf(getStep()) + 1));
		});
		Button previousButton = new Button("Previous");
		previousButton.setOnAction((ae) -> {
			if (steps.indexOf(getStep()) > 0)
				setStep(steps.get(steps.indexOf(getStep()) - 1));
		});
		Button lastButton = new Button("Last");
		lastButton.setOnAction((ae) -> {
			if (!steps.isEmpty())
				setStep(steps.get(steps.size() - 1));
		});
		HBox buttonsPane = new HBox(5, firstButton, nextButton, previousButton, lastButton);
		buttonsPane.setAlignment(Pos.CENTER);

		HBox sensorsPane = new HBox(5);
		sensorsPane.setAlignment(Pos.CENTER);

		// Wall Sensor
		GridPane grid = new GridPane();
		grid.setHgap(1);
		grid.setVgap(1);
		sensorsPane.getChildren().add(grid);

		Pane leftWallSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> leftWallSensor.setStyle("-fx-background-color: " + ((robot.getLeftWallSensor(maze, n.ry, n.rx) == 1) ? "red" : "green") + ";"));
		grid.add(leftWallSensor, 0, 1);

		Pane upWallSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> upWallSensor.setStyle("-fx-background-color: " + ((robot.getUpWallSensor(maze, n.ry, n.rx) == 1) ? "red" : "green") + ";"));
		grid.add(upWallSensor, 1, 0);

		grid.add(createRobot("black"), 1, 1);

		Pane rightWallSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> rightWallSensor.setStyle("-fx-background-color: " + ((robot.getRightWallSensor(maze, n.ry, n.rx) == 1) ? "red" : "green") + ";"));
		grid.add(rightWallSensor, 2, 1);

		Pane downWallSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> downWallSensor.setStyle("-fx-background-color: " + ((robot.getDownWallSensor(maze, n.ry, n.rx) == 1) ? "red" : "green") + ";"));
		grid.add(downWallSensor, 1, 2);

		// End Sensor
		grid = new GridPane();
		grid.setHgap(1);
		grid.setVgap(1);
		sensorsPane.getChildren().add(grid);

		Pane leftEndSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> leftEndSensor.setStyle("-fx-background-color: " + ((robot.getLeftEndSensor(maze, n.ry, n.rx) == 1) ? "green" : "red") + ";"));
		grid.add(leftEndSensor, 0, 1);

		Pane upEndSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> upEndSensor.setStyle("-fx-background-color: " + ((robot.getUpEndSensor(maze, n.ry, n.rx) == 1) ? "green" : "red") + ";"));
		grid.add(upEndSensor, 1, 0);

		grid.add(createRobot("black"), 1, 1);

		Pane rightEndSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> rightEndSensor.setStyle("-fx-background-color: " + ((robot.getRightEndSensor(maze, n.ry, n.rx) == 1) ? "green" : "red") + ";"));
		grid.add(rightEndSensor, 2, 1);

		Pane downEndSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> downEndSensor.setStyle("-fx-background-color: " + ((robot.getDownEndSensor(maze, n.ry, n.rx) == 1) ? "green" : "red") + ";"));
		grid.add(downEndSensor, 1, 2);

		// Output
		grid = new GridPane();
		grid.setHgap(1);
		grid.setVgap(1);
		HBox outputPane = new HBox(grid);
		outputPane.setAlignment(Pos.CENTER);

		Pane leftOutputSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> leftOutputSensor.setStyle("-fx-background-color: " + ((n.output == 1) ? "green" : "red") + ";"));
		grid.add(leftOutputSensor, 0, 1);

		Pane upOutputSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> upOutputSensor.setStyle("-fx-background-color: " + ((n.output == 2) ? "green" : "red") + ";"));
		grid.add(upOutputSensor, 1, 0);

		grid.add(createRobot("black"), 1, 1);

		Pane rightOutputSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> rightOutputSensor.setStyle("-fx-background-color: " + ((n.output == 3) ? "green" : "red") + ";"));
		grid.add(rightOutputSensor, 2, 1);

		Pane downOutputSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> downOutputSensor.setStyle("-fx-background-color: " + ((n.output == 4) ? "green" : "red") + ";"));
		grid.add(downOutputSensor, 1, 2);

		VBox pane = new VBox(10, buttonsPane, contentPane, sensorsPane, outputPane);
		pane.setAlignment(Pos.TOP_CENTER);
		stage.setScene(new Scene(pane, (maze.getXLength() * 30), (maze.getYLength() * 30) + 230));
		stage.setResizable(false);

		setStep(steps.get(0));
		stage.show();
	}

	private ObjectProperty<FitnessCalculator.Step> currentStep;

	private ObjectProperty<FitnessCalculator.Step> currentStepProperty() {
		if (currentStep == null)
			currentStep = new SimpleObjectProperty<>(this, "currentStep", null);
		return currentStep;
	}

	private void setStep(FitnessCalculator.Step value) {
		currentStepProperty().set(value);
	}

	private FitnessCalculator.Step getStep() {
		return currentStepProperty().get();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private Robot loadRobot() {
		try (BufferedReader reader = new BufferedReader(new FileReader("view_input.txt"))) {
			String n = reader.readLine();
			n = n.replaceAll("\\D", " ");
			n = n.trim();
			n = n.replaceAll("\\s+", ";");
			List<String> numbers = Arrays.asList(n.split(";"));
			int inputSize = Robot.INPUT_SIZE;
			int stateSize = (numbers.size() / inputSize) / 2;

			Integer[][][] value = new Integer[stateSize][inputSize][2];
			for (int v = 0, s = 0; s < stateSize; s++) {
				for (int i = 0; i < inputSize; i++) {
					value[s][i][0] = new Integer(numbers.get(v++));
					value[s][i][1] = new Integer(numbers.get(v++));
				}
			}

			return new Robot(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

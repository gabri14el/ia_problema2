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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MazeView extends Application {

	private Robot robot;
	private Maze maze;

	public MazeView() {
		maze = Maze.getGroup(0)[0];
		robot = loadRobot();
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	private Node createRobot(String color, String backgroundColor) {
		Circle c = new Circle(10);
		c.setStyle("-fx-fill: " + color + ";");
		VBox pane = new VBox(c);
		pane.setAlignment(Pos.CENTER);
		pane.setStyle("-fx-background-color: " + backgroundColor + ";");

		return pane;
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

	private Node createMazeView(List<FitnessCalculator.Step> steps) {
		GridPane m = new GridPane();
		FitnessCalculator.Step lastStep = steps.get(steps.size() - 1);
		TilePane stepPane;
		StackPane pane;
		FitnessCalculator.Step step;
		Label numberLabel;

		for (int y = 0; y < maze.getYLength(); y++) {
			for (int x = 0; x < maze.getXLength(); x++) {
				pane = createStackPane();
				stepPane = new TilePane(2.5, 2.5);
				stepPane.setAlignment(Pos.CENTER);

				if (y == lastStep.ry && x == lastStep.rx) {
					if (maze.isStart(y, x))
						pane.getChildren().add(createRobot("red", "blue"));
					else if (maze.isEnd(y, x))
						pane.getChildren().add(createRobot("red", "#11FF11"));
					else
						pane.getChildren().add(createRobot("red", "white"));
				} else if (maze.isWall(y, x))
					pane.getChildren().add(createMazePane("black"));
				else if (maze.isStart(y, x))
					pane.getChildren().add(createMazePane("blue"));
				else if (maze.isEnd(y, x))
					pane.getChildren().add(createMazePane("#11FF11"));
				else
					pane.getChildren().add(createMazePane("white"));

				for (int s = 0; s < steps.size() - 1; s++) {
					step = steps.get(s);
					if (step.ry == y && step.rx == x && !(y == lastStep.ry && x == lastStep.rx)) {
						stepPane.setStyle("-fx-border-color: red;");
						numberLabel = new Label("" + s);
						numberLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: red; -fx-font-weight: bold;");
						stepPane.getChildren().add(numberLabel);
					}
				}
				pane.getChildren().add(stepPane);

				m.add(pane, x, y);
			}
		}

		return m;
	}

	private StackPane createStackPane() {
		StackPane pane = new StackPane();
		pane.setAlignment(Pos.CENTER);
		pane.setPrefWidth(30);
		pane.setPrefHeight(30);
		pane.setMinWidth(30);
		pane.setMinHeight(30);
		pane.setMaxWidth(30);
		pane.setMaxHeight(30);
		return pane;
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

		FitnessCalculator calculator = new FitnessCalculator()
				.setScoreWallColision(App.FITNESS_WALL_COLISION)
				.setScoreStep(App.FITNESS_STEP)
				.setScoreCleanStep(App.FITNESS_CLEAN_STEP)
				.setScoreRevisit(App.FITNESS_REVISIT)
				.setScoreEnd(App.FITNESS_END)
				.setMaze(maze)
				.setRobot(robot);
		robot.setFitness(calculator.calculate());
		List<FitnessCalculator.Step> steps = calculator.getLastMazeSteps();

		currentStepProperty().addListener((obs, o, n) -> contentPane.setCenter(createMazeView(steps.subList(0, steps.indexOf(n) + 1))));

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

		VBox dataPane = new VBox(15);
		dataPane.setPadding(new Insets(5));
		dataPane.setAlignment(Pos.CENTER);

		VBox itemPane;

		// Wall Sensor
		GridPane grid = new GridPane();
		grid.setHgap(1);
		grid.setVgap(1);
		itemPane = new VBox(2, grid, new Label("Wall Sensor"));
		itemPane.setAlignment(Pos.CENTER);
		dataPane.getChildren().add(itemPane);

		Pane leftWallSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> leftWallSensor.setStyle("-fx-background-color: " + ((robot.getLeftWallSensor(maze, n.ry, n.rx) == 1) ? "red" : "#11FF11") + ";"));
		grid.add(leftWallSensor, 0, 1);

		Pane upWallSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> upWallSensor.setStyle("-fx-background-color: " + ((robot.getUpWallSensor(maze, n.ry, n.rx) == 1) ? "red" : "#11FF11") + ";"));
		grid.add(upWallSensor, 1, 0);

		Pane rightWallSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> rightWallSensor.setStyle("-fx-background-color: " + ((robot.getRightWallSensor(maze, n.ry, n.rx) == 1) ? "red" : "#11FF11") + ";"));
		grid.add(rightWallSensor, 2, 1);

		Pane downWallSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> downWallSensor.setStyle("-fx-background-color: " + ((robot.getDownWallSensor(maze, n.ry, n.rx) == 1) ? "red" : "#11FF11") + ";"));
		grid.add(downWallSensor, 1, 2);

		// End Sensor
		grid = new GridPane();
		grid.setHgap(1);
		grid.setVgap(1);
		itemPane = new VBox(2, grid, new Label("End Sensor"));
		itemPane.setAlignment(Pos.CENTER);
		dataPane.getChildren().add(itemPane);

		Pane leftEndSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> leftEndSensor.setStyle("-fx-background-color: " + ((robot.getLeftEndSensor(maze, n.ry, n.rx) == 1) ? "#11FF11" : "red") + ";"));
		grid.add(leftEndSensor, 0, 1);

		Pane upEndSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> upEndSensor.setStyle("-fx-background-color: " + ((robot.getUpEndSensor(maze, n.ry, n.rx) == 1) ? "#11FF11" : "red") + ";"));
		grid.add(upEndSensor, 1, 0);

		Pane rightEndSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> rightEndSensor.setStyle("-fx-background-color: " + ((robot.getRightEndSensor(maze, n.ry, n.rx) == 1) ? "#11FF11" : "red") + ";"));
		grid.add(rightEndSensor, 2, 1);

		Pane downEndSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> downEndSensor.setStyle("-fx-background-color: " + ((robot.getDownEndSensor(maze, n.ry, n.rx) == 1) ? "#11FF11" : "red") + ";"));
		grid.add(downEndSensor, 1, 2);

		// Output
		grid = new GridPane();
		grid.setHgap(1);
		grid.setVgap(1);
		itemPane = new VBox(2, grid, new Label("Next Output"));
		itemPane.setAlignment(Pos.CENTER);
		dataPane.getChildren().add(itemPane);

		Pane leftOutputSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> leftOutputSensor.setStyle("-fx-background-color: " + ((n.output == 1) ? "#117711" : "#771111") + ";"));
		grid.add(leftOutputSensor, 0, 1);

		Pane upOutputSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> upOutputSensor.setStyle("-fx-background-color: " + ((n.output == 2) ? "#117711" : "#771111") + ";"));
		grid.add(upOutputSensor, 1, 0);

		Pane rightOutputSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> rightOutputSensor.setStyle("-fx-background-color: " + ((n.output == 3) ? "#117711" : "#771111") + ";"));
		grid.add(rightOutputSensor, 2, 1);

		Pane downOutputSensor = createStatusPane();
		currentStepProperty().addListener((obs, o, n) -> downOutputSensor.setStyle("-fx-background-color: " + ((n.output == 4) ? "#117711" : "#771111") + ";"));
		grid.add(downOutputSensor, 1, 2);

		// States
		itemPane = new VBox();
		Label currenttsateLabel = new Label("0");
		currenttsateLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
		Label nextStateLabel = new Label("0");
		nextStateLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
		Label maxStatesLabel = new Label("" + robot.getStateSize());
		maxStatesLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
		Label fitnessLabel = new Label("0");
		fitnessLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
		Label countStepsLabel = new Label("0");
		countStepsLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
		currentStepProperty().addListener((obs, o, n) -> {
			currenttsateLabel.setText(nextStateLabel.getText());
			nextStateLabel.setText("" + n.state);
			fitnessLabel.setText(String.format("%d", ((int) n.fitness)));
			countStepsLabel.setText(String.format("%d", (n.count)));
		});
		itemPane.getChildren().addAll(currenttsateLabel, new Label("Current State"), nextStateLabel, new Label("Next State"), maxStatesLabel, new Label("Max States"), fitnessLabel,
				new Label("Fitness"), countStepsLabel, new Label("Steps"));
		itemPane.setAlignment(Pos.CENTER);

		HBox pane1 = new HBox(10, contentPane, dataPane, itemPane);
		VBox pane = new VBox(10, pane1, buttonsPane);
		pane.setPadding(new Insets(10));
		pane.setAlignment(Pos.TOP_CENTER);
		pane.setOnKeyPressed((ke) -> {
			try {
				if (KeyCode.UP == ke.getCode()) {
					setStep(steps.get(0));
					ke.consume();
				} else if (KeyCode.DOWN == ke.getCode()) {
					setStep(steps.get(steps.size() - 1));
					ke.consume();
				} else if (KeyCode.LEFT == ke.getCode()) {
					setStep(steps.get(steps.indexOf(getStep()) - 1));
					ke.consume();
				} else if (KeyCode.RIGHT == ke.getCode()) {
					setStep(steps.get(steps.indexOf(getStep()) + 1));
					ke.consume();
				} else if (KeyCode.ESCAPE == ke.getCode()) {
					System.exit(0);
				}
			} catch (IndexOutOfBoundsException e) {}
		});
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(new Scene(pane, (maze.getXLength() * 28) + 270, (maze.getYLength() * 9) + 400));
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

			String[][] value = new String[stateSize][inputSize];
			for (int v = 0, s = 0; s < stateSize; s++)
				for (int i = 0; i < inputSize; i++)
					value[s][i] = new String(numbers.get(v++) + ":" + numbers.get(v++));

			return new Robot(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

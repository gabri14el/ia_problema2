package br.uefs.ecomp.ia.maze_robots.view;

import java.util.List;
import br.uefs.ecomp.ia.maze_robots.App;
import br.uefs.ecomp.ia.maze_robots.FitnessCalculator;
import br.uefs.ecomp.ia.maze_robots.Maze;
import br.uefs.ecomp.ia.maze_robots.Robot;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MazeView extends Application {

	private Robot robot;
	private Maze maze;

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	private String getText(FitnessCalculator.Step step) {
		String result = "";
		for (int y = 0; y < maze.getYLength(); y++) {
			for (int x = 0; x < maze.getXLength(); x++) {
				if (y == step.ry && x == step.rx)
					result += 'O';
				else if (!maze.isWall(y, x))
					result += maze.get(y, x) + " ";
				else
					result += maze.get(y, x);
			}
			result += "\n";
		}
		return result;
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setOnCloseRequest((we) -> System.exit(0));
		FitnessCalculator calculator = new FitnessCalculator()
				.setMaxSteps(App.SC_MAX_STEPS)
				.setStopInEnd(App.SC_STOP_IN_END)
				.setScoreWallColision(App.FITNESS_WALL_COLISION)
				.setScoreStep(App.FITNESS_STEP)
				.setScoreEnd(App.FITNESS_END)
				.setMaze(maze);
		calculator.calculate(robot);
		List<FitnessCalculator.Step> steps = calculator.getSteps();

		TextArea textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setStyle("fx-text-alignment: center;");
		textArea.setPrefWidth(200);
		textArea.setPrefHeight(250);
		textArea.setPadding(new Insets(0, 100, 0, 100));
		currentStepProperty().addListener((obs, o, n) -> textArea.setText(getText(steps.get(n.intValue()))));
		if (!steps.isEmpty())
			setCurrentStep(0);

		Button firstButton = new Button("First");
		firstButton.setOnAction((ae) -> {
			if (!steps.isEmpty())
				setCurrentStep(0);
		});
		Button nextButton = new Button("Next");
		nextButton.setOnAction((ae) -> {
			if (getCurrentStep() < (steps.size() - 1))
				setCurrentStep(getCurrentStep() + 1);
		});
		Button previousButton = new Button("Previous");
		previousButton.setOnAction((ae) -> {
			if (getCurrentStep() > 0)
				setCurrentStep(getCurrentStep() - 1);
		});
		Button lastButton = new Button("Last");
		lastButton.setOnAction((ae) -> {
			if (!steps.isEmpty())
				setCurrentStep(steps.size() - 1);
		});
		HBox buttonsPane = new HBox(5, firstButton, nextButton, previousButton, lastButton);
		buttonsPane.setAlignment(Pos.CENTER);

		VBox pane = new VBox(10, textArea, buttonsPane);
		stage.setScene(new Scene(pane, 400, 300));
		stage.setResizable(false);
		stage.show();
	}

	private IntegerProperty currentStep;

	private IntegerProperty currentStepProperty() {
		if (currentStep == null)
			currentStep = new SimpleIntegerProperty(this, "currentStep", -1);
		return currentStep;
	}

	private void setCurrentStep(Integer value) {
		currentStepProperty().set(value);
	}

	private Integer getCurrentStep() {
		return currentStepProperty().get();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public MazeView() {
		maze = Maze.get(5);
		robot = new Robot(new Integer[][][] { { { 0, 0 }, { 3, 0 }, { 2, 0 }, { 2, 0 }, { 4, 0 }, { 1, 0 }, { 4, 0 }, { 0, 0 }, { 2, 0 }, { 4, 0 }, { 1, 0 }, { 4, 0 }, { 0, 0 }, { 1, 0 }, { 2, 0 },
				{ 1, 0 }, { 2, 0 }, { 2, 0 }, { 3, 0 }, { 1, 0 }, { 1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 0 }, { 4, 0 }, { 2, 0 }, { 2, 0 }, { 4, 0 }, { 0, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 0, 0 },
				{ 0, 0 }, { 2, 0 }, { 1, 0 }, { 0, 0 }, { 0, 0 }, { 4, 0 }, { 4, 0 }, { 2, 0 }, { 4, 0 }, { 1, 0 }, { 0, 0 }, { 2, 0 }, { 0, 0 }, { 1, 0 }, { 3, 0 }, { 0, 0 }, { 4, 0 }, { 1, 0 },
				{ 3, 0 }, { 0, 0 }, { 4, 0 }, { 2, 0 }, { 4, 0 }, { 0, 0 }, { 4, 0 }, { 3, 0 }, { 2, 0 }, { 3, 0 }, { 2, 0 }, { 4, 0 }, { 0, 0 }, { 1, 0 }, { 1, 0 }, { 4, 0 }, { 0, 0 }, { 3, 0 },
				{ 0, 0 }, { 4, 0 }, { 4, 0 }, { 2, 0 }, { 3, 0 }, { 3, 0 }, { 1, 0 }, { 0, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 4, 0 }, { 3, 0 }, { 3, 0 }, { 2, 0 }, { 2, 0 }, { 3, 0 }, { 2, 0 },
				{ 2, 0 }, { 4, 0 }, { 2, 0 }, { 0, 0 }, { 4, 0 }, { 4, 0 }, { 4, 0 }, { 2, 0 }, { 2, 0 }, { 2, 0 }, { 2, 0 }, { 4, 0 }, { 4, 0 }, { 4, 0 }, { 3, 0 }, { 4, 0 }, { 0, 0 }, { 1, 0 },
				{ 2, 0 }, { 1, 0 }, { 3, 0 }, { 4, 0 }, { 0, 0 }, { 1, 0 }, { 4, 0 }, { 3, 0 }, { 2, 0 }, { 4, 0 }, { 2, 0 }, { 0, 0 }, { 4, 0 }, { 0, 0 }, { 2, 0 }, { 0, 0 }, { 0, 0 }, { 3, 0 },
				{ 1, 0 }, { 3, 0 }, { 3, 0 }, { 1, 0 }, { 4, 0 }, { 1, 0 }, { 3, 0 }, { 1, 0 }, { 4, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 4, 0 }, { 2, 0 }, { 3, 0 }, { 1, 0 }, { 1, 0 }, { 4, 0 },
				{ 4, 0 }, { 1, 0 }, { 2, 0 }, { 4, 0 }, { 0, 0 }, { 3, 0 }, { 3, 0 }, { 3, 0 }, { 4, 0 }, { 1, 0 }, { 1, 0 }, { 3, 0 }, { 4, 0 }, { 3, 0 }, { 4, 0 }, { 1, 0 }, { 3, 0 }, { 3, 0 },
				{ 4, 0 }, { 4, 0 }, { 3, 0 }, { 2, 0 }, { 4, 0 }, { 0, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 4, 0 }, { 0, 0 }, { 1, 0 }, { 4, 0 }, { 1, 0 }, { 3, 0 }, { 2, 0 }, { 3, 0 }, { 2, 0 },
				{ 1, 0 }, { 1, 0 }, { 2, 0 }, { 0, 0 }, { 0, 0 }, { 2, 0 }, { 1, 0 }, { 1, 0 }, { 3, 0 }, { 0, 0 }, { 4, 0 }, { 0, 0 }, { 3, 0 }, { 3, 0 }, { 2, 0 }, { 3, 0 }, { 3, 0 }, { 1, 0 },
				{ 4, 0 }, { 3, 0 }, { 0, 0 }, { 2, 0 }, { 1, 0 }, { 3, 0 }, { 2, 0 }, { 2, 0 }, { 2, 0 }, { 4, 0 }, { 0, 0 }, { 3, 0 }, { 3, 0 }, { 1, 0 }, { 1, 0 }, { 4, 0 }, { 4, 0 }, { 2, 0 },
				{ 0, 0 }, { 1, 0 }, { 0, 0 }, { 2, 0 }, { 0, 0 }, { 3, 0 }, { 1, 0 }, { 4, 0 }, { 3, 0 }, { 4, 0 }, { 4, 0 }, { 1, 0 }, { 2, 0 }, { 1, 0 }, { 4, 0 }, { 4, 0 }, { 3, 0 }, { 2, 0 },
				{ 1, 0 }, { 3, 0 }, { 3, 0 }, { 0, 0 }, { 0, 0 }, { 1, 0 }, { 3, 0 }, { 4, 0 }, { 4, 0 }, { 2, 0 }, { 0, 0 }, { 2, 0 }, { 0, 0 }, { 3, 0 }, { 4, 0 }, { 3, 0 }, { 4, 0 }, { 4, 0 },
				{ 4, 0 }, { 0, 0 }, { 0, 0 }, { 1, 0 }, { 3, 0 }, { 1, 0 } } });
	}
}

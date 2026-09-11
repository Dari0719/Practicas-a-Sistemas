package co.edu.poli.allten.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class GameView {
	private final Label statusLabel = new Label();
	private final Button startButton = new Button("Iniciar Partida");
	private final TextField nicknameField = new TextField();
	private final BorderPane homeLayout = new BorderPane();
	private final Scene scene;

	public GameView() {
		Label titleLabel = new Label("Allten");
		titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #111111;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setPadding(new Insets(42, 0, 0, 0));

		Label nicknameLabel = new Label("NICKNAME");
		nicknameLabel.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #333333;");

		nicknameField.setId("nickname-field");
		nicknameField.setPrefHeight(38);
		nicknameField.setPromptText("Númeromaster");
		nicknameField.setStyle("-fx-background-color: white; -fx-border-color: #111111;"
				+ "-fx-border-radius: 22px; -fx-background-radius: 22px;"
				+ "-fx-padding: 0 14px; -fx-font-size: 11px;");

		startButton.setMaxWidth(Double.MAX_VALUE);
		startButton.setId("start-button");
		startButton.setPrefHeight(36);
		startButton.setStyle("-fx-background-color: #111111; -fx-text-fill: white;"
				+ "-fx-font-size: 11px; -fx-font-weight: bold;"
				+ "-fx-background-radius: 22px; -fx-border-radius: 22px;");

		statusLabel.setVisible(false);
		statusLabel.setManaged(false);
		statusLabel.setId("status-label");
		statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #b3261e;");

		VBox form = new VBox(8, nicknameLabel, nicknameField, statusLabel, startButton);
		form.setMaxWidth(430);

		Label footerTitle = new Label("Reto diario · 09 SEP 2026");
		footerTitle.setStyle("-fx-font-size: 8px; -fx-font-weight: bold; -fx-text-fill: #555555;");
		Label footerDescription = new Label("Números al azar · 10 operaciones");
		footerDescription.setStyle("-fx-font-size: 8px; -fx-text-fill: #777777;");
		VBox footer = new VBox(1, footerTitle, footerDescription);
		footer.setAlignment(Pos.CENTER);
		footer.setPadding(new Insets(0, 0, 44, 0));

		homeLayout.setTop(titleBox);
		homeLayout.setCenter(form);
		homeLayout.setBottom(footer);
		homeLayout.setStyle("-fx-background-color: #e9e8e6;");
		BorderPane.setAlignment(form, Pos.CENTER);
		scene = new Scene(homeLayout, 800, 500);
	}

	public Scene createScene() {
		return scene;
	}

	public void setOnStartGame(Runnable action) {
		startButton.setOnAction(event -> action.run());
	}

	public String getNickname() {
		return nicknameField.getText().trim();
	}

	public void showNicknameRequired() {
		statusLabel.setText("Debes escribir un nickname para continuar.");
		statusLabel.setVisible(true);
		statusLabel.setManaged(true);
		nicknameField.requestFocus();
	}

	public void showGameStarted(String nickname) {
		Button backButton = new Button("Volver");
		backButton.setId("back-button");
		backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333;"
				+ "-fx-font-size: 11px; -fx-font-weight: bold;");
		backButton.setOnAction(event -> {
			statusLabel.setVisible(false);
			statusLabel.setManaged(false);
			scene.setRoot(homeLayout);
		});

		Label gameTitle = new Label("Allten");
		gameTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #222222;");
		Label playerLabel = new Label("Jugador: " + nickname);
		playerLabel.setId("player-label");
		playerLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");
		VBox titleContent = new VBox(2, gameTitle, playerLabel);
		titleContent.setAlignment(Pos.CENTER);
		Label scoreLabel = new Label("0 pts");
		scoreLabel.setStyle("-fx-background-color: #f6d319; -fx-background-radius: 12px;"
				+ "-fx-padding: 4px 10px; -fx-font-size: 10px; -fx-font-weight: bold;");

		BorderPane header = new BorderPane();
		header.setLeft(backButton);
		header.setCenter(titleContent);
		header.setRight(scoreLabel);
		header.setPadding(new Insets(8, 18, 8, 22));
		header.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-width: 0 0 1 0;");

		Label operationLabel = new Label("OPERACIÓN 1/10");
		operationLabel.setStyle("-fx-background-color: #dcefd7; -fx-background-radius: 12px;"
				+ "-fx-padding: 5px 10px; -fx-font-size: 9px; -fx-font-weight: bold;");
		Label numbersLabel = new Label("TUS 4 NÚMEROS");
		numbersLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #777777;");
		HBox numbers = new HBox(8,
				createNumberLabel("3", "#f6d319"),
				createNumberLabel("7", "#a9d1e8"),
				createNumberLabel("2", "#78b477"),
				createNumberLabel("9", "white"));
		numbers.setAlignment(Pos.CENTER);
		Label resolveLabel = new Label("RESUELVE");
		resolveLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #777777;");
		Label questionLabel = new Label("12 + 7 = ?");
		questionLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #111111;");
		VBox gameCard = new VBox(12, numbersLabel, numbers, resolveLabel, questionLabel);
		gameCard.setAlignment(Pos.CENTER);
		gameCard.setPadding(new Insets(18));
		gameCard.setStyle("-fx-background-color: white; -fx-border-color: #333333;"
				+ "-fx-border-radius: 8px; -fx-background-radius: 8px;");
		gameCard.setMaxWidth(680);

		TextField answerField = new TextField();
		answerField.setPromptText("Tu respuesta");
		answerField.setPrefHeight(38);
		answerField.setStyle("-fx-background-color: white; -fx-border-color: #333333;"
				+ "-fx-border-radius: 22px; -fx-background-radius: 22px; -fx-padding: 0 14px;");
		Button checkButton = new Button("Comprobar respuesta");
		checkButton.setMaxWidth(Double.MAX_VALUE);
		checkButton.setPrefHeight(36);
		checkButton.setStyle("-fx-background-color: #111111; -fx-text-fill: white;"
				+ "-fx-font-weight: bold; -fx-background-radius: 22px;");
		VBox answerBox = new VBox(8, new Label("TU RESPUESTA"), answerField, checkButton);
		answerBox.setMaxWidth(680);

		VBox gameContent = new VBox(18, operationLabel, gameCard, answerBox);
		gameContent.setAlignment(Pos.TOP_CENTER);
		gameContent.setPadding(new Insets(28, 20, 20, 20));
		gameContent.setStyle("-fx-background-color: #e9e8e6;");

		BorderPane gameLayout = new BorderPane(gameContent);
		gameLayout.setTop(header);
		gameLayout.setStyle("-fx-background-color: #e9e8e6;");
		scene.setRoot(gameLayout);
	}

	private Label createNumberLabel(String value, String color) {
		Label numberLabel = new Label(value);
		numberLabel.setAlignment(Pos.CENTER);
		numberLabel.setPrefSize(42, 42);
		numberLabel.setStyle("-fx-background-color: " + color + "; -fx-border-color: #333333;"
				+ "-fx-border-radius: 3px; -fx-background-radius: 3px; -fx-font-size: 16px;");
		return numberLabel;
	}
}

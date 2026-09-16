package co.edu.poli.allten.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import co.edu.poli.allten.model.GameModel;
import co.edu.poli.allten.view.GameView;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class GameControllerTest {

    @BeforeClass
    public static void startJavaFxToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException exception) {
            latch.countDown();
        }
        assertTrue("JavaFX no pudo inicializarse", latch.await(5, TimeUnit.SECONDS));
    }

    @AfterClass
    public static void stopJavaFxToolkit() {
        Platform.exit();
    }

    @Test
    public void startButtonRequiresNickname() throws Exception {
        runOnJavaFxThread(() -> {
            GameModel model = new GameModel();
            GameView view = new GameView();
            new GameController(model, view);

            Button startButton = (Button) view.createScene().lookup("#start-button");
            Label statusLabel = (Label) view.createScene().lookup("#status-label");

            assertEquals("Iniciar Partida", startButton.getText());
            startButton.fire();

            assertFalse(model.isGameStarted());
            assertTrue(statusLabel.isVisible());
            assertEquals("Debes escribir un nickname para continuar.", statusLabel.getText());
        });
    }

    @Test
    public void startButtonStartsGameWithNickname() throws Exception {
        runOnJavaFxThread(() -> {
            GameModel model = new GameModel();
            GameView view = new GameView();
            new GameController(model, view);

            TextField nicknameField = (TextField) view.createScene().lookup("#nickname-field");
            Button startButton = (Button) view.createScene().lookup("#start-button");
            nicknameField.setText("Jugador1");
            startButton.fire();

            assertTrue(model.isGameStarted());
                assertNull(view.createScene().lookup("#start-button"));
                assertEquals("Jugador: Jugador1", ((Label) view.createScene()
                    .lookup("#player-label")).getText());

                Button backButton = (Button) view.createScene().lookup("#back-button");
                backButton.fire();
                assertTrue(view.createScene().lookup("#start-button") != null);
        });
    }

    @Test
    public void modelStartsSessionWithNickname() {
        GameModel model = new GameModel();

        model.startGame("Jugador1");

        assertTrue(model.isGameStarted());
        assertTrue(model.getSession() != null);
        assertEquals("Jugador1", model.getSession().getPlayer().getNickname());
        assertEquals(10, model.getSession().getRounds().size());
    }

    @Test
    public void keypadButtonUpdatesExpressionField() throws Exception {
        runOnJavaFxThread(() -> {
            GameModel model = new GameModel();
            GameView view = new GameView();
            new GameController(model, view);

            TextField nicknameField = (TextField) view.createScene().lookup("#nickname-field");
            Button startButton = (Button) view.createScene().lookup("#start-button");
            nicknameField.setText("Jugador1");
            startButton.fire();

            Button numberButton = (Button) view.createScene().lookup(".number-key");
            TextField answerField = (TextField) view.createScene().lookup("#answer-field");
            String visibleNumber = numberButton.getText();
            numberButton.fire();

            assertEquals(visibleNumber, answerField.getText());
        });
    }

    @Test
    public void playerCanSelectObjectiveNineBeforeObjectiveOne() {
        GameModel model = new GameModel();
        model.startGame("Jugador1");

        assertTrue(model.selectTarget(9));
        assertEquals(9, model.getCurrentRound().getRoundNumber());

        assertTrue(model.selectTarget(1));
        assertEquals(1, model.getCurrentRound().getRoundNumber());
    }

    private static void runOnJavaFxThread(ThrowingRunnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        assertTrue("La prueba JavaFX no terminó", latch.await(5, TimeUnit.SECONDS));
        if (failure.get() != null) {
            throw new AssertionError(failure.get());
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
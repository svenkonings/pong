package pong.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import pong.GPIO;

public class MainPane extends Application implements GPIO.Listener {
    private static final double X = 4096; // 2^12
    private static final double Y = 2048; // 2^11
    private static final double ASPECT_RATIO = X / Y;
    private static final double BALL_SCALE_X = 50;
    private static final double BALL_SCALE_Y = BALL_SCALE_X / ASPECT_RATIO;
    private static final double PADDLE_SCALE_X = 50;
    private static final double PADDLE_SCALE_Y = 8;

    private StackPane root;
    private Pane pane;
    private Circle ball;
    private Rectangle paddleLeft;
    private Rectangle paddleRight;

    @Override
    public void start(Stage primaryStage) throws Exception {
        pane = new Pane();
        pane.setStyle("-fx-background-color: gray");
        root = new StackPane(pane);
        root.setStyle("-fx-background-color: white");
        root.setAlignment(Pos.CENTER);
        root.widthProperty().addListener(observable -> resize());
        root.heightProperty().addListener(observable -> resize());
        ball = new Circle();
        paddleLeft = new Rectangle();
        paddleRight = new Rectangle();
        ball.setFill(Color.RED);
        paddleLeft.setFill(Color.BLUE);
        paddleRight.setFill(Color.BLUE);
        pane.getChildren().addAll(ball, paddleLeft, paddleRight);
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
        System.out.println("Application Started");
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void resize() {
        double width = root.getWidth();
        double height = root.getHeight();
        if (height >= width / ASPECT_RATIO) {
            pane.setMaxSize(width, width / ASPECT_RATIO);
        } else {
            pane.setMaxSize(height * ASPECT_RATIO, height);
        }
    }

    // GPIO.Listener implementation
    @Override
    public void ballX(int x) {
    }

    @Override
    public void ballY(int y) {
    }

    @Override
    public void paddleLeft(int y) {
    }

    @Override
    public void paddleRight(int y) {
    }
}

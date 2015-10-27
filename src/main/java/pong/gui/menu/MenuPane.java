package pong.gui.menu;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pong.Dimen;
import pong.gui.Paddle;

public class MenuPane extends Application {
    private final static double PADDLE_WIDTH = 20, PADDLE_HEIGHT = 50;
    private final static double SCENE_WIDTH = 1280, SCENE_HEIGHT = 720;
    private Pane pane;
    private Paddle paddle;
    private Button selected;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        paddle = new Paddle(PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFill(Color.BLUE);
        pane = new Pane(paddle);
        primaryStage.setScene(new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT));
        primaryStage.show();
        test();
    }

    // Updates the y position of the paddle according to the value given by the FPGA
    public void paddleY(int fieldY) {
        // Convert from FPGA format to GUI format
        double y = Dimen.convertY(fieldY, pane.getHeight());
        paddle.setY(y);

        for (Node child : pane.getChildrenUnmodifiable()) {
            if (child instanceof Button) {
                Button button = (Button) child;
                if (button.containsY(y)) {
                    button.setFill(Color.RED);
                    selected = button;
                    return;
                } else {
                    button.setFill(Color.BLUE);
                }
            }
        }
        selected = null;
    }

    public void click() {
        if (selected != null) {
            selected.click();
        }
    }

    private void test() {
        Button button = new Button(100, 100, 20, 50) {
            @Override
            public void click() {
                System.out.println(paddle.getY());
            }
        };
        button.setFill(Color.BLUE);
        pane.getChildren().add(button);
        paddleY(400);
        click();
    }
}

package pong.gui.menu;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pong.Field;
import pong.gui.Paddle;

public class MenuPane extends Application {
    private Pane pane;
    private Paddle paddle;
    private Button selected;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        paddle = new Paddle(20, 50);
        paddle.setFill(Color.BLUE);
        pane = new Pane(paddle);
        primaryStage.setScene(new Scene(pane, 1280, 720));
        primaryStage.show();
        test();
    }

    public void paddleY(int fieldY) {
        double y = (fieldY / Field.MAX_Y) * pane.getHeight();
        paddle.setFieldY(y);
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

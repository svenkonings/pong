package pong.gui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pong.Fpga;
import pong.control.BaseController;

import java.awt.*;
import java.util.Arrays;
import java.util.Observable;

/**
 * Created by Lindsay on 02-Nov-15.
 */
public class GuiBase extends Application{
    /* General */
    private static Stage stage;
    // Initialisation values, would be overwritten immediately
    private static double screen_width = 1080, screen_height = 720;
    // Field
    private static double field_height, field_width;
    private static double screen_to_field_width = 36.0 / 40.0;
    private static double field_x, field_y;
    private static Rectangle field;
    // Paddle
    private static double paddle_length, paddle_width;
    private static double paddle_length_to_width = 0.25;
    private static double paddle_x, paddle_y;
    private static Paddle paddle;
    // Calibration
    private static double[] cal = new double[3];
    private static int cal_cnt = 0;
    // Selected button
    private static MenuButton selected;
    private static int sel_cnt = 0, sel_thr = 10;
    // Colors
    private static final Color UNPRESSED = Color.DARKSEAGREEN, PRESSED = Color.SEAGREEN;
    private static final Color FIELD_COLOR = Color.LIGHTGREEN;
    private static final String BG = "white";

    /* MENU1
    "Calibration: hold the object in front of the sensor and click on button 1 on FPGA to confirm"
    Arrows (leftupper, middle, lower): images */
    private static Scene menu1;
    private static Pane pane1;
    private static Text text1;

    /* MENU2
    Btn: Single player
    Btn: Two player */
    private static Scene menu2;
    private static Pane pane2;
    private static MenuButton button_sp, button_mp;
    private static Text sp_text;
    private static Text text2;

    /* MENU3A: 1P
    Btn: Easy
    Btn: Medium
    Btn: Hard
     */
    private static Scene menu3a;
    private static Pane pane3a;
    private static MenuButton button_ez, button_md, button_hd;
    private static Text text3a;

    @Override
    public void start(Stage primaryStage) {
        (new BaseController(this)).start();
        calibrateGui();
        setUpMenu1();
        setUpStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void calibrateGui() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        screen_width = bounds.getWidth();
        screen_height = bounds.getHeight();
        field_width = screen_width * screen_to_field_width;
        field_height = field_width / 2;
        field = new Rectangle(field_width, field_height);
        field_x = (screen_width - field_width) / 2;
        field.setX(field_x);
        field_y = (screen_height - field_height) / 2;
        field.setY(field_y);
        field.setFill(FIELD_COLOR);
        System.out.println("SCR: " + screen_height + ", FIE: " + field_height);
    }

    // Updates calibration values and dependancies
    public void calibrateFpga(double coor) {
        cal[cal_cnt] = coor;
        text1.setText(text1.getText() + "\nValue[" + cal_cnt + "] = " + coor);
        cal_cnt++;
        if (cal_cnt == 3) {
            Fpga.calibrate(cal[0], cal[1], cal[2]);
            paddle_length = field_height * Fpga.PADDLE_LENGTH / Fpga.HEIGHT;
            paddle_width = paddle_length * paddle_length_to_width;
            System.out.println("GUI: " + paddle_length + ", " + paddle_width);
            setUpMenu2();
            free(field);
            stage.setScene(menu2);
        }
    }

    public void setUpStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Swipe ball!");
        stage.setScene(menu1);
//        stage.setFullScreen(true); // Resets when switching scene!
        stage.show();
    }

    public void setUpMenu1() {
        // Nodes
        text1 = new Text("Calibration time! Waiting for calibration values...");
        text1.setFont(new Font("Verdana", 25));
        text1.setX((screen_width - text1.getBoundsInParent().getWidth()) / 2);
        text1.setY(text1.getBoundsInParent().getHeight());
        // Pane and scene
        pane1 = new Pane();
        pane1.setStyle("-fx-background-color: " + BG + ";-fx-padding: 10px;");
        pane1.getChildren().addAll(field, text1);
        menu1 = new Scene(pane1, screen_width, screen_height);
    }

    public void setUpMenu2() {
        // Paddle
        paddle = new Paddle(paddle_width, paddle_length);
        paddle.setFill(Color.BLUE);
        paddle.setX(field_x - paddle_width);
        paddle.setY(field_y);
        // Buttons
        double[][] dimens = createButtons(2);
        button_sp = new MenuButton(dimens[0][0], dimens[0][1], dimens[0][2], dimens[0][3]) {
            @Override
            public void click() {
                System.out.println("SINGLEPLAYER CLICKED");
                free(paddle, field);
                setUpMenu3();
                stage.setScene(menu3a);
            }
        };
        button_sp.addText("Singleplayer");
        sp_text = button_sp.getText();
        button_sp.setFill(UNPRESSED);
        button_mp = new MenuButton(dimens[1][0], dimens[1][1], dimens[1][2], dimens[1][3]) {
            @Override
            public void click() {
                System.out.println("MP CLICKED");
                free(paddle, field);
                setUpMenu3();
                stage.setScene(menu3a);
            }
        };
        button_mp.setFill(UNPRESSED);
        // Coordinate text
        text2 = new Text("Nothing happened yet.");
        text2.setFont(new Font("Verdana", 25));
        text2.setX((screen_width - text2.getBoundsInParent().getWidth()) / 2);
        text2.setY(text2.getBoundsInParent().getHeight());
        // Pane and scene
        pane2 = new Pane();
        pane2.setStyle("-fx-background-color: " + BG + ";-fx-padding: 10px;");
        pane2.getChildren().addAll(paddle, field, button_sp, button_mp, sp_text, text2);
        menu2 = new Scene(pane2, screen_width, screen_height);
    }

    public void setUpMenu3() {
        System.out.println("MENU 3a YAAY");
        // Buttons
        double[][] dimens = createButtons(3);
        button_ez = new MenuButton(dimens[0][0], dimens[0][1], dimens[0][2], dimens[0][3]) {
            @Override
            public void click() {
                System.out.println("EZ CLICKED");
                free(paddle, field);
                pane1.getChildren().addAll(field);
                stage.setScene(menu1);
            }
        };
        button_ez.setFill(UNPRESSED);
        button_md = new MenuButton(dimens[1][0], dimens[1][1], dimens[1][2], dimens[1][3]) {
            @Override
            public void click() {
                System.out.println("MED CLICKED");
                free(paddle, field);
                pane2.getChildren().addAll(field);
                stage.setScene(menu1);
            }
        };
        button_md.setFill(UNPRESSED);
        button_hd = new MenuButton(dimens[2][0], dimens[2][1], dimens[2][2], dimens[2][3]) {
            @Override
            public void click() {
                System.out.println("HARD CLICKED");
                free(paddle, field);
                pane1.getChildren().addAll(field);
                stage.setScene(menu1);
            }
        };
        button_hd.setFill(UNPRESSED);
        System.out.println("YAAY 2");
        // Coordinate text
        text3a = new Text("Nothing happened yet.");
        text3a.setFont(new Font("Verdana", 25));
        text3a.setX((screen_width - text2.getBoundsInParent().getWidth()) / 2);
        text3a.setY(text3a.getBoundsInParent().getHeight());
        System.out.println("YAAY 3");
        // Pane and scene
        pane3a = new Pane();
        pane3a.setStyle("-fx-background-color: " + BG + ";-fx-padding: 10px;");
        System.out.println("YAAY 4");
        pane3a.getChildren().addAll(paddle, field, button_ez, button_md, button_hd, text3a);
        System.out.println("YAAY 5");
        menu3a = new Scene(pane3a, screen_width, screen_height);
        System.out.println("YAAY 6");
    }

    public void updatePaddleY(int fpgaY) {
        double y = Fpga.fpga2guiY(fpgaY, field_height, paddle_length);
        y += field_y;
        paddle.setY(y);
        text2.setText("SEL = " + selected + ", cnt = " + sel_cnt);
        text2.setX((screen_width - text2.getBoundsInParent().getWidth()) / 2);
        if (stage.getScene() != null) {
            System.out.println("PREUHM " + stage.getScene().getRoot());
            ObservableList<Node> nodes = stage.getScene().getRoot().getChildrenUnmodifiable();
            System.out.println("UHM " + nodes);
            for (Node child : nodes) {
                // Loop through all the buttons of this pane
                if (child instanceof MenuButton) {
                    MenuButton mb = (MenuButton) child;
                    // Test if the middle of the paddle is somewhere between the top and the bottom of the menuButton
                    if (mb.containsY(y + paddle.getHeight() / 2)) {
                        // MenuButton selected
                        mb.setFill(PRESSED);
                        if (selected != null && selected == mb) {
                            if (sel_cnt == sel_thr) {
                                // Pressed the button long enough
                                selected = null;
                                sel_cnt = 0;
                                mb.click();
                            } else {
                                // Pressed the button not yet long enough
                                sel_cnt++;
                            }
                        } else {
                            // First time on this button
                            selected = mb;
                            sel_cnt = 1;
                        }
                    } else {
                        if (selected == mb) {
                            // Does not select this button *anymore*
                            selected = null;
                            sel_cnt = 0;
                        } else {
                            // Does not select this button
                            mb.setFill(UNPRESSED);
                        }
                    }
                }
            }
        }
    }

    public Rectangle cloneRect(Rectangle rekt) {
        Rectangle res = new Rectangle(rekt.getX(), rekt.getY(), rekt.getWidth(), rekt.getHeight());
        res.setFill(rekt.getFill());
        return res;
    }

    // Free shared nodes to use in other panes
    public void free(Node... nodes) {
        System.out.print("NODES: ");
        for (Node n : nodes) {
            System.out.print(n + ", ");
        }
        System.out.println();
        Pane[] panes = {pane1, pane2, pane3a};
        for (Pane pane : panes) {
            if (pane != null) {
                System.out.println("\tPRE \t" + pane.getChildren());
                pane.getChildren().removeAll(nodes);
                System.out.println("\tPOST\t" + pane.getChildren());
            }
        }
    }

    public double[][] createButtons(int N) {
        // Each element is [x, y, width, height]
        double[][] res = new double[N][4];
        int margin = 40;
        for (int i = 0; i < N; i++) {
            res[i][3] = (field_height - (N + 1) * margin) / N;
            res[i][2] = field_width * 0.75;
            res[i][0] = field_x + ((field_width - res[i][2]) / 2);
            res[i][1] = field_y+ (i * res[i][3] + (i + 1) * margin);
        }
        return res;
    }

}

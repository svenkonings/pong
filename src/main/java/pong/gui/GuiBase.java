package pong.gui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pong.Fpga;

/**
 * Created by Lindsay on 02-Nov-15.
 */
public class GuiBase extends Application{
    /* General */
    private Stage stage;
    // Initialisation values, would be overwritten immediately
    private double screen_width = 1080, screen_height = 720;
    // Playing field
    private double field_height, field_width;
    private double screen_to_field_width = 38.0 / 40.0;
    // Paddle
    private double paddle_length, paddle_width;
    private double paddle_length_to_width = 0.25;
    private double[] cal = new double[3];
    private int cal_cnt = 0;

    /* MENU1
    "Calibration: hold the object in front of the sensor and click on button 1 on FPGA to confirm"
    Arrows (leftupper, middle, lower): images */
    private Scene menu1;
    private FlowPane pane1;
    private Text text1;
    private Label label1;
    private Button button1;

    /* MENU2
    Btn: Single player
    Btn: Two player */
    private Scene menu2;
    private FlowPane pane2;
    private Button button_sp;
    private Button button_mp;

    @Override
    public void start(Stage primaryStage) {
        calibrateGui();
        setUpMenu1();
        setUpMenu2();
        setUpStage(primaryStage);
        calibrateFpga(1100);
        calibrateFpga(100);
        calibrateFpga(350);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void calibrateGui() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        screen_width = bounds.getWidth();
        screen_height = bounds.getHeight();
        field_width = screen_width * screen_to_field_width;
        field_height = field_width / 2;
        System.out.println("SCR: " + screen_height + ", FIE: " + field_height);
    }

    // Updates calibration values and dependancies
    public void calibrateFpga(double coor) {
        cal[cal_cnt] = coor;
        cal_cnt++;
        if (cal_cnt == 3) {
            Fpga.calibrate(cal[0], cal[1], cal[2]);
            paddle_length = field_height * Fpga.PADDLE_LENGTH / Fpga.HEIGHT;
            paddle_width = paddle_length * paddle_length_to_width;
            System.out.println("GUI: " + paddle_length + ", " + paddle_width);
        } else {
            System.out.println("Calibrate more pls!");
        }
    }

    public void setUpStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Swipe ball!");
        stage.setScene(menu1);
        stage.show();
    }

    public void setUpMenu1() {
        // Nodes
        button1 = new Button("MenuButton of menu 1");
        button1.setOnAction(e -> stage.setScene(menu2));
        text1 = new Text("Hallo dit is menu 1 enzo.");
        label1 = new Label("En dit is een label btw.");
        // Pane and scene
        pane1 = new FlowPane();
        pane1.setStyle("-fx-background-color: tan;-fx-padding: 10px;");
        pane1.getChildren().addAll(button1, text1, label1);
        menu1 = new Scene(pane1, 300, 400);
    }

    public void setUpMenu2() {
        // Nodes
        button_sp = new Button("Singleplayer");
        button_mp = new Button("Multiplayer");
        button_sp.setOnAction(e -> stage.setScene(menu1));
        button_mp.setOnAction(e -> stage.setScene(menu1));
        // Pane and scene
        pane2 = new FlowPane();
        pane2.setStyle("-fx-background-color: red;-fx-padding: 10px;");
        pane2.getChildren().addAll(button_sp, button_mp);
        menu2 = new Scene(pane2, 300, 400);
    }
}

package pong.gui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pong.Fpga;
import pong.control.BaseController;
import pong.gpio.Gpio;

public class GuiBase extends Application implements Gpio.Listener {
    /* General */
    private Stage stage;
    private Scene menu;
    private Pane pane;
    private double screenWidth, screenHeight;
    private Gpio gpio;
    // Field
    private double fieldHeight, fieldWidth;
    private static final double SCREEN_TO_FIELD_WIDTH = 9.0 / 10.0;
    private double fieldX, fieldY;
    private Rectangle field;
    // Paddle
    private double paddleLength, paddleWidth;
    private static final double PADDLE_LENGTH_TO_WIDTH = 0.25;
    private Paddle paddleLeft, paddleRight;
    // Calibration
    private int[] cal = new int[3];
    private int calCnt = 0;
    private int prevCal = 0;
    // Selected button
    private MenuButton selected;
    private int selCnt = 0;
    private static final int SEL_THR = 10;
    // Colors
    private static final Color UNPRESSED = Color.DARKSEAGREEN, PRESSED = Color.SEAGREEN;
    private static final Color FIELD_COLOR = Color.LIGHTGREEN, PADDLE_COLOR = Color.MEDIUMSEAGREEN;
    private static final String BG = "white";

    /* MENU1
    "Calibration: hold the object in front of the sensor and click on button 1 on FPGA to confirm"
    Arrows (leftupper, middle, lower): images */
    private Text text1;
    private Group group1;

    /* MENU2
    Btn: Single player
    Btn: Two player */
    private MenuButton buttonSp, buttonMp;
    private Text spText, text2;
    private Group group2;

    /* MENU3A: 1P
    Btn: Easy
    Btn: Medium
    Btn: Hard
     */
    private MenuButton buttonEz, buttonMd, buttonHd;
    private Text text3a;
    private Group group3a;

    /* MENU3B
    "Calibration: hold the object in front of the sensor and click on button 1 on FPGA to confirm"
    Arrows (rightupper, middle, lower): images
     */
    private Text text3b;
    private Group group3b;

    @Override
    public void init() {
//      gpio = new Gpio(this);
//      gpio.start();
        calibrateGui();
        setUpPane();
        setUpMenu1();
        setUpMenu2();
        setUpMenu3a();
    }

    @Override
    public void start(Stage primaryStage) {
        setUpStage(primaryStage);
        (new BaseController(this)).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void calibrateGui() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        screenWidth = bounds.getWidth();
        screenHeight = bounds.getHeight();
        setUpField();
        System.out.println("SCR: " + screenHeight + ", FIE: " + fieldHeight);
    }

    public void setUpField() {
        fieldWidth = screenWidth * SCREEN_TO_FIELD_WIDTH;
        fieldHeight = fieldWidth / 2;
        field = new Rectangle(fieldWidth, fieldHeight);
        fieldX = (screenWidth - fieldWidth) / 2;
        field.setX(fieldX);
        fieldY = (screenHeight - fieldHeight) / 2;
        field.setY(fieldY);
        field.setFill(FIELD_COLOR);
    }

    public void setUpPane() {
        pane = new Pane();
        pane.setStyle("-fx-background-color: " + BG + ";-fx-padding: 10px;");
        menu = new Scene(pane, screenWidth, screenHeight);
        // Initialize paddles
        paddleLeft = new Paddle();
        paddleRight = new Paddle();
    }

    // Updates calibration values and dependancies
    public void calibrateFpga(int coor) {
        if (coor != prevCal) {
            cal[calCnt] = coor;
            text1.setText(text1.getText() + "\nValue[" + calCnt + "] = " + coor);
            calCnt++;
            if (calCnt == 3) {
                Fpga.calibrate(cal[0], cal[1], cal[2]);
                paddleLength = fieldHeight * Fpga.PADDLE_LENGTH / Fpga.HEIGHT;
                paddleWidth = paddleLength * PADDLE_LENGTH_TO_WIDTH;
                System.out.println("GUI: " + paddleLength + ", " + paddleWidth);
                setUpPaddle(paddleLeft);
                setUpPaddle(paddleRight);
                switchGroup(group2);
            }
            prevCal = coor;
        }
    }

    public Paddle getPaddleLeft() {
        return paddleLeft;
    }

    public Paddle getPaddleRight() {
        return paddleRight;
    }

    public void setUpPaddle(Paddle paddle) {
        paddle.setWidth(paddleWidth);
        paddle.setHeight(paddleLength);
        paddle.setFill(PADDLE_COLOR);
        if (paddle == paddleLeft) {
            paddle.setX(fieldX - paddleWidth);
        } else if (paddle == paddleRight) {
            paddle.setX(fieldX + fieldWidth);
        }
        paddle.setY(fieldY);
    }

    public void setUpStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Swipe ball");
        stage.setScene(menu);
//        stage.setFullScreen(true); // Resets when switching scene!
        stage.show();
    }

    public void setUpMenu1() {
        // Nodes
        text1 = new Text("Calibration time! Waiting for calibration values...");
        text1.setFont(new Font("Verdana", 25));
        text1.setX((screenWidth - text1.getBoundsInParent().getWidth()) / 2);
        text1.setY(text1.getBoundsInParent().getHeight());
        group1 = new Group();
        group1.getChildren().add(text1);
        pane.getChildren().addAll(field, group1);
    }

    public void setUpMenu2() {
        // Buttons
        MenuButton[] mb = createButtons(2, () -> switchGroup(group3a), () -> switchGroup(group1));
        buttonSp = mb[0];
        buttonSp.addText("Singleplayer");
        spText = buttonSp.getText();
        buttonMp = mb[1];
        // Coordinate text
        text2 = new Text("Nothing happened yet.");
        text2.setFont(new Font("Verdana", 25));
        text2.setX((screenWidth - text2.getBoundsInParent().getWidth()) / 2);
        text2.setY(text2.getBoundsInParent().getHeight());
        group2 = new Group();
        group2.getChildren().addAll(buttonSp, buttonMp, spText, text2);
        pane.getChildren().addAll(paddleLeft, paddleRight);
    }

    public void setUpMenu3a() {
        // Buttons
        MenuButton[] mb = createButtons(3, () -> switchGroup(group1), () -> switchGroup(group1), () -> switchGroup(group1));
        buttonEz = mb[0];
        buttonMd = mb[1];
        buttonHd = mb[2];
        // Coordinate text
        text3a = new Text("Nothing happened yet.");
        text3a.setFont(new Font("Verdana", 25));
        text3a.setX((screenWidth - text2.getBoundsInParent().getWidth()) / 2);
        text3a.setY(text3a.getBoundsInParent().getHeight());
        // Group
        group3a = new Group();
        group3a.getChildren().addAll(buttonEz, buttonMd, buttonHd, text3a);
    }

    public void updatePaddleY(int fpgaY, Paddle paddle) {
        double y = Fpga.fpga2guiY(fpgaY, fieldHeight, paddleLength);
        y += fieldY;
        paddle.setY(y);
        text2.setText("SEL = " + selected + ", cnt = " + selCnt);
        text2.setX((screenWidth - text2.getBoundsInParent().getWidth()) / 2);
        // Only the left paddle will control the menu!
        if (stage.getScene() != null && paddle == paddleLeft) {
            ObservableList<Node> nodes = stage.getScene().getRoot().getChildrenUnmodifiable();
            for (Node child : nodes) {
                if (child instanceof Group) {
                    for (Node grandchild : ((Group) child).getChildrenUnmodifiable()) {
                        // Loop through all the buttons of this pane
                        if (grandchild instanceof MenuButton) {
                            MenuButton mb = (MenuButton) grandchild;
                            // Test if the middle of the paddle is somewhere between the top and the bottom of the menuButton
                            if (mb.containsY(y + paddle.getHeight() / 2)) {
                                // MenuButton selected
                                mb.setFill(PRESSED);
                                if (selected != null && selected == mb) {
                                    if (selCnt == SEL_THR) {
                                        // Pressed the button long enough
                                        selected = null;
                                        selCnt = 0;
                                        mb.click();
                                    } else {
                                        // Pressed the button not yet long enough
                                        selCnt++;
                                    }
                                } else {
                                    // First time on this button
                                    selected = mb;
                                    selCnt = 1;
                                }
                            } else {
                                if (selected == mb) {
                                    // Does not select this button *anymore*
                                    selected = null;
                                    selCnt = 0;
                                } else {
                                    // Does not select this button
                                    mb.setFill(UNPRESSED);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static String groupToString(Group group) {
        String s = "Group = [";
        for (Node n : group.getChildren()) {
            s += n.toString() + ", ";
        }
        return s + "]";
    }

    public void switchGroup(Group group) {
        Pane pane = (Pane) stage.getScene().getRoot();
        Node unwantedChild = null;
        for (Node child : pane.getChildren()) {
            if (child instanceof Group) {
                System.out.println("PRE \tUNWANTED = " + groupToString((Group) child));
                unwantedChild = child;
                break;
            } else {
                System.out.println("PRE \tWANTED   = " + child);
            }
        }
        if (unwantedChild != null) {
            pane.getChildren().remove(unwantedChild);
        }
        pane.getChildren().add(group);
        for (Node child : pane.getChildren()) {
            if (child instanceof Group) {
                System.out.println("POST\tGROUP CHILD = " + groupToString((Group) child));
            } else {
                System.out.println("POST\tOTHER CHILD = " + child.toString());
            }
        }
    }

    // Expects an array of at least N elements
    public MenuButton[] createButtons(int N, Runnable... runnables) {
        // Each element is [x, y, width, height]
        MenuButton[] mb = new MenuButton[N];
        int margin = 40;
        for (int i = 0; i < N; i++) {
            double h = (fieldHeight - (N + 1) * margin) / N;
            double w = fieldWidth * 0.75;
            double x = fieldX + ((fieldWidth - w) / 2);
            double y = fieldY + (i * h + (i + 1) * margin);
            mb[i] = new MenuButton(x, y, w, h);
            mb[i].setClick(runnables[i]);
            mb[i].setFill(UNPRESSED);
        }
        return mb;
    }

    public double getFieldHeight() {
        return fieldHeight;
    }

    @Override
    public void paddleLeft(int y) {
        updatePaddleY(y, paddleLeft);
    }

    @Override
    public void goalLeft() {

    }

    @Override
    public void paddleRight(int y) {
        updatePaddleY(y, paddleRight);
    }

    @Override
    public void goalRight() {

    }

    @Override
    public void ballX(int x) {

    }

    @Override
    public void collision() {

    }

    @Override
    public void ballY(int y) {

    }

    @Override
    public void calibration(int value) {
        calibrateFpga(value);
    }
}

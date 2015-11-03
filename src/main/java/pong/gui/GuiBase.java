package pong.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pong.Fpga;
import pong.control.BaseController;
import pong.gpio.Gpio;

import java.util.Iterator;

public class GuiBase extends Application implements Gpio.Listener {
    /* General */
    private Gpio gpio;
    private static final boolean GPIO = true;
    private Stage stage;
    private Scene menu;
    private Pane pane;
    private ButtonGroup currentMenu;
    private double screenWidth, screenHeight;
    private int currentMode;
    // Field
    private double fieldHeight, fieldWidth;
    private static final double SCREEN_TO_FIELD_WIDTH = 9.0 / 10.0;
    private double fieldX, fieldY;
    private Rectangle field;
    // Paddle
    private double paddleLength, paddleWidth;
    private static final double PADDLE_LENGTH_TO_WIDTH = 0.25;
    private Paddle paddleLeft, paddleRight;
    private double paddleRightY;
    // Calibration
    private int[] cal = new int[3];
    private int calCnt = 0;
    private int prevCal = 0;
    // Selected button
    private MenuButton selected;
    private int selCnt = 0;
    private static final int SEL_THR = GPIO ? 320 : 10;
    // Colors
    private static final Color PRESSED = Color.MEDIUMSEAGREEN, UNPRESSED = Color.SEAGREEN;
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
    private Text textSp,textMp, text2;
    private ButtonGroup group2;

    /* MENU3A: 1P
    Btn: Easy
    Btn: Medium
    Btn: Hard */
    private MenuButton buttonEz, buttonMd, buttonHd;
    private Text text3a;
    private ButtonGroup group3a;

    /* MENU3B
    "Calibration: hold the object in front of the sensor and click on button 1 on FPGA to confirm"
    Arrows (rightupper, middle, lower): images */
    private Text text3b;
    private Group group3b;

    /* MENU4
    Scores: player1 - player2
    Ball
    Debug values in bottom */
    private Text text4;
    private Circle ball;
    private double ballX;
    private double ballY;
    private static final double PADDLE_LENGTH_TO_BALL_RADIUS = 0.125; // which is 1/2 of to ball diameter
    private double ballWidth;
    private Group group4;
    private int goalLeft = 0, goalRight = 0;

    /* MENU5:
    Btn: Resume */
    private MenuButton buttonRs;
    private Text text5;
    private ButtonGroup group5;

    @Override
    public void start(Stage primaryStage) {
        if (GPIO) {
            gpio = new Gpio(this);
            gpio.start();
        }
//        playSound("Level_Up.wav");
        calibrateGui();
        setUpPane();
        setUpMenu1();
        setUpMenu2();
        setUpMenu3a();
        setUpMenu3b();
        setUpMenu4();
        setUpMenu5();
        setUpStage(primaryStage);
        send(Gpio.CALIBRATION);
        if (!GPIO){
            (new BaseController(this)).start();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void calibrateGui() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        screenWidth = bounds.getWidth();
        screenHeight = bounds.getHeight();
        setUpField();
//        System.out.println("SCR: " + screenHeight + ", FIE: " + fieldHeight);
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
        // paddleRight and the ball are buffered and updated on paddleLeft
        paddleRightY = -1;
        ball = new Circle();
        ballX = -1;
        ballY = -1;
    }

    // Updates calibration values and dependancies
    public void calibrateFpga(int coor) {
        if (coor != prevCal) {
//            System.out.println("HURRAY!");
            cal[calCnt] = coor;
            text1.setText(text1.getText() + "\nValue[" + calCnt + "] = " + coor);
            calCnt++;
            if (calCnt == 3) {
                Fpga.calibrate(cal[0], cal[1], cal[2]);
                paddleLength = fieldHeight * Fpga.PADDLE_LENGTH / Fpga.HEIGHT;
                paddleWidth = paddleLength * PADDLE_LENGTH_TO_WIDTH;
//                System.out.println("GUI: " + paddleLength + ", " + paddleWidth);
                setUpPaddle(paddleLeft);
                setUpPaddle(paddleRight);
                setUpBall();
                switchGroup(group2);
                send(Gpio.MENU);
            }
        }
        prevCal = coor;
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

    public void setUpBall() {
        ballWidth = paddleLength * PADDLE_LENGTH_TO_BALL_RADIUS;
        ball.setRadius(ballWidth);
        ball.setFill(PADDLE_COLOR);
        ball.setCenterX(fieldX + fieldWidth / 2);
        ball.setCenterY(fieldY + fieldHeight / 2);
    }

    public void setUpStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Swipe ball");
        stage.setScene(menu);
//        stage.setFullScreen(true); // Resets when switching scene!
        stage.show();
    }

    public void setUpMenu1() {
        text1 = new Text("Calibration time! Waiting for calibration values of left paddle...");
        text1.setFont(new Font("Verdana", 25));
        text1.setX((screenWidth - text1.getBoundsInParent().getWidth()) / 2);
        text1.setY(text1.getBoundsInParent().getHeight());
        group1 = new Group();
        group1.getChildren().add(text1);
        pane.getChildren().addAll(field, group1);
    }

    public void setUpMenu2() {
        // Buttons
        MenuButton[] mb = createButtons(2,
                () -> switchGroup(group3a),
                () -> {switchGroup(group4); send(Gpio.START_GAME);}
        );
        buttonSp = mb[0];
        textSp = buttonSp.addText("Singleplayer");
        buttonMp = mb[1];
        textMp = buttonMp.addText("Multiplayer");
        // Coordinate text
        String s = "FPGA: min_y=" + Fpga.MIN_Y + ", paddle_y=" + Fpga.PADDLE_Y + ", max_y=" + Fpga.MAX_Y + "\npaddle_length=" + Fpga.PADDLE_LENGTH + ", height=" + Fpga.HEIGHT + ", width=" + Fpga.WIDTH;
        text2 = new Text(s);
        text2.setFont(new Font("Verdana", 25));
        text2.setX((screenWidth - text2.getBoundsInParent().getWidth()) / 2);
        text2.setY(text2.getBoundsInParent().getHeight());
        group2 = new ButtonGroup();
        group2.addButtons(buttonSp, buttonMp);
        group2.getChildren().addAll(textSp, textMp, text2);
        pane.getChildren().addAll(paddleLeft, paddleRight);
    }

    public void setUpMenu3a() {
        // Buttons
        MenuButton[] mb = createButtons(3,
                () -> {switchGroup(group4); send(Gpio.AI_1);},
                () -> {switchGroup(group4); send(Gpio.AI_2);},
                () -> {switchGroup(group4); send(Gpio.AI_3);}
        );
        buttonEz = mb[0];
        buttonMd = mb[1];
        buttonHd = mb[2];
        // Coordinate text
        text3a = new Text("Nothing happened yet.");
        text3a.setFont(new Font("Verdana", 25));
        text3a.setX((screenWidth - text2.getBoundsInParent().getWidth()) / 2);
        text3a.setY(text3a.getBoundsInParent().getHeight());
        // Group
        group3a = new ButtonGroup();
        group3a.addButtons(buttonEz, buttonMd, buttonHd);
        group3a.getChildren().add(text3a);
    }

    public void setUpMenu3b() {
        text3b = new Text("Calibration time! Waiting for calibration values of right paddle...\nOh wait we don't do that anymore ;(");
        text3b.setFont(new Font("Verdana", 25));
        text3b.setX((screenWidth - text3b.getBoundsInParent().getWidth()) / 2);
        text3b.setY(text3b.getBoundsInParent().getHeight());
        group3b = new Group();
        group3b.getChildren().add(text3b);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        switchGroup(group4);
    }

    public void setUpMenu4() {
        text4 = new Text(goalLeft + " - " + goalRight);
        text4.setFont(new Font("Verdana", 25));
        text4.setX((screenWidth - text4.getBoundsInParent().getWidth()) / 2);
        text4.setY(text3b.getBoundsInParent().getHeight());
//        System.out.println(ball);
        group4 = new Group();
        group4.getChildren().addAll(ball, text4);
    }

    public void setUpMenu5() {
        MenuButton[] mb = createButtons(1, () -> {switchGroup(group4); send(currentMode);});
        buttonRs = mb[0];
        buttonRs.addText("Resume");
        text5 = new Text("Pause");
        text5.setFont(new Font("Verdana", 25));
        text5.setX((screenWidth - text4.getBoundsInParent().getWidth()) / 2);
        text5.setY(text3b.getBoundsInParent().getHeight());
        group5 = new ButtonGroup();
        group5.addButtons(buttonRs);
        group5.getChildren().add(text5);
    }

    public void send(int mode) {
        currentMode = mode;
        if (GPIO) {
            gpio.send(mode);
        }
    }

    public void updateGoal(boolean left) {
        if (left) {
            goalLeft++;
        } else {
            goalRight++;
        }
        text4.setText(goalLeft + " - " + goalRight);
        text4.setX((screenWidth - text4.getBoundsInParent().getWidth()) / 2);
        playSound("whoosh.wav");
    }

    public void updatePaddleLeft(int fpgaY) {
        double y = Fpga.convertPaddleY(fpgaY, fieldHeight);
        y += fieldY;
        paddleLeft.setY(y);
//        text2.setText("SEL = " + selected + ", cnt = " + selCnt);
//        text2.setX((screenWidth - text2.getBoundsInParent().getWidth()) / 2);
        if (paddleRightY != -1) {
            paddleRight.setY(paddleRightY);
            paddleRightY = -1;
        }
        if (ballX != -1) {
            ball.setCenterX(ballX);
            ballX = -1;
        }
        if (ballY != -1) {
            ball.setCenterY(ballY);
            ballY = -1;
        }
        // Only the left paddle will control the menu!
        if (currentMenu != null) {
            // Loop through all the buttons of this pane
            for (MenuButton mb : currentMenu.getButtons()) {
                // Test if the middle of the paddle is somewhere between the top and the bottom of the menuButton
                if (mb.containsY(y + paddleLeft.getHeight() / 2)) {
                    if (selected == mb) {
                        if (selCnt < SEL_THR) {
                            // Pressed the button not yet long enough
                            selCnt++;
                        } else {
                            // Pressed the button long enough
                            selected = null;
                            selCnt = 0;
                            mb.setFill(PRESSED);
                            mb.click();
                        }
                    } else {
                        // First time on this button
                        selected = mb;
                        selCnt = 1;
                        // MenuButton selected
                        mb.setStrokeWidth(mb.getHeight() / 20);
                        mb.setStroke(PRESSED);
                        mb.setStrokeType(StrokeType.INSIDE);
                    }
                } else if (selected == mb) {
                    // Does not select this button *anymore*
                    selected = null;
                    selCnt = 0;
                    // Does not select this button
                    mb.setFill(UNPRESSED);
                    mb.setStroke(UNPRESSED);
                    mb.setStrokeWidth(0);
                }
            }
        }
    }

    public void updatePaddleRight(int fpgaY) {
        double y = Fpga.convertPaddleY(fpgaY, fieldHeight);
        y += fieldY;
        paddleRightY = y;
    }

    public void updateBallY(int fpgaY) {
        double y = Fpga.convertBallY(fpgaY, fieldHeight);
        y += fieldY;
        ballY = y;
    }

    public void updateBallX(int fpgaX) {
        double x = Fpga.convertBallX(fpgaX, fieldWidth);
        x += fieldX;
        ballX = x;
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
        for (Iterator<Node> iterator = pane.getChildren().iterator(); iterator.hasNext();) {
            Node child = iterator.next();
            if (child instanceof Group) {
                iterator.remove();
            }
        }
        if (group != null) {
            if (group instanceof ButtonGroup) {
                currentMenu = (ButtonGroup) group;
            } else {
                currentMenu = null;
            }
            pane.getChildren().add(group);
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

    public void playSound(String fileName) {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Clip clip = AudioSystem.getClip();
//                    AudioInputStream ais = AudioSystem.getAudioInputStream(new File(fileName));
//                    clip.open(ais);
//                    clip.start();
//                } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//        (new Thread(new MediaPlayer(fileName))).start();
    }

    public double getFieldHeight() {
        return fieldHeight;
    }

    public static double getPaddleLengthToBallRadius() {
        return PADDLE_LENGTH_TO_BALL_RADIUS;
    }

    public static Color getPRESSED() {
        return PRESSED;
    }

    @Override
    public void paddleLeft(int y) {
        Platform.runLater(() -> updatePaddleLeft(y));
    }

    @Override
    public void goalLeft() {
        Platform.runLater(() -> updateGoal(true));
    }

    @Override
    public void paddleRight(int y) {
        Platform.runLater(() -> updatePaddleRight(y));
    }

    @Override
    public void goalRight() {
        Platform.runLater(() -> updateGoal(false));
    }

    @Override
    public void ballX(int x) {
        Platform.runLater(() -> updateBallX(x));
    }

    @Override
    public void collision() {
        playSound("ping.wav");
    }

    @Override
    public void ballY(int y) {
        Platform.runLater(() -> updateBallY(y));
    }

    @Override
    public void calibration(int value) {
        Platform.runLater(() -> calibrateFpga(value));
    }

    @Override
    public void pause(int y) {
        if (stage.getScene().getRoot() != group5) {
            Platform.runLater(() -> switchGroup(group5));
        }
        Platform.runLater(() -> updatePaddleLeft(y));
    }
}

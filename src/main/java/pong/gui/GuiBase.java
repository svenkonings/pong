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

/**
 * Created by Lindsay on 02-Nov-15.
 */
public class GuiBase extends Application{
    /* General */
    private Stage stage;
    private Scene menu;
    private Pane pane;
    private double screen_width, screen_height;
    // Field
    private double field_height, field_width;
    private static final double SCREEN_TO_FIELD_WIDTH = 9.0 / 10.0;
    private double field_x, field_y;
    private Rectangle field;
    // Paddle
    private double paddle_length, paddle_width;
    private double paddle_length_to_width = 0.25;
    private double paddle_x, paddle_y;
    private Paddle paddle;
    // Calibration
    private double[] cal = new double[3];
    private int cal_cnt = 0;
    // Selected button
    private MenuButton selected;
    private int sel_cnt = 0, sel_thr = 10;
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
    private MenuButton button_sp, button_mp;
    private Text sp_text, text2;
    private Group group2;

    /* MENU3A: 1P
    Btn: Easy
    Btn: Medium
    Btn: Hard
     */
    private MenuButton button_ez, button_md, button_hd;
    private Text text3a;
    private Group group3a;

    /* MENU3B
    "Calibration: hold the object in front of the sensor and click on button 1 on FPGA to confirm"
    Arrows (rightupper, middle, lower): images
     */
    private Text text3b;
    private Group group3b;

    @Override
    public void start(Stage primaryStage) {
        (new BaseController(this)).start();
        calibrateGui();
        setUpPane();
        setUpMenu1();
        setUpMenu2();
        setUpMenu3a();
        setUpStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void calibrateGui() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        screen_width = bounds.getWidth();
        screen_height = bounds.getHeight();
        setUpField();
        System.out.println("SCR: " + screen_height + ", FIE: " + field_height);
    }

    public void setUpField() {
        field_width = screen_width * SCREEN_TO_FIELD_WIDTH;
        field_height = field_width / 2;
        field = new Rectangle(field_width, field_height);
        field_x = (screen_width - field_width) / 2;
        field.setX(field_x);
        field_y = (screen_height - field_height) / 2;
        field.setY(field_y);
        field.setFill(FIELD_COLOR);
    }

    public void setUpPane() {
        pane = new Pane();
        pane.setStyle("-fx-background-color: " + BG + ";-fx-padding: 10px;");
        menu = new Scene(pane, screen_width, screen_height);
        // Laffe init to avoid null pointers
        paddle = new Paddle(0, 0);
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
            setUpPaddle();
            switchGroup(group2);
        }
    }

    public void setUpPaddle() {
        paddle.setWidth(paddle_width);
        paddle.setHeight(paddle_length);
        paddle.setFill(PADDLE_COLOR);
        paddle.setX(field_x - paddle_width);
        paddle.setY(field_y);
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
        text1.setX((screen_width - text1.getBoundsInParent().getWidth()) / 2);
        text1.setY(text1.getBoundsInParent().getHeight());
        group1 = new Group();
        group1.getChildren().add(text1);
        pane.getChildren().addAll(field, group1);
    }

    public void setUpMenu2() {
        // Buttons
        double[][] dimens = createButtons(2);
        button_sp = new MenuButton(dimens[0][0], dimens[0][1], dimens[0][2], dimens[0][3]) {
            @Override
            public void click() {
                System.out.println("SINGLEPLAYER CLICKED");
                switchGroup(group3a);
            }
        };
        button_sp.addText("Singleplayer");
        sp_text = button_sp.getText();
        button_sp.setFill(UNPRESSED);
        button_mp = new MenuButton(dimens[1][0], dimens[1][1], dimens[1][2], dimens[1][3]) {
            @Override
            public void click() {
                System.out.println("MP CLICKED");
                switchGroup(group1);
            }
        };
        button_mp.setFill(UNPRESSED);
        // Coordinate text
        text2 = new Text("Nothing happened yet.");
        text2.setFont(new Font("Verdana", 25));
        text2.setX((screen_width - text2.getBoundsInParent().getWidth()) / 2);
        text2.setY(text2.getBoundsInParent().getHeight());
        group2 = new Group();
        group2.getChildren().addAll(button_sp, button_mp, sp_text, text2);
        pane.getChildren().add(paddle);
    }

    public void setUpMenu3a() {
        // Buttons
        double[][] dimens = createButtons(3);
        button_ez = new MenuButton(dimens[0][0], dimens[0][1], dimens[0][2], dimens[0][3]) {
            @Override
            public void click() {
                System.out.println("EZ CLICKED");
                switchGroup(group2);
            }
        };
        button_ez.setFill(UNPRESSED);
        button_md = new MenuButton(dimens[1][0], dimens[1][1], dimens[1][2], dimens[1][3]) {
            @Override
            public void click() {
                System.out.println("MED CLICKED");
                switchGroup(group2);
            }
        };
        button_md.setFill(UNPRESSED);
        button_hd = new MenuButton(dimens[2][0], dimens[2][1], dimens[2][2], dimens[2][3]) {
            @Override
            public void click() {
                System.out.println("HARD CLICKED");
                switchGroup(group2);
            }
        };
        button_hd.setFill(UNPRESSED);
        // Coordinate text
        text3a = new Text("Nothing happened yet.");
        text3a.setFont(new Font("Verdana", 25));
        text3a.setX((screen_width - text2.getBoundsInParent().getWidth()) / 2);
        text3a.setY(text3a.getBoundsInParent().getHeight());
        // Group
        group3a = new Group();
        group3a.getChildren().addAll(button_ez, button_md, button_hd, text3a);
    }

    public void updatePaddleY(int fpgaY) {
        double y = Fpga.fpga2guiY(fpgaY, field_height, paddle_length);
        y += field_y;
        paddle.setY(y);
        text2.setText("SEL = " + selected + ", cnt = " + sel_cnt);
        text2.setX((screen_width - text2.getBoundsInParent().getWidth()) / 2);
        if (stage.getScene() != null) {
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
        }
    }

    public Rectangle cloneRect(Rectangle rekt) {
        Rectangle res = new Rectangle(rekt.getX(), rekt.getY(), rekt.getWidth(), rekt.getHeight());
        res.setFill(rekt.getFill());
        return res;
    }

    // Free shared nodes to use in other panes
//    public void free(Node... nodes) {
//        System.out.print("NODES: ");
//        for (Node n : nodes) {
//            System.out.print(n + ", ");
//        }
//        System.out.println();
//        Pane[] panes = {pane, pane2, pane3a};
//        for (Pane pane : panes) {
//            if (pane != null) {
//                System.out.println("\tPRE \t" + pane.getChildren());
//                pane.getChildren().removeAll(nodes);
//                System.out.println("\tPOST\t" + pane.getChildren());
//            }
//        }
//    }

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

package pong;

import javafx.scene.layout.Pane;

// Pure dimensions of the game field, without customized scaling for display in a window (Scene).
public class Dimen {
    public static final double ASPECT_RATIO = 1 / 2;   // height / width, TODO can probably be 2/3 = 1/1.5

    // FPGA format: left bottom is (MIN_X, MIN_Y)
    /*y bottom*/ public static double FPGA_MIN_Y;   // Will be calibrated
    /*y top   */ public static double FPGA_MAX_Y; // Will be calibrated

    public static double FPGA_PADDLE_HEIGHT = (FPGA_MAX_Y - FPGA_MIN_Y) / 4; // Will be calibrated. Default is a PADDLE_RATIOth of the field height.

    // JAVA GUI format: left top is (0,0)
    /*x left   = 0 */
    /*x right  = pane.getWidth() */
    /*y top    = 0 */
    /*y bottom = pane.getHeight(); */

    // Converts coordinate of the FPGA format to Java GUI format.
    // Input FPGA y-coordinate is of the bottom of the paddle.
    public static double fpga2guiY(int fpga_y, Pane pane) {
        return fpga2guiY(fpga_y, pane.getHeight());
    }

    public static double fpga2guiY(int fpga_y, double gui_max_height) {
        // GUI y coordinate of the bottom of the paddle
        double y = gui_max_height - ((fpga_y - FPGA_MIN_Y) / (FPGA_MAX_Y - FPGA_MIN_Y)) * gui_max_height;
        System.out.println("gui y = " + y);
        // GUI paddle height
        double paddle_height = gui_max_height * FPGA_PADDLE_HEIGHT / (FPGA_MAX_Y - FPGA_MIN_Y);
        System.out.println("gui paddle height = " + paddle_height);
        return y - paddle_height;
    }

    public static void main(String[] args) {
        System.out.println("FPGA_PADDLE_H = " + FPGA_PADDLE_HEIGHT);
        System.out.println("FPGA2GUI " + fpga2guiY(-150, 1080));
//        System.out.println(fpga2guiY(350, 1080) + 1080 * Dimen.REAL_PADDLE_HEIGHT / Dimen.REAL_MAX_Y);
    }
}

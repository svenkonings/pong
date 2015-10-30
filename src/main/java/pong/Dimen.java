package pong;

import javafx.scene.layout.Pane;

// Pure dimensions of the game field, without customized scaling for display in a window (Scene).
public class Dimen {
    // REAL format: left bottom is (0,0)
    /*x left  */ public static final double REAL_MIN_X = 0;
    /*x right */ public static final double REAL_MAX_X = 4096; // 2^12
    /*y bottom*/ public static final double REAL_MIN_Y = 0;
    /*y top   */ public static final double REAL_MAX_Y = 2048; // 2^11
    public static double REAL_PADDLE_HEIGHT = REAL_MAX_Y / 4; // Will be updated to a calibrated value. Default is a tenth of the field height.

    public static final double PADDLE_RATIO = 4;

    // FPGA format: left bottom is (MIN_X, MIN_Y)
//    /*x left  */ public static final double FPGA_MIN_X = 0;
//    /*x right */ public static final double FPGA_MAX_X = 4096; // 2^12
    /*y bottom*/ public static double FPGA_MIN_Y = 100;   // Will be calibrated
    /*y top   */ public static double FPGA_MAX_Y = 1100; // Will be calibrated

    public static final double ASPECT_RATIO = REAL_MAX_X / REAL_MAX_Y;
    public static double FPGA_PADDLE_HEIGHT = (FPGA_MAX_Y - FPGA_MIN_Y) / PADDLE_RATIO; // Will be updated to a calibrated value. Default is a PADDLE_RATIOth of the field height.

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
        double paddle_height = gui_max_height * Dimen.REAL_PADDLE_HEIGHT / Dimen.REAL_MAX_Y;
        System.out.println("gui paddle height = " + paddle_height);
        return y - paddle_height;
    }

    // Converts coordinate of the REAL format to Java GUI format.
    // Input REAL y-coordinate is of the bottom of the paddle.
    public static double real2guiY(int real_y, Pane pane) {
        return real2guiY(real_y, pane.getHeight());
    }

    public static double real2guiY(int real_y, double gui_max_height) {
        return gui_max_height * (REAL_MAX_Y - (double) real_y - REAL_PADDLE_HEIGHT) / REAL_MAX_Y;
    }

    public static void main(String[] args) {
        System.out.println("REAL_PADDLE_H = " + REAL_PADDLE_HEIGHT + ", FPGA_PADDLE_H = " + FPGA_PADDLE_HEIGHT);
        System.out.println("Real2GUI " + real2guiY(160, 1080));
        System.out.println("FPGA2GUI " + fpga2guiY(-150, 1080));
//        System.out.println(fpga2guiY(350, 1080) + 1080 * Dimen.REAL_PADDLE_HEIGHT / Dimen.REAL_MAX_Y);
    }
}

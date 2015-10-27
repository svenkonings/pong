package pong;

import javafx.scene.layout.Pane;

// Pure dimensions of the game field, without customized scaling for display in a window (Scene).
public class Dimen {
    // FPGA format: left bottom is (0,0)
    /*x left  */ public static final double FPGA_MIN_X = 0;
    /*x right */ public static final double FPGA_MAX_X = 4096; // 2^12
    /*y bottom*/ public static final double FPGA_MIN_Y = 0;
    /*y top   */ public static final double FPGA_MAX_Y = 2048; // 2^11
    public static final double ASPECT_RATIO = FPGA_MAX_X / FPGA_MAX_Y;
    public static double FPGA_PADDLE_HEIGHT = 40; // Will be updated to a calibrated value. Default is 40.

    // JAVA GUI format: left top is (0,0)
    /*x left   = 0 */
    /*x right  = pane.getWidth() */
    /*y top    = 0 */
    /*y bottom = pane.getHeight(); */

    // Converts coordinate of the FPGA format to Java GUI format.
    // FPGA y-coordinate is of the bottom of the paddle
    public static double convertY(int fpga_y, Pane pane) {
        return convertY(fpga_y, pane.getHeight());
    }

    public static double convertY(int fpga_y, double gui_max_height) {
        return gui_max_height * (FPGA_MAX_Y - (double) fpga_y - FPGA_PADDLE_HEIGHT) / FPGA_MAX_Y;
    }

    public static void main(String[] args) {
        System.out.println(convertY(28, 720));
    }
}

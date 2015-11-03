package pong;

import pong.gui.GuiBase;

public class Fpga {
    public static final double ASPECT_RATIO = 1 / 2;   // height / width, TODO can probably be 2/3 = 1/1.5

    // FPGA format: left bottom is (MIN_X, MIN_Y)
    /*y bottom*/ public static double MIN_Y;   // Will be calibrated
    /*y top   */ public static double MAX_Y; // Will be calibrated
    public static double PADDLE_Y, PADDLE_LENGTH; // Will be calibrated
    public static double HEIGHT, WIDTH;

    // JAVA GUI format: left top is (0,0)
    /*x left   = 0 */
    /*x right  = field width */
    /*y top    = 0 */
    /*y bottom = field height */

    public static void calibrate(double val1, double val2, double val3) {
        if (val1 < val2) {
            if (val1 < val3) {
                // val1 is smallest
                MIN_Y = val1;
                if (val2 < val3) {
                    // val 2 is middle
                    PADDLE_Y = val2;
                    MAX_Y = val3;
                } else {
                    // val2 is largest
                    MAX_Y = val2;
                    PADDLE_Y = val3;
                }
            } else {
                // val1 is middle, val2 is largest
                PADDLE_Y = val1;
                MAX_Y = val2;
                MIN_Y = val3;
            }
        } else {
            if (val2 < val3) {
                // val2 is smallest
                MIN_Y = val2;
                if (val1 < val3) {
                    // val 1 is middle
                    PADDLE_Y = val1;
                    MAX_Y = val3;
                } else {
                    // val1 is largest
                    MAX_Y = val1;
                    PADDLE_Y = val3;
                }
            } else {
                // val2 is middle, val1 is largest
                MAX_Y = val1;
                PADDLE_Y = val2;
                MIN_Y = val3;
            }
        }
        PADDLE_LENGTH = PADDLE_Y - MIN_Y;
        HEIGHT = MAX_Y - MIN_Y;
        WIDTH = 2 * HEIGHT;
        System.out.println(MIN_Y + ", " + PADDLE_Y + ", " + MAX_Y);
        // Update dependencies

    }

    /**
     * Converts coordinate of the FPGA format to Java GUI format.
     * @param fpgaY FPGA y-coordinate of the bottom of the paddle
     * @param field_height height of the field in GUI
     * @return y-coordinate of the top of the paddle in GUI format
     */
    public static double convertPaddleY(int fpgaY, double field_height) {
        // GUI y coordinate of the bottom of the paddle
        double y = field_height - ((fpgaY - MIN_Y) / HEIGHT) * field_height;
//        System.out.println("gui y = " + y);
        // GUI paddle height
        double paddle_height = field_height * PADDLE_LENGTH / HEIGHT;
//        System.out.println("gui paddle height = " + paddle_height);
        return y - paddle_height;
    }

    /**
     * Converts coordinate of the FPGA format to Java GUI format.
     * @param fpgaY FPGA y-coordinate of the bottom of the ball
     * @param field_height height of the field in GUI
     * @return y-coordinate of the center of the ball in GUI format
     */
    public static double convertBallY(int fpgaY, double field_height) {
        // GUI y coordinate of the bottom of the paddle
        double y = field_height - ((fpgaY - MIN_Y) / HEIGHT) * field_height;
//        System.out.println("gui y = " + y);
        // GUI paddle height
        double paddle_height = field_height * PADDLE_LENGTH * GuiBase.getPaddleLengthToBallRadius() / HEIGHT;
//        System.out.println("gui paddle height = " + paddle_height);
        return y - paddle_height;
    }

    public static double convertBallX(int fpgaX, double field_width) {
        return field_width * fpgaX / WIDTH + PADDLE_LENGTH * GuiBase.getPaddleLengthToBallRadius();
    }

    public static void main(String[] args) {
//        System.out.println("FPGA_PADDLE_H = " + FPGA_PADDLE_LENGTH);
//        System.out.println("FPGA2GUI " + convertPaddleY(-150, 1080));
//        System.out.println(convertPaddleY(350, 1080) + 1080 * Fpga.REAL_PADDLE_HEIGHT / Fpga.REAL_MAX_Y);
        calibrate(1, 2, 3);
        calibrate(1, 3, 2);
        calibrate(2, 1, 3);
        calibrate(2, 3, 1);
        calibrate(3, 1, 2);
        calibrate(3, 2, 1);
    }
}

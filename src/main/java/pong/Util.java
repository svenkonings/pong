package pong;

public class Util {
    public static int pow(int base, int exponent) {
        int result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        return result;
    }

    public static void inverse(Object[] data) {
        for (int left = 0, right = data.length - 1; left < right; left++, right--) {
            // swap the values at the left and right indices
            Object temp = data[left];
            data[left] = data[right];
            data[right] = temp;
        }
    }
}

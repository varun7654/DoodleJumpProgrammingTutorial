package com.dacubeking.doodlejump.util;

public final class MathUtil {
    private MathUtil() {

    }

    /**
     * Mod method that always returns a positive number.
     *
     * @param a The number to be modded.
     * @param b The modulus.
     * @return The modulus of x. (positive number)
     */
    public static float mod(float a, float b) {
        float result = a % b;
        if (a < 0) {
            result += b;
        }
        return result;
    }
}

package com.dacubeking.doodlejump.util;

public final class MathUtil {
    private MathUtil() {

    }

    /**
     * Mod method that always returns a positive number.
     *
     * @param x The number to be modded.
     * @param y The modulus.
     * @return The modulus of x. (positive number)
     */
    public static float mod(float x, float y) {
        float result = x % y;
        if (x < 0) {
            result += y;
        }
        return result;
    }
}

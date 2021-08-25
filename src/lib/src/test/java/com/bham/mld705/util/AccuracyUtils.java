package com.bham.mld705.util;

import java.lang.Math;

/**
 * @author Martin de Spirlet
 */
public final class AccuracyUtils {

    private AccuracyUtils() {
        throw new AssertionError();
    }

    public static long getAbsoluteError(int expected, int actual) {
        return Math.abs((long) actual - (long) expected);
    }

    public static boolean isCorrect(long absoluteError, double errorBound) {
        return absoluteError <= errorBound;
    }

}

package com.bham.mld705.util;

import java.util.Arrays;

/**
 * A class that provides static utility methods for various mathematical
 * operations.
 *
 * @author Martin de Spirlet
 */
public final class Math {

    private static final long LSB_MASK = 0x0000_0000_0000_0001;

    /**
     * Provides a private no-argument constructor that prevents this class from
     * being instantiated.
     *
     * @throws AssertionError if called from within this class
     */
    private Math() {
        throw new AssertionError();
    }

    /**
     * Checks whether the bit in the given position of the binary representation of
     * the given number is set. The position is counted from the right, where the
     * least-significant bit is in position zero.
     *
     * @param number the number whose bit to check
     * @param bit    the position of the bit to check
     * @return {@code true} if the bit is set, {@code false} otherwise
     */
    public static boolean isBitSet(long number, int bit) {
        return ((number >> bit) & LSB_MASK) == LSB_MASK;
    }

    /**
     * Returns the square of the given base, modulo the given modulus. Note that
     * unchecked integer overflow will occur if the absolute value of the base is
     * greater than the positive square root of {@code Long.MAX_VALUE}.
     *
     * @param base    the base
     * @param modulus the modulo
     * @return the square of the given base, modulo the given modulus
     * @see Long#MAX_VALUE
     */
    public static long squareModulo(long base, long modulus) {
        return (base * base) % modulus;
    }

    /**
     * Returns the given base raised to the given non-negative exponent, modulo the
     * given modulus. Note that unchecked integer overflow will occur if the
     * absolute value of the base is greater than the positive square root of
     * {@code Long.MAX_VALUE}. This may also occur if the modulus exceeds the same
     * threshold.
     *
     * @param base     the base
     * @param exponent the non-negative exponent
     * @param modulus  the modulus
     * @return the given base raised to the given non-negative exponent, modulo the
     *         given modulus
     * @throws IllegalArgumentException if the exponent is negative
     * @see Long#MAX_VALUE
     */
    public static long raiseNonNegativeModulo(long base, long exponent, long modulus) {
        if (exponent < 0L) {
            throw new IllegalArgumentException("Cannot raise base to negative exponent: " + exponent);
        }

        long result = isBitSet(exponent, 0) ? base : 1L;

        for (int i = 1, bits = Long.SIZE - Long.numberOfLeadingZeros(exponent); i < bits; i++) {
            base = squareModulo(base, modulus);

            if (isBitSet(exponent, i)) {
                result = (result * base) % modulus;
            }
        }

        return result;
    }

    /**
     * Sorts the given array of integers and returns the median. If the number of
     * elements is odd, the median is the middle element after sorting. If the
     * number of elements is even, the median is the integral part of the arithmetic
     * mean of the two middle elements after sorting.
     *
     * @param array the array of integers whose median to return
     * @return the median of the given array of integers
     */
    public static int sortAndGetMedian(int[] array) {
        Arrays.sort(array);

        return ((array.length % 2) == 0) ? (array[array.length / 2 - 1] + array[array.length / 2]) / 2
                : array[(array.length - 1) / 2];
    }

}

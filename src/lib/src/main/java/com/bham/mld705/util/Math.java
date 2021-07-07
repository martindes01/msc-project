package com.bham.mld705.util;

public final class Math {

    private static final byte LSB_MASK = 0b00000001;

    private Math() {
        throw new AssertionError();
    }

    public static long squareModulo(long base, long modulus) {
        return (base * base) % modulus;
    }

    public static long raiseNonNegativeModulo(long base, long exponent, long modulus) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Cannot raise base to negative exponent: " + exponent);
        }

        long result = ((exponent & LSB_MASK) == LSB_MASK) ? base : 1L;

        for (int i = 1, bits = Long.SIZE - Long.numberOfLeadingZeros(exponent); i < bits; i++) {
            base = squareModulo(base, modulus);

            if (((exponent >> i) & LSB_MASK) == LSB_MASK) {
                result = (result * base) % modulus;
            }
        }

        return result;
    }

}

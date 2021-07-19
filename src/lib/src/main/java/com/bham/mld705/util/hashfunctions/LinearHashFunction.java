package com.bham.mld705.util.hashfunctions;

import java.util.concurrent.ThreadLocalRandom;

public final class LinearHashFunction implements HashFunction {

    private static final int PARAMETER_LOWER_BOUND = 1;

    private final int MODULUS;
    private final int PARAMETER_A;
    private final int PARAMETER_B;
    private final int PRIME;

    public LinearHashFunction(int prime, int modulus) {
        PRIME = prime;
        MODULUS = modulus;

        PARAMETER_A = ThreadLocalRandom.current().nextInt(PARAMETER_LOWER_BOUND, PRIME);
        PARAMETER_B = ThreadLocalRandom.current().nextInt(PARAMETER_LOWER_BOUND, PRIME);
    }

    @Override
    public int apply(int key) {
        return ((PARAMETER_A * key + PARAMETER_B) % PRIME) % MODULUS;
    }

    @Override
    public String toString() {
        return "LinearHashFunction [PRIME=" + PRIME + ", MODULUS=" + MODULUS + ", PARAMETER_A=" + PARAMETER_A
                + ", PARAMETER_B=" + PARAMETER_B + "]";
    }

}

package com.bham.mld705.util.hashfunctions;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A pairwise independent hash function that maps an integer key to an integer
 * hash value. This hash function is drawn from the family of polynomials. The
 * probability of observing any pair of hash values is uniform.
 *
 * @author Martin de Spirlet
 * @see HashFunction
 */
public final class LinearHashFunction implements HashFunction {

    private static final int PARAMETER_LOWER_BOUND = 1;

    private final int MODULUS;
    private final int PARAMETER_A;
    private final int PARAMETER_B;
    private final int PRIME;

    /**
     * Constructs a new {@code LinearHashFunction} with the given prime and modulus.
     * Note that the given prime must be prime in order for the new
     * {@code LinearHashFunction} to be pairwise independent.
     *
     * @param prime   the prime of the new {@code LinearHashFunction}
     * @param modulus the modulus of the new {@code LinearHashFunction}
     */
    public LinearHashFunction(int prime, int modulus) {
        PRIME = prime;
        MODULUS = modulus;

        PARAMETER_A = ThreadLocalRandom.current().nextInt(PARAMETER_LOWER_BOUND, PRIME);
        PARAMETER_B = ThreadLocalRandom.current().nextInt(PARAMETER_LOWER_BOUND, PRIME);
    }

    /**
     * Maps the given key to a pairwise independent hash value.
     *
     * @param key the key to map to a hash value
     * @return the hash value of the given key
     */
    @Override
    public int apply(int key) {
        return Math.floorMod(PARAMETER_A * key + PARAMETER_B, PRIME) % MODULUS;
    }

    /**
     * Returns a {@code String} that represents this {@code LinearHashFunction}.
     *
     * @return a {@code String} that represents this {@code LinearHashFunction}
     */
    @Override
    public String toString() {
        return "LinearHashFunction [PRIME=" + PRIME + ", MODULUS=" + MODULUS + ", PARAMETER_A=" + PARAMETER_A
                + ", PARAMETER_B=" + PARAMETER_B + "]";
    }

}

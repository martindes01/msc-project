package com.bham.mld705.util.hashfunctions;

/**
 * A hash function that maps an integer key to an integer hash value.
 *
 * @author Martin de Spirlet
 */
@FunctionalInterface
public interface HashFunction {

    /**
     * Maps the given key to a hash value.
     *
     * @param key the key to map to a hash value
     * @return the hash value of the given key
     */
    public abstract int apply(int key);

}

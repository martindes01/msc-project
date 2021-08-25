package com.bham.mld705.util;

import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin de Spirlet
 */
public final class Multiset {

    private static final int ZERO_MULTIPLICITY = 0;

    private long absoluteNorm;

    private Map<Integer, Integer> multiplicities;

    public Multiset() {
        multiplicities = new HashMap<>();
    }

    public long getAbsoluteNorm() {
        return absoluteNorm;
    }

    public synchronized int getMultiplicity(int item) {
        return multiplicities.getOrDefault(item, ZERO_MULTIPLICITY);
    }

    public synchronized void update(int item, int weight) {
        int oldValue = getMultiplicity(item);
        int newValue = oldValue + weight;

        absoluteNorm -= Math.abs(oldValue);
        absoluteNorm += Math.abs(newValue);

        multiplicities.put(item, newValue);
    }

}

package com.bham.mld705.util;

import java.util.TreeMap;

/**
 * @author Martin de Spirlet
 */
public final class RankTable {

    private static final int ZERO_RANK = 0;

    private long cardinality;

    private TreeMap<Integer, Integer> multiplicities;

    public RankTable() {
        multiplicities = new TreeMap<>();
    }

    public long getCardinality() {
        return cardinality;
    }

    public int getRank(int item) {
        return multiplicities.headMap(item).values().stream().reduce(Integer::sum).orElseGet(() -> ZERO_RANK);
    }

    public void update(int item, int weight) {
        multiplicities.merge(item, weight, Integer::sum);

        cardinality += weight;
    }

}

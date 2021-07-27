package com.bham.mld705.summaries;

import java.util.Arrays;

public final class DyadicCountSketch {

    private static class FrequencyTable implements FrequencySummary<FrequencyTable> {

        private final int OFFSET;
        private final int SIZE;

        private int[] counters;

        private FrequencyTable(FrequencyTable frequencyTable) {
            OFFSET = frequencyTable.OFFSET;
            SIZE = frequencyTable.SIZE;

            counters = Arrays.copyOf(frequencyTable.counters, SIZE);
        }

        public FrequencyTable(int size) {
            SIZE = size;

            OFFSET = SIZE >> 1;

            reset();
        }

        @Override
        public int getFrequency(int item) {
            return counters[item + OFFSET];
        }

        @Override
        public void update(int item, int weight) {
            counters[item + OFFSET] += weight;
        }

        @Override
        public FrequencyTable copy() {
            return new FrequencyTable(this);
        }

        @Override
        public void merge(FrequencyTable other) {
            for (int i = 0; i < SIZE; i++) {
                counters[i] += other.counters[i];
            }
        }

        @Override
        public void reset() {
            counters = new int[SIZE];
        }

        @Override
        public String toString() {
            return "FrequencyTable [SIZE=" + SIZE + ", OFFSET=" + OFFSET + ", counters=" + Arrays.toString(counters)
                    + "]";
        }

    }

}

package com.bham.mld705.tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.bham.mld705.summaries.Fingerprint;
import com.bham.mld705.wrappers.spark.FingerprintAccumulator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * @author Martin de Spirlet
 */
public final class FingerprintTest extends Test {

    private static final String[] ACCURACY_RESULTS_HEADER = new String[] { "Multiset A", "Hash value A", "Multiset B",
            "Hash value B", "Expected equal", "Actually equal", "Correct" };
    private static final int MULTISETS = 2;

    private static final int ITEM_INDEX = 0;
    private static final int WEIGHT_INDEX = 1;

    private static final int MULTISET_1_INDEX_OFFSET = 0;
    private static final int MULTISET_2_INDEX_OFFSET = 2;

    private static final String MULTISET_1_STRING = "Multiset 1";
    private static final String MULTISET_1_REVERSED_STRING = "Multiset 1 reversed";
    private static final String MULTISET_2_STRING = "Multiset 2";

    private static final int FINGERPRINTS = 3;

    private static final int FINGERPRINT_1_INDEX = 0;
    private static final int FINGERPRINT_1_REVERSED_INDEX = 1;
    private static final int FINGERPRINT_2_INDEX = 2;

    private static final int DEFAULT_ITEM_LOWER_BOUND = 0;
    private static final int DEFAULT_ITEM_UPPER_BOUND = Integer.MAX_VALUE;
    private static final String DEFAULT_NAME = "fingerprint";
    private static final int DEFAULT_WEIGHT_LOWER_BOUND = Byte.MIN_VALUE;
    private static final int DEFAULT_WEIGHT_UPPER_BOUND = Byte.MAX_VALUE;

    private static FingerprintAccumulator fingerprintAccumulator;
    private static String staticDelimiter;

    private Fingerprint[] fingerprints;

    public FingerprintTest(String[] args) {
        super(args, DEFAULT_NAME, DEFAULT_ITEM_LOWER_BOUND, DEFAULT_ITEM_UPPER_BOUND, DEFAULT_WEIGHT_LOWER_BOUND,
                DEFAULT_WEIGHT_UPPER_BOUND);

        fingerprints = new Fingerprint[FINGERPRINTS];
    }

    @Override
    protected void buildSummary() {
        fingerprintAccumulator = new FingerprintAccumulator();
        staticDelimiter = getDelimiter();

        SparkConf configuration = new SparkConf().setMaster(ACCURACY_MASTER).setAppName(getName());
        JavaSparkContext context = new JavaSparkContext(configuration);

        // Parse each line as a list of integers and apply an index
        JavaPairRDD<Long, List<Integer>> indexedDataset = context.textFile(getDatasetPath().toString())
                .map(line -> Arrays.asList(line.split(staticDelimiter)).stream().map(token -> Integer.parseInt(token))
                        .collect(Collectors.toList()))
                .zipWithUniqueId().mapToPair(vkPair -> new Tuple2<>(vkPair._2, vkPair._1));

        // Build the fingerprint of the first multiset
        indexedDataset.foreach(
                kvPair -> fingerprintAccumulator.add(new Tuple2<>(kvPair._2.get(ITEM_INDEX + MULTISET_1_INDEX_OFFSET),
                        kvPair._2.get(WEIGHT_INDEX + MULTISET_1_INDEX_OFFSET))));

        fingerprints[FINGERPRINT_1_INDEX] = fingerprintAccumulator.value().copy();

        fingerprintAccumulator.reset();

        // Build the fingerprint of the first multiset reversed
        indexedDataset.sortByKey(false).foreach(
                kvPair -> fingerprintAccumulator.add(new Tuple2<>(kvPair._2.get(ITEM_INDEX + MULTISET_1_INDEX_OFFSET),
                        kvPair._2.get(WEIGHT_INDEX + MULTISET_1_INDEX_OFFSET))));

        fingerprints[FINGERPRINT_1_REVERSED_INDEX] = fingerprintAccumulator.value().copy();

        fingerprintAccumulator.reset();

        // Build the fingerprint of the second multiset
        indexedDataset.foreach(
                kvPair -> fingerprintAccumulator.add(new Tuple2<>(kvPair._2.get(ITEM_INDEX + MULTISET_2_INDEX_OFFSET),
                        kvPair._2.get(WEIGHT_INDEX + MULTISET_1_INDEX_OFFSET))));

        fingerprints[FINGERPRINT_2_INDEX] = fingerprintAccumulator.value().copy();

        context.stop();
        context.close();
    }

    @Override
    protected void createData() {
        try (PrintWriter writer = new PrintWriter(getDatasetPath().toFile())) {
            for (int i = 0; i < getSize(); i++) {
                List<Object> values = new ArrayList<>();

                for (int j = 0; j < MULTISETS; j++) {
                    values.add(ThreadLocalRandom.current().nextLong((long) getItemLowerBound(),
                            (long) getItemUpperBound() + 1L));
                    values.add(ThreadLocalRandom.current().nextLong((long) getWeightLowerBound(),
                            (long) getWeightUpperBound() + 1L));
                }

                writer.println(values.stream().map(String::valueOf).collect(Collectors.joining(getDelimiter())));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void printAccuracyResults() {
        try (PrintWriter writer = new PrintWriter(getAccuracyResultsPath().toFile())) {
            writer.println(Arrays.stream(ACCURACY_RESULTS_HEADER).collect(Collectors.joining(RESULTS_DELIMITER)));

            writer.println(getResultString(MULTISET_1_STRING, fingerprints[FINGERPRINT_1_INDEX],
                    MULTISET_1_REVERSED_STRING, fingerprints[FINGERPRINT_1_REVERSED_INDEX], true));

            writer.println(getResultString(MULTISET_1_STRING, fingerprints[FINGERPRINT_1_INDEX], MULTISET_2_STRING,
                    fingerprints[FINGERPRINT_2_INDEX], false));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected long timeSummary() {
        fingerprintAccumulator = new FingerprintAccumulator();
        staticDelimiter = getDelimiter();

        SparkConf configuration = new SparkConf().setMaster(PERFORMANCE_MASTER).setAppName(getName());
        JavaSparkContext context = new JavaSparkContext(configuration);

        // Parse each line as a pair of integers
        JavaPairRDD<Integer, Integer> dataset = context.textFile(getDatasetPath().toString())
                .map(line -> Arrays.asList(line.split(staticDelimiter)).stream().map(token -> Integer.parseInt(token))
                        .collect(Collectors.toList()))
                .mapToPair(list -> new Tuple2<>(list.get(ITEM_INDEX), list.get(WEIGHT_INDEX)));

        long start = System.nanoTime();

        // Build the fingerprint of the multiset
        dataset.foreach(pair -> fingerprintAccumulator.add(pair));

        long end = System.nanoTime();

        context.stop();
        context.close();

        return end - start;
    }

    private static String getResultString(String multisetA, Fingerprint fingerprintA, String multisetB,
            Fingerprint fingerprintB, boolean expected) {
        boolean actual = fingerprintA.equals(fingerprintB);
        boolean correct = actual == expected;

        return Arrays
                .stream(new Object[] { multisetA, fingerprintA.getHashValue(), multisetB, fingerprintB.getHashValue(),
                        expected, actual, correct })
                .map(String::valueOf).collect(Collectors.joining(RESULTS_DELIMITER));
    }

}

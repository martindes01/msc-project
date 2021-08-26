package com.bham.mld705.tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.bham.mld705.summaries.CountSketch;
import com.bham.mld705.util.AccuracyUtils;
import com.bham.mld705.util.Multiset;
import com.bham.mld705.wrappers.spark.CountSketchAccumulator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * @author Martin de Spirlet
 */
public final class CountSketchTest extends Test {

    private static final String[] ACCURACY_RESULTS_HEADER = new String[] { "Expected", "Actual", "Absolute error",
            "Correct" };
    private static final String[] ACCURACY_RESULTS_INFO_HEADER = new String[] { "Queries", "Error factor",
            "Error bound", "Failure count bound", "Failure proportion bound", "Failure count", "Failure proportion" };

    private static final String DATASET_INFO_HEADER = "Absolute norm";

    private static final int ITEM_INDEX = 0;
    private static final int WEIGHT_INDEX = 1;

    private static final String COLUMNS_OPTION = "--columns";
    private static final String ROWS_OPTION = "--rows";

    private static final int DEFAULT_COLUMNS = 10;
    private static final int DEFAULT_ITEM_LOWER_BOUND = Integer.MIN_VALUE;
    private static final int DEFAULT_ITEM_UPPER_BOUND = Integer.MAX_VALUE;
    private static final String DEFAULT_NAME = "count-sketch";
    private static final int DEFAULT_ROWS = 7;
    private static final int DEFAULT_WEIGHT_LOWER_BOUND = Byte.MIN_VALUE;
    private static final int DEFAULT_WEIGHT_UPPER_BOUND = Byte.MAX_VALUE;

    private static CountSketchAccumulator countSketchAccumulator;
    private static String staticDelimiter;

    private int columns = DEFAULT_COLUMNS;
    private int rows = DEFAULT_ROWS;

    private CountSketch countSketch;
    private Multiset multiset;

    public CountSketchTest(String[] args) {
        super(args, DEFAULT_NAME, DEFAULT_ITEM_LOWER_BOUND, DEFAULT_ITEM_UPPER_BOUND, DEFAULT_WEIGHT_LOWER_BOUND,
                DEFAULT_WEIGHT_UPPER_BOUND);

        String columnsValue = getOption(COLUMNS_OPTION);
        if (columnsValue != null) {
            columns = Integer.parseInt(columnsValue);
        }

        String rowsValue = getOption(ROWS_OPTION);
        if (rowsValue != null) {
            rows = Integer.parseInt(rowsValue);
        }
    }

    @Override
    protected void buildSummary() {
        countSketchAccumulator = new CountSketchAccumulator(rows, columns);
        staticDelimiter = getDelimiter();

        SparkConf configuration = new SparkConf().setMaster(ACCURACY_MASTER).setAppName(getName());
        JavaSparkContext context = new JavaSparkContext(configuration);

        // Parse each line as a pair of integers
        JavaPairRDD<Integer, Integer> dataset = context.textFile(getDatasetPath().toString())
                .map(line -> Arrays.asList(line.split(staticDelimiter)).stream().map(token -> Integer.parseInt(token))
                        .collect(Collectors.toList()))
                .mapToPair(list -> new Tuple2<>(list.get(ITEM_INDEX), list.get(WEIGHT_INDEX)));

        // Build the count sketch of the multiset
        dataset.foreach(pair -> countSketchAccumulator.add(pair));

        countSketch = countSketchAccumulator.value().copy();

        context.stop();
        context.close();
    }

    @Override
    protected void createData() {
        multiset = new Multiset();

        try (PrintWriter writer = new PrintWriter(getDatasetPath().toFile())) {
            for (int i = 0; i < getSize(); i++) {
                int item = (int) ThreadLocalRandom.current().nextLong((long) getItemLowerBound(),
                        (long) getItemUpperBound() + 1L);
                int weight = (int) ThreadLocalRandom.current().nextLong((long) getWeightLowerBound(),
                        (long) getWeightUpperBound() + 1L);

                writer.println(item + getDelimiter() + weight);

                multiset.update(item, weight);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(getDatasetInfoPath().toFile())) {
            writer.println(DATASET_INFO_HEADER);

            writer.println(multiset.getAbsoluteNorm());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void printAccuracyResults() {
        double errorFactor = 1.0 / (double) columns;
        double failureProbability = 1.0 / (double) (1 << rows);

        double errorBound = errorFactor * (double) multiset.getAbsoluteNorm();

        int failures = 0;

        try (PrintWriter writer = new PrintWriter(getAccuracyResultsPath().toFile())) {
            writer.println(Arrays.stream(ACCURACY_RESULTS_HEADER).collect(Collectors.joining(RESULTS_DELIMITER)));

            for (long i = (long) getItemLowerBound(); i <= (long) getItemUpperBound(); i += getQueryStep()) {
                int expected = multiset.getMultiplicity((int) i);
                int actual = countSketch.getFrequency((int) i);

                long absoluteError = AccuracyUtils.getAbsoluteError(expected, actual);
                boolean correct = AccuracyUtils.isCorrect(absoluteError, errorBound);

                if (!correct) {
                    failures++;
                }

                writer.println(getResultString(expected, actual, absoluteError, correct));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(getAccuracyResultsInfoPath().toFile())) {
            writer.println(Arrays.stream(ACCURACY_RESULTS_INFO_HEADER).collect(Collectors.joining(RESULTS_DELIMITER)));

            writer.println(Arrays
                    .stream(new Object[] { getQueries(), errorFactor, errorBound,
                            failureProbability * (double) getQueries(), failureProbability, failures,
                            (double) failures / (double) getQueries() })
                    .map(String::valueOf).collect(Collectors.joining(RESULTS_DELIMITER)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected long timeSummary() {
        countSketchAccumulator = new CountSketchAccumulator(rows, columns);
        staticDelimiter = getDelimiter();

        SparkConf configuration = new SparkConf().setMaster(PERFORMANCE_MASTER).setAppName(getName());
        JavaSparkContext context = new JavaSparkContext(configuration);

        // Parse each line as a pair of integers
        JavaPairRDD<Integer, Integer> dataset = context.textFile(getDatasetPath().toString())
                .map(line -> Arrays.asList(line.split(staticDelimiter)).stream().map(token -> Integer.parseInt(token))
                        .collect(Collectors.toList()))
                .mapToPair(list -> new Tuple2<>(list.get(ITEM_INDEX), list.get(WEIGHT_INDEX)));

        long start = System.nanoTime();

        // Build the count sketch of the multiset
        dataset.foreach(pair -> countSketchAccumulator.add(pair));

        long end = System.nanoTime();

        context.stop();
        context.close();

        return end - start;
    }

    private static String getResultString(int expected, int actual, long absoluteError, boolean correct) {
        return Arrays.stream(new Object[] { expected, actual, absoluteError, correct }).map(String::valueOf)
                .collect(Collectors.joining(RESULTS_DELIMITER));
    }

}

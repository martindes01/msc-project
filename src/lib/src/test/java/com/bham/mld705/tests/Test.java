package com.bham.mld705.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.bham.mld705.util.FileUtils;

/**
 * @author Martin de Spirlet
 */
public abstract class Test {

    protected static final String ACCURACY_MASTER = "local[*]";
    protected static final String PERFORMANCE_MASTER = "local";

    protected static final String RESULTS_DELIMITER = FileUtils.CSV_DELIMITER;
    protected static final String RESULTS_EXTENSION = FileUtils.CSV_EXTENSION;

    private static final String NAME_DELIMITER = "-";

    private static final String DATASET_SUFFIX = "-dataset";
    private static final String DATASET_INFO_SUFFIX = "-dataset-info";

    private static final String ACCURACY_RESULTS_SUFFIX = "-accuracy-results";
    private static final String ACCURACY_RESULTS_INFO_SUFFIX = "-accuracy-results-info";

    private static final String[] PERFORMANCE_RESULTS_HEADER = new String[] { "Run", "Duration / ns" };
    private static final String PERFORMANCE_RESULTS_SUFFIX = "-performance-results";

    private static final String OPTION_ASSIGNMENT_DELIMITER = "=";
    private static final int OPTION_KEY_INDEX = 0;
    private static final int OPTION_VALUE_INDEX = 1;

    private static final String DELIMITER_OPTION = "--delimiter";
    private static final String DIRECTORY_OPTION = "--directory";
    private static final String INPUT_OPTION = "--input";
    private static final String ITEM_LOWER_BOUND_OPTION = "--item-lower-bound";
    private static final String ITEM_UPPER_BOUND_OPTION = "--item-upper-bound";
    private static final String NAME_OPTION = "--name";
    private static final String QUERIES = "--queries";
    private static final String RUNS_OPTION = "--runs";
    private static final String SIZE_OPTION = "--size";
    private static final String WEIGHT_LOWER_BOUND_OPTION = "--weight-lower-bound";
    private static final String WEIGHT_UPPER_BOUND_OPTION = "--weight-upper-bound";

    private static final String DEFAULT_DELIMITER = FileUtils.CSV_DELIMITER;
    private static final Path DEFAULT_DIRECTORY = Path.of(System.getProperty("user.dir"));
    private static final int DEFAULT_INDEX = 0;
    private static final String DEFAULT_INPUT = "";
    private static final long DEFAULT_QUERIES = 65536L;
    private static final int DEFAULT_RUNS = 1;
    private static final int DEFAULT_SIZE = 65536;

    private String delimiter = DEFAULT_DELIMITER;
    private Path directory = DEFAULT_DIRECTORY;
    private String extension;
    private int index = DEFAULT_INDEX;
    private String input = DEFAULT_INPUT;
    private int itemLowerBound;
    private long itemRange;
    private int itemUpperBound;
    private String name;
    private long queries = DEFAULT_QUERIES;
    private long queryStep;
    private int runs = DEFAULT_RUNS;
    private int size = DEFAULT_SIZE;
    private int weightLowerBound;
    private int weightUpperBound;

    private Map<String, String> options;

    public Test(String[] args, String defaultName, int defaultItemLowerBound, int defaultItemUpperBound,
            int defaultWeightLowerBound, int defaultWeightUpperBound) {
        name = defaultName;

        itemLowerBound = defaultItemLowerBound;
        itemUpperBound = defaultItemUpperBound;

        weightLowerBound = defaultWeightLowerBound;
        weightUpperBound = defaultWeightUpperBound;

        options = new HashMap<>();

        parseArgs(args);

        extension = FileUtils.getDelimitedFileExtension(delimiter);

        itemRange = (long) itemUpperBound - (long) itemLowerBound + 1L;
        queryStep = Math.max(itemRange / queries, 1L);
    }

    protected abstract void buildSummary();

    protected abstract void createData();

    protected Path getAccuracyResultsPath() {
        return directory.resolve(getIndexedName() + ACCURACY_RESULTS_SUFFIX + RESULTS_EXTENSION);
    }

    protected Path getAccuracyResultsInfoPath() {
        return directory.resolve(getIndexedName() + ACCURACY_RESULTS_INFO_SUFFIX + RESULTS_EXTENSION);
    }

    protected Path getDatasetPath() {
        if (input.equals(DEFAULT_INPUT)) {
            return directory.resolve(getIndexedName() + DATASET_SUFFIX + extension);
        } else {
            return directory.resolve(input);
        }
    }

    protected Path getDatasetInfoPath() {
        return directory.resolve(getIndexedName() + DATASET_INFO_SUFFIX + extension);
    }

    protected String getDelimiter() {
        return delimiter;
    }

    protected int getItemLowerBound() {
        return itemLowerBound;
    }

    protected long getItemRange() {
        return itemRange;
    }

    protected int getItemUpperBound() {
        return itemUpperBound;
    }

    private String getIndexedName() {
        return name + NAME_DELIMITER + index;
    }

    protected String getName() {
        return name;
    }

    protected String getOption(String key) {
        return options.remove(key);
    }

    private Path getPerformanceResultsPath() {
        return directory.resolve(getName() + PERFORMANCE_RESULTS_SUFFIX + RESULTS_EXTENSION);
    }

    protected long getQueries() {
        return queries;
    }

    protected long getQueryStep() {
        return queryStep;
    }

    protected int getSize() {
        return size;
    }

    protected int getWeightLowerBound() {
        return weightLowerBound;
    }

    protected int getWeightUpperBound() {
        return weightUpperBound;
    }

    private void parseArgs(String[] args) {
        for (String arg : args) {
            String[] argPair = arg.split(OPTION_ASSIGNMENT_DELIMITER);

            String optionKey = argPair[OPTION_KEY_INDEX];
            String optionValue = argPair[OPTION_VALUE_INDEX];

            switch (optionKey) {
                case DELIMITER_OPTION:
                    delimiter = optionValue;
                    break;

                case DIRECTORY_OPTION:
                    directory = directory.resolve(optionValue);
                    new File(directory.toString()).mkdirs();
                    break;

                case INPUT_OPTION:
                    input = optionValue;
                    break;

                case ITEM_LOWER_BOUND_OPTION:
                    itemLowerBound = Integer.parseInt(optionValue);
                    break;

                case ITEM_UPPER_BOUND_OPTION:
                    itemUpperBound = Integer.parseInt(optionValue);
                    break;

                case NAME_OPTION:
                    name = optionValue;
                    break;

                case QUERIES:
                    queries = Long.parseLong(optionValue);
                    break;

                case RUNS_OPTION:
                    runs = Integer.parseInt(optionValue);
                    break;

                case SIZE_OPTION:
                    size = Integer.parseInt(optionValue);
                    break;

                case WEIGHT_LOWER_BOUND_OPTION:
                    weightLowerBound = Integer.parseInt(optionValue);
                    break;

                case WEIGHT_UPPER_BOUND_OPTION:
                    weightUpperBound = Integer.parseInt(optionValue);
                    break;

                default:
                    options.put(optionKey, optionValue);
                    break;
            }
        }
    }

    private void setIndex(int index) {
        this.index = index;
    }

    protected abstract void printAccuracyResults();

    private void printPerformanceResults(long[] durations) {
        try (PrintWriter writer = new PrintWriter(getPerformanceResultsPath().toFile())) {
            writer.println(Arrays.stream(PERFORMANCE_RESULTS_HEADER).collect(Collectors.joining(RESULTS_DELIMITER)));

            for (int i = 0; i < durations.length; i++) {
                writer.println(i + RESULTS_DELIMITER + durations[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void runAccuracyTest() {
        for (int i = 0; i < runs; i++) {
            setIndex(i);
            createData();
            buildSummary();
            printAccuracyResults();
        }
    }

    public void runPerformanceTest() {
        long[] durations = new long[runs];

        for (int i = 0; i < runs; i++) {
            setIndex(i);
            createData();
        }

        for (int i = 0; i < runs; i++) {
            setIndex(i);
            durations[i] = timeSummary();
        }

        printPerformanceResults(durations);
    }

    protected abstract long timeSummary();

}
